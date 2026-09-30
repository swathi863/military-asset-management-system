/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        military: {
          900: '#141d14',
          800: '#1f2b1f',
          700: '#2b3b2b',
          600: '#3c523c',
          500: '#4d694d',
          accent: '#84a98c',
          gold: '#d4af37',
          alert: '#d90429'
        }
      }
    },
  },
  plugins: [],
}
