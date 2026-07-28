import { NavLink } from "react-router-dom";
import "./HomeComp.css";

export default function HomeComp() {
  const features = [
    {
      title: "Weekly Timesheets",
      text: "Employees can record project-wise effort and submit weekly hours.",
    },
    {
      title: "Approval Flow",
      text: "Managers review submitted timesheets and keep work records clear.",
    },
    {
      title: "Role-Based Access",
      text: "Admin, HR, Manager, and Employee users get focused workspaces.",
    },
  ];

  const steps = [
    "HR registers the organization",
    "Admin approves the HR request",
    "HR creates managers and employees",
    "Employees submit weekly timesheets",
    "Managers approve or reject submissions",
  ];

  return (
    <main className="home-page">
      <nav className="home-nav">
        <NavLink className="home-brand" to="/">
          <span>WP</span>
          <div>
            <strong>WorkPuls</strong>
            <small>Timesheet Management</small>
          </div>
        </NavLink>

        <div className="home-nav-actions">
          <NavLink to="/login">Login</NavLink>
          <NavLink className="nav-register" to="/hr-register">
            HR Register
          </NavLink>
        </div>
      </nav>

      <section className="home-hero">
        <div className="hero-content">
          <p className="home-kicker">Time Sheet Management System</p>
          <h1>Manage work hours, projects, and approvals in one place.</h1>
          <p>
            WorkPuls helps organizations track weekly timesheets, manage people,
            and keep approvals clear across HR, managers, and employees.
          </p>

          <div className="hero-actions">
            <NavLink className="hero-primary" to="/login">
              Login
            </NavLink>
            <NavLink className="hero-secondary" to="/hr-register">
              Register as HR Head
            </NavLink>
          </div>
        </div>

        <div className="hero-summary" aria-label="System overview">
          <div className="summary-card summary-main">
            <span>Weekly Review</span>
            <strong>40 hrs</strong>
            <p>Submitted for manager approval</p>
          </div>
          <div className="summary-grid">
            <div className="summary-card">
              <span>Roles</span>
              <strong>4</strong>
            </div>
            <div className="summary-card">
              <span>Status</span>
              <strong>Live</strong>
            </div>
          </div>
        </div>
      </section>

      <section className="home-section">
        <div className="section-heading">
          <p className="home-kicker">Core Features</p>
          <h2>Built around the real timesheet workflow</h2>
        </div>

        <div className="feature-grid">
          {features.map((feature) => (
            <article className="feature-card" key={feature.title}>
              <h3>{feature.title}</h3>
              <p>{feature.text}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="home-section workflow-section">
        <div className="section-heading">
          <p className="home-kicker">Process</p>
          <h2>How WorkPuls works</h2>
        </div>

        <ol className="workflow-list">
          {steps.map((step) => (
            <li key={step}>{step}</li>
          ))}
        </ol>
      </section>
    </main>
  );
}
