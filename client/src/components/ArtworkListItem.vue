<template>
  <div class="artwork-list-item">
    <router-link
      :to="{
        name: 'WebtoonListView',
        params: { artworkId: artwork.artworkId, serialDay: artwork.serialDay },
      }"
      @click="saveToSession(artwork.artworkId, artwork.serialDay)"
      style="text-decoration: none;"
    >
      <div class="artwork-outer">
        <img
          v-if="artwork.profileImage"
          :src="getProfileImageUrl(artwork)"
          alt="artwork image"
          class="artwork-image"
        />
        <div class="artwork-info">
          <h4>{{ artwork.title }}</h4>
          <p>작가 : {{ artwork.nickname }}</p>
        </div>
        <div class="artwork-serialDay">
          <p v-if="artwork.serialDay === 'Finish'">완결</p>
          <p v-else>{{ translateDayToKorean(artwork.serialDay) }}</p>
        </div>
        <div class="artwork-genre">
          <p>{{ artwork.genre }}</p>
        </div>
        <div class="artwork-updatedAt">
          <p>{{ formatDate(artwork.updatedAt) }}</p>
        </div>
      </div>
    </router-link>
  </div>
</template>

<script>
export default {
  props: {
    artwork: {
      type: Object,
      required: true
    }
  },
  methods: {
    getProfileImageUrl (artwork) {
      return `/assets/webtoonImage/artwork_id${artwork.artworkId}/profile_image/${artwork.profileImage}`
    },
    saveToSession (artworkId, serialDay) {
      sessionStorage.setItem('artworkId', artworkId)
      sessionStorage.setItem('serialDay', serialDay)
    },
    formatDate (dateString) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' }
      return new Date(dateString).toLocaleDateString('ko-KR', options)
    },
    translateDayToKorean (day) {
      const dayMap = {
        Sun: '일',
        Mon: '월',
        Tue: '화',
        Wed: '수',
        Thu: '목',
        Fri: '금',
        Sat: '토'
      }
      return dayMap[day] || day
    }
  }
}
</script>

<style scoped>
.artwork-list-item {
  margin: 0 auto;
  padding: 15px 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  max-width: 1200px;
}

.artwork-outer {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  align-items: center;
}

.artwork-image {
  width: 160px;
  height: 160px;
  object-fit: cover;
  margin: 0 auto;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.artwork-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  flex: 1;
}

.artwork-info h4 {
  margin-block-start: 14px;
  margin-block-end: 14px;
  font-size: 24px;
  margin-bottom: 8px;
  color: #000;
}

.artwork-info p,
.artwork-serialDay p,
.artwork-genre p,
.artwork-updatedAt p {
  margin: 4px 0;
  color: #000;
}

.artwork-details p strong {
  font-weight: 600;
  color: #000;
}

@media (max-width: 1200px) {
  .artwork-list-item {
    max-width: 991px;
  }
  .artwork-outer {
    grid-template-columns: 140px 1fr 80px 90px 1fr;
  }
  .artwork-info h4 {
    margin-block-start: 10px;
    margin-block-end: 10px;
    font-size: 20px;
  }
}

@media (max-width: 992px) {
  .artwork-list-item {
    max-width: 746px;
  }
  .artwork-image {
    width: 130px;
    height: 130px;
  }
}

@media (max-width: 768px) {
  .artwork-list-item {
    padding: 40px 0;
    background-color: #eee;
    max-width: 480px;
  }
  .artwork-list-item:not(:nth-last-child(1)) {
    margin-bottom: 15px;
  }
  .artwork-outer {
    display: flex;
    flex-direction: column;
  }
  .artwork-image {
    width: 400px;
    height: 400px;
  }
}

@media (max-width: 576px) {
  .artwork-list-item {
    max-width: 380px;
  }
  .artwork-image {
    width: 320px;
    height: 320px;
  }
}

@media (max-width: 425px) {
  .artwork-list-item {
    max-width: 330px;
  }
  .artwork-image {
    width: 280px;
    height: 280px;
  }
}

@media (max-width: 375px) {
  .artwork-list-item {
    max-width: 280px;
  }
  .artwork-image {
    width: 220px;
    height: 220px;
  }
}

@media (max-width: 320px) {
  .artwork-list-item {
    max-width: 240px;
  }
  .artwork-image {
    width: 180px;
    height: 180px;
  }
}
</style>
