import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { ShoppingCart, Plus, CheckCircle2, AlertTriangle } from 'lucide-react';

export default function PurchasesPage({ filters, bases, equipmentTypes, user }) {
  const [purchases, setPurchases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [formError, setFormError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [notification, setNotification] = useState('');

  const defaultBaseId = user?.role === 'BASE_COMMANDER' ? user.assignedBaseId : (bases[0]?.id || '');

  // Form State
  const [formData, setFormData] = useState({
    baseId: defaultBaseId,
    equipmentTypeId: equipmentTypes[0]?.id || '',
    quantity: 1,
    unitCost: 100,
    vendorName: '',
    remarks: ''
  });

  useEffect(() => {
    if (user?.role === 'BASE_COMMANDER' && user.assignedBaseId) {
      setFormData(prev => ({ ...prev, baseId: user.assignedBaseId }));
    }
  }, [user]);

  useEffect(() => {
    fetchPurchases();
  }, [filters]);

  const fetchPurchases = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getPurchases(filters);
      setPurchases(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError('');
    try {
      await api.recordPurchase({
        ...formData,
        quantity: Number(formData.quantity),
        unitCost: Number(formData.unitCost)
      });
      setNotification('Purchase recorded successfully and stock updated in MySQL!');
      setShowModal(false);
      fetchPurchases();
      setTimeout(() => setNotification(''), 4000);
    } catch (err) {
      setFormError(err.message);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 bg-slate-900 border border-slate-800 p-5 rounded-xl">
        <div className="flex items-center space-x-3">
          <div className="p-2.5 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 rounded-lg">
            <ShoppingCart className="h-6 w-6" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-100 uppercase tracking-wide">Asset Procurement & Purchases</h2>
            <p className="text-xs text-slate-400">Record and track strategic asset purchases across military installations</p>
          </div>
        </div>

        <button
          onClick={() => {
            setFormError('');
            setShowModal(true);
          }}
          className="flex items-center space-x-2 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs rounded-lg shadow-lg transition-all"
        >
          <Plus className="h-4 w-4" />
          <span>Record New Purchase</span>
        </button>
      </div>

      {notification && (
        <div className="bg-emerald-950/80 border border-emerald-800 text-emerald-300 p-3 rounded-lg text-xs flex items-center gap-2">
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

      {/* Historical Purchases Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-md">
        <div className="px-6 py-4 border-b border-slate-800 flex items-center justify-between">
          <h3 className="text-sm font-bold text-slate-200 uppercase tracking-wider">Historical Purchase Audit Register</h3>
          <span className="text-xs text-slate-400 font-mono">Total Records: {purchases.length}</span>
        </div>

        {loading ? (
          <div className="p-8 text-center text-slate-400 text-xs">Loading purchase logs from backend...</div>
        ) : purchases.length === 0 ? (
          <div className="p-8 text-center text-slate-500 text-xs">No purchase records matching current telemetry filters.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-300">
              <thead className="bg-slate-950 text-slate-400 uppercase tracking-wider font-mono">
                <tr>
                  <th className="p-3.5">PO Number</th>
                  <th className="p-3.5">Base</th>
                  <th className="p-3.5">Equipment Type</th>
                  <th className="p-3.5 text-right">Quantity</th>
                  <th className="p-3.5 text-right">Unit Cost</th>
                  <th className="p-3.5 text-right">Total Cost</th>
                  <th className="p-3.5">Vendor / Supplier</th>
                  <th className="p-3.5">Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800 font-mono">
                {purchases.map((p) => (
                  <tr key={p.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="p-3.5 font-bold text-emerald-400">{p.purchaseOrderNumber}</td>
                    <td className="p-3.5 text-slate-200">{p.base?.name}</td>
                    <td className="p-3.5 font-semibold text-slate-100">{p.equipmentType?.name}</td>
                    <td className="p-3.5 text-right font-bold text-emerald-400">+{p.quantity}</td>
                    <td className="p-3.5 text-right text-slate-300">${p.unitCost?.toLocaleString()}</td>
                    <td className="p-3.5 text-right font-bold text-slate-100">${(p.totalCost || p.quantity * p.unitCost)?.toLocaleString()}</td>
                    <td className="p-3.5 text-slate-400">{p.vendorName}</td>
                    <td className="p-3.5 text-slate-500">{new Date(p.purchaseDate).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Record Purchase Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 uppercase tracking-wide flex items-center gap-2">
                <ShoppingCart className="h-5 w-5 text-emerald-400" />
                Record Asset Purchase
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
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Target Base</label>
                <select
                  value={formData.baseId}
                  disabled={user?.role === 'BASE_COMMANDER'}
                  onChange={(e) => setFormData({ ...formData, baseId: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
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
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
                >
                  {equipmentTypes.map(e => <option key={e.id} value={e.id}>{e.name} ({e.category})</option>)}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Quantity</label>
                  <input
                    type="number"
                    min="1"
                    value={formData.quantity}
                    onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                    required
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500 font-mono"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Unit Cost ($)</label>
                  <input
                    type="number"
                    min="0"
                    step="0.01"
                    value={formData.unitCost}
                    onChange={(e) => setFormData({ ...formData, unitCost: e.target.value })}
                    required
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500 font-mono"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Vendor / Defense Supplier</label>
                <input
                  type="text"
                  placeholder="e.g., Colt Defense, Oshkosh, Raytheon"
                  value={formData.vendorName}
                  onChange={(e) => setFormData({ ...formData, vendorName: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Remarks / Order Purpose</label>
                <textarea
                  rows="2"
                  value={formData.remarks}
                  onChange={(e) => setFormData({ ...formData, remarks: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
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
                  className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white text-xs rounded-lg font-semibold shadow-md"
                >
                  Submit & Update Inventory
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
