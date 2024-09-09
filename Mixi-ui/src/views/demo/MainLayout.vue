<template>
  <div id="main">
    <div class="home">
      <el-container height="100%">
        <el-aside width="100px">
          <Nav></Nav>
        </el-aside>
        <el-main>
          <Index :uid="123"
                 :roomId="123"
          >
          </Index>
        </el-main>
      </el-container>
    </div>
  </div>
</template>

<script>
import {defineComponent} from "vue";
import Nav from "@/components/room/Nav.vue";
import Index from "@/views/index.vue";

export default defineComponent({
  components: {Index, Nav}


})
</script>

<style lang="scss" scoped>
@import url(../../assets/css/iconfont.css);
.iconfont {
  font-family: "iconfont" !important;
  font-style: normal;
  font-size: 25px;
  vertical-align: middle;
  color: rgb(117,120,137);
  transition: .3s;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
* {
  padding: 0;
  margin: 0;
}
#main {
  width: 100vw;
  height: 100vh;
  background-color: rgb(151,157,167);
}
.home {
  width: 90vw;
  height: 90vh;
  background-color: rgb(39, 42, 55);
  border-radius: 15px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}
</style>
