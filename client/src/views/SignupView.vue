<template>
  <div class="register">
    <div class="register-wrap">
      <h3 class="register-title">회원 가입</h3>
      <form @submit.prevent="registerUser">
        <div class="form-floating">
          <label for="username">이름 : </label>
          <input v-model="form.username" type="text" id="username" required />
        </div>
        <div class="form-floating">
          <label for="nickname">닉네임 : </label>
          <input v-model="form.nickname" type="text" id="nickname" required />
        </div>
        <div class="form-floating">
          <label for="email">이메일 : </label>
          <input v-model="form.email" type="email" id="email" required />
        </div>
        <div class="form-floating">
          <label for="password">비밀번호 : </label>
          <input
            v-model="form.password"
            type="password"
            id="password"
            required
          />
        </div>
        <div class="form-floating">
          <label for="birthdate">생년월일 : </label>
          <input v-model="form.birthdate" type="date" id="birthdate" required />
        </div>
        <div class="form-floating">
          <label for="gender">성별 : </label>
          <select v-model="form.gender" id="gender" required>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
          </select>
        </div>
        <div class="form-floating">
          <label for="phoneNumber">휴대폰 번호 : </label>
          <input v-model="form.phoneNumber" type="text" id="phoneNumber" />
        </div>
        <div class="form-check">
          <label for="marketingConsent">마케팅 약관 동의 </label>
          <input
            v-model="form.marketingConsent"
            type="checkbox"
            id="marketingConsent"
          />
        </div>
        <div class="form-check">
          <label for="termsAgreed">이용 약관 동의 </label>
          <input
            v-model="form.termsAgreed"
            type="checkbox"
            id="termsAgreed"
            required
          />
        </div>
        <button class="btn" type="submit">회원가입</button>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data () {
    return {
      form: {
        username: '',
        nickname: '',
        email: '',
        password: '',
        birthdate: '',
        gender: '',
        phoneNumber: '',
        termsAgreed: false,
        marketingConsent: false
      }
    }
  },
  methods: {
    async registerUser () {
      try {
        const response = await axios.post(
          'http://localhost:8080/api/users/register',
          this.form
        )
        alert(response.data)
      } catch (error) {
        alert('An error occurred: ' + error.response.data)
      }
    }
  }
}
</script>
<style scoped>
.register {
  position: relative;
  width: 100%;
  height: 800px;
  display: flex;
  align-items: center;
}
.register-wrap {
  width: 400px;
  margin: 0 auto;
  padding: 40px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
}
.register .register-title {
  margin-bottom: 30px;
  text-align: center;
  font-size: 24px;
  color: #333;
}
.register .form-floating {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.register .form-check {
  margin: 20px auto;
  display: grid;
  width: 320px;
  grid-template-columns: repeat(2, auto);
  align-items: center;
}

label {
  min-width: 140px;
  margin-right: 5px;
  text-align: right;
  font-weight: bold;
}
.register .form-check label {
  min-width: auto;
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
select {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 4px;
}
#termsAgreed,
#marketingConsent {
  width: 15px;
  height: 15px;
  margin-left: 10px;
}

.register .btn {
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

.register .btn:hover {
  background-color: #0056b3;
}

@media (max-width: 768px) {
  .register-wrap {
    width: 360px;
    padding: 30px;
  }
  label {
    min-width: 100px;
  }
}

@media (max-width: 425px) {
  .register {
    width: auto;
  }
  .register-wrap {
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
  .register-wrap {
    margin: 0 10px;
    padding: 10px;
  }
  label {
    min-width: 90px;
  }
  .register .form-floating {
    justify-content: start;
    margin-left: 20px;
  }
  #username,
  #nickname,
  #email,
  #password,
  #birthdate,
  #gender,
  #phoneNumber {
    width: 180px;
  }
  .register .form-check {
    width: 260px;
  }
  .register .btn {
    width: 270px;
    margin: 20px auto 0;
  }
}
@media (max-width: 320px) {
  .register .form-floating {
    margin-left: 15px;
  }
  #username,
  #nickname,
  #email,
  #password,
  #birthdate,
  #gender,
  #phoneNumber {
    width: 150px;
  }
  .register .btn {
    width: 240px;
    margin: 20px auto 0;
  }
}
</style>
