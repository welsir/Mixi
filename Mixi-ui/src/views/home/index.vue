<template>
  <div class="chatHome">
    <div class="chatLeft">
      <div class="title">
        <h1 style="font-size: 30px;">{{roomName}}的房间</h1>
      </div>
      <div class="online-person">
        <span class="onlin-text">房间成员</span>
        <div class="person-cards-wrapper">
          <div
              class="personList"
              v-for="personInfo in personList"
              :key="personInfo.id"
          >
            <PersonCard
                :personInfo="personInfo"
                :pcCurrent="pcCurrent"
            ></PersonCard>
          </div>
        </div>
      </div>
    </div>
    <div class="chatRight">
      <div>
        <ChatWindow
            :roomId=roomId
            :uid=uid
            :members="personList">
        </ChatWindow>
      </div>
    </div>
    <!-- <el-col :span="4"><div class="grid-content bg-purple"></div></el-col> -->
  </div>
</template>

<script lang="ts">
import PersonCard from "../../components/room/PersonCard.vue";
import ChatWindow from "../../components/room/chatwindow.vue";
import {chatStore} from "@/store/chatStore";
import {decodeRemoteMessage, heartBeatMessage, joinRoomMessage, queryOnlineMembers} from "@/util/socketMessage";
import Bytes from "@/util/byteUtil";

export default {
  name: "App",
  components: {
    PersonCard,
    ChatWindow,
  },
  data() {
    return {
      pcCurrent: "",
      personList: [
        {
          img: "",
          name: "genius",
          detail: "Genius大傻逼",
          lastMsg: "to do",
          id: "1234",
          headImg: "../../public/img/head_portrait1.jpg",
        },
        {
          img: "",
          name: "伍老板",
          detail: "INTP",
          lastMsg: "dada dw ertgthy j uy",
          id: "123",
          headImg: "../../public/img/head_portrait2.jpg",
        },
        {
          img: "",
          name: "唐文杰",
          detail: "唐文杰666",
          lastMsg: "大萨达萨达所大大萨达",
          id: "12",
          headImg: "../../public/img/head_portrait3.jpg",
        },
      ],
      showChatWindow: false,
      chatWindowInfo: {},
    };
  },
  methods: {
    clickPerson(info) {
      this.showChatWindow = true;
      this.chatWindowInfo = info;
      this.personInfo = info;
      this.pcCurrent = info.id;
    },
    personCardSort(id) {
      if (id !== this.personList[0].id) {
        console.log(id);
        let nowPersonInfo;
        for (let i = 0; i < this.personList.length; i++) {
          if (this.personList[i].id == id) {
            nowPersonInfo = this.personList[i];
            this.personList.splice(i, 1);
            break;
          }
        }
        this.personList.unshift(nowPersonInfo);
      }
    },

  },
  mounted() {
    const store = chatStore();
    const ws = store.createWsIfAbsent();
    ws.init();
    ws.send(joinRoomMessage({roomId: this.roomId, uid: this.uid}))
    ws.send(queryOnlineMembers({roomId:this.roomId}));
    ws.onmessage((event: any) => {
      let data: Blob = event.data;
      data.text().then((res: any) => {
        let msg: any = decodeRemoteMessage(new Bytes(res));
        console.log(msg)
        if(msg.headers.length!=0&&msg.headers[0].cmd==0x0e){
          this.personList = msg.headers[0].members
        }
      });
    });
  },
  setup(){
    const roomName = chatStore().roomName;
    const uid = chatStore().uid;
    const roomId = chatStore().roomId;
    return {
      roomName,
      uid,
      roomId
    }
  }
};
</script>

<style lang="scss" scoped>
.chatHome {
  // margin-top: 20px;
  display: flex;
  .chatLeft {
    width: 280px;
    .title {
      color: #fff;
      padding-left: 10px;
    }
    .online-person {
      margin-top: 65px;
      .onlin-text {
        padding-left: 10px;
        color: rgb(176, 178, 189);
      }
      .person-cards-wrapper {
        padding-left: 10px;
        height: 65vh;
        margin-top: 20px;
        overflow: hidden;
        overflow-y: scroll;
        box-sizing: border-box;
        &::-webkit-scrollbar {
          width: 0; /* Safari,Chrome 隐藏滚动条 */
          height: 0; /* Safari,Chrome 隐藏滚动条 */
          display: none; /* 移动端、pad 上Safari，Chrome，隐藏滚动条 */
        }
      }
    }
  }

  .chatRight {
    flex: 1;
    padding-right: 30px;
    .showIcon {
      position: absolute;
      top: calc(50% - 150px); /*垂直居中 */
      left: calc(50% - 50px); /*水平居中 */
      .icon-snapchat {
        width: 300px;
        height: 300px;
        font-size: 300px;
        // color: rgb(28, 30, 44);
      }
    }
  }
}
</style>
