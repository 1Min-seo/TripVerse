<template>
  <div class="app-container">
    <main class="main-content">
      <section id="start-page" class="start-section">
        <div class="container">
          <div class="card-wrapper">
            <div class="card-content">
              <div class="text-content">
                <h1 class="title">
                  AI 여행 추천 테스트
                </h1>
                <p class="description">
                  몇 가지 질문에 답하고 당신의 성향에 맞는 최적의 여행지를 발견하세요.
                  AI가 당신의 취향과 선호도를 분석하여 맞춤형 여행 경험을 추천해 드립니다.
                </p>
                <div class="button-group">
                  <button
                      @click="startTest"
                      class="start-button"
                  >
                    테스트 시작하기
                  </button>
                  <div class="info-text">
                    <div class="icon-wrapper">
                      <i class="ri-time-line"></i>
                    </div>
                    소요 시간: 약 3분
                  </div>
                  <div class="info-text">
                    <div class="icon-wrapper">
                      <i class="ri-question-line"></i>
                    </div>
                    총 8개의 질문
                  </div>
                  </div>
              </div>
              <div class="image-content">
                <img
                    :src="travelImage"
                    alt="여행 테스트 이미지"
                    class="hero-image"
                >
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
export default {
  name: 'TravelTest',
  data() {
    return {
      travelImage: 'https://readdy.ai/api/search-image?query=Travel%20concept%20with%20airplane%20flying%20over%20beautiful%20diverse%20landscapes%20including%20beach%2C%20mountains%2C%20city%20skyline%2C%20and%20countryside.%20Vibrant%20colors%2C%20dreamy%20atmosphere%2C%20soft%20lighting%2C%20travel%20inspiration%2C%20wanderlust%2C%20adventure%2C%20exploration%20theme&width=600&height=600&seq=1&orientation=portrait'
    }
  },
  methods: {
    startTest() {
      this.$router.push('/travel/test/questions')
      console.log('테스트 시작!');
    }
  },
}
</script>

<style scoped>
</style>