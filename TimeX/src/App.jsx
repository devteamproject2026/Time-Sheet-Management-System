import { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { finishSessionCheck, restoreSession } from "./redux/authslice";
import { AUTH_API_URL } from "./config/api";

// Layouts
import ProtectedLayout from "./components/Shared/ProtectedLayout";
import ModulePlaceholder from "./components/Shared/ModulePlaceholder";

// Public Pages
import HomeComp from "./components/HomeComp";
import LoginComp from "./components/LoginComp";
import LogoutComp from "./components/LogoutComponent";
import HrRegistration from "./components/HR-Head/HrRegistration";

// Protected Routes
import ProtectedRoute from "./components/ProtectedRoute";

// Admin Pages
import PendingHrRequests from "./components/Admin/PendingHrRequests";

// HR Pages
import CreateEmployee from "./components/HR-Head/CreateEmployee";
import CreateManager from "./components/HR-Head/CreateManager";

import ChangePassword from "./components/Shared/ChangePassword";

// Business Service Pages
import ClientsPage from "./components/Business/ClientsPage";
import ProjectsPage from "./components/Business/ProjectsPage";
import AssignmentsPage from "./components/Business/AssignmentsPage";
import ManagerTeamPage from "./components/Business/ManagerTeamPage";
import BusinessDashboard from "./components/Business/BusinessDashboard";

// Transaction Service Pages
import TasksPage from "./components/Transaction/TasksPage";
import TimesheetsPage from "./components/Transaction/TimesheetsPage";
import AttendancePage from "./components/Transaction/AttendancePage";
import ComplaintsPage from "./components/Transaction/ComplaintsPage";
import ReportsPage from "./components/Transaction/ReportsPage";
import EmployeeAssistantPage from "./components/AI/EmployeeAssistantPage";

// import ProtectedRoute from "./components/ProtectedRoute";

// import AdminDashboard from "./components/Admin/AdminDashboard";
// import HrDashboard from "./components/HR-Head/HrDashboard";
// import ManagerDashboard from "./components/Manager/ManagerDashboard";
// import EmployeeDashboard from "./components/Employee/EmployeeDashboard";
// import HrRegistration from "./components/HR-Head/HrRegistration";
// import PendingHrRequests from "./components/Admin/PendingHrRequests";
// import CreateEmployee from "./components/HR-Head/CreateEmployee";
// import CreateManager from "./components/HR-Head/CreateManager";

function App() {
  const dispatch = useDispatch();
  const isInitializing = useSelector((state) => state.auth.isInitializing);
  const sessionCheckStarted = useRef(false);

  useEffect(() => {
    // React StrictMode runs effects twice in development. This guard prevents
    // two identical /me requests during the initial session check.
    if (sessionCheckStarted.current) return;
    sessionCheckStarted.current = true;

    fetch(`${AUTH_API_URL}/me`, {
      method: "GET",
      // The JWT is stored in an HttpOnly cookie, so the browser must include it.
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) return null;
        return response.json();
      })
      .then((data) => {
        if (!data) {
          dispatch(finishSessionCheck());
          return;
        }

        dispatch(
          restoreSession({
            user: {
              userId: data.userId,
              username: data.username,
              role: data.role,
            },
          })
        );
      })
      .catch(() => {
        // A missing, expired, or unreachable session should behave as logged out.
        dispatch(finishSessionCheck());
      });
  }, [dispatch]);

  // Avoid redirecting to /login before the cookie check has completed.
  if (isInitializing) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border text-primary" role="status"></div>
        <p className="mt-3">Checking your session...</p>
      </div>
    );
  }

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
                <BusinessDashboard />
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
          <Route
            path="/admin/companies"
            element={
              <ProtectedRoute role="ADMIN">
                <ClientsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/projects"
            element={
              <ProtectedRoute role="ADMIN">
                <ProjectsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/users"
            element={
              <ProtectedRoute role="ADMIN">
                <ModulePlaceholder
                  title="HR Heads"
                  description="The Admin HR Head directory will be implemented here after its safe Auth API is available."
                />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/analytics"
            element={
              <ProtectedRoute role="ADMIN">
                <ModulePlaceholder
                  title="Analytics"
                  description="Administrative reporting and analytics will be implemented in a later module."
                />
              </ProtectedRoute>
            }
          />

          {/* HR Routes */}
          <Route
            path="/hr"
            element={
              <ProtectedRoute role="HR_HEAD">
                <BusinessDashboard />
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
          <Route
            path="/hr/clients"
            element={
              <ProtectedRoute role="HR_HEAD">
                <ClientsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/hr/projects"
            element={
              <ProtectedRoute role="HR_HEAD">
                <ProjectsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/hr/employees"
            element={
              <ProtectedRoute role="HR_HEAD">
                <AssignmentsPage />
              </ProtectedRoute>
            }
          />
          {/* Manager Routes */}
          <Route
            path="/manager"
            element={
              <ProtectedRoute role="MANAGER">
                <BusinessDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/manager/projects"
            element={
              <ProtectedRoute role="MANAGER">
                <ProjectsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/manager/tasks"
            element={
              <ProtectedRoute role="MANAGER">
                <TasksPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/manager/team"
            element={
              <ProtectedRoute role="MANAGER">
                <ManagerTeamPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/manager/timesheets"
            element={
              <ProtectedRoute role="MANAGER">
                <TimesheetsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/manager/attendance"
            element={
              <ProtectedRoute role="MANAGER">
                <AttendancePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/manager/complaints"
            element={
              <ProtectedRoute role="MANAGER">
                <ComplaintsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/manager/reports"
            element={
              <ProtectedRoute role="MANAGER">
                <ReportsPage />
              </ProtectedRoute>
            }
          />

          {/* Employee Routes */}
          <Route
            path="/employee"
            element={
              <ProtectedRoute role="EMPLOYEE">
                <BusinessDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/employee/projects"
            element={
              <ProtectedRoute role="EMPLOYEE">
                <ProjectsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/employee/tasks"
            element={
              <ProtectedRoute role="EMPLOYEE">
                <TasksPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/employee/timesheets"
            element={
              <ProtectedRoute role="EMPLOYEE">
                <TimesheetsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/employee/attendance"
            element={
              <ProtectedRoute role="EMPLOYEE">
                <AttendancePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/employee/complaints"
            element={
              <ProtectedRoute role="EMPLOYEE">
                <ComplaintsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/employee/assistant"
            element={
              <ProtectedRoute role="EMPLOYEE">
                <EmployeeAssistantPage />
              </ProtectedRoute>
            }
          />

                <Route
        path="/change-password"
        element={
            <ProtectedRoute>
                <ChangePassword />
            </ProtectedRoute>
        }
    />



        </Route>

        

        <Route
          path="/unauthorized"
          element={
            <div className="container mt-5 text-center">
              <h1>Access Denied</h1>
              <p>You are logged in, but your role cannot open this page.</p>
            </div>
          }
        />

        {/* Catch all - 404 */}
        <Route path="*" element={<div className="container mt-5"><h1>Page Not Found</h1></div>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
