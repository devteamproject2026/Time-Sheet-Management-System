import { NavLink } from "react-router-dom";
import "./HrDashboard.css";

export default function HrDashboard() {
  const stats = [
    { label: "Total Employees", value: "24", detail: "3 joined this month" },
    { label: "Managers", value: "6", detail: "Across active teams" },
    { label: "Active Projects", value: "8", detail: "5 currently staffed" },
    { label: "Pending Timesheets", value: "12", detail: "Need review soon" },
  ];

  const activities = [
    "New manager account created for Product Team",
    "Three employee profiles are ready for project assignment",
    "Weekly timesheet review window closes this Friday",
  ];

  return (
    <section className="hr-dashboard">
      <div className="hr-dashboard-header">
        <div>
          <p className="section-kicker">HR Workspace</p>
          <h1>HR Dashboard</h1>
          <p className="dashboard-intro">
            Manage people, project staffing, and timesheet activity from one
            organized view.
          </p>
        </div>

        <div className="header-actions">
          <NavLink className="primary-action" to="/hr/create-employee">
            Add Employee
          </NavLink>
          <NavLink className="secondary-action" to="/hr/create-manager">
            Add Manager
          </NavLink>
        </div>
      </div>

      <div className="stats-grid">
        {stats.map((item) => (
          <article className="stat-card" key={item.label}>
            <span>{item.label}</span>
            <strong>{item.value}</strong>
            <p>{item.detail}</p>
          </article>
        ))}
      </div>

      <div className="dashboard-grid">
        <section className="dashboard-panel">
          <div className="panel-heading">
            <div>
              <p className="section-kicker">Quick Actions</p>
              <h2>Common HR Tasks</h2>
            </div>
          </div>

          <div className="quick-actions">
            <NavLink to="/hr/create-manager">Create Manager</NavLink>
            <NavLink to="/hr/create-employee">Create Employee</NavLink>
            <NavLink to="/hr/timesheets">Review Timesheets</NavLink>
          </div>
        </section>

        <section className="dashboard-panel">
          <div className="panel-heading">
            <div>
              <p className="section-kicker">Today</p>
              <h2>Recent Activity</h2>
            </div>
          </div>

          <ul className="activity-list">
            {activities.map((activity) => (
              <li key={activity}>{activity}</li>
            ))}
          </ul>
        </section>
      </div>
    </section>
  );
}
