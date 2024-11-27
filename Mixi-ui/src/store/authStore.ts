import { defineStore } from "pinia";

import router from "../router";
import {storage} from "@/util/storage";
interface Profile {
  username: string;
  email: string;
  password: string;
  avatar: string;
  nickname: string;
  sex: string;
  resume: string;
}

export const useAuthStore = defineStore("auth",{
  state: () => ({
    isLoggedIn: false, // 登录状态
    profile: null as Profile | null,
    user: null,
  }),

  getters: {
    getLoginState(state){
      this.isLoggedIn = storage.get('Authorization')!=null
      return state.isLoggedIn
    },
    getProfile(state){
      return state.profile
    }
  },

  actions: {
    // 登录
    setLoggedIn(profile:Profile) {
      this.isLoggedIn = true
      storage.set('isLoggedIn',this.isLoggedIn)
      this.updateProfile(profile)
    },
    // 登出
    setLoggedOut() {
      this.isLoggedIn = false
      storage.remove('profile')
    },
    // 更新用户信息
    updateProfile(profile:Profile) {
      this.profile = profile
      storage.set('profile',profile)
    },
  },
});
