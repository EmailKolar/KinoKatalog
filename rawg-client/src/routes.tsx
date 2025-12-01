import { createBrowserRouter } from "react-router-dom";
import Layout from "./pages/Layout";
import HomePage from "./pages/HomePage";
import GameDetailPage from "./pages/GameDetailPage";
import ErrorPage from "./pages/ErrorPage";
import MovieDetailPage from "./pages/MovieDetailPage";
import AdminPage from "./pages/AdminPage";
import UserDetailPage from "./pages/UserDetailPage";

const router = createBrowserRouter([{
    path: "/",
    element: <Layout />,
    errorElement: <ErrorPage />,
    children: [
        {
            path: "/",
            element: <HomePage />,
        },
        {
            path: "/games/:id",
            element: <GameDetailPage />,
        },
        {
            path: "/movies/:id",
            element: <MovieDetailPage />,
        },
        {
        path: "/admin",
        element: <AdminPage />,
      },
      { path: "/users/:id",
        element: <UserDetailPage />
    },
    ],
}]);

export default router;