<template>
  <div id="app">
    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner"></div>
      <p class="loading-text">여행 플랜을 생성 중이에요...</p>
    </div>

    <main class="container">
      <div class="header">
        <h1 class="title">나만의 맞춤 여행 플래너</h1>
        <p class="subtitle">
          당신의 선호도를 바탕으로 AI가 최적의 여행 코스를 추천해드립니다. 아래 질문에 답해주세요.
        </p>
      </div>

      <div class="progress-container">
        <div class="progress-info">
          <div>
            <span class="progress-label">진행 상태</span>
            <div class="progress-step">
              <span class="current-step">{{ currentStep }}</span>
              <span class="step-divider">/</span>
              <span class="total-steps">{{ totalSteps }}</span>
            </div>
          </div>
          <div class="progress-bar-container">
            <div class="progress-bar" :style="{ width: progressPercentage + '%' }"></div>
          </div>
        </div>
      </div>

      <form @submit.prevent="submitForm">
        <div class="question-container">
          <div v-show="currentStep === 1" class="question-card">
            <h2 class="question-title">Q1. 선호하는 여행 스타일을 선택해주세요</h2>
            <div class="options-grid">
              <label class="option-card" :class="{ active: formData.travelStyle === 'nature' }">
                <input type="radio" v-model="formData.travelStyle" value="nature" />
                <div class="option-content">
                  <div class="icon-container nature">
                    <i class="ri-plant-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">자연 친화적인 여행</h3>
                    <p class="option-description">산, 바다, 숲 등 자연을 만끽할 수 있는 여행</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.travelStyle === 'city' }">
                <input type="radio" v-model="formData.travelStyle" value="city" />
                <div class="option-content">
                  <div class="icon-container city">
                    <i class="ri-building-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">도시 관광 중심 여행</h3>
                    <p class="option-description">쇼핑, 맛집, 도시 명소를 즐기는 여행</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.travelStyle === 'culture' }">
                <input type="radio" v-model="formData.travelStyle" value="culture" />
                <div class="option-content">
                  <div class="icon-container culture">
                    <i class="ri-ancient-pavilion-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">문화/역사 탐방 여행</h3>
                    <p class="option-description">박물관, 유적지, 문화 체험을 즐기는 여행</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.travelStyle === 'relaxation' }">
                <input type="radio" v-model="formData.travelStyle" value="relaxation" />
                <div class="option-content">
                  <div class="icon-container relaxation">
                    <i class="ri-hotel-bed-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">휴양/힐링 여행</h3>
                    <p class="option-description">스파, 리조트, 휴식을 취하는 여행</p>
                  </div>
                </div>
              </label>
            </div>
          </div>

          <div v-show="currentStep === 2" class="question-card">
            <h2 class="question-title">Q2. 희망하는 여행 기간을 선택해주세요</h2>
            <div class="options-grid">
              <label class="option-card" :class="{ active: formData.travelDuration === '1-2' }">
                <input type="radio" v-model="formData.travelDuration" value="1-2" />
                <div class="option-content">
                  <div class="icon-container duration1">
                    <i class="ri-calendar-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">1-2일</h3>
                    <p class="option-description">주말 여행이나 짧은 휴가</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.travelDuration === '3-4' }">
                <input type="radio" v-model="formData.travelDuration" value="3-4" />
                <div class="option-content">
                  <div class="icon-container duration2">
                    <i class="ri-calendar-2-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">3-4일</h3>
                    <p class="option-description">연휴나 짧은 연차 사용</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.travelDuration === '5-7' }">
                <input type="radio" v-model="formData.travelDuration" value="5-7" />
                <div class="option-content">
                  <div class="icon-container duration3">
                    <i class="ri-calendar-check-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">5-7일</h3>
                    <p class="option-description">일주일 정도의 휴가</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.travelDuration === '7+' }">
                <input type="radio" v-model="formData.travelDuration" value="7+" />
                <div class="option-content">
                  <div class="icon-container duration4">
                    <i class="ri-calendar-todo-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">7일 이상</h3>
                    <p class="option-description">장기 여행이나 휴가</p>
                  </div>
                </div>
              </label>
            </div>
          </div>

          <div v-show="currentStep === 3" class="question-card">
            <h2 class="question-title">Q3. 선호하는 이동 수단을 선택해주세요</h2>
            <div class="options-grid">
              <label class="option-card" :class="{ active: formData.transportation === 'public' }">
                <input type="radio" v-model="formData.transportation" value="public" />
                <div class="option-content">
                  <div class="icon-container transport1">
                    <i class="ri-train-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">대중교통</h3>
                    <p class="option-description">버스, 지하철, 기차 등을 이용</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.transportation === 'rental' }">
                <input type="radio" v-model="formData.transportation" value="rental" />
                <div class="option-content">
                  <div class="icon-container transport2">
                    <i class="ri-car-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">렌터카</h3>
                    <p class="option-description">자유롭게 이동 가능한 렌터카 이용</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.transportation === 'walking' }">
                <input type="radio" v-model="formData.transportation" value="walking" />
                <div class="option-content">
                  <div class="icon-container transport3">
                    <i class="ri-walk-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">도보/자전거</h3>
                    <p class="option-description">걷거나 자전거를 이용한 여행</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.transportation === 'package' }">
                <input type="radio" v-model="formData.transportation" value="package" />
                <div class="option-content">
                  <div class="icon-container transport4">
                    <i class="ri-bus-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">패키지 투어</h3>
                    <p class="option-description">가이드와 함께하는 패키지 여행</p>
                  </div>
                </div>
              </label>
            </div>
          </div>

          <div v-show="currentStep === 4" class="question-card">
            <h2 class="question-title">Q4. 1일 예상 여행 경비를 선택해주세요</h2>
            <div class="options-grid">
              <label class="option-card" :class="{ active: formData.budget === 'under-100k' }">
                <input type="radio" v-model="formData.budget" value="under-100k" />
                <div class="option-content">
                  <div class="icon-container budget1">
                    <i class="ri-money-dollar-circle-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">10만원 이하</h3>
                    <p class="option-description">경제적인 여행 선호</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.budget === '100k-200k' }">
                <input type="radio" v-model="formData.budget" value="100k-200k" />
                <div class="option-content">
                  <div class="icon-container budget2">
                    <i class="ri-money-dollar-circle-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">10-20만원</h3>
                    <p class="option-description">적절한 가성비 추구</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.budget === '200k-300k' }">
                <input type="radio" v-model="formData.budget" value="200k-300k" />
                <div class="option-content">
                  <div class="icon-container budget3">
                    <i class="ri-money-dollar-circle-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">20-30만원</h3>
                    <p class="option-description">편안한 여행 추구</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.budget === 'over-300k' }">
                <input type="radio" v-model="formData.budget" value="over-300k" />
                <div class="option-content">
                  <div class="icon-container budget4">
                    <i class="ri-money-dollar-circle-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">30만원 이상</h3>
                    <p class="option-description">럭셔리한 여행 선호</p>
                  </div>
                </div>
              </label>
            </div>
          </div>

          <div v-show="currentStep === 5" class="question-card">
            <h2 class="question-title">Q5. 선호하는 여행지 유형을 선택해주세요</h2>
            <div class="options-grid">
              <label class="option-card" :class="{ active: formData.destinationType === 'domestic' }">
                <input type="radio" v-model="formData.destinationType" value="domestic" />
                <div class="option-content">
                  <div class="icon-container destination1">
                    <i class="ri-map-pin-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">국내 여행</h3>
                    <p class="option-description">한국 내 다양한 지역 탐방</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.destinationType === 'asia' }">
                <input type="radio" v-model="formData.destinationType" value="asia" />
                <div class="option-content">
                  <div class="icon-container destination2">
                    <i class="ri-earth-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">아시아</h3>
                    <p class="option-description">일본, 대만, 태국, 베트남 등</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.destinationType === 'europe' }">
                <input type="radio" v-model="formData.destinationType" value="europe" />
                <div class="option-content">
                  <div class="icon-container destination3">
                    <i class="ri-earth-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">유럽</h3>
                    <p class="option-description">프랑스, 이탈리아, 스페인 등</p>
                  </div>
                </div>
              </label>
              <label class="option-card" :class="{ active: formData.destinationType === 'americas' }">
                <input type="radio" v-model="formData.destinationType" value="americas" />
                <div class="option-content">
                  <div class="icon-container destination4">
                    <i class="ri-earth-line"></i>
                  </div>
                  <div class="option-text">
                    <h3 class="option-title">미주/남미</h3>
                    <p class="option-description">미국, 캐나다, 멕시코 등</p>
                  </div>
                </div>
              </label>
            </div>
          </div>
        </div>

        <div v-show="currentStep === 6" class="question-card">
          <h2 class="question-title">Q6. 누구와 여행하시나요?</h2>
          <div class="options-grid">
            <label class="option-card" :class="{ active: formData.companion === 'solo' }">
              <input type="radio" v-model="formData.companion" value="solo" />
              <div class="option-content">
                <div class="icon-container companion-solo">
                  <i class="ri-user-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">혼자</h3>
                  <p class="option-description">나만의 온전한 시간을 보내는 여행</p>
                </div>
              </div>
            </label>
            <label class="option-card" :class="{ active: formData.companion === 'friends' }">
              <input type="radio" v-model="formData.companion" value="friends" />
              <div class="option-content">
                <div class="icon-container companion-friends">
                  <i class="ri-group-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">친구와</h3>
                  <p class="option-description">친구들과 함께 즐거운 추억을 만드는 여행</p>
                </div>
              </div>
            </label>
            <label class="option-card" :class="{ active: formData.companion === 'family' }">
              <input type="radio" v-model="formData.companion" value="family" />
              <div class="option-content">
                <div class="icon-container companion-family">
                  <i class="ri-home-heart-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">가족과</h3>
                  <p class="option-description">사랑하는 가족과 소중한 시간을 보내는 여행</p>
                </div>
              </div>
            </label>
            <label class="option-card" :class="{ active: formData.companion === 'couple' }">
              <input type="radio" v-model="formData.companion" value="couple" />
              <div class="option-content">
                <div class="icon-container companion-couple">
                  <i class="ri-heart-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">연인과</h3>
                  <p class="option-description">특별한 사람과 로맨틱한 순간을 만드는 여행</p>
                </div>
              </div>
            </label>
          </div>
        </div>

        <div v-show="currentStep === 7" class="question-card">
          <h2 class="question-title">Q7. 여행에서 가장 중요하게 생각하는 것은?</h2>
          <div class="options-grid">
            <label class="option-card" :class="{ active: formData.priority === 'rest' }">
              <input type="radio" v-model="formData.priority" value="rest" />
              <div class="option-content">
                <div class="icon-container priority-rest">
                  <i class="ri-spa-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">휴식</h3>
                  <p class="option-description">편안하고 여유로운 휴식을 통한 재충전</p>
                </div>
              </div>
            </label>
            <label class="option-card" :class="{ active: formData.priority === 'food' }">
              <input type="radio" v-model="formData.priority" value="food" />
              <div class="option-content">
                <div class="icon-container priority-food">
                  <i class="ri-restaurant-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">맛집 탐방</h3>
                  <p class="option-description">현지 음식과 다양한 미식 경험</p>
                </div>
              </div>
            </label>
            <label class="option-card" :class="{ active: formData.priority === 'sightseeing' }">
              <input type="radio" v-model="formData.priority" value="sightseeing" />
              <div class="option-content">
                <div class="icon-container priority-sightseeing">
                  <i class="ri-landscape-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">관광 명소</h3>
                  <p class="option-description">유명 관광지 방문 및 포토존</p>
                </div>
              </div>
            </label>
            <label class="option-card" :class="{ active: formData.priority === 'activity' }">
              <input type="radio" v-model="formData.priority" value="activity" />
              <div class="option-content">
                <div class="icon-container priority-activity">
                  <i class="ri-bicycle-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">액티비티</h3>
                  <p class="option-description">익스트림 스포츠, 체험 등 활동적인 경험</p>
                </div>
              </div>
            </label>
          </div>
        </div>


        <div v-show="currentStep === 8" class="question-card">
          <h2 class="question-title">Q8. 하루 일정을 어느 정도로 채우고 싶으신가요?</h2>
          <div class="options-grid">
            <label class="option-card" :class="{ active: formData.dailyPace === 'relaxed' }">
              <input type="radio" v-model="formData.dailyPace" value="relaxed" />
              <div class="option-content">
                <div class="icon-container pace-relaxed">
                  <i class="ri-time-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">느긋하게</h3>
                  <p class="option-description">휴식 위주, 여유로운 일정</p>
                </div>
              </div>
            </label>
            <label class="option-card" :class="{ active: formData.dailyPace === 'medium' }">
              <input type="radio" v-model="formData.dailyPace" value="medium" />
              <div class="option-content">
                <div class="icon-container pace-medium">
                  <i class="ri-timer-2-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">적당히</h3>
                  <p class="option-description">관광과 휴식의 균형 잡힌 일정</p>
                </div>
              </div>
            </label>
            <label class="option-card" :class="{ active: formData.dailyPace === 'tight' }">
              <input type="radio" v-model="formData.dailyPace" value="tight" />
              <div class="option-content">
                <div class="icon-container pace-tight">
                  <i class="ri-timer-flash-line"></i>
                </div>
                <div class="option-text">
                  <h3 class="option-title">빡빡하게</h3>
                  <p class="option-description">다양한 장소 방문, 활동적인 일정</p>
                </div>
              </div>
            </label>
          </div>
        </div>


        <div class="button-container">
          <button
              v-if="currentStep > 1"
              type="button"
              @click="prevStep"
              class="btn btn-secondary"
          >
            이전 질문
          </button>
          <button
              v-if="currentStep < totalSteps"
              type="button"
              @click="nextStep"
              class="btn btn-primary"
              :class="{ 'ml-auto': currentStep === 1 }"
          >
            다음 질문
          </button>
          <button
              v-if="currentStep === totalSteps"
              type="submit"
              class="btn btn-primary"
          >
            여행 플랜 생성하기
          </button>
        </div>
      </form>
    </main>
  </div>
