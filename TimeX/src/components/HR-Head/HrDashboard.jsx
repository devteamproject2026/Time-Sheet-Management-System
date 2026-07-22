import { NavLink, Outlet } from "react-router-dom";

export default function HrDashboard() {
  return (
    <>
      <h1>HR Dashboard</h1>

      <ul>
        <li>
          <NavLink to="create-manager">
            Create Manager
          </NavLink>
        </li>

        <li>
          <NavLink to="create-employee">
            Create Employee
          </NavLink>
        </li>

        <li>
          <NavLink to="/logout">
            Logout
          </NavLink>
        </li>
      </ul>

      <hr />

      <Outlet />
    </>
  );
}