<template>
  <div v-if="isAdmin" class="admin">
    <h2>관리자 페이지</h2>
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
      <EpisodeConfirm v-if="activeTab === 'episode'" />
      <ArtworkConfirm v-if="activeTab === 'artwork'" />
      <BreakResponse v-if="activeTab === 'break'" />
      <NormalResponse v-if="activeTab === 'normal'" />
      <CompleteResponse v-if="activeTab === 'complete'" />
      <CommentsManagement v-if="activeTab === 'comments'" />
    </div>
  </div>
</template>

<script>
import ArtworkConfirm from '@/components/admin/artistTab/ArtworkConfirm.vue'
import BreakResponse from '@/components/admin/artistTab/BreakResponse.vue'
import CompleteResponse from '@/components/admin/artistTab/CompleteResponse.vue'
import EpisodeConfirm from '@/components/admin/artistTab/EpisodeConfirm.vue'
import NormalResponse from '@/components/admin/artistTab/NormalResponse.vue'
import CommentsManagement from '@/components/admin/userTab/CommentsManagement.vue'

export default {
  components: {
    ArtworkConfirm,
    BreakResponse,
    CompleteResponse,
    EpisodeConfirm,
    NormalResponse,
    CommentsManagement
  },
  data () {
    return {
      activeTab: 'episode', // Default active tab
      tabs: [
        { name: 'episode', label: '에피소드 검수' }, // Episode Submit
        { name: 'artwork', label: '신작 웹툰 등록' }, // Artwork Submit
        { name: 'break', label: '휴재 처리' }, // Break Request
        { name: 'normal', label: '휴재 복귀 처리' }, // Normal Request
        { name: 'complete', label: '완결 처리' }, // Complete Request
        { name: 'comments', label: '유저 댓글 관리' } // Complete Request
      ]
    }
  },
  computed: {
    // Vuex 스토어에서 isAdmin 상태를 가져오는 계산된 속성
    isAdmin () {
      return this.$store.getters.isAdmin
    }
  },
  mounted () {
  }
}
</script>

<style scoped>
.admin {
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
  .admin {
    max-width: 972px;
  }
}
@media (max-width: 992px) {
  .admin {
    max-width: 728px;
    min-width: 720px;
  }
}
</style>
