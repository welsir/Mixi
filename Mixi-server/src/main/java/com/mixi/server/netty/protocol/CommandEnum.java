package com.mixi.server.netty.protocol;

/**
 * @Description
 * @Author welsir
 * @Date 2024/7/9 21:36
 */
public enum CommandEnum {
    CHATROOM_JOIN(10, "加入房间"),

    CHATROOM_SEND(12, "发送消息"),
    CHATROOM_QUERY_MEMBERS(14,"查询在现成员");
    private int code;

    private String desc;

    CommandEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }
}
