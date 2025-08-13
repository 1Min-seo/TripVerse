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
                class="photo-upload relative"
                v-for="(url, index) in photoUrls"
                :key="index"
            >
              <div v-if="url" class="preview-wrapper">
                <img
                    :src="url"
                    alt="미리보기"
                    class="preview-image"
                />
                <button
                    type="button"
                    @click="removePhoto(index)"
                    class="remove-photo-btn absolute top-1 right-1 text-red-500 bg-white rounded-full p-1 shadow">
                  <i class="fa-solid fa-times"></i>
                </button>
              </div>

              <label
                  class="upload-label"
                  @click="url ? null : uploadPhoto(index)"
                  :class="{ 'hidden-upload-label': url }"
              >
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

            <div
                class="photo-upload"
                v-if="photoUrls.length < 5"
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
          <button type="submit">수정하기</button>
        </div>
      </form>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const reviewId = route.params.reviewId;

// 폼 데이터
const title = ref('');
const content = ref('');
const rating = ref(0);

// 이미지 관련 데이터
const photoUrls = ref([]); // 기존 이미지와 S3 업로드 후의 URL 리스트
const fileInputs = ref([]); // 숨겨진 파일 입력 요소들에 대한 참조

// --- 데이터 로딩 (기존 리뷰 정보 가져오기) ---
const fetchReviewDetails = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/reviews/${reviewId}`, {
      withCredentials: true
    });
    const review = response.data;
    title.value = review.title;
    content.value = review.content;
    rating.value = review.rating;

    // 기존 이미지 URL을 photoUrls에 설정
    photoUrls.value = review.imageUrls || [];

    // fileInputs 배열 초기화 (photoUrls의 길이에 맞춰서)
    await nextTick();
  } catch (err) {
    console.error("리뷰 세부 정보 로드 실패:", err);
    alert('리뷰 정보를 가져오는 데 실패했습니다.');
  }
};

// --- 이미지 관리 로직 ---

// S3 업로드 API 호출 (백엔드에 /api/upload/image 엔드포인트가 있다고 가정)
const uploadImageToS3 = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  try {
    // S3 업로드 엔드포인트 호출
    const response = await axios.post('http://localhost:8080/api/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      withCredentials: true
    });
    // 업로드된 이미지 URL 반환
    return response.data.imageUrl;
  } catch (error) {
    console.error("S3 업로드 실패:", error);
    alert("이미지 업로드에 실패했습니다.");
    return null;
  }
};

// 파일 선택 시 호출되는 핸들러
const handlePhotoUpload = async (event, index) => {
  const file = event.target.files[0];
  if (!file) return;

  // 파일을 S3에 업로드하고 URL을 받아옵니다.
  const uploadedUrl = await uploadImageToS3(file);

  if (uploadedUrl) {
    // 해당 인덱스의 photoUrls 값을 업로드된 URL로 업데이트
    photoUrls.value[index] = uploadedUrl;
  }
};

// 사진 슬롯 추가 (새 이미지 업로드를 위해)
const addPhotoSlot = async () => {
  if (photoUrls.value.length < 5) {
    // 새로운 빈 슬롯을 photoUrls에 추가하고, Vue가 DOM 업데이트를 기다립니다.
    photoUrls.value.push(null);
    await nextTick();

    // 새로운 슬롯의 파일 입력 필드를 클릭하여 파일 선택 창을 엽니다.
    uploadPhoto(photoUrls.value.length - 1);
  }
};

// 사진 선택 트리거
const uploadPhoto = (index) => {
  const input = fileInputs.value[index];
  if (input) {
    input.click();
  }
};

// 이미지 삭제 (UI에서 'x' 버튼 클릭 시)
const removePhoto = (index) => {
  // 해당 인덱스의 이미지를 photoUrls 배열에서 제거합니다.
  // 이 변경사항은 submitUpdateReview 시 백엔드로 전달되어 S3 이미지 삭제를 트리거합니다.
  photoUrls.value.splice(index, 1);
};


// --- 리뷰 수정 제출 ---
const submitUpdateReview = async () => {
  // 백엔드로 수정된 데이터와 photoUrls 리스트를 함께 전송
  try {
    await axios.put(`http://localhost:8080/api/reviews/edit/${reviewId}`, {
      title: title.value,
      content: content.value,
      rating: rating.value,
      photos: photoUrls.value // 수정된 이미지 URL 리스트 (기존 + 새 이미지)
    },{
      withCredentials: true
    });
    alert("수정이 완료 되었습니다!");
    router.push('/mypage');
  } catch (error) {
    console.error("리뷰 수정 중 오류 발생:", error);
    alert("리뷰 수정에 실패했습니다. (콘솔 확인)");
  }
};

// --- 기타 폼 관련 함수 ---
const setRating = (star) => {
  rating.value = star;
}

const updateTitleCount = () => {}

const updateContentCount = () => {}

// 컴포넌트 마운트 시 기존 리뷰 정보 로드
onMounted(() => {
  fetchReviewDetails();
});
</script>

<style scoped>
/* 기존 스타일 */
.review-form {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.review-form-main {
  background-color: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #333;
}

input[type="text"],
textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

input[type="text"]:focus,
textarea:focus {
  border-color: #007bff;
  outline: none;
}

.char-count {
  text-align: right;
  font-size: 0.8rem;
  color: #666;
  margin-top: 0.25rem;
}

/* Rating stars */
.rating .fa-star {
  cursor: pointer;
  color: #ccc;
  font-size: 1.5rem;
  transition: color 0.2s;
}

.rating .fa-star.active {
  color: #ffc107;
}

span {
  margin-left: 0.5rem;
}

/* Photo grid */
.photo-grid {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.photo-upload {
  width: 100px;
  height: 100px;
  border: 1px dashed #ccc;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  background-color: #f0f0f0;
  position: relative;
}

.upload-label {
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.upload-label i {
  font-size: 2rem;
  color: #999;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.photo-info {
  font-size: 0.85rem;
  color: #777;
  margin-top: 0.5rem;
}

.form-buttons {
  text-align: right;
}

.form-buttons button {
  padding: 0.75rem 1.5rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.form-buttons button:hover {
  background-color: #0056b3;
}

/* 이미지 삭제 버튼 스타일 */
.remove-photo-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  z-index: 10;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.remove-photo-btn i {
  color: #dc3545; /* 빨간색 'x' 아이콘 */
  font-size: 0.8rem;
}

/* 이미지가 있을 때는 카메라 아이콘 숨기기 */
.hidden-upload-label {
  display: none;
}
</style>