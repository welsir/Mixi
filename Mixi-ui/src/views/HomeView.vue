<script lang="ts">
import {defineComponent, ref} from 'vue'
import { provide } from 'vue';
import Head from "@/components/Head.vue";
import { defineStore } from 'pinia';
import {Parameter} from "@/api/room/roomType";
import {createApi, joinApi} from "@/api/room/roomApi";
import router from "@/router";

export const chatStore = defineStore('chat', {
  state: () => ({
    roomName: '',
    uid: '',
    roomId: 0,
  }),
  actions: {
    setChatDetails(roomName: string, number: number, uid: string) {
      this.roomName = roomName;
      this.uid = uid;
      this.roomId = number;
    },
  },
});
export default defineComponent({
  setup() {
    const parameter = ref<Parameter>({
      anonymityFlag: false,
      limit: 24,
      roomName: ""
    });
    const join = ref(false);
    const create = ref(false);
    const roomNumber = ref('');
    let RoomId = ref()

    const joinButton =()=>{
      join.value = true;
    }
    const createButton =()=>{
      create.value=true;
    }
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
    const createRoom = () => {
      console.log(parameter.value)
      createApi(parameter.value).then((res:any)=>{
        if(res.code == 200) {
          joinApi(res.data.link).then((res:any)=>{
            if(res.code == 200) {
              console.log(res.data)
            }
          })
        }})
      // const randomNumber = Math.floor(Math.random() * 1e16);
      // const paddedNumber = String(randomNumber).padStart(16, '0');
      // roomNumber.value = paddedNumber.replace(/(.{4})/g, '$1-').slice(0, -1);
      // chatStore().setChatDetails(parameter.value.roomName,Math.floor(Math.random() * 1e6),roomNumber.value);
      // router.push({name: 'chat',});
    }

    const jointRoom = () =>{

    }

    return {
      parameter,
      joinButton,
      jointRoom,
      createButton,
      createRoom,
      join,
      create,
      RoomId,
    };
  }
});
</script>

<template>
  <Head/>
  <div class="main flex">
    <div class="create flex">
      <div v-show="!join" class="create-box flex">
        <h1>Mixi</h1>
        <p>便捷实时通讯网站</p>
        <div>
          <button @click="joinButton" class="join-btn">加入房间</button>
          <button @click="createButton" class="M-btn" style="width: 100px; height: 40px;margin-left:15px">+创建房间</button>
        </div>
      </div>
      <div v-show="join" class="join-room">
        <div @click="join = false" class="close">X</div>
        <h4>加入房间</h4>
        <label style="margin-top: 10px"><input type="checkbox">同意我们的《用户协议》和《隐私计划》</label>
        <div style="margin-top:25px">
          <input v-model="RoomId" placeholder="请输入房间号" class="M-input">
          <button class="M-btn" style="width:60px;height:40px;margin-left:20px" @click="jointRoom">加入</button>
        </div>
      </div>
      <div v-show="create" class="create-room">
        <div class="main flex">
          <div class="create flex">
            <div class="create-card">
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
                </ul>
                <button @click="createRoom()" class="M-btn">+创建房间</button>
              </div>
              <div @click="create = false" class="close">X</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
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
  .create-room{
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
    .create {
      position: relative;
      width: 40%;
      height: 100%;
      background-color: #efefef;

      .create-card {
        width: 100%;
        height: 100%;
        background-color: white;
        border-radius: 10px;
        box-shadow: #d7d7d7 0 0 5px;
        display: flex;
        .close{
          position: absolute;
          width:50px;
          height:50px;
          display: flex;
          justify-content: center;
          align-items: center;
          background-color: white;
          top:0;
          left:calc(100% - 50px);
          cursor: pointer;
        }
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
  }
}
</style>
