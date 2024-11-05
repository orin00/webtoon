<template>
  <div class="home">
    <SliderSection :images="homeImages" />

    <div class="new-webtoon">
      <h2 class="new-title">신작 웹툰 - 새롭게 연재를 시작한 웹툰들을 소개합니다!</h2>
      <NewSection />
    </div>

    <div class="banner-ad">
      <BannerSection></BannerSection>
    </div>
  </div>
</template>

<script>
import SliderSection from '@/components/SliderSection.vue'
import NewSection from '@/components/NewSection.vue'
import BannerSection from '@/components/BannerSection.vue'

export default {
  name: 'HomeView',
  components: {
    SliderSection,
    NewSection,
    BannerSection
  },
  data () {
    return {
      homeImages: [] // 빈 배열로 초기화
    }
  },
  async created () {
    try {
      const response = await fetch('/assets/homeImages.json')
      const data = await response.json()
      this.homeImages = data
    } catch (error) {
      console.error('Error fetching home images:', error)
    }
  }
}
</script>

<style scoped>
.home {
  position: relative;
  width: 1200px;
  display: inline-block;
}
.home .new-webtoon {
  position: relative;
  margin: 50px auto 0;
  width: 100%;
}
.home .new-webtoon .new-title {
  text-align: left;
  font-size: 20px;
  line-height: 21px;
  font-weight: 600;
}
.home .banner-ad {
  position: relative;
  margin: 50px auto 0;
  width: 100%;
}

@media (max-width: 1200px) {
  .home {
    width: 991px;
  }
}
@media (max-width: 992px) {
  .home {
    width: 768px;
  }
}
@media (max-width: 768px) {
  .home {
    width: 595px;
  }
}
@media (max-width: 595px) {
  .home {
    width: 100%;
  }
  .home .new-webtoon,
  .home .banner-ad {
    margin: 20px auto 0;
  }
  .home .new-webtoon .new-title{
    display: none;
  }
}
</style>
