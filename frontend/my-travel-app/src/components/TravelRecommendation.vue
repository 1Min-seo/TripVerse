<template>
  <div class="container">
    <div class="card-wrapper">
      <div class="card-content-area">
        <div v-if="isLoading" class="loading-container">
          <div class="loading-spinner">
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
          </div>
          <p class="loading-text">맞춤 여행 추천을 생성하고 있습니다...</p>
        </div>

        <div v-else-if="error" class="error-container">
          <div class="error-icon">
            <i class="ri-emotion-sad-line"></i>
          </div>
          <h3 class="error-title">추천 생성에 실패했습니다</h3>
          <p class="error-message">{{ error }}</p>
          <button @click="fetchRecommendation" class="retry-button">
            <i class="ri-refresh-line mr-2"></i>
            다시 시도하기
          </button>
        </div>

        <div v-else class="recommendation-container">
          <div class="recommendation-header">
            <div class="header-content">
              <div class="title-section">
                <h1 class="recommendation-title">
                  <i class="ri-map-pin-line title-icon"></i>
                  {{ recommendationTitle }}
                </h1>
                <div class="preference-summary">
                  <i class="ri-user-line summary-icon"></i>
                  {{ preferenceSummary }}
                </div>
              </div>
              <div class="action-buttons">
                <button @click="toggleBookmark" class="action-btn bookmark-btn" :class="{ 'bookmarked': isBookmarked }">
                  <i :class="bookmarkIconClass"></i>
                </button>
                <button @click="copyRecommendation" class="action-btn copy-btn">
                  <i :class="copyIconClass"></i>
                </button>
                <button @click="shareRecommendation" class="action-btn share-btn">
                  <i :class="shareIconClass"></i>
                </button>
              </div>
            </div>
          </div>

          <div class="daily-plans-section">
            <h2 class="section-title">
              <i class="ri-calendar-line mr-2"></i>
              여행 일정
            </h2>
            <div class="daily-plans-grid">
              <div
                  v-for="(plan, index) in dailyPlans"
                  :key="index"
                  class="daily-plan-card"
                  :class="getDayCardClass(index)"
              >
                <div class="plan-header">
                  <div class="day-badge" :class="getDayBadgeClass(index)">
                    <span class="day-number">{{ index + 1 }}</span>
                  </div>
                  <h3 class="plan-title">{{ plan.title }}</h3>
                </div>
                <div class="plan-content">
                  <p class="plan-description" v-html="formatContent(plan.content)"></p>
                </div>
                <div class="plan-decoration" :class="getDecorationClass(index)"></div>
              </div>
            </div>
          </div>

          <div v-if="travelTips.length > 0" class="tips-section">
            <h2 class="section-title">
              <i class="ri-lightbulb-line mr-2"></i>
              여행 팁
            </h2>
            <div class="tips-container">
              <div
                  v-for="(tip, index) in travelTips"
                  :key="index"
                  class="tip-card"
              >
                <div class="tip-icon">
                  <i :class="getTipIcon(index)"></i>
                </div>
                <p class="tip-content" v-html="formatContent(tip)"></p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'TravelRecommendation',
  props: {
    preferenceId: {
      type: [String, Number],
      required: false
    }
  },
  data() {
    return {
      isLoading: true,
      error: null,
      recommendationTitle: '여행 추천',
      dailyPlans: [],
      preferenceSummary: '',
      travelTips: [],
      rawAiResponse: '',
      isBookmarked: false,
      copyIconClass: 'ri-file-copy-line',
      shareIconClass: 'ri-share-line',
      newRecommendationIconClass: 'ri-refresh-line',
      bookmarkIconClass: 'ri-bookmark-line'
    }
  },
  computed: {
    currentPreferenceId() {
      return this.preferenceId || this.$route.params.preferenceId
    }
  },
  mounted() {
    console.log('TravelRecommendation mounted, preferenceId:', this.currentPreferenceId)
    this.fetchRecommendation()
  },
  methods: {
    async fetchRecommendation() {
      if (!this.currentPreferenceId) {
        this.error = '추천 ID가 없습니다.'
        this.isLoading = false
        return
      }

      this.isLoading = true
      this.error = null

      try {
        console.log('API 호출 시작:', `http://localhost:8080/travel-preferences/${this.currentPreferenceId}/recommendation`)

        const response = await axios.get(
            `http://localhost:8080/api/travel-preferences/${this.currentPreferenceId}/recommendation`,
            {
              headers: {
                'Content-Type': 'application/json'
              }
            }
        )

        console.log('API 응답:', response.data)

        const data = response.data

        if (!data) {
          throw new Error('응답 데이터가 없습니다.')
        }

        const aiRecommendation = data.aiRecommendation
        console.log('AI 추천 텍스트:', aiRecommendation)

        if (!aiRecommendation) {
          throw new Error('AI 추천 데이터가 없습니다.')
        }

        this.rawAiResponse = aiRecommendation
        this.dailyPlans = this.parseDailyPlans(aiRecommendation)
        this.travelTips = this.parseTravelTips(aiRecommendation)
        this.preferenceSummary = this.makePreferenceSummary(data)
        this.isBookmarked = data.isBookmarked || false
        this.recommendationTitle = this.extractTitle(aiRecommendation) || '맞춤 여행 추천'

        console.log('파싱된 일차별 계획:', this.dailyPlans)
        console.log('파싱된 여행 팁:', this.travelTips)

      } catch (error) {
        console.error('추천 정보 불러오기 실패:', error)
        this.error = error.response?.data?.message || error.message || '추천 정보를 불러오는 데 실패했습니다.'
      } finally {
        this.isLoading = false
      }
    },

    extractTitle(text) {
      const titleMatch = text.match(/(.+?)\s*여행\s*계획/i)
      return titleMatch ? titleMatch[0].trim() : null
    },

    parseDailyPlans(text) {
      const result = []
      const dayMatches = text.match(/(\d+)일차[:\s]*(.*?)(?=\d+일차[:\s]*|---\s*추가\s*팁|$)/gs)

      if (dayMatches) {
        dayMatches.forEach(match => {
          const dayMatch = match.match(/(\d+)일차[:\s]*(.*)/s)
          if (dayMatch) {
            result.push({
              title: `${dayMatch[1]}일차`,
              content: dayMatch[2].trim()
            })
          }
        })
      }

      if (result.length === 0) {
        const lines = text.split('\n')
        let currentDay = null
        let currentContent = []

        lines.forEach(line => {
          const dayMatch = line.match(/(\d+)일차/)
          if (dayMatch) {
            if (currentDay) {
              result.push({
                title: currentDay,
                content: currentContent.join('\n').trim()
              })
            }
            currentDay = `${dayMatch[1]}일차`
            currentContent = []
          } else if (currentDay && line.trim()) {
            currentContent.push(line)
          }
        })

        if (currentDay) {
          result.push({
            title: currentDay,
            content: currentContent.join('\n').trim()
          })
        }
      }

      return result
    },

    parseTravelTips(text) {
      const tipsMatch = text.match(/---\s*추가\s*팁\s*---(.*?)$/s)
      if (!tipsMatch) return []

      const tipsBlock = tipsMatch[1]
      return tipsBlock
          .split(/\n/)
          .map(line => line.replace(/^[-•*]\s*/, '').trim())
          .filter(line => line.length > 2)
    },

    makePreferenceSummary(data) {
      const styleMap = {
        'nature': '자연 친화적',
        'city': '도시 관광',
        'culture': '문화/역사',
        'relaxation': '휴양/힐링'
      }

      const durationMap = {
        '1-2': '1-2일',
        '3-4': '3-4일',
        '5-7': '5-7일',
        '7+': '7일 이상'
      }

      const budgetMap = {
        'under-100k': '10만원 이하',
        '100k-200k': '10-20만원',
        '200k-300k': '20-30만원',
        'over-300k': '30만원 이상'
      }

      const companionMap = {
        'solo': '혼자',
        'friends': '친구와',
        'family': '가족과',
        'couple': '연인과'
      }

      const priorityMap = {
        'rest': '휴식',
        'food': '맛집 탐방',
        'sightseeing': '관광 명소',
        'activity': '액티비티'
      }

      return `${styleMap[data.travelStyle] || data.travelStyle} 스타일, ${durationMap[data.travelDuration] || data.travelDuration} 일정, ${budgetMap[data.budget] || data.budget} 예산, ${companionMap[data.companion] || data.companion} 동행, ${priorityMap[data.priority] || data.priority} 중시`
    },

    getDayCardClass(index) {
      const classes = ['day-card-blue', 'day-card-green', 'day-card-purple', 'day-card-orange', 'day-card-pink', 'day-card-cyan']
      return classes[index % classes.length]
    },

    getDayBadgeClass(index) {
      const classes = ['badge-blue', 'badge-green', 'badge-purple', 'badge-orange', 'badge-pink', 'badge-cyan']
      return classes[index % classes.length]
    },

    getDecorationClass(index) {
      const classes = ['decoration-blue', 'decoration-green', 'decoration-purple', 'decoration-orange', 'decoration-pink', 'decoration-cyan']
      return classes[index % classes.length]
    },

    getTipIcon(index) {
      const icons = ['ri-compass-line', 'ri-restaurant-line', 'ri-camera-line', 'ri-map-line', 'ri-time-line', 'ri-money-dollar-circle-line']
      return icons[index % icons.length]
    },

    toggleBookmark() {
      this.isBookmarked = !this.isBookmarked
      this.bookmarkIconClass = this.isBookmarked ? 'ri-bookmark-fill' : 'ri-bookmark-line'
    },

    copyRecommendation() {
      const combined = this.dailyPlans.map(d => `${d.title}: ${d.content}`).join('\n\n')
      navigator.clipboard.writeText(combined)
      this.showNotification('추천 일정이 복사되었습니다!')
    },

    shareRecommendation() {
      const currentUrl = window.location.href;
      const shareText = `${this.recommendationTitle} - ${this.preferenceSummary}에 맞는 여행 추천을 확인해보세요!`;

      if (navigator.share) {
        navigator.share({
          title: this.recommendationTitle,
          text: shareText,
          url: currentUrl
        }).then(() => {
          console.log('공유 성공');
        }).catch((error) => {
          console.error('공유 실패:', error);
          this.fallbackToClipboard(currentUrl);
        });
      } else {
        this.fallbackToClipboard(currentUrl);
      }
    },


    showNotification(message) {

      alert(message)
    },

    formatContent(content) {
      if (!content) return ''

      return content.replace(/\*\*(.*?)\*\*/g, '<strong class="highlight-text">$1</strong>')
    }
  }
}
</script>