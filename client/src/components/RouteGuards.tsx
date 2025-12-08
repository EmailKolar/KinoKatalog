import React from "react";
import { useAuth } from "../services/auth";
import { Navigate } from "react-router-dom";

export const RequireAdmin: React.FC<{ children: React.ReactElement }> = ({ children }) => {
  const auth = useAuth();
  if (!auth.isAuthenticated) return <Navigate to="/" replace />;
  if (!auth.isAdmin) return <Navigate to="/" replace />;
  return children;
};

// allow route when token is present; actual ownership check happens in the page
export const RequireOwnerOnly: React.FC<{ children: React.ReactElement }> = ({ children }) => {
  const auth = useAuth();
  if (!auth.isAuthenticated) return <Navigate to="/" replace />;
  return children;
};