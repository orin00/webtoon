<template>
  <div class="webtoon-list">
    <!-- webtoon profile -->
    <div class="webtoon-profile">
      <div class="profile-outer">
        <img
          v-if="webtoon.profileImage"
          :src="getProfileImageUrl(webtoon)"
          alt="webtoon image"
        />
      </div>
      <div class="profile-outer">
        <div class="profile-content">
          <h1 class="profile-title">{{ webtoon.title }}<span class="card-status" v-if="webtoon.status === 'Break'">휴재중</span></h1>
          <div class="profile-inner">
            <p class="profile-genre">{{ webtoon.genre }}</p>
            <p class="profile-nickname">
              <span>작가</span> {{ webtoon.nickname }}
            </p>
          </div>

          <p class="profile-description">{{ webtoon.description }}</p>
          <div class="profile-tagbox">
            <p class="profile-serialDay" v-if="webtoon.serialDay !== 'Finish'">
              {{ translateDayToKorean(webtoon.serialDay) }}요 웹툰
            </p>
          </div>

          <div class="favorite-btn-container">
            <button @click="addToFavorites" class="favorite-btn">
              즐겨찾기 추가
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="episode-container">
      <div v-for="episode in episodes" :key="episode.id" class="episode-card">
        <router-link
          v-if="episode.reviewStatus === 'Approved'"
          :to="{
            name: 'WebtoonDetailView',
            params: {
              artworkId: artworkId,
              serialDay: serialDay,
              episodeCount: episode.episodeCount,
            },
          }"
          @click="setSessionStorage(episode)"
        >
          <img :src="getThumbnailUrl(episode)" alt="webtoon image" />
          <div class="episode-info">
            <h4 class="episode-title">
              {{ episode.episodeCount }}화 - {{ episode.episodeTitle }}
            </h4>
            <div class="episode-detail">
              <p class="episode-averageRate">
                {{
                  episode.averageRating !== undefined &&
                  episode.averageRating !== null
                    ? episode.averageRating.toFixed(1)
                    : 0
                }}
                / 5.0
              </p>
              <p class="episode-updateAt">
                {{ formatUpdatedAt(episode.updatedAt) }}
              </p>
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      webtoon: {}, // 웹툰 프로필 정보
      episodes: [],
      artworkId: '', // 세션에서 가져올 artworkId
      serialDay: '' // 세션에서 가져올 serialDay
    }
  },
  created () {
    // 세션에서 artworkId와 serialDay를 가져와서 초기화
    this.artworkId = sessionStorage.getItem('artworkId') || ''
    this.serialDay = sessionStorage.getItem('serialDay') || ''
    this.fetchWebtoonProfile(this.artworkId)
    this.fetchEpisodes(this.artworkId)
  },
  methods: {
    async fetchWebtoonProfile (artworkId) {
      try {
        const response = await fetch(`/api/artworks/${artworkId}`)
        this.webtoon = await response.json()
      } catch (error) {
        console.error('Error fetching webtoon profile:', error)
      }
    },
    async fetchEpisodes (artworkId) {
      try {
        const response = await fetch(`/api/episodes/artwork/${artworkId}`)
        this.episodes = await response.json()
        console.log('Fetched episodes:', this.episodes)

        // 평균 별점 가져오기
        await this.fetchEpisodeRatings()
      } catch (error) {
        console.error('Error fetching episodes:', error)
      }
    },

    async fetchEpisodeRatings () {
      try {
        const episodeRatings = await Promise.all(
          this.episodes.map(async (episode) => {
            const response = await fetch(
              `/api/rates/average?artworkId=${this.artworkId}&episodeCount=${episode.episodeCount}`
            )
            const data = await response.json()
            return {
              episodeCount: episode.episodeCount,
              averageRating:
                typeof data.averageRating === 'number' ? data.averageRating : 0 // 기본값을 0으로 설정
            }
          })
        )

        this.episodes = this.episodes.map((episode) => {
          const rating = episodeRatings.find(
            (r) => r.episodeCount === episode.episodeCount
          )
          return {
            ...episode,
            averageRating: rating ? rating.averageRating : 0 // 평균 별점을 0으로 설정
          }
        })
      } catch (error) {
        console.error('Error fetching episode ratings:', error)
      }
    },
    async addToFavorites () {
      // 이메일을 세션 스토리지에서 가져오기
      const email = sessionStorage.getItem('email')
      const artworkId = this.artworkId

      if (!email) {
        alert('로그인 후에 이용해주세요.')
        return
      }

      try {
        await fetch('/api/favorites/save', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: new URLSearchParams({
            email: email,
            artworkId: artworkId
          })
        })
        alert('즐겨찾기에 추가되었습니다!')
      } catch (error) {
        console.error('Error saving favorite:', error)
        alert('즐겨찾기 추가에 실패했습니다.')
      }
    },
    setSessionStorage (episode) {
      sessionStorage.setItem('artworkId', this.artworkId)
      sessionStorage.setItem('serialDay', this.serialDay)
      sessionStorage.setItem('episodeCount', episode.episodeCount)
    },
    getProfileImageUrl (webtoon) {
      return `/assets/webtoonImage/artwork_id${this.artworkId}/profile_image/${webtoon.profileImage}`
    },
    getThumbnailUrl (episode) {
      return `/assets/webtoonImage/artwork_id${this.artworkId}/episode_count${episode.episodeCount}/thumbnail_url/${episode.thumbnailUrl}`
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
    },
    formatUpdatedAt (dateString) {
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}.${month}.${day}`
    }
  }
}
</script>

<style scoped>
.webtoon-profile {
  position: relative;
  max-width: 1200px;
  height: 360px;
  display: grid;
  grid-template-columns: 360px 1fr;
  margin: 20px auto 0;
}
.webtoon-profile .profile-outer {
  width: 100%;
  height: 360px;
}
.webtoon-profile .profile-outer:nth-child(1) img {
  width: 100%;
  aspect-ratio: 1 / 1; /* 1:1 비율로 설정 */
  object-fit: cover; /* 이미지가 카드 영역에 맞게 잘리도록 설정 */
}
.webtoon-profile .profile-outer:nth-child(2) {
  position: relative;
}
.webtoon-profile .profile-outer:nth-child(2) .profile-content {
  padding: 16px 0 20px 52px;
}
.webtoon-profile .profile-outer:nth-child(2) .profile-title {
  font-size: 36px;
  padding: 6px 0;
  margin: 0 0 12px;
  text-align: left;
  font-weight: 600;
  line-height: 1.3;
  color: #000;
  word-break: break-word;
  display: flex;
  align-items: center;
}
.card-status {
  display: inline-flex;
  margin-left: 16px;
  font-size: 15px;
  padding: 8px 16px;
  background-color: #e5e5e9;
  border-radius: 1rem;
}
.webtoon-profile .profile-outer:nth-child(2) .profile-inner {
  position: relative;
  font-size: 16px;
  line-height: 1.2;
  display: flex;
  align-items: center;
}
.webtoon-profile .profile-outer:nth-child(2) .profile-inner .profile-genre {
  position: relative;
  padding-right: 15px;
  color: #1f2329;
}

.webtoon-profile
  .profile-outer:nth-child(2)
  .profile-inner
  .profile-genre::after {
  content: "";
  position: absolute;
  top: 0;
  bottom: 0;
  right: 0;
  width: 1px;
  background-color: #1f2329;
}
.webtoon-profile .profile-outer:nth-child(2) .profile-inner .profile-nickname {
  padding-left: 15px;
  text-align: left;
  color: #1f2329;
}

.webtoon-profile .profile-outer:nth-child(2) .profile-description {
  font-size: 15px;
  font-weight: 500;
  padding: 12px 0 0;
  margin: 0;
  text-align: left;
  line-height: 1.1;
  display: -webkit-box; /* 추가 */
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-word;
  white-space: pre-line;
}
.webtoon-profile .profile-outer:nth-child(2) .profile-tagbox {
  display: flex;
  padding: 8px 5px;
}
.webtoon-profile
  .profile-outer:nth-child(2)
  .profile-tagbox
  .profile-serialDay {
  color: #1f2329;
  padding: 5px 12px;
  background: #dfdfdf;
  display: inline-block;
  border-radius: 4px;
  font-size: 14px;
}
.webtoon-profile .profile-outer:nth-child(2) .profile-content .favorite-btn-container {
  position: relative;
  bottom: 0;
  display: block;
  margin-top: 20px;
}
.webtoon-profile .profile-outer:nth-child(2) .favorite-btn-container .favorite-btn {
  padding: 0 28px;
  height: 53px;
  display: flex;
  flex: 1;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
  font-size: 16px;
  font-weight: 600;
  line-height: 21px;
  color: #fff;
  background: #07f;
  border: none;
  border-radius: 4px;
  outline: none;
  cursor: pointer;
}

.episode-container {
  position: relative;
  max-width: 1200px;
  margin: 40px auto 0;
  display: flex;
  flex-wrap: wrap;
}
.episode-card {
  width: 100%;
}
.episode-card:not(:nth-child(1)) a {
  width: 100%;
  display: grid;
  grid-template-columns: 150px 1fr;
  box-sizing: border-box;
  border-bottom: 1px solid #ccc;
  padding: 10px;
  text-align: center;
}
.episode-card:nth-child(1) a {
  width: 100%;
  display: grid;
  grid-template-columns: 150px 1fr;
  box-sizing: border-box;
  border-top: 1px solid #ccc;
  border-bottom: 1px solid #ccc;
  padding: 10px;
  text-align: center;
}
.episode-card img {
  width: 100%;
  aspect-ratio: 1 / 1; /* 1:1 비율로 설정 */
  object-fit: cover; /* 이미지가 카드 영역에 맞게 잘리도록 설정 */
}
.episode-card a {
  text-decoration: none;
}
.episode-card a .episode-info {
  display: flex;
  flex-direction: column;
  padding: 15px 24px 0;
}
.episode-card a .episode-info .episode-title {
  text-align: left;
  line-height: 1.1;
  font-weight: 600;
  font-size: 20px;
  display: -webkit-box; /* 추가 */
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-word;
  white-space: pre-line;
  margin: 16px 0 0;
  color: #1f2329;
}
.episode-card a .episode-info .episode-title:hover {
  text-decoration: underline;
}
.episode-card a .episode-info .episode-detail {
  text-align: left;
  display: flex;
  align-items: center;
  margin: 8px 0 0;
}
.episode-card a .episode-info .episode-detail .episode-updateAt {
  line-height: 1.1;
  font-weight: 300;
  font-size: 15px;
  color: #999;
  margin-left: 12px;
}
.episode-card a .episode-info .episode-detail .episode-averageRate {
  line-height: 1.1;
  font-weight: 300;
  font-size: 15px;
  color: #999;
}

/* 반응형 스타일 */
@media (max-width: 1280px) {
  .webtoon-profile {
    max-width: 972px;
  }
  .episode-container {
    max-width: 972px;
  }
}

@media (max-width: 992px) {
  .webtoon-profile {
    max-width: 728px;
    grid-template-columns: 280px 1fr;
    height: 280px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-content .favorite-btn {
    height: 40px;
  }
  .webtoon-profile .profile-outer {
    height: 280px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-content {
    padding: 0 0 0 52px;
    margin-top: 12px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-title {
    font-size: 32px;
    line-height: 1.1;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-inner .profile-genre,
  .webtoon-profile
    .profile-outer:nth-child(2)
    .profile-inner
    .profile-nickname {
    margin-block-start: 5px;
    margin-block-end: 5px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-tagbox p {
    margin-block-start: 5px;
    margin-block-end: 5px;
  }
  .episode-container {
    max-width: 728px;
  }
  .episode-card:nth-child(1) a,
  .episode-card:not(:nth-child(1)) a {
    grid-template-columns: 150px 1fr;
  }
}

@media (max-width: 768px) {
  .webtoon-profile {
    display: flex;
    max-width: 400px;
    height: auto;
    flex-direction: column;
  }
  .webtoon-profile .profile-outer:nth-child(1) {
    width: 100%;
    display: inline-flex;
    justify-content: center;
  }
  .webtoon-profile .profile-outer:nth-child(1) img {
    width: 280px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-content {
    padding: 0;
    width: 280px;
    margin: 12px auto 0;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-title {
    display: flex;
    flex-direction: column;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-title .card-status {
    margin-left: 0;
    margin-top: 8px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-content .favorite-btn-container {
    display: inline-flex;
    margin-top: 20px;
  }
  .episode-container {
    max-width: 400px;
    margin: 16px auto 0;
  }
  .episode-card:nth-child(1) a, .episode-card:not(:nth-child(1)) a {
    display: flex;
    flex-direction: column;
  }
  .episode-card a img,
  .episode-card a .episode-info {
    width: 240px;
    margin: 0 auto;
  }
  .episode-card a .episode-info .episode-title {
    font-size: 18px;
    line-height: 1.3;
  }
  .episode-card a .episode-info .episode-detail .episode-averageRate,
  .episode-card a .episode-info .episode-detail .episode-updateAt {
    margin-block-start: 5px;
    margin-block-end: 5px;
    font-size: 15px;
  }
}

/* 여기부터 작업하면 됨 */
@media (max-width: 425px) {
  .webtoon-profile,
  .episode-container {
    max-width: 360px;
  }
}

@media (max-width: 375px) {
  .webtoon-profile .profile-outer {
    height: 240px;
  }
  .webtoon-profile .profile-outer:nth-child(1) img {
    width: 240px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-content {
    width: 240px;
  }
  .webtoon-profile,
  .episode-container {
    max-width: 300px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-title {
    font-size: 26px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-inner {
    font-size: 14px;
  }
  .episode-card a .episode-info .episode-title {
    font-size: 16px;
  }
  .episode-card a img {
    width: 200px;
  }
  .episode-card a .episode-info .episode-title {
    margin: 10px 0 0;
  }
  .episode-card a .episode-info .episode-detail .episode-averageRate,
  .episode-card a .episode-info .episode-detail .episode-updateAt {
    font-size: 12px;
  }
}

@media (max-width: 320px) {
  .webtoon-profile,
  .episode-container {
    max-width: 280px;
  }
  .webtoon-profile .profile-outer {
    width: 260px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-content {
    width: 220px;
  }
  .webtoon-profile .profile-outer:nth-child(2) .profile-title {
    font-size: 18px;
  }
  .episode-card a .episode-info .episode-title {
    font-size: 16px;
  }
  .webtoon-profile .profile-outer:nth-child(2) {
    padding: 8px;
  }
}
</style>
