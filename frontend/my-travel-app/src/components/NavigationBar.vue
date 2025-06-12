<template>
  <header class="navigation-bar">
    <div class="nav-container">
      <div class="logo">
        <router-link to ="/">TripVerse</router-link>
      </div>
      <input type="text" placeholder="여행지를 검색해보세요" class="search-input" />
      <nav class="nav-links">
        <a href="#">여행 플랜</a>
        <a href="#">인기 여행지</a>
        <a href="#">커뮤니티</a>
        <router-link to="/MyPage">마이페이지</router-link>
      </nav>
      <div class="auth-buttons">
        <!-- 로그인 후 이름을 표시 -->
        <div v-if="name">WELCOME {{name}}!</div>

        <!-- 로그인 상태에 따라 버튼 변경 -->
        <button v-if="!name" class="login-btn" @click="goToGoogleLogin">로그인</button>
        <button v-if="name" class="logout-btn" @click="logout">로그아웃</button>
      </div>
    </div>
  </header>
</template>

<script setup>
import axios from 'axios'
import { ref, onMounted } from 'vue'

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

<style scoped>

</style>