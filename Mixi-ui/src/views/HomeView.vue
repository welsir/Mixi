<!--
 * @Author: LisianthusLeaf
 * @Date: 2024-06-24 10:25:41
 * @LastEditors: LisianthusLeaf
 * @LastEditTime: 2024-07-08 15:21:43
 * @FilePath: \Mi\Mixi-ui\src\views\HomeView.vue
 * @Description:
 * WX:baiyao0211
 * Copyright (c) 2024 by LisianthusLeaf, All Rights Reserved.
-->
<script setup lang="ts">
import { ref } from 'vue'
import router from "@/router";
import Head from "@/components/Head.vue";
let members = ref([
  { name: 'Genius', slogan: 'Genius', description: 'genius大天才', avatar: 'http://geniusay.com/assets/Genius2-1096d28d.jpg' },
  { name: 'Weisir', slogan: 'Weisir', description: '既生welsir，何生genius', avatar: 'https://www.welsir.com/img/icon/logo.JPG' },
  { name: 'Dragon King', slogan: 'Dragon King', description: '坚果云大Boss', avatar: 'https://www.dracoum.com/assets/images/AxeDragon180.png' },
  { name: '项梦缘', slogan: '', description: '', avatar: '' },
  { name: '唐文杰', slogan: '', description: '', avatar: '' },
  { name: '李小春', slogan: '', description: '', avatar: '' },
  { name: '蒋权', slogan: '', description: '', avatar: '' },
  { name: '邓鸿翔', slogan: '', description: '', avatar: '' },
  { name: '郑庹村', slogan: '', description: '', avatar: '' }
])

const images = import.meta.glob('../../public/img/members/*.(png|jpg|svg)');
const backgroundImages = Object.keys(images);
const join = ref(false)
let RoomId = ref()

const joinRoom =()=>{
  join.value = true;
}
const isActive = ref(false);
const toggleAnimation = () => {
  isActive.value = !isActive.value;
  showBackground.value = false;
  setTimeout(() => {
    showImages.value = true;
  }, 500);
};
const showBackground = ref(true);
const showImages = ref(false);
const currentIndex= ref(0);
function changeBackgroundImage() {
  // 每次动画结束后更改背景图索引
  currentIndex.value = (currentIndex.value + 1) % backgroundImages.length;
}
</script>

