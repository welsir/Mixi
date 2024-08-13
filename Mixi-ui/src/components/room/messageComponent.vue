<template>
    <div style="text-align: center;" v-if="headers.length!=0&&headers[0].cmd == '10'">
        <span style="color: skyblue;text-decoration: underline;">{{ headers[0].uid }}</span> 加入房间
    </div>
    <div class="message-box" :style="{flexDirection:currentUser?'row-reverse':'row'}" v-else-if="body">
        <div clas="message-box__left">
            <div style="height: 40px;width:40px;border-radius: 20px;background-image: url('/favicon.ico');background-repeat: no-repeat;background-position: center center;"></div>
        </div>
        <div clas="message-box__right">
            <div class="message-box__right__content">
                <div class="content" :style="{backgroundColor:currentUser?'rgba(174,221,255)':'rgba(240,240,245)'}" >
                    <div>{{ body.content }}</div>
                </div>
                
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { storage } from '@/util/storage';
import { onMounted } from 'vue';
import {ref} from 'vue'
const props = defineProps<{
    message:any
}>()
const msg = ref<any>({})
const currentUser = ref<boolean>()
const headers = ref<any[]>([])
const body = ref<any>()
onMounted(()=>{
    msg.value = props.message
    headers.value = msg.value.headers
    body.value = msg.value.body
    console.log(body.value.fromUid)
    console.log(storage.get('uid'))
    currentUser.value = body.value.fromUid === storage.get('uid')
})
</script>
<style scoped>
*{
    border: none;
}
.message-box{
    width: calc(100% - 40px);
    padding: 20px;
    display: flex;
    min-height: 100%;
}

.message-box__left{
    width: 30px;
}

.message-box__right{
    width: calc(100% - 30px);
}

.message-box__right__content{
    width: 100%;
}

.content {
    margin: 0 10px;
    padding: 10px;
    border-radius: 10px;
    text-overflow: clip;
}
</style>