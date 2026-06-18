import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import styles from './Dashboard.module.css';

export default function Register() {
  const [formData, setFormData] = useState({ name: '', email: '', password: '', role: 'STUDENT' });
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      await api.post('/auth/register', formData);
      alert('Registration Successful! Please login.');
      navigate('/login');
    } catch (error) {
      alert('Registration Failed');
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card} style={{ maxWidth: '400px', margin: '50px auto' }}>
        <h2>Register</h2>
        <form onSubmit={handleRegister}>
          <div className="form-group">
            <label>Name</label>
            <input type="text" onChange={(e) => setFormData({...formData, name: e.target.value})} required />
          </div>
          <div className="form-group">
            <label>Email</label>
            <input type="email" onChange={(e) => setFormData({...formData, email: e.target.value})} required />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" onChange={(e) => setFormData({...formData, password: e.target.value})} required />
          </div>
          <div className="form-group">
            <label>Role</label>
            <select onChange={(e) => setFormData({...formData, role: e.target.value})}>
              <option value="STUDENT">STUDENT</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
          <button type="submit" className={styles.btn} style={{ width: '100%' }}>Register</button>
        </form>
        <p style={{ marginTop: '15px' }}>Already have an account? <Link to="/login">Login</Link></p>
      </div>
    </div>
  );
}
