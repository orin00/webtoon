<template>
  <div class="detail-view">
    <div class="detail-container">
      <div v-if="episode" class="detail-wrap">
        <div class="detail-top">
          <div class="detail-outer">
            <div class="detail-thumbnail">
              <img :src="getThumbnailUrl(episode)" alt="webtoon image" />
            </div>
            <div class="detail-info">
              <div class="detail-title">
                <p>{{ episode.episodeCount }} 화</p>
                <h3>{{ episode.episodeTitle }}</h3>
              </div>
              <p>
                {{
                  averageRating !== null ? averageRating.toFixed(1) : "0.0"
                }}&nbsp;/&nbsp;5.0
              </p>
              <p>{{ formatUpdatedAt(episode.updatedAt) }}</p>
            </div>
          </div>
        </div>

        <div class="episode-images">
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
            <img
              v-if="episode && episode.pplAdUrl"
              :src="getPplAdUrl(episode)"
              :alt="'PplAdUrl image'"
            />
          </div>
        </div>
      </div>
      <div v-else class="detail-notFound">
        <p>에피소드 정보를 불러오지 못했습니다.</p>
      </div>
    </div>
    <div class="rating-section">
      <div class="rating-section-wrap">
        <div class="average-rating">
          <div class="average-rating-wrap">
            <p>
              {{
                averageRating !== null ? averageRating.toFixed(1) : "0.0"
              }}&nbsp;/&nbsp;5.0
            </p>
            <p>{{ ratingCount }} 명</p>
          </div>
        </div>
        <!-- 별점 매기기 UI -->
        <div class="rating-container">
          <div class="rating-wrap wrap-1" v-if="isLoggedIn && !isRated">
            <p>별점을 선택하세요</p>
            <div class="stars">
              <span v-for="star in 5" :key="star" @click="selectRating(star)">
                <i :class="['star-icon', { active: star <= selectedRating }]">
                  <span class="material-symbols-outlined">kid_star</span>
                </i>
              </span>
            </div>

            <button @click="submitRating" :disabled="selectedRating === 0">
              별점 등록
            </button>
          </div>

          <div class="rating-wrap wrap-2" v-else-if="!isLoggedIn">
            <p>로그인 후 별점을 등록할 수 있습니다</p>
          </div>

          <div class="rating-wrap wrap-3" v-else>
            <p>이미 별점이 등록되었습니다</p>
            <div class="user-rating">
              <p>등록한 별점</p>
              <p>
                <span
                  v-for="star in userRating"
                  :key="star"
                  class="star-icon user-rating"
                >
                  <span class="material-symbols-outlined">kid_star</span>
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 댓글 등록 UI -->
    <div class="comment-container">
      <div class="comment-wrap wrap-1" v-if="!isLoggedIn">
        <p>로그인 후 댓글을 등록할 수 있습니다</p>
      </div>

      <div class="comment-wrap wrap-2" v-else-if="isLoggedIn">
        <h5>
          댓글쓰기 <span> {{ comments.length }}</span>
        </h5>
        <div class="comment-input-wrap">
          <div class="comment-input">
            <span>{{ nickname }} 님</span>
            <textarea
              v-model="newComment"
              placeholder="댓글을 입력하세요"
            ></textarea>
          </div>
          <div class="comment-btn">
            <button class="comment-createBtn" @click="submitComment">
              등록
            </button>
          </div>
        </div>
      </div>

      <div class="comment-wrap wrap-3">
        <div v-if="comments.length > 0" class="comment-allReview">
          <h5>전체 댓글</h5>
          <div
            v-for="(comment, index) in comments"
            :key="index"
            class="comment-list"
          >
            <p class="comment-outer">
              <strong class="comment-nickname">{{ comment.nickname }}</strong>
              <span v-if="comment.isPublic" class="comment-writing">
                <!-- 정상적인 댓글인 경우 -->
                {{ comment.comment }}
              </span>
              <span v-else class="comment-writing">
                <!-- 비공개 댓글인 경우 -->
                '관리자에 의해서 비공개 처리된 댓글입니다.'
              </span>
              <small class="comment-createdAt">{{
                formatCreatedAtDate(comment.createdAt)
              }}</small>
            </p>
            <!-- 삭제 버튼 활성화 조건 추가 -->
            <button
              v-if="comment.email === currentUserEmail"
              @click="deleteComment(comment.id)"
              class="comment-deleteBtn"
            >
              삭제
            </button>
          </div>
        </div>

        <div v-else>
          <p>등록된 댓글이 없습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data () {
    return {
      episode: null,
      artworkId: '',
      episodeCount: 0,
      selectedRating: 0,
      isRated: false,
      isLoggedIn: false,
      averageRating: null, // 평균 별점을 저장하는 데이터
      ratingCount: 0, // 별점 등록 인원 수를 저장하는 데이터
      userRating: null, // 사용자가 등록한 별점을 저장하는 데이터
      newComment: '', // 사용자가 입력한 댓글
      comments: [], // 댓글 목록
      currentUserEmail: sessionStorage.getItem('email'), // 현재 로그인한 이메일
      nickname: sessionStorage.getItem('nickname')
    }
  },
  async created () {
    this.artworkId = sessionStorage.getItem('artworkId')
    this.episodeCount = sessionStorage.getItem('episodeCount')
    const email = sessionStorage.getItem('email')
    const nickname = sessionStorage.getItem('nickname')

    this.isLoggedIn = !!email // 이메일이 있으면 로그인 상태로 간주
    console.log(nickname)

    if (this.artworkId && this.episodeCount) {
      await this.fetchEpisodeDetail(this.artworkId, this.episodeCount)
      await this.checkIfRated(email, this.artworkId, this.episodeCount) // episodeCount도 전달
      await this.fetchAverageRating(this.artworkId, this.episodeCount) // 평균 별점 요청
      await this.fetchRatingCount(this.artworkId, this.episodeCount) // 별점 등록 인원 수 요청
      await this.fetchComments(this.artworkId, this.episodeCount)
      if (this.isLoggedIn) {
        await this.fetchUserRating(email, this.artworkId, this.episodeCount) // 사용자 별점 요청
      }
    } else {
      console.error('No artworkId or episodeCount found in sessionStorage.')
    }
  },
  methods: {
    async fetchEpisodeDetail (artworkId, episodeCount) {
      try {
        const response = await fetch(
          `/api/episodes/artwork/${artworkId}/count/${episodeCount}`
        )
        if (response.ok) {
          const episodes = await response.json()
          this.episode = episodes.length > 0 ? episodes[0] : null
          console.log('Fetched episode:', this.episode) // episode 데이터 출력
        } else {
          console.error(
            `Error fetching episode details: ${response.statusText}`
          )
        }
      } catch (error) {
        console.error('Error fetching episode details:', error)
      }
    },
    // 댓글 전체 조회
    async fetchComments () {
      try {
        const response = await fetch(
          `/api/comments/artwork/${this.artworkId}/episode/${this.episodeCount}`
        )
        if (response.ok) {
          this.comments = await response.json()
        } else {
          console.error('Error fetching comments:', response.statusText)
        }
      } catch (error) {
        console.error('Error occurred while fetching comments:', error)
      }
    },
    // 댓글 전송
    async submitComment () {
      const email = sessionStorage.getItem('email')
      const nickname = sessionStorage.getItem('nickname')

      if (!email || !nickname) {
        console.error('Email or nickname is missing.')
        return
      }

      const commentData = {
        aId: this.artworkId,
        episodeCount: this.episodeCount,
        nickname: nickname,
        email: email,
        comment: this.newComment,
        createdAt: new Date().toISOString()
      }

      console.log('commentData:', commentData)

      try {
        const response = await fetch(
          'http://localhost:8080/api/comments/save',
          {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(commentData)
          }
        )

        console.log('Response status:', response.status)
        console.log('Response OK:', response.ok)

        if (response.ok) {
          this.newComment = ''
          await this.fetchComments() // 댓글 목록 갱신
        } else {
          console.error('댓글 등록 중 오류 발생:', response.statusText)
        }
      } catch (error) {
        console.error('댓글 등록 중 오류 발생:', error)
      }
    },
    // 댓글 삭제
    async deleteComment (commentId) {
      try {
        const response = await fetch(
          `http://localhost:8080/api/comments/delete/${commentId}`,
          {
            method: 'DELETE'
          }
        )

        if (response.ok) {
          await this.fetchComments() // 댓글 목록을 다시 가져옵니다.
        } else {
          console.error('Error deleting comment:', response.statusText)
        }
      } catch (error) {
        console.error('Error occurred while deleting comment:', error)
      }
    },
    // 평균 별점을 계산 결과 산출
    async fetchAverageRating (artworkId, episodeCount) {
      try {
        if (!artworkId || !episodeCount) {
          console.warn('Missing artworkId or episodeCount.')
          return
        }

        const response = await fetch(
          `/api/rates/average?artworkId=${artworkId}&episodeCount=${episodeCount}`
        )

        if (response.ok) {
          const result = await response.json()

          if (result.averageRating === 'averageRating is null') {
            this.averageRating = null
          } else {
            this.averageRating = parseFloat(result.averageRating)
          }
        } else {
          console.error(
            `Error fetching average rating: ${response.statusText}`
          )
          this.averageRating = null
        }
      } catch (error) {
        console.error('Error occurred while fetching average rating:', error)
      }
    },
    async fetchRatingCount () {
      try {
        const response = await axios.get(
          'http://localhost:8080/api/rates/count',
          {
            params: {
              artworkId: this.artworkId,
              episodeCount: this.episodeCount
            }
          }
        )
        this.ratingCount = response.data
      } catch (error) {
        console.error('Error fetching rating count:', error)
      }
    },
    async fetchUserRating (email, artworkId, episodeCount) {
      try {
        const encodedEmail = encodeURIComponent(email) // 이메일을 인코딩
        const hasRatingResponse = await fetch(
          `/api/rates/check?email=${encodedEmail}&artworkId=${artworkId}&episodeCount=${episodeCount}`
        )

        if (hasRatingResponse.ok) {
          const result = await hasRatingResponse.json()
          this.isRated = result.isRated

          // 별점이 등록된 경우에만 조회
          if (this.isRated) {
            const response = await fetch(
              `/api/rates/user-rating?email=${encodedEmail}&artworkId=${artworkId}&episodeCount=${episodeCount}`
            )

            if (response.ok) {
              if (response.status === 204) {
                this.userRating = null // 별점이 없을 때
              } else {
                const result = await response.json()
                this.userRating = result // 사용자 별점 값을 받아옴
              }
            } else {
              console.error(
                `Error fetching user rating: ${response.statusText}`
              )
            }
          } else {
            this.userRating = null // 별점이 등록되지 않은 경우 처리
          }
        } else {
          console.error(
            `Error checking if rating exists: ${hasRatingResponse.statusText}`
          )
        }
      } catch (error) {
        console.error('사용자 별점 조회중 오류 발생:', error)
      }
    },

    getThumbnailUrl (episode) {
      return `/assets/webtoonImage/artwork_id${this.artworkId}/episode_count${episode.episodeCount}/thumbnail_url/${episode.thumbnailUrl}`
    },
    getEpisodeImageUrl (episode, url) {
      return `/assets/webtoonImage/artwork_id${this.artworkId}/episode_count${episode.episodeCount}/episode_url/${url}`
    },
    getPplAdUrl (episode) {
      if (episode && episode.pplAdUrl) {
        return `/assets/webtoonImage/artwork_id${this.artworkId}/episode_count${episode.episodeCount}/ppl_ad_url/${episode.pplAdUrl}`
      }
      return '' // URL이 없을 경우 빈 문자열 반환
    },
    splitEpisodeUrls (episodeUrl) {
      return episodeUrl.split(', ')
    },
    formatUpdatedAt (dateString) {
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}.${month}.${day}`
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
    },
    async checkIfRated (email, artworkId, episodeCount) {
      try {
        const response = await fetch(
          `/api/rates/check?email=${encodeURIComponent(
            email
          )}&artworkId=${artworkId}&episodeCount=${episodeCount}`
        )
        if (response.ok) {
          const result = await response.json()
          this.isRated = result.isRated
        } else {
          console.error(`Error fetching rating check: ${response.statusText}`)
        }
      } catch (error) {
        console.error('별점 확인 중 오류 발생:', error)
      }
    },
    selectRating (star) {
      this.selectedRating = star
    },
    async submitRating () {
      const email = sessionStorage.getItem('email')
      const artworkId = this.artworkId

      if (!email) {
        alert('로그인 후 별점을 등록할 수 있습니다.')
        return
      }

      try {
        const response = await fetch(
          `/api/rates/save?artworkId=${artworkId}&email=${email}&star=${this.selectedRating}&episodeCount=${this.episode.episodeCount}`,
          {
            method: 'POST'
          }
        )
        if (response.ok) {
          // 별점 등록 알림 후 새로고침
          this.isRated = true
          alert('별점이 등록되었습니다.')

          window.location.reload()
        } else {
          console.error('별점 등록 중 오류 발생')
        }
      } catch (error) {
        console.error('별점 저장 요청 실패:', error)
      }
    }
  }
}
</script>

<style scoped>
.detail-view {
  position: relative;
  margin: 20px 0;
}
.detail-container {
  width: 100%;
  display: inline;
  margin: 0 auto;
}
.detail-container .detail-wrap .detail-top {
  width: 100%;
  background: #383b45;
  display: inline-flex;
  justify-content: center;
}
.detail-container .detail-wrap .detail-outer {
  width: 1200px;
  display: grid;
  grid-template-columns: 180px auto;
  box-sizing: border-box;
  color: #fff;
  margin: 0 auto;
}
.detail-container .detail-wrap .detail-outer .detail-thumbnail {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30px;
  box-sizing: border-box;
}
.detail-container .detail-wrap .detail-outer .detail-thumbnail img {
  width: 100%;
  aspect-ratio: 1/1;
  border-radius: 4px;
  object-fit: cover;
  object-position: center center;
  overflow: hidden;
  display: inline;
}
.detail-container .detail-wrap .detail-outer .detail-info {
  padding: 30px;
  text-align: left;
}
.detail-container .detail-wrap .detail-outer .detail-info p {
  margin: 1rem 0 0;
}

.detail-container .detail-wrap .detail-outer .detail-info .detail-title {
  position: relative;
  font-size: 16px;
  line-height: 1;
  display: flex;
  align-items: center;
}
.detail-container .detail-wrap .detail-outer .detail-info .detail-title p {
  position: relative;
  padding-right: 15px;
  display: inline;
  margin: 1rem 0 0;
  font-size: 16px;
}
.detail-container
  .detail-wrap
  .detail-outer
  .detail-info
  .detail-title
  p::after {
  content: "";
  position: absolute;
  top: 0;
  bottom: 2px;
  right: 0;
  width: 1px;
  background: #fff;
}
.detail-container .detail-wrap .detail-outer .detail-info .detail-title h3 {
  display: inline;
  margin: 1rem 0 0;
  padding-left: 15px;
  font-size: 24px;
  text-align: left;
  line-height: 1.1;
  font-weight: 600;
  display: -webkit-box; /* 추가 */
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-word;
  white-space: pre-line;
}

.episode-images {
  display: flex;
  flex-direction: column;
  width: 1200px;
  margin: 34px auto 0;
}
.episode-images .episode-image {
  margin: 0 auto;
  width: 700px;
  height: 100%;
  overflow: hidden;
  box-sizing: border-box;
}
.episode-images .episode-image img {
  width: 100%;
  height: auto;
  object-fit: cover;
  display: block;
}

.episode-ppl {
  display: flex;
  margin: 50px auto 47px;
  width: 960px;
  overflow: hidden;
  box-sizing: border-box;
}
.episode-ppl img {
  margin: 0 auto;
  width: 600px;
  height: 600px;
  aspect-ratio: 1/1;
  object-fit: cover;
  display: block;
}

.rating-section {
  max-width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid #ccc;
  border-bottom: 1px solid #ccc;
  padding: 24px 0;
}
.rating-section-wrap {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  align-items: center;
  width: 600px;
}
.rating-section .average-rating {
  margin: 0 auto;
  height: 50px;
  display: inline-flex;
  align-items: center;
}
.rating-section .average-rating-wrap {
  display: flex;
  align-items: center;
}
.rating-section .average-rating p {
  margin: 0;
  font-size: 16px;
}
.rating-section .average-rating p:nth-child(2) {
  margin: -4px 0 0 16px;
  padding: 6px 12px;
  background: #383b45af;
  color: #fff;
  border-radius: 4px;
}
.stars {
  display: flex;
  justify-content: center;
}
.star-icon {
  font-size: 2rem;
  cursor: pointer;
  color: gray;
}
.user-rating {
  display: inline-flex;
  align-items: center;
}
.user-rating p:nth-child(1) {
  margin-right: 14px;
}
.user-rating .star-icon.user-rating,
.star-icon.active {
  color: gold;
}

.comment-container {
  position: relative;
  width: 100%;
  display: flex;
  flex-direction: column;
}
.comment-container .comment-wrap {
  margin: 0 auto;
  width: 860px;
  display: inline-block;
}
.comment-container .comment-wrap.wrap-2 h5 {
  margin: 32px 60px 0;
  font-size: 20px;
  font-weight: 700;
  color: #000;
  display: flex;
  align-items: center;
}
.comment-container .comment-wrap.wrap-2 h5 span {
  padding: 2px 0 0 6px;
}
.comment-container .comment-wrap.wrap-2 .comment-input-wrap {
  margin: 12px 60px 0;
  border: 1px solid #b3b3b3;
}
.comment-container .comment-wrap.wrap-2 .comment-input {
  display: flex;
  flex-direction: column;
  margin: 28px 33px 18px;
  background: #fff;
}
.comment-container .comment-wrap.wrap-2 .comment-input span {
  display: flex;
  font-size: 14px;
  color: #000;
  font-weight: 600;
}
.comment-container .comment-wrap.wrap-2 .comment-input textarea {
  position: relative;
  display: block;
  float: left;
  margin: 15px 0;
  width: 100%;
  height: 80px;
  border: none;
  color: #333;
  background-color: transparent;
  font-size: 14px;
  line-height: 18px;
  overflow-x: hidden;
  overflow-y: auto;
  z-index: 1;
  -webkit-appearance: none;
  resize: none;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}
.comment-container .comment-wrap.wrap-2 .comment-input textarea:focus {
  outline: 2px solid gold;
  border-radius: 2px;
}
.comment-container .comment-wrap.wrap-2 .comment-btn {
  position: relative;
  height: 43px;
  padding-right: 68px;
  border-top: 1px solid #ededed;
}
.comment-container .comment-wrap.wrap-2 .comment-btn .comment-createBtn {
  position: absolute;
  padding: 0 20px;
  margin: 0;
  right: 0;
  width: auto;
  height: 100%;
  font-size: 13px;
  display: block;
  background: #007bff;
  color: white;
  border: 1px solid #0075f3;
  cursor: pointer;
}
.comment-container .comment-wrap.wrap-3 .comment-allReview {
  margin: 48px 60px 0;
}
.comment-container .comment-wrap.wrap-3 .comment-allReview h5 {
  margin: 32px 0 10px;
    font-size: 20px;
    font-weight: 700;
    color: #000;
    display: flex;
    align-items: center;
}
.comment-container .comment-wrap.wrap-3 .comment-allReview .comment-list {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  border-bottom: 1px solid #e2e2e2;
  padding-bottom: 5px;
}
.comment-container
.comment-wrap.wrap-3
.comment-allReview
.comment-list
.comment-deleteBtn {
  margin: 0;
  padding: 10px 20px;
  font-size: 13px;
  background: #007bff;
  color: white;
  border: 1px solid #0075f3;
  cursor: pointer;
}
.comment-container
.comment-wrap.wrap-3
.comment-allReview
.comment-list
.comment-outer {
  display: flex;
  flex-direction: column;
  text-align: left;
  width: calc(100% - 75px);
  margin-block-start: 8px;
  margin-block-end: 8px;
}
.comment-container
.comment-wrap.wrap-3
.comment-allReview
.comment-list
.comment-outer
.comment-nickname {
  padding-bottom: 2px;
  display: inline-block;
  overflow: hidden;
  max-width: 50%;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 14px;
  color: #000;
  line-height: 25px;
  font-weight: 700;
}
.comment-container
.comment-wrap.wrap-3
.comment-allReview
.comment-list
.comment-outer
.comment-writing {
  overflow: hidden;
  position: relative;
  font-size: 16px;
  line-height: 1.38;
  word-break: break-all;
  word-wrap: break-word;
}
.comment-container
.comment-wrap.wrap-3
.comment-allReview
.comment-list
.comment-outer
.comment-createdAt {
  padding-top: 2px;
  float: left;
  font-size: 12px;
  font-family: tahoma, helvetica, sans-serif;
  color: #999;
}

@media (max-width: 1200px) {
  .detail-container .detail-wrap .detail-outer {
    width: 991px;
  }
  .episode-images {
    width: 991px;
  }
}

@media (max-width: 992px) {
  .detail-container .detail-wrap .detail-outer {
    width: 768px;
    grid-template-columns: 150px auto;
  }
  .detail-container .detail-wrap .detail-outer .detail-thumbnail {
    padding: 20px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info {
    padding: 20px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title p,
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title h3 {
    margin: 12px 0 0;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title h3 {
    width: 400px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info p {
    margin: 12px 0 0;
  }
  .episode-images {
    width: 768px;
  }
  .episode-images .episode-image {
    width: 680px;
  }
  .episode-ppl {
    width: 680px;
  }
  .episode-ppl img {
    width: 480px;
    height: 480px;
  }
  .rating-section-wrap {
    width: 480px;
    grid-template-columns: 180px auto;
  }
  .comment-container .comment-wrap {
    width: 680px;
  }
}
@media (max-width: 768px) {
  .detail-container .detail-wrap .detail-outer {
    width: 595px;
    display: flex;
    flex-direction: column;
  }
  .detail-container .detail-wrap .detail-outer .detail-thumbnail {
    width: 260px;
    margin: 0 auto;
  }
  .detail-container .detail-wrap .detail-outer .detail-info {
    width: 340px;
    margin: 0 auto;
    padding: 0 0 20px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title {
    align-items: start;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title p,
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title h3 {
    margin: 0;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title p {
    margin-top: 5px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title h3 {
    width: 280px;
    -webkit-line-clamp: 2;
  }
  .episode-images {
    width: 560px;
  }
  .episode-images .episode-image {
    width: 480px;
  }
  .episode-ppl {
    width: 480px;
  }
  .episode-ppl img {
    width: 320px;
    height: 320px;
  }
  .rating-section-wrap {
    display: flex;
    flex-direction: column;
  }
  .comment-container .comment-wrap {
    width: 480px;
  }
  .comment-container .comment-wrap.wrap-3 .comment-allReview {
    margin: 48px 30px 0;
  }
  .comment-container .comment-wrap.wrap-3 .comment-allReview .comment-list {
    flex-direction: column;
  }
  .comment-container .comment-wrap.wrap-3 .comment-allReview .comment-list .comment-outer {
    width: 100%;
  }
  .comment-container .comment-wrap.wrap-2 .comment-input-wrap {
    margin: 12px 30px 0;
  }
  .comment-container .comment-wrap.wrap-2 h5 {
    margin: 32px 30px 0;
  }
  .comment-container .comment-wrap.wrap-2 .comment-input {
    margin: 20px 22px 12px;
  }
}
@media (max-width: 576px) {
  .detail-container .detail-wrap .detail-outer {
    width: 420px;
  }
  .detail-container .detail-wrap .detail-outer .detail-thumbnail {
    width: 220px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info {
    width: 240px;
  }

  .detail-container .detail-wrap .detail-outer .detail-info .detail-title p {
    margin-top: 5px;
    font-size: 14px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title p::after {
    bottom: 1px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title h3 {
    width: 180px;
    font-size: 18px;
    line-height: 1.5;
    -webkit-line-clamp: 2;
  }
  .detail-container .detail-wrap .detail-outer .detail-info p {
    font-size: 13px;
  }
  .episode-images {
    width: 420px;
  }
  .episode-images .episode-image {
    width: 360px;
  }
  .episode-ppl {
    width: 320px;
  }
  .episode-ppl img {
    width: 280px;
    height: 280px;
  }
  .rating-section-wrap {
    width: 420px;
  }
  .comment-container .comment-wrap {
    width: 420px;
  }
  .comment-container .comment-wrap.wrap-3 .comment-allReview .comment-list .comment-deleteBtn {
    padding: 8px 16px;
    font-size: 13px;
  }
}
@media (max-width: 425px) {
  .detail-container .detail-wrap .detail-outer {
    width: 360px;
  }
  .detail-container .detail-wrap .detail-outer .detail-thumbnail {
    width: 200px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info {
    width: 220px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title h3 {
    width: 160px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info p {
    margin: 8px 0 0;
  }
  .episode-images {
    width: 360px;
  }
  .episode-images .episode-image {
    width: 330px;
  }
  .episode-ppl {
    width: 300px;
  }
  .episode-ppl img {
    width: 260px;
    height: 260px;
  }
  .rating-section-wrap {
    width: 360px;
  }
  .comment-container .comment-wrap {
    width: 360px;
  }
  .comment-container .comment-wrap.wrap-2 h5 {
    margin: 32px 20px 0;
  }
  .comment-container .comment-wrap.wrap-2 .comment-input-wrap {
    margin: 12px 20px 0;
  }
  .comment-container .comment-wrap.wrap-3 .comment-allReview {
    margin: 28px 20px 0;
  }
}
@media (max-width: 375px) {
  .detail-container .detail-wrap .detail-outer {
    width: 300px;
  }
  .detail-container .detail-wrap .detail-outer .detail-thumbnail {
    width: 180px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info {
    width: 200px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title h3 {
    width: 130px;
  }
  .episode-images {
    width: 300px;
  }
  .episode-images .episode-image {
    width: 280px;
  }
  .episode-ppl {
    width: 280px;
  }
  .episode-ppl img {
    width: 240px;
    height: 240px;
  }
  .rating-section {
    padding: 15px 0;
  }
  .rating-section-wrap {
    width: 300px;
  }
  .comment-container .comment-wrap {
    width: 300px;
  }
  .comment-container .comment-wrap.wrap-2 h5 {
    margin: 22px 20px 0;
  }
  .comment-container .comment-wrap.wrap-3 .comment-allReview {
    margin: 22px 20px 0;
  }
}
@media (max-width: 320px) {
  .detail-container .detail-wrap .detail-outer {
    width: 280px;
  }
  .detail-container .detail-wrap .detail-outer .detail-thumbnail {
    width: 160px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info {
    width: 180px;
  }
  .detail-container .detail-wrap .detail-outer .detail-info .detail-title h3 {
    width: 120px;
  }
  .episode-images {
    width: 280px;
  }
  .episode-images .episode-image {
    width: 260px;
  }
  .episode-ppl {
    width: 260px;
  }
  .episode-ppl img {
    width: 220px;
    height: 220px;
  }
  .rating-section-wrap {
    width: 280px;
  }
  .comment-container .comment-wrap {
    width: 280px;
  }
  .comment-container .comment-wrap.wrap-2 h5 {
    margin: 22px 20px 0;
  }
  .comment-container .comment-wrap.wrap-2 .comment-input textarea {
    height: 60px;
  }
  .comment-container .comment-wrap.wrap-3 .comment-allReview {
    margin: 22px 20px 0;
  }
}
</style>
