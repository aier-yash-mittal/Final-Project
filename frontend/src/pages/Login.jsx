import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import styles from './Dashboard.module.css';

export default function Login() {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/auth/login', { email, password });
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('role', response.data.role);
      localStorage.setItem('name', response.data.name);

      if (response.data.role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/student');
      }
    } catch (error) {
      setError('Login Failed: ' + (error.response?.data?.message || 'Invalid Credentials'));
    }
  };

  return (
    <div className={styles.container} style={{ maxWidth: '400px', marginTop: '100px' }}>
      <div className={styles.card}>
        <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Login</h2>
        {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} required />
          </div>
          <button type="submit" className={styles.btn} style={{ width: '100%' }}>Login</button>
        </form>
        <p style={{ textAlign: 'center', marginTop: '15px' }}>
          Don't have an account? <a href="/register">Register here</a>
        </p>
      </div>
    </div>
  );
}
