import React, { createContext, useContext, useState } from "react";
import ApiClient, { axiosInstance, setAuthHeader } from "./api-client";
import { User } from "../domain/user/user";

type AuthContextValue = {
  user: User | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  refresh: () => Promise<void>;
  ensureUserLoaded: () => Promise<void>;
  register: (req: { username: string; email: string; password: string }) => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const usersClient = new ApiClient<User>("users");

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const tokenPresent = !!localStorage.getItem("kk_auth_token");
  const [user, setUser] = useState<User | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(tokenPresent);

  const refresh = async () => {
    const me = await usersClient.get("me");
    setUser(me);
  };

  const ensureUserLoaded = async () => {
    if (user) return;
    if (!isAuthenticated) throw new Error("not authenticated");
    await refresh();
  };

  const login = async (username: string, password: string) => {
    const res = await axiosInstance.post("auth/login", { username, password });
    const token: string = res.data?.token ?? res.data?.accessToken ?? res.data;
    if (!token) throw new Error("No token returned");
    setAuthHeader(token);
    setIsAuthenticated(true);
    try {
      await refresh();
    } catch (err) {
      setAuthHeader(null);
      setIsAuthenticated(false);
      throw err;
    }
  };

  const logout = () => {
    setUser(null);
    setIsAuthenticated(false);
    setAuthHeader(null);
  };
  const register = async (req: { username: string; email: string; password: string }) => {
    // Send new user registration request
    await axiosInstance.post("users/register", req);
  }; 

  const value: AuthContextValue = {
    user,
    isAuthenticated,
    isAdmin: user?.role === "ADMIN" || user?.role === "ROLE_ADMIN",
    login,
    logout,
    refresh,
    ensureUserLoaded,
    register,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};