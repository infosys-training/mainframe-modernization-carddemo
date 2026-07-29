import {
  createContext,
  useContext,
  useState,
  useCallback,
  type ReactNode,
} from "react";
import { login as apiLogin, logout as apiLogout } from "../services/api";

interface AuthUser {
  user_id: string;
  user_type: string;
}

interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  isAdmin: boolean;
  isAuthenticated: boolean;
  login: (userId: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const stored = localStorage.getItem("user");
    return stored ? JSON.parse(stored) : null;
  });
  const [token, setToken] = useState<string | null>(
    () => localStorage.getItem("token")
  );

  const login = useCallback(async (userId: string, password: string) => {
    const response = await apiLogin(userId, password);
    const { access_token, user_type, user_id } = response.data;
    const userData: AuthUser = { user_id, user_type };
    localStorage.setItem("token", access_token);
    localStorage.setItem("user", JSON.stringify(userData));
    setToken(access_token);
    setUser(userData);
  }, []);

  const logout = useCallback(() => {
    apiLogout().catch(() => {});
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAdmin: user?.user_type === "A",
        isAuthenticated: !!token,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
