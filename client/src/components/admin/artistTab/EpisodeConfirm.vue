<template>
  <div>
    <h4>아티스트의 아트워크 에피소드 연재</h4>
    <div v-if="paginatedEpisodes.length > 0" class="artwork-wrap">
      <div
        v-for="episode in paginatedEpisodes"
        :key="episode.id"
        class="episode-list"
      >
        <div v-if="artwork" class="artwork-info">
          <h4>아트워크 정보</h4>
          <div class="artwork-detail">
            <p><strong>작가 닉네임:</strong> {{ artwork.nickname }}</p>
            <p><strong>작가 이메일:</strong> {{ artwork.email }}</p>
            <p><strong>작품 제목:</strong> {{ artwork.title }}</p>
            <p><strong>장르:</strong> {{ artwork.genre }}</p>
            <p><strong>연재 요일:</strong> {{ translateDayToKorean(artwork.serialDay) }}요일</p>
            <p><strong>작품 상태:</strong> {{ statusToKorean(artwork.status) }}</p>
          </div>
        </div>
        <div v-if="episode" class="episode-info">
          <h4>에피소드 정보</h4>
          <div class="episode-detail">
            <p><strong>에피소드 제목:</strong> {{ episode.episodeTitle }}</p>
            <p><strong>작품 ID:</strong> {{ episode.aId }}</p>
            <p><strong>에피소드 번호:</strong> {{ episode.episodeCount }}</p>
            <p>
              <strong>업로드 날짜:</strong>
              {{ formatCreatedAtDate(episode.createdAt) }}
            </p>
            <p>
              <strong>업데이트 날짜:</strong>
              {{ formatCreatedAtDate(episode.updatedAt) }}
            </p>
          </div>

          <div class="episode-thumbnail">
            <strong>썸네일</strong>
            <img
            :src="getThumbnailUrl(episode)"
            alt="Thumbnail" />
          </div>

          <div class="episode-images">
            <strong>에피소드 이미지</strong>
            <div
              v-for="(url, index) in splitEpisodeUrls(episode.episodeUrl)"
              :key="index"
              class="episode-image"
            >
              <img
                :src="getEpisodeImageUrl(episode, url)"
                :alt="`Episode image ${index + 1}`"
              />
            </div>
            <div class="episode-ppl">
              <strong>PPL 광고:</strong>
              <img
                v-if="episode && episode.pplAdUrl"
                :src="getPplAdUrl(episode)"
                :alt="'PPL 광고 이미지'"
              />
            </div>
          </div>
        </div>

        <div class="button-section">
          <button @click="updateReviewStatus(episode.id, 'Approved')">
            승인하기
          </button>
          <button
            @click="
              deleteEpisode(episode.id, episode.aId, episode.episodeCount)
            "
          >
            거부하기
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
      <p>검수 요청된 에피소드가 없습니다.</p>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      episodes: [],
      artwork: null,
      currentPage: 1
    }
  },
  computed: {
    paginatedEpisodes () {
      const start = (this.currentPage - 1) * 1
      const end = start + 1
      return this.episodes.slice(start, end)
    },
    totalPages () {
      return Math.ceil(this.episodes.length / 1)
    }
  },
  created () {
    this.fetchEpisodesUnderReview()
  },
  methods: {
    async fetchEpisodesUnderReview () {
      try {
        const response = await fetch('/api/episodes/review')
        if (response.ok) {
          this.episodes = await response.json()
          console.log(this.episodes)

          // 첫 번째 에피소드의 아트워크 정보 로드
          if (this.episodes.length > 0) {
            this.fetchArtwork(this.episodes[0].aId)
          }
        } else {
          console.error('Failed to fetch episodes.')
        }
      } catch (error) {
        console.error('Error fetching episodes:', error)
      }
    },
    async updateReviewStatus (episodeId, status) {
      try {
        const response = await fetch(
          `/api/episodes/${episodeId}/review-status`,
          {
            method: 'PUT',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(status)
          }
        )

        if (response.ok) {
          console.log(
            `에피소드 ${episodeId}의 상태가 ${status}로 업데이트되었습니다.`
          )
          // 상태가 업데이트된 후, 에피소드 상태 다시 불러옴
          await this.fetchEpisodesUnderReview()
        } else {
          throw new Error('상태 업데이트 실패')
        }
      } catch (error) {
        console.error('Error updating review status:', error)
        alert(error.message)
      }
    },
    async fetchArtwork (aId) {
      try {
        const response = await fetch(`/api/artworks/${aId}`)
        if (response.ok) {
          this.artwork = await response.json()
          console.log(this.artwork)
        } else {
          console.error('Failed to fetch artwork.')
        }
      } catch (error) {
        console.error('Error fetching artwork:', error)
      }
    },
    async deleteEpisode (episodeId, aId, episodeCount) {
      try {
        const folderResponse = await fetch(
          `http://localhost:5000/api/deleteEpisodeFolder?aId=${aId}&episodeCount=${episodeCount}`,
          {
            method: 'DELETE'
          }
        )
        if (!folderResponse.ok) {
          throw new Error('폴더 삭제 실패')
        }

        const episodeResponse = await fetch(`/api/episodes/${episodeId}`, {
          method: 'DELETE'
        })
        if (!episodeResponse.ok) {
          throw new Error('에피소드 삭제 실패')
        }

        this.episodes = this.episodes.filter(
          (episode) => episode.id !== episodeId
        )

        const emailResponse = await fetch(
          'http://localhost:5000/api/sendAlertEmail',
          {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email: this.artwork.email })
          }
        )
        if (!emailResponse.ok) {
          throw new Error('이메일 전송 실패')
        }

        console.log('폴더 삭제 및 이메일 전송이 성공적으로 완료되었습니다.')
      } catch (error) {
        console.error(error.message)
        alert(error.message)
      }
    },
    getThumbnailUrl (episode) {
      return `/assets/webtoonImage/artwork_id${episode.aId}/episode_count${episode.episodeCount}/thumbnail_url/${episode.thumbnailUrl}`
    },
    getEpisodeImageUrl (episode, url) {
      return `/assets/webtoonImage/artwork_id${episode.aId}/episode_count${episode.episodeCount}/episode_url/${url}`
    },
    getPplAdUrl (episode) {
      if (episode.pplAdUrl) {
        return `/assets/webtoonImage/artwork_id${episode.aId}/episode_count${episode.episodeCount}/ppl_ad_url/${episode.pplAdUrl}`
      }
      return ''
    },
    splitEpisodeUrls (episodeUrl) {
      return episodeUrl.split(', ')
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
.episode-list {
  margin-bottom: 30px;
  display: flex;
  flex-direction: column;
}
.episode-list img {
  width: 100%;
}
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}
.episode-thumbnail,
.episode-images,
.episode-ppl {
  border-top: 1px solid #ccc;
  margin-top: 30px;
  padding-top: 20px;
  display: flex;
  flex-direction: column;
}
.episode-thumbnail img,
.episode-images .episode-image img,
.episode-ppl img {
  width: 680px;
  margin: 30px auto 0;
}
.episode-images {
  margin-bottom: 30px;
}
.artwork-detail,
.episode-detail {
  margin: 0 auto;
  width: 480px;
  text-align: left;
}
.button-section {
  border-top: 1px solid #ccc;
  padding-top: 20px;
  width: 100%;
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
