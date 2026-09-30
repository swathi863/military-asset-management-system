import React, { useState } from 'react';
import { X, ArrowDownRight, ArrowUpRight, ShoppingCart, Layers, Info } from 'lucide-react';

export default function NetMovementModal({ isOpen, onClose, details }) {
  const [activeSubTab, setActiveSubTab] = useState('purchases');

  if (!isOpen || !details) return null;

  const purchases = details.purchases || [];
  const transfersIn = details.transfersIn || [];
  const transfersOut = details.transfersOut || [];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-4xl w-full max-h-[90vh] flex flex-col shadow-2xl overflow-hidden">
        
        {/* Modal Header */}
        <div className="px-6 py-4 border-b border-slate-800 flex items-center justify-between bg-slate-950">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-emerald-500/10 border border-emerald-500/30 rounded-lg text-emerald-400">
              <Layers className="h-5 w-5" />
            </div>
            <div>
              <h3 className="text-lg font-bold text-slate-100 flex items-center gap-2">
                Net Asset Movement Breakdown
                <span className="text-xs bg-emerald-950 text-emerald-400 font-mono px-2 py-0.5 rounded border border-emerald-800">
                  Net = Purchases + Transfers In - Transfers Out
                </span>
              </h3>
              <p className="text-xs text-slate-400">Detailed transaction log for the selected telemetry period</p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Metric Overview Bar inside Modal */}
        <div className="grid grid-cols-4 gap-4 px-6 py-4 bg-slate-900/60 border-b border-slate-800/80 text-center">
          <div className="bg-slate-950/60 border border-slate-800 p-3 rounded-xl">
            <span className="text-[10px] font-bold text-slate-400 uppercase">Purchases (+)</span>
            <div className="text-lg font-mono font-bold text-emerald-400">+{details.totalPurchasesQuantity}</div>
          </div>
          <div className="bg-slate-950/60 border border-slate-800 p-3 rounded-xl">
            <span className="text-[10px] font-bold text-slate-400 uppercase">Transfers In (+)</span>
            <div className="text-lg font-mono font-bold text-blue-400">+{details.totalTransfersInQuantity}</div>
          </div>
          <div className="bg-slate-950/60 border border-slate-800 p-3 rounded-xl">
            <span className="text-[10px] font-bold text-slate-400 uppercase">Transfers Out (-)</span>
            <div className="text-lg font-mono font-bold text-rose-400">-{details.totalTransfersOutQuantity}</div>
          </div>
          <div className="bg-emerald-950/40 border border-emerald-800/60 p-3 rounded-xl">
            <span className="text-[10px] font-bold text-emerald-400 uppercase">Net Movement</span>
            <div className="text-lg font-mono font-bold text-emerald-300">
              {details.netMovement >= 0 ? `+${details.netMovement}` : details.netMovement}
            </div>
          </div>
        </div>

        {/* Sub-tabs Navigation */}
        <div className="flex border-b border-slate-800 px-6 bg-slate-950">
          <button
            onClick={() => setActiveSubTab('purchases')}
            className={`flex items-center space-x-2 py-3 px-4 text-xs font-semibold border-b-2 transition-colors ${
              activeSubTab === 'purchases'
                ? 'border-emerald-500 text-emerald-400'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            <ShoppingCart className="h-4 w-4" />
            <span>Purchases ({purchases.length})</span>
          </button>

          <button
            onClick={() => setActiveSubTab('transfersIn')}
            className={`flex items-center space-x-2 py-3 px-4 text-xs font-semibold border-b-2 transition-colors ${
              activeSubTab === 'transfersIn'
                ? 'border-blue-500 text-blue-400'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            <ArrowDownRight className="h-4 w-4" />
            <span>Transfers In ({transfersIn.length})</span>
          </button>

          <button
            onClick={() => setActiveSubTab('transfersOut')}
            className={`flex items-center space-x-2 py-3 px-4 text-xs font-semibold border-b-2 transition-colors ${
              activeSubTab === 'transfersOut'
                ? 'border-rose-500 text-rose-400'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            <ArrowUpRight className="h-4 w-4" />
            <span>Transfers Out ({transfersOut.length})</span>
          </button>
        </div>

        {/* Tab Content */}
        <div className="p-6 overflow-y-auto flex-1">
          {activeSubTab === 'purchases' && (
            <div>
              {purchases.length === 0 ? (
                <div className="text-center py-8 text-slate-500 text-sm flex flex-col items-center gap-2">
                  <Info className="h-6 w-6 text-slate-600" />
                  No purchase transactions recorded for the current filter parameters.
                </div>
              ) : (
                <div className="overflow-x-auto border border-slate-800 rounded-xl">
                  <table className="w-full text-left text-xs text-slate-300">
                    <thead className="bg-slate-950 text-slate-400 uppercase tracking-wider font-mono">
                      <tr>
                        <th className="p-3">Order #</th>
                        <th className="p-3">Base</th>
                        <th className="p-3">Equipment</th>
                        <th className="p-3 text-right">Qty</th>
                        <th className="p-3 text-right">Unit Cost</th>
                        <th className="p-3">Vendor</th>
                        <th className="p-3">Date</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-800 font-mono">
                      {purchases.map(p => (
                        <tr key={p.id} className="hover:bg-slate-800/40">
                          <td className="p-3 font-semibold text-emerald-400">{p.purchaseOrderNumber}</td>
                          <td className="p-3 text-slate-300">{p.base?.name}</td>
                          <td className="p-3 text-slate-200">{p.equipmentType?.name}</td>
                          <td className="p-3 text-right font-bold text-emerald-400">+{p.quantity}</td>
                          <td className="p-3 text-right text-slate-300">${p.unitCost?.toLocaleString()}</td>
                          <td className="p-3 text-slate-400">{p.vendorName}</td>
                          <td className="p-3 text-slate-500">{new Date(p.purchaseDate).toLocaleDateString()}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {activeSubTab === 'transfersIn' && (
            <div>
              {transfersIn.length === 0 ? (
                <div className="text-center py-8 text-slate-500 text-sm flex flex-col items-center gap-2">
                  <Info className="h-6 w-6 text-slate-600" />
                  No completed inbound transfers for the current filter parameters.
                </div>
              ) : (
                <div className="overflow-x-auto border border-slate-800 rounded-xl">
                  <table className="w-full text-left text-xs text-slate-300">
                    <thead className="bg-slate-950 text-slate-400 uppercase tracking-wider font-mono">
                      <tr>
                        <th className="p-3">Transfer Code</th>
                        <th className="p-3">From Source Base</th>
                        <th className="p-3">To Target Base</th>
                        <th className="p-3">Equipment</th>
                        <th className="p-3 text-right">Qty Received</th>
                        <th className="p-3">Date</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-800 font-mono">
                      {transfersIn.map(t => (
                        <tr key={t.id} className="hover:bg-slate-800/40">
                          <td className="p-3 font-semibold text-blue-400">{t.transferCode}</td>
                          <td className="p-3 text-slate-400">{t.sourceBase?.name}</td>
                          <td className="p-3 text-slate-200 font-semibold">{t.targetBase?.name}</td>
                          <td className="p-3 text-slate-200">{t.equipmentType?.name}</td>
                          <td className="p-3 text-right font-bold text-blue-400">+{t.quantity}</td>
                          <td className="p-3 text-slate-500">{new Date(t.transferDate).toLocaleDateString()}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {activeSubTab === 'transfersOut' && (
            <div>
              {transfersOut.length === 0 ? (
                <div className="text-center py-8 text-slate-500 text-sm flex flex-col items-center gap-2">
                  <Info className="h-6 w-6 text-slate-600" />
                  No completed outbound transfers for the current filter parameters.
                </div>
              ) : (
                <div className="overflow-x-auto border border-slate-800 rounded-xl">
                  <table className="w-full text-left text-xs text-slate-300">
                    <thead className="bg-slate-950 text-slate-400 uppercase tracking-wider font-mono">
                      <tr>
                        <th className="p-3">Transfer Code</th>
                        <th className="p-3">From Source Base</th>
                        <th className="p-3">To Target Base</th>
                        <th className="p-3">Equipment</th>
                        <th className="p-3 text-right">Qty Sent</th>
                        <th className="p-3">Date</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-800 font-mono">
                      {transfersOut.map(t => (
                        <tr key={t.id} className="hover:bg-slate-800/40">
                          <td className="p-3 font-semibold text-rose-400">{t.transferCode}</td>
                          <td className="p-3 text-slate-200 font-semibold">{t.sourceBase?.name}</td>
                          <td className="p-3 text-slate-400">{t.targetBase?.name}</td>
                          <td className="p-3 text-slate-200">{t.equipmentType?.name}</td>
                          <td className="p-3 text-right font-bold text-rose-400">-{t.quantity}</td>
                          <td className="p-3 text-slate-500">{new Date(t.transferDate).toLocaleDateString()}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Modal Footer */}
        <div className="px-6 py-3 border-t border-slate-800 bg-slate-950 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs rounded-lg transition-colors font-semibold"
          >
            Close Breakdown Window
          </button>
        </div>

      </div>
    </div>
  );
}
