import { useEffect, useState } from "react";
import { transactionApi } from "../../services/apiClient";
import { BusinessAlert, BusinessEmpty, BusinessLoading } from "../Business/BusinessStates";
import "../Business/business.css";
import "./transaction.css";

export default function ReportsPage() {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  useEffect(() => {
    let cancelled = false;
    transactionApi("/reports/employees")
      .then((data) => { if (!cancelled) setReports(Array.isArray(data) ? data : []); })
      .catch((error) => { if (!cancelled) setFeedback({ type: "error", message: error.message }); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, []);

  const totals = reports.reduce((result, item) => ({
    tasks: result.tasks + item.totalTasks,
    completed: result.completed + item.completedTasks,
    hours: result.hours + Number(item.totalApprovedHours || 0),
    pending: result.pending + item.pendingTimesheets,
  }), { tasks: 0, completed: 0, hours: 0, pending: 0 });

  return <section className="business-page">
    <header className="business-page-header"><div><p className="business-kicker">Transaction Service</p><h1>Employee Reports</h1><p>Monitor Task completion and approved working hours for Employees in your Projects.</p></div></header>
    <BusinessAlert feedback={feedback} />
    <div className="transaction-summary-grid">
      <article className="transaction-summary-card"><span>Total Tasks</span><strong>{totals.tasks}</strong></article>
      <article className="transaction-summary-card"><span>Completed Tasks</span><strong>{totals.completed}</strong></article>
      <article className="transaction-summary-card"><span>Approved Hours</span><strong>{totals.hours.toFixed(2)}</strong></article>
      <article className="transaction-summary-card"><span>Pending Timesheets</span><strong>{totals.pending}</strong></article>
    </div>
    <div className="business-panel"><div className="business-panel-header"><h2>Team Performance</h2><span className="business-count">{reports.length} Employees</span></div>
      {loading ? <BusinessLoading message="Loading Employee reports..." /> : reports.length === 0
        ? <BusinessEmpty title="No Employee reports" message="HR has not assigned Employees to your Projects." />
        : <div className="business-table-wrap"><table className="business-table">
          <thead><tr><th>Employee</th><th>Tasks</th><th>Completed</th><th>Average progress</th><th>Approved hours</th><th>Pending</th><th>Approved</th><th>Rejected</th></tr></thead>
          <tbody>{reports.map((item) => <tr key={item.employeeId}>
            <td><span className="business-primary-text">{item.employeeFullName}</span><span className="business-secondary-text">{item.employeeUsername}</span></td>
            <td>{item.totalTasks}</td><td>{item.completedTasks}</td><td>{item.averageProgress}%</td><td>{item.totalApprovedHours}</td><td>{item.pendingTimesheets}</td><td>{item.approvedTimesheets}</td><td>{item.rejectedTimesheets}</td>
          </tr>)}</tbody>
        </table></div>}
    </div>
  </section>;
}
