<template>
  <div class="login">
    <img src="../../../public/img/login-bg.png" alt="login image" class="login__img" />

    <form @submit.prevent="handleSubmit" class="login__form">
      <div class="login__content">
        <div class="login__box">
          <i class="ri-user-3-line login__icon"></i>

          <div class="login__box-input">
            <input
                type="email"
                v-model="loginForm.email"
                required
                class="login__input"
                id="login-email"
                placeholder=" "
            />
            <label for="login-email" class="login__label">邮箱</label>
          </div>
        </div>

        <div class="login__picCode">
          <div class="login__box">
            <i class="ri-lock-2-line login__icon"></i>

            <div class="login__box-input">
              <input
                  type="text"
                  v-model="loginForm.code"
                  required
                  class="login__input"
                  id="login-verification-code"
                  placeholder=" "

              />
              <label for="login-verification-code" class="login__label">邮箱验证码</label>
            </div>
          </div>
          <button class="login__button-picCode" @click="verityEmailAndGeneratePicCode">
            发送验证码
          </button>
        </div>
        <div class="login__picCode">
          <div class="login__box">
            <i class="ri-lock-2-line login__icon"></i>

            <div class="login__box-input">
              <input
                  type="text"
                  v-model="loginForm.code"
                  required
                  class="login__input"
                  id="login-verification-code"
                  placeholder=" "

              />
              <label for="login-verification-code" class="login__label">验证码</label>
            </div>
          </div>
          <img v-if="!picLoading" @click="generatePicCode()" :src="picCode.img" alt="Base64 Image" class="pic-code" />
          <v-img v-else
                 class="mx-auto"
                 :lazy-src="preImg"
          >
            <template v-slot:placeholder>
              <CircleLoading/>
            </template>
          </v-img>
        </div>
      </div>

      <button type="submit" class="login__button">登录</button>

      <p class="login__register">
        第一次访问？ <a href="#" @click="guestLogin">尝试游客登陆</a>
      </p>
    </form>
  </div>
</template>
<style scoped>
</style>

<script setup lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import '@/styles/main.css'
import {LoginForm,PicCode, defaultValue} from "@/views/login/LoginTypes";
import {loginApi,linkLoginApi,sendPicCaptcha,visitorLoginApi} from '@/api/user/userApi'
import CircleLoading from "@/components/loading/CircleLoading.vue";
import Fingerprint2 from 'fingerprintjs2';
const loginForm = ref<LoginForm>({...defaultValue.defaultLoginForm})
const picCode = ref<PicCode>({...defaultValue.defaultPicCode})
const needOpenPicCode = ref(false)
const isPasswordVisible = ref(false)
const picLoading = ref(true)
const preImg = ref("data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAALQAAAAoCAIAAADYC0ddAAAE2UlEQVR4Xu2VzauVVRTGbaA0SSNoUHJARNCBiBkZQnS9cALp4w8QihxUBH04cBCUkyBvE0GE60AHanJHd3QbZFyLICGyyYXoA0n6BPEjsIGzBuGSHZvNetZae+193/Oe9+W+8EPuWet5nr3O4QHXjT56YGBAZB2OBhpn+91bBM4NZq7uInDeJr0sx9zhTwmcd5ahHO3Ri3L8t7JM4LxH9LIcvWAoRxt8vekogfO+MH7ibAS3Xaa9cqS/UdGPFcphe42VweKXp4kKYxFavjbXSPUVFtxmaaYcG17cSOA8hX03/8XoEr32VoSVAwWNoF2lzTVKv2CRWGTK5fDcjRbRaG+RUr2TUy8sEelEe0WbExf3vk5oYtESeHb7dwRaUOmhmXJkwe/mvBv1hsujKVWWwsphvGKsVl8OjzjLFMqBHw3Yl7RdHo1f1gjGQ8bKUFZYcJsyu/IOgfNRRTm+efQMgXMbdqvz9ApXU5osj+1/nsA5w3grXeFWVLK/NfzJo3bKMbf5JwLnAXar53T8hnWuUoGTvpQDt4aYuYrLoTHRcmgTEUOWrnDr1/iVhoDZ31j/CmEkiB9FjEdFDePD55YJ+qOxchDvvvkHET++dm0PMZJuZddgFG5xoqGFa3OnvUJWJGiqHNlHmQbpbjmOLVwicIsTDTFcHGpkxVkBynB7Z9czhCFIE4yJaNl54WUCt2mCkRNWTZZDQzxFHI6gHGG47cR9RL1IGo6gHjH0/jRDdvvX94lmy/Hb7M9E2GrlSE8SQ1I6Vw7x+gbLgWIRzaXNRZjyxud/EmEVymFHiStxGEjLkQoeOn+SYHYxgTHxcqTXpAeJQ22uhRgwi9+oJYgTAxSn5UCNkZAd2mmxHKlATGD8X47FpYcJXK8e7RpxLg6NuUFqyRo3XPuesBPEiUGUfXzuPQIFqQbTSud2mlPA6EQ54gon9txGfEJELAcmONOYN5Rj918LhKbBwPOzIwLnmt6IEjWGLNLqfyvaPKw0pb0yqHOlpAmlaUxfVA76OIlyMJmtHDVYjqPfXiZwbpzCDnUq8QmNOhcje5uGx6JpxGFcbf5qHyHqRYumzLomWw77eTxRVBorgzpX4KUHZ4jwt32bhtMiysRhul1NOZhYJCobK4eI+KS4dcpwq1HnCsRyeM4TcVpEGU5EC04MlwgzYkj/yvHkng8IlNmuCvA2f5TTIspwIlpwolmypAlpVLfKgRoMaacceFtRmlOPsqxL09suJyxqmuVgAo+GPh765RMCZbarCHYVA/WIU48y2/XDIyMi1WDCamBpEyyHdve2A48TtiYbYlPnQm+0i0ONaiX9O3/9VQKVgTVUjn8X/yHQ6AmxqXMxY+rV5iJR9uPlwwQKUBkyx7lyoMt5khMWOIVyRJzl2Du/QmghItmnRVIXGu2tqCwqhye52pUVBA0LnGY5PIzbKgf+NKUCUYlbTem0iC7UGBbcMkHU9KAcFSGlrlRvWCpkuI18duUu4cxMYRaPKyrfPribwAQxbVLlEB8rpS6k1JXqt761QKAGlVp43P698ziBgkhT5UCBobfLwYxrvRypeJwrB+oNQbYcmCYGIhUW0ZgNmVQ5GuGpOzcJnA+0w1COAZVOl2PNsuPp++C8ZYZydJEOlePq+AiBu4Hus+WL3wmcN8I91LvwJnHcw58AAAAASUVORK5CYII=")
const fingerPrint = ref('');

