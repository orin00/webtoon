<template>
  <div v-if="isArtist" class="artist">
    <h2>아티스트 페이지</h2>
    <div class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab.name"
        :class="{ active: activeTab === tab.name }"
        @click="activeTab = tab.name"
      >
        {{ tab.label }}
      </button>
    </div>

    <div class="tab-content">
      <EpisodeSubmit v-if="activeTab === 'episode'" />
      <ArtworkSubmit v-if="activeTab === 'artwork'" />
      <BreakRequest v-if="activeTab === 'break'" />
      <NormalRequest v-if="activeTab === 'normal'" />
      <CompleteRequest v-if="activeTab === 'complete'" />
    </div>
  </div>
</template>

<script>
import EpisodeSubmit from '@/components/artist/EpisodeSubmit.vue'
import ArtworkSubmit from '@/components/artist/ArtworkSubmit.vue'
import BreakRequest from '@/components/artist/BreakRequest.vue'
import NormalRequest from '@/components/artist/NormalRequest.vue'
import CompleteRequest from '@/components/artist/CompleteRequest.vue'

export default {
  components: {
    EpisodeSubmit,
    ArtworkSubmit,
    BreakRequest,
    NormalRequest,
    CompleteRequest
  },
  data () {
    return {
      activeTab: 'episode', // Default active tab
      tabs: [
        { name: 'episode', label: '에피소드 검수 신청' }, // Episode Submit
        { name: 'artwork', label: '신작 웹툰 등록 신청' }, // Artwork Submit
        { name: 'break', label: '휴재 신청' }, // Break Request
        { name: 'normal', label: '휴재 복귀 신청' }, // Normal Request
        { name: 'complete', label: '완결 신청' } // Complete Request
      ]
    }
  },
  computed: {
    // Vuex 스토어에서 isArtist 상태를 가져오는 계산된 속성
    isArtist () {
      return this.$store.getters.isArtist
    }
  },
  mounted () {
  }
}
</script>

<style scoped>
.artist {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
}

.tabs {
  display: flex;
  margin-bottom: 20px;
}

.tabs button {
  padding: 10px;
  margin-right: 5px;
  cursor: pointer;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.tabs button.active {
  background-color: #007bff;
  color: white;
}

.tab-content {
  border: 1px solid #ccc;
  padding: 20px;
  border-radius: 4px;
}
@media (max-width: 1280px) {
  .artist {
    max-width: 972px;
  }
}
@media (max-width: 992px) {
  .artist {
    max-width: 728px;
    min-width: 720px;
  }
}
</style>
