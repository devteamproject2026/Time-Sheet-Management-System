import { NavLink, Outlet } from "react-router-dom";

export default function AdminDashboard() {

    return (
        <>
            <h1>Admin Dashboard</h1>

            <NavLink to="pending-hr">
                Pending HR Requests
            </NavLink>

            <br />

            <NavLink to="/logout">
                Logout
            </NavLink>

            <hr />

            <Outlet />
        </>
    );
}