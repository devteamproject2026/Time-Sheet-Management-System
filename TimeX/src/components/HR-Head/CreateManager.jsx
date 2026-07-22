
import { useState } from "react";
import "./HrUserForm.css";

export default function CreateManager() {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    fname: "",
    lname: "",
    email: "",
    contact: "",
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

    fetch("http://localhost:9000/create-manager", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    })
      .then((resp) => {
        if (resp.status === 201) {
          setMsg("Manager Created Successfully");
        } else {
          setMsg("Manager Creation Failed");
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
              <label htmlFor="manager-fname">First name</label>
              <input
                id="manager-fname"
                name="fname"
                value={formData.fname}
                placeholder="Enter first name"
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="manager-lname">Last name</label>
              <input
                id="manager-lname"
                name="lname"
                value={formData.lname}
                placeholder="Enter last name"
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="manager-username">Username</label>
              <input
                id="manager-username"
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
                name="password"
                type="password"
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
                name="email"
                type="email"
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
                name="contact"
                type="tel"
                value={formData.contact}
                placeholder="Enter contact number"
                onChange={handleChange}
                autoComplete="tel"
                required
              />
            </div>
          </div>

          <button className="hr-user-submit" type="submit">
            Create Manager
          </button>

          {msg && <p className="hr-user-message">{msg}</p>}
        </form>

        <aside className="hr-user-note">
          <p className="hr-user-kicker">Role Access</p>
          <h2>Manager account</h2>
          <p>
            Managers can view assigned team members, manage project tasks, and
            approve or reject submitted timesheets.
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
