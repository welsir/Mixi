package com.mixi.webroom.controller;

import com.mixi.common.annotation.auth.ApiAuth;
import com.mixi.common.annotation.auth.AuthType;
import com.mixi.webroom.pojo.dto.CreateRoomDTO;
import com.mixi.webroom.service.WebRoomService;
import org.springframework.web.bind.annotation.*;
import io.github.common.web.Result;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
*@Author：XiaoChun
*@Date：2024/6/25  17:18
*/
@RestController
@RequestMapping("/webRoom")
public class WebRoomController {

    @Resource
    WebRoomService webRoomService;

    @PostMapping("/create")
    @ApiAuth(value = AuthType.NEED)
    public Result<?> createRoom(@RequestBody
                                    @Valid
                                    CreateRoomDTO createRoomDTO) {
        return webRoomService.createRoom(createRoomDTO);
    }

    @GetMapping("/linkShare")
    @ApiAuth(value = AuthType.NEED)
    public Result<?> linkShare() {
        return webRoomService.linkShare();
    }

    @PostMapping("/pull")
    @ApiAuth(value = AuthType.NEED, before = "TouristBeforeHandler")
    public Result<?> pull(@RequestBody
                              @Valid
                              @Size(max = 20, message = "The maximum limit for email list is 20")
                              List<String> emails) {
        return webRoomService.pull(emails);
    }

    @GetMapping("/linkJoin")
    @ApiAuth(value = AuthType.NEED)
    public Result<?> linkJoin(@RequestParam
                                  @Valid
                                  @NotBlank
                                  String key) {
        return webRoomService.linkJoin(key);
    }

    @PostMapping("/quit")
    @ApiAuth(value = AuthType.NEED)
    public Result<?> quitRoom(@RequestPart
                                  @Valid
                                  @NotBlank
                                  String roomId) {
        return webRoomService.quitRoom(roomId);
    }

    @PostMapping("/transferOwner")
    @ApiAuth(value = AuthType.NEED)
    public Result<?> transferOwner(@RequestPart
                                   @Valid
                                   @NotBlank
                                   String newOwner){
        return webRoomService.transferOwner(newOwner);
    }

    @GetMapping("/getRoomInfo")
    @ApiAuth(value = AuthType.NEED)
    public Result<?> getRoomInfo() {
        return webRoomService.getRoomInfo();
    }

    @PostMapping("/queryRoom")
    public Result<?> queryRoomIfAbsent(@NotBlank String roomId){
        return webRoomService.getRoomAddress(roomId);
    }
}
