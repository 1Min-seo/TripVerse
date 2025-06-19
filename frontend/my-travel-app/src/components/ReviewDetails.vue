<template>
  <div>
    <div class="review-detail-container" style="margin-left : 17%;">
      <div class="review-detail max-w-2xl mx-auto px-6 py-8 bg-white shadow-lg rounded-xl">
        <header class="mb-12">
          <button @click="goBack" class="text-gray-600 hover:text-custom transition-colors !rounded-button">
            <i class="fas fa-arrow-left text-xl"></i>
          </button>
          <div class="flex items-center space-x-4">
            <button @click="toggleScrap" class="scrap-button" style="margin-left: -10px; margin-top: -10px;">
              <i :class="['fa-bookmark', isScrapped ? 'fas text-yellow-500' : 'far text-gray-400']"></i>
            </button>
          </div>
        </header>

        <div class="flex justify-between items-center mb-4" style="margin-top:-20px">
          <div>
            <div class="form-group">
              <h2 id="title">{{ reviewDetails.title || '제목이 없습니다.' }}</h2>
            </div>
            <div class="flex items-center" style="margin-top: -10px;">
              <div class="flex items-center text-yellow-400">
                <i v-for="star in Math.floor(reviewDetails.rating || 0)" :key="star" class="fas fa-star"></i>
                <i v-if="(reviewDetails.rating || 0) % 1 !== 0" class="fas fa-star-half-alt"></i>
                <i v-for="emptyStar in 5 - Math.ceil(reviewDetails.rating || 0)" :key="emptyStar + 5" class="far fa-star"></i>
                <span class="ml-2 text-gray-600">{{ reviewDetails.rating.toFixed(1) || 0 }}/5.0</span>
              </div>
            </div>
          </div>
          <br>
          <div class="text-gray-500 text-sm flex items-center space-x-2">
            <span>{{ reviewDetails.userName || '알 수 없음' }}</span>
            <span style="margin-left: 20px">{{ formatDate(reviewDetails.createdDate) }}</span>
          </div>
        </div>
        <div class="mb-8">
          <div class="glide">
            <div class="glide__track" data-glide-el="track">
              <ul class="glide__slides">
                <li class="glide__slide" v-for="(photo, index) in reviewDetails.imageUrls" :key="index">
                  <img :src="photo" alt="Review Image" class="w-full h-[100px] object-cover rounded-lg" style="width: 100%; height: 700px; overflow: hidden; margin-top: 30px">
                </li>
              </ul>
            </div>
            <!-- Glide arrows -->
            <div class="glide__arrows custom-glide-arrows" data-glide-el="controls">
              <button class="glide__arrow glide__arrow--left" data-glide-dir="<">
                <i class="fas fa-chevron-left"></i>
              </button>
              <button class="glide__arrow glide__arrow--right" data-glide-dir=">">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
          </div>
        </div>

        <div class="form-group">
          <p id="content">{{ reviewDetails.content || '내용이 없습니다.' }}</p>
        </div>

        <div class="flex items-center justify-between pt-6 border-t border-gray-200">
          <div class="flex items-center space-x-4 w-full">
            <button @click="toggleHeart(reviewDetails.id)" class="like-button">
              <i :class="(heartDetails.isLiked ? 'fas' : 'far') + ' fa-heart text-red-500'"></i>
              <span :class="heartDetails.isLiked ? 'text-red-500' : 'text-gray-400'">
  {{ heartDetails.heartCount }} 좋아요
</span>
            </button>
          </div>

          <div v-if="isUser" class="flex space-x-4">
            <router-link :to="`/reviews/edit/${reviewDetails.id}`" class="edit-button">수정</router-link>
            <button @click="deleteReview" class="delete-button">삭제</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeMount, nextTick } from 'vue';
import axios from 'axios';
import Glide from '@glidejs/glide';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const reviewId = route.params.reviewId;

const photos = ref([]);

const reviewDetails = ref({
  id: null,
  title: '',
  content: '',
  rating: 0,
  authorId: null,
  imageUrls: [],
  createdDate: '',
});

const heartDetails = ref({
  reviewId: null,
  userId: null,
  isLiked: false,
  heartCount: 0
})

const isScrapped = ref(false);


const goBack = () => {
  router.go(-1);
};

const fetchReviewDetails = async () => {
  try {
    console.log(1)
    const {data} = await axios.get(
        `http://localhost:8080/api/reviews/${reviewId}`,
         { withCredentials: true }
    );
    console.log(2)
    reviewDetails.value = data;
    console.log(3)
    await nextTick();
  } catch (error) {
    console.error("리뷰 세부 정보를 가져오는 중 오류 발생:", error);
  }
};

const fetchHeartDetails = async() => {
  try {
    const { data } = await axios.get(
        `http://localhost:8080/api/hearts`,
        {
          params: { reviewId },
          withCredentials: true
        }
    );
    heartDetails.value = data;
  }catch (error) {
    console.error("좋아요 정보를 가져오는 중 오류 발생:", error);
  }
}

const toggleHeart = async () => {
  try {
    const {data} = await axios.post(
        `http://localhost:8080/api/hearts/toggle`,
        { reviewId },
        { withCredentials: true }
    );
    heartDetails.value = data;
  } catch (error) {
    console.error("하트 토글 중 오류 발생:", ㄱㅡerror);
  }
};

const updateReview = () => {
  router.push(`/review/edit/${reviewDetails.value.id}`);
};

const deleteReview = async () => {
  if (!confirm("정말로 삭제하시겠습니까?")) return;
  try {
    await axios.delete(`http://localhost:8080/api/reviews/delete/${reviewDetails.value.id}`, {
      withCredentials: true
    });
    alert("삭제되었습니다.");
    router.push('/mypage');
  } catch (error) {
    console.error("리뷰 삭제 중 오류:", error);
  }
};

const formatDate = (date) => {
  if (!date) return "작성일 정보 없음";
  try {
    return new Date(date).toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    return "날짜 형식 오류";
  }
};

const isUser = (userName) => {
  if (!userName) return "알수 없음";
  return userName;
}

onBeforeMount(() => {
  fetchReviewDetails();
  fetchHeartDetails();
});

const initGlide = () => {
  if (reviewDetails.value.imageUrls && reviewDetails.value.imageUrls.length > 0) {
    nextTick(() => {
      new Glide('.glide', {
        type: 'carousel',
        perView: 1,
        gap: 0,
        arrows: true,
      }).mount();
    });
  }
};
</script>

<!-- Google Fonts -->
<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">