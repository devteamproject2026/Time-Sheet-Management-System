import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { transactionApi } from "../../services/apiClient";
import { BusinessAlert, BusinessEmpty, BusinessLoading, StatusBadge } from "../Business/BusinessStates";
import "../Business/business.css";

const emptyForm = { taskId: "", workDate: "", hoursWorked: "", workDescription: "" };

export default function TimesheetsPage() {
  const managerView = useSelector((state) => state.auth.user?.role) === "MANAGER";
  const [timesheets, setTimesheets] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [reviewingId, setReviewingId] = useState(null);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const [sheetData, taskData] = await Promise.all([
          transactionApi(managerView ? "/timesheets/review" : "/timesheets/my"),
          managerView ? Promise.resolve([]) : transactionApi("/tasks/my"),
        ]);
        if (!cancelled) {
          setTimesheets(Array.isArray(sheetData) ? sheetData : []);
          setTasks(Array.isArray(taskData) ? taskData : []);
        }
      } catch (error) {
        if (!cancelled) setFeedback({ type: "error", message: error.message });
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, [managerView]);

  const submit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setFeedback({ type: "", message: "" });
    try {
      const saved = await transactionApi("/timesheets", {
        method: "POST",
        body: {
          taskId: Number(form.taskId),
          workDate: form.workDate,
          hoursWorked: Number(form.hoursWorked),
          workDescription: form.workDescription,
        },
      });
      setTimesheets((current) => [saved, ...current]);
      setForm(emptyForm);
      setFeedback({ type: "success", message: "Timesheet submitted for Manager review." });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setSaving(false);
    }
  };

  const review = async (timesheet, decision) => {
    const comments = window.prompt(
      decision === "APPROVED" ? "Optional approval comments:" : "Reason for rejection:"
    );
    if (comments === null) return;
    setReviewingId(timesheet.timesheetId);
    try {
      const updated = await transactionApi(
        `/timesheet-approvals/timesheet/${timesheet.timesheetId}`,
        { method: "POST", body: { decision, comments } }
      );
      setTimesheets((current) => current.map((item) =>
        item.timesheetId === timesheet.timesheetId ? updated : item
      ));
      setFeedback({ type: "success", message: `Timesheet ${decision.toLowerCase()} successfully.` });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setReviewingId(null);
    }
  };

  const eligibleTasks = tasks.filter((task) => task.status !== "ASSIGNED");

  return <section className="business-page">
    <header className="business-page-header"><div>
      <p className="business-kicker">Transaction Service</p>
      <h1>{managerView ? "Team Timesheets" : "My Timesheets"}</h1>
      <p>{managerView
        ? "Review work submitted against Tasks that you manage."
        : "Record daily hours against an accepted Task and track its approval status."}</p>
    </div></header>
    <BusinessAlert feedback={feedback} />

    {!managerView && <div className="business-panel">
      <div className="business-panel-header"><h2>Submit Timesheet</h2></div>
      <form className="business-form" onSubmit={submit}>
        <div className="business-form-grid">
          <div className="business-field business-field--full">
            <label htmlFor="sheetTask">Accepted Task</label>
            <select id="sheetTask" value={form.taskId} required
              onChange={(event) => setForm((current) => ({ ...current, taskId: event.target.value }))}>
              <option value="">Select a Task</option>
              {eligibleTasks.map((task) => <option key={task.taskId} value={task.taskId}>
                {task.taskName} — {task.projectName}
              </option>)}
            </select>
          </div>
          <div className="business-field"><label htmlFor="workDate">Work date</label>
            <input id="workDate" type="date" value={form.workDate} max={new Date().toISOString().slice(0, 10)} required
              onChange={(event) => setForm((current) => ({ ...current, workDate: event.target.value }))} /></div>
          <div className="business-field"><label htmlFor="hoursWorked">Hours worked</label>
            <input id="hoursWorked" type="number" min="0.25" max="24" step="0.25" value={form.hoursWorked} required
              onChange={(event) => setForm((current) => ({ ...current, hoursWorked: event.target.value }))} /></div>
          <div className="business-field business-field--full"><label htmlFor="workDescription">Work description</label>
            <textarea id="workDescription" value={form.workDescription} maxLength="4000" required
              onChange={(event) => setForm((current) => ({ ...current, workDescription: event.target.value }))} /></div>
        </div>
        {eligibleTasks.length === 0 && !loading && <BusinessAlert feedback={{ type: "info", message: "Accept a Task before submitting a Timesheet." }} />}
        <button className="business-button" disabled={saving || eligibleTasks.length === 0}>{saving ? "Submitting..." : "Submit Timesheet"}</button>
      </form>
    </div>}

    <div className="business-panel">
      <div className="business-panel-header"><h2>{managerView ? "Timesheets for Review" : "Submission History"}</h2><span className="business-count">{timesheets.length} entries</span></div>
      {loading ? <BusinessLoading message="Loading Timesheets..." /> : timesheets.length === 0
        ? <BusinessEmpty title="No Timesheets found" message={managerView ? "Your team has not submitted time yet." : "Submit your first Timesheet above."} />
        : <div className="business-table-wrap"><table className="business-table">
          <thead><tr>{managerView && <th>Employee</th>}<th>Task</th><th>Project</th><th>Date</th><th>Hours</th><th>Status</th>{managerView && <th>Review</th>}</tr></thead>
          <tbody>{timesheets.map((sheet) => <tr key={sheet.timesheetId}>
            {managerView && <td>{sheet.employeeFullName || sheet.employeeUsername}</td>}
            <td><span className="business-primary-text">{sheet.taskName}</span><span className="business-secondary-text">{sheet.workDescription}</span></td>
            <td>{sheet.projectName}</td><td>{sheet.workDate}</td><td>{sheet.hoursWorked}</td><td><StatusBadge status={sheet.status} /></td>
            {managerView && <td><div className="business-row-actions">
              {sheet.status === "PENDING" ? <>
                <button className="business-button" disabled={reviewingId === sheet.timesheetId} onClick={() => review(sheet, "APPROVED")}>Approve</button>
                <button className="business-button business-button--danger" disabled={reviewingId === sheet.timesheetId} onClick={() => review(sheet, "REJECTED")}>Reject</button>
              </> : <span className="business-secondary-text">Reviewed</span>}
            </div></td>}
          </tr>)}</tbody>
        </table></div>}
    </div>
  </section>;
}
