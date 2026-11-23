import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { defineConfig, loadEnv } from 'vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, import.meta.dirname, '')
  // 로컬 개발 서버가 /api 요청을 넘길 Spring 서버
  const apiProxyTarget = env.API_PROXY_TARGET || 'http://localhost:8080'

  return {
    plugins: [react(), tailwindcss()],
    resolve: {
      tsconfigPaths: true,
    },
    server: {
      // 서버 API는 모두 /api/v1로 시작하니 경로를 떼지 않고(rewrite 없이) 그대로 넘긴다
      proxy: {
        '^/api(/|\\?|$)': { target: apiProxyTarget, changeOrigin: true },
      },
    },
    test: {
      environment: 'jsdom',
      setupFiles: ['./src/test/setup.ts'],
    },
  }
})
