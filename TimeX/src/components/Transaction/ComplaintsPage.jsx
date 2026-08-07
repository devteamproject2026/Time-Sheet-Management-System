import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { transactionApi } from "../../services/apiClient";
import { BusinessAlert, BusinessEmpty, BusinessLoading, StatusBadge } from "../Business/BusinessStates";
import "../Business/business.css";

const emptyForm = { managerId: "", subject: "", description: "" };

export default function ComplaintsPage() {
  const managerView = useSelector((state) => state.auth.user?.role) === "MANAGER";
  const [complaints, setComplaints] = useState([]);
  const [managers, setManagers] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const [complaintData, managerData] = await Promise.all([
          transactionApi(managerView ? "/complaints/assigned" : "/complaints/my"),
          managerView ? Promise.resolve([]) : transactionApi("/complaints/available-managers"),
        ]);
        if (!cancelled) {
          setComplaints(Array.isArray(complaintData) ? complaintData : []);
          setManagers(Array.isArray(managerData) ? managerData : []);
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
    try {
      const saved = await transactionApi("/complaints", {
        method: "POST",
        body: { ...form, managerId: Number(form.managerId) },
      });
      setComplaints((current) => [saved, ...current]);
      setForm(emptyForm);
      setFeedback({ type: "success", message: "Complaint raised to the selected Manager." });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setSaving(false);
    }
  };

  const resolve = async (complaint) => {
    const resolution = window.prompt("Enter the resolution provided to the Employee:");
    if (!resolution?.trim()) return;
    setSaving(true);
    try {
      const updated = await transactionApi(`/complaints/${complaint.complaintId}/resolve`, {
        method: "PUT", body: { resolution },
      });
      setComplaints((current) => current.map((item) => item.complaintId === complaint.complaintId ? updated : item));
      setFeedback({ type: "success", message: "Complaint resolved successfully." });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setSaving(false);
    }
  };

  return <section className="business-page">
    <header className="business-page-header"><div>
      <p className="business-kicker">Transaction Service</p>
      <h1>{managerView ? "Assigned Complaints" : "My Complaints"}</h1>
      <p>{managerView ? "Review and resolve issues raised by Employees in your Project teams." : "Raise an issue only to a Manager connected through your assigned Projects."}</p>
    </div></header>
    <BusinessAlert feedback={feedback} />

    {!managerView && <div className="business-panel">
      <div className="business-panel-header"><h2>Raise a Complaint</h2></div>
      <form className="business-form" onSubmit={submit}>
        <div className="business-form-grid">
          <div className="business-field business-field--full"><label htmlFor="complaintManager">Manager</label>
            <select id="complaintManager" value={form.managerId} required onChange={(event) => setForm((current) => ({ ...current, managerId: event.target.value }))}>
              <option value="">Select a connected Manager</option>
              {managers.map((manager) => <option key={manager.userId} value={manager.userId}>{manager.fullName || manager.username} ({manager.username})</option>)}
            </select></div>
          <div className="business-field business-field--full"><label htmlFor="complaintSubject">Subject</label>
            <input id="complaintSubject" maxLength="100" value={form.subject} required onChange={(event) => setForm((current) => ({ ...current, subject: event.target.value }))} /></div>
          <div className="business-field business-field--full"><label htmlFor="complaintDescription">Description</label>
            <textarea id="complaintDescription" maxLength="4000" value={form.description} required onChange={(event) => setForm((current) => ({ ...current, description: event.target.value }))} /></div>
        </div>
        {managers.length === 0 && !loading && <BusinessAlert feedback={{ type: "info", message: "No connected Manager is available. Ask HR to verify your Project assignment." }} />}
        <button className="business-button" disabled={saving || managers.length === 0}>{saving ? "Submitting..." : "Raise Complaint"}</button>
      </form>
    </div>}

    <div className="business-panel">
      <div className="business-panel-header"><h2>Complaint History</h2><span className="business-count">{complaints.length} complaints</span></div>
      {loading ? <BusinessLoading message="Loading Complaints..." /> : complaints.length === 0
        ? <BusinessEmpty title="No Complaints found" message={managerView ? "No Employee has raised a Complaint to you." : "You have not raised a Complaint."} />
        : <div className="business-table-wrap"><table className="business-table">
          <thead><tr>{managerView ? <th>Employee</th> : <th>Manager</th>}<th>Issue</th><th>Status</th><th>Created</th><th>Resolution</th>{managerView && <th>Action</th>}</tr></thead>
          <tbody>{complaints.map((item) => <tr key={item.complaintId}>
            <td>{managerView ? (item.employeeFullName || item.employeeUsername) : (item.managerFullName || item.managerUsername)}</td>
            <td><span className="business-primary-text">{item.subject}</span><span className="business-secondary-text">{item.description}</span></td>
            <td><StatusBadge status={item.status} /></td>
            <td>{item.createdAt ? new Date(item.createdAt).toLocaleString() : "—"}</td>
            <td>{item.resolution || "Pending"}</td>
            {managerView && <td>{item.status !== "RESOLVED" && <button className="business-button" disabled={saving} onClick={() => resolve(item)}>Resolve</button>}</td>}
          </tr>)}</tbody>
        </table></div>}
    </div>
  </section>;
}
