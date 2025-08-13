<template>
  <div class="container">
    <h2>새 여행 계획 만들기</h2>

    <div class="trip-meta">
      <input v-model="tripName" placeholder="예: 제주도 가족 여행" />
      <input type="date" v-model="startDate" @change="updateDays" />
      <input type="date" v-model="endDate" @change="updateDays" />
    </div>

    <p>총 여행 기간: {{ days.length }}일</p>

    <div class="main-grid">
      <div class="days-section">
        <div v-for="(day, dayIdx) in days" :key="dayIdx" class="day-box">
          <h3>Day {{ dayIdx + 1 }} ({{ day.date }})</h3>

          <div
              v-for="(place, idx) in day.places"
              :key="idx"
              class="place-box"
          >
            <input
                :id="`autocomplete-${dayIdx}-${idx}`"
                v-model="place.name"
                placeholder="장소 검색 또는 직접 입력"
            />
            <input type = "time" v-model="place.time" placeholder="시간 (예: 오전 10:00)" />
            <input v-model="place.memo" placeholder="메모" />
            <button @click="removePlace(dayIdx, idx)">❌</button>
          </div>

          <button @click="addPlace(dayIdx)">+ 장소 추가</button>
        </div>
      </div>

      <!-- 오른쪽: 지도 -->
      <div class="map-section">
        <div id="map" style="height: 500px;"></div>

        <div class="map-info">
          <h4>장소 목록</h4>
          <ol>
            <li v-for="(place, i) in allPlaces" :key="i">
              {{ place.name }}
            </li>
          </ol>
        </div>
      </div>
    </div>

    <button @click="saveTrip">여행 일정 저장하기</button>
  </div>
</template>
<script>
import { ref, onMounted, watch, nextTick, computed } from 'vue'
import axios from 'axios'
import router from "@/router/index.js";

export default {
  setup() {
    const tripName = ref('')
    const startDate = ref('2025-05-01')
    const endDate = ref('2025-05-03')
    const days = ref([])

    const map = ref(null)
    let markers = []

    function updateDays() {
      if (!startDate.value || !endDate.value) return
      const start = new Date(startDate.value)
      const end = new Date(endDate.value)
      const diff = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1

      days.value = []
      for (let i = 0; i < diff; i++) {
        const date = new Date(start)
        date.setDate(start.getDate() + i)
        const formatted = date.toISOString().split('T')[0]
        days.value.push({date: formatted, places: []})
      }

      nextTick(() => setupAllAutocomplete())
    }

    function addPlace(dayIdx) {
      days.value[dayIdx].places.push({
        name: '',
        time: '',
        memo: '',
        latitude: 0,
        longitude: 0,
        placeOrder: days.value[dayIdx].places.length
      })
      nextTick(() => setupAllAutocomplete())
    }

    function removePlace(dayIdx, placeIdx) {
      days.value[dayIdx].places.splice(placeIdx, 1)
      days.value[dayIdx].places.forEach((place, idx) => {
        place.placeOrder = idx
      })
      updateMarkers()
    }

    function initMap() {
      const center = {lat: 33.4996213, lng: 126.5311884}
      map.value = new google.maps.Map(document.getElementById('map'), {
        center,
        zoom: 10,
      })
    }

    function updateMarkers() {
      markers.forEach(m => m.setMap(null))
      markers = []

      let idx = 1
      for (const day of days.value) {
        for (const place of day.places) {
          if (!place.name || !place.latitude || !place.longitude) continue
          const marker = new google.maps.Marker({
            position: {lat: place.latitude, lng: place.longitude},
            map: map.value,
            label: String(idx),
          })
          markers.push(marker)
          idx++
        }
      }
    }

    function setupAllAutocomplete() {
      for (let d = 0; d < days.value.length; d++) {
        const places = days.value[d].places
        for (let p = 0; p < places.length; p++) {
          const inputId = `autocomplete-${d}-${p}`
          const input = document.getElementById(inputId)
          if (!input) continue

          const autocomplete = new google.maps.places.Autocomplete(input)
          autocomplete.addListener('place_changed', () => {
            const place = autocomplete.getPlace()
            if (!place.geometry) return
            places[p].name = place.name
            places[p].latitude = place.geometry.location.lat()
            places[p].longitude = place.geometry.location.lng()
            updateMarkers()
          })
        }
      }
    }

    const allPlaces = computed(() =>
        days.value.flatMap(day => day.places.filter(p => p.name))
    )

    function saveTrip() {
      const requestBody = {
        title: tripName.value,
        startDate: startDate.value,
        endDate: endDate.value,
        days: days.value.map(day => ({
          date: day.date,
          places: day.places
              .filter(place => place.name && place.name.trim() !== '')
              .map((place, idx) => ({
                name: place.name.trim(),
                time: place.time || null,
                memo: place.memo || '',
                latitude: place.latitude || 0,
                longitude: place.longitude || 0,
                placeOrder: idx
              }))
        }))
      }

      console.log('전송할 데이터:', JSON.stringify(requestBody, null, 2))

      axios.post('http://localhost:8080/api/plans', requestBody, {withCredentials: true})
          .then(response => {
            console.log('저장 성공:', response.data)
            alert('여행이 저장되었습니다!')
            router.push('/MyPage');
          })
          .catch(error => {
            console.error('저장 중 오류 발생:', error)
            console.error('에러 응답:', error.response?.data)
            alert('저장에 실패했습니다: ' + (error.response?.data?.message || error.message))
          })
    }


    onMounted(() => {
      updateDays()
      initMap()
    })

    watch(days, () => updateMarkers(), {deep: true})

    return {
      tripName,
      startDate,
      endDate,
      days,
      allPlaces,
      updateDays,
      addPlace,
      removePlace,
      saveTrip,
    }
  }
}
</script>