/*
 * @Author: LisianthusLeaf
 * @Date: 2024-07-11 15:32:16
 * @Description: 自动化路由
 * @FilePath: \Mixi\Mixi-ui\src\router\index.ts
 */
import { createRouter, createWebHistory } from 'vue-router'
import { routes, handleHotUpdate } from 'vue-router/auto-routes'
import HomeView from "@/views/HomeView.vue";
import CreatRoom from "@/views/CreatRoom.vue";

const customRoutes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView
  },{
    path: '/CreatRoom',
    name: 'createRoom',
    Component: CreatRoom
  }
];

const allRoutes = [...customRoutes, ...routes];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: allRoutes,
});
if (import.meta.hot)  {
  handleHotUpdate(router)
}
export default router
