<!--
 * @Author: Dhx
 * @Date: 2024-07-22 16:05:16
 * @Description:
 * @FilePath: \Mixi\Mixi-ui\src\components\Head.vue
-->
<script setup lang="ts">
import {ref} from 'vue'
import router from "@/router/index";
import { useAuthStore } from "@/store/authStore";
import UserStatus from '@/components/user/UserStatus.vue'
import Login from "@/views/demo/Login.vue";
import presetUna from '@una-ui/preset'
import prefixes from '@una-ui/preset/prefixes'
import extratorUna from '@una-ui/extractor-vue-script'
import {
  presetAttributify,
  presetIcons,
  presetUno,
  transformerDirectives,
  transformerVariantGroup,
} from 'unocss'
const loginButton = ref(false);
 const presets = [
  presetUno(),
  presetAttributify(),
  presetIcons({
    scale: 1.2,
    extraProperties: {
      'display': 'inline-block',
      'vertical-align': 'middle',
    },
  }),
  presetUna(),
]
   const extractors = [
  extratorUna({
    prefixes,
  }),
]
    const transformers= [
  transformerDirectives(),
  transformerVariantGroup(),
]
</script>

<template>
  <header class="flex">
    <div class="logo flex" @click="router.push('/HomeView')">Mixi</div>
    <div class="navs flex">
      <nav class="flex" @click="router.push('/price')">使用指南</nav>
      <nav class="flex" @click="router.push('/price')">定价</nav>
      <nav class="flex" @click="router.push('/about')">关于我们</nav>
      <nav class="flex">
        <button @click="loginButton = true" v-if="!useAuthStore().getLoginState" class="M-btn">登录/注册</button>
        <UserStatus v-else></UserStatus>
      </nav>
    </div>
    <div v-show="loginButton" class="loginForm">
      <Login></Login>
    </div>
  </header>
</template>

<style scoped>
header {
  position: fixed;
  top: 0;
  left: 0;
  height: 5rem;
  width: 100%;
  box-shadow: #d7d7d7 1px 1px 3px;
  z-index: 10;
  background-color: white;
  justify-content: space-between;

  .logo {
    width: 200px;
    height: 100%;
    font-family: 'Juice ITC';
    font-size: 50px;
    cursor: pointer;
  }

  .M-btn {
    width: 150px;
    height: 40px;
  }

  .navs {
    height: 100%;

    nav {
      top: 0;
      position: sticky;
      padding: 20px;
      height: 100%;

      img {
        width: 50px;
        height: 50px;
        background-color: #efefef;
      }
    }
  }
}
.loginForm{
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
</style>
