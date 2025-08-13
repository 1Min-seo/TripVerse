<template>
  <div class="mypage-wrapper">
    <!-- 왼쪽: 사이드 메뉴 + 프로필 -->
    <div class="side-panel" style="width: 180px;">
      <div class="side-menu-profile">
        <img :src="profileImage" alt="Profile Photo" />
        <p class="profile-name">{{ userName }}</p>
        <p class="profile-email">{{ userEmail }}</p>
      </div>

      <nav class="side-menu-nav">
        <a href="#" @click.prevent="selectMenu('작성한 글')" :class="{ active: selectedMenu === '작성한 글' }">
          <i class="icon">📄</i> 내가 작성한 글
        </a>
        <a href="#" @click.prevent="selectMenu('좋아요 누른 글')" :class="{ active: selectedMenu === '좋아요 누른 글' }">
          <i class="icon">❤️</i> 좋아요 누른 글
        </a>
        <a href="#" @click.prevent="selectMenu('스크랩한 글')" :class="{ active: selectedMenu === '스크랩한 글' }">
          <i class="icon">📑</i> 스크랩한 글
        </a>
        <a href="#" @click.prevent="selectMenu('여행 플래너')" :class="{ active: selectedMenu === '여행 플래너' }">
          <i class="icon">🌍</i> 내 여행 플래너
        </a>
      </nav>
    </div>

    <main class="profile-section" style="margin-top: -290px">
      <router-link to="/reviews/new" class="write-review-btn" v-if="selectedMenu !== '여행 플래너'">글쓰기</router-link>

      <!-- 여행 플래너 섹션 -->
      <div v-if="selectedMenu === '여행 플래너'" class="travel-planner-section">
        <div class="section-header">
          <h2>🌍 내 여행 플래너</h2>
          <p>나만의 여행 취향과 계획을 확인해보세요</p>
        </div>

        <div class="travel-cards" v-if="travelPreferences && travelPreferences.length > 0">
          <div
              v-for="(preference, index) in travelPreferences"
              :key="index"
              class="travel-preference-card"
          >
            <div class="card-header">
              <div class="travel-icon">✈️</div>
              <h3>여행 계획 {{ index + 1 }}</h3>
            </div>

            <div class="preference-details">
              <div class="preference-item">
                <div class="preference-label">
                  <span class="icon">🎨</span>
                  여행 스타일
                </div>
                <div class="preference-value">{{ preference.travelStyle }}</div>
              </div>

              <div class="preference-item">
                <div class="preference-label">
                  <span class="icon">📍</span>
                  목적지 유형
                </div>
                <div class="preference-value">{{ preference.destinationType }}</div>
              </div>

              <div class="preference-item">
                <div class="preference-label">
                  <span class="icon">⏰</span>
                  여행 기간
                </div>
                <div class="preference-value">{{ preference.travelDuration }}</div>
              </div>
            </div>

            <div class="card-footer">
              <button class="edit-btn">수정</button>
              <button class="plan-btn">계획 세우기</button>
            </div>
          </div>
        </div>

        <div v-else class="no-travel-data">
          <div class="empty-state">
            <div class="empty-icon">🗺️</div>
            <h3>아직 여행 계획이 없어요</h3>
            <p>첫 번째 여행 계획을 세워보세요!</p>
            <button class="create-plan-btn">여행 계획 만들기</button>
          </div>
        </div>
      </div>

      <!-- 기존 리뷰 그리드 (여행 플래너가 아닐 때만 보임) -->
      <div class="items" id="reviews" v-else>
        <template v-if="userReviews && userReviews.length > 0">
          <div
              v-for="(review, index) in userReviews"
              :key="index"
              class="review-card"
              @click="goToReviewDetails(review.id)"
          >
            <img
                :src="review.imageUrls && review.imageUrls.length > 0 ? getPhotoUrl(review.imageUrls[0]) : noImage"
                alt="Review Image"
                class="review-image"
            />
            <div class="review-info">
              <h3>{{ review.title }}</h3>
              <div class="post-info">
                <div class="post-date">{{ formatDate(review.createdAt) }}</div>
                <div class="post-stats">
                  <div class="stat-group">
                    <span>❤️ {{ review.heartCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <div class="pagination" v-if="selectedMenu !== '여행 플래너'">
        <button class="pagination-button" :disabled="currentPage === 0" @click="changePage('prev')">이전</button>
        <span>{{ currentPage + 1 }} / {{ totalPages || 1 }}</span>
        <button class="pagination-button" :disabled="currentPage >= totalPages - 1" @click="changePage('next')">다음</button>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import noImage from '@/assets/images/no-image.png';

export default {
  setup() {
    const router = useRouter();

    const userName = ref();
    const userEmail = ref();
    const profileImage = ref(null);
    const selectedMenu = ref('작성한 글');

    const userReviews = ref([]);
    const travelPreferences = ref([]); // 여행 플래너 데이터
    const currentPage = ref(0);
    const totalPages = ref(0);
    const searchTerm = ref('');
    const sortOption = ref('latest');

    const selectMenu = (menuName) => {
      selectedMenu.value = menuName;
    };

    const fetchUserInfo = () => {
      axios
          .get('http://localhost:8080/api/user/info', { withCredentials: true })
          .then(response => {
            const data = response.data;
            userName.value = data.name;
            userEmail.value = data.email;
            profileImage.value = data.profileImageUrl;
          })
          .catch(error => {
            console.error('사용자 정보 가져오기 실패:', error);
          });
    };

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    };

    const getPhotoUrl = (fileName) => {
      return fileName || noImage;
    };

    const goToReviewDetails = (reviewId) => {
      router.push({ name: 'ReviewDetails', params: { reviewId } });
    };

    const fetchUserReviews = async () => {
      let url = '';

      if (selectedMenu.value === '작성한 글') {
        url = `http://localhost:8080/api/mypage/reviews?page=${currentPage.value}&size=3&sort=${sortOption.value}&search=${searchTerm.value}`;
      } else if (selectedMenu.value === '좋아요 누른 글') {
        url = `http://localhost:8080/api/mypage/likes?page=${currentPage.value}`;
        console.log(url);
      } else if (selectedMenu.value === '스크랩한 글') {
        url = `http://localhost:8080/api/mypage/scrapped-reviews?page=${currentPage.value}&size=3`;
      } else if (selectedMenu.value === '여행 플래너') {
        await fetchTravelPreferences();
        return;
      }

      try {
        const response = await axios.get(url, { withCredentials: true });

        if (selectedMenu.value === '좋아요 누른 글') {
          userReviews.value = response.data || [];
          totalPages.value = 1;
        } else {
          const { content, totalPages: total } = response.data;
          userReviews.value = content || [];
          totalPages.value = total || 1;
        }

        await fetchHeartDetails();
      } catch (error) {
        console.error('데이터 불러오기 실패:', error);
        userReviews.value = [];
        totalPages.value = 1;
      }
    };

    const fetchHeartDetails = async () => {
      const requests = userReviews.value.map(async (review) => {
        try {
          const {data} = await axios.get('http://localhost:8080/api/hearts', {
            params: {reviewId: review.id},
            withCredentials: true
          });
          review.heartCount = data.heartCount;
          console.log(`리뷰 ID ${review.id}의 좋아요 개수: ${review.heartCount}`)
        } catch (error) {
          console.error(`리뷰 ID ${review.id}의 좋아요 정보 불러오기 실패:`, error);
          review.heartCount = 0;
        }
      });
      await Promise.all(requests);
    };

    const fetchTravelPreferences = async () => {
      try {
        const url = `http://localhost:8080/api/mypage/travel-preference?page=${currentPage.value}`;
        const response = await axios.get(url, { withCredentials: true });

        travelPreferences.value = response.data || [];

        for(const index in response.data) {
          console.log(response.data[index].travelStyle)
          console.log(response.data[index].destinationType)
          console.log(response.data[index].travelDuration)
        }

        console.log('여행 플래너 데이터:', response.data[0]);
      } catch (error) {
        console.error('여행 플래너 데이터 불러오기 실패:', error);
        travelPreferences.value = [];
        totalPages.value = 1;
      }
    };

    const changePage = (direction) => {
      if (direction === 'prev' && currentPage.value > 0) {
        currentPage.value -= 1;
      } else if (direction === 'next' && currentPage.value < totalPages.value - 1) {
        currentPage.value += 1;
      }
      fetchUserReviews();
    };

    const searchPosts = () => {
      currentPage.value = 0;
      fetchUserReviews();
    };

    watch(sortOption, () => {
      currentPage.value = 0;
      fetchUserReviews();
    });

    watch(selectedMenu, () => {
      currentPage.value = 0;
      fetchUserReviews();
    });

    onMounted(() => {
      fetchUserInfo();
      fetchUserReviews();
    });

    return {
      userName,
      userEmail,
      profileImage,
      userReviews,
      travelPreferences,
      currentPage,
      totalPages,
      searchTerm,
      sortOption,
      noImage,
      getPhotoUrl,
      goToReviewDetails,
      changePage,
      searchPosts,
      formatDate,
      selectMenu,
      selectedMenu
    };
  }
};
</script>

<style scoped>
.travel-planner-section {
  width: 100%;
  padding: 20px;
}

.section-header {
  margin-bottom: 32px;
}

.section-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.section-header p {
  font-size: 14px;
  color: #6c757d;
}

.travel-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.travel-preference-card {
  background: #ffffff;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.2s ease;
}

.travel-preference-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f8f9fa;
}

