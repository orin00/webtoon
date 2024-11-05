<template>
  <div class="webtoon">
    <h2>요일웹툰</h2>

    <SliderSection :images="webtoonImages" />

    <div class="webtoon-nav day-filter">
      <ul>
        <li>
          <button class="filter-button" @click="filterByDay('All')">요일 전체</button>
        </li>
        <li v-for="(day, index) in koreanDays" :key="day">
          <button class="filter-button" @click="filterByDay(days[index])">{{ day }}</button>
        </li>
      </ul>
    </div>

    <div class="webtoon-nav genre-filter">
      <ul>
        <li>
          <button class="filter-button" @click="filterByGenre('All')">전체 장르</button>
        </li>
        <li v-for="genre in genres" :key="genre">
          <button class="filter-button" @click="filterByGenre(genre)">{{ genre }}</button>
        </li>
      </ul>
    </div>

    <div class="webtoon-container">
      <div
        v-for="webtoon in filteredWebtoons"
        :key="webtoon.artworkId"
        class="webtoon-card"
      >
        <router-link
          :to="{
            name: 'WebtoonListView',
            params: {
              artworkId: webtoon.artworkId,
              serialDay: webtoon.serialDay,
            },
          }"
          @click="saveToSession(webtoon.artworkId, webtoon.serialDay)"
        >
          <img
            v-if="webtoon.profileImage"
            :src="getProfileImageUrl(webtoon)"
            alt="webtoon image"
            class="card-image"
          />

          <div class="card-info">
            <h4 class="card-title">{{ webtoon.title }}<span class="card-status" v-if="webtoon.status === 'Break'">휴재중</span></h4>
            <p class="card-nickname">{{ webtoon.nickname }}</p>
            <p class="card-serialDay">{{ translateDayToKorean(webtoon.serialDay) }}</p>
            <p class="card-genre">{{ webtoon.genre }}</p>
          </div>

        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import SliderSection from '@/components/SliderSection.vue'

