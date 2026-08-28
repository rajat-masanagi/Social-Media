import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  // Vite 8's optional Lightning CSS binary is flaky under some Windows npm installs.
  // Keep this learning app's tiny stylesheet readable instead of native-minifying it.
  build: { cssMinify: false },
  test: { environment: 'jsdom', setupFiles: './src/test-setup.js' },
})
