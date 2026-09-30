import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import FilterBar from './components/FilterBar';
import Dashboard from './components/Dashboard';
import PurchasesPage from './components/PurchasesPage';
import TransfersPage from './components/TransfersPage';
import AssignmentsExpendituresPage from './components/AssignmentsExpendituresPage';
import AuditLogsPage from './components/AuditLogsPage';
import LoginModal from './components/LoginModal';
import { api } from './api';

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [currentUser, setCurrentUser] = useState(() => api.getCurrentUser());

  // Dropdown reference options
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);
  const [metaError, setMetaError] = useState('');

  // Telemetry Filters
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    baseId: '',
    equipmentTypeId: ''
  });

  useEffect(() => {
    if (currentUser) {
      loadMetaData();
      if (currentUser.role === 'BASE_COMMANDER' && currentUser.assignedBaseId) {
        setFilters(prev => ({ ...prev, baseId: String(currentUser.assignedBaseId) }));
      }
    }
  }, [currentUser]);

  const loadMetaData = async () => {
    try {
      setMetaError('');
      const fetchedBases = await api.getBases();
      const fetchedEquipments = await api.getEquipmentTypes();
      setBases(fetchedBases);
      setEquipmentTypes(fetchedEquipments);
    } catch (err) {
      setMetaError(err.message);
      if (err.message.includes('401') || err.message.includes('Unauthorized')) {
        handleLogout();
      }
    }
  };

  const handleLoginSuccess = (userData) => {
    setCurrentUser(userData);
    setActiveTab('dashboard');
  };

  const handleLogout = () => {
    api.logout();
    setCurrentUser(null);
  };

  if (!currentUser) {
    return <LoginModal onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans selection:bg-emerald-500 selection:text-white">
      {/* Navbar */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        user={currentUser}
        onLogout={handleLogout}
      />

      {/* Main Content Body */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {metaError && (
          <div className="mb-4 p-3 bg-rose-950/80 border border-rose-800 text-rose-300 rounded-lg text-xs">
            System Error: {metaError}
          </div>
        )}

        {/* Global Filter Bar */}
        <FilterBar
          filters={filters}
          setFilters={setFilters}
          bases={bases}
          equipmentTypes={equipmentTypes}
          user={currentUser}
        />

        {/* Dynamic Page Views */}
        {activeTab === 'dashboard' && <Dashboard filters={filters} />}

        {activeTab === 'purchases' && (
          <PurchasesPage
            filters={filters}
            bases={bases}
            equipmentTypes={equipmentTypes}
            user={currentUser}
          />
        )}

        {activeTab === 'transfers' && (
          <TransfersPage
            filters={filters}
            bases={bases}
            equipmentTypes={equipmentTypes}
            user={currentUser}
          />
        )}

        {activeTab === 'assignments' && currentUser.role !== 'LOGISTICS_OFFICER' && (
          <AssignmentsExpendituresPage
            filters={filters}
            bases={bases}
            equipmentTypes={equipmentTypes}
            user={currentUser}
          />
        )}

        {activeTab === 'audit-logs' && currentUser.role === 'ADMIN' && (
          <AuditLogsPage />
        )}
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-900 bg-slate-950 py-4 text-center text-xs text-slate-500 font-mono">
        Military Asset Management System (MAMS) &copy; 2026 — Defensive Command Telemetry System
      </footer>
    </div>
  );
}
