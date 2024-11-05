<template>
  <div>
    <h4>휴재 허용</h4>
    <div v-if="paginatedArtworks.length > 0" class="artwork-wrap">
      <div
        v-for="artwork in paginatedArtworks"
        :key="artwork.artworkId"
        class="artwork-list"
      >
        <div class="profile-image">
          <img
            v-if="artwork.profileImage"
            :src="getProfileImageUrl(artwork)"
            alt="ProfileImage"
          />
        </div>
        <div class="artist-info">
          <h4>작가 정보</h4>
          <div class="artist-detail">
            <p><strong>작가 닉네임 :</strong> {{ artwork.nickname }}</p>
            <p><strong>작가 이메일 :</strong> {{ artwork.email }}</p>
          </div>
        </div>
        <div class="artwork-info">
          <h4>작품 정보</h4>
          <div class="artwork-detail">
            <p><strong>작품 제목 :</strong> {{ artwork.title }}</p>
            <p><strong>작품 번호 :</strong> {{ artwork.artworkId }}</p>
            <p><strong>작품 장르 :</strong> {{ artwork.genre }}</p>
            <p>
              <strong>연재일 :</strong> {{ translateDayToKorean(artwork.serialDay) }}요일
            </p>
            <p>
              <strong>검수 요청 상태 :</strong> {{ reviewStatusToKorean(artwork.reviewStatus) }}
            </p>
            <p>
              <strong>연재 상황 :</strong> {{ statusToKorean(artwork.status) }}
            </p>
          </div>
        </div>

        <div class="button-section">
          <button @click="approveBreak(artwork.artworkId)">휴재 승인</button>
          <button @click="denyBreak(artwork.artworkId)">휴재 거부</button>
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
      <p>휴재 신청된 작품이 없습니다.</p>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      artworks: [], // 조회된 작품들을 저장할 배열
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
  mounted () {
    this.fetchBreakReviewArtworks()
  },
  methods: {
    async fetchBreakReviewArtworks () {
      try {
        const response = await fetch('/api/artworks/BreakReview')
        if (response.ok) {
          this.artworks = await response.json()
        } else {
          console.error('데이터 조회 실패', response.status)
        }
      } catch (error) {
        console.error('API 호출 오류', error)
      }
    },
    getProfileImageUrl (artwork) {
      return `/assets/webtoonImage/artwork_id${artwork.artworkId}/profile_image/${artwork.profileImage}`
    },
    async approveBreak (artworkId) {
      console.log(`Approving artwork with ID: ${artworkId}`) // 로그 추가
      try {
        const response = await fetch(
          `/api/artworks/${artworkId}/status-Break`,
          {
            method: 'PUT'
          }
        )
        if (response.ok) {
          const updatedArtwork = await response.json()
          console.log('Artwork updated:', updatedArtwork)
          this.fetchBreakReviewArtworks() // 다시 작품 리스트를 가져옴
        } else {
          console.error('Artwork update failed', response.status)
        }
      } catch (error) {
        console.error('API 호출 오류', error)
      }
    },
    async denyBreak (artworkId) {
      console.log(`Denying break for artwork with ID: ${artworkId}`) // 로그 추가
      try {
        const response = await fetch(
          `/api/artworks/${artworkId}/status-Break-Denied`,
          {
            method: 'PUT'
          }
        )
        if (response.ok) {
          const updatedArtwork = await response.json()
          console.log('Artwork denied:', updatedArtwork)
          this.fetchBreakReviewArtworks() // 다시 작품 리스트를 가져옴
        } else {
          console.error('Artwork denial failed', response.status)
        }
      } catch (error) {
        console.error('API 호출 오류', error)
      }
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
.artist-detail,
.artwork-detail {
  margin: 0 auto;
  width: 480px;
  text-align: left;
}
.profile-image {
  width: 480px;
  margin: 30px auto 0;
  display: flex;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
}
.profile-image img {
  display: inline;
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
