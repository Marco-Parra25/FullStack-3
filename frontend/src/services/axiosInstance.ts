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

axiosInstance.interceptors.response.use(
  response => response,
  error => {
    const status = error.response?.status
    if (status === 401) {
      sessionStorage.clear()
      window.location.replace('/')
    } else if (status === 403) {
      window.location.replace('/403')
    }
    return Promise.reject(error)
  }
)

export default axiosInstance
