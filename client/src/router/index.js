// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import store from '../store/index'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/webtoon',
    name: 'webtoon',
    component: () => import(/* webpackChunkName: "webtoon" */ '../views/WebtoonView.vue')
  },
  {
    path: '/webtoon/:artworkId/:serialDay',
    name: 'WebtoonListView',
    component: () => import(/* webpackChunkName: "webtoon" */ '../views/WebtoonListView.vue'),
    beforeEnter: (to, from, next) => {
      // sessionStorage에 값을 저장
      sessionStorage.setItem('artworkId', to.params.artworkId)
      sessionStorage.setItem('serialDay', to.params.serialDay)
      next()
    }
  },
  {
    path: '/webtoon/:artworkId/:serialDay/:episodeCount',
    name: 'WebtoonDetailView',
    component: () => import(/* webpackChunkName: "webtoon" */ '../views/WebtoonDetailView.vue'),
    beforeEnter: (to, from, next) => {
      // sessionStorage에 값을 저장
      sessionStorage.setItem('artworkId', to.params.artworkId)
      sessionStorage.setItem('episodeCount', to.params.episodeCount)
      next()
    }
  },
  {
    path: '/complete',
    name: 'complete',
    component: () => import(/* webpackChunkName: "complete" */ '../views/CompleteView.vue')
  },
  {
    path: '/mypage',
    name: 'mypage',
    component: () => import(/* webpackChunkName: "mypage" */ '../views/MypageView.vue')
  },
  {
    path: '/signup',
    name: 'signup',
    component: () => import(/* webpackChunkName: "signup" */ '../views/SignupView.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import(/* webpackChunkName: "login" */ '../views/LoginView.vue')
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import(/* webpackChunkName: "admin" */ '../views/AdminView.vue'),
    meta: { requiresRole: 'ADMIN' }
  },
  {
    path: '/artist',
    name: 'artist',
    component: () => import(/* webpackChunkName: "artist" */ '../views/ArtistView.vue'),
    meta: { requiresRole: 'ARTIST' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  // 사용자의 권한에 따른 라우터 설정 => 권한 없으면 index 페이지로
  if (to.matched.some(record => record.meta.requiresRole)) {
    const { isAdmin, isArtist } = await store.dispatch('checkRole')
    const requiredRole = to.meta.requiresRole

    if (requiredRole === 'ADMIN' && !isAdmin) {
      next('/')
    } else if (requiredRole === 'ARTIST' && !isArtist) {
      next('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
