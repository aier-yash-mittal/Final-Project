import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import StudentDashboard from './pages/StudentDashboard';
import AdminDashboard from './pages/AdminDashboard';

function App() {
  const PrivateRoute = ({ children, roleRequired }) => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    
    if (!token) return <Navigate to="/login" />;
    if (roleRequired && role !== roleRequired) return <Navigate to="/login" />;
    
    return children;
  };

  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        <Route 
          path="/student/*" 
          element={
            <PrivateRoute roleRequired="STUDENT">
              <StudentDashboard />
            </PrivateRoute>
          } 
        />
        
        <Route 
          path="/admin/*" 
          element={
            <PrivateRoute roleRequired="ADMIN">
              <AdminDashboard />
            </PrivateRoute>
          } 
        />
      </Routes>
    </div>
  );
}

export default App;
