package com.mixi.server.netty.channel;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.mixi.server.core.store.TimelineMemoryStore;
import com.mixi.server.util.ApplicationContextUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author welsir
 * @Date 2024/7/7 18:57
 */
public class RoomChannelManager {

    private static final Map<String, RoomInfo> ROOM_INFO_MAP = new ConcurrentHashMap<>();
    private static final Map<String,String> MEMBERS_ROOM_MAP = new ConcurrentHashMap<>();
    @Data
    public static class RoomInfo {
        private final String name;
        private final Set<MixiNettyChannel> channels;
        private final Map<String, MixiNettyChannel> memberChannelsMap;
        private AtomicInteger msgCounter = new AtomicInteger(1);
        public RoomInfo(String name) {
            this.name = name;
            this.channels = new ConcurrentHashSet<>();
            this.memberChannelsMap = new ConcurrentHashMap<>();
        }

        public MixiNettyChannel registerUid(String uid, MixiNettyChannel channel) {
            channels.add(channel);
            return memberChannelsMap.putIfAbsent(uid,channel);
        }

        public boolean deregisterUid(MixiNettyChannel channel) {
            String memberUid = channel.getAttrs().getUid();
            if (StringUtils.isBlank(memberUid)) {
                return false;
            }
            channels.remove(channel);
            memberChannelsMap.remove(memberUid);
            return true;
        }

        public Integer generateMsgIdgenerateMsgId(){
            return msgCounter.incrementAndGet();
        }
    }

    public static boolean addChannel(String roomName, MixiNettyChannel channel, String uid) {
        RoomInfo roomInfo = ROOM_INFO_MAP.computeIfAbsent(roomName, RoomInfo::new);
        MEMBERS_ROOM_MAP.put(channel.getChannelId(),roomName);
        if (StringUtils.isNotBlank(uid)) {
            roomInfo.registerUid(uid, channel);
        }
        channel.getAttrs().setEnter(true);
        channel.getAttrs().getRooms().add(roomName);
        MixiNettyChannel.addChannel(uid,channel);
        return true;
    }

    public static void removeChannel(String roomName, MixiNettyChannel channel,boolean admin) {
        MEMBERS_ROOM_MAP.remove(channel.getChannelId());
        RoomInfo roomInfo = ROOM_INFO_MAP.get(roomName);
        if (roomInfo != null) {
            Set<MixiNettyChannel> channels = roomInfo.getChannels();
            TimelineMemoryStore store = ApplicationContextUtils.getBean(TimelineMemoryStore.class);
            if(admin){
                destroyRoom(roomName);
                store.removeRoomStore(roomName);
            }else{
                channels.remove(channel);
                if (channels.isEmpty()) {
                    ROOM_INFO_MAP.computeIfPresent(roomName, (k, v) -> v.getChannels().isEmpty() ? null : v);
                }
                roomInfo.deregisterUid(channel);
                store.removeUserStore(channel.getChannelId());
                if(roomInfo.getChannels().size()==0){
                    destroyRoom(roomName);
                    store.removeRoomStore(roomName);
                }
            }
        }
        channel.getAttrs().setEnter(false);
    }

    public static void destroyRoom(String roomName) {
        RoomInfo roomInfo = ROOM_INFO_MAP.remove(roomName);
        if (roomInfo != null) {
            for (MixiNettyChannel channel : roomInfo.getChannels()) {
                MEMBERS_ROOM_MAP.remove(channel.getChannelId());
                channel.getAttrs().setEnter(false);
            }
        }
    }

    public static RoomInfo getRoomInfo(String roomName) {
        return ROOM_INFO_MAP.get(roomName);
    }

    public static Collection<RoomInfo> getAllRoomInfos() {
        return ROOM_INFO_MAP.values();
    }

    public static String getRoomId(String channelId){
        return MEMBERS_ROOM_MAP.get(channelId);
    }
}
