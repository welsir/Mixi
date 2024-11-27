package com.mixi.webroom.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.mixi.common.exception.ServeException;
import com.mixi.common.utils.RCode;
import com.mixi.common.utils.UserThread;
import com.mixi.webroom.core.listener.TicketExpireListener;
import com.mixi.webroom.core.worker.SnowFlakeIdWorker;
import com.mixi.webroom.domain.RedisOption;
import com.mixi.common.pojo.Ticket;
import com.mixi.webroom.core.rpc.VideoService;
import com.mixi.webroom.pojo.entity.WebRoom;
import com.mixi.webroom.pojo.dto.CreateRoomDTO;
import com.mixi.webroom.pojo.vo.JoinVO;
import com.mixi.webroom.pojo.vo.RoomInfoVO;
import com.mixi.webroom.service.EmailService;
import com.mixi.webroom.service.WebRoomService;
import com.mixi.webroom.utils.*;
import io.github.common.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.mixi.webroom.constants.RedisKeyConstants.*;


/**
 * @Author：XiaoChun
 * @Date：2024/6/25 17:20
 */
@Service
@Slf4j
public class WebRoomServiceImpl implements WebRoomService {
    @Resource
    private RedisOption redisOption;

    @Resource
    private VideoService videoService;

    @Resource
    private SnowFlakeIdWorker snowFlakeIdWorker;

    @Resource
    private WebRoomUtil webRoomUtil;

    @Resource
    private EmailService asyncEmailSender;

    @Resource
    private TicketExpireListener ticketExpireListener;

    @Value("${mixi.ticket.expire:60}")
    private Integer ticketExpire;
    @Resource
    NacosDiscoveryProperties nacosDiscoveryProperties;
    NacosNamingService nacosNamingService = null;
    @PostConstruct
    public void init() throws NacosException {
        nacosNamingService = new NacosNamingService(nacosDiscoveryProperties.getNacosProperties());
    }

    @Override
    public Result<?> createRoom(CreateRoomDTO createRoomDTO) {
        String uid = UserThread.getUserId();
        if(redisOption.hashExist(user(uid), CONNECTED)){
            throw new ServeException(RCode.USER_CONNECTED);
        }
        // 生成roomId
        String roomId = String.valueOf(snowFlakeIdWorker.nextId());
        Map<String, Object> resultMap = new HashMap<>();
        // 判断当前用户状态
        if(redisOption.setHashNx(user(uid), OWN, roomId, 60, TimeUnit.SECONDS)){
            WebRoom webRoom = new WebRoom(createRoomDTO, roomId, webRoomUtil.balanceSocketIp(), webRoomUtil.balanceVideoIp());

            videoService.createRoom();
            // 设置房间相关信息
            redisOption.setHashObject(webRoom(roomId), Map.of(OWNER, uid, INFO, JSONObject.toJSONString(webRoom), NUMBER, 0, MAX, webRoom.getLimit()), 60, TimeUnit.SECONDS);
            log.info(String.format("房间已创建，房间号：%s，创建者：%s", roomId, uid));
        }

        String roomLink = createTicket(roomId, uid);
        resultMap.put("link", roomLink);
        try {
            List<Instance> instances = nacosNamingService.getAllInstances("Mixi-nettyServer");
            List<String> ipList = instances.stream()
                    .map(Instance::getIp)
                    .collect(Collectors.toList());
            resultMap.put("wsURL",ipList.get(new Random().nextInt(ipList.size())));
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }

        return Result.success(resultMap);
    }

    private String createTicket(String roomId, String uid){
        Ticket ticket = Ticket.builder()
                .uId(uid)
                .roomId(roomId)
                .build();
        return webRoomUtil.ticket(ticket);
    }

    private String createLink(String roomId, String uid){
        Ticket ticket = Ticket.builder()
                .uId(uid)
                .roomId(roomId)
                .build();
        return webRoomUtil.link(ticket);
    }

