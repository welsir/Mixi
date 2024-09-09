<script setup lang="ts">
import { ref } from "vue";
import { useAuthStore } from "@/stores/authStore";
import { createApi,joinApi } from "@/api/room/roomApi";
import type { Parameter } from '@/api/room/roomType'
import router from "@/router";


const data = [
  { avatar: '', name: 'Genius' },
  { avatar: '', name: 'Genius' },
  { avatar: '', name: 'Genius' },
  { avatar: '', name: 'Genius' },
  { avatar: '', name: 'Genius' },
  { avatar: '', name: 'Genius' },
  { avatar: '', name: 'Genius' },
  { avatar: '', name: 'Genius' },
  { avatar: '', name: 'Genius' },
  { avatar: '', name: 'Genius' },
]
const history = ref()
history.value = data.map(item => ({
  ...item, // 展开原始对象
  isChoose: false, // 添加isChoose属性并设置为false
}));
// 筛选已选项
function filterChosenItems() {
  return history.value.filter((item: any) => item.isChoose === true);
}

let parameter = ref<Parameter>({
  anonymityFlag: false, limit: 30, roomName: ""
})
const createRoom = () => {

  createApi(parameter.value).then((res:any)=>{
    if(res.code == 200) {
      joinApi(res.data.link).then((res:any)=>{
        if(res.code == 200) {
          console.log(res.data)
        }
      })
    }
  })
}
</script>