</template>

<script>
import axios from 'axios';
import router from "@/router/index.js";
export default {
  name: 'TravelTestQuestions',
  data() {
    return {
      isLoading: false,
      currentStep: 1,
      totalSteps: 8,
      formData: {
        travelStyle: '',
        travelDuration: '',
        transportation: '',
        budget: '',
        destinationType: '',
        companion: '',
        priority: '',
        dailyPace: ''
      }
    }
  },

  computed: {
    progressPercentage() {
      return (this.currentStep / this.totalSteps) * 100;
    }
  },
  methods: {
    nextStep() {
      const currentQuestionKey = Object.keys(this.formData)[this.currentStep - 1];
      if (!this.formData[currentQuestionKey]) {
        alert('답변을 선택해주세요!');
        return;
      }

      if (this.currentStep < this.totalSteps) {
        this.currentStep++;
        console.log("현재 currentStep 값:", this.currentStep, typeof this.currentStep)
      }
    },
    prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--;
      }
    },
    async submitForm() {
      const lastQuestionKey = Object.keys(this.formData)[this.totalSteps - 1];
      if (!this.formData[lastQuestionKey]) {
        alert('답변을 선택해주세요!');
        return;
      }

      this.isLoading = true;
      try {
        console.log('Form submitted:', this.formData);

        // 1. 선호도 저장
        const saveResponse = await axios.post(
            `http://localhost:8080/api/travel-preferences`,
            this.formData
        );
        console.log('선호도 저장 응답:', saveResponse.data);

        const preferenceId = saveResponse.data.preferenceId;
        console.log('preferenceId: ' + preferenceId);

        // 2. AI 추천 생성
        const recommendationResponse = await axios.post(
            `http://localhost:8080/api/travel-preferences/${preferenceId}/recommendation`,
            this.formData
        );

        console.log("AI 추천 생성 응답:", recommendationResponse.data);

        // 3. 성공 메시지 및 페이지 이동
        alert('여행 플랜이 성공적으로 생성되었습니다!');
        await this.$router.push(`/travel-preference/${preferenceId}/recommendation`);

      } catch (e) {
        console.error('오류 발생:', e);
        if (e.response && e.response.data) {
          alert('오류가 발생했습니다: ' + (e.response.data.message || e.response.data));
        } else {
          alert('오류가 발생했습니다. 네트워크 연결을 확인해주세요.');
        }
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>