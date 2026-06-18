import axios from 'axios';

// Base URL points to API Gateway
const API_URL = 'http://localhost:9090';

const axiosInstance = axios.create({
  baseURL: API_URL,
});

// Interceptor to add JWT token to every request
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;
