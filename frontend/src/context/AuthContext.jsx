import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { loginApi, logoutApi, meApi } from '../api/authApi';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [username, setUsername] = useState('');
  const [role, setRole] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  const clearAuth = () => {
    setUsername('');
    setRole('');
  };

  const hydrate = async () => {
    try {
      const { data } = await meApi();
      setUsername(data.name);
      setRole(data.role);
    } catch {
      clearAuth();
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    hydrate();
  }, []);

  useEffect(() => {
    const onUnauthorized = () => clearAuth();
    window.addEventListener('auth:unauthorized', onUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', onUnauthorized);
  }, []);

  const login = async (payload) => {
    await loginApi(payload);
    await hydrate();
  };

  const logout = async () => {
    try {
      await logoutApi();
    } finally {
      clearAuth();
      window.location.assign('/login');
    }
  };

  const value = useMemo(
    () => ({
      username,
      role,
      isAuthenticated: Boolean(username && role),
      isAdmin: role === 'ADMIN',
      isLoading,
      login,
      logout
    }),
    [username, role, isLoading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
