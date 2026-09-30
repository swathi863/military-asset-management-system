import React, { useState } from 'react';
import { api } from '../api';
import { Shield, Lock, User, Key, AlertCircle, CheckCircle2 } from 'lucide-react';

export default function LoginModal({ onLoginSuccess }) {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('password123');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    if (e) e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const user = await api.login(username, password);
      setLoading(false);
      onLoginSuccess(user);
    } catch (err) {
      setLoading(false);
      setError(err.message || 'Authentication failed. Please check credentials.');
    }
  };

  const quickSelectUser = (userAcc, userPass) => {
    setUsername(userAcc);
    setPassword(userPass);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/90 backdrop-blur-md">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-5">
        <div className="text-center space-y-2">
          <div className="inline-flex p-3 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 rounded-xl mb-1">
            <Shield className="h-8 w-8" />
          </div>
          <h2 className="text-xl font-extrabold text-slate-100 uppercase tracking-wide">
            Military Asset Management System
          </h2>
          <p className="text-xs text-slate-400">Authenticate with Spring Boot JWT Security</p>
        </div>

        {error && (
          <div className="bg-rose-950/80 border border-rose-800 text-rose-300 p-3 rounded-lg text-xs flex items-center gap-2">
            <AlertCircle className="h-4 w-4 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-300 uppercase mb-1">Username</label>
            <div className="relative">
              <User className="h-4 w-4 absolute left-3 top-2.5 text-slate-500" />
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                className="pl-9 pr-3 py-2 w-full bg-slate-950 border border-slate-800 rounded-lg text-xs text-slate-100 focus:outline-none focus:border-emerald-500 font-mono"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-300 uppercase mb-1">Password</label>
            <div className="relative">
              <Key className="h-4 w-4 absolute left-3 top-2.5 text-slate-500" />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="pl-9 pr-3 py-2 w-full bg-slate-950 border border-slate-800 rounded-lg text-xs text-slate-100 focus:outline-none focus:border-emerald-500 font-mono"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs uppercase tracking-wider rounded-lg shadow-lg transition-all flex items-center justify-center space-x-2"
          >
            {loading ? (
              <span>Authenticating...</span>
            ) : (
              <>
                <Lock className="h-4 w-4" />
                <span>Secure Sign In</span>
              </>
            )}
          </button>
        </form>

        {/* Quick Credentials Selection for Demo / Testing */}
        <div className="border-t border-slate-800 pt-4 space-y-2">
          <span className="block text-[10px] font-bold text-slate-400 uppercase text-center tracking-wider">
            Quick Select Authenticated Identity
          </span>
          <div className="grid grid-cols-2 gap-2 text-[10px]">
            <button
              onClick={() => quickSelectUser('admin', 'password123')}
              className={`p-2 rounded border text-left transition-colors ${
                username === 'admin' ? 'bg-emerald-950 border-emerald-500 text-emerald-300 font-bold' : 'bg-slate-950 border-slate-800 text-slate-300 hover:bg-slate-800'
              }`}
            >
              <div>Admin (Central)</div>
              <div className="text-[9px] text-slate-500">`admin`</div>
            </button>

            <button
              onClick={() => quickSelectUser('commander_bragg', 'password123')}
              className={`p-2 rounded border text-left transition-colors ${
                username === 'commander_bragg' ? 'bg-emerald-950 border-emerald-500 text-emerald-300 font-bold' : 'bg-slate-950 border-slate-800 text-slate-300 hover:bg-slate-800'
              }`}
            >
              <div>Commander (Bragg)</div>
              <div className="text-[9px] text-slate-500">`commander_bragg`</div>
            </button>

            <button
              onClick={() => quickSelectUser('commander_pendleton', 'password123')}
              className={`p-2 rounded border text-left transition-colors ${
                username === 'commander_pendleton' ? 'bg-emerald-950 border-emerald-500 text-emerald-300 font-bold' : 'bg-slate-950 border-slate-800 text-slate-300 hover:bg-slate-800'
              }`}
            >
              <div>Commander (Pendleton)</div>
              <div className="text-[9px] text-slate-500">`commander_pendleton`</div>
            </button>

            <button
              onClick={() => quickSelectUser('logistics_officer', 'password123')}
              className={`p-2 rounded border text-left transition-colors ${
                username === 'logistics_officer' ? 'bg-emerald-950 border-emerald-500 text-emerald-300 font-bold' : 'bg-slate-950 border-slate-800 text-slate-300 hover:bg-slate-800'
              }`}
            >
              <div>Logistics Officer</div>
              <div className="text-[9px] text-slate-500">`logistics_officer`</div>
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}
