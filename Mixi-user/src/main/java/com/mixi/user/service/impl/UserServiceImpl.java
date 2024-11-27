package com.mixi.user.service.impl;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import com.mixi.common.exception.ServeException;
import com.mixi.common.utils.R;
import com.mixi.common.utils.RCode;
import com.mixi.common.utils.ThreadContext;
import com.mixi.common.utils.UserThread;
import com.mixi.user.bean.UserAgentInfo;
import com.mixi.user.bean.dto.LoginDTO;
import com.mixi.user.bean.LinkInfo;
import com.mixi.user.bean.dto.TouristLoginDTO;
import com.mixi.user.bean.entity.User;
import com.mixi.user.bean.vo.UserVO;
import com.mixi.user.chain.PicCodeVerifyChain;
import com.mixi.user.config.UserPropertiesConfig;
import com.mixi.user.domain.CaptchaServiceGateway;
import com.mixi.user.domain.RedisGateway;
import com.mixi.user.service.UserService;
import com.mixi.user.utils.AgentUtil;
import com.mixi.user.utils.UserUtil;
import io.github.common.SafeBag;
import io.github.common.web.Result;
import io.github.servicechain.ServiceChainFactory;
import io.github.servicechain.bootstrap.ReturnType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

import static com.mixi.common.constant.constpool.TransferConstant.FINGER_PRINT;
import static com.mixi.user.constants.RedisKeyConstant.*;
import static com.mixi.user.constants.ServeCodeConstant.REPEAT_OPERATION;
import static com.mixi.user.constants.ServeCodeConstant.TOKEN_GENERATE_ERROR;
import static com.mixi.user.utils.FingerprintUtil.isValidFingerprint;

