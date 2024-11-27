import request from "@/util/request";
import {useAuthStore} from "@/store/authStore";
import type {Profile,VisitorLoginForm,LinkLoginForm, LinkVerifyForm} from "@/api/user/userType";

export function loginApi(form: Profile) {
    return request({
        url: '/api/user/login',
        method: 'post',
        data: form
    })
}
export function logoutApi() {
    return request({
        url: '/api/user/logout',
        method: 'post'
    })
}
export function linkLoginApi(form:LinkLoginForm) {
    return request({
        url: '/api/user/linkLogin',
        method: 'post',
        data:form
    })
}
export function linkVerifyApi(form:LinkVerifyForm) {
    return request({
        url: '/api/user/linkVerify',
        method: 'get',
        params: form
    })
}
export function getUserInfoApi(uid:string){
    return request({
        url: '/api/user/getUserInfo',
        method: 'get',
        params:uid
    })
}
export function visitorLoginApi(form:VisitorLoginForm){
    return request({
        url: '/api/user/visit/login',
        method: 'post',
        data:form
    })
}

export function sendPicCaptcha(){
    return request.post('/api/user/code/pic')
}


export function emailLogin(loginForm){
    return request.post('/user/login',loginForm)
}

export function emailRegister(registerForm){
    return request.post('/user/register',registerForm)
}

export function sendCodeToEmail(email: string,pid: string,code: string){
    return request({
        url: '/user/sendCaptcha',
        method:"post",
        params:{
            email: email,
            pid: pid,
            code: code
        }
    })
}

export function getUserInfo(){
    return request({
        url: '/user/getInfo',
        method:"get",
    })
}

