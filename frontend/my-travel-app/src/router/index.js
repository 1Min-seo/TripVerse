import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/Home.vue'
import MyPage from '../components/MyPage.vue'
import CreateReview from '../components/CreateReview.vue'
import ReviewDetails from '../components/ReviewDetails.vue'
import UpdateReview from '../components/UpdateReview.vue'
import CreatePlan from '../components/CreatePlan.vue'
import PopularPlans from '../components/PopularPlans.vue'
import TravelTest from '../components/TravelTest.vue'
import TravelTestQuestions from '../components/TravelTestQuestions.vue'
import TravelRecommendation from '../components/TravelRecommendation.vue'

import OAuth2Redirect from '../components/OAuth2Redirect.vue'
import axios from 'axios'

const routes = [
    { path: '/', name: 'Home', component: Home },
    {
        path: '/MyPage',
        name: 'MyPage',
        component: MyPage,
        beforeEnter: async (to, from, next) => {
            try {
                await axios.get("http://localhost:8080/api/test", {
                    withCredentials: true
                })
                next()
            } catch (e) {
                alert('로그인 후 이용 가능한 서비스입니다.')
                next('/')
            }
        }
    },
    {
        path: '/CreatePlan',
        name: 'CreatePlan',
        component: CreatePlan,
        beforeEnter: async (to, from, next) => {
            try {
                await axios.get("http://localhost:8080/api/test", {
                    withCredentials: true
                })
                next()
            } catch (e) {
                alert('로그인 후 이용 가능한 서비스입니다.')
                next('/')
            }
        }

    },
    { path: '/reviews/new', name: 'CreateReview', component: CreateReview },
    { path: '/reviews/:reviewId', name: 'ReviewDetails', component: ReviewDetails},
    { path: '/reviews/edit/:reviewId', name: 'UpdateReview', component: UpdateReview},
    { path: '/oauth2/redirect', name: 'OAuth2Redirect', component: OAuth2Redirect },
    //{ path: '/PopularPlan', name: 'popularPlan', component: popularPlan}
    {path: '/travel/test', name: 'TravelTest', component: TravelTest},
    {path: '/travel/test/questions', name: 'TravelTestQuestions', component: TravelTestQuestions},
    { path: '/travel-preference/:preferenceId/recommendation', name: 'TravelRecommendation', component: TravelRecommendation }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router