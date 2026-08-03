import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { businessApi } from "../../services/apiClient";
import {
  BusinessAlert,
  BusinessEmpty,
  BusinessLoading,
} from "./BusinessStates";
import "./business.css";

const emptyClient = {
  clientName: "",
  companyName: "",
  email: "",
  contact: "",
  address: "",
};

const fetchClients = () => businessApi("/clients");

export default function ClientsPage() {
  const role = useSelector((state) => state.auth.user?.role);
  const canEdit = role === "HR_HEAD";

  const [clients, setClients] = useState([]);
  const [formData, setFormData] = useState(emptyClient);
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [feedback, setFeedback] = useState({ type: "", message: "" });

  useEffect(() => {
    let cancelled = false;

    const load = async () => {
      try {
        const data = await fetchClients();
        if (!cancelled) setClients(Array.isArray(data) ? data : []);
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

  const resetForm = () => {
    setFormData(emptyClient);
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
    setFormData(emptyClient);
    setEditingId(null);
    setFeedback({ type: "", message: "" });
    setShowForm(true);
  };

  const startEdit = (client) => {
    setFormData({
      clientName: client.clientName || "",
      companyName: client.companyName || "",
      email: client.email || "",
      contact: client.contact || "",
      address: client.address || "",
    });
    setEditingId(client.clientId);
    setFeedback({ type: "", message: "" });
    setShowForm(true);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setFeedback({ type: "", message: "" });

    try {
      const savedClient = await businessApi(
        editingId ? `/clients/${editingId}` : "/clients",
        {
          method: editingId ? "PUT" : "POST",
          body: formData,
        }
      );

      setClients((current) =>
        editingId
          ? current.map((client) =>
              client.clientId === editingId ? savedClient : client
            )
          : [savedClient, ...current]
      );
      setFeedback({
        type: "success",
        message: `Client ${editingId ? "updated" : "created"} successfully.`,
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
          <h1>Clients</h1>
          <p>
            {canEdit
              ? "Create clients and maintain the contact details used by Projects."
              : "Review client and company information across the organization."}
          </p>
        </div>

        {canEdit && !showForm && (
          <button className="business-button" type="button" onClick={startCreate}>
            Add Client
          </button>
        )}
      </header>

      <BusinessAlert feedback={feedback} />

      {canEdit && showForm && (
        <div className="business-panel">
          <div className="business-panel-header">
            <h2>{editingId ? "Update Client" : "Create Client"}</h2>
            <button
              className="business-button business-button--secondary"
              type="button"
              onClick={resetForm}
              disabled={saving}
            >
              Cancel
            </button>
          </div>

          <form className="business-form" onSubmit={handleSubmit}>
            <div className="business-form-grid">
              <div className="business-field">
                <label htmlFor="clientName">Client name</label>
                <input
                  id="clientName"
                  name="clientName"
                  value={formData.clientName}
                  onChange={handleChange}
                  maxLength="100"
                  required
                />
              </div>

              <div className="business-field">
                <label htmlFor="companyName">Company name</label>
                <input
                  id="companyName"
                  name="companyName"
                  value={formData.companyName}
                  onChange={handleChange}
                  maxLength="100"
                />
              </div>

              <div className="business-field">
                <label htmlFor="clientEmail">Email</label>
                <input
                  id="clientEmail"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  maxLength="100"
                />
              </div>

              <div className="business-field">
                <label htmlFor="clientContact">Contact</label>
                <input
                  id="clientContact"
                  name="contact"
                  value={formData.contact}
                  onChange={handleChange}
                  maxLength="15"
                />
              </div>

              <div className="business-field business-field--full">
                <label htmlFor="clientAddress">Address</label>
                <textarea
                  id="clientAddress"
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  maxLength="255"
                />
              </div>
            </div>

            <div className="business-form-actions">
              <button className="business-button" type="submit" disabled={saving}>
                {saving
                  ? "Saving..."
                  : editingId
                    ? "Update Client"
                    : "Create Client"}
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="business-panel">
        <div className="business-panel-header">
          <h2>Client Directory</h2>
          <span className="business-count">{clients.length} clients</span>
        </div>

        {loading ? (
          <BusinessLoading message="Loading clients..." />
        ) : clients.length === 0 ? (
          <BusinessEmpty
            title="No clients found"
            message={canEdit ? "Use Add Client to create the first client." : "HR has not created any clients yet."}
          />
        ) : (
          <div className="business-table-wrap">
            <table className="business-table">
              <thead>
                <tr>
                  <th>Client</th>
                  <th>Contact</th>
                  <th>Address</th>
                  <th>Created</th>
                  {canEdit && <th>Action</th>}
                </tr>
              </thead>
              <tbody>
                {clients.map((client) => (
                  <tr key={client.clientId}>
                    <td>
                      <span className="business-primary-text">{client.clientName}</span>
                      <span className="business-secondary-text">
                        {client.companyName || "No company provided"}
                      </span>
                    </td>
                    <td>
                      {client.email || "—"}
                      <span className="business-secondary-text">
                        {client.contact || "No contact number"}
                      </span>
                    </td>
                    <td>{client.address || "—"}</td>
                    <td>
                      {client.createdAt
                        ? new Date(client.createdAt).toLocaleDateString()
                        : "—"}
                    </td>
                    {canEdit && (
                      <td>
                        <button
                          className="business-button business-button--secondary"
                          type="button"
                          onClick={() => startEdit(client)}
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
