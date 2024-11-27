<!--
 * @Author: Dhx
 * @Date: 2024-07-24 11:11:16
 * @Description:
 * @FilePath: \Mixi\Mixi-ui\src\views\Verify-email.vue
-->
<template>
    <div style="width: 100vw;height: 100vh;line-height: 100vh;font-size: 32px;text-align: center;">
        <span v-if="time!=0">验证成功! {{ time }} 秒后跳转到首页。</span>
    </div>
</template>
<script lang="ts" setup>
import { onMounted,ref } from 'vue';
import { useRouter } from 'vue-router';
import {linkVerifyApi} from '@/api/user/userApi'
import { storage } from '@/util/storage';
import {useAuthStore} from "@/store/authStore";

let router = useRouter();
let time = ref(0)
onMounted(()=>{
    linkVerifyApi({linkToken:router.currentRoute.value.query.token as string}).then((res:any)=>{
        if(res.code == 200) {
            useAuthStore().setLoggedIn({
                username: '',
                email: '',
                password: '',
                avatar: '',
                nickname: '',
                sex: '',
                resume: ''
            })
            time.value = 6
            storage.set("Authorization",res.data.token)
            let interval  = setInterval(()=>{
                if(time.value == 0){
                    clearInterval(interval)
                    router.push('/HomeView')
                }
                time.value--
            },1000)
        }
    },(res:any)=>{
        // error
    })
})

</script>
<style scoped>
</style>
