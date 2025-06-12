<template>
  <section class="hero-section">
    <div class="hero-content">
      <div class="hero-text">
        <h1 class="hero-title">나만의 여행 계획을 만들어보세요</h1>
        <p class="hero-description">
          트래블러스의 다양한 기능으로 쉽고 빠르게 여행 계획을 세워보세요.
        </p>
        <router-link to="/createPlan" class="hero-button">지금 시작하기</router-link>
      </div>
      <div class="hero-image-wrapper">
<!--        <img class="hero-image" src="@/assets/hero-image.jpeg" alt="여행 아이템" />-->
      </div>
    </div>
  </section>
</template>

<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";

const name = ref(null)

const goToGoogleLogin = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google'
}

// 로그인 상태 확인
onMounted(() => {
  axios.get("http://localhost:8080/api/test", {
    withCredentials: true
  })
      .then(res => {
        name.value = res.data;
      })
      .catch(err => {
        console.log("로그인되지 않음");
      });
});

const logout = () => {
  // 쿠키에서 accessToken 삭제
  document.cookie = "accessToken=; max-age=0; path=/";

  // 서버로 로그아웃 요청
  axios.post("http://localhost:8080/api/logout", {}, { withCredentials: true })
      .then(() => {
        // 로그아웃 후 페이지 새로 고침
        name.value = null;
        window.location.reload();
      })
      .catch(err => {
        console.log("로그아웃 실패");
      });
}
</script>