.travel-icon {
  font-size: 20px;
  margin-right: 10px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #343a40;
  margin: 0;
}

.preference-details {
  margin-bottom: 20px;
}

.preference-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 12px 0;
}

.preference-item:not(:last-child) {
  border-bottom: 1px solid #f8f9fa;
}

.preference-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 500;
  color: #6c757d;
}

.preference-label .icon {
  margin-right: 6px;
  font-size: 14px;
}

.preference-value {
  font-size: 14px;
  font-weight: 500;
  color: #495057;
  background: #f8f9fa;
  padding: 4px 12px;
  border-radius: 6px;
}

.card-footer {
  display: flex;
  gap: 8px;
}

.edit-btn, .plan-btn {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-btn {
  background: #ffffff;
  color: #6c757d;
}

.edit-btn:hover {
  background: #f8f9fa;
  border-color: #adb5bd;
}

.plan-btn {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.plan-btn:hover {
  background: #0056b3;
  border-color: #0056b3;
}

/* 빈 상태 스타일 */
.no-travel-data {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state h3 {
  font-size: 18px;
  color: #495057;
  margin-bottom: 8px;
  font-weight: 500;
}

.empty-state p {
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 20px;
}

.create-plan-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.create-plan-btn:hover {
  background: #0056b3;
}
</style>