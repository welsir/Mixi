import axios from "axios";
import { useSnackbarStore } from "@/stores/snackbarStore";
import { statusTextMap,errorStatusCodes,ignoreStatusCodes } from '@/util/statusCodes'
import { storage } from "./storage";
const request = axios.create({
  baseURL: "http://116.205.236.94:8080",
  // baseURL: "/api",
  timeout: 10000,
});

const retries = 2; // 设置重试次数为3次
const retryDelay = 1000; // 设置重试的间隔时间
// request 拦截器
request.interceptors.request.use(
  (config:any) => {
    config.headers['Content-Type'] = 'application/json';
    const Authorization = storage.get<string>('Authorization')
    config.headers.Authorization = 'Bearer ' + Authorization
    config.headers["X-Request-Prefix"] = config.url
    return config;
  },
  (error:any) => {
    return Promise.reject(error);
  }
);

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
  (response:any) => {
    const snackbarStore = useSnackbarStore();
    if (response.status != 200) {
      snackbarStore.showErrorMessage("请求拦截器异常!");
    }
    let res = response.data;
    if (!ignoreStatusCodes[res.code]) {
      snackbarStore.showErrorMessage(res.msg);
    }
    // 如果是返回的文件
    if (response.config.responseType === "blob") {
      return res;
    }
    // 兼容服务端返回的字符串数据
    if (typeof res === "string") {
      res = res ? JSON.parse(res) : res;
    }
    return res;
  },
  (error) => {
    // 失败重连
    if(error.response && errorStatusCodes.includes(error.response.status)){
      const config = error.config
      // 如果config不存在或未设置重试选项，则拒绝
      if (!config || !retries) {
        return Promise.reject(error)
      }
      // 设置变量来跟踪重试次数
      config.__retryCount = config.__retryCount || 0
      // 检查是否达到最大重试次数
      if (config.__retryCount >= retries) {
        return Promise.reject(error)
      }
      // 增加重试计数器
      config.__retryCount += 1
      // 创建一个新的Promise来处理每次重试之前等待一段时间
      const backoff = new Promise((resolve) => {
        setTimeout(() => {
          resolve('重新请求：' + config.__retryCount)
        }, retryDelay || 1)
      })
      // 返回Promise，以便Axios知道我们已经处理了错误
      return backoff.then((txt) => {
        return request(config)
      })
    }

    const snackbarStore = useSnackbarStore();
    if (error.response && error.response.status) {
      const statusText = statusTextMap[error.response.status] ?? '其他错误'
      snackbarStore.showErrorMessage(`${statusText}(${error.response.status})`)
      if (error.response.status === 401) {
        // storage.remove('token')
        // router.replace({path:'/login'})
      }
      return Promise.reject(error)
    }
    return Promise.reject(error);
  },
);

export default request;
