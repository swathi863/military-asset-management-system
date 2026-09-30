import React from 'react';
import { Filter, Calendar, Building2, Package, RefreshCw, Lock } from 'lucide-react';

export default function FilterBar({ filters, setFilters, bases, equipmentTypes, user }) {
  const isCommander = user?.role === 'BASE_COMMANDER';
  const assignedBaseId = user?.assignedBaseId;

  const handleChange = (field, value) => {
    // Prevent Base Commander from changing baseId
    if (field === 'baseId' && isCommander) {
      return;
    }
    setFilters(prev => ({ ...prev, [field]: value }));
  };

  const resetFilters = () => {
    setFilters({
      startDate: '',
      endDate: '',
      baseId: isCommander ? String(assignedBaseId) : '',
      equipmentTypeId: ''
    });
  };

  return (
    <div className="bg-slate-900/80 backdrop-blur border border-slate-800 rounded-xl p-4 mb-6 shadow-md">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        {/* Header */}
        <div className="flex items-center space-x-2 text-slate-300 text-sm font-semibold uppercase tracking-wider">
          <Filter className="h-4 w-4 text-emerald-400" />
          <span>Telemetry Filters</span>
        </div>

        {/* Filter Controls */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 w-full md:w-auto">
          {/* Base Filter */}
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-500">
              {isCommander ? <Lock className="h-4 w-4 text-amber-400" /> : <Building2 className="h-4 w-4" />}
            </div>
            <select
              value={filters.baseId || ''}
              disabled={isCommander}
              onChange={(e) => handleChange('baseId', e.target.value)}
              className={`pl-9 pr-3 py-2 w-full bg-slate-950 border border-slate-800 rounded-lg text-xs text-slate-200 focus:outline-none transition-colors ${
                isCommander ? 'opacity-80 cursor-not-allowed border-amber-800/60' : 'focus:border-emerald-500'
              }`}
            >
              {!isCommander && <option value="">All Bases</option>}
              {bases.map(b => (
                <option key={b.id} value={b.id}>
                  {b.name} {isCommander && String(b.id) === String(assignedBaseId) ? '(Assigned)' : ''}
                </option>
              ))}
            </select>
          </div>

          {/* Equipment Type Filter */}
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-500">
              <Package className="h-4 w-4" />
            </div>
            <select
              value={filters.equipmentTypeId || ''}
              onChange={(e) => handleChange('equipmentTypeId', e.target.value)}
              className="pl-9 pr-3 py-2 w-full bg-slate-950 border border-slate-800 rounded-lg text-xs text-slate-200 focus:outline-none focus:border-emerald-500 transition-colors"
            >
              <option value="">All Equipment Types</option>
              {equipmentTypes.map(e => (
                <option key={e.id} value={e.id}>{e.name} ({e.category})</option>
              ))}
            </select>
          </div>

          {/* Start Date */}
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-500">
              <Calendar className="h-4 w-4" />
            </div>
            <input
              type="date"
              value={filters.startDate || ''}
              onChange={(e) => handleChange('startDate', e.target.value)}
              placeholder="Start Date"
              className="pl-9 pr-3 py-2 w-full bg-slate-950 border border-slate-800 rounded-lg text-xs text-slate-200 focus:outline-none focus:border-emerald-500 transition-colors"
            />
          </div>

          {/* End Date */}
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-500">
              <Calendar className="h-4 w-4" />
            </div>
            <input
              type="date"
              value={filters.endDate || ''}
              onChange={(e) => handleChange('endDate', e.target.value)}
              placeholder="End Date"
              className="pl-9 pr-3 py-2 w-full bg-slate-950 border border-slate-800 rounded-lg text-xs text-slate-200 focus:outline-none focus:border-emerald-500 transition-colors"
            />
          </div>
        </div>

        {/* Reset Button */}
        <button
          onClick={resetFilters}
          className="flex items-center space-x-1.5 px-3 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs rounded-lg transition-colors border border-slate-700 whitespace-nowrap self-end md:self-auto"
        >
          <RefreshCw className="h-3.5 w-3.5" />
          <span>Reset</span>
        </button>
      </div>
    </div>
  );
}
