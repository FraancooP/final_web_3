import { createBrowserRouter } from "react-router";
import { Login } from "@/app/pages/Login";
import { Dashboard } from "@/app/pages/Dashboard";
import { ProtectedRoute } from "@/app/components/ProtectedRoute";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Login />,
  },
  {
    path: "/dashboard",
    element: (
      <ProtectedRoute>
        <Dashboard />
      </ProtectedRoute>
    ),
  },
]);
