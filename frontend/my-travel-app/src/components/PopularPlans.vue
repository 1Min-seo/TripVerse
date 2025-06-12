<template>
  <section class="popular-section">
    <div class="popular-container">
      <h2 class="popular-title">인기 여행 플랜</h2>
      <div class="plan-grid">
        <div class="plan-card" v-for="i in 4" :key="i">

          <div class="plan-content">
            <h3 class="plan-title">부산 2박 3일</h3>
            <p class="plan-description">해운대, 감천문화마을 등 알찬 여행 코스</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
</script>