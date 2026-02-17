"use client";

import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
  type ReactNode,
} from "react";
import {
  saveCredentials,
  clearCredentials,
  getStoredUsername,
  isAuthenticated as checkAuth,
  userApi,
} from "@/lib/api";

interface AuthContextType {
  username: string | null;
  isLoggedIn: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [username, setUsername] = useState<string | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const stored = getStoredUsername();
    if (stored && checkAuth()) {
      setUsername(stored);
      setIsLoggedIn(true);
    }
  }, []);

  const login = useCallback(async (user: string, pass: string) => {
    saveCredentials(user, pass);
    try {
      // Verify credentials by calling a protected endpoint
      await userApi.getUsers();
      setUsername(user);
      setIsLoggedIn(true);
    } catch {
      clearCredentials();
      throw new Error("Invalid credentials");
    }
  }, []);

  const register = useCallback(async (user: string, pass: string) => {
    await userApi.createUser({ username: user, password: pass });
    saveCredentials(user, pass);
    setUsername(user);
    setIsLoggedIn(true);
  }, []);

  const logout = useCallback(() => {
    clearCredentials();
    setUsername(null);
    setIsLoggedIn(false);
  }, []);

  return (
    <AuthContext.Provider value={{ username, isLoggedIn, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
