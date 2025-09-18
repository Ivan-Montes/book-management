import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],

	server: {
		port: 3000,
		proxy: {
			'/genres': {
				target: 'http://localhost:8080/api/v1',
				changeOrigin: true,
			},
			'/publishers': {
				target: 'http://localhost:8080/api/v1',
				changeOrigin: true,
			},
			'/authors': {
				target: 'http://localhost:8080/api/v1',
				changeOrigin: true,
			},
			'/bookshops': {
				target: 'http://localhost:8080/api/v1',
				changeOrigin: true,
			},
			'/bookbookshops': {
				target: 'http://localhost:8080/api/v1',
				changeOrigin: true,
			},
			'/books': {
				target: 'http://localhost:8080/api/v1',
				changeOrigin: true,
			}
		}
	}
})
