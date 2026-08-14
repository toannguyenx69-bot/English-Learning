import React, { createContext, ReactNode, useContext, useMemo, useState } from 'react';
import { login as loginRequest, type AuthResponse } from '../api/auth';

type AuthContextValue = {
  isAuthenticated: boolean;
  token: string | null;
  login: (email: string, password: string) => Promise<AuthResponse>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('accessToken'));

  const login = async (email: string, password: string) => {
    const response = await loginRequest({ email, password });
    localStorage.setItem('accessToken', response.accessToken);
    setToken(response.accessToken);
    return response;
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    setToken(null);
  };

  const value = useMemo<AuthContextValue>(
    () => ({
      isAuthenticated: Boolean(token),
      token,
      login,
      logout,
    }),
    [token]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }

  return context;
}
