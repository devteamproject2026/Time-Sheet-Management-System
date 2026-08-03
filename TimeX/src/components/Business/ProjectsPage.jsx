import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { authApi, businessApi } from "../../services/apiClient";
import {
  BusinessAlert,
  BusinessEmpty,
  BusinessLoading,
  StatusBadge,
} from "./BusinessStates";
import "./business.css";

const createEmptyProject = (hrHeadId = "") => ({
  projectName: "",
  description: "",
  clientId: "",
  managerId: "",
  hrHeadId: hrHeadId || "",
  startDate: "",
  endDate: "",
  status: "ACTIVE",
});

const projectEndpointFor = (role) => {
  if (role === "MANAGER") return "/projects/my-managed-projects";
  if (role === "EMPLOYEE") return "/projects/my-assigned-projects";
  return "/projects";
};

const roleDescription = {
  ADMIN: "Review every Project, its Client, Manager and current status.",
  HR_HEAD: "Create Projects, select their Client and Manager, and maintain Project status.",
  MANAGER: "View only the Projects currently managed by your account.",
  EMPLOYEE: "View only the Projects to which HR has assigned you.",
};

export default function ProjectsPage() {
  const user = useSelector((state) => state.auth.user);
  const role = user?.role;
  const canEdit = role === "HR_HEAD";

  const [projects, setProjects] = useState([]);
  const [clients, setClients] = useState([]);
  const [managers, setManagers] = useState([]);
  const [formData, setFormData] = useState(createEmptyProject(user?.userId));
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  useEffect(() => {
    let cancelled = false;

    const load = async () => {
      try {
        const requests = [businessApi(projectEndpointFor(role))];

        if (role === "HR_HEAD") {
          requests.push(businessApi("/clients"));
          requests.push(authApi("/users/managers"));
        }

        const [projectData, clientData = [], managerData = []] =
          await Promise.all(requests);

        if (!cancelled) {
          setProjects(Array.isArray(projectData) ? projectData : []);
          setClients(Array.isArray(clientData) ? clientData : []);
          setManagers(Array.isArray(managerData) ? managerData : []);
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
  }, [role]);

  const resetForm = () => {
    setFormData(createEmptyProject(user?.userId));
    setEditingId(null);
    setShowForm(false);
  };

  const handleChange = (event) => {
    setFormData((current) => ({
      ...current,
      [event.target.name]: event.target.value,
    }));
  };

  const startCreate = () => {
    setFormData(createEmptyProject(user?.userId));
    setEditingId(null);
    setFeedback({ type: "", message: "" });
    setShowForm(true);
  };

  const startEdit = (project) => {
    setFormData({
      projectName: project.projectName || "",
      description: project.description || "",
      clientId: String(project.clientId || ""),
      managerId: String(project.managerId || ""),
      hrHeadId: String(project.hrHeadId || user?.userId || ""),
      startDate: project.startDate || "",
      endDate: project.endDate || "",
      status: project.status || "ACTIVE",
    });
    setEditingId(project.projectId);
    setFeedback({ type: "", message: "" });
    setShowForm(true);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setFeedback({ type: "", message: "" });

    const requestBody = {
      ...formData,
      clientId: Number(formData.clientId),
      managerId: Number(formData.managerId),
      hrHeadId: Number(formData.hrHeadId),
      startDate: formData.startDate || null,
      endDate: formData.endDate || null,
    };

    try {
      const savedProject = await businessApi(
        editingId ? `/projects/${editingId}` : "/projects",
        {
          method: editingId ? "PUT" : "POST",
          body: requestBody,
        }
      );

      setProjects((current) =>
        editingId
          ? current.map((project) =>
              project.projectId === editingId ? savedProject : project
            )
          : [savedProject, ...current]
      );
      setFeedback({
        type: "success",
        message: `Project ${editingId ? "updated" : "created"} successfully.`,
      });
      resetForm();
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="business-page">
      <header className="business-page-header">
        <div>
          <p className="business-kicker">Business Service</p>
          <h1>{role === "EMPLOYEE" ? "My Assigned Projects" : role === "MANAGER" ? "My Managed Projects" : "Projects"}</h1>
          <p>{roleDescription[role] || "View Project information."}</p>
        </div>

        {canEdit && !showForm && (
          <button className="business-button" type="button" onClick={startCreate}>
            Create Project
          </button>
        )}
      </header>

      <BusinessAlert feedback={feedback} />

      {canEdit && showForm && (
        <div className="business-panel">
          <div className="business-panel-header">
            <h2>{editingId ? "Update Project" : "Create Project"}</h2>
            <button
              className="business-button business-button--secondary"
              type="button"
              onClick={resetForm}
              disabled={saving}
            >
              Cancel
            </button>
          </div>

          {(clients.length === 0 || managers.length === 0) && (
            <BusinessAlert
              feedback={{
                type: "info",
                message:
                  "A Project needs at least one Client and one active Manager. Create or activate them first.",
              }}
            />
          )}

          <form className="business-form" onSubmit={handleSubmit}>
            <div className="business-form-grid">
              <div className="business-field business-field--full">
                <label htmlFor="projectName">Project name</label>
                <input
                  id="projectName"
                  name="projectName"
                  value={formData.projectName}
                  onChange={handleChange}
                  maxLength="100"
                  required
                />
              </div>

              <div className="business-field business-field--full">
                <label htmlFor="projectDescription">Description</label>
                <textarea
                  id="projectDescription"
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                />
              </div>

              <div className="business-field">
                <label htmlFor="projectClient">Client</label>
                <select
                  id="projectClient"
                  name="clientId"
                  value={formData.clientId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select a client</option>
                  {clients.map((client) => (
                    <option key={client.clientId} value={client.clientId}>
                      {client.clientName} — {client.companyName || "No company"}
                    </option>
                  ))}
                </select>
              </div>

              <div className="business-field">
                <label htmlFor="projectManager">Manager</label>
                <select
                  id="projectManager"
                  name="managerId"
                  value={formData.managerId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select a manager</option>
                  {managers.map((manager) => (
                    <option key={manager.userId} value={manager.userId}>
                      {manager.fullName || manager.username} ({manager.username})
                    </option>
                  ))}
                </select>
              </div>

              <div className="business-field">
                <label htmlFor="projectStartDate">Start date</label>
                <input
                  id="projectStartDate"
                  name="startDate"
                  type="date"
                  value={formData.startDate}
                  onChange={handleChange}
                />
              </div>

              <div className="business-field">
                <label htmlFor="projectEndDate">End date</label>
                <input
                  id="projectEndDate"
                  name="endDate"
                  type="date"
                  min={formData.startDate || undefined}
                  value={formData.endDate}
                  onChange={handleChange}
                />
              </div>

              <div className="business-field">
                <label htmlFor="projectStatus">Status</label>
                <select
                  id="projectStatus"
                  name="status"
                  value={formData.status}
                  onChange={handleChange}
                >
                  <option value="ACTIVE">Active</option>
                  <option value="ON_HOLD">On hold</option>
                  <option value="COMPLETED">Completed</option>
                </select>
              </div>

              <div className="business-field">
                <label>HR Head</label>
                <input value={user?.username || "Current HR"} disabled />
              </div>
            </div>

            <div className="business-form-actions">
              <button
                className="business-button"
                type="submit"
                disabled={saving || clients.length === 0 || managers.length === 0}
              >
                {saving
                  ? "Saving..."
                  : editingId
                    ? "Update Project"
                    : "Create Project"}
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="business-panel">
        <div className="business-panel-header">
          <h2>Project Directory</h2>
          <span className="business-count">{projects.length} projects</span>
        </div>

        {loading ? (
          <BusinessLoading message="Loading projects..." />
        ) : projects.length === 0 ? (
          <BusinessEmpty
            title="No projects found"
            message={canEdit ? "Create a Project after adding a Client and Manager." : "No Projects are currently available for your account."}
          />
        ) : (
          <div className="business-table-wrap">
            <table className="business-table">
              <thead>
                <tr>
                  <th>Project</th>
                  <th>Client</th>
                  <th>Manager</th>
                  <th>Timeline</th>
                  <th>Status</th>
                  {canEdit && <th>Action</th>}
                </tr>
              </thead>
              <tbody>
                {projects.map((project) => (
                  <tr key={project.projectId}>
                    <td>
                      <span className="business-primary-text">{project.projectName}</span>
                      <span className="business-secondary-text">
                        {project.description || `Project ID: ${project.projectId}`}
                      </span>
                    </td>
                    <td>{project.clientName || "—"}</td>
                    <td>{project.managerUsername || "—"}</td>
                    <td>
                      {project.startDate || "Not set"}
                      <span className="business-secondary-text">
                        to {project.endDate || "Not set"}
                      </span>
                    </td>
                    <td><StatusBadge status={project.status} /></td>
                    {canEdit && (
                      <td>
                        <button
                          className="business-button business-button--secondary"
                          type="button"
                          onClick={() => startEdit(project)}
                        >
                          Edit
                        </button>
                      </td>
                    )}
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
