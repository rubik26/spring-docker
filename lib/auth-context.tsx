"use client";

import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
  type ReactNode,
} from "react";
import type { User } from "./types";
import {
  getStoredCredentials,
  saveCredentials,
  clearCredentials,
  createUser,
  fetchUsers,
} from "./api";

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const resolveUser = useCallback(async () => {
    const creds = getStoredCredentials();
    if (!creds) {
      setIsLoading(false);
      return;
    }
    try {
      const users = await fetchUsers();
      const found = users.find((u) => u.username === creds.username);
      if (found) {
        setUser(found);
      } else {
        clearCredentials();
      }
    } catch {
      clearCredentials();
    }
    setIsLoading(false);
  }, []);

  useEffect(() => {
    resolveUser();
  }, [resolveUser]);

  const login = async (username: string, password: string) => {
    saveCredentials(username, password);
    try {
      const users = await fetchUsers();
      const found = users.find((u) => u.username === username);
      if (!found) {
        clearCredentials();
        throw new Error("Invalid credentials");
      }
      setUser(found);
    } catch (err) {
      clearCredentials();
      throw err;
    }
  };

  const register = async (username: string, password: string) => {
    const newUser = await createUser({ username, password });
    saveCredentials(username, password);
    setUser(newUser);
  };

  const logout = () => {
    clearCredentials();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
