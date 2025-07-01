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
      <router-link to="/reviews/new" class="write-review-btn">글쓰기</router-link>

      <!-- 리뷰 그리드 -->
      <div class="items" id="reviews">
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

      <!-- 페이지네이션 -->
      <div class="pagination">
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
        url = `http://localhost:8080/api/mypage/planners?page=${currentPage.value}&size=3`;
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