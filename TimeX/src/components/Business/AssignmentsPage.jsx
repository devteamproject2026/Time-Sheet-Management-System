import { useEffect, useState } from "react";
import { authApi, businessApi } from "../../services/apiClient";
import {
  BusinessAlert,
  BusinessEmpty,
  BusinessLoading,
} from "./BusinessStates";
import "./business.css";

export default function AssignmentsPage() {
  const [assignments, setAssignments] = useState([]);
  const [projects, setProjects] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [formData, setFormData] = useState({ employeeId: "", projectId: "" });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [removingId, setRemovingId] = useState(null);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  useEffect(() => {
    let cancelled = false;

    const load = async () => {
      try {
        const [assignmentData, projectData, employeeData] = await Promise.all([
          businessApi("/employee-projects"),
          businessApi("/projects"),
          authApi("/users/employees"),
        ]);

        if (!cancelled) {
          setAssignments(Array.isArray(assignmentData) ? assignmentData : []);
          setProjects(Array.isArray(projectData) ? projectData : []);
          setEmployees(Array.isArray(employeeData) ? employeeData : []);
        }
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
  }, []);

  const handleChange = (event) => {
    setFormData((current) => ({
      ...current,
      [event.target.name]: event.target.value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setFeedback({ type: "", message: "" });

    try {
      const assignment = await businessApi("/employee-projects", {
        method: "POST",
        body: {
          employeeId: Number(formData.employeeId),
          projectId: Number(formData.projectId),
        },
      });

      setAssignments((current) => [assignment, ...current]);
      setFormData({ employeeId: "", projectId: "" });
      setFeedback({
        type: "success",
        message: "Employee assigned to the Project successfully.",
      });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setSaving(false);
    }
  };

  const removeAssignment = async (assignment) => {
    const confirmed = window.confirm(
      `Remove ${assignment.employeeFullName || assignment.employeeUsername} from ${assignment.projectName}?`
    );
    if (!confirmed) return;

    setRemovingId(assignment.employeeProjectId);
    setFeedback({ type: "", message: "" });

    try {
      await businessApi(`/employee-projects/${assignment.employeeProjectId}`, {
        method: "DELETE",
      });
      setAssignments((current) =>
        current.filter(
          (item) => item.employeeProjectId !== assignment.employeeProjectId
        )
      );
      setFeedback({
        type: "success",
        message: "Employee removed from the Project successfully.",
      });
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setRemovingId(null);
    }
  };

  return (
    <section className="business-page">
      <header className="business-page-header">
        <div>
          <p className="business-kicker">Business Service</p>
          <h1>Employee Assignments</h1>
          <p>
            Assign active Employees to Projects and maintain Project staffing.
            Only HR can change these assignments.
          </p>
        </div>
      </header>

      <BusinessAlert feedback={feedback} />

      <div className="business-panel">
        <div className="business-panel-header">
          <h2>Assign an Employee</h2>
        </div>

        <form className="business-form" onSubmit={handleSubmit}>
          <div className="business-form-grid">
            <div className="business-field">
              <label htmlFor="assignmentEmployee">Employee</label>
              <select
                id="assignmentEmployee"
                name="employeeId"
                value={formData.employeeId}
                onChange={handleChange}
                required
              >
                <option value="">Select an employee</option>
                {employees.map((employee) => (
                  <option key={employee.userId} value={employee.userId}>
                    {employee.fullName || employee.username} ({employee.username})
                  </option>
                ))}
              </select>
            </div>

            <div className="business-field">
              <label htmlFor="assignmentProject">Project</label>
              <select
                id="assignmentProject"
                name="projectId"
                value={formData.projectId}
                onChange={handleChange}
                required
              >
                <option value="">Select a project</option>
                {projects.map((project) => (
                  <option key={project.projectId} value={project.projectId}>
                    {project.projectName} — {project.managerUsername}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {(employees.length === 0 || projects.length === 0) && !loading && (
            <BusinessAlert
              feedback={{
                type: "info",
                message:
                  "Create an active Employee and Project before creating an assignment.",
              }}
            />
          )}

          <div className="business-form-actions">
            <button
              className="business-button"
              type="submit"
              disabled={saving || employees.length === 0 || projects.length === 0}
            >
              {saving ? "Assigning..." : "Assign Employee"}
            </button>
          </div>
        </form>
      </div>

      <div className="business-panel">
        <div className="business-panel-header">
          <h2>Current Assignments</h2>
          <span className="business-count">{assignments.length} assignments</span>
        </div>

        {loading ? (
          <BusinessLoading message="Loading assignments..." />
        ) : assignments.length === 0 ? (
          <BusinessEmpty
            title="No assignments found"
            message="Select an Employee and Project above to create the first assignment."
          />
        ) : (
          <div className="business-table-wrap">
            <table className="business-table">
              <thead>
                <tr>
                  <th>Employee</th>
                  <th>Project</th>
                  <th>Manager</th>
                  <th>Assigned</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {assignments.map((assignment) => (
                  <tr key={assignment.employeeProjectId}>
                    <td>
                      <span className="business-primary-text">
                        {assignment.employeeFullName || assignment.employeeUsername}
                      </span>
                      <span className="business-secondary-text">
                        {assignment.employeeUsername}
                      </span>
                    </td>
                    <td>{assignment.projectName}</td>
                    <td>{assignment.managerUsername || "—"}</td>
                    <td>
                      {assignment.assignedDate
                        ? new Date(assignment.assignedDate).toLocaleDateString()
                        : "—"}
                    </td>
                    <td>
                      <button
                        className="business-button business-button--danger"
                        type="button"
                        disabled={removingId !== null}
                        onClick={() => removeAssignment(assignment)}
                      >
                        {removingId === assignment.employeeProjectId
                          ? "Removing..."
                          : "Remove"}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </section>
  );
}
