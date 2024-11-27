import type {Profile} from "@/api/user/userType";
import {storage} from "@/util/storage";
import {useAuthStore} from "@/store/authStore";
import {loginApi, logoutApi} from "@/api/user/userApi";
import config from "@/config/OAuthGithub";
import GithubConfig from "@/config/OAuthGithub";
import router from "@/router";
import OAuthGithub from "@/config/OAuthGithub";

// 普通token登录
export async function loginToken(form: Profile): Promise<void> {
    try {
        const response = await loginApi(form);
        const data = response.data

        const { token } = data;
        if(!token) return;
        storage.set('token', token); // 将 token 保存到 localStorage
        useAuthStore().setLoggedIn(data);
    } catch (error) {
        // 获取的数据里没有token
        console.error('登陆失败:', error);
    }
}
// export async function loginGithub() {
//     if (!GithubConfig) {
//         console.error('Github OAuth配置失效');
//         return;
//     }
//     const code = Math.random() * 4;
//
//     const {
//         userAuthorizationUri,
//         clientId,
//         response_type,
//         scope,
//         state,
//         redirect_uri
//     } = GithubConfig;
//
//     // 检查必要的配置是否都已设置
//     if (!userAuthorizationUri || !clientId || !response_type || !scope || !state || !redirect_uri) {
//         console.error('Missing required configuration for login');
//         return;
//     }
//     const queryParams = {
//         client_id: clientId,
//         response_type: response_type,
//         scope: scope,
//         state: state,
//         redirect_uri: redirect_uri
//     };
//     // await loginGithubApi(queryParams)
//     await router.push('/LoginView/'+OAuthGithub.userAuthorizationUri+'?'+queryParams)
// }

// 普通token退出登录
export async function logoutToken() {
    try {
        await logoutApi();
        storage.remove('token'); // 移除token
        useAuthStore().setLoggedOut()
    } catch (error) {
        console.error('退出失败:', error);
    }
}