    @Override
    public Result<?> linkShare() {
        String uid = UserThread.getUserId();
        String roomId = redisOption.getHashString(user(uid), OWN);
        Map<String, Object> resultMap = new HashMap<>();

        if(roomId == null){
            throw new ServeException(RCode.THE_USER_DID_NOT_CREATE_A_ROOM);
        }

        String roomLink = createTicket(roomId, null);
        resultMap.put("link", roomLink);
        return Result.success(resultMap);
    }

    @Override
    public Result<?> pull(List<String> emails) {
        String uid = UserThread.getUserId();
        String roomId = redisOption.getHashString(user(uid), OWN);
        if(roomId == null){
            throw new ServeException(RCode.THE_USER_DID_NOT_CREATE_A_ROOM);
        }
        if(!redisOption.setHashNx(webRoom(roomId), PULL_FLAG, true, 3, TimeUnit.MINUTES)){
            throw new ServeException(RCode.PULL_HAS_NOT_COOLED_DOWN);
        }

        String roomLink = createLink(roomId, null);
        for(String email : new HashSet<>(emails)){
            asyncEmailSender.sendLink(email, roomLink);
        }
        return Result.success();
    }

    @Override
    public Result<?> linkJoin(String key) {
        String uid = UserThread.getUserId();
        Ticket ticket = webRoomUtil.decryptLink(key);
        //判断匿名用户是否准入 后续改为业务链

        //用户当前状态为已连接
        if(redisOption.hashExist(user(uid), CONNECTED)){
            throw new ServeException(RCode.USER_CONNECTED);
        }

        if(!redisOption.hashExist(webRoom(ticket.getRoomId()), OWNER)){
            throw new ServeException(RCode.ROOM_NOT_EXIST);
        }

        if(ticketExpireListener.exists(user(uid) + ":" + TICKET)){
            ticketExpireListener.remove(user(uid) + ":" + TICKET);
        }

        if(!redisOption.compareAndIncrement(webRoom(ticket.getRoomId()))){
            throw new ServeException(RCode.ROOM_FULLED);
        }
        WebRoom webRoom = redisOption.getHashObject(webRoom(ticket.getRoomId()), INFO, WebRoom.class);

        JoinVO joinVO = JoinVO.builder()
                .videoIp(webRoom.getVideoIp())
                .ticket(createTicket(ticket.getRoomId(), uid))
                .socketIp(webRoom.getSocketIp()).build();
        redisOption.setCacheObject(user(uid) + ":" + TICKET, joinVO.getTicket(), ticketExpire, TimeUnit.SECONDS);
        ticketExpireListener.put(user(uid) + ":" + TICKET, new String[]{webRoom(ticket.getRoomId()), NUMBER});

        return Result.success(joinVO);
    }

    @Override
    public Result<?> quitRoom(String roomId) {
        String uid = UserThread.getUserId();
        // 房主 or 成员
        if((uid.equals(redisOption.getHashString(webRoom(roomId), OWNER)))) {
            redisOption.deleteHash(webRoom(roomId));
        }
        redisOption.deleteHash(user(uid));
        return Result.success();
    }

    @Override
    public Result<?> transferOwner(String newOwner) {
        String uid = UserThread.getUserId();
        if(!redisOption.transferOwner(uid, newOwner)){
            throw new ServeException(RCode.FAILED_TRANSFER);
        }
        return Result.success();
    }


    @Override
    public Result<?> getRoomInfo() {
        String uid = UserThread.getUserId();
        String roomId = redisOption.getHashString(user(uid), CONNECTED);
        if(roomId == null) throw new ServeException(RCode.THE_USER_DID_NOT_JOINED_ROOM);
        Map<String, Object> roomMap = redisOption.getHashMap(webRoom(roomId));
        WebRoom webRoom = JSONObject.parseObject((String) roomMap.get(INFO), WebRoom.class);
        RoomInfoVO roomInfoVO = new RoomInfoVO();
        BeanUtil.copyProperties(webRoom, roomInfoVO);
        roomInfoVO.setCreateId((String) roomMap.get(OWNER));
        return Result.success(roomInfoVO);
    }

    @Override
    public Result<?> getRoomAddress(String roomId) {



        return Result.success();
    }
}
