import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { transactionApi } from "../../services/apiClient";
import { BusinessAlert, BusinessEmpty, BusinessLoading, StatusBadge } from "../Business/BusinessStates";
import "../Business/business.css";

const today = () => new Date().toISOString().slice(0, 10);

export default function AttendancePage() {
  const managerView = useSelector((state) => state.auth.user?.role) === "MANAGER";
  const [records, setRecords] = useState([]);
  const [date, setDate] = useState(today());
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  const load = async (selectedDate = date) => {
    setLoading(true);
    try {
      const data = await transactionApi(managerView
        ? `/attendance/team?date=${selectedDate}`
        : "/attendance/my");
      setRecords(Array.isArray(data) ? data : []);
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let cancelled = false;
    transactionApi(managerView ? `/attendance/team?date=${today()}` : "/attendance/my")
      .then((data) => { if (!cancelled) setRecords(Array.isArray(data) ? data : []); })
      .catch((error) => { if (!cancelled) setFeedback({ type: "error", message: error.message }); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [managerView]);

  const recordAction = async (action) => {
    setSaving(true);
    setFeedback({ type: "", message: "" });
    try {
      const updated = await transactionApi(`/attendance/${action}`, {
        method: action === "check-in" ? "POST" : "PUT",
      });
      setRecords((current) => [updated, ...current.filter((item) => item.attendanceId !== updated.attendanceId)]);
      setFeedback({ type: "success", message: action === "check-in" ? "Checked in successfully." : "Checked out successfully." });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setSaving(false);
    }
  };

  const todaysRecord = !managerView && records.find((item) => item.attendanceDate === today());

  return <section className="business-page">
    <header className="business-page-header"><div>
      <p className="business-kicker">Transaction Service</p>
      <h1>{managerView ? "Team Attendance" : "My Attendance"}</h1>
      <p>{managerView ? "View Attendance for Employees assigned to your Projects." : "Record one check-in and check-out for each working day."}</p>
    </div></header>
    <BusinessAlert feedback={feedback} />

    <div className="business-panel">
      {managerView ? <div className="business-toolbar">
        <div><p className="business-kicker">Attendance date</p><h2 className="h5 mb-0">Choose a day</h2></div>
        <input type="date" value={date} max={today()} onChange={(event) => { setDate(event.target.value); load(event.target.value); }} />
      </div> : <div className="business-form-actions">
        <button className="business-button" disabled={saving || Boolean(todaysRecord)} onClick={() => recordAction("check-in")}>Check In</button>
        <button className="business-button business-button--secondary" disabled={saving || !todaysRecord || Boolean(todaysRecord?.checkOut)} onClick={() => recordAction("check-out")}>Check Out</button>
      </div>}
    </div>

    <div className="business-panel">
      <div className="business-panel-header"><h2>{managerView ? `Team Records — ${date}` : "Attendance History"}</h2><span className="business-count">{records.length} records</span></div>
      {loading ? <BusinessLoading message="Loading Attendance..." /> : records.length === 0
        ? <BusinessEmpty title="No Attendance found" message={managerView ? "No managed Employee recorded Attendance for this date." : "Use Check In to create today's Attendance."} />
        : <div className="business-table-wrap"><table className="business-table">
          <thead><tr>{managerView && <th>Employee</th>}<th>Date</th><th>Check in</th><th>Check out</th><th>Status</th></tr></thead>
          <tbody>{records.map((item) => <tr key={item.attendanceId}>
            {managerView && <td><span className="business-primary-text">{item.employeeFullName || item.employeeUsername}</span><span className="business-secondary-text">{item.employeeUsername}</span></td>}
            <td>{item.attendanceDate}</td><td>{item.checkIn || "—"}</td><td>{item.checkOut || "—"}</td><td><StatusBadge status={item.status} /></td>
          </tr>)}</tbody>
        </table></div>}
    </div>
  </section>;
}
