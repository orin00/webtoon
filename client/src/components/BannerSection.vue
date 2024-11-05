<template>
  <div class="banner-container">
    <a href="/webtoon">
      <img src="assets/homeImage/banner/banner.png" alt="" />
    </a>
  </div>
</template>
<script>
export default {
  components: {},
  data () {
    return {
      sampleData: ''
    }
  },
  created () {},
  mounted () {},
  unmounted () {},
  methods: {}
}
</script>
<style scoped>
.banner-container {
  width: 100%;
  cursor: pointer;
}
.banner-container a {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
.banner-container a img {
  width: 100%;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  object-fit: cover;
}
@media (max-width: 595px) {
  .banner-container a img {
    border: none;
    border-radius: 0;
  }
}
</style>
