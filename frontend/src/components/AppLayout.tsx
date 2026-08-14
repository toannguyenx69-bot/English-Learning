import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const navLinkStyle = ({ isActive }: { isActive: boolean }) => ({
  padding: '8px 12px',
  textDecoration: 'none',
  color: isActive ? '#fff' : '#dfe8ff',
  background: isActive ? '#1e3a8a' : 'transparent',
  borderRadius: 6,
  fontWeight: 600,
});

export default function AppLayout() {
  const { logout } = useAuth();

  return (
    <div style={{ minHeight: '100vh', background: '#f3f6fb' }}>
      <nav style={{ background: '#0f172a', color: '#fff', padding: '12px 24px', display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap' }}>
        <NavLink to="/dashboard" style={navLinkStyle}>Dashboard</NavLink>
        <NavLink to="/profile" style={navLinkStyle}>Profile</NavLink>
        <NavLink to="/vocabulary" style={navLinkStyle}>Vocabulary</NavLink>
        <NavLink to="/grammar" style={navLinkStyle}>Grammar</NavLink>
        <button
          type="button"
          onClick={logout}
          style={{ marginLeft: 'auto', padding: '8px 12px', borderRadius: 6, border: 'none', cursor: 'pointer', fontWeight: 700 }}
        >
          Logout
        </button>
      </nav>

      <main style={{ padding: 24 }}>
        <Outlet />
      </main>
    </div>
  );
}
