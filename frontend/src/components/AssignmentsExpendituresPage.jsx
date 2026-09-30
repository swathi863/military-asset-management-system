import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { Users, Flame, Plus, RotateCcw, CheckCircle2, AlertTriangle } from 'lucide-react';

export default function AssignmentsExpendituresPage({ filters, bases, equipmentTypes, user }) {
  const [activeSubTab, setActiveSubTab] = useState('assignments');

  // Data states
  const [assignments, setAssignments] = useState([]);
  const [expenditures, setExpenditures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [formError, setFormError] = useState('');
  const [notification, setNotification] = useState('');

  // Modal states
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [showExpendModal, setShowExpendModal] = useState(false);

  const isCommander = user?.role === 'BASE_COMMANDER';
  const defaultBaseId = isCommander ? user.assignedBaseId : (bases[0]?.id || '');

  // Forms
  const [assignForm, setAssignForm] = useState({
    baseId: defaultBaseId,
    equipmentTypeId: equipmentTypes[0]?.id || '',
    assignedToPersonnel: '',
    serviceId: '',
    rankTitle: 'Sergeant',
    quantity: 1,
    expectedReturnDate: '',
    notes: ''
  });

  const [expendForm, setExpendForm] = useState({
    baseId: defaultBaseId,
    equipmentTypeId: equipmentTypes[0]?.id || '',
    quantity: 1,
    reason: 'TRAINING',
    operationName: '',
    remarks: ''
  });

  useEffect(() => {
    if (isCommander && user.assignedBaseId) {
      setAssignForm(prev => ({ ...prev, baseId: user.assignedBaseId }));
      setExpendForm(prev => ({ ...prev, baseId: user.assignedBaseId }));
    }
  }, [user]);

  useEffect(() => {
    fetchData();
  }, [filters]);

  const fetchData = async () => {
    setLoading(true);
    setError('');
    try {
      const assignData = await api.getAssignments(filters);
      const expendData = await api.getExpenditures(filters);
      setAssignments(assignData);
      setExpenditures(expendData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAssignSubmit = async (e) => {
    e.preventDefault();
    setFormError('');
    try {
      await api.createAssignment({
        ...assignForm,
        quantity: Number(assignForm.quantity)
      });
      setNotification('Asset successfully assigned to personnel!');
      setShowAssignModal(false);
      fetchData();
      setTimeout(() => setNotification(''), 4000);
    } catch (err) {
      setFormError(err.message);
    }
  };

  const handleExpendSubmit = async (e) => {
    e.preventDefault();
    setFormError('');
    try {
      await api.recordExpenditure({
        ...expendForm,
        quantity: Number(expendForm.quantity)
      });
      setNotification('Expenditure recorded! Stock balance updated in MySQL.');
      setShowExpendModal(false);
      fetchData();
      setTimeout(() => setNotification(''), 4000);
    } catch (err) {
      setFormError(err.message);
    }
  };

  const handleReturnAsset = async (id) => {
    try {
      await api.returnAssignment(id);
      setNotification('Assigned asset returned to inventory!');
      fetchData();
      setTimeout(() => setNotification(''), 4000);
    } catch (err) {
      alert(`Error returning asset: ${err.message}`);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 bg-slate-900 border border-slate-800 p-5 rounded-xl">
        <div className="flex items-center space-x-3">
          <div className="p-2.5 bg-amber-500/10 text-amber-400 border border-amber-500/20 rounded-lg">
            <Users className="h-6 w-6" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-100 uppercase tracking-wide">Personnel Assignments & Expenditures</h2>
            <p className="text-xs text-slate-400">Track asset issue to service members and record operational ammo & equipment expenditures</p>
          </div>
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={() => {
              setFormError('');
              setShowAssignModal(true);
            }}
            className="flex items-center space-x-2 px-3.5 py-2 bg-amber-600 hover:bg-amber-500 text-white font-semibold text-xs rounded-lg shadow-md transition-all"
          >
            <Plus className="h-4 w-4" />
            <span>Assign Asset</span>
          </button>
          <button
            onClick={() => {
              setFormError('');
              setShowExpendModal(true);
            }}
            className="flex items-center space-x-2 px-3.5 py-2 bg-rose-600 hover:bg-rose-500 text-white font-semibold text-xs rounded-lg shadow-md transition-all"
          >
            <Flame className="h-4 w-4" />
            <span>Record Expenditure</span>
          </button>
        </div>
      </div>

      {notification && (
        <div className="bg-amber-950/80 border border-amber-800 text-amber-300 p-3 rounded-lg text-xs flex items-center gap-2">
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

      {/* Tabs */}
      <div className="flex border-b border-slate-800 bg-slate-900/60 rounded-t-xl px-4 pt-2">
        <button
          onClick={() => setActiveSubTab('assignments')}
          className={`flex items-center space-x-2 py-3 px-5 text-xs font-bold border-b-2 transition-colors uppercase tracking-wider ${
            activeSubTab === 'assignments'
              ? 'border-amber-500 text-amber-400 bg-slate-900/80 rounded-t-lg'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Users className="h-4 w-4" />
          <span>Active Assignments ({assignments.length})</span>
        </button>

        <button
          onClick={() => setActiveSubTab('expenditures')}
          className={`flex items-center space-x-2 py-3 px-5 text-xs font-bold border-b-2 transition-colors uppercase tracking-wider ${
            activeSubTab === 'expenditures'
              ? 'border-rose-500 text-rose-400 bg-slate-900/80 rounded-t-lg'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Flame className="h-4 w-4" />
          <span>Expended Assets Register ({expenditures.length})</span>
        </button>
      </div>

      {/* Content area */}
      <div className="bg-slate-900 border border-slate-800 rounded-b-2xl overflow-hidden shadow-md">
        {activeSubTab === 'assignments' && (
          <div>
            {loading ? (
              <div className="p-8 text-center text-slate-400 text-xs">Loading assignment logs from backend...</div>
            ) : assignments.length === 0 ? (
              <div className="p-8 text-center text-slate-500 text-xs">No personnel assignment records matching current filters.</div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs text-slate-300">
                  <thead className="bg-slate-950 text-slate-400 uppercase tracking-wider font-mono">
                    <tr>
                      <th className="p-3.5">Code</th>
                      <th className="p-3.5">Assigned Personnel</th>
                      <th className="p-3.5">Service ID</th>
                      <th className="p-3.5">Base</th>
                      <th className="p-3.5">Equipment Type</th>
                      <th className="p-3.5 text-right">Qty</th>
                      <th className="p-3.5">Status</th>
                      <th className="p-3.5">Due Date</th>
                      <th className="p-3.5 text-center">Action</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800 font-mono">
                    {assignments.map((a) => (
                      <tr key={a.id} className="hover:bg-slate-800/40 transition-colors">
                        <td className="p-3.5 font-bold text-amber-400">{a.assignmentCode}</td>
                        <td className="p-3.5 font-semibold text-slate-100">{a.rankTitle} {a.assignedToPersonnel}</td>
                        <td className="p-3.5 text-slate-400">{a.serviceId}</td>
                        <td className="p-3.5 text-slate-300">{a.base?.name}</td>
                        <td className="p-3.5 font-semibold text-slate-200">{a.equipmentType?.name}</td>
                        <td className="p-3.5 text-right font-bold text-amber-400">{a.quantity}</td>
                        <td className="p-3.5">
                          {a.status === 'ACTIVE' ? (
                            <span className="bg-amber-950 text-amber-400 border border-amber-800 px-2 py-0.5 rounded text-[10px] font-bold">ACTIVE</span>
                          ) : (
                            <span className="bg-emerald-950 text-emerald-400 border border-emerald-800 px-2 py-0.5 rounded text-[10px] font-bold">RETURNED</span>
                          )}
                        </td>
                        <td className="p-3.5 text-slate-500">{a.expectedReturnDate || 'N/A'}</td>
                        <td className="p-3.5 text-center">
                          {a.status === 'ACTIVE' ? (
                            <button
                              onClick={() => handleReturnAsset(a.id)}
                              className="px-2.5 py-1 bg-emerald-950 hover:bg-emerald-900 border border-emerald-700 text-emerald-300 rounded text-[10px] font-bold flex items-center gap-1 mx-auto"
                            >
                              <RotateCcw className="h-3 w-3" /> Return Asset
                            </button>
                          ) : (
                            <span className="text-[10px] text-slate-600 font-sans italic">Returned</span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {activeSubTab === 'expenditures' && (
          <div>
            {loading ? (
              <div className="p-8 text-center text-slate-400 text-xs">Loading expenditure logs from backend...</div>
            ) : expenditures.length === 0 ? (
              <div className="p-8 text-center text-slate-500 text-xs">No expenditure records matching current filters.</div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs text-slate-300">
                  <thead className="bg-slate-950 text-slate-400 uppercase tracking-wider font-mono">
                    <tr>
                      <th className="p-3.5">Expenditure Code</th>
                      <th className="p-3.5">Base</th>
                      <th className="p-3.5">Equipment Type</th>
                      <th className="p-3.5 text-right">Qty Expended</th>
                      <th className="p-3.5">Reason</th>
                      <th className="p-3.5">Operation / Drill</th>
                      <th className="p-3.5">Timestamp</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800 font-mono">
                    {expenditures.map((e) => (
                      <tr key={e.id} className="hover:bg-slate-800/40 transition-colors">
                        <td className="p-3.5 font-bold text-rose-400">{e.expenditureCode}</td>
                        <td className="p-3.5 text-slate-300">{e.base?.name}</td>
                        <td className="p-3.5 font-semibold text-slate-200">{e.equipmentType?.name}</td>
                        <td className="p-3.5 text-right font-bold text-rose-400">-{e.quantity}</td>
                        <td className="p-3.5 font-semibold text-slate-100 uppercase">{e.reason}</td>
                        <td className="p-3.5 text-slate-400">{e.operationName || 'N/A'}</td>
                        <td className="p-3.5 text-slate-500">{new Date(e.expenditureDate).toLocaleDateString()}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Assign Modal */}
      {showAssignModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 uppercase tracking-wide flex items-center gap-2">
                <Users className="h-5 w-5 text-amber-400" />
                Assign Asset to Personnel
              </h3>
              <button onClick={() => setShowAssignModal(false)} className="text-slate-400 hover:text-white">✕</button>
            </div>

            {formError && (
              <div className="bg-rose-950/80 border border-rose-800 text-rose-300 p-2.5 rounded-lg text-xs flex items-center gap-2">
                <AlertTriangle className="h-4 w-4 text-rose-400 shrink-0" />
                <span>{formError}</span>
              </div>
            )}

            <form onSubmit={handleAssignSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Base</label>
                <select
                  value={assignForm.baseId}
                  disabled={isCommander}
                  onChange={(e) => setAssignForm({ ...assignForm, baseId: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-amber-500"
                >
                  {bases.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Equipment / Asset Type</label>
                <select
                  value={assignForm.equipmentTypeId}
                  onChange={(e) => setAssignForm({ ...assignForm, equipmentTypeId: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-amber-500"
                >
                  {equipmentTypes.map(e => <option key={e.id} value={e.id}>{e.name} ({e.category})</option>)}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Rank / Title</label>
                  <input
                    type="text"
                    placeholder="e.g., Sergeant, Captain"
                    value={assignForm.rankTitle}
                    onChange={(e) => setAssignForm({ ...assignForm, rankTitle: e.target.value })}
                    required
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-amber-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Service ID / Mil ID</label>
                  <input
                    type="text"
                    placeholder="e.g., MIL-98421"
                    value={assignForm.serviceId}
                    onChange={(e) => setAssignForm({ ...assignForm, serviceId: e.target.value })}
                    required
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-amber-500 font-mono"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Assigned Personnel Name</label>
                <input
                  type="text"
                  placeholder="e.g., John Miller"
                  value={assignForm.assignedToPersonnel}
                  onChange={(e) => setAssignForm({ ...assignForm, assignedToPersonnel: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Quantity</label>
                  <input
                    type="number"
                    min="1"
                    value={assignForm.quantity}
                    onChange={(e) => setAssignForm({ ...assignForm, quantity: e.target.value })}
                    required
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-amber-500 font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Expected Return Date</label>
                  <input
                    type="date"
                    value={assignForm.expectedReturnDate}
                    onChange={(e) => setAssignForm({ ...assignForm, expectedReturnDate: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-amber-500"
                  />
                </div>
              </div>

              <div className="flex justify-end space-x-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setShowAssignModal(false)}
                  className="px-3.5 py-2 bg-slate-800 text-slate-300 text-xs rounded-lg font-semibold hover:bg-slate-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-amber-600 hover:bg-amber-500 text-white text-xs rounded-lg font-semibold shadow-md"
                >
                  Issue Asset Assignment
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Record Expenditure Modal */}
      {showExpendModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 uppercase tracking-wide flex items-center gap-2">
                <Flame className="h-5 w-5 text-rose-400" />
                Record Asset Expenditure
              </h3>
              <button onClick={() => setShowExpendModal(false)} className="text-slate-400 hover:text-white">✕</button>
            </div>

            {formError && (
              <div className="bg-rose-950/80 border border-rose-800 text-rose-300 p-2.5 rounded-lg text-xs flex items-center gap-2">
                <AlertTriangle className="h-4 w-4 text-rose-400 shrink-0" />
                <span>{formError}</span>
              </div>
            )}

            <form onSubmit={handleExpendSubmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Base</label>
                <select
                  value={expendForm.baseId}
                  disabled={isCommander}
                  onChange={(e) => setExpendForm({ ...expendForm, baseId: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-rose-500"
                >
                  {bases.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Equipment / Asset Type</label>
                <select
                  value={expendForm.equipmentTypeId}
                  onChange={(e) => setExpendForm({ ...expendForm, equipmentTypeId: e.target.value })}
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-rose-500"
                >
                  {equipmentTypes.map(e => <option key={e.id} value={e.id}>{e.name} ({e.category})</option>)}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Quantity Expended</label>
                  <input
                    type="number"
                    min="1"
                    value={expendForm.quantity}
                    onChange={(e) => setExpendForm({ ...expendForm, quantity: e.target.value })}
                    required
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-rose-500 font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Reason</label>
                  <select
                    value={expendForm.reason}
                    onChange={(e) => setExpendForm({ ...expendForm, reason: e.target.value })}
                    required
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-rose-500"
                  >
                    <option value="TRAINING">TRAINING EXERCISE</option>
                    <option value="OPERATIONAL_USAGE">OPERATIONAL USAGE</option>
                    <option value="DAMAGED_DISPOSAL">DAMAGED / DISPOSAL</option>
                    <option value="EXPIRED">EXPIRED SHELF-LIFE</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Operation / Drill Name</label>
                <input
                  type="text"
                  placeholder="e.g., Exercise Valor Shield 2026"
                  value={expendForm.operationName}
                  onChange={(e) => setExpendForm({ ...expendForm, operationName: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 text-xs text-slate-200 focus:outline-none focus:border-rose-500"
                />
              </div>

              <div className="flex justify-end space-x-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setShowExpendModal(false)}
                  className="px-3.5 py-2 bg-slate-800 text-slate-300 text-xs rounded-lg font-semibold hover:bg-slate-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-rose-600 hover:bg-rose-500 text-white text-xs rounded-lg font-semibold shadow-md"
                >
                  Record Expenditure
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