export default {
  components: { SliderSection },
  data () {
    return {
      webtoons: [],
      days: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
      genres: [],
      selectedDay: 'All',
      selectedGenre: 'All',
      webtoonImages: []
    }
  },
  computed: {
    koreanDays () {
      const dayMap = {
        Sun: '일',
        Mon: '월',
        Tue: '화',
        Wed: '수',
        Thu: '목',
        Fri: '금',
        Sat: '토'
      }
      return this.days.map(day => dayMap[day])
    },
    filteredWebtoons () {
      let filtered = this.webtoons

      if (this.selectedDay !== 'All') {
        filtered = filtered.filter(webtoon => webtoon.serialDay === this.selectedDay)
      }

      if (this.selectedGenre !== 'All') {
        filtered = filtered.filter(webtoon => webtoon.genre === this.selectedGenre)
      }

      const uniqueWebtoons = new Map()
      filtered.forEach(webtoon => {
        if (!uniqueWebtoons.has(webtoon.artworkId)) {
          uniqueWebtoons.set(webtoon.artworkId, webtoon)
        }
      })

      return Array.from(uniqueWebtoons.values())
    }
  },
  async created () {
    try {
      const genreResponse = await fetch('/assets/artworkGenres.json')
      const genreData = await genreResponse.json()
      this.genres = genreData
    } catch (error) {
      console.error('Error fetching genres:', error)
    }

    try {
      const imageResponse = await fetch('/assets/webtoonImages.json')
      const imageData = await imageResponse.json()
      this.webtoonImages = imageData
    } catch (error) {
      console.error('Error fetching home images:', error)
    }

    this.fetchWebtoons()
  },
  methods: {
    async fetchWebtoons () {
      try {
        const response = await fetch('/api/artworks/status')
        const data = await response.json()
        console.log('Normal Webtoons Data:', data)
        this.webtoons = data
      } catch (error) {
        console.error('Error fetching webtoons:', error)
      }
    },
    filterByDay (day) {
      this.selectedDay = day
      this.selectedGenre = 'All'
    },
    filterByGenre (genre) {
      this.selectedGenre = genre
      this.selectedDay = 'All'
    },
    getProfileImageUrl (webtoon) {
      return `/assets/webtoonImage/artwork_id${webtoon.artworkId}/profile_image/${webtoon.profileImage}`
    },
    saveToSession (artworkId, serialDay) {
      sessionStorage.setItem('artworkId', artworkId)
      sessionStorage.setItem('serialDay', serialDay)
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
.webtoon {
  padding: 20px;
}

.webtoon h2 {
  margin-bottom: 20px;
  font-size: 24px;
  text-align: center;
}

.webtoon-nav ul {
  list-style: none;
  padding: 0;
  margin-bottom: 20px;
}

.webtoon-nav li {
  display: inline;
}

.webtoon-nav.day-filter,
.webtoon-nav.genre-filter {
  position: relative;
  max-width: 1280px;
  margin: 0 auto;
}
.webtoon-nav.day-filter ul {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(8, auto);
  margin-top: 30px;
  margin-bottom: 0;
}
.webtoon-nav.genre-filter ul {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(11, auto);
  column-gap: 8px;
  margin-top: 30px;
  margin-bottom: 0;
}

.webtoon-nav.day-filter ul li,
.webtoon-nav.genre-filter ul li {
  width: 100%;
}

.webtoon-nav.day-filter ul li .filter-button {
  width: 100%;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-right: 1px solid #4599f3;
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.webtoon-nav.day-filter ul li .filter-button:hover {
  background-color: #0056b3;
}

.webtoon-nav.genre-filter ul li .filter-button {
  width: 100%;
  background-color: #e5e5e9;
  color: #4a4e57;
  border: none;
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  border-radius: 4px;
}

.webtoon-container {
  max-width: 1280px;
  margin: 40px auto 0;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.webtoon-card {
  box-sizing: border-box;
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
  transition: transform 0.3s ease;
  width: 100%;
  height: 100%;
}
.webtoon-card a {
  text-decoration: none;
  color: #000;
}

.card-image {
  width: 100%;
  aspect-ratio: 1/1;
  object-fit: cover;
  border-radius: 4px;
}

.card-info {
  display: inline-flex;
  flex-direction: column;
  text-align: center;
}

.card-info > p {
  margin: 6px 0;
}

.card-title {
  margin: 10px 0;
  font-size: 18px;
  font-weight: bold;
  display: inline-flex;
  align-items: center;
}

.card-nickname {
  font-size: 15px;
}
.card-serialDay {
  font-size: 15px;
}

.card-status {
  display: inline-flex;
  margin-left: 16px;
  font-size: 15px;
  padding: 8px 16px;
  background-color: #e5e5e9;
  border-radius: 1rem;
}
@media (max-width: 1200px) {
  .webtoon-nav.day-filter,
  .webtoon-nav.genre-filter,
  .webtoon-container {
    max-width: 991px;
  }

  .card-title {
    display: inline-flex;
    flex-direction: column;
  }
  .card-status {
    margin-top: 8px;
    margin-left: 0;
  }
}

@media (max-width: 992px) {
  .webtoon-nav.day-filter,
  .webtoon-nav.genre-filter,
  .webtoon-container {
    max-width: 768px;
  }
  .webtoon-container {
    grid-template-columns: repeat(3, 1fr);
  }
  .webtoon-nav.day-filter ul {
    margin-top: 16px;
    display: grid;
    grid-template-columns: calc((100%/6) - 8px) repeat(7, minmax(0, 1fr));
  }
  .webtoon-nav.genre-filter ul {
    margin-top: 16px;
    grid-template-columns: repeat(6, minmax(0, 1fr)); /* minmax 함수에 0과 1fr을 사용하여 균등하게 분배 */
    grid-template-rows: repeat(2, auto);
    row-gap: 13px;
  }
}

@media (max-width: 768px) {
  .webtoon-nav.genre-filter,
  .webtoon-nav.day-filter,
  .webtoon-container {
    max-width: 595px;
  }
  .card-title {
    font-size: 15px;
  }
  .card-info > p {
    font-size: 12px;
  }

  .card-status {
    font-size: 12px;
  }

  .webtoon-nav.day-filter ul {
    margin-top: 16px;
    display: grid;
    grid-template-columns: calc((100%/6) - 8px) repeat(7, minmax(0, 1fr));
  }
  .webtoon-nav.genre-filter ul {
    margin-top: 16px;
    grid-template-columns: repeat(6, minmax(0, 1fr)); /* minmax 함수에 0과 1fr을 사용하여 균등하게 분배 */
    grid-template-rows: repeat(2, auto);
    row-gap: 13px;
  }
}

@media (max-width: 595px) {
  .webtoon-nav.genre-filter,
  .webtoon-nav.day-filter,
  .webtoon-container {
    max-width: 480px;
  }
  .webtoon-container {
    grid-template-columns: repeat(2, 1fr);
  }
  .webtoon-nav.day-filter ul {
    grid-template-columns: 52px repeat(7, minmax(0, 1fr));
  }
  .webtoon-nav.day-filter ul li .filter-button {
    height: 52px;
  }
  .webtoon-nav.day-filter ul li .filter-button,
  .webtoon-nav.genre-filter ul li .filter-button {
    padding: 8px 5px;
  }
  .webtoon-nav.genre-filter ul li .filter-button {
    font-size: 11px;
  }
  .webtoon-nav.genre-filter ul {
    margin-top: 16px;
    grid-template-columns: repeat(4, minmax(0, 1fr)); /* minmax 함수에 0과 1fr을 사용하여 균등하게 분배 */
    grid-template-rows: repeat(3, auto);
    row-gap: 13px;
  }
  .card-title {
    margin: 8px 0 10px;
  }
  .webtoon-container p {
    margin: 8px 0;
  }
}

@media (max-width: 480px) {
  .webtoon-card {
    width: 100%;
  }
}

</style>
