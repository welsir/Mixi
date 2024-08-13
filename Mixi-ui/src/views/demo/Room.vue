<!--
 * @Author: Dhx
 * @Date: 2024-07-28 17:18:12
 * @Description:
 * @FilePath: \Mixi\Mixi-ui\src\views\demo\Room.vue
-->
<template>
    <div style="width: 100%;height: 90vh;display: flex;padding: 70px 0px;">
        <div style="width: 20%;height: 100%;">
            <div style="height: 10%;width: 100%;display: flex;justify-content: space-around;background-color: rgba(20,20,20);">
                <button @click="inviteFunc">邀请好友</button>
                <button @click="share">分享链接</button>
            </div>
            <div style="height: 80%;width: 100%;overflow-y: scroll;background-color: rgba(80,80,80);">
                <ul v-show="invite">
                    <li v-for="i in 30" :key="i" style="margin: 5px;border: none;">
                        <div style="border: none;"
                            v-if="i == 1 || (i > 1 && (inviteEmail[i - 2] != '' && inviteEmail[i - 2] != null))">
                            <span>邮箱：</span><input v-model="inviteEmail[i - 1]">
                        </div>
                    </li>
                    <button @click="sendInvite">发送邀请</button>
                </ul>

            </div>
            <div style="height: 10%;width: 100%;display: flex;justify-content: space-around;background-color: rgba(20,20,20);">
                <button @click="quitRoom">退出房间</button>
            </div>
        </div>
        <div style="width: 70%;height: 100%;">
            <div style="height: 90%;width: 100%;overflow-y: scroll;">
                <div style="width: 100%;min-height: 70px;border: none;"  v-for="(msg,index) in messageArray" :key="index">
                    <messageComponent :message="msg"></messageComponent>
                </div>
            </div>
            <div style="height: 10%;width: 100%;display: flex;background-color: rgba(80,80,80);">
                <input style="height: 50px;width: 80%;margin: 10px;" v-model="messageContent" @keyup.enter="sendMessage">
                <button @click="sendMessage">发送</button>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { shareApi, pullApi, quitApi } from '@/api/room/roomApi'
import MixiWebSocket from '@/util/webSocket';
import {joinRoomMessage,chatMessage,decodeRemoteMessage, heartBeatMessage} from '@/util/socketMessage'
import { onMounted } from 'vue';
import { ref } from 'vue';
import Bytes from '@/util/byteUtil'
import { storage } from '@/util/storage';
import messageComponent from '@/components/room/messageComponent.vue';
let invite = ref(false)
let inviteEmail = ref<String[]>([])
onMounted(() => {
    for (let i = 0; i < 20; i++)inviteEmail.value[i] = ''
})
const sendInvite = () => {
    inviteEmail.value = inviteEmail.value.filter((item, index) => {
        return (inviteEmail.value.indexOf(item) === index) || item != ''
    })
    inviteEmail.value = inviteEmail.value.filter((item, index) => {
        return item != ''
    })
    pullApi(inviteEmail.value).then((res: any) => {
        if (res.code == 200) alert('邀请成功')
    })
}
const inviteFunc = () => {
    invite.value = true
}
const quitRoom = () => {
    quitApi('').then((res:any)=>{

    })
}
const share = () => {
    shareApi().then((res: any) => {
        var input = document.createElement("input");
        var body = document.body;
        body.appendChild(input);
        input.value = res.data.link;
        input.select();
        document.execCommand("copy");
        body.removeChild(input);
        alert('已复制链接')
    })
}
const uid = Math.ceil(Math.random()*100000)
const sendMessage = () => {
    socket.send(chatMessage({roomId:123,uid:uid,content:messageContent.value}))
    messageContent.value = ''
}
const messageArray = ref<any[]>([])
const messageContent = ref('')
const socket = new MixiWebSocket('ws://123.249.107.238:8090/chat')
socket.onopen(() => {
    storage.set('uid',uid)
    socket.send(joinRoomMessage({roomId:123,uid:uid}))
    setInterval(()=>{
        socket.send(heartBeatMessage())
    },5000)
})
socket.onmessage((event: any) => {
    let data:Blob = event.data
    data.text().then((res:any)=>{
        let msg:any = decodeRemoteMessage(new Bytes(res))
        if(msg.body.code == 200||(msg.body=="",msg.headers.length==0))return
        messageArray.value.push(msg)
        console.log(messageArray.value)
    })
})
socket.init()



</script>
<style scoped>
* {
    list-style: none;
}

button {
    height: 50px;
    width: 100px;
    margin: 10px;
}
</style>
