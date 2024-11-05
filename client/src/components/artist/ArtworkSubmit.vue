<template>
  <div>
    <h4>신작 웹툰 등록 신청</h4>

    <form @submit.prevent="submitForm" class="artwork-form">
      <div class="artist-info">
        <div>
          <label for="email">이메일:</label>
          <p id="email">&nbsp;{{ email }}</p>
        </div>
        <div>
          <label for="username">성명:</label>
          <p id="username">&nbsp;{{ username }}</p>
        </div>
        <div>
          <label for="nickname">닉네임:</label>
          <p id="nickname">&nbsp;{{ nickname }}</p>
        </div>
      </div>

      <div class="artworkId-outer">
        <label for="artist_id">해당 작품에 부여될 번호:</label>
        <p id="artist_id">&nbsp;{{ artworkId }}</p>
      </div>

      <div class="title-outer">
        <label for="title">작품명</label>
        <input type="text" id="title" v-model="title" required />
      </div>

      <div class="description-outer">
        <label for="description">작품소개글</label>
        <textarea id="description" v-model="description" rows="5" required></textarea>
      </div>

      <div class="genre-outer">
        <label>장르 선택</label>
        <div class="radio-group">
          <label v-for="g in genres" :key="g">
            <input type="radio" v-model="genre" :value="g" required />
            {{ g }}
          </label>
        </div>
      </div>

      <div class="serialDay-outer">
        <label>연재일</label>
        <div class="radio-group">
          <label v-for="d in serialDays" :key="d">
            <input type="radio" v-model="serialDay" :value="d" required />
            {{ translateDayToKorean(d) }}요일
          </label>
        </div>
      </div>

      <div class="image-upload" @dragover.prevent @drop="handleDrop('profile', $event)">
        <span>프로필 업로드</span>
        <label for="profile-file-upload" class="drop-zone">
          <img v-if="profileImageSrc" :src="profileImageSrc" alt="Uploaded Image" class="image-preview" />
        </label>
        <input type="file" id="profile-file-upload" @change="handleFileUpload('profile')" ref="profileFileInput" accept="image/*" />
      </div>

      <button type="submit">제출</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data () {
    return {
      email: '',
      username: '',
      nickname: '',
      artworkId: null,
      title: '',
      description: '',
      genre: '',
      serialDay: '',
      profileImageSrc: '',
      profileImageName: '',
      profileImageFile: null,
      genres: [],
      serialDays: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
    }
  },
  mounted () {
    const email = this.$store.state.account.email
    if (email) {
      this.email = email

      axios.get(`/api/users/email?email=${email}`)
        .then((response) => {
          const userData = response.data
          this.username = userData.username
          this.nickname = userData.nickname
        })
        .catch((error) => {
          console.error('유저 정보를 불러오는 중 오류 발생', error)
        })
    }

    axios.get('/api/artworks/next-id')
      .then((response) => {
        this.artworkId = response.data
        console.log('artworkId:', this.artworkId)
      })
      .catch((error) => {
        console.error('다음 artwork_id를 불러오는 중 오류 발생:', error)
      })

    fetch('/assets/artworkGenres.json')
      .then((response) => response.json())
      .then((data) => {
        this.genres = data
        if (data.length > 0) {
          this.genre = data[0]
        }
      })
      .catch((error) => {
        console.error('장르 데이터를 불러오는 중 오류 발생:', error)
      })
  },
  methods: {
    handleFileUpload (type) {
      const fileInput = this.$refs.profileFileInput
      if (fileInput.files.length > 0) {
        const file = fileInput.files[0]
        this.profileImageFile = file
        this.profileImageName = file.name

        const reader = new FileReader()
        reader.onload = (e) => {
          this.profileImageSrc = e.target.result
        }
        reader.readAsDataURL(file)
      }
    },
    handleDrop (type, event) {
      event.preventDefault()
      const files = event.dataTransfer.files
      if (files.length > 0) {
        const file = files[0]
        this.profileImageFile = file
        this.profileImageName = file.name

        const reader = new FileReader()
        reader.onload = (e) => {
          this.profileImageSrc = e.target.result
        }
        reader.readAsDataURL(file)
      }
    },
    submitForm () {
      if (this.profileImageFile) {
        const formData = new FormData()
        formData.append('profile', this.profileImageFile)
        formData.append('artworkId', this.artworkId) // artworkId 전송

        axios.post('http://localhost:5000/api/artworks/submitProfile', formData)
          .then(() => {
            console.log('프로필 이미지가 성공적으로 업로드되었습니다.')
            console.log('artworkId', this.artworkId) // artworkId 전송
            this.submitArtworkData()
          })
          .catch(error => {
            console.error('프로필 이미지 업로드 중 오류 발생:', error)
          })
      } else {
        this.submitArtworkData()
      }
    },
    submitArtworkData () {
      const artworkData = {
        email: this.email,
        username: this.username,
        nickname: this.nickname,
        artworkId: this.artworkId,
        title: this.title,
        description: this.description,
        status: 'Normal',
        reviewStatus: 'Review',
        genre: this.genre,
        serialDay: this.serialDay,
        profileImage: this.profileImageName // 파일 이름 전송
      }

      axios.post('/api/artworks/submit', artworkData)
        .then(response => {
          console.log('Artwork가 성공적으로 등록되었습니다:', response.data)
          this.resetForm()
          window.location.reload() // 페이지 새로고침
        })
        .catch(error => {
          console.error('Artwork 등록 중 오류 발생:', error)
        })
    },
    resetForm () {
      this.title = ''
      this.description = ''
      this.genre = ''
      this.serialDay = ''
      this.profileImageSrc = ''
      this.profileImageName = ''
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
.artwork-form {
  display: inline-block;
  width: 720px;
  text-align: left;
}
.artist-info > div {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}
.artist-info > div label,
.artist-info > div p,
.artworkId-outer > label,
.artworkId-outer > p{
  display: inline-flex;
  margin-block-start: 0;
  margin-block-end: 0;
}
.artworkId-outer {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.title-outer,
.description-outer {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}
.title-outer input,
.description-outer textarea {
  margin-top: 12px;
  width: calc(100% - 6px);
  border: 1px solid #b3b3b3;
  outline: none;
}
.genre-outer {
  display: inline-flex;
  flex-direction: column;
  margin-bottom: 12px;
}
.genre-outer .radio-group {
  margin-top: 8px;
  display: grid;
  grid-template-columns: repeat(5, 130px);
}
.serialDay-outer {
  display: inline-flex;
  flex-direction: column;
  margin-bottom: 12px;
}
.serialDay-outer .radio-group {
  margin-top: 8px;
  display: grid;
  grid-template-columns: repeat(5, 130px);
}
.image-upload {
  border: 2px dashed #ccc;
  padding: 20px;
  margin: 10px 0;
  text-align: center;
}
.drop-zone {
  display: block;
  width: 100%;
  height: auto;
  background-color: #f9f9f9;
  position: relative;
}
.image-preview {
  width: 480px;
  height: auto;
  margin: 10px 0;
}
.artwork-form button {
  margin: 0 auto;
  padding: 0 28px;
  height: 46px;
  display: flex;
  flex: 1;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
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

@media (max-width: 992px) {
  .artwork-form {
    width: 680px;
  }
}
</style>