const handleSubmit = async () => {

  await loginApi()

}
const verityEmailAndGeneratePicCode = async () => {

  if(!needOpenPicCode.value){
    await generatePicCode()
    needOpenPicCode.value = true;
  }else{
    if(!picCode.value.code){
      alert("图片验证码不能为空")
      return
    }
    await sendCodeToEmail(currentForm().value.email, picCode.value.pid, picCode.value.code).then(res=>{
      snackbarStore.showSuccessMessage("已发送邮箱验证码，请检查邮箱")
      needOpenPicCode.value = false;
      coolDownTimer.startCoolDown(res.data.email, res.data.coolDown)
    }).catch((error)=>{
      snackbarStore.showErrorMessage("发送失败："+error.message)
      generatePicCode()
    })
  }
}

const generatePicCode = async ()=>{
  picLoading.value = true
  picCode.value.code = ""
  await sendPicCaptcha().then(res=>{
    picCode.value.pid = res.data.pid
    picCode.value.img = "data:image/png;base64, "+res.data.base64
    setTimeout(()=>{
      preImg.value = picCode.value.img
    }, 300)
  }).catch((error)=>{
    snackbarStore.showErrorMessage("网络异常")
    picCode.value.img = errorPicImg
  })
  picLoading.value = false
}
onMounted(()=>
    {
      Fingerprint2.get((components) => { // 参数只有回调函数时，默认浏览器指纹依据所有配置信息进行生成
        const values = components.map(component => component.value); // 配置的值的数组
        const murmur = Fingerprint2.x64hash128(values.join(''), 31); // 生成浏览器指纹
        localStorage.setItem('browserId', murmur); // 存储浏览器指纹，在项目中用于校验用户身份和埋点
        fingerPrint.value = murmur;
      });
      generatePicCode();
    }
)


const guestLogin = async ()=>{
  alert(fingerPrint.value)
  await visitorLoginApi(fingerPrint).then(res=>{

  })
}
const togglePasswordVisibility = () => {
  isPasswordVisible.value = !isPasswordVisible.value
}
</script>