<template>
  <div class="main flex">
    <div class="create flex">
      <div class="create-card">
        <!--历史邀请列表-->
        <div class="history" v-if="useAuthStore().isLoggedIn">
          <h5>历史邀请</h5>
          <ul>
            <li v-for="item in history" @click="item.isChoose = !item.isChoose">
              <img :src="item.avatar" alt="avatar" />
              <label>{{ item.name }}</label>
              <svg v-show="item.isChoose" xml:space="preserve" viewBox="0 0 100 100" y="0" x="0"
                style="margin: initial; display: block; shape-rendering: auto" preserveAspectRatio="xMidYMid" width="30"
                height="30">
                <g class="ldl-scale" style="transform-origin: 50% 50%; transform: rotate(0deg) scale(0.8, 0.8);">
                  <g class="ldl-ani">
                    <g class="ldl-layer">
                      <g class="ldl-ani"
                        style="transform: matrix3d(0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 1); transform-box: view-box; opacity: 1; animation: 1s linear -0.75s infinite normal forwards running animate; transform-origin: 50px 50px;">
                        <path fill="#333"
                          d="M76.5 90h-53C16 90 10 84 10 76.5v-53C10 16 16 10 23.5 10h53C84 10 90 16 90 23.5v53C90 84 84 90 76.5 90z"
                          style="stroke-width: 1; fill: rgb(113, 94, 138);"></path>
                      </g>
                    </g>
                    <g class="ldl-layer">
                      <g class="ldl-ani">
                        <g>
                          <g class="ldl-layer">
                            <g class="ldl-ani"
                              style="transform: matrix3d(0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 1); transform-box: view-box; opacity: 1; animation: 1s linear -1s infinite normal forwards running animate; transform-origin: 50px 50px;">
                              <path d="M44.6 72.7L21.2 49.4l7.6-7.6 15.8 15.9L71.2 31l7.6 7.6z" fill="#abbd81"
                                style="stroke-width: 1; fill: rgb(255, 255, 255);"></path>
                            </g>
                          </g>
                        </g>
                      </g>
                    </g>
                  </g>
                </g>
              </svg>
              <svg v-show="!item.isChoose" xml:space="preserve" viewBox="0 0 100 100" y="0" x="0"
                style="margin: initial; display: block; shape-rendering: auto" preserveAspectRatio="xMidYMid" width="30"
                height="30">
                <g class="ldl-scale" style="transform-origin: 50% 50%; transform: rotate(0deg) scale(0.8, 0.8);">
                  <g class="ldl-ani">
                    <g class="ldl-layer">
                      <g class="ldl-ani"
                        style="opacity: 1; transform-origin: 50px 50px; transform: matrix3d(0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 1); animation: 1s linear -0.75s infinite normal forwards running animate; transform-box: view-box;">
                        <path stroke-miterlimit="10" stroke-width="8" stroke="#333" fill="none"
                          d="M76.5 90h-53C16 90 10 84 10 76.5v-53C10 16 16 10 23.5 10h53C84 10 90 16 90 23.5v53C90 84 84 90 76.5 90z"
                          style="stroke-width: 8; stroke: rgb(113, 94, 138);"></path>
                      </g>
                    </g>
                    <g class="ldl-layer">
                      <g class="ldl-ani">
                        <g>
                          <g class="ldl-layer">
                            <g class="ldl-ani"
                              style="opacity: 1; transform-origin: 50px 50px; transform: matrix3d(0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 1); animation: 1s linear -1s infinite normal forwards running animate; transform-box: view-box;">
                              <path d="M44.6 75.6L19.8 50.8l10.4-10.4 14.4 14.4 25.2-25.2L80.2 40z" fill="#abbd81"
                                style="stroke-width: 1; fill: rgb(113, 94, 138);"></path>
                            </g>
                          </g>
                        </g>
                      </g>
                    </g>
                    <metadata xmlns:d="https://loading.io/stock/"></metadata>
                  </g>
                </g>
              </svg>
            </li>
          </ul>
        </div>
        <!--房间参数设置-->
        <div class="parameter flex">
          <ul>
            <li>
              <h5>房间名</h5>
              <input v-model="parameter.roomName">
            </li>
            <li>
              <h5>人数限制</h5>
              <input v-model="parameter.limit">
            </li>
            <li @click="parameter.anonymityFlag = !parameter.anonymityFlag">
              <svg v-show="parameter.anonymityFlag" xml:space="preserve" viewBox="0 0 100 100" y="0" x="0"
                style="margin: initial; display: block; shape-rendering: auto" preserveAspectRatio="xMidYMid" width="30"
                height="30">
                <g class="ldl-scale" style="transform-origin: 50% 50%; transform: rotate(0deg) scale(0.8, 0.8);">
                  <g class="ldl-ani">
                    <g class="ldl-layer">
                      <g class="ldl-ani"
                        style="transform: matrix3d(0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 1); transform-box: view-box; opacity: 1; animation: 1s linear -0.75s infinite normal forwards running animate; transform-origin: 50px 50px;">
                        <path fill="#333"
                          d="M76.5 90h-53C16 90 10 84 10 76.5v-53C10 16 16 10 23.5 10h53C84 10 90 16 90 23.5v53C90 84 84 90 76.5 90z"
                          style="stroke-width: 1; fill: rgb(113, 94, 138);"></path>
                      </g>
                    </g>
                    <g class="ldl-layer">
                      <g class="ldl-ani">
                        <g>
                          <g class="ldl-layer">
                            <g class="ldl-ani"
                              style="transform: matrix3d(0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 1); transform-box: view-box; opacity: 1; animation: 1s linear -1s infinite normal forwards running animate; transform-origin: 50px 50px;">
                              <path d="M44.6 72.7L21.2 49.4l7.6-7.6 15.8 15.9L71.2 31l7.6 7.6z" fill="#abbd81"
                                style="stroke-width: 1; fill: rgb(255, 255, 255);"></path>
                            </g>
                          </g>
                        </g>
                      </g>
                    </g>
                  </g>
                </g>
              </svg>
              <svg v-show="!parameter.anonymityFlag" xml:space="preserve" viewBox="0 0 100 100" y="0" x="0"
                style="margin: initial; display: block; shape-rendering: auto" preserveAspectRatio="xMidYMid" width="30"
                height="30">
                <g class="ldl-scale" style="transform-origin: 50% 50%; transform: rotate(0deg) scale(0.8, 0.8);">
                  <g class="ldl-ani">
                    <g class="ldl-layer">
                      <g class="ldl-ani"
                        style="opacity: 1; transform-origin: 50px 50px; transform: matrix3d(0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 1); animation: 1s linear -0.75s infinite normal forwards running animate; transform-box: view-box;">
                        <path stroke-miterlimit="10" stroke-width="8" stroke="#333" fill="none"
                          d="M76.5 90h-53C16 90 10 84 10 76.5v-53C10 16 16 10 23.5 10h53C84 10 90 16 90 23.5v53C90 84 84 90 76.5 90z"
                          style="stroke-width: 8; stroke: rgb(113, 94, 138);"></path>
                      </g>
                    </g>
                    <g class="ldl-layer">
                      <g class="ldl-ani">
                        <g>
                          <g class="ldl-layer">
                            <g class="ldl-ani"
                              style="opacity: 1; transform-origin: 50px 50px; transform: matrix3d(0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 0.91, 0, 0, 0, 0, 1); animation: 1s linear -1s infinite normal forwards running animate; transform-box: view-box;">
                              <path d="M44.6 75.6L19.8 50.8l10.4-10.4 14.4 14.4 25.2-25.2L80.2 40z" fill="#abbd81"
                                style="stroke-width: 1; fill: rgb(113, 94, 138);"></path>
                            </g>
                          </g>
                        </g>
                      </g>
                    </g>
                  </g>
                </g>
              </svg>
              <h5>允许游客进入</h5>
            </li>
          </ul>
          <button @click="createRoom()" class="M-btn">+创建房间</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.create {
  position: relative;
  width: 100%;
  height: 650px;
  background-color: #efefef;

  .create-card {
    width: 80%;
    height: 80%;
    background-color: white;
    border-radius: 10px;
    box-shadow: #d7d7d7 0 0 5px;
    display: flex;
  }
}

.history {
  min-width: 200px;
  width: 30%;
  padding: 30px 0px 30px 30px;

  h5 {
    font-weight: normal;
    font-size: 25px;
    padding-bottom: 10px;
    border-right: solid #d7d7d7 1px;
  }

  ul {
    border-right: solid #d7d7d7 1px;
    list-style: none;
    padding-left: 0;
    overflow-y: scroll;
    height: 90%;

    li {
      display: grid;
      align-items: center;
      grid-template-columns: 60px auto 50px;
      line-height: 55px;

      img {
        width: 40px;
        height: 40px;
        background-color: #e1beef;
        border-radius: 50px;
      }

      input {
        position: relative;
        float: right;
      }
    }
  }

  ul::-webkit-scrollbar {
    display: none;
    /* Chrome, Safari, Opera */
  }
}

.parameter {
  flex: 1;
  flex-direction: column;

  button {
    margin-top: 50px;
    width: 300px;
    height: 50px;
  }

  ul {
    list-style: none;
    padding-left: 0;
    width: 300px;

    li {
      display: grid;
      align-items: center;
      grid-template-columns: 100px auto;
      line-height: 60px;

      input {
        height: 40px;
        border-radius: 5px;
      }
    }
  }
}
</style>
