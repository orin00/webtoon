import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons'

// FontAwesome 아이콘 라이브러리에 아이콘 추가
library.add(faChevronLeft, faChevronRight)

// Vue 애플리케이션 생성 및 설정
createApp(App).component('font-awesome-icon', FontAwesomeIcon).use(store).use(router).mount('#app')
