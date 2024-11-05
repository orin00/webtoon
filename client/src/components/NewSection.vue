<template>
  <div class="new-container">
    <div class="new-wrapper">
      <div v-for="artwork in artworks" :key="artwork.id" class="artwork-item">
        <router-link
          :to="{
            name: 'WebtoonListView',
            params: {
              artworkId: artwork.artworkId,
              serialDay: artwork.serialDay,
            },
          }"
          @click="saveToSession(artwork.artworkId, artwork.serialDay)"
        >
          <div class="profile-outer">
            <img :src="getProfileImageUrl(artwork)" alt="Profile Image" />
          </div>

          <div class="new-info">
            <h3 class="new-title">{{ artwork.title }}</h3>
            <span class="new-genre">{{ artwork.genre }}</span>
          </div>

          <span class="new-nickname">{{ artwork.nickname }}</span>
          <p class="new-description">{{ artwork.description }}</p>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      artworks: []
    }
  },
  mounted () {
    this.fetchLatestArtworks()
  },
  methods: {
    async fetchLatestArtworks () {
      try {
        const response = await fetch('/api/artworks/latest')
        if (!response.ok) {
          throw new Error('Failed to fetch latest artworks')
        }
        this.artworks = await response.json()
        console.log(this.artworks)
      } catch (error) {
        console.error(error)
      }
    },
    saveToSession (artworkId, serialDay) {
      sessionStorage.setItem('artworkId', artworkId)
      sessionStorage.setItem('serialDay', serialDay)
    },
    getProfileImageUrl (artworks) {
      return `/assets/webtoonImage/artwork_id${artworks.artworkId}/profile_image/${artworks.profileImage}`
    }
  }
}
</script>

<style scoped>
.new-container {
  position: relative;
  width: 100%;
  margin-top: 20px;
}

.new-wrapper {
  display: flex;
  align-items: center;
}

.new-wrapper a {
  text-decoration: none;
}

.artwork-item {
  margin-right: 20px;
  width: 33.33%;
  height: 100%;
  text-align: left;
}
.artwork-item:nth-last-child(1) {
  margin-right: 0px;
}
.profile-outer {
  width: 100%;
  height: 300px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 0.4rem;
  overflow: hidden;
}
.profile-outer img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center center;
}
.new-info {
  margin-top: 12px;
  display: flex;
  align-items: center;
}
.new-title {
  margin-left: 15px;
  color: #000;
  word-break: break-word;
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  font-size: 20px;
  font-weight: 600;
  line-height: 30px;
  margin-block-start: 0;
  margin-block-end: 0;
}
.new-nickname {
  margin-block-start: 2px;
  margin-block-end: 0;
  margin-left: 15px;
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  font-size: 14px;
  line-height: 20px;
  font-weight: 500;
  color: #000;
  word-break: break-word;
}
.new-description {
  margin-block-start: 2px;
  margin-block-end: 0;
  margin-left: 15px;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  font-size: 14px;
  font-weight: 500;
  line-height: 20px;
  font-weight: 500;
  color: #666;
  word-break: break-word;
}
.new-genre {
  display: inline-block;
  margin-left: 8px;
  padding: 0 10px;
  background: #eee;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  line-height: 30px;
  letter-spacing: -0.42px;
  color: #000;
}
@media (max-width: 1200px) {
  .profile-outer {
    height: 246px;
  }
}
@media (max-width: 992px) {
  .profile-outer {
    height: 188px;
  }
  .new-info {
    margin-top: 8px;
  }
  .new-title {
    font-size: 16px;
    line-height: 24px;
  }
  .new-genre {
    font-size: 12px;
    line-height: 24px;
  }
  .new-description {
    font-size: 12px;
    line-height: 16px;
  }
}
@media (max-width: 768px) {
  .new-wrapper {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
  .artwork-item {
    margin-right: 0;
    width: 100%;
    margin-top: 20px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  }
  .profile-outer {
    width: auto;
    height: 210px;
    padding: 10px;
    border: none;
  }
  .profile-outer img {
    border-radius: 0.4rem;
  }
  .new-description {
    margin-bottom: 18px;
  }
}
@media (max-width: 595px) {
  .new-wrapper {
    display: flex;
    flex-direction: column;
    gap: 0px;
  }
  .artwork-item {
    width: 462px;
  }
  .artwork-item:nth-child(1) {
    margin-top: 0;
  }
  .profile-outer {
    height: 300px;
  }
}
@media (max-width: 480px) {
  .artwork-item {
    width: 400px;
  }
  .profile-outer {
    height: 275px;
  }
}
@media (max-width: 425px) {
  .artwork-item {
    width: 360px;
  }
  .profile-outer {
    height: 240px;
  }
}
@media (max-width: 375px) {
  .artwork-item {
    width: 320px;
  }
  .profile-outer {
    height: 200px;
  }
}
@media (max-width: 320px) {
  .artwork-item {
    width: 100%;
  }
  .profile-outer {
    height: 180px;
  }
}
</style>
