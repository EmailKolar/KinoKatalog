import { createBrowserRouter } from "react-router-dom";
import Layout from "./pages/Layout";
import HomePage from "./pages/HomePage";
import GameDetailPage from "./pages/GameDetailPage";
import ErrorPage from "./pages/ErrorPage";
import MovieDetailPage from "./pages/MovieDetailPage";
import AdminPage from "./pages/AdminPage";
import UserDetailPage from "./pages/UserDetailPage";
import { RequireAdmin, RequireOwnerOnly } from "./components/RouteGuards";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    errorElement: <ErrorPage />,
    children: [
      { path: "/", element: <HomePage /> },
      { path: "/games/:id", element: <GameDetailPage /> },
      { path: "/movies/:id", element: <MovieDetailPage /> },
      {
        path: "/admin",
        element: (
          <RequireAdmin>
            <AdminPage />
          </RequireAdmin>
        ),
      },
      {
        path: "/users/:id",
        element: (
          <RequireOwnerOnly>
            <UserDetailPage />
          </RequireOwnerOnly>
        ),
      },
    ],
  },
]);

export default router;