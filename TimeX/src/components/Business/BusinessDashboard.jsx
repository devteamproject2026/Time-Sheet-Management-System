import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { NavLink } from "react-router-dom";
import { businessApi } from "../../services/apiClient";
import { BusinessAlert, BusinessLoading } from "./BusinessStates";
import "../HR-Head/HrDashboard.css";

const dashboardConfiguration = {
  ADMIN: {
    kicker: "Administration",
    title: "Admin Dashboard",
    description: "Supervise Clients and Projects through read-only Business Service views.",
    requests: ["/clients", "/projects"],
    actions: [
      ["View Clients", "/admin/companies"],
      ["View Projects", "/admin/projects"],
      ["Pending HR Requests", "/admin/pending-hr"],
    ],
  },
  HR_HEAD: {
    kicker: "HR Workspace",
    title: "HR Dashboard",
    description: "Manage Clients, Projects and Project staffing from one organized view.",
    requests: ["/clients", "/projects", "/employee-projects"],
    actions: [
      ["Manage Clients", "/hr/clients"],
      ["Manage Projects", "/hr/projects"],
      ["Manage Assignments", "/hr/employees"],
      ["Create Employee", "/hr/create-employee"],
    ],
  },
  MANAGER: {
    kicker: "Manager Workspace",
    title: "Manager Dashboard",
    description: "Manage Projects, Tasks, Timesheet approvals and daily team activity.",
    requests: ["/projects/my-managed-projects"],
    actions: [
      ["My Projects", "/manager/projects"],
      ["My Project Teams", "/manager/team"],
      ["Manage Tasks", "/manager/tasks"],
      ["Review Timesheets", "/manager/timesheets"],
      ["Team Attendance", "/manager/attendance"],
      ["Employee Reports", "/manager/reports"],
    ],
  },
  EMPLOYEE: {
    kicker: "Employee Workspace",
    title: "Employee Dashboard",
    description: "Complete assigned Tasks, submit Timesheets and record daily activity.",
    requests: ["/projects/my-assigned-projects"],
    actions: [
      ["My Assigned Projects", "/employee/projects"],
      ["My Tasks", "/employee/tasks"],
      ["Submit Timesheet", "/employee/timesheets"],
      ["My Attendance", "/employee/attendance"],
      ["Raise Complaint", "/employee/complaints"],
    ],
  },
};

export default function BusinessDashboard() {
  const user = useSelector((state) => state.auth.user);
  const role = user?.role;
  const configuration = dashboardConfiguration[role];

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  useEffect(() => {
    let cancelled = false;

    const load = async () => {
      try {
        const responses = await Promise.all(
          (dashboardConfiguration[role]?.requests || []).map((path) =>
            businessApi(path)
          )
        );
        if (!cancelled) setData(responses);
      } catch (error) {
        if (!cancelled) {
          setFeedback({ type: "error", message: error.message });
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    load();
    return () => {
      cancelled = true;
    };
  }, [role]);

  const clients = Array.isArray(data[0]) ? data[0] : [];
  const projects = Array.isArray(data[role === "ADMIN" || role === "HR_HEAD" ? 1 : 0])
    ? data[role === "ADMIN" || role === "HR_HEAD" ? 1 : 0]
    : [];
  const assignments = role === "HR_HEAD" && Array.isArray(data[2]) ? data[2] : [];

  const stats = role === "ADMIN"
    ? [
        ["Clients", clients.length, "Available for supervision"],
        ["Projects", projects.length, "Across all Managers"],
        ["Active Projects", projects.filter((item) => item.status === "ACTIVE").length, "Currently active"],
        ["Completed", projects.filter((item) => item.status === "COMPLETED").length, "Finished Projects"],
      ]
    : role === "HR_HEAD"
      ? [
          ["Clients", clients.length, "Business clients"],
          ["Projects", projects.length, "All Projects"],
          ["Active Projects", projects.filter((item) => item.status === "ACTIVE").length, "Currently active"],
          ["Assignments", assignments.length, "Employee-Project links"],
        ]
      : [
          ["My Projects", projects.length, role === "MANAGER" ? "Projects you manage" : "Projects assigned to you"],
          ["Active", projects.filter((item) => item.status === "ACTIVE").length, "Currently active"],
          ["On Hold", projects.filter((item) => item.status === "ON_HOLD").length, "Temporarily paused"],
          ["Completed", projects.filter((item) => item.status === "COMPLETED").length, "Finished Projects"],
        ];

  if (!configuration) return null;

  return (
    <section className="hr-dashboard">
      <div className="hr-dashboard-header">
        <div>
          <p className="section-kicker">{configuration.kicker}</p>
          <h1>{configuration.title}</h1>
          <p className="dashboard-intro">
            Welcome, {user?.username}. {configuration.description}
          </p>
        </div>
      </div>

      <BusinessAlert feedback={feedback} />

      {loading ? (
        <BusinessLoading message="Loading dashboard information..." />
      ) : (
        <div className="stats-grid">
          {stats.map(([label, value, detail]) => (
            <article className="stat-card" key={label}>
              <span>{label}</span>
              <strong>{value}</strong>
              <p>{detail}</p>
            </article>
          ))}
        </div>
      )}

      <section className="dashboard-panel">
        <div className="panel-heading">
          <div>
            <p className="section-kicker">Quick Actions</p>
            <h2>Available for your role</h2>
          </div>
        </div>

        <div className="quick-actions">
          {configuration.actions.map(([label, path]) => (
            <NavLink to={path} key={path}>{label}</NavLink>
          ))}
        </div>
      </section>
    </section>
  );
}
