import { BrowserRouter, Route, Routes } from "react-router-dom";

import HomeComp from "./components/HomeComp";
import LoginComp from "./components/LoginComp";
import LogoutComp from "./components/LogoutComponent";

import ProtectedRoute from "./components/ProtectedRoute";

import AdminDashboard from "./components/Admin/AdminDashboard";
import HrDashboard from "./components/HR-Head/HrDashboard";
import ManagerDashboard from "./components/Manager/ManagerDashboard";
import EmployeeDashboard from "./components/Employee/EmployeeDashboard";
import HrRegistration from "./components/HR-Head/HrRegistration";
import PendingHrRequests from "./components/Admin/PendingHrRequests";
import CreateEmployee from "./components/HR-Head/CreateEmployee";
import CreateManager from "./components/HR-Head/CreateManager";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}

        <Route path="/" element={<HomeComp />} />

        <Route path="/login" element={<LoginComp />} />

        <Route path="/logout" element={<LogoutComp />} />

        <Route path="/hr-register" element={<HrRegistration />} />

        {/* Admin */}

        <Route
          path="/admin"
          element={
            <ProtectedRoute role="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          }
        >
          <Route path="pending-hr" element={<PendingHrRequests />} />
        </Route>

        {/* HR */}

        <Route
          path="/hr"
          element={
            <ProtectedRoute role="HR_HEAD">
              <HrDashboard />
            </ProtectedRoute>
          }
        >
          <Route path="create-manager" element={<CreateManager />} />

          <Route path="create-employee" element={<CreateEmployee />} />
        </Route>

        {/* Manager */}

        <Route
          path="/manager"
          element={
            <ProtectedRoute role="MANAGER">
              <ManagerDashboard />
            </ProtectedRoute>
          }
        />

        {/* Employee */}

        <Route
          path="/employee"
          element={
            <ProtectedRoute role="EMPLOYEE">
              <EmployeeDashboard />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
