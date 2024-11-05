<template>
  <div>
    <h4>특정 아트워크의 에피소드 연재 요청</h4>

    <select v-model="selectedArtwork" @change="updateSelectedArtworkId">
      <option v-for="artwork in artworks" :key="artwork.id" :value="artwork.id">
        {{ artwork.title }}
      </option>
    </select>

    <form @submit.prevent="submitForm" class="episode-form">
      <div v-if="selectedArtworkId !== null" class="episode-info">
        <h5>작품 번호: {{ selectedArtworkId }}</h5>
        <h5>해당 에피소드 화: {{ episodeCount + 1 }}</h5>
      </div>

      <div
        class="image-upload"
        @dragover.prevent
        @drop="handleDrop('thumbnail')"
      >
        <span>썸네일 업로드</span>
        <label for="thumbnail-file-upload" class="drop-zone">
          <img
            v-if="thumbnailImageSrc"
            :src="thumbnailImageSrc"
            alt="Uploaded Image"
            class="image-preview"
          />
        </label>
        <input
          type="file"
          id="thumbnail-file-upload"
          @change="handleFileUpload('thumbnail')"
          ref="thumbnailFileInput"
          accept="image/*"
        />
        <p v-if="thumbnailImageName">
          업로드된 이미지: {{ thumbnailImageName }}
        </p>
        <p v-if="!thumbnailImageSrc && formSubmitted" class="error-message">
          썸네일 이미지를 업로드해주세요.
        </p>
      </div>

      <div class="title-outer">
        <label for="title">에피소드명:</label>
        <input type="text" id="title" v-model="title" required />
      </div>

      <div
        class="image-upload"
        @dragover.prevent
        @drop="handleDrop('episodes')"
      >
        <span>에피소드 업로드</span>

        <label for="episodes-file-upload" class="drop-zone">
          <div
            v-for="(imgSrc, index) in episodeImageSrcs"
            :key="index"
            class="image-item"
          >
            <img :src="imgSrc" alt="Uploaded Image" class="image-preview" />
            <button
              type="button"
              @click="removeEpisodeImage(index)"
              class="remove-button"
            >
              X
            </button>
          </div>
        </label>
        <input
          type="file"
          id="episodes-file-upload"
          @change="handleFileUpload('episodes')"
          ref="episodesFileInput"
          accept="image/*"
          multiple
        />
        <p v-if="episodeImageNames.length">
          업로드된 이미지들: {{ episodeImageNames.join(", ") }}
        </p>
        <p
          v-if="episodeImageSrcs.length === 0 && formSubmitted"
          class="error-message"
        >
          에피소드 이미지를 업로드해주세요.
        </p>
      </div>

      <div class="image-upload" @dragover.prevent @drop="handleDrop('ppl')">
        <span>PPL 이미지 업로드</span>
        <label for="ppl-file-upload" class="drop-zone">
          <img
            v-if="pplImageSrc"
            :src="pplImageSrc"
            alt="PPL Image"
            class="image-preview"
          />
        </label>
        <input
          type="file"
          id="ppl-file-upload"
          @change="handleFileUpload('ppl')"
          ref="pplFileInput"
          accept="image/*"
        />
        <p v-if="pplImageName">업로드된 이미지: {{ pplImageName }}</p>
        <p v-if="!pplImageSrc && formSubmitted" class="error-message">
          PPL 이미지를 업로드해주세요.
        </p>
      </div>

      <button type="submit" class="submit-button">제출</button>
    </form>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  data () {
    return {
      artworks: [],
      selectedArtwork: null,
      selectedArtworkId: null,
      episodeCount: 0,
      title: '',
      thumbnailImageSrc: null,
      thumbnailImageName: '',
      episodeImageSrcs: [],
      episodeImageNames: [],
      pplImageSrc: null,
      pplImageName: '',
      formSubmitted: false
    }
  },
  computed: {
    ...mapState(['account'])
  },
  created () {
    this.fetchArtworks()
  },
  methods: {
    submitForm () {
      this.submitImages()
      const formData = {
        aId: this.selectedArtworkId,
        episodeCount: this.episodeCount + 1,
        thumbnailUrl: this.thumbnailImageName,
        episodeTitle: this.title,
        episodeUrl: this.episodeImageNames.join(', '),
        pplAdUrl: this.pplImageName || null,
        rating: 0,
        reviewStatus: 'Review'
      }

      console.log('Submitting episode data:', formData)

      fetch('/api/episodes/submitEpisode', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return response.json()
        })
        .then((data) => {
          console.log('에피소드가 성공적으로 생성되었습니다:', data)
          this.formSubmitted = true
        })
        .catch((error) => {
          console.error('에피소드 생성 중 오류 발생:', error)
        })
    },
    submitImages () {
      const imageData = new FormData()
      imageData.append('aId', this.selectedArtworkId)
      imageData.append('episodeCount', this.episodeCount + 1)

      if (this.$refs.thumbnailFileInput.files[0]) {
        imageData.append('thumbnail', this.$refs.thumbnailFileInput.files[0])
      }

      Array.from(this.$refs.episodesFileInput.files).forEach((file) => {
        imageData.append('episodes', file)
      })

      if (this.$refs.pplFileInput.files[0]) {
        imageData.append('ppl', this.$refs.pplFileInput.files[0])
      }

      fetch('http://localhost:5000/api/upload', {
        method: 'POST',
        body: imageData
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return response.json()
        })
        .then((data) => {
          console.log('이미지가 성공적으로 업로드되었습니다:', data)
        })
        .catch((error) => {
          console.error('이미지 업로드 중 오류 발생:', error)
        })
    },
    fetchArtworks () {
      const email = this.account ? this.account.email : null
      if (!email) {
        console.error('로그인된 사용자 이메일을 찾을 수 없습니다.')
        return
      }

      fetch(`/api/artworks/notCompleted?email=${encodeURIComponent(email)}`)
        .then((response) => {
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return response.json()
        })
        .then((data) => {
          // review_status가 "Approved"인 작품 필터 기능 구현
          this.artworks = data.filter(
            (artwork) => artwork.reviewStatus === 'Approved'
          )
        })
        .catch((error) => {
          console.error('아트워크 가져오기 중 오류 발생:', error)
        })
    },
    updateSelectedArtworkId () {
      const selectedArtwork = this.artworks.find(
        (artwork) => artwork.id === this.selectedArtwork
      )
      if (selectedArtwork) {
        this.selectedArtworkId = selectedArtwork.artworkId
        this.fetchEpisodeCount(selectedArtwork.artworkId)
      } else {
        this.selectedArtworkId = null
        this.episodeCount = 0
      }
    },
    fetchEpisodeCount (aId) {
      fetch(`/api/episodes/count?aId=${encodeURIComponent(aId)}`)
        .then((response) => {
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return response.json()
        })
        .then((data) => {
          this.episodeCount = data.count
        })
        .catch((error) => {
          console.error('에피소드 수 조회 중 오류 발생:', error)
        })
    },
    handleFileUpload (type) {
      const fileInput =
    type === 'thumbnail'
      ? this.$refs.thumbnailFileInput
      : type === 'episodes'
        ? this.$refs.episodesFileInput
        : this.$refs.pplFileInput

      const files = Array.from(fileInput.files)
      if (files.length > 0) {
        if (type === 'episodes') {
          // 모든 선택된 파일을 처리
          files.forEach((file) => {
            // 파일 이름 중복 검사
            if (!this.episodeImageNames.includes(file.name)) {
              this.episodeImageSrcs.push(URL.createObjectURL(file))
              this.episodeImageNames.push(file.name)
            }
          })
        } else {
          const reader = new FileReader()
          reader.onload = (e) => {
            if (type === 'thumbnail') {
              this.thumbnailImageSrc = e.target.result
              this.thumbnailImageName = files[0].name
            } else if (type === 'ppl') {
              this.pplImageSrc = e.target.result
              this.pplImageName = files[0].name
            }
          }
          reader.readAsDataURL(files[0])
        }
      }
    },
    removeEpisodeImage (index) {
      this.episodeImageSrcs.splice(index, 1)
      this.episodeImageNames.splice(index, 1)
    },
    handleDrop (type) {
      const fileInput =
        type === 'thumbnail'
          ? this.$refs.thumbnailFileInput
          : type === 'episodes'
            ? this.$refs.episodesFileInput
            : this.$refs.pplFileInput

      fileInput.files = event.dataTransfer.files
      this.handleFileUpload(type)
    }
  }
}
</script>