@RequiredArgsConstructor
@SuppressWarnings("all")
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserPropertiesConfig propertiesConfig;

    @Resource
    private CaptchaServiceGateway captchaServiceGateway;

    @Resource
    private UserDaoServiceImpl userDaoService;

    @Resource
    private UserAsyncService userAsyncService;

    @Resource
    private RedisGateway redisGateway;

    @Resource
    private ServiceChainFactory chainFactory;

    @Resource
    private UserUtil userUtil;

    @Resource
    private StringEncryptor encryptor;


    @Override
    public Result<?> getPicCode() {
        Result<Map<String, String>> result = captchaServiceGateway.image();
        if (!String.valueOf(RCode.SUCCESS.getCode()).equals(result.getCode())) {
            throw ServeException.SystemError("获取图片验证码失败");
        }
        Map<String, String> data = result.getData();
        String base64 = data.get("base64");
        String code = data.get("code");
        String picId = UUID.randomUUID().toString();
        log.info("生成图片验证码成功，picId:{},code:{}", picId, code);

        redisGateway.set(PIC_CODE_KEY, code, picId);

        return Result.success(Map.of("base64", base64, "picId", picId));
    }

    @Override
    public Result<?> linkLogin(LoginDTO loginDTO, String userAgent) {

        UserAgentInfo agentInfo = AgentUtil.getUserAgent(userAgent);

        chainFactory.get("linkLogin")
                .<LoginDTO>supplierMap(Map.of(
                        1, (loginDto) -> new String[]{loginDto.getPicId(), loginDto.getPicCode()},
                        2, (loginDto) -> agentInfo,
                        3, LoginDTO::getEmail
                ))
                .returnType(ReturnType.THROW)
                .execute(loginDTO);

        String email = loginDTO.getEmail();

        //生成短链
        String shortLink = generateShortLink(email, agentInfo);
        if (!redisGateway.setIfAbsent(EMAIL_LINK_TOKEN_KEY, shortLink, email)) {
            String msg = String.format("该邮箱已经发送校验邮件，请稍后%s分钟后再试", EMAIL_LINK_TOKEN_KEY.getTime());
            throw ServeException.of(REPEAT_OPERATION, msg);
        }

        // async send email
        userAsyncService.sendEmail(email, propertiesConfig.getVerifyLinkUrl(), shortLink);

        return Result.success();
    }

    private String generateShortLink(String email, UserAgentInfo agentInfo) {
        LinkInfo linkInfo = LinkInfo.builder()
                .browser(agentInfo.getBrowser())
                .os(agentInfo.getOs())
                .ltId(UUID.randomUUID().toString())
                .email(email).build();
        return encryptor.encrypt(JSONObject.toJSONString(linkInfo));
    }

    @Override
    public Result<?> linkVerify(String linkToken, String userAgent) {
        // 解析 linkToken
        UserAgentInfo agentInfo = AgentUtil.getUserAgent(userAgent);
        ThreadContext.setData("agentInfo", agentInfo);
        SafeBag<LinkInfo> linkInfo = new SafeBag<>();


        chainFactory.get("linkVerify")
                .<String>supplierMap(Map.of(
                        1, (obj) -> agentInfo,
                        3, (obj) -> {
                            linkInfo.setData(ThreadContext.context().getData("LinkInfo", LinkInfo.class));
                            return linkInfo.getData().getEmail();
                        }
                ))
                // 邮箱不存在则直接进行用户注册
                .failCallbackMap(Map.of(
                        3, () -> {
                            emailUserNoPwdRegister(linkInfo.getData().getEmail());
                        }
                ))
                // 邮箱存在则进行用户登录
                .successCallbackMap(Map.of(
                        3, () -> {
                            String email = linkInfo.getData().getEmail();
                            log.info("{}邮箱已存在，进行登录", email);
                            User user = userDaoService.query()
                                    .eq("email", email)
                                    .eq("del_flag", false)
                                    .one();
                            if (!chainFactory.get("tokenAndInfo").execute(user)) {
                                throw ServeException.of(TOKEN_GENERATE_ERROR, "token生成失败");
                            }
                        }
                ))
                .returnType(ReturnType.THROW)
                .execute(linkToken);

        // 生成token
        return Result.success(Map.of(
                "token", ThreadContext.getData("token")
        ));
    }

    /**
     * 邮箱链接直接注册
     */
    private void emailUserNoPwdRegister(String email) {

        // 尝试从用户线程里获取指纹
        Object fingerprint = UserThread.getField(FINGER_PRINT);

        User user;

        // 如果有指纹，代表此用户之前是使用游客身份登录过的
        if (fingerprint != null) {

            // 验证指纹的合法性
            if (!isValidFingerprint(fingerprint.toString())) throw new ServeException(RCode.ILLEGAL_FINGERPRINT);

            // 尝试从数据库里获取此用户
            user = userDaoService.getUserByFinger(fingerprint.toString());

            if (user == null) throw new ServeException(RCode.ILLEGAL_FINGERPRINT);

            // 将此用户转正
            user = convertVisitorToRegularUser(user, email);
        }

        // 如果没有指纹，代表此用户之前没有用游客身份登录，走正常注册
        else {
            user = userUtil.newJoinUser(email, null);
            log.info("邮箱不存在，进行用户注册,用户邮箱为:{} 新用户名为:{}", user.getEmail(), user.getNickname());
            userDaoService.register(user);
        }


        if (!chainFactory.get("tokenAndInfo").execute(user)) {
            throw ServeException.of(TOKEN_GENERATE_ERROR, "token生成失败");
        }
    }

    /**
     * 获取用户信息(uid为空代表获取自己的信息)
     */
    @Override
    public Result<?> getUserInfo(String uid) {
        uid = StringUtils.isEmpty(uid) ? UserThread.getUserId() : uid;
        String userJson = redisGateway.get(USER_INFO_KEY, uid);
        UserVO userVO;
        if (StringUtils.isNotEmpty(userJson)) {
            userVO = JSONObject.parseObject(userJson, UserVO.class);
        } else {
            userVO = new UserVO();
            User userEntity = userDaoService.query().eq("id", uid).one();

            if (userEntity == null) throw new ServeException(RCode.USER_DOES_NOT_EXIST);

            BeanUtils.copyProperties(userEntity, userVO);
            userAsyncService.saveUserInfo(userEntity);
        }
        return Result.success(
                Map.of(
                        "userInfo", userVO
                )
        );
    }

    /**
     * 根据指纹生成游客token（指纹不存在，将新建游客用户）
     * @return 游客用户登录token
     */
    @Override
    public Result<?> visitorUserLogin(TouristLoginDTO loginDTO) {

        // 验证验证码是否正确
        ((PicCodeVerifyChain) chainFactory.getChain("picCodeVerify")).filter(new String[]{loginDTO.getPicId(), loginDTO.getPicCode()});

        String fingerprint = loginDTO.getFingerprint();

        // 验证指纹的合法性
        if (!isValidFingerprint(fingerprint)) throw new ServeException(RCode.ILLEGAL_FINGERPRINT);

        // 判断该指纹是否存在数据库中
        User user = userDaoService.getUserByFinger(fingerprint);

        // 指纹不存在，走游客用户创建逻辑
        if (user == null) {
            user = userUtil.newJoinUser(fingerprint);
            log.info("指纹不存在，进行游客用户注册，用户指纹为:{} 新用户名为:{}", user.getFinger(), user.getNickname());
            userDaoService.register(user);
        }

        // 登录并生成token
        if (!chainFactory.get("tokenAndInfo").execute(user)) {
            throw ServeException.of(TOKEN_GENERATE_ERROR, "token生成失败");
        }

        log.info("游客用户:{}登录成功", user.getNickname());

        // 返回游客token
        return Result.success(Map.of("token",ThreadContext.getData("token").toString(),"uid",UUID.randomUUID().toString()));
    }

    /**
     * 将游客用户转正为正常用户
     */
    private User convertVisitorToRegularUser(User user, String email) {
        // 用户信息更新为正常用户
        user.setEmail(email);
        user.setUsername(email);
        user.setRoles(userUtil.getUserRole());

        // 保存更新后的用户信息
        userDaoService.updateById(user);

        log.info("用户:{} 转正成功，新的邮箱为:{}", user.getNickname(), email);
        return user;
    }
}
