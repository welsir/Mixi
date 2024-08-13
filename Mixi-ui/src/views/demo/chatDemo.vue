<!--
 * @Author: Dhx
 * @Date: 2024-07-08 09:17:23
 * @Description:
 * @FilePath: \Mixi\Mixi-ui\src\views\demo\chatDemo.vue
-->
<template>
    <div>
        123
    </div>
</template>
<script lang="ts" setup>
import MixiWebSocket from '@/util/webSocket'
let webSocket = new MixiWebSocket("ws://localhost:8001/websocket-server")
webSocket.init()
webSocket.onmessage((event)=>{
    console.log(event.data)
})
webSocket.onopen(()=>{

})
</script>
