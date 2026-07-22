//import { useSelector } from "react-redux";
import { useSelector } from "react-redux";
import { NavLink } from "react-router-dom";
import "./Sidebar.css";

export default function Sidebar() {
  const auth = useSelector((state) => state.auth);
  const role = auth.user?.role;

  const getMenuItems = (userRole) => {
    const baseItems = [
      { label: "Dashboard", path: "", icon: "house" }
    ];

    switch (userRole) {
      case "ADMIN":
        return [
          ...baseItems,
          { label: "Companies", path: "/companies", icon: "building" },
          { label: "Projects", path: "/projects", icon: "diagram-3" },
          { label: "Users", path: "/users", icon: "people" },
          { label: "Pending HR", path: "/pending-hr", icon: "hourglass" },
          { label: "Analytics", path: "/analytics", icon: "bar-chart" },
        ];

      case "HR_HEAD":
        return [
          ...baseItems,
          { label: "Projects", path: "/projects", icon: "diagram-3" },
          { label: "Employees", path: "/employees", icon: "person-check" },
          { label: "Timesheets", path: "/timesheets", icon: "clock" },
          { label: "Create Manager", path: "/create-manager", icon: "person-plus" },
          { label: "Create Employee", path: "/create-employee", icon: "person-plus" },
        ];

      case "MANAGER":
        return [
          ...baseItems,
          { label: "Projects", path: "/projects", icon: "diagram-3" },
          { label: "Tasks", path: "/tasks", icon: "list-check" },
          { label: "Team", path: "/team", icon: "people" },
          { label: "Timesheets", path: "/timesheets", icon: "clock" },
        ];

      case "EMPLOYEE":
        return [
          ...baseItems,
          { label: "My Projects", path: "/projects", icon: "diagram-3" },
          { label: "My Tasks", path: "/tasks", icon: "list-check" },
          { label: "My Timesheets", path: "/timesheets", icon: "clock" },
        ];

      default:
        return baseItems;
    }
  };

  const menuItems = getMenuItems(role);
  const baseRoute = `/${role === "ADMIN" ? "admin" : role === "HR_HEAD" ? "hr" : role === "MANAGER" ? "manager" : "employee"}`;

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <h5 className="mb-0">Menu</h5>
      </div>

      <nav className="sidebar-nav">
        <ul className="nav flex-column">
          {menuItems.map((item, idx) => (
            <li className="nav-item" key={idx}>
              <NavLink
                to={baseRoute + item.path}
                className={({ isActive }) =>
                  `nav-link d-flex align-items-center gap-2 ${isActive ? "active" : ""}`
                }
              >
                <i className={`bi bi-${item.icon}`}></i>
                <span>{item.label}</span>
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>

      <div className="sidebar-footer">
        <small className="text-muted">WorkPulse v1.0</small>
      </div>
    </aside>
  );
}



// ---------------------------------



