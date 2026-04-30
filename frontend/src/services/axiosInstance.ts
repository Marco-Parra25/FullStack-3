import axios from 'axios'

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_BFF_URL || 'http://localhost:3001',
})

axiosInstance.interceptors.request.use(config => {
  const token = sessionStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default axiosInstance
