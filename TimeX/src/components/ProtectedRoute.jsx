import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ children, role }) {
   const loginstate = useSelector((state) => state.auth);

   // App.jsx completes the /api/auth/me cookie check before routes render.
   // This extra guard keeps this component safe if it is reused elsewhere.
   if (loginstate.isInitializing) {
      return <div className="container mt-5 text-center">Checking session...</div>;
   }

   // Redirect only after confirming that no valid session exists.
   if (!loginstate.isAuthenticated) {
      return <Navigate to="/login" />;
   }
   //Role
   if (loginstate.user.role !== role) {
      return <Navigate to="/unauthorized" />;
   }

   return children;
}
