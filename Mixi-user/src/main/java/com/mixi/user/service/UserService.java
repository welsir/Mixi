package com.mixi.user.service;


import com.mixi.common.utils.R;
import com.mixi.user.bean.dto.LoginDTO;
import com.mixi.user.bean.dto.TouristLoginDTO;
import io.github.common.web.Result;

public interface UserService{

    Result<?> getPicCode();

    Result<?> linkLogin(LoginDTO loginDTO,String userAgent);

    Result<?> linkVerify(String linkToken, String userAgent);

    Result<?> getUserInfo(String userId);

    Result<?> visitorUserLogin(TouristLoginDTO touristLoginDTO);
}
