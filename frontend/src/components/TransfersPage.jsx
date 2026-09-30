import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { ArrowLeftRight, Plus, ArrowRight, CheckCircle, Clock, XCircle, CheckCircle2, AlertTriangle } from 'lucide-react';

export default function TransfersPage({ filters, bases, equipmentTypes, user }) {
  const [transfers, setTransfers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [formError, setFormError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [notification, setNotification] = useState('');

  const isCommander = user?.role === 'BASE_COMMANDER';
  const defaultSourceBase = isCommander ? user.assignedBaseId : (bases[0]?.id || '');
  const defaultTargetBase = bases.find(b => String(b.id) !== String(defaultSourceBase))?.id || bases[1]?.id || '';

  const [formData, setFormData] = useState({
    sourceBaseId: defaultSourceBase,
    targetBaseId: defaultTargetBase,
    equipmentTypeId: equipmentTypes[0]?.id || '',
    quantity: 1,
    remarks: ''
  });

  useEffect(() => {
    if (isCommander && user.assignedBaseId) {
      setFormData(prev => ({
        ...prev,
        sourceBaseId: user.assignedBaseId,
        targetBaseId: bases.find(b => String(b.id) !== String(user.assignedBaseId))?.id || ''
      }));
    }
  }, [user, bases]);

  useEffect(() => {
    fetchTransfers();
  }, [filters]);

  const fetchTransfers = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getTransfers(filters);
      setTransfers(data);
    } catch (err) {
      setError(err.message);
    } fontLine: {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError('');

    if (String(formData.sourceBaseId) === String(formData.targetBaseId)) {
      setFormError('Source base and target base cannot be identical.');
      return;
    }
    if (Number(formData.quantity) <= 0) {
      setFormError('Quantity must be greater than 0.');
      return;
    }

    try {
      await api.initiateTransfer({
        ...formData,
        quantity: Number(formData.quantity)
      });
      setNotification('Transfer initiated! Source stock updated.');
      setShowModal(false);
      fetchTransfers();
      setTimeout(() => setNotification(''), 4000);
    } catch (err) {
      setFormError(err.message);
    }
  };

  const handleStatusUpdate = async (id, status) => {
    try {
      await api.updateTransferStatus(id, status);
      setNotification(`Transfer status changed to ${status}`);
      fetchTransfers();
      setTimeout(() => setNotification(''), 4000);
    } catch (err) {
      alert(`Error updating transfer status: ${err.message}`);
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'COMPLETED':
        return (
          <span className="inline-flex items-center gap-1 bg-emerald-950 text-emerald-400 border border-emerald-800 px-2.5 py-0.5 rounded text-[10px] font-bold uppercase">
            <CheckCircle className="h-3 w-3" /> Completed
          </span>
        );
      case 'IN_TRANSIT':
        return (
          <span className="inline-flex items-center gap-1 bg-blue-950 text-blue-400 border border-blue-800 px-2.5 py-0.5 rounded text-[10px] font-bold uppercase animate-pulse">
            <Clock className="h-3 w-3" /> In Transit
          </span>
        );
      case 'CANCELLED':
        return (
          <span className="inline-flex items-center gap-1 bg-rose-950 text-rose-400 border border-rose-800 px-2.5 py-0.5 rounded text-[10px] font-bold uppercase">
            <XCircle className="h-3 w-3" /> Cancelled
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1 bg-amber-950 text-amber-400 border border-amber-800 px-2.5 py-0.5 rounded text-[10px] font-bold uppercase">
            <Clock className="h-3 w-3" /> Pending
          </span>
        );
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 bg-slate-900 border border-slate-800 p-5 rounded-xl">
        <div className="flex items-center space-x-3">
          <div className="p-2.5 bg-blue-500/10 text-blue-400 border border-blue-500/20 rounded-lg">
            <ArrowLeftRight className="h-6 w-6" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-100 uppercase tracking-wide">Inter-Base Asset Transfers</h2>
            <p className="text-xs text-slate-400">Reallocate military equipment between operational bases with chain of custody tracking</p>
          </div>
        </div>

        <button
          onClick={() => {
            setFormError('');
            setShowModal(true);
          }}
          className="flex items-center space-x-2 px-4 py-2.5 bg-blue-600 hover:bg-blue-500 text-white font-semibold text-xs rounded-lg shadow-lg transition-all"
        >
          <Plus className="h-4 w-4" />
          <span>Initiate New Transfer</span>
        </button>
      </div>

      {notification && (
        <div className="bg-blue-950/80 border border-blue-800 text-blue-300 p-3 rounded-lg text-xs flex items-center gap-2">
          <CheckCircle2 className="h-4 w-4" />
          <span>{notification}</span>
        </div>
      )}

      {error && (
        <div className="bg-rose-950/80 border border-rose-800 text-rose-300 p-4 rounded-xl text-xs flex items-center gap-2">
          <AlertTriangle className="h-5 w-5 text-rose-400 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Transfer History Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-md">
        <div className="px-6 py-4 border-b border-slate-800 flex items-center justify-between">
          <h3 className="text-sm font-bold text-slate-200 uppercase tracking-wider">Transfer Operations Register</h3>
          <span className="text-xs text-slate-400 font-mono">Total Records: {transfers.length}</span>
        </div>

        {loading ? (
          <div className="p-8 text-center text-slate-400 text-xs">Loading transfer telemetry from backend...</div>
        ) : transfers.length === 0 ? (
          <div className="p-8 text-center text-slate-500 text-xs">No transfer records matching current telemetry filters.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-300">
              <thead className="bg-slate-950 text-slate-400 uppercase tracking-wider font-mono">
                <tr>
                  <th className="p-3.5">Transfer Code</th>
                  <th className="p-3.5">Source Base</th>
                  <th className="p-3.5">Target Base</th>
                  <th className="p-3.5">Equipment Type</th>
                  <th className="p-3.5 text-right">Quantity</th>
                  <th className="p-3.5">Status</th>
                  <th className="p-3.5">Transfer Date</th>
                  <th className="p-3.5 text-center">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800 font-mono">
                {transfers.map((t) => (
                  <tr key={t.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="p-3.5 font-bold text-blue-400">{t.transferCode}</td>
                    <td className="p-3.5 text-slate-300">{t.sourceBase?.name}</td>
                    <td className="p-3.5 text-slate-200 font-semibold flex items-center gap-1.5">
                      <ArrowRight className="h-3 w-3 text-blue-400" />
                      {t.targetBase?.name}
                    </td>
                    <td className="p-3.5 font-semibold text-slate-100">{t.equipmentType?.name}</td>
                    <td className="p-3.5 text-right font-bold text-slate-100">{t.quantity}</td>
                    <td className="p-3.5">{getStatusBadge(t.status)}</td>
                    <td className="p-3.5 text-slate-500">{new Date(t.transferDate).toLocaleDateString()}</td>
                    <td className="p-3.5 text-center space-x-1">
                      {t.status === 'IN_TRANSIT' || t.status === 'PENDING' ? (
                        <>
                          <button
                            onClick={() => handleStatusUpdate(t.id, 'COMPLETED')}
                            className="px-2 py-1 bg-emerald-950 hover:bg-emerald-900 border border-emerald-700 text-emerald-300 rounded text-[10px] font-bold"
                          >
                            Mark Complete
                          </button>
                          <button
                            onClick={() => handleStatusUpdate(t.id, 'CANCELLED')}
                            className="px-2 py-1 bg-rose-950 hover:bg-rose-900 border border-rose-700 text-rose-300 rounded text-[10px] font-bold"
                          >
                            Cancel
                          </button>
                        </>
                      ) : (
                        <span className="text-[10px] text-slate-600 font-sans italic">Locked</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Initiate Transfer Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 uppercase tracking-wide flex items-center gap-2">
                <ArrowLeftRight className="h-5 w-5 text-blue-400" />
                Initiate Inter-Base Transfer
              </h3>
              <button onClick={() => setShowModal(false)} className="text-slate-400 hover:text-white">✕</button>
            </div>

            {formError && (
              <div className="bg-rose-950/80 border border-rose-800 text-rose-300 p-2.5 rounded-lg text-xs flex items-center gap-2">
                <AlertTriangle className="h-4 w-4 text-rose-400 shrink-0" />
                <span>{formError}</span>
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-3.5">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Source Base (From)</label>
                <select
                  value={formData.sourceBaseId}
                  disabled={isCommander}
                  onChange={(e) => setFormData({ ...formData, sourceBaseId: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-blue-500"
                >
                  {bases.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Target Base (To)</label>
                <select
                  value={formData.targetBaseId}
                  onChange={(e) => setFormData({ ...formData, targetBaseId: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-blue-500"
                >
                  {bases.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Equipment / Asset Type</label>
                <select
                  value={formData.equipmentTypeId}
                  onChange={(e) => setFormData({ ...formData, equipmentTypeId: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-blue-500"
                >
                  {equipmentTypes.map(e => <option key={e.id} value={e.id}>{e.name} ({e.category})</option>)}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Quantity</label>
                <input
                  type="number"
                  min="1"
                  value={formData.quantity}
                  onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-blue-500 font-mono"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Transfer Purpose / Operational Directive</label>
                <textarea
                  rows="2"
                  value={formData.remarks}
                  onChange={(e) => setFormData({ ...formData, remarks: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-blue-500"
                />
              </div>

              <div className="flex justify-end space-x-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-3.5 py-2 bg-slate-800 text-slate-300 text-xs rounded-lg font-semibold hover:bg-slate-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs rounded-lg font-semibold shadow-md"
                >
                  Dispatch Transfer Order
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
