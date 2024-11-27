package com.mixi.server.netty.protocol;

import com.mixi.server.pojo.VO.TimelineMessage;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @Description
 * @Author welsir
 * @Date 2024/7/10 21:53
 */
@Data
public class ChatroomMsg implements Serializable {

    private static final long serialVersionUID = 7718754664815537037L;

    private String roomId;
    private Long fromUid;
    private String content;
    private String messageId;

    public static ChatroomMsg convertMsgToChatRoom(TimelineMessage message){
        ChatroomMsg chatroomMsg = new ChatroomMsg();
        chatroomMsg.setRoomId(message.getRoomId());
        chatroomMsg.setContent(message.getContent());
        chatroomMsg.setFromUid(Long.valueOf(message.getFromId()));
        chatroomMsg.setMessageId(String.valueOf(message.getId()));
        return chatroomMsg;
    }
}
