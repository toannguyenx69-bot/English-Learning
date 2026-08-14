import React, { useEffect, useState } from 'react';
import { getCurrentUser, type UserProfile } from '../api/user';

export default function ProfilePage() {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUser = async () => {
      setLoading(true);
      setError(null);

      try {
        const data = await getCurrentUser();
        setUser(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Unable to load profile.');
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  if (loading) {
    return <p>Loading profile...</p>;
  }

  if (error) {
    return <p style={{ color: 'crimson' }}>Error: {error}</p>;
  }

  if (!user) {
    return <p>No user data available.</p>;
  }

  return (
    <div style={{ maxWidth: 600, margin: '0 auto', background: '#fff', border: '1px solid #dfe3ec', borderRadius: 12, padding: 24 }}>
      <h1>Profile</h1>
      <p><strong>Username:</strong> {user.username}</p>
      <p><strong>Email:</strong> {user.email}</p>
      {user.createdAt && <p><strong>Created:</strong> {new Date(user.createdAt).toLocaleString()}</p>}
      {user.updatedAt && <p><strong>Updated:</strong> {new Date(user.updatedAt).toLocaleString()}</p>}
    </div>
  );
}
