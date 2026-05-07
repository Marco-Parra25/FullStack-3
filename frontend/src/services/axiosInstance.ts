import axios from 'axios'

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080',
})

axiosInstance.interceptors.request.use(config => {
  const token = sessionStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default axiosInstance
