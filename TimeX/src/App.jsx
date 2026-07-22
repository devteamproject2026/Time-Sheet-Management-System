
import { BrowserRouter, Route, Routes } from "react-router-dom";

// Layouts
import ProtectedLayout from "./components/Shared/ProtectedLayout";

// Public Pages
import HomeComp from "./components/HomeComp";
import LoginComp from "./components/LoginComp";
import LogoutComp from "./components/LogoutComponent";
import HrRegistration from "./components/HR-Head/HrRegistration";

// Protected Routes
import ProtectedRoute from "./components/ProtectedRoute";

// Admin Pages
import AdminDashboard from "./components/Admin/AdminDashboard";
import PendingHrRequests from "./components/Admin/PendingHrRequests";

// HR Pages
import HrDashboard from "./components/HR-Head/HrDashboard";
import CreateEmployee from "./components/HR-Head/CreateEmployee";
import CreateManager from "./components/HR-Head/CreateManager";

// Manager Pages
import ManagerDashboard from "./components/Manager/ManagerDashboard";

// Employee Pages
import EmployeeDashboard from "./components/Employee/EmployeeDashboard";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<HomeComp />} />
        <Route path="/login" element={<LoginComp />} />
        <Route path="/logout" element={<LogoutComp />} />
        <Route path="/hr-register" element={<HrRegistration />} />

        {/* Protected Routes with Layout (Navbar + Sidebar) */}
        <Route element={<ProtectedLayout />}>
          {/* Admin Routes */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute role="ADMIN">
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/pending-hr"
            element={
              <ProtectedRoute role="ADMIN">
                <PendingHrRequests />
              </ProtectedRoute>
            }
          />

          {/* HR Routes */}
          <Route
            path="/hr"
            element={
              <ProtectedRoute role="HR_HEAD">
                <HrDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/hr/create-manager"
            element={
              <ProtectedRoute role="HR_HEAD">
                <CreateManager />
              </ProtectedRoute>
            }
          />
          <Route
            path="/hr/create-employee"
            element={
              <ProtectedRoute role="HR_HEAD">
                <CreateEmployee />
              </ProtectedRoute>
            }
          />

          {/* Manager Routes */}
          <Route
            path="/manager"
            element={
              <ProtectedRoute role="MANAGER">
                <ManagerDashboard />
              </ProtectedRoute>
            }
          />

          {/* Employee Routes */}
          <Route
            path="/employee"
            element={
              <ProtectedRoute role="EMPLOYEE">
                <EmployeeDashboard />
              </ProtectedRoute>
            }
          />
        </Route>

        {/* Catch all - 404 */}
        <Route path="*" element={<div className="container mt-5"><h1>Page Not Found</h1></div>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
