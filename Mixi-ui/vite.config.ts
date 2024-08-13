/*
 * @Author: Dhx
 * @Date: 2024-07-05 15:32:16
 * @Description: 
 * @FilePath: \Mixi\Mixi-ui\vite.config.ts
 */
import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import VueRouter from 'unplugin-vue-router/vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    VueRouter({
      routesFolder: ['src/views'],
    }),
    vue(),
    vueJsx(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/mixiApi/gateway/': {
        target: 'http://116.205.236.94:8080/',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/mixiApi\/gateway/,"/")
      },
    }
  }
})
