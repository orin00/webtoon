<template>
  <nav>
    <div class="nav-wrap">
      <div class="nav-serve">
        <div class="nav-main-logo">
          <router-link
            :to="{ path: '/' }"
            :class="{
              'nav-logo': true,
              'active-link': isActive('/'),
            }"
          >
            <img src="/favicon.ico" alt="favicon" />
          </router-link>
        </div>

        <button class="hamburger" @click="toggleMenu" v-if="isMobile">
          <span class="material-symbols-outlined">
            {{ showMenu ? "close" : "menu" }}
          </span>
        </button>
      </div>

      <!-- 메뉴 항목들 -->
      <div
        :class="{
          'nav-menubar': true,
          'menu-visible': showMenu || !isMobile,
          'menu-hidden': !showMenu && isMobile,
        }"
      >
        <div class="menubar-wrap">
          <router-link
            :to="{ path: '/' }"
            :class="{ 'active-link': isActive('/') }"
            @click="closeMenu"
            >홈</router-link
          >
          <router-link
            :to="{ path: '/webtoon' }"
            :class="{ 'active-link': isActive('/webtoon') }"
            @click="closeMenu"
            >요일웹툰</router-link
          >
          <router-link
            :to="{ path: '/complete' }"
            :class="{ 'active-link': isActive('/complete') }"
            @click="closeMenu"
            >완결웹툰</router-link
          >

          <router-link
            v-if="!isLoggedIn"
            :to="{ path: '/login' }"
            :class="{ 'active-link': isActive('/login') }"
            @click="closeMenu"
            >로그인</router-link
          >
          <router-link
            v-if="!isLoggedIn"
            :to="{ path: '/signup' }"
            :class="{ 'active-link': isActive('/signup') }"
            @click="closeMenu"
            >회원가입</router-link
          >

          <router-link
            v-if="isLoggedIn"
            :to="{ path: '/mypage' }"
            :class="{ 'active-link': isActive('/mypage') }"
            @click="closeMenu"
            >{{ nickname }} 님</router-link
          >
          <router-link
            v-if="isLoggedIn && isAdmin"
            :to="{ path: '/admin' }"
            :class="{ 'active-link': isActive('/admin') }"
            @click="closeMenu"
            >관리자</router-link
          >
          <router-link
            v-if="isLoggedIn && isArtist"
            :to="{ path: '/artist' }"
            :class="{ 'active-link': isActive('/artist') }"
            @click="closeMenu"
            >아티스트</router-link
          >

          <button
            v-if="isLoggedIn"
            @click="logout"
            :class="{ 'active-link': isActive('/') }"
          >
            로그아웃
          </button>
          <button
            v-if="isLoggedIn"
            @click="deleteAccount"
            :class="{ 'active-link': isActive('/') }"
          >
            회원탈퇴
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import { computed, ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

export default {
  setup () {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()

    const isLoggedIn = computed(() => !!store.state.account)
    const isAdmin = computed(() => store.getters.isAdmin)
    const isArtist = computed(() => store.getters.isArtist)
    const nickname = computed(() => store.state.account?.nickname || '')

    const showMenu = ref(false)
    const isMobile = ref(window.innerWidth <= 768)

    const toggleMenu = () => {
      showMenu.value = !showMenu.value
    }

    const handleResize = () => {
      isMobile.value = window.innerWidth <= 768
      if (!isMobile.value) {
        showMenu.value = false // 모바일이 아니면 메뉴를 항상 숨김
      }
    }

    onMounted(() => {
      window.addEventListener('resize', handleResize)
    })

    const logout = async () => {
      await store.dispatch('logout')
      router.push('/')
    }

    const deleteAccount = async () => {
      const account = store.state.account
      if (!account) return

      try {
        await axios.delete(`/api/users/${account.id}`)
        await store.dispatch('logout')
        router.push('/')
      } catch (error) {
        alert('회원 탈퇴 중 오류가 발생했습니다: ' + error.message)
      }
    }
    const closeMenu = () => {
      showMenu.value = false
    }
    const isActive = (path) => {
      const currentPath = route.path
      const serialDay = route.params.serialDay

      if (
        path === '/' &&
        (currentPath === '/logout' || currentPath === '/deleteAccount')
      ) {
        return false
      }

      if (path === '/') {
        return currentPath === '/'
      }

      if (path === '/webtoon') {
        return currentPath.startsWith('/webtoon') && serialDay !== 'Finish'
      }

      if (path === '/complete') {
        return currentPath.startsWith('/complete') || serialDay === 'Finish'
      }

      return currentPath.startsWith(path) && currentPath !== '/'
    }

    return {
      closeMenu,
      isLoggedIn,
      isAdmin,
      isArtist,
      nickname,
      logout,
      deleteAccount,
      isActive,
      showMenu,
      toggleMenu,
      isMobile
    }
  }
}
</script>

<style scoped>
nav {
  position: relative;
  top: 0;
  padding: 30px 0;
  width: 100%;
  height: auto;
  display: inline-block;
}
.nav-wrap {
  width: 1200px;
  margin: 0 auto;
  display: inline-flex;
  justify-content: space-between;
  align-items: center;
}
.nav-wrap .nav-menubar {
  margin-right: 24px;
  width: 90%;
}
.nav-wrap .nav-menubar .menubar-wrap {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.nav-main-logo {
  position: relative;
  width: 48px;
  height: 36px;
  margin-left: 24px;
}
.nav-main-logo .nav-logo {
  position: absolute;
  width: 100%;
  height: 100%;
  transform: translateX(-24px);
}

.nav-main-logo .nav-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.nav-wrap .nav-menubar .menubar-wrap a {
  text-align: center;
  width: 140px;
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
  cursor: pointer;
}

.nav-wrap .nav-menubar .menubar-wrap a.active-link {
  color: #07f;
}

.nav-wrap .nav-menubar .menubar-wrap button {
  width: 120px;
  text-align: center;
  font-weight: bold;
  background: none;
  border: none;
  color: #2c3e50;
  cursor: pointer;
}

.hamburger {
  display: none;
  font-size: 2em;
  color: #555;
  background: none;
  border: none;
  cursor: pointer;
}

.menu-visible {
  display: inline-flex;
}

.menu-hidden {
  display: none;
}

/* 반응형 스타일 */
@media (max-width: 1200px) {
  .nav-wrap {
    width: 992px;
  }
}

@media (max-width: 992px) {
  .nav-wrap {
    width: 768px;
  }

  .nav-wrap .nav-menubar .menubar-wrap a,
  .nav-wrap .nav-menubar .menubar-wrap button {
    font-size: 0.9em;
  }
}

@media (max-width: 768px) {
  nav {
    padding: 15px 0;
  }
  .nav-wrap {
    position: relative;
    width: 576px;
    padding: 15px;
  }
  .nav-wrap .nav-serve {
    width: 100%;
    display: flex;
    justify-content: space-between;
  }
  .nav-main-logo {
    margin-top: 10px;
    margin-left: 12px;
  }
  .nav-main-logo a {
    margin: 0 !important;
  }
  .nav-wrap a,
  .nav-wrap button {
    width: auto;
    text-align: center;
    margin: 10px 0;
  }

  .nav-wrap .nav-menubar {
    position: fixed;
    margin-top: 82px;
    width: 100%;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: #fff;
    z-index: 101;
  }
  .menubar-wrap {
    margin-top: 15px;
    display: flex;
    width: 100%;
    flex-direction: column;
    text-align: center;
  }
  .menubar-wrap a {
    margin: 10px auto;
  }
  .menu-visible {
    display: block;
  }

  .menu-hidden {
    display: none;
  }

  .hamburger {
    display: block;
  }
}

@media (max-width: 576px) {
  .nav-wrap {
    width: 100%;
    padding: 10px 0;
  }

  .nav-serve .nav-main-logo {
    margin-left: 24px;
  }
  .nav-serve .hamburger {
    margin-right: 18px;
  }

  .nav-wrap a,
  .nav-wrap button {
    font-size: 0.8em;
  }
}
</style>
