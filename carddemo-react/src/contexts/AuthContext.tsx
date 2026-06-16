import { useState, useCallback, useEffect, type ReactNode } from 'react';
import type { User } from '@/types';
import { authApi } from '@/api';
import { AuthContext } from './authContextValue';

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState(() => {
    const token = localStorage.getItem('token');
    const userJson = localStorage.getItem('user');
    const user = userJson ? (JSON.parse(userJson) as User) : null;
    return {
      token,
      user,
      isAuthenticated: !!token,
      isAdmin: user?.userType === 'A',
    };
  });

  useEffect(() => {
    if (state.token) {
      localStorage.setItem('token', state.token);
    } else {
      localStorage.removeItem('token');
    }
    if (state.user) {
      localStorage.setItem('user', JSON.stringify(state.user));
    } else {
      localStorage.removeItem('user');
    }
  }, [state.token, state.user]);

  const login = useCallback(async (userId: string, password: string) => {
    const response = await authApi.login({ userId, password });
    setState({
      token: response.token,
      user: response.user,
      isAuthenticated: true,
      isAdmin: response.user.userType === 'A',
    });
  }, []);

  const logout = useCallback(() => {
    setState({ token: null, user: null, isAuthenticated: false, isAdmin: false });
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }, []);

  return (
    <AuthContext.Provider value={{ ...state, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
