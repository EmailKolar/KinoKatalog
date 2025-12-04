import React, { createContext, useContext, useState } from "react";
import ApiClient, { axiosInstance } from "./api-client";
import { User } from "../domain/user/user";

type AuthContextValue = {
  user: User | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  refresh: () => Promise<void>;
  ensureUserLoaded: () => Promise<void>;
  register: (req: { username: string; email: string; password: string }) => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const usersClient = new ApiClient<User>("users");

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

  const refresh = async () => {
    try {
      const me = await usersClient.get("me");
      setUser(me);
      setIsAuthenticated(true);
    } catch (error) {
      // If refresh fails, user is not authenticated
      setUser(null);
      setIsAuthenticated(false);
      throw error;
    }
  };

  const ensureUserLoaded = async () => {
    if (user) return;
    await refresh(); // This will throw if not authenticated
  };

  const login = async (username: string, password: string) => {
    // No need to handle token - cookie is set automatically by backend
    await axiosInstance.post("auth/login", { username, password });
    
    // Fetch user data after successful login
    await refresh();
  };

  const logout = async () => {
    try {
      // Call backend logout to clear cookie
      await axiosInstance.post("auth/logout");
    } catch (error) {
      // Even if logout fails, clear local state
      console.error("Logout error:", error);
    } finally {
      setUser(null);
      setIsAuthenticated(false);
    }
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