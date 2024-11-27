package com.mixi.server.netty.protocol;

import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2024/7/9 21:29
 */
@Data
public class AccessResponse {

    public static final AccessResponse SUCCESS = new AccessResponse(200,"ok");
    public static final AccessResponse INVALID_ROOMID = new AccessResponse(100, "Invalid roomID");
    public static final AccessResponse INVALID_USER_UID = new AccessResponse(101,"Invalid userId");
    public static final AccessResponse JOIN_ROOM_INVALID = new AccessResponse(102,"No allow to join room at the same time");
    public static final AccessResponse NOT_IN_ROOM_WHEN_SEND = new AccessResponse(103, "Not in room when send");
    public static final AccessResponse EMPTY_UID = new AccessResponse(104, "Empty uid");
    /**
     * 状态码 0：成功 其他：失败
     */
    private int code;

    /**
     * 响应数据
     */
    private String desc;

    public AccessResponse() {

    }

    public AccessResponse(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
