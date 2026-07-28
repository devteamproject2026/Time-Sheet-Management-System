import { useState } from "react";
import "./HrUserForm.css";

export default function CreateManager() {

  const [formData, setFormData] = useState({
    username: "",
    password: "",
    firstName: "",
    lastName: "",
    email: "",
    contact: "",
    joiningDate: "",
  });

  const [msg, setMsg] = useState("");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    fetch("http://localhost:8081/api/auth/register-manager", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    })
      .then(async (resp) => {
        const message = await resp.text();

        if (resp.ok) {
          setMsg(message);

          setFormData({
            username: "",
            password: "",
            firstName: "",
            lastName: "",
            email: "",
            contact: "",
            joiningDate: "",
          });
        } else {
          setMsg(message);
        }
      })
      .catch((err) => {
        console.log(err);
        setMsg("Something Went Wrong");
      });
  };

  return (
    <section className="hr-user-page">

      <div className="hr-user-header">
        <div>
          <p className="hr-user-kicker">HR Setup</p>

          <h1>Create Manager</h1>

          <p>
            Add a manager account so they can handle team tasks and review
            employee timesheets.
          </p>
        </div>
      </div>

      <div className="hr-user-layout">

        <form className="hr-user-form-card" onSubmit={handleSubmit}>

          <div className="form-card-heading">
            <p>Manager Details</p>
            <h2>Account Information</h2>
          </div>

          <div className="form-grid">

            <div className="form-field">
              <label htmlFor="manager-firstName">First Name</label>

              <input
                id="manager-firstName"
                type="text"
                name="firstName"
                value={formData.firstName}
                placeholder="Enter first name"
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="manager-lastName">Last Name</label>

              <input
                id="manager-lastName"
                type="text"
                name="lastName"
                value={formData.lastName}
                placeholder="Enter last name"
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="manager-username">Username</label>

              <input
                id="manager-username"
                type="text"
                name="username"
                value={formData.username}
                placeholder="Choose username"
                onChange={handleChange}
                autoComplete="username"
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="manager-password">Password</label>

              <input
                id="manager-password"
                type="password"
                name="password"
                value={formData.password}
                placeholder="Create password"
                onChange={handleChange}
                autoComplete="new-password"
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="manager-email">Email</label>

              <input
                id="manager-email"
                type="email"
                name="email"
                value={formData.email}
                placeholder="manager@company.com"
                onChange={handleChange}
                autoComplete="email"
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="manager-contact">Contact</label>

              <input
                id="manager-contact"
                type="tel"
                name="contact"
                value={formData.contact}
                placeholder="Enter contact number"
                onChange={handleChange}
                autoComplete="tel"
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="manager-joiningDate">Joining Date</label>

              <input
                id="manager-joiningDate"
                type="date"
                name="joiningDate"
                value={formData.joiningDate}
                onChange={handleChange}
                required
              />
            </div>

          </div>

          <button className="hr-user-submit" type="submit">
            Create Manager
          </button>

          {msg && (
            <p className="hr-user-message">
              {msg}
            </p>
          )}

        </form>

        <aside className="hr-user-note">

          <p className="hr-user-kicker">Role Access</p>

          <h2>Manager Account</h2>

          <p>
            Managers can view assigned team members, manage project tasks,
            and approve or reject submitted timesheets.
          </p>

          <ul>
            <li>Status is set to approved automatically.</li>
            <li>The account can log in immediately after creation.</li>
            <li>Project/team assignment can be added in the next module.</li>
          </ul>

        </aside>

      </div>

    </section>
  );
}