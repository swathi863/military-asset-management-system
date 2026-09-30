const API_BASE_URL = 'http://localhost:8080/api';

// Helper to get Authorization Header
const getAuthHeaders = () => {
  const token = localStorage.getItem('mams_jwt_token');
  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
};

// Helper to handle HTTP responses and throw real errors (NO SILENT MOCK FALLBACKS)
const handleResponse = async (res) => {
  if (!res.ok) {
    let errorMessage = `HTTP ${res.status}: ${res.statusText}`;
    try {
      const errorData = await res.json();
      if (errorData && errorData.message) {
        errorMessage = errorData.message;
      }
    } catch (e) {
      // Ignore JSON parse errors if response is plain text
    }
    throw new Error(errorMessage);
  }
  return await res.json();
};

export const api = {
  // Authentication
  login: async (username, password) => {
    const res = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    const data = await handleResponse(res);
    if (data.token) {
      localStorage.setItem('mams_jwt_token', data.token);
      localStorage.setItem('mams_user', JSON.stringify(data));
    }
    return data;
  },

  logout: () => {
    localStorage.removeItem('mams_jwt_token');
    localStorage.removeItem('mams_user');
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('mams_user');
    return user ? JSON.parse(user) : null;
  },

  // Metadata
  getBases: async () => {
    const res = await fetch(`${API_BASE_URL}/bases`, { headers: getAuthHeaders() });
    return await handleResponse(res);
  },

  getEquipmentTypes: async () => {
    const res = await fetch(`${API_BASE_URL}/equipment-types`, { headers: getAuthHeaders() });
    return await handleResponse(res);
  },

  // Telemetry Dashboard
  getDashboardMetrics: async (filters = {}) => {
    const query = new URLSearchParams();
    if (filters.baseId) query.append('baseId', filters.baseId);
    if (filters.equipmentTypeId) query.append('equipmentTypeId', filters.equipmentTypeId);
    if (filters.startDate) query.append('startDate', filters.startDate);
    if (filters.endDate) query.append('endDate', filters.endDate);

    const res = await fetch(`${API_BASE_URL}/dashboard/metrics?${query.toString()}`, { headers: getAuthHeaders() });
    return await handleResponse(res);
  },

  getNetMovementDetails: async (filters = {}) => {
    const query = new URLSearchParams();
    if (filters.baseId) query.append('baseId', filters.baseId);
    if (filters.equipmentTypeId) query.append('equipmentTypeId', filters.equipmentTypeId);
    if (filters.startDate) query.append('startDate', filters.startDate);
    if (filters.endDate) query.append('endDate', filters.endDate);

    const res = await fetch(`${API_BASE_URL}/dashboard/net-movement-details?${query.toString()}`, { headers: getAuthHeaders() });
    return await handleResponse(res);
  },

  // Purchases
  getPurchases: async (filters = {}) => {
    const query = new URLSearchParams();
    if (filters.baseId) query.append('baseId', filters.baseId);
    if (filters.equipmentTypeId) query.append('equipmentTypeId', filters.equipmentTypeId);
    if (filters.startDate) query.append('startDate', filters.startDate);
    if (filters.endDate) query.append('endDate', filters.endDate);

    const res = await fetch(`${API_BASE_URL}/purchases?${query.toString()}`, { headers: getAuthHeaders() });
    return await handleResponse(res);
  },

  recordPurchase: async (payload) => {
    const res = await fetch(`${API_BASE_URL}/purchases`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(payload)
    });
    return await handleResponse(res);
  },

  // Transfers
  getTransfers: async (filters = {}) => {
    const query = new URLSearchParams();
    if (filters.baseId) query.append('baseId', filters.baseId);
    if (filters.equipmentTypeId) query.append('equipmentTypeId', filters.equipmentTypeId);
    if (filters.startDate) query.append('startDate', filters.startDate);
    if (filters.endDate) query.append('endDate', filters.endDate);

    const res = await fetch(`${API_BASE_URL}/transfers?${query.toString()}`, { headers: getAuthHeaders() });
    return await handleResponse(res);
  },

  initiateTransfer: async (payload) => {
    const res = await fetch(`${API_BASE_URL}/transfers`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(payload)
    });
    return await handleResponse(res);
  },

  updateTransferStatus: async (transferId, status) => {
    const res = await fetch(`${API_BASE_URL}/transfers/${transferId}/status?status=${status}`, {
      method: 'PUT',
      headers: getAuthHeaders()
    });
    return await handleResponse(res);
  },

  // Assignments
  getAssignments: async (filters = {}) => {
    const query = new URLSearchParams();
    if (filters.baseId) query.append('baseId', filters.baseId);
    if (filters.equipmentTypeId) query.append('equipmentTypeId', filters.equipmentTypeId);
    if (filters.startDate) query.append('startDate', filters.startDate);
    if (filters.endDate) query.append('endDate', filters.endDate);

    const res = await fetch(`${API_BASE_URL}/assignments?${query.toString()}`, { headers: getAuthHeaders() });
    return await handleResponse(res);
  },

  createAssignment: async (payload) => {
    const res = await fetch(`${API_BASE_URL}/assignments`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(payload)
    });
    return await handleResponse(res);
  },

  returnAssignment: async (assignmentId) => {
    const res = await fetch(`${API_BASE_URL}/assignments/${assignmentId}/return`, {
      method: 'PUT',
      headers: getAuthHeaders()
    });
    return await handleResponse(res);
  },

  // Expenditures
  getExpenditures: async (filters = {}) => {
    const query = new URLSearchParams();
    if (filters.baseId) query.append('baseId', filters.baseId);
    if (filters.equipmentTypeId) query.append('equipmentTypeId', filters.equipmentTypeId);
    if (filters.startDate) query.append('startDate', filters.startDate);
    if (filters.endDate) query.append('endDate', filters.endDate);

    const res = await fetch(`${API_BASE_URL}/expenditures?${query.toString()}`, { headers: getAuthHeaders() });
    return await handleResponse(res);
  },

  recordExpenditure: async (payload) => {
    const res = await fetch(`${API_BASE_URL}/expenditures`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(payload)
    });
    return await handleResponse(res);
  },

  // Audit Logs (ADMIN only)
  getAuditLogs: async () => {
    const res = await fetch(`${API_BASE_URL}/audit-logs`, { headers: getAuthHeaders() });
    return await handleResponse(res);
  }
};
