<template>
  <div>
    <h4>아티스트의 아트워크 신작 등록</h4>
    <div v-if="paginatedArtworks.length > 0" class="artwork-wrap">
      <div
        v-for="artwork in paginatedArtworks"
        :key="artwork.artworkId"
        class="artwork-list"
      >
        <div v-if="artwork" class="artist-info">
          <h4>작가 정보</h4>
          <div class="artist-detail">
            <p><strong>작가 닉네임:</strong> {{ artwork.nickname }}</p>
            <p><strong>작가 이메일:</strong> {{ artwork.email }}</p>
          </div>
        </div>

        <div v-if="artwork" class="artwork-info">
          <h4>신작 작품 정보</h4>
          <div class="profile-image">
            <img :src="getProfileImageUrl(artwork)" alt="ProfileImage" />
          </div>
          <div class="artwork-detail">
            <p><strong>작품 제목:</strong> {{ artwork.title }}</p>
            <p><strong>작품 설명:</strong> {{ artwork.description }}</p>
            <p><strong>장르:</strong> {{ artwork.genre }}</p>
            <p>
              <strong>작품 검수 상태:</strong>
              {{ reviewStatusToKorean(artwork.reviewStatus) }}
            </p>
            <p>
              <strong>작품 상태:</strong> {{ statusToKorean(artwork.status) }}
            </p>
            <p>
              <strong>연재 요일:</strong>
              {{ translateDayToKorean(artwork.serialDay) }}요일
            </p>
            <p>
              <strong>업로드 날짜:</strong>
              {{ formatCreatedAtDate(artwork.createdAt) }}
            </p>
          </div>
        </div>

        <div class="button-section">
          <button @click="approveArtwork(artwork.artworkId)">승인</button>
          <button @click="deleteArtwork(artwork.artworkId, artwork.email)">
            거부
          </button>
        </div>
      </div>
      <div class="pagination-controls">
        <button @click="previousPage" :disabled="currentPage === 1">
          이전
        </button>
        <span> {{ currentPage }} / {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">
          다음
        </button>
      </div>
    </div>
    <div v-else>
      <p>검수 요청된 신작 웹툰이 없습니다.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data () {
    return {
      artworks: [],
      currentPage: 1
    }
  },
  computed: {
    paginatedArtworks () {
      const start = (this.currentPage - 1) * 1
      const end = start + 1
      return this.artworks.slice(start, end)
    },
    totalPages () {
      return Math.ceil(this.artworks.length / 1)
    }
  },
  created () {
    this.fetchArtworks()
  },
  methods: {
    fetchArtworks () {
      axios
        .get('/api/artworks/review-status')
        .then((response) => {
          this.artworks = response.data
          console.log(this.artworks)
        })
        .catch((error) => {
          console.error('Error fetching artworks:', error)
        })
    },
    getProfileImageUrl (artwork) {
      return `/assets/webtoonImage/artwork_id${artwork.artworkId}/profile_image/${artwork.profileImage}`
    },
    approveArtwork (artworkId) {
      axios
        .post(`/api/artworks/approve/${artworkId}`)
        .then((response) => {
          console.log(response.data)
          this.fetchArtworks() // 아트워크 목록 새로고침
        })
        .catch((error) => {
          console.error('Error approving artwork:', error)
        })
    },
    deleteArtwork (artworkId, email) {
      axios
        .delete(`/api/artworks/${artworkId}`)
        .then((response) => {
          console.log('Artwork deleted:', response.data)

          // 작품 폴더 삭제 - nodejs에서 처리
          axios
            .delete(
              `http://localhost:5000/api/deleteFolder?artworkId=${artworkId}`
            )
            .then((response) => {
              console.log('Folder deleted:', response.data)

              // 이메일 전송 - 작가에게 거부 이메일 전송
              axios
                .post('http://localhost:5000/api/sendAlertEmail', { email })
                .then((response) => {
                  console.log('Email sent:', response.data)
                  this.fetchArtworks() // 아트워크 목록 갱신
                })
                .catch((error) => {
                  console.error('Error sending email:', error)
                })
            })
            .catch((error) => {
              console.error('Error deleting folder:', error)
            })
        })
        .catch((error) => {
          console.error('Error deleting artwork:', error)
        })
    },
    previousPage () {
      if (this.currentPage > 1) {
        this.currentPage--
      }
    },
    nextPage () {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
      }
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
    reviewStatusToKorean (reviewStatus) {
      const reviewStatusMap = {
        Review: '검토중',
        Approved: '검토완료'
      }
      return reviewStatusMap[reviewStatus] || reviewStatus
    },
    statusToKorean (status) {
      const statusMap = {
        Normal: '연재중',
        Break: '휴재중',
        Completed: '완결',
        NormalReview: '복귀 검토중',
        BreakReview: '휴재 검토중',
        CompletedReview: '완결 검토중'
      }
      return statusMap[status] || status
    },
    formatCreatedAtDate (dateString) {
      const date = new Date(dateString)

      // 연도, 월, 일, 시간, 분 추출
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')

      // 원하는 형식으로 문자열 반환
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  }
}
</script>

<style scoped>
.artwork-wrap {
  position: relative;
  width: 720px;
  display: inline-flex;
  flex-direction: column;
}
.artwork-list {
  display: flex;
  flex-direction: column;
  margin-bottom: 30px;
}
.artist-detail {
  margin: 0 auto;
  width: 480px;
  text-align: left;
}
.artwork-detail {
  width: 480px;
  margin: 0 auto;
  text-align: left;
}
.profile-image {
  width: 480px;
  margin: 30px auto 0;
}
.profile-image img {
  width: 100%;
  object-fit: cover;
  object-position: center center;
}
.button-section {
  border-top: 1px solid #ccc;
  padding-top: 20px;
  width: 480px;
  display: flex;
  margin: 0 auto;
  justify-content: space-around;
}
.button-section button {
  width: 130px;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  line-height: 23px;
  color: #fff;
  background: #07f;
  border: none;
  border-radius: 4px;
  outline: none;
  cursor: pointer;
}
.pagination-controls span {
  margin: 0 10px;
}

@media (max-width: 992px) {
  .artwork-wrap {
    width: 680px;
  }
}
</style>
