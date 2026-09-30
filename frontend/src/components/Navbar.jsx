import React from 'react';
import { Shield, LayoutDashboard, ShoppingCart, ArrowLeftRight, Users, FileText, UserCheck, LogOut } from 'lucide-react';

export default function Navbar({ activeTab, setActiveTab, user, onLogout }) {
  if (!user) return null;

  const role = user.role;
  const isLogistics = role === 'LOGISTICS_OFFICER';
  const isAdmin = role === 'ADMIN';
  const isCommander = role === 'BASE_COMMANDER';

  return (
    <header className="bg-slate-900 border-b border-slate-800 sticky top-0 z-40 shadow-xl">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo & Title */}
          <div className="flex items-center space-x-3">
            <div className="bg-emerald-600/20 p-2 rounded-lg border border-emerald-500/30 text-emerald-400">
              <Shield className="h-6 w-6" />
            </div>
            <div>
              <span className="font-bold text-lg text-slate-100 tracking-wide uppercase flex items-center gap-2">
                MAMS <span className="text-xs bg-emerald-950 text-emerald-400 px-2 py-0.5 rounded border border-emerald-800/60 font-mono">v1.0 MIL-SPEC</span>
              </span>
              <p className="text-xs text-slate-400 font-medium">Military Asset Management System</p>
            </div>
          </div>

          {/* Navigation Links based on Authenticated Role */}
          <nav className="hidden md:flex space-x-1">
            <button
              onClick={() => setActiveTab('dashboard')}
              className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-all ${
                activeTab === 'dashboard'
                  ? 'bg-slate-800 text-emerald-400 shadow-sm border border-slate-700'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
              }`}
            >
              <LayoutDashboard className="h-4 w-4" />
              <span>Dashboard</span>
            </button>

            <button
              onClick={() => setActiveTab('purchases')}
              className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-all ${
                activeTab === 'purchases'
                  ? 'bg-slate-800 text-emerald-400 shadow-sm border border-slate-700'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
              }`}
            >
              <ShoppingCart className="h-4 w-4" />
              <span>Purchases</span>
            </button>

            <button
              onClick={() => setActiveTab('transfers')}
              className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-all ${
                activeTab === 'transfers'
                  ? 'bg-slate-800 text-emerald-400 shadow-sm border border-slate-700'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
              }`}
            >
              <ArrowLeftRight className="h-4 w-4" />
              <span>Transfers</span>
            </button>

            {!isLogistics && (
              <button
                onClick={() => setActiveTab('assignments')}
                className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-all ${
                  activeTab === 'assignments'
                    ? 'bg-slate-800 text-emerald-400 shadow-sm border border-slate-700'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
                }`}
              >
                <Users className="h-4 w-4" />
                <span>Assignments & Exp.</span>
              </button>
            )}

            {isAdmin && (
              <button
                onClick={() => setActiveTab('audit-logs')}
                className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-all ${
                  activeTab === 'audit-logs'
                    ? 'bg-slate-800 text-emerald-400 shadow-sm border border-slate-700'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
                }`}
              >
                <FileText className="h-4 w-4" />
                <span>Audit Logs</span>
              </button>
            )}
          </nav>

          {/* User Profile & Logout */}
          <div className="flex items-center space-x-3">
            <div className="flex items-center space-x-2 bg-slate-950 border border-slate-800 rounded-lg px-3 py-1.5">
              <UserCheck className="h-4 w-4 text-emerald-400" />
              <div className="text-left">
                <div className="text-xs font-bold text-slate-100 flex items-center gap-1.5">
                  {user.fullName || user.username}
                  <span className="text-[9px] bg-slate-800 text-emerald-400 px-1.5 py-0.5 rounded font-mono border border-slate-700">
                    {role}
                  </span>
                </div>
                <div className="text-[10px] text-slate-400 font-mono">
                  {isAdmin ? 'Central Command Scope' : `Base ID Scope: ${user.assignedBaseId}`}
                </div>
              </div>
            </div>

            <button
              onClick={onLogout}
              title="Sign Out"
              className="p-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg transition-colors border border-slate-700"
            >
              <LogOut className="h-4 w-4 text-rose-400" />
            </button>
          </div>
        </div>
      </div>
    </header>
  );
}
