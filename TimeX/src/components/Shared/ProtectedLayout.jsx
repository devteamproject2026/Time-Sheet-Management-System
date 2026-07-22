import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";
import "./ProtectedLayout.css";

export default function ProtectedLayout() {
  return (
    <div className="protected-layout">
      {/* Navbar at top */}
      <Navbar />

      <div className="layout-container">
        {/* Sidebar on left */}
        <Sidebar />

        {/* Main content in center */}
        <main className="main-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}


//--------------------------------

