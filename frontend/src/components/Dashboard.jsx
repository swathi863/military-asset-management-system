import React, { useState, useEffect } from 'react';
import { api } from '../api';
import NetMovementModal from './NetMovementModal';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import { Shield, TrendingUp, ArrowLeftRight, UserCheck, Flame, Box, ExternalLink, Activity, AlertTriangle } from 'lucide-react';

export default function Dashboard({ filters }) {
  const [metrics, setMetrics] = useState(null);
  const [netDetails, setNetDetails] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, [filters]);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getDashboardMetrics(filters);
      setMetrics(data);

      const details = await api.getNetMovementDetails(filters);
      setNetDetails(details);
    } catch (err) {
      setError(err.message || 'Failed to load telemetry metrics from backend.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20 text-slate-400">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-500 mr-3"></div>
        <span>Fetching authenticated asset telemetry from backend...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-rose-950/80 border border-rose-800 text-rose-300 p-6 rounded-2xl flex items-center gap-3">
        <AlertTriangle className="h-6 w-6 text-rose-400 shrink-0" />
        <div>
          <h3 className="font-bold text-sm uppercase">Telemetry Load Error</h3>
          <p className="text-xs text-rose-200 mt-0.5">{error}</p>
        </div>
      </div>
    );
  }

  const chartData = [
    { name: 'Opening Bal.', count: metrics.openingBalance, fill: '#64748b' },
    { name: 'Purchases', count: metrics.purchasesCount, fill: '#10b981' },
    { name: 'Transfers In', count: metrics.transfersIn, fill: '#3b82f6' },
    { name: 'Transfers Out', count: metrics.transfersOut, fill: '#ef4444' },
    { name: 'Assigned', count: metrics.assignedAssets, fill: '#f59e0b' },
    { name: 'Expended', count: metrics.expendedAssets, fill: '#dc2626' },
    { name: 'Closing Bal.', count: metrics.closingBalance, fill: '#059669' },
  ];

  return (
    <div className="space-y-6">
      {/* Telemetry Status Bar */}
      <div className="flex items-center justify-between bg-slate-900 border border-slate-800 p-4 rounded-xl">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-emerald-500/10 text-emerald-400 rounded-lg">
            <Activity className="h-5 w-5 animate-pulse" />
          </div>
          <div>
            <h2 className="text-base font-bold text-slate-100 uppercase tracking-wide">Command Asset Overview</h2>
            <p className="text-xs text-slate-400 font-mono">Net Movement = Purchases + Transfers In - Transfers Out</p>
          </div>
        </div>

        <div className="text-right font-mono text-xs text-slate-400">
          Status: <span className="text-emerald-400 font-bold">LIVE API SYNC</span>
        </div>
      </div>

      {/* Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
        {/* Opening Balance */}
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 shadow-sm hover:border-slate-700 transition-all">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-400 uppercase">Opening Balance</span>
            <Box className="h-4 w-4 text-slate-400" />
          </div>
          <div className="mt-3 text-2xl font-mono font-bold text-slate-100">
            {metrics.openingBalance.toLocaleString()}
          </div>
          <span className="text-[10px] text-slate-500">Stock at period start</span>
        </div>

        {/* Net Movement Card - CLICKABLE POPUP TRIGGER */}
        <div
          onClick={() => setIsModalOpen(true)}
          className="bg-slate-900 border border-emerald-500/40 hover:border-emerald-400 rounded-xl p-4 shadow-lg cursor-pointer transition-all hover:scale-[1.02] group relative overflow-hidden"
        >
          <div className="absolute top-0 right-0 bg-emerald-500/20 text-emerald-400 text-[9px] font-bold px-2 py-0.5 rounded-bl uppercase tracking-wider flex items-center gap-1">
            <span>Pop-up Detail</span>
            <ExternalLink className="h-2.5 w-2.5" />
          </div>
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-emerald-400 uppercase">Net Movement</span>
            <TrendingUp className="h-4 w-4 text-emerald-400 group-hover:translate-x-0.5 transition-transform" />
          </div>
          <div className="mt-3 text-2xl font-mono font-bold text-emerald-300">
            {metrics.netMovement >= 0 ? `+${metrics.netMovement.toLocaleString()}` : metrics.netMovement.toLocaleString()}
          </div>
          <div className="text-[10px] text-emerald-500/80 mt-1 font-mono flex items-center justify-between">
            <span>+{metrics.purchasesCount + metrics.transfersIn} In / -{metrics.transfersOut} Out</span>
            <span className="underline font-semibold text-emerald-400">Click to View</span>
          </div>
        </div>

        {/* Assigned Assets */}
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 shadow-sm hover:border-slate-700 transition-all">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-400 uppercase">Assigned Assets</span>
            <UserCheck className="h-4 w-4 text-amber-400" />
          </div>
          <div className="mt-3 text-2xl font-mono font-bold text-amber-300">
            {metrics.assignedAssets.toLocaleString()}
          </div>
          <span className="text-[10px] text-amber-500/80">Issued to personnel & units</span>
        </div>

        {/* Expended Assets */}
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 shadow-sm hover:border-slate-700 transition-all">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-400 uppercase">Expended Assets</span>
            <Flame className="h-4 w-4 text-rose-400" />
          </div>
          <div className="mt-3 text-2xl font-mono font-bold text-rose-300">
            {metrics.expendedAssets.toLocaleString()}
          </div>
          <span className="text-[10px] text-rose-500/80">Consumed or decommissioned</span>
        </div>

        {/* Closing Balance */}
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 shadow-sm hover:border-slate-700 transition-all">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-400 uppercase">Closing Balance</span>
            <Shield className="h-4 w-4 text-emerald-400" />
          </div>
          <div className="mt-3 text-2xl font-mono font-bold text-slate-100">
            {metrics.closingBalance.toLocaleString()}
          </div>
          <span className="text-[10px] text-slate-500">Calculated inventory total</span>
        </div>

        {/* Available Stock */}
        <div className="bg-slate-950 border border-emerald-800/80 rounded-xl p-4 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-emerald-400 uppercase">Available Stock</span>
            <ArrowLeftRight className="h-4 w-4 text-emerald-400" />
          </div>
          <div className="mt-3 text-2xl font-mono font-bold text-emerald-400">
            {metrics.availableStock.toLocaleString()}
          </div>
          <span className="text-[10px] text-emerald-500">Ready for assignment</span>
        </div>
      </div>

      {/* Visual Chart Section */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-md">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h3 className="text-sm font-bold text-slate-200 uppercase tracking-wider">Asset Distribution Telemetry</h3>
            <p className="text-xs text-slate-400">Comparative metrics across inventory stages</p>
          </div>
        </div>

        <div className="h-72 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
              <XAxis dataKey="name" stroke="#94a3b8" tick={{ fontSize: 12 }} />
              <YAxis stroke="#94a3b8" tick={{ fontSize: 12 }} />
              <Tooltip
                contentStyle={{ backgroundColor: '#0f172a', borderColor: '#334155', borderRadius: '8px', color: '#f8fafc' }}
                cursor={{ fill: '#1e293b' }}
              />
              <Bar dataKey="count" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Pop-up Modal Component for Net Movement */}
      <NetMovementModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        details={netDetails}
      />
    </div>
  );
}
