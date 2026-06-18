import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import styles from './Dashboard.module.css';

export default function StudentDashboard() {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [requests, setRequests] = useState([]);
  const [quantities, setQuantities] = useState({});

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
    setItems(res.data.content);
    setItemTotalPages(res.data.totalPages);
  };

  const fetchAllItemsForMapping = async () => {
    const res = await api.get(`/items?page=0&size=10000`);
    setAllItems(res.data.content);
  };

  const fetchMyRequests = async () => {
    const apiSort = reqSortBy === 'itemName' ? 'createdAt' : reqSortBy;
    const res = await api.get(`/requests/my-requests?page=${reqPage}&size=20&sortBy=${apiSort}&status=${reqStatus}`);
    setRequests(res.data.content);
    setReqTotalPages(res.data.totalPages);
  };

  const sortedRequests = [...requests].sort((a, b) => {
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
        <h2>Student Dashboard</h2>
        <button onClick={logout} className={styles.btn}>Logout</button>
      </div>
      <div className={styles.container}>
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
                  <td><button className={styles.btn} onClick={() => makeRequest(item.id)}>Request</button></td>
                </tr>
              ))}
            </tbody>
          </table>
          <div style={{ marginTop: '10px', display: 'flex', gap: '10px', alignItems: 'center' }}>
            <button className={styles.btn} disabled={itemPage === 0} onClick={() => setItemPage(p => p - 1)}>Previous</button>
            <span>Page {itemPage + 1} of {itemTotalPages}</span>
            <button className={styles.btn} disabled={itemPage + 1 >= itemTotalPages} onClick={() => setItemPage(p => p + 1)}>Next</button>
          </div>
        </div>

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
            <thead><tr><th>Request ID</th><th>Timestamp</th><th>Item Name (ID)</th><th>Quantity</th><th>Status</th><th>Rejection Reason</th></tr></thead>
            <tbody>
              {sortedRequests.map(req => {
                const itemName = allItems.find(i => i.id === req.itemId)?.name || 'Unknown';
                return (
                <tr key={req.id}>
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
      </div>
    </div>
  );
}
