import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { FileText, Search, AlertTriangle } from 'lucide-react';

export default function AuditLogsPage() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getAuditLogs();
      setLogs(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredLogs = logs.filter(log => {
    const term = searchTerm.toLowerCase();
    return (
      log.username?.toLowerCase().includes(term) ||
      log.action?.toLowerCase().includes(term) ||
      log.resource?.toLowerCase().includes(term) ||
      log.details?.toLowerCase().includes(term)
    );
  });

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 bg-slate-900 border border-slate-800 p-5 rounded-xl">
        <div className="flex items-center space-x-3">
          <div className="p-2.5 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 rounded-lg">
            <FileText className="h-6 w-6" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-100 uppercase tracking-wide">System Transaction Audit Trail</h2>
            <p className="text-xs text-slate-400">Immutable forensic audit log capturing all purchases, transfers, assignments & API operations</p>
          </div>
        </div>

        <div className="relative w-full sm:w-64">
          <Search className="h-4 w-4 absolute left-3 top-2.5 text-slate-500" />
          <input
            type="text"
            placeholder="Search audit trail..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-9 pr-3 py-2 w-full bg-slate-950 border border-slate-800 rounded-lg text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
          />
        </div>
      </div>

      {error && (
        <div className="bg-rose-950/80 border border-rose-800 text-rose-300 p-4 rounded-xl text-xs flex items-center gap-2">
          <AlertTriangle className="h-5 w-5 text-rose-400 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Audit Log Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-md">
        <div className="px-6 py-4 border-b border-slate-800 flex items-center justify-between">
          <h3 className="text-sm font-bold text-slate-200 uppercase tracking-wider">Secured API & Action Records</h3>
          <span className="text-xs text-slate-400 font-mono">Showing {filteredLogs.length} events</span>
        </div>

        {loading ? (
          <div className="p-8 text-center text-slate-400 text-xs">Fetching security audit telemetry from backend...</div>
        ) : filteredLogs.length === 0 ? (
          <div className="p-8 text-center text-slate-500 text-xs">No audit logs matching search term.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-300">
              <thead className="bg-slate-950 text-slate-400 uppercase tracking-wider font-mono">
                <tr>
                  <th className="p-3.5">Timestamp</th>
                  <th className="p-3.5">User</th>
                  <th className="p-3.5">Role</th>
                  <th className="p-3.5">Action Code</th>
                  <th className="p-3.5">Resource</th>
                  <th className="p-3.5">Details</th>
                  <th className="p-3.5 text-center">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800 font-mono">
                {filteredLogs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="p-3.5 text-slate-400 whitespace-nowrap">
                      {new Date(log.timestamp).toLocaleString()}
                    </td>
                    <td className="p-3.5 font-bold text-slate-100">{log.username}</td>
                    <td className="p-3.5">
                      <span className="bg-slate-800 text-slate-300 border border-slate-700 px-2 py-0.5 rounded text-[10px]">
                        {log.userRole}
                      </span>
                    </td>
                    <td className="p-3.5 font-bold text-emerald-400">{log.action}</td>
                    <td className="p-3.5 text-slate-400">{log.resource}</td>
                    <td className="p-3.5 text-slate-300 max-w-md truncate">{log.details}</td>
                    <td className="p-3.5 text-center">
                      <span className="bg-emerald-950 text-emerald-400 border border-emerald-800 px-2 py-0.5 rounded text-[10px] font-bold">
                        {log.statusCode || 200} OK
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