<style scoped>
.episode-info {
  width: 480px;
  margin: 0 auto;
  text-align: start;
}
.episode-info h5 {
  font-size: 16px;
}
.episode-form {
  position: relative;
  width: 720px;
  margin: 0 auto;
  text-align: left;
}
.title-outer {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}
.title-outer input {
  margin-top: 12px;
  width: calc(100% - 6px);
  border: 1px solid #b3b3b3;
  outline: none;
  cursor: pointer;
  font-size: 18px;
  height: 24px;
}
select {
  background: white;
  border: 1px solid #07f;
  border-radius: 4px;
  display: inline-block;
  line-height: 16px;
  padding: 5px 10px;
  margin: 0;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-appearance: none;
  -moz-appearance: none;
  cursor: pointer;
}
.image-upload {
  border: 2px dashed #ccc;
  width: calc(100% - 4px);
  padding: 20px 0;
  margin: 12px 0;
  text-align: center;
}
.image-item {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-bottom: 10px;
}
.image-item .remove-button {
  width: 46px;
  height: 46px;
  color: #fff;
  background: #07f;
  border: none;
  border-radius: 4px;
  outline: none;
  cursor: pointer;
}

.image-preview {
  width: 600px;
  height: auto;
  object-fit: cover;
  margin: 10px 0;
}
.drop-zone {
  margin: 20px 0;
  display: block;
  width: 100%;
  min-height: 60px;
  background: #ebebeb;
  position: relative;
  cursor: pointer;
}
.error-message {
  color: red;
}
.episode-form .submit-button {
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
  .episode-form {
    width: 680px;
  }
}
</style>
