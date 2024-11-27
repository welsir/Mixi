<!--
 * @Author: Dhx
 * @Date: 2024-07-22 16:49:56
 * @Description:
 * @FilePath: \Mixi\Mixi-ui\src\views\demo\Login.vue
-->
<template>
  <div class="upper" >
    <div class="login-div" style="height: 70vh;width:100vw;position: relative;">
      <form class="login" style="text-align: center;">
        <h1>Mixi</h1>
        <div class="input-text">
          <input type="text" id="inputEmail" name="email" placeholder="Email" v-model="loginForm.email"/>
          <div class="warning-input" id="warningEmail">
            Please enter a valid email or phone number.
          </div>
        </div>

        <div class="input-text">
          <div class="input-group">
            <input type="password" id="inputPassword" name="Code" placeholder="Code" v-model="loginForm.picCode"/>
            <verify :getPicId="getPicId"></verify>
          </div>
          <div class="warning-input" id="warningPassword">
            Your password must contain between 4 and 60 characters.
          </div>
        </div>

        <div>
          <button class="signin-button" @click="loginFunc">登录</button>
        </div>
        <div class="remember-flex">
          <div>
          </div>
          <div class="help">
            <a class="color_text" href="#">跳转注册</a>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import type { LinkLoginForm, VisitorLoginForm } from "@/api/user/userType";
import { linkLoginApi, visitorLoginApi } from '@/api/user/userApi';
import { storage } from '@/util/storage';
import verify from "@/components/common/VerifyCode.vue";

import '../../assets/css/additional.css';
import '../../assets/css/normalize.css';
import '../../assets/css/style.css';

let loginMethod = ref(1)
let loginForm = ref<LinkLoginForm>({
    email: '',
    picId: '',
    picCode: '',
})
let visitorLoginForm = ref<VisitorLoginForm>({
    fingerprint: 'TWJMIXI666',
    picId: '',
    picCode: ''
})
const getPicId = (picId: string) => {
    loginForm.value.picId = picId
}
const loginFunc = (event) => {
    event.preventDefault();
    if (loginMethod.value == 0) {
        linkLoginApi(loginForm.value).then((res: any) => {
            if (res.code == 200) {
                alert('success')
            }else{
              console.log('request fail:'+res.message)
            }
        }, (res: any) => {
            console.log(res.message)
        })
    } else if (loginMethod.value == 1) {
        visitorLoginForm.value.picId = loginForm.value.picId
        visitorLoginForm.value.picCode = loginForm.value.picCode
        visitorLoginApi(visitorLoginForm.value).then((res: any) => {
            if (res.code == 200) {
                storage.set('Authorization', res.message)
            }
        })
    }
}
</script>
<style scoped>
.footer {
  display: flex;
  justify-content: center; /* 居中对齐水平内容 */
  align-items: center; /* 居中对齐垂直内容 */
  padding: 10px; /* 可选：设置内边距 */
  background-color: #f8f9fa; /* 可选：设置背景色 */
  border-top: 1px solid #eaeaea; /* 可选：设置顶部边框 */
}

.author {
  font-size: 14px; /* 可选：设置字体大小 */
}

.author a {
  color: #007bff; /* 可选：设置链接颜色 */
  text-decoration: none; /* 可选：去掉下划线 */
}

.author a:hover {
  text-decoration: underline; /* 可选：设置链接悬停效果 */
}
.input-group {
  display: flex;
  align-items: center; /* 垂直对齐 */
}

.input-group input {
  width: 50%;
  margin-right: 50px;
}

verify {
  margin-left: 10px; /* 与输入框之间的间距 */
}

.input-text {
  width: 100%; /* 确保整个组件适应容器宽度 */
}

.warning-input {
  margin-top: 5px;
  color: red;
  font-size: 12px;
}
input {
    height: 50px;
    outline: none;
    border: black 1px solid;
    margin-left: 10px;
}

.btn {
    width: 150px;
    height: 80px;
    border-radius: 10px;
    line-height: 80px;
    text-align: center;
    color: black;
    font-size: 24px;
    margin-top: 20px;
    cursor: pointer;
    border: rgba(220, 220, 220) 1px solid;
}

.btn:hover {
    background-color: rgba(230, 230, 230);
}
</style>
