<template>
  <section class="popular-section">
    <div class="popular-container">
      <h2 class="popular-title">인기 여행 플랜</h2>
      <div class="plan-grid">
        <div class="plan-card" v-for="review in topReviews" :key="review.id" @click="goToReviewDetails(review.id)">
          <div class="plan-content">
            <div class="plan-image">
              <img
                  :src="review.imageUrls && review.imageUrls.length > 0 ? review.imageUrls[0] : noImage"
                  alt="리뷰 이미지"
                  class="review-image"
              />
            </div>
          </div>
          <h3 class="plan-title">{{ review.title }}</h3>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import noImage from '@/assets/images/no-img.jpg';
import router from "@/router/index.js";

const topReviews = ref([])

const fetchTopLikedReviews = async() => {
  try{
    const {data} = await axios
        .get(`http://localhost:8080/api/reviews/likes/top`,
            {
              params: {limit : 6},
              withCredentials: true,
            })
        topReviews.value = data
    //console.log(topReviews.value[0]?.title);
  }catch(error) {
    console.log("top 리뷰를 가져오는데 실패", error);
  }
}

const goToReviewDetails = (reviewId) => {
  router.push({ name: 'ReviewDetails', params: { reviewId } });
};


onMounted(fetchTopLikedReviews)
</script>