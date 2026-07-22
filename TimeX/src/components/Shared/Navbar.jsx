import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { logout } from "../../redux/authslice";
import "./Navbar.css";

export default function Navbar() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const auth = useSelector((state) => state.auth);

  const handleLogout = () => {
    dispatch(logout());
    navigate("/", { replace: true });
  };

  const getRoleName = (role) => {
    const roleMap = {
      "ADMIN": "Admin",
      "HR_HEAD": "HR Head",
      "MANAGER": "Manager",
      "EMPLOYEE": "Employee"
    };
    return roleMap[role] || role;
  };

  const username = auth.user?.username || "User";
  const userInitial = username.charAt(0).toUpperCase();

  return (
    <nav className="app-navbar">
      <div className="navbar-brand-group">
        <span className="navbar-mark">WP</span>
        <div>
          <span className="navbar-title">WorkPulse</span>
          <span className="navbar-subtitle">Timesheet Management</span>
        </div>
      </div>

      <div className="navbar-actions">
        <div className="navbar-user">
          <span className="user-avatar">{userInitial}</span>
          <div className="user-info">
            <span className="user-name">{username}</span>
            <span className="user-role">{getRoleName(auth.user?.role)}</span>
          </div>
        </div>

        <span className={`role-pill role-${getRoleClass(auth.user?.role)}`}>
          {getRoleName(auth.user?.role)}
        </span>

        <button
          className="logout-button"
          onClick={handleLogout}
          title="Logout"
          type="button"
        >
          Logout
        </button>
      </div>
    </nav>
  );
}

function getRoleClass(role) {
  switch (role) {
    case "ADMIN":
      return "admin";
    case "HR_HEAD":
      return "hr";
    case "MANAGER":
      return "manager";
    case "EMPLOYEE":
      return "employee";
    default:
      return "default";
  }
}


//--------------------------------


