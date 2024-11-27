package com.mixi.webroom.service;

import com.mixi.webroom.pojo.dto.CreateRoomDTO;
import io.github.common.web.Result;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @Author：XiaoChun
 * @Date：2024/6/25 17:20
 */
@Component
public interface WebRoomService {
    Result<?> createRoom(CreateRoomDTO createRoomDTO);

    Result<?> linkShare();

    Result<?> pull(List<String> emails);

    Result<?> linkJoin(String key);

    Result<?> quitRoom(String roomId);

    Result<?> transferOwner(String newOwner);

    Result<?> getRoomInfo();

    Result<?> getRoomAddress(String roomId);
}
