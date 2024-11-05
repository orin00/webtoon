<template>
  <div>
    <h4>유저 댓글 관리</h4>
    <div v-if="paginatedComments.length > 0" class="comment-wrap">
      <div
        v-for="comment in paginatedComments"
        :key="comment.id"
        class="comment-list"
      >
        <div v-if="comment" class="user-info">
          <h4>작성자 정보</h4>
          <p><strong>닉네임:</strong> {{ comment.nickname }}</p>
          <p><strong>이메일:</strong> {{ comment.email }}</p>
        </div>
        <div v-if="comment" class="comment-info">
          <h4>댓글 정보</h4>
          <p><strong>작품 번호:</strong> {{ comment.aId }}</p>
          <p><strong>에피소드 번호:</strong> {{ comment.episodeCount }}</p>
          <p><strong>댓글 내용:</strong> {{ comment.comment }}</p>
          <p><strong>댓글 작성 날짜:</strong> {{ formatCreatedAtDate(comment.createdAt) }}</p>
        </div>

        <div class="button-section">
          <button @click="hideComment(comment.id)">비공개 처리</button>
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
      <p>작성된 댓글이 없습니다.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data () {
    return {
      comments: [],
      artwork: null,
      currentPage: 1
    }
  },
  computed: {
    paginatedComments () {
      const start = (this.currentPage - 1) * 5
      const end = start + 5
      return this.comments.slice(start, end)
    },
    totalPages () {
      return Math.ceil(this.comments.length / 5)
    }
  },
  created () {
    this.fetchComments()
  },
  methods: {
    async fetchComments () {
      try {
        const response = await axios.get('/api/comments')
        this.comments = response.data
        console.log(this.comments)
      } catch (error) {
        console.error('댓글 조회 실패:', error)
      }
    },
    async hideComment (id) {
      try {
        await axios.patch(`/api/comments/${id}/hide`)
        console.log(`댓글 ID ${id} 비공개 처리 성공`)
        this.fetchComments() // 댓글 목록 새로 고침
      } catch (error) {
        console.error('댓글 비공개 처리 실패:', error)
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
.comment-wrap {
  position: relative;
  width: 720px;
  display: inline-flex;
  flex-direction: column;
}
.comment-list {
  margin: 0 auto 30px;
  padding: 20px 30px;
  width: 480px;
  display: flex;
  flex-direction: column;
  border: 1px solid #ccc;
}
.user-info,
.comment-info {
  padding: 0 15px;
  text-align: left;
}
.comment-info {
  margin-top: 20px;
}
.user-info h4,
.comment-info h4 {
  margin: 0;
  font-size: 20px;
  text-align: center;
}
.user-info p,
.comment-info p {
  margin: 10px 0;
  font-size: 14px;
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
  .comment-wrap {
    width: 680px;
  }
}
</style>
