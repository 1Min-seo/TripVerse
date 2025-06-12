<template>
  <div class="review-form">
    <br><br>
    <main class="review-form-main">
      <form @submit.prevent="submitUpdateReview" role="form">
        <div class="form-group">
          <label for="title">제목</label>
          <input
              type="text"
              id="title"
              v-model="title"
              maxlength="50"
              placeholder="리뷰 제목을 입력해주세요"
              @input="updateTitleCount"
          />
          <p class="char-count">{{ title.length }}/50</p>
        </div>

        <div class="form-group">
          <label>평점</label>
          <div class="rating">
            <i
                v-for="star in 5"
                :key="star"
                class="fa fa-star"
                :class="{ 'active': star <= rating }"
                @click="setRating(star)"
            ></i>
            <span>{{ rating }}/5</span>
          </div>
        </div>

        <div class="form-group">
          <label>사진 첨부</label>
          <div class="photo-grid">
            <div
                class="photo-upload"
                v-for="(file, index) in photos"
                :key="index"
            >
              <!-- 미리보기 이미지 -->
              <div v-if="photoUrls[index]" class="preview-wrapper">
                <img
                    :src="photoUrls[index]"
                    alt="미리보기"
                    class="preview-image"
                />
              </div>

              <!-- 사진 선택 아이콘 -->
              <label class="upload-label" @click="uploadPhoto(index)">
                <i class="fa-solid fa-camera"></i>
                <input
                    type="file"
                    accept="image/*"
                    :ref="el => fileInputs[index] = el"
                    @change="handlePhotoUpload($event, index)"
                    style="display: none"
                />
              </label>
            </div>

            <!-- 사진 추가 버튼 (최대 5개까지) -->
            <div
                class="photo-upload"
                v-if="photos.length < 5"
                @click="addPhotoSlot"
            >
              <label class="upload-label">
                <i class="fa-solid fa-plus"></i>
              </label>
            </div>
          </div>
          <p class="photo-info">최대 5장까지 업로드 가능합니다.</p>
        </div>

        <div class="form-group">
          <label for="content">리뷰 내용</label>
          <textarea
              id="content"
              v-model="content"
              rows="8"
              maxlength="1000"
              placeholder="최소 20자 이상 작성해주세요"
              @input="updateContentCount"
          ></textarea>
          <p class="char-count">{{ content.length }}/1000</p>
        </div>

        <div class="form-buttons">
          <button type="submit">등록하기</button>
        </div>
      </form>
    </main>
  </div>
</template>
<script>
import {ref, onMounted, nextTick} from 'vue';
  import axios from 'axios';
  import { useRoute, useRouter } from 'vue-router';

  export default {
    setup() {
      const route = useRoute();
      const router = useRouter();
      const reviewId = route.params.reviewId;

      const title = ref('');
      const content = ref('');
      const rating = ref(0);
      const photos = ref([]);
      const photoUrls = ref([]);
      const fileInputs = ref([]);

      const setRating = (star) => {
        rating.value = star;
      }

      const addPhotoSlot = async () => {
        if (photos.value.length < 5) {
          photos.value.push(null);
          photoUrls.value.push(null);
          await nextTick();
          uploadPhoto(photos.value.length - 1);
        }
      };

      const uploadPhoto = (index) => {
        const input = fileInputs.value[index];
        if (!input) {
          console.warn("input 아직 준비 안 됨");
          return;
        }
        input.click();
      };

      const submitUpdateReview = async () => {
        await axios.put(`http://localhost:8080/api/reviews/edit/${reviewId}`, {
          title: title.value,
          content: content.value,
          rating: rating.value,
        },{
          withCredentials: true
        });
        alert("수정이 완료 되었습니다!");
        router.push('/MyPage');
      }

        //기존 리뷰
        onMounted(async () => {
          try {
            const response = await axios.get(`http://localhost:8080/api/reviews/${reviewId}`, {
              withCredentials: true
            });
            const review = response.data;
            title.value = review.title;
            content.value = review.content;
            rating.value = review.rating;

            photoUrls.value = review.imageUrls || [];
            photos.value = new Array(photoUrls.value.length).fill(null);

            console.log("이미지 url:", [...photos.value]);
          } catch (err) {
            console.error(err);
            alert('failed to load review');
          }
        });

        return {
          title,
          content,
          rating,
          photos,
          photoUrls,
          fileInputs,
          addPhotoSlot,
          setRating,
          submitUpdateReview
        }
      }
    }
</script>