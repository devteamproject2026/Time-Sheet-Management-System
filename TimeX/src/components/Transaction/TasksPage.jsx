import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { businessApi, transactionApi } from "../../services/apiClient";
import {
  BusinessAlert,
  BusinessEmpty,
  BusinessLoading,
  StatusBadge,
} from "../Business/BusinessStates";
import "../Business/business.css";
import "./transaction.css";

const emptyForm = {
  projectId: "",
  employeeId: "",
  taskName: "",
  taskDescription: "",
  startDate: "",
  endDate: "",
};

export default function TasksPage() {
  const role = useSelector((state) => state.auth.user?.role);
  const managerView = role === "MANAGER";
  const [tasks, setTasks] = useState([]);
  const [projects, setProjects] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [progress, setProgress] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const [taskData, projectData] = await Promise.all([
          transactionApi(managerView ? "/tasks/managed" : "/tasks/my"),
          managerView
            ? businessApi("/projects/my-managed-projects")
            : Promise.resolve([]),
        ]);
        if (!cancelled) {
          setTasks(Array.isArray(taskData) ? taskData : []);
          setProjects(Array.isArray(projectData) ? projectData : []);
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

  const loadProjectEmployees = async (projectId) => {
    setEmployees([]);
    if (!projectId) return;
    try {
      const data = await businessApi(`/employee-projects/project/${projectId}`);
      setEmployees(Array.isArray(data) ? data : []);
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    }
  };

  const handleFormChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
    if (name === "projectId") {
      setForm((current) => ({ ...current, projectId: value, employeeId: "" }));
      loadProjectEmployees(value);
    }
  };

  const submitTask = async (event) => {
    event.preventDefault();
    setSaving(true);
    setFeedback({ type: "", message: "" });
    const body = {
      employeeId: Number(form.employeeId),
      taskName: form.taskName,
      taskDescription: form.taskDescription,
      startDate: form.startDate || null,
      endDate: form.endDate || null,
    };
    try {
      const saved = editingId
        ? await transactionApi(`/tasks/${editingId}`, { method: "PUT", body })
        : await transactionApi("/tasks", {
            method: "POST",
            body: { ...body, projectId: Number(form.projectId) },
          });
      setTasks((current) => editingId
        ? current.map((item) => item.taskId === editingId ? saved : item)
        : [saved, ...current]);
      setForm(emptyForm);
      setEmployees([]);
      setEditingId(null);
      setFeedback({
        type: "success",
        message: editingId ? "Task updated successfully." : "Task assigned successfully.",
      });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setSaving(false);
    }
  };

  const beginEdit = async (task) => {
    await loadProjectEmployees(task.projectId);
    setEditingId(task.taskId);
    setForm({
      projectId: String(task.projectId),
      employeeId: String(task.employeeId),
      taskName: task.taskName,
      taskDescription: task.taskDescription || "",
      startDate: task.startDate || "",
      endDate: task.endDate || "",
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const acceptTask = async (taskId) => {
    try {
      const updated = await transactionApi(`/tasks/${taskId}/accept`, { method: "PUT" });
      setTasks((current) => current.map((item) => item.taskId === taskId ? updated : item));
      setFeedback({ type: "success", message: "Task accepted. You can now update its progress." });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    }
  };

  const updateProgress = async (task) => {
    const value = progress[task.taskId] ?? task.progressPercent;
    try {
      const updated = await transactionApi(`/tasks/${task.taskId}/progress`, {
        method: "PUT",
        body: { progressPercent: Number(value), remarks: task.remarks || "" },
      });
      setTasks((current) => current.map((item) => item.taskId === task.taskId ? updated : item));
      setFeedback({ type: "success", message: "Task progress updated successfully." });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    }
  };

  return (
    <section className="business-page">
      <header className="business-page-header">
        <div>
          <p className="business-kicker">Transaction Service</p>
          <h1>{managerView ? "Task Management" : "My Tasks"}</h1>
          <p>{managerView
            ? "Create Tasks only for Employees assigned to your active Projects."
            : "Accept assigned Tasks and report progress from zero to 100 percent."}</p>
        </div>
      </header>

      <BusinessAlert feedback={feedback} />

      {managerView && (
        <div className="business-panel">
          <div className="business-panel-header">
            <h2>{editingId ? "Edit Task" : "Assign a Task"}</h2>
          </div>
          <form className="business-form" onSubmit={submitTask}>
            <div className="business-form-grid">
              <div className="business-field">
                <label htmlFor="taskProject">Project</label>
                <select id="taskProject" name="projectId" value={form.projectId}
                  onChange={handleFormChange} required disabled={Boolean(editingId)}>
                  <option value="">Select a managed Project</option>
                  {projects.filter((project) => project.status === "ACTIVE").map((project) => (
                    <option key={project.projectId} value={project.projectId}>{project.projectName}</option>
                  ))}
                </select>
              </div>
              <div className="business-field">
                <label htmlFor="taskEmployee">Employee</label>
                <select id="taskEmployee" name="employeeId" value={form.employeeId}
                  onChange={handleFormChange} required disabled={!form.projectId}>
                  <option value="">Select an assigned Employee</option>
                  {employees.map((item) => (
                    <option key={item.employeeProjectId} value={item.employeeId}>
                      {item.employeeFullName || item.employeeUsername}
                    </option>
                  ))}
                </select>
              </div>
              <div className="business-field business-field--full">
                <label htmlFor="taskName">Task name</label>
                <input id="taskName" name="taskName" value={form.taskName}
                  onChange={handleFormChange} maxLength="100" required />
              </div>
              <div className="business-field business-field--full">
                <label htmlFor="taskDescription">Description</label>
                <textarea id="taskDescription" name="taskDescription"
                  value={form.taskDescription} onChange={handleFormChange} maxLength="4000" />
              </div>
              <div className="business-field">
                <label htmlFor="taskStartDate">Start date</label>
                <input id="taskStartDate" name="startDate" type="date"
                  value={form.startDate} onChange={handleFormChange} />
              </div>
              <div className="business-field">
                <label htmlFor="taskEndDate">End date</label>
                <input id="taskEndDate" name="endDate" type="date"
                  value={form.endDate} onChange={handleFormChange} />
              </div>
            </div>
            <div className="business-form-actions">
              <button className="business-button" disabled={saving}>
                {saving ? "Saving..." : editingId ? "Update Task" : "Assign Task"}
              </button>
              {editingId && <button type="button" className="business-button business-button--secondary"
                onClick={() => { setEditingId(null); setForm(emptyForm); setEmployees([]); }}>Cancel</button>}
            </div>
          </form>
        </div>
      )}

      <div className="business-panel">
        <div className="business-panel-header">
          <h2>{managerView ? "Managed Tasks" : "Assigned Tasks"}</h2>
          <span className="business-count">{tasks.length} Tasks</span>
        </div>
        {loading ? <BusinessLoading message="Loading Tasks..." /> : tasks.length === 0 ? (
          <BusinessEmpty title="No Tasks found" message={managerView
            ? "Create the first Task using the form above."
            : "Your Manager has not assigned a Task yet."} />
        ) : (
          <div className="business-table-wrap"><table className="business-table">
            <thead><tr><th>Task</th><th>Project</th>{managerView && <th>Employee</th>}<th>Status</th><th>Progress</th><th>Dates</th><th>Action</th></tr></thead>
            <tbody>{tasks.map((task) => <tr key={task.taskId}>
              <td><span className="business-primary-text">{task.taskName}</span><span className="business-secondary-text">{task.taskDescription || "No description"}</span></td>
              <td>{task.projectName}</td>
              {managerView && <td>{task.employeeFullName || task.employeeUsername}</td>}
              <td><StatusBadge status={task.status} /></td>
              <td>{managerView ? `${task.progressPercent}%` : (
                <div className="transaction-progress-control">
                  <input type="number" min={task.progressPercent} max="100"
                    value={progress[task.taskId] ?? task.progressPercent}
                    disabled={task.status === "ASSIGNED" || task.status === "COMPLETED"}
                    onChange={(event) => setProgress((current) => ({ ...current, [task.taskId]: event.target.value }))} />
                  <span>%</span>
                </div>
              )}</td>
              <td>{task.startDate || "—"}<span className="business-secondary-text">to {task.endDate || "—"}</span></td>
              <td><div className="business-row-actions">
                {managerView && task.status !== "COMPLETED" && <button className="business-button business-button--secondary" onClick={() => beginEdit(task)}>Edit</button>}
                {!managerView && task.status === "ASSIGNED" && <button className="business-button" onClick={() => acceptTask(task.taskId)}>Accept</button>}
                {!managerView && ["ACCEPTED", "IN_PROGRESS"].includes(task.status) && <button className="business-button" onClick={() => updateProgress(task)}>Save Progress</button>}
              </div></td>
            </tr>)}</tbody>
          </table></div>
        )}
      </div>
    </section>
  );
}
