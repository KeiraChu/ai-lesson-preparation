import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { KeepAlive } from 'vue'
const routes = [
  {
    path: '/',
    component: () => import('../views/layout/LayoutContainer.vue'),
    redirect: '/welcome',
    children: [
      {
        path: '/create/knowledge-plan',
        name: 'knowledge-plan',
        component: () => import('@/views/ai备课/KnowledgeLesson.vue')
      },
      {
        path: '/create/plan',
        name: 'create-plan',
        component: () => import('@/views/ai备课/CreateWord.vue')
      },
      {
        path: '/create/ppt',
        name: 'chat',
        component: () => import('@/views/ai备课/CreatePPT.vue'),
        meta: {
          KeepAlive: true
        }
      },
      {
        path: '/study/analysis',
        name: 'analysis',
        component: () => import('@/views/ai备课/Analysis.vue'),
        meta: {
          KeepAlive: true
        }
      },
      {
        path: '/user/profile',
        name: 'user-profile',
        component: () => import('@/views/user/UserProfile.vue'),
        meta: {
          KeepAlive: true
        }
      },
      {
        path: '/user/password',
        name: 'user-password',
        component: () => import('@/views/user/UserPassword.vue'),
        meta: {
          KeepAlive: true
        }
      },
      {
        path: '/user/mydoc',
        name: 'user-mydoc',
        component: () => import('@/views/user/Mydoc.vue'),
        meta: {
          KeepAlive: true
        }
      }
    ]
  },
  {
    path: '/welcome',
    name: 'welcome',
    component: () => import('../views/welcome/welcome.vue')
  },
  {
    path: '/login',
    component: () => import('../views/login/LoginPage.vue')
  },
  {
    path: '/aiPPT',
    component: () => import('../views/ai备课/AIPPT.vue')
  },
  {
    path: '/theme',
    component: () => import('../views/ai备课/Theme.vue')
  },
  {
    path: '/ppt',
    component: () => import('../views/ai备课/PPT.vue')
  },
  {
    path: '/create/test',
    component: () => import('../views/ai备课/CreateTest.vue')
  }
]
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const whiteList = ['/welcome', '/login']

  if (whiteList.includes(to.path)) {
    return next() // 直接放行
  } else {
    if (userStore.token) {
      next()
    } else {
      next('/login')
    }
  }
})
export default router
