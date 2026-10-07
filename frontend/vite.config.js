import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        // 代理前缀，可自定义
        target: process.env.VITE_API_BASE_URL || 'http://localhost:8080', // 后端地址
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, ''),
        headers: {
          Connection: 'keep-alive',
          Referer: process.env.VITE_API_BASE_URL || 'http://localhost:8080'
          // Referer: 'http://127.0.0.1:8080'
        },
        onProxyReq: (proxyReq, req, res) => {
          console.log(`Proxying ${req.method} ${req.url} -> ${proxyReq.path}`)
        }
      }
    }
  },
  optimizeDeps: {
    include: ['file-saver', 'html-docx-js']
  },
  plugins: [
    vue(),
    // vueDevTools(),
    AutoImport({
      resolvers: [ElementPlusResolver()]
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
