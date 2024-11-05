<template>
  <div class="complete">
    <h2>완결웹툰</h2>

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
          @click="saveToSession(webtoon.artworkId, webtoon.serialDay, webtoon.genre)"
        >
          <img
            v-if="webtoon.profileImage"
            :src="getProfileImageUrl(webtoon)"
            alt="webtoon image"
            class="card-image"
          />
          <h4 class="card-title">{{ webtoon.title }}</h4>
          <p class="card-nickname">{{ webtoon.nickname }}</p>
          <p class="card-info">{{ webtoon.genre }}</p>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  components: { },
  data () {
    return {
      webtoons: [], // 모든 웹툰 데이터를 저장
      filteredWebtoons: [], // 필터된 웹툰 데이터를 저장
      genres: [], // 장르 목록
      selectedGenre: 'All' // 선택된 장르
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
    this.fetchWebtoons()
  },
  methods: {
    async fetchWebtoons () {
      try {
        const response = await fetch('/api/artworks/completed')
        if (!response.ok) {
          throw new Error('Network response was not ok')
        }
        const data = await response.json()
        console.log('Completed Webtoons Data:', data) // 데이터 확인
        this.webtoons = data
        this.filterByGenre(this.selectedGenre) // 장르 필터링 초기화
      } catch (error) {
        console.error('Error fetching completed webtoons:', error)
      }
    },
    filterByGenre (genre) {
      this.selectedGenre = genre
      const filtered =
        genre === 'All'
          ? this.webtoons
          : this.webtoons.filter((webtoon) => webtoon.genre === genre)

      // 중복 제거
      const uniqueWebtoons = new Map()
      filtered.forEach((webtoon) => {
        if (!uniqueWebtoons.has(webtoon.artworkId)) {
          uniqueWebtoons.set(webtoon.artworkId, webtoon)
        }
      })

      this.filteredWebtoons = Array.from(uniqueWebtoons.values())
    },
    getProfileImageUrl (webtoon) {
      return `/assets/webtoonImage/artwork_id${webtoon.artworkId}/profile_image/${webtoon.profileImage}`
    },
    saveToSession (artworkId, serialDay, genre) {
      sessionStorage.setItem('artworkId', artworkId)
      sessionStorage.setItem('serialDay', serialDay)
      sessionStorage.setItem('genre', genre) // genre도 저장
    }
  }
}
</script>

<style scoped>
.complete {
  padding: 20px;
}

.complete h2 {
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

.webtoon-nav.genre-filter {
  position: relative;
  max-width: 1280px;
  margin: 0 auto;
}

.webtoon-nav.genre-filter ul {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(11, auto);
  column-gap: 8px;
  margin-top: 30px;
  margin-bottom: 0;
}

.webtoon-nav.genre-filter ul li {
  width: 100%;
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

.card-title {
  margin-top: 10px;
  font-size: 18px;
  font-weight: bold;
}

.card-nickname {
  padding: 0 10px;
  font-size: 14px;
  line-height: 1.4;
  margin-top: 8px;
}

.card-info {
  font-size: 12px;
  margin-top: 6px;
}
@media (max-width: 1200px) {
  .webtoon-nav,
  .webtoon-container {
    max-width: 991px;
  }
}

@media (max-width: 992px) {
  .webtoon-nav.genre-filter,
  .webtoon-container {
    max-width: 768px;
  }
  .webtoon-container {
    grid-template-columns: repeat(3, 1fr);
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
  .webtoon-container {
    max-width: 595px;
  }
  .webtoon-container .webtoon-card h4 {
    font-size: 15px;
  }
  .webtoon-container .webtoon-card p {
    font-size: 11px;
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
  .webtoon-container {
    max-width: 480px;
  }
  .webtoon-container {
    grid-template-columns: repeat(2, 1fr);
  }
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