<template>
  <Head/>
  <div class="main flex">
    <div class="create flex">
      <div v-show="!join" class="create-box flex">
        <h1>Mixi</h1>
        <p>便捷实时通讯网站</p>
        <div>
          <button @click="joinRoom" class="join-btn">加入房间</button>
          <button class="M-btn" style="width: 100px; height: 40px;margin-left:15px">+创建房间</button>
        </div>
      </div>
      <div v-show="join" class="join-room">
        <div @click="join = false" class="close">X</div>
        <h4>加入房间</h4>
        <label style="margin-top: 10px"><input type="checkbox">同意我们的《用户协议》和《隐私计划》</label>
        <div style="margin-top:25px">
          <input v-model="RoomId" placeholder="请输入房间号" class="M-input">
          <button class="M-btn" style="width:60px;height:40px;margin-left:20px">加入</button>
        </div>
      </div>
    </div>
    <div class="team">
      <div class="card-container">
        <div
            class="card imageCoverFull"
            :key="currentIndex"
            :style="{ backgroundImage: `url(${backgroundImages[currentIndex.valueOf()]})` }"
            @animationiteration="changeBackgroundImage"
            @click="toggleAnimation"
        >
          <img :src="members[currentIndex.valueOf()].avatar" alt="avatar" class="memberImg"/>
        </div>
      </div>
      <div v-if="showImages" class="members" :class="{ active: isActive }">
        <div
          v-for="(item, index) in members"
          :key="index"
          class="M-card imageCoverFull"
          :style="{'--i':-4+index, 'background-image': `url(${backgroundImages[index]})`}"
          @click="handleMouseOver(item)"
        >
          <img :src="item.avatar" alt="avatar" />
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
/* 父容器，包含3D效果 */
.card-container {
  perspective: 800px; /* 设置3D效果的视角深度 */
  display: flex;
  justify-content: center;
  align-items: center;
  height: 30vh;
  /* 卡片的基本样式 */
  .card {
    width: 160px;
    height: 240px;
    background-image: url("../../public/img/membersEnter.png");
    border-radius: 10px;
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
    transform: rotate(45deg);
    animation: rotateCard 5s infinite linear;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .card .memberImg{
    position: relative;
    margin-bottom: 140px;
  }
}
/* 定义3D旋转的动画 */
@keyframes rotateCard {
  0% {
    transform: rotate(30deg); /* 保持初始倾斜30度 */
    opacity: 0; /* 初始状态，完全透明 */
  }
  20% {
    opacity: 1; /* 在旋转的20%时完全显示 */
  }
  80% {
    opacity: 1; /* 在接近结束前一直保持完全显示 */
  }
  100% {
    transform: rotate(30deg) rotateY(360deg); /* 360度绕Y轴旋转，并保持30度倾斜 */
    opacity: 0; /* 结束时再次透明 */
  }
}

h1{
  content: 'Mixi';
  position: relative;
  color: #ffffff;
  z-index: 1;
}
h1::after{
  content: 'Mixi';
  position: absolute;
  left: 0;
  color: #000000;
  z-index: -1;
  transform: translate(-35px,3px) scale(0.8) skew(50deg);
  filter: blur(1px);
  -webkit-mask: linear-gradient(transparent,#000);
}
.create {
  position: relative;
  width: 100%;
  height: 500px;
  background-color: rgb(151,157,167);

  .create-box {
    width: 500px;
    height: 300px;
    flex-direction: column;
    h1 {
      font-size: 60px;
    }
    p {
      font-size: 25px;
      color: #6b6b6b;
      margin-bottom: 25px;
    }
    .join-btn{
      background-color: #d7d7d7;
      border: solid 2px #a2a2a2;
      color: #545454;
      width: 100px;
      height: 40px;
      border-radius: 8px;
      cursor: pointer;
    }
    .join-btn:hover{
      background-color: #cccccc;
    }
  }
  .join-room{
    position: relative;
    width:700px;
    height:300px;
    background-color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    .close{
      position: absolute;
      width:50px;
      height:50px;
      display: flex;
      justify-content: center;
      align-items: center;
      top:0;
      left:calc(100% - 50px);
      cursor: pointer;
    }
  }
}
.team {
  width: 90%;
  background-color: white;
  margin-top: 50px;
  margin-bottom: 50px;
  position: relative;
  justify-content: center;
  align-items: center;

  .members{
    position: relative;
    display: flex;
    flex-direction: row; /* 横向排列 */
    flex-wrap: wrap; /* 如果元素太多，超过容器宽度时换行 */
    margin-left: 1000px;
    margin-top: 100px;
  }
  img {
    margin-top: 145px;
    width: 50px;
    height: 50px;
    border-radius: 50px;
    background-color: white;
  }
  .M-card {
    position: absolute;
    width: 240px;
    height: 360px;
    border-radius: 8px;
    display: flex;
    justify-content: center;
    align-content: center;
    color: black;
    font-weight: 700;
    border: 10px solid rgba(0,0,0,.1);
    transition: .5s;
    transform-origin: 50% 100%;
    opacity: 0;
    margin-left: 140px;

    h5 {
      display: flex;
      align-items: center;
      grid-column-start: 1;
      grid-column-end: 3;
    }
  }
  .members.active .M-card {
    transform: rotate(calc(var(--i) * 5deg))
                translate(calc(var(--i) * 120px), -50px);
    box-shadow: 0 15px 50px rgba(0,0,0, .1);
    cursor: pointer;
    opacity: 1; /* 显示卡片 */
    z-index: 1; /* 卡片展示 */
  }

  .members.active .M-card:hover{
    translate: calc(var(--i) * 20px) -50px;
    z-index: 999;
  }

  .imageCoverFull{
    background-size: cover;
    background-position: center;
  }
  h5{
    margin-left: 1090px;
  }
  p {
    color: black;
    margin-top: 5px;
    margin-bottom: 30px;
  }
}
</style>
