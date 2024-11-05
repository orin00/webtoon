<template>
  <div class="footer">
    <div class="footer-wrap">
      <div class="footer-outer1">
        <div class="footer-inner1">
          <div class="footer-main-logo">
            <a href="/" class="footer-logo">
              <img src="/favicon.ico" alt="favicon" />
            </a>
          </div>
        </div>
        <div class="footer-inner2">
          <ul>
            <li>
              <a href="/">홈</a>
            </li>
            <li>
              <a href="/webtoon">요일웹툰</a>
            </li>
            <li>
              <a href="/complete">완결웹툰</a>
            </li>
          </ul>
        </div>
      </div>

      <hr />

      <div class="footer-outer2">
        <div class="footer-inner3">
          <p>github : <a href="https://github.com/orin00/">https://github.com/orin00/</a></p>
        </div>
        <div class="footer-inner4">
          <p>email : osc0319@naver.com</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  components: {},
  data () {
    return {
      sampleData: ''
    }
  },
  created () {},
  mounted () {},
  unmounted () {},
  methods: {}
}
</script>
<style scoped>
.footer {
  position: relative;
  margin-top: 60px;
  height: auto;
  background: #e5e5e9;
  color: #4a4e57;
}
.footer-wrap {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  font-size: 16px;
}
.footer-wrap .footer-outer1 {
  margin-top: 30px;
  width: 100%;
}
.footer-wrap .footer-outer2 {
  margin-bottom: 32px;
  width: 100%;
}
.footer-inner1 .footer-main-logo {
  position: relative;
  display: inline-flex;
  justify-content: center;
  width: 72px;
  height: 72px;
}
.footer-inner1 .footer-main-logo .footer-logo {
  position: absolute;
  width: 100%;
  height: 100%;
}
.footer-inner1 .footer-main-logo .footer-logo img {
  width: 100%;
  object-fit: contain;
}
.footer-inner2 {
  width: 991px;
  margin: 10px auto 0;
}
.footer-inner2 ul {
  margin: 0;
  width: 100%;
  display: flex;
  column-gap: 20px;
  justify-content: space-around;
}
.footer-inner2 ul li {
  width: 33.33%;
}
.footer-inner2 ul li a {
  text-decoration: none;
  color: #4a4e57;
  cursor: pointer;
}
.footer-inner2 ul li a:hover {
  border-bottom: 1px solid #4a4e57;
}
.footer-wrap hr {
  margin-top: 20px;
  background: #bbb;
  outline: none;
  border: none;
  height: 1px;
  width: 100%;
}
.footer-inner3 {
  margin-top: 14px;
  width: 100%;
}
.footer-inner3 a {
  text-decoration: none;
  color: #4a4e57;
  line-height: 21px;
  word-break: break-word;
}
.footer-inner3 a:hover {
  border-bottom: 1px solid #4a4e57;
}
.footer-inner4 {
  margin-top: 10px;
  width: 100%;
}
.footer-inner4 p {
  margin: 0;
  line-height: 21px;
  word-break: break-word;
}
@media (max-width: 1200px) {
  .footer-wrap {
    width: 991px;
  }
  .footer-inner2 {
    width: 768px;
  }
}
@media (max-width: 991px) {
  .footer-wrap {
    width: 768px;
  }
  .footer-inner2 {
    width: 595px;
  }
}
@media (max-width: 768px) {
  .footer-wrap {
    width: 595px;
  }
  .footer-inner2 {
    width: 480px;
  }
}
@media (max-width: 595px) {
  .footer-wrap {
    width: 480px;
  }
  .footer-wrap .footer-outer1 {
    display: flex;
    flex-direction: column;
  }
  .footer-inner2 {
    width: 100%;
  }
  .footer-inner2 ul {
    flex-direction: column;
    column-gap: 0;
    row-gap: 8px;
  }
  .footer-inner2 ul li {
    width: 100%;
    text-align: center;
  }
}
@media (max-width: 480px) {
  .footer-wrap {
    width: 100%;
    font-size: 14px;
  }
  .footer-wrap hr {
    width: 90%;
    margin: 20px auto 0;
  }
  .footer-inner1 .footer-main-logo {
    width: 54px;
    height: 54px;
  }
  .footer-inner3 a {
    line-height: 16px;
  }
  .footer-inner4 p {
    line-height: 16px;
  }
}
</style>
