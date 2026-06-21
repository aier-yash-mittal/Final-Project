import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import styles from './Dashboard.module.css';

export default function StudentDashboard() {
  const navigate = useNavigate();
  const userName = localStorage.getItem('name') || 'Student';
  const [activeTab, setActiveTab] = useState('dashboard');
  const [items, setItems] = useState([]);
  const [requests, setRequests] = useState([]);
  const [quantities, setQuantities] = useState({});
  const [cart, setCart] = useState([]);

  const [itemPage, setItemPage] = useState(0);
  const [itemTotalPages, setItemTotalPages] = useState(1);
  
  const [reqPage, setReqPage] = useState(0);
  const [reqTotalPages, setReqTotalPages] = useState(1);

  const [reqStatus, setReqStatus] = useState('ALL');
  const [reqSortBy, setReqSortBy] = useState('createdAt');
  const [allItems, setAllItems] = useState([]);

  useEffect(() => {
    fetchItems();
  }, [itemPage]);

  useEffect(() => {
    fetchMyRequests();
  }, [reqPage, reqStatus, reqSortBy]);

  useEffect(() => {
    fetchAllItemsForMapping();
  }, []);

  const fetchItems = async () => {
    const res = await api.get(`/items?page=${itemPage}&size=20`); 
    setItems(res.data.content); //items save
    setItemTotalPages(res.data.totalPages); 
  };

  const fetchAllItemsForMapping = async () => {  
    const res = await api.get(`/items?page=0&size=10000`);
    setAllItems(res.data.content);
  };

  const fetchMyRequests = async () => { //My Requests tab ke liye requests fetch karne wala function.
    const apiSort = reqSortBy === 'itemName' ? 'createdAt' : reqSortBy;
    const res = await api.get(`/requests/my-requests?page=${reqPage}&size=20&sortBy=${apiSort}&status=${reqStatus}`);
    setRequests(res.data.content);
    setReqTotalPages(res.data.totalPages);
  };

  const sortedRequests = [...requests].sort((a, b) => { //Client-side sorting for itemName since backend doesn't support it directly.
    if (reqSortBy === 'itemName') {
      const nameA = allItems.find(i => i.id === a.itemId)?.name || '';
      const nameB = allItems.find(i => i.id === b.itemId)?.name || '';
      return nameA.localeCompare(nameB);
    }
    return 0;
  });

  const handleQuantityChange = (itemId, value) => {
    setQuantities({ ...quantities, [itemId]: value });
  };

  const addToCart = (item) => {
    const qty = parseInt(quantities[item.id] || 1);
    setCart(prev => {
      const existing = prev.find(c => c.item.id === item.id);
      if (existing) {
        return prev.map(c => c.item.id === item.id ? { ...c, quantity: c.quantity + qty } : c);
      }
      return [...prev, { item, quantity: qty }];
    });
  };

  const removeFromCart = (itemId) => {
    setCart(prev => prev.filter(c => c.item.id !== itemId));
  };

  const submitOrder = async () => {
    if (cart.length === 0) return;
    try {
      const payload = cart.map(c => ({ itemId: c.item.id, quantity: c.quantity }));
      await api.post('/requests/order', payload);
      alert('Order submitted successfully');
      setCart([]);
      fetchMyRequests();
      setActiveTab('requests');
    } catch (e) {
      alert('Failed to submit order');
    }
  };

  const makeRequest = async (itemId) => {
    const qty = quantities[itemId] || 1;
    try {
      await api.post('/requests', { itemId, quantity: parseInt(qty) });
      alert('Request submitted successfully');
      fetchMyRequests();
    } catch (e) {
      alert('Failed to submit request');
    }
  };

  const logout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const formatTime = (isoString) => {
    if (!isoString) return '-';
    return new Date(isoString).toLocaleString();
  };

  return (
    <div>
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <h2>Stationery Hub <span className={styles.roleLabel}>STUDENT WORKSPACE</span></h2>
          <p>Inventory & Request Management</p>
        </div>
        <div className={styles.headerRight} style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
          <div style={{ textAlign: 'right' }}>
            <strong>Welcome Back, {userName}</strong><br/>
            <small style={{ color: '#ccc' }}>Browse catalog and track your stationery requests.</small>
          </div>
          <button onClick={logout} className={styles.btn}>Logout</button>
        </div>
      </div>
      <div className={styles.container}>
        
        <div className={styles.tabs}>
          <button 
            className={`${styles.tabButton} ${activeTab === 'dashboard' ? styles.activeTab : ''}`}
            onClick={() => setActiveTab('dashboard')}
          >
            Dashboard
          </button>
          <button 
            className={`${styles.tabButton} ${activeTab === 'catalog' ? styles.activeTab : ''}`}
            onClick={() => setActiveTab('catalog')}
          >
            Catalog
          </button>
          <button 
            className={`${styles.tabButton} ${activeTab === 'requests' ? styles.activeTab : ''}`}
            onClick={() => setActiveTab('requests')}
          >
            Requests
          </button>
        </div>

        {activeTab === 'dashboard' && (
          <div className={styles.summaryCards}>
            <div className={styles.summaryCard}>
              <h3>Total Catalog Items</h3>
              <p>{allItems.length}</p>
            </div>
            <div className={styles.summaryCard}>
              <h3>My Pending Requests</h3>
              <p>{requests.filter(r => r.status === 'PENDING').length}</p>
            </div>
            <div className={styles.summaryCard}>
              <h3>My Fulfilled Requests</h3>
              <p>{requests.filter(r => r.status === 'FULFILLED').length}</p>
            </div>
          </div>
        )}

        {activeTab === 'catalog' && (
        <div className={styles.card}>
          <h3>Available Stationery</h3>
          <table>
            <thead><tr><th>Name</th><th>Category</th><th>Available</th><th>Quantity</th><th>Action</th></tr></thead>
            <tbody>
              {items.map(item => (
                <tr key={item.id}>
                  <td>{item.name}</td><td>{item.category}</td><td>{item.availableQuantity} {item.unit}</td>
                  <td>
                    <input 
                      type="number" 
                      min="1" 
                      max={item.availableQuantity} 
                      value={quantities[item.id] || 1} 
                      onChange={(e) => handleQuantityChange(item.id, e.target.value)} 
                      style={{ width: '60px', padding: '5px' }}
                    />
                  </td>
                  <td>
                    <button className={styles.btn} onClick={() => addToCart(item)} style={{marginRight: '5px'}}>Add to Cart</button>
                    <button className={styles.btn} onClick={() => makeRequest(item.id)} style={{background: '#888'}}>Request</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div style={{ marginTop: '10px', display: 'flex', gap: '10px', alignItems: 'center' }}>
            <button className={styles.btn} disabled={itemPage === 0} onClick={() => setItemPage(p => p - 1)}>Previous</button>
            <span>Page {itemPage + 1} of {itemTotalPages}</span>
            <button className={styles.btn} disabled={itemPage + 1 >= itemTotalPages} onClick={() => setItemPage(p => p + 1)}>Next</button>
          </div>
          
          {cart.length > 0 && (
            <div style={{ marginTop: '20px', padding: '15px', background: '#f9f9f9', borderRadius: '5px', border: '1px solid #ddd' }}>
              <h4>Your Cart</h4>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                {cart.map((c, idx) => (
                  <li key={idx} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                    <span>{c.item.name} x{c.quantity}</span>
                    <button className={styles.btn} style={{ background: 'red', padding: '2px 8px' }} onClick={() => removeFromCart(c.item.id)}>Remove</button>
                  </li>
                ))}
              </ul>
              <button className={styles.btn} style={{ background: 'green', width: '100%' }} onClick={submitOrder}>Submit Order</button>
            </div>
          )}
        </div>
        )}

        {activeTab === 'requests' && (
        <div className={styles.card}>
          <h3>My Requests</h3>
          <div style={{ display: 'flex', gap: '15px', marginBottom: '15px' }}>
            <select value={reqStatus} onChange={(e) => { setReqStatus(e.target.value); setReqPage(0); }} style={{ padding: '5px' }}>
              <option value="ALL">All Statuses</option>
              <option value="PENDING">Pending</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
              <option value="FULFILLED">Fulfilled</option>
            </select>
            <select value={reqSortBy} onChange={(e) => { setReqSortBy(e.target.value); setReqPage(0); }} style={{ padding: '5px' }}>
              <option value="createdAt">Sort by Date</option>
              <option value="status">Sort by Status</option>
              <option value="itemName">Sort by Item Name</option>
            </select>
          </div>
          <table>
            <thead><tr><th>Order ID</th><th>Request ID</th><th>Timestamp</th><th>Item Name (ID)</th><th>Quantity</th><th>Status</th><th>Rejection Reason</th></tr></thead>
            <tbody>
              {sortedRequests.map(req => {
                const itemName = allItems.find(i => i.id === req.itemId)?.name || 'Unknown';
                const orderId = req.requestGroupId || '-';
                return (
                <tr key={req.id}>
                  <td>{orderId}</td>
                  <td><strong>{req.id}</strong></td>
                  <td>{formatTime(req.createdAt)}</td>
                  <td>{itemName} ({req.itemId})</td><td>{req.quantity}</td>
                  <td><strong>{req.status}</strong></td><td>{req.rejectionReason || '-'}</td>
                </tr>
              )})}
            </tbody>
          </table>
          <div style={{ marginTop: '10px', display: 'flex', gap: '10px', alignItems: 'center' }}>
            <button className={styles.btn} disabled={reqPage === 0} onClick={() => setReqPage(p => p - 1)}>Previous</button>
            <span>Page {reqPage + 1} of {reqTotalPages}</span>
            <button className={styles.btn} disabled={reqPage + 1 >= reqTotalPages} onClick={() => setReqPage(p => p + 1)}>Next</button>
          </div>
        </div>
        )}
      </div>
    </div>
  );
}
