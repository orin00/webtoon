<template>
  <div class="slider-container">
    <div class="slider-wrapper">
      <a
        class="slide-idx"
        v-for="(image) in images"
        :key="image.id"
        :href="baseUrl + image.href"
      >
        <img :src="getImageUrl(image.imageUrl)" />
      </a>
    </div>
    <button class="prev-button" @click="prevSlide">
      <font-awesome-icon icon="chevron-left" />
    </button>
    <button class="next-button" @click="nextSlide">
      <font-awesome-icon icon="chevron-right" />
    </button>
    <div class="page-info">{{ currentIndex + 1 }} / {{ images.length }}</div>
  </div>
</template>

<script>
export default {
  props: {
    images: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      currentIndex: 0,
      baseUrl: 'http://localhost:3000/'
    }
  },
  methods: {
    getImageUrl (imagePath) {
      return `/assets${imagePath}`
    },
    prevSlide () {
      this.currentIndex =
        (this.currentIndex - 1 + this.images.length) % this.images.length
      this.updateSlidePosition()
    },
    nextSlide () {
      this.currentIndex =
        (this.currentIndex + 1) % this.images.length
      this.updateSlidePosition()
    },
    updateSlidePosition () {
      const sliderWrapper = this.$el.querySelector('.slider-wrapper')
      sliderWrapper.style.transform = `translateX(-${this.currentIndex * 100}%)`
    },
    autoSlide () {
      setInterval(() => {
        this.nextSlide()
      }, 6000)
    }
  },
  mounted () {
    this.updateSlidePosition()
    this.autoSlide()
  }
}
</script>

<style scoped>
.slider-container {
  position: relative;
  width: 1200px;
  height: 720px;
  margin: 0 auto;
  overflow: hidden;
}

.slider-wrapper {
  display: flex;
  transition: transform 0.5s ease-in-out;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.slide-idx {
  min-width: 100%;
  height: 100%;
}

.slide-idx img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.prev-button,
.next-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 50%;
  border: none;
  width: 58px;
  height: 58px;
  padding: 10px;
  cursor: pointer;
}

.prev-button svg,
.next-button svg {
  width: 25px;
  height: 25px;
}

.prev-button {
  left: 10px;
}

.next-button {
  right: 10px;
}

.page-info {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.5);
  color: white;
  padding: 5px 10px;
  border-radius: 5px;
}

/* 반응형 스타일 */
@media (max-width: 1200px) {
  .slider-container {
    width: 991px;
    height: 559px;
  }
}

@media (max-width: 992px) {
  .slider-container {
    width: 768px;
    height: 432px;
  }

  .prev-button,
  .next-button {
    width: 50px;
    height: 50px;
    padding: 8px;
  }

  .prev-button svg,
  .next-button svg {
    width: 20px;
    height: 20px;
  }
}

@media (max-width: 768px) {
  .slider-container {
    width: 595px;
    height: 324px;
  }

  .prev-button,
  .next-button {
    width: 45px;
    height: 45px;
    padding: 7px;
  }

  .prev-button svg,
  .next-button svg {
    width: 18px;
    height: 18px;
  }
}

@media (max-width: 595px) {
  .slider-container {
    width: 100%;
    height: 324px;
  }

  .prev-button,
  .next-button {
    width: 40px;
    height: 40px;
    padding: 6px;
  }

  .prev-button svg,
  .next-button svg {
    width: 16px;
    height: 16px;
  }

  .slide-idx img {
    object-position: center center;
  }
}

/* mobile-reponsive */
@media (max-width: 425px) {
  .slider-container {
    height: 239px;
  }
}
@media (max-width: 375px) {
  .slider-container {
    height: 211px;
  }
}
@media (max-width: 320px) {
  .slider-container {
    height: 180px;
  }
}
</style>
