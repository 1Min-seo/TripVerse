import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

import './assets/css/style.css'
import './assets/css/NavigationBar.css'
import './assets/css/HeroSection.css'
import './assets/css/PopularPlans.css'
import './assets/css/JourneyStart.css'
import './assets/css/Mypage.css'
import './assets/css/CreateReview.css'
import './assets/css/ReviewDetails.css'
import './assets/css/CreatePlan.css'
import './assets/css/TravelTest.css'
import './assets/css/TravelTestQuestions.css'

axios.defaults.withCredentials = true
createApp(App)
    .use(router)
    .mount('#app')