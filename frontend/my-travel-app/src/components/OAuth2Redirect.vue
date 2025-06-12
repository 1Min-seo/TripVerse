<template>
  <div>로그인 중입니다...</div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const token = urlParams.get('token')

  if (token) {
    localStorage.setItem('accessToken', token)
    router.push('/')
  } else {
    alert('로그인 실패')
    router.push('/')
  }
})
</script>