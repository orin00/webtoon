<template>
  <NavBar />
  <router-view />
  <FooterBar />
</template>

<script>
import NavBar from './components/NavBar.vue'
import FooterBar from './components/FooterBar.vue'

export default {
  components: {
    NavBar,
    FooterBar
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #000;
}

* {
  scroll-behavior: smooth;
}

html,
body {
  display: inline;
  margin: 0 auto;
  box-sizing: border-box;
}
ul,
li {
  list-style: none;
  padding: 0;
}
</style>
