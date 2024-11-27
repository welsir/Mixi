<script lang="ts" setup>
import {ref} from 'vue'
import { useAuthStore } from "@/store/authStore";
import { getUserInfoApi } from '@/api/user/userApi'
import { onMounted } from "vue";
import { storage } from '@/util/storage';
import router from '@/router';
onMounted(() => {
  getUserInfoApi('').then((res: any) => {
    if (res.code == 200) {
      useAuthStore().setLoggedIn(res.data.userInfo)
    }
  }, (error: any) => {

  })
})
let UserStatusMenu =ref({
  open: false,

})
const clickAvatar = ()=>{
  UserStatusMenu.value.open = !UserStatusMenu.value.open
}
const blurAvatar = () =>{
  UserStatusMenu.value.open = false
}
const logout = () => {
  storage.remove("Authorization")
  router.go(0)
}
</script>
<template>
    <div class="user-status" tabindex="1" @blur="blurAvatar" >
        <img :src="useAuthStore().getProfile?.avatar" alt="avatar" @click="clickAvatar"
            />
        <div class="user-status__menu"
            :style="{ visibility: UserStatusMenu.open ? 'visible' : 'hidden', height: UserStatusMenu.open ? 'auto' : '0' }">
            <div class="user-status__menu-item" @click="logout">
                退出登录
            </div>
        </div>
    </div>
</template>
<style scoped>

.user-status {
  position: relative;
  width: 80px;

  img {
    position: relative;
    left: 50%;
    transform: translate(-50%);
    cursor: pointer;
    width: 50px;
    height: 50px;
    border-radius: 25px;
    transition: all 0.4s;
  }

  img:hover {
    scale: 1.05;
  }

  .user-status__menu {
    position: absolute;
    height: 300px;
    width: 120px;
    transform: translate(-40px);
    top: 70px;
    transition: height 0.5s;
    overflow: hidden;
    border-radius: 5px;
    border: rgba(220,220,220) 1px solid;
    background-color: white;
    padding:0px 5px;

    .user-status__menu-item {
      height: 50px;
      width: 100%;
      text-align: center;
      line-height: 50px;
      color: black;
      border-radius: 5px;
      margin: 5px 0;
      cursor: pointer;
    }
    .user-status__menu-item:hover{
        background-color: rgba(230,230,230);
      }
  }
}
</style>
