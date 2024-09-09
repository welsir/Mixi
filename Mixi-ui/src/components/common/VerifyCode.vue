<template>
    <img :src="'data:image/png;base64,'+imgUrl" :width="props.picWidth" :height="props.picHeight==-1?props.picWidth*0.618:props.picHeight" @click="getVerifyCode" style="cursor: pointer;">
</template>
<script lang="ts" setup>
import {ref} from 'vue'
import { getVerifyCodeApi } from '@/api/common/verifyCodeApi';
let props = defineProps({
    picWidth:{
        type:Number,
        default: 100,
        required: true
    },
    picHeight:{
        type:Number,
        default: -1,
        required: false
    },
    getPicId: {
        type:Function,
        defual: (picId:string)=>{},
        reuired: true
    }
})
let imgUrl = ref('')
let counter = 0
const emit = defineEmits(['picId']);
const getVerifyCode = () => {
    if(counter>5){
        console.log('请勿频繁操作')
    } // 防止频繁获取验证码
    counter++
    getVerifyCodeApi().then((res:any)=>{
      console.log(res)
        if(res.code == 200) {
            imgUrl.value = res.data.base64
            props.getPicId!(res.data.picId)
            emit('picId', res.data.picId);
        }
    })
}
getVerifyCode();
setInterval(()=>{
    if(counter!=0)counter--
},4000)
</script>
<style scoped>
</style>
