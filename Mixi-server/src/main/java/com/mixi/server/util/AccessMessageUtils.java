package com.mixi.server.util;

import com.alibaba.fastjson.JSON;
import com.mixi.server.common.Constants;
import com.mixi.server.netty.protocol.AccessMessage;
import com.mixi.server.netty.protocol.ChatroomMsg;
import com.mixi.server.netty.protocol.Header;
import com.mixi.server.netty.protocol.HeaderEnum;
import com.mixi.server.pojo.VO.TimelineMessage;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author welsir
 * @Date 2024/7/7 20:33
 */
public class AccessMessageUtils {

    public static AccessMessage createHeartResponse() {
        AccessMessage response = new AccessMessage();
        response.setHeartBeat(true);
        return response;
    }

    public static AccessMessage createChatRoomResponse(ChatroomMsg chatroomMsg){
        AccessMessage message = new AccessMessage();
        byte[] body = JSON.toJSONBytes(chatroomMsg);
        message.setBody(body);
        message.setCmd(12);
        message.setVersion(1);
        Header header = new Header(HeaderEnum.CHATROOM.getType(), JSON.toJSONBytes(Map.of(Constants.CHATROOM_ID,chatroomMsg.getRoomId(),Constants.CHATROOM_UID,chatroomMsg.getFromUid())));
        message.setHeaders(List.of(header));
        return message;
    }

    public static String extractHeaderData(AccessMessage message, HeaderEnum headerEnum) {
        int headerType = headerEnum.getType();
        List<Header> headers = message.getHeaders();
        if (CollectionUtils.isEmpty(headers)) {
            return null;
        }
        return headers.stream().filter(header -> header.getType() == headerType).findFirst().map(header -> new String(header.getData(), StandardCharsets.UTF_8)).orElse(null);
    }

    public static AccessMessage createResponse(int cmd, byte[] body) {
        AccessMessage response = new AccessMessage();
        response.setCmd(cmd);
        response.setBody(body);
        response.setVersion(1);
        return response;
    }

    public static AccessMessage createResponse(Header header,byte[] body,int cmd){
        AccessMessage response = new AccessMessage();
        response.setCmd(cmd);
        response.setBody(body);
        response.setVersion(1);
        response.setHeaders(List.of(header));
        return response;
    }
}
