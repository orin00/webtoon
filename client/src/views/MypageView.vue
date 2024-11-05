<template>
  <div class="mypage">
    <h2>My Page</h2>

    <div v-if="artworks.length">
      <h3>즐겨찾기 리스트</h3>
      <div class="my-list-wrap">
        <div class="my-list-head">
          <span>프로필</span>
          <span>작가</span>
          <span>연재일</span>
          <span>장르</span>
          <span>업데이트일</span>
        </div>
        <ArtworkListItem
          v-for="artwork in artworks"
          :key="artwork.artworkId"
          :artwork="artwork"
        />
      </div>
    </div>
    <div v-else>
      <p>즐겨찾기한 작품이 없습니다.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import ArtworkListItem from '../components/ArtworkListItem.vue'

export default {
  components: {
    ArtworkListItem
  },
  data () {
    return {
      favoriteIds: [], // 즐겨찾기한 작품 ID
      artworks: [] // 표시할 작품 목록
    }
  },
  async mounted () {
    await this.fetchFavorites()
    await this.fetchArtworkDetails()
  },
  methods: {
    // 즐겨찾기한 작품 ID 가져오기
    async fetchFavorites () {
      try {
        const email = sessionStorage.getItem('email')

        if (!email) {
          alert('로그인 정보가 없습니다.')
          return
        }

        const response = await axios.get('/api/favorites/by-email', {
          params: { email }
        })
        const favorites = response.data
        this.favoriteIds = [] // 초기화

        // 즐겨찾기 항목에서 ID를 추출
        for (const favorite of favorites) {
          this.favoriteIds.push(favorite.aId)
        }

        console.log('Favorite a_ids:', this.favoriteIds)
      } catch (error) {
        console.error('Error fetching favorites:', error)
      }
    },
    async fetchArtworkDetails () {
      try {
        if (this.favoriteIds.length === 0) {
          alert('즐겨찾기 한 목록이 없습니다.')
          return
        }

        const url = '/api/favorites/artworks/by-artist-ids' // 수정된 URL
        const params = new URLSearchParams()
        this.favoriteIds.forEach((id) => params.append('artistIds', id)) // 파라미터 이름 수정
        const fullUrl = `${url}?${params.toString()}`

        console.log('Final Request URL:', fullUrl)

        const response = await axios.get(fullUrl)
        this.artworks = response.data
        console.log('즐겨찾기 목록', this.artworks)
      } catch (error) {
        console.error('Error fetching artwork details:', error)
      }
    },
    getProfileImageUrl (artwork) {
      return `/assets/webtoonImage/artwork_id${artwork.artworkId}/profile_image/${artwork.profileImage}`
    },
    saveToSession (artworkId, serialDay) {
      sessionStorage.setItem('artworkId', artworkId)
      sessionStorage.setItem('serialDay', serialDay)
    }
  }
}
</script>

<style scoped>
.mypage {
  padding: 20px 0;
}

.my-list-wrap {
  margin-top: 15px;
}

.my-list-head {
  margin: 0 auto;
  max-width: 1200px;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  background-color: #f0f0f0;
  padding: 15px 20px;
  font-weight: bold;
}

.my-list-head span {
  text-align: center;
}

.my-list-item {
  margin-bottom: 15px;
}

@media (max-width: 1200px) {
  .my-list-head {
    max-width: 991px;
    grid-template-columns: 140px 1fr 80px 90px 1fr;
  }
}
@media (max-width: 992px) {
  .my-list-head {
    max-width: 746px;
  }
}

@media (max-width: 768px) {
  .my-list-head {
    display: none;
  }
}
</style>
