
   import { useSelector } from "react-redux";
   import { Navigate } from "react-router-dom";

   export default function ProtectedRoute({ children, role }) {
      const loginstate  = useSelector((state) => state.auth);


      //Any User Loged in or not 
      if (!loginstate.isAuthenticated) {
       return <Navigate to="/login" />;
      }
      //Role
      if (loginstate.user.role !== role) {
       return <Navigate to="/unauthorized" />;
      }

     return children;
  }