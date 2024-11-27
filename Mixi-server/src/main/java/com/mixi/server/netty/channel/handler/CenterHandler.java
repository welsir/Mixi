package com.mixi.server.netty.channel.handler;

import com.alibaba.fastjson.JSON;
import com.mixi.server.netty.channel.MixiNettyChannel;
import com.mixi.server.netty.channel.RoomChannelManager;
import com.mixi.server.netty.channel.support.ChannelAttrs;
import com.mixi.server.netty.handler.ChatRoomHandler;
import com.mixi.server.netty.handler.MixiHandler;
import com.mixi.server.netty.handler.MixiHandlerRouter;
import com.mixi.server.netty.protocol.AccessMessage;
import com.mixi.server.util.AccessMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @Description
 * @Author welsir
 * @Date 2024/7/7 19:48
 */
@Slf4j
public class CenterHandler implements ChannelHandler {

    @Override
    public void connect(MixiNettyChannel channel) {

    }

    @Override
    public void disconnect(MixiNettyChannel channel) {
        ChannelAttrs attrs = channel.getAttrsIfExists();
        if (attrs == null) {
            return;
        }
        Set<String> rooms = attrs.getRooms();
        for (String room : rooms) {
            RoomChannelManager.getRoomInfo(room).deregisterUid(channel);
            RoomChannelManager.removeChannel(room,channel,false);
        }
    }

    @Override
    public void send(MixiNettyChannel channel, Object msg) {

    }

    @Override
    public void receive(MixiNettyChannel channel, Object msg) {
        AccessMessage message = (AccessMessage) msg;
        MixiHandler handler = MixiHandlerRouter.route(message.getCmd());
        Assert.notNull(handler,"cmd is error! please check the cmd");
        Object res = handler.processEvent(channel, message);
        AccessMessage response = AccessMessageUtils.createResponse(message.getCmd(), JSON.toJSONBytes(res));
        log.info("The connection of {} -> {} receive message, channelId={}", channel.getRemoteAddress(), channel.getLocalAddress(), channel.getChannelId());
        channel.send(response);
    }

    @Override
    public void caught(MixiNettyChannel channel, Throwable throwable) {

    }

}
