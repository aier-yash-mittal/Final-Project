import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import styles from './Dashboard.module.css';

export default function AdminDashboard() {
  const navigate = useNavigate();
  const userName = localStorage.getItem('name') || 'Admin';
  const [activeTab, setActiveTab] = useState('dashboard');
  const [requests, setRequests] = useState([]);
  const [items, setItems] = useState([]);

  const [itemPage, setItemPage] = useState(0);
  const [itemTotalPages, setItemTotalPages] = useState(1);

  const [reqPage, setReqPage] = useState(0);
  const [reqTotalPages, setReqTotalPages] = useState(1);
  
  const [reqStatus, setReqStatus] = useState('ALL');
  const [reqSortBy, setReqSortBy] = useState('createdAt');

  // Inventory Form State
  const [isEditing, setIsEditing] = useState(false);
  const [editId, setEditId] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    category: 'Paper',
    unit: 'pcs',
    availableQuantity: 100,
    minimumQuantity: 10
  });

  const [allItems, setAllItems] = useState([]);

  useEffect(() => {
    fetchRequests();
  }, [reqPage, reqStatus, reqSortBy]);

  useEffect(() => {
    fetchItems();
  }, [itemPage]);

  useEffect(() => {
    fetchAllItemsForMapping();
  }, []);

  const fetchRequests = async () => {
    const apiSort = reqSortBy === 'itemName' ? 'createdAt' : reqSortBy;
    const res = await api.get(`/requests?page=${reqPage}&size=20&sortBy=${apiSort}&status=${reqStatus}`);
    setRequests(res.data.content);
    setReqTotalPages(res.data.totalPages);
  };

  const fetchAllItemsForMapping = async () => {
    const res = await api.get(`/items?page=0&size=10000`);
    setAllItems(res.data.content);
  };

  const sortedRequests = [...requests].sort((a, b) => {
    if (reqSortBy === 'itemName') {
      const nameA = allItems.find(i => i.id === a.itemId)?.name || '';
      const nameB = allItems.find(i => i.id === b.itemId)?.name || '';
      return nameA.localeCompare(nameB);
    }
    return 0; // Otherwise backend handles sorting
  });

  const fetchItems = async () => {
    const res = await api.get(`/items?page=${itemPage}&size=20`);
    setItems(res.data.content);
    setItemTotalPages(res.data.totalPages);
  };

  const updateRequestStatus = async (id, action) => {
    try {
      if (action === 'reject') {
        const reason = prompt("Enter rejection reason:");
        if(!reason) return;
        await api.put(`/requests/reject/${id}?reason=${reason}`);
      } else {
        await api.put(`/requests/${action}/${id}`);
      }
      fetchRequests();
      fetchItems();
    } catch (e) {
      alert('Action failed');
    }
  };

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmitItem = async (e) => {
    e.preventDefault();
    try {
      if (isEditing) {
        await api.put(`/items/${editId}`, formData);
        alert('Item updated successfully');
      } else {
        await api.post('/items', formData);
        alert('Item added successfully');
      }
      resetForm();
      fetchItems();
    } catch (e) {
      alert(isEditing ? 'Failed to update item' : 'Failed to add item');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this item?')) {
      try {
        await api.delete(`/items/${id}`);
        alert('Item deleted successfully');
        fetchItems();
      } catch (e) {
        alert('Failed to delete item');
      }
    }
  };

  const handleEditClick = (item) => {
    setIsEditing(true);
    setEditId(item.id);
    setFormData({
      name: item.name,
      category: item.category,
      unit: item.unit,
      availableQuantity: item.availableQuantity,
      minimumQuantity: item.minimumQuantity
    });
  };

  const resetForm = () => {
    setIsEditing(false);
    setEditId(null);
    setFormData({ name: '', category: 'General', unit: 'pcs', availableQuantity: 100, minimumQuantity: 10 });
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
          <h2>Stationery Hub <span className={styles.roleLabel}>ADMIN WORKSPACE</span></h2>
          <p>Inventory & Request Management</p>
        </div>
        <div className={styles.headerRight} style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
          <div style={{ textAlign: 'right' }}>
            <strong>Welcome Back, {userName}</strong><br/>
            <small style={{ color: '#ccc' }}>Manage inventory, approvals and stationery requests.</small>
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
              <h3>Total Items</h3>
              <p>{allItems.length}</p>
            </div>
            <div className={styles.summaryCard}>
              <h3>Pending Requests</h3>
              <p>{requests.filter(r => r.status === 'PENDING').length}</p>
            </div>
            <div className={styles.summaryCard}>
              <h3>Low Stock Items</h3>
              <p>{allItems.filter(i => i.availableQuantity <= i.minimumQuantity).length}</p>
            </div>
          </div>
        )}

        {activeTab === 'requests' && (
        <div className={styles.card}>
          <h3>All Requests</h3>
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
            <thead><tr><th>Order ID</th><th>Request ID</th><th>Timestamp</th><th>Student</th><th>Item Name (ID)</th><th>Available Qty</th><th>Requested Qty</th><th>Status</th><th>Actions</th></tr></thead>
            <tbody>
              {sortedRequests.map(req => {
                const item = allItems.find(i => i.id === req.itemId);
                const itemName = item?.name || 'Unknown';
                const availableQty = item?.availableQuantity ?? '-';
                const orderId = req.requestGroupId || '-';
                return (
                <tr key={req.id}>
                  <td>{orderId}</td>
                  <td><strong>{req.id}</strong></td>
                  <td>{formatTime(req.createdAt)}</td>
                  <td>{req.studentEmail}</td><td>{itemName} ({req.itemId})</td><td>{availableQty}</td><td>{req.quantity}</td>
                  <td><strong>{req.status}</strong></td>
                  <td>
                    {req.status === 'PENDING' && (
                      <>
                        <button className={styles.btn} onClick={() => updateRequestStatus(req.id, 'approve')} style={{marginRight: '5px'}}>Approve</button>
                        <button className={styles.btn} onClick={() => updateRequestStatus(req.id, 'reject')} style={{background: 'red'}}>Reject</button>
                      </>
                    )}
                    {req.status === 'APPROVED' && (
                      <button className={styles.btn} onClick={() => updateRequestStatus(req.id, 'fulfil')} style={{background: 'green'}}>Fulfil</button>
                    )}
                  </td>
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

        {activeTab === 'catalog' && (
        <div className={styles.card}>
          <h3>Inventory Management</h3>
          <form onSubmit={handleSubmitItem} style={{display: 'flex', flexWrap: 'wrap', gap: '10px', marginBottom: '20px', background: '#f9f9f9', padding: '15px', borderRadius: '5px'}}>
            <input type="text" name="name" value={formData.name} onChange={handleInputChange} placeholder="Name" required style={{padding: '8px'}} />
            <select name="category" value={formData.category} onChange={handleInputChange} required style={{padding: '8px'}}>
              <option value="Paper">Paper</option>
              <option value="Pen">Pen</option>
              <option value="Pencil">Pencil</option>
              <option value="Notebook">Notebook</option>
              <option value="Marker">Marker</option>
              <option value="Other">Other</option>
            </select>
            <input type="text" name="unit" value={formData.unit} onChange={handleInputChange} placeholder="Unit (e.g., pcs)" required style={{padding: '8px'}} />
            <input type="number" name="availableQuantity" value={formData.availableQuantity} onChange={handleInputChange} placeholder="Available Qty" required style={{padding: '8px'}} />
            <input type="number" name="minimumQuantity" value={formData.minimumQuantity} onChange={handleInputChange} placeholder="Min Qty" required style={{padding: '8px'}} />
            <div style={{width: '100%'}}>
              <button type="submit" className={styles.btn} style={{marginRight: '10px'}}>{isEditing ? 'Update Item' : 'Create Item'}</button>
              {isEditing && <button type="button" className={styles.btn} onClick={resetForm} style={{background: '#666'}}>Cancel Edit</button>}
            </div>
          </form>

          <table>
            <thead><tr><th>ID</th><th>Name</th><th>Category</th><th>Unit</th><th>Available Qty</th><th>Min Qty</th><th>Actions</th></tr></thead>
            <tbody>
              {items.map(item => (
                <tr key={item.id}>
                  <td>{item.id}</td>
                  <td>{item.name}</td>
                  <td>{item.category}</td>
                  <td>{item.unit}</td>
                  <td>
                    {item.availableQuantity}
                    {item.availableQuantity <= item.minimumQuantity && (
                      <span style={{color: 'red', fontWeight: 'bold', marginLeft: '5px'}}>[LOW STOCK]</span>
                    )}
                  </td>
                  <td>{item.minimumQuantity}</td>
                  <td>
                    <button className={styles.btn} onClick={() => handleEditClick(item)} style={{marginRight: '5px'}}>Edit</button>
                    <button className={styles.btn} onClick={() => handleDelete(item.id)} style={{background: 'red'}}>Delete</button>
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
        </div>
        )}

      </div>
    </div>
  );
}
