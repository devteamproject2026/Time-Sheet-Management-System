import { BrowserRouter, Route, Routes } from "react-router-dom";
import HomeComp from "./components/HomeComp";
import LoginComp from "./components/LoginComp";
import UserDashboard from "./components/UserDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import AdminDashboard from "./components/AminDashboard";
import LogoutComp from "./components/LogoutComponent";

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<HomeComp />}>
            <Route path="login" element={<LoginComp />} />
            <Route path="logout" element={<LogoutComp/>} />
          </Route>

          <Route
            path="/user"
            element={
              <ProtectedRoute role={2}>
                <UserDashboard />
              </ProtectedRoute>
            }
          />

          <Route
            path="/admin"
            element={
              <ProtectedRoute role={1}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />


        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;