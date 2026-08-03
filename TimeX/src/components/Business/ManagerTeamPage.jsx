import { useEffect, useState } from "react";
import { businessApi } from "../../services/apiClient";
import {
  BusinessAlert,
  BusinessEmpty,
  BusinessLoading,
} from "./BusinessStates";
import "./business.css";

export default function ManagerTeamPage() {
  const [projects, setProjects] = useState([]);
  const [selectedProjectId, setSelectedProjectId] = useState("");
  const [team, setTeam] = useState([]);
  const [loading, setLoading] = useState(true);
  const [teamLoading, setTeamLoading] = useState(false);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  useEffect(() => {
    let cancelled = false;

    const load = async () => {
      try {
        const projectData = await businessApi("/projects/my-managed-projects");
        const availableProjects = Array.isArray(projectData) ? projectData : [];

        if (cancelled) return;
        setProjects(availableProjects);

        if (availableProjects.length > 0) {
          const firstProjectId = availableProjects[0].projectId;
          setSelectedProjectId(String(firstProjectId));
          const teamData = await businessApi(
            `/employee-projects/project/${firstProjectId}`
          );
          if (!cancelled) setTeam(Array.isArray(teamData) ? teamData : []);
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

  const changeProject = async (event) => {
    const projectId = event.target.value;
    setSelectedProjectId(projectId);
    setTeam([]);
    setFeedback({ type: "", message: "" });

    if (!projectId) return;

    setTeamLoading(true);
    try {
      const data = await businessApi(`/employee-projects/project/${projectId}`);
      setTeam(Array.isArray(data) ? data : []);
    } catch (error) {
      setFeedback({ type: "error", message: error.message });
    } finally {
      setTeamLoading(false);
    }
  };

  return (
    <section className="business-page">
      <header className="business-page-header">
        <div>
          <p className="business-kicker">Business Service</p>
          <h1>My Project Teams</h1>
          <p>
            Select one of your managed Projects to see the Employees assigned by HR.
          </p>
        </div>
      </header>

      <BusinessAlert feedback={feedback} />

      <div className="business-panel">
        <div className="business-toolbar">
          <div>
            <p className="business-kicker">Team filter</p>
            <h2 className="h5 mb-0">Select a managed Project</h2>
          </div>

          <select
            value={selectedProjectId}
            onChange={changeProject}
            disabled={loading || projects.length === 0}
            aria-label="Select a managed Project"
          >
            <option value="">Select a Project</option>
            {projects.map((project) => (
              <option key={project.projectId} value={project.projectId}>
                {project.projectName}
              </option>
            ))}
          </select>
        </div>

        {loading || teamLoading ? (
          <BusinessLoading message="Loading Project team..." />
        ) : projects.length === 0 ? (
          <BusinessEmpty
            title="No managed Projects"
            message="HR has not assigned any Projects to your Manager account."
          />
        ) : team.length === 0 ? (
          <BusinessEmpty
            title="No team members assigned"
            message="HR has not added Employees to this Project yet."
          />
        ) : (
          <div className="business-table-wrap">
            <table className="business-table">
              <thead>
                <tr>
                  <th>Employee</th>
                  <th>Username</th>
                  <th>Project</th>
                  <th>Assigned date</th>
                </tr>
              </thead>
              <tbody>
                {team.map((assignment) => (
                  <tr key={assignment.employeeProjectId}>
                    <td className="business-primary-text">
                      {assignment.employeeFullName || assignment.employeeUsername}
                    </td>
                    <td>{assignment.employeeUsername}</td>
                    <td>{assignment.projectName}</td>
                    <td>
                      {assignment.assignedDate
                        ? new Date(assignment.assignedDate).toLocaleDateString()
                        : "—"}
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
