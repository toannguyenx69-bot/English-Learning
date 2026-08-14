import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await login(email, password);
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'grid', placeItems: 'center', background: '#f3f6fb' }}>
      <div style={{ width: '100%', maxWidth: 420, background: '#fff', border: '1px solid #dfe3ec', borderRadius: 12, padding: 24 }}>
        <h1 style={{ marginTop: 0 }}>Login</h1>

        <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 16 }}>
          <label style={{ display: 'grid', gap: 8 }}>
            <span>Email</span>
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="you@example.com"
              required
              style={{ padding: '10px 12px', borderRadius: 8, border: '1px solid #cfd8e3' }}
            />
          </label>

          <label style={{ display: 'grid', gap: 8 }}>
            <span>Password</span>
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Password"
              required
              style={{ padding: '10px 12px', borderRadius: 8, border: '1px solid #cfd8e3' }}
            />
          </label>

          {error && <p style={{ margin: 0, color: 'crimson' }}>{error}</p>}

          <button
            type="submit"
            disabled={loading}
            style={{ padding: '12px 16px', border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: loading ? 'not-allowed' : 'pointer', fontWeight: 700 }}
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <p style={{ marginTop: 16, textAlign: 'center' }}>
          Need an account?{' '}
          <Link to="/register" style={{ color: '#2563eb', textDecoration: 'none' }}>
            Register
          </Link>
        </p>
      </div>
    </div>
  );
}
