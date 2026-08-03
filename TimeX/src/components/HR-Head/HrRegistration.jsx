import { useState } from "react";
import { NavLink } from "react-router-dom";
import { AUTH_API_URL } from "../../config/api";
import { readApiError } from "../../utils/apiError";
import "./HrRegistration.css";

export default function HrRegistration() {

  const [formData, setFormData] = useState({
    username: "",
    password: "",
    firstName: "",
    lastName: "",
    email: "",
    contact: "",
    joiningDate: ""
  });

  const [msg, setMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);
    setMsg("");

    try {
      const response = await fetch(`${AUTH_API_URL}/register-hr`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(formData)
      });

      if (!response.ok) {
        setMsg(
          await readApiError(response, "Unable to submit the HR request.")
        );
        return;
      }

      setMsg((await response.text()) || "HR request submitted successfully.");
    } catch (err) {
      console.error(err);
      setMsg("Cannot connect to the Auth Service. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="hr-register-page">

      <section className="register-info">

        <NavLink className="register-brand" to="/">
          <span>WP</span>

          <div>
            <strong>WorkPuls</strong>
            <small>Timesheet Management</small>
          </div>
        </NavLink>

        <div className="register-copy">
          <p className="register-kicker">HR Access Request</p>

          <h1>Register your HR account for approval.</h1>

          <p>
            Submit your details to request HR Head access. After admin approval,
            you can create managers, employees and manage organization workflows.
          </p>
        </div>

        <div className="register-steps">

          <div>
            <strong>1</strong>
            <span>Submit Request</span>
          </div>

          <div>
            <strong>2</strong>
            <span>Admin Review</span>
          </div>

          <div>
            <strong>3</strong>
            <span>Start Managing Users</span>
          </div>

        </div>

      </section>

      <section className="register-panel">

        <div className="register-card">

          <div className="register-card-top">

            <NavLink className="back-home-link" to="/">
              Back to Home
            </NavLink>

            <NavLink className="login-link" to="/login">
              Login
            </NavLink>

          </div>

          <div className="register-heading">
            <p>Create Request</p>
            <h2>HR Registration</h2>
          </div>

          <form className="register-form" onSubmit={handleSubmit}>

            <div className="form-row">

              <div className="form-field">
                <label>First Name</label>

                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-field">
                <label>Last Name</label>

                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  required
                />
              </div>

            </div>

            <div className="form-field">
              <label>Username</label>

              <input
                type="text"
                name="username"
                value={formData.username}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label>Password</label>

              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label>Email</label>

              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label>Contact</label>

              <input
                type="text"
                name="contact"
                value={formData.contact}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label>Joining Date</label>

              <input
                type="date"
                name="joiningDate"
                value={formData.joiningDate}
                onChange={handleChange}
                required
              />
            </div>

            <button className="register-button" type="submit" disabled={loading}>
              {loading ? "Submitting..." : "Submit Request"}
            </button>

          </form>

          {msg && (
            <p className="register-message">
              {msg}
            </p>
          )}

        </div>

      </section>

    </main>
  );
}
