<template>
  <div class="login">
    <div class="login-wrap">
      <h3 class="login-title">로그인</h3>
      <form @submit.prevent="submit">
        <div class="form-floating">
          <label for="email">이메일 : </label>
          <input
            type="email"
            class="form-control"
            id="email"
            placeholder="name@example.com"
            v-model="state.form.email"
            required
          />
        </div>
        <div class="form-floating">
          <label for="password">비밀번호 : </label>
          <input
            type="password"
            class="form-control"
            id="password"
            placeholder="Password"
            v-model="state.form.password"
            required
          />
        </div>
        <button class="btn" type="submit">로그인</button>
      </form>
    </div>
  </div>
</template>

<script>
import { reactive, onMounted, watch } from 'vue'
import axios from 'axios'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  setup () {
    const state = reactive({
      form: {
        email: '',
        password: ''
      }
    })

    const router = useRouter()
    const store = useStore()

    const submit = async () => {
      if (!state.form.email || !state.form.password) {
        alert('이메일 또는 비밀번호가 입력되지 않았습니다.')
        return
      }

      try {
        const response = await axios.post('/api/account/login', state.form)
        const account = response.data
        console.log('Login response:', account)
        store.commit('setAccount', account)
        sessionStorage.setItem('account', JSON.stringify(account))
        sessionStorage.setItem('email', account.email)
        sessionStorage.setItem('nickname', account.nickname)
        sessionStorage.setItem('role', account.role)

        const expirationTime = new Date().getTime() + 1000 * 60 * 60 * 2
        sessionStorage.setItem('expiration', expirationTime)

        // 콘솔에 세션에 저장된 이메일 정보 출력
        console.log('Session email:', sessionStorage.getItem('email'))

        const { isAdmin, isArtist } = await store.dispatch('checkRole')
        if (isAdmin) {
          router.push('/')
        } else if (isArtist) {
          router.push('/')
        } else {
          router.push('/')
        }
      } catch (error) {
        if (error.response && error.response.status === 401) {
          alert('잘못된 이메일 또는 비밀번호입니다.')
        } else {
          alert('오류가 발생했습니다: ' + error.message)
        }
      }
    }

    const checkSessionExpiration = () => {
      const expiration = sessionStorage.getItem('expiration')
      if (expiration && new Date().getTime() > expiration) {
        alert('세션이 만료되었습니다. 다시 로그인해주세요.')
        sessionStorage.removeItem('account')
        sessionStorage.removeItem('expiration')
        store.commit('clearState')
        router.push('/login')
      }
    }

    onMounted(() => {
      checkSessionExpiration()
    })

    watch(() => store.state.account, checkSessionExpiration) // 상태 변화 시 세션 만료 체크

    return { state, submit }
  }
}
</script>

<style scoped>
.login {
  position: relative;
  width: 100%;
  height: 600px;
  display: flex;
  align-items: center;
}

.login-wrap {
  width: 400px;
  margin: 0 auto;
  padding: 40px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
}

.login .login-title {
  margin-bottom: 30px;
  text-align: center;
  font-size: 24px;
  color: #333;
}

.login .form-floating {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

label {
  min-width: 140px;
  margin-right: 5px;
  text-align: right;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.login .btn {
  margin-top: 20px;
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  font-size: 16px;
}

.login .btn:hover {
  background-color: #0056b3;
}

@media (max-width: 768px) {
  .login-wrap {
    width: 360px;
    padding: 30px;
  }
  label {
    min-width: 100px;
  }
}

@media (max-width: 425px) {
  .login {
    width: auto;
  }
  .login-wrap {
    width: 100%;
    margin: 0 20px;
    padding: 20px;
  }
  label {
    min-width: 100px;
    font-size: 14px;
  }
}
@media (max-width: 375px) {
  .login-wrap {
    margin: 0 10px;
    padding: 10px;
  }
  label {
    min-width: 90px;
  }
  .login .form-floating {
    justify-content: start;
    margin-left: 20px;
  }
  #email,
  #password {
    width: 180px;
  }
  .login .btn {
    width: 270px;
    margin: 20px auto 0;
  }
}
@media (max-width: 320px) {
  .login .form-floating {
    margin-left: 15px;
  }
  #email,
  #password {
    width: 150px;
  }
  .login .btn {
    width: 240px;
    margin: 20px auto 0;
  }
}
</style>
