import { useState } from "react";
import "./HrUserForm.css";

export default function CreateEmployee() {

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

    fetch("http://localhost:8081/api/auth/register-employee", {
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

          <h1>Create Employee</h1>

          <p>
            Add an employee account so they can access assigned work and
            submit weekly timesheets.
          </p>
        </div>
      </div>

      <div className="hr-user-layout">

        <form className="hr-user-form-card" onSubmit={handleSubmit}>

          <div className="form-card-heading">
            <p>Employee Details</p>
            <h2>Account Information</h2>
          </div>

          <div className="form-grid">

            <div className="form-field">
              <label htmlFor="employee-firstName">First Name</label>

              <input
                id="employee-firstName"
                type="text"
                name="firstName"
                value={formData.firstName}
                placeholder="Enter first name"
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="employee-lastName">Last Name</label>

              <input
                id="employee-lastName"
                type="text"
                name="lastName"
                value={formData.lastName}
                placeholder="Enter last name"
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="employee-username">Username</label>

              <input
                id="employee-username"
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
              <label htmlFor="employee-password">Password</label>

              <input
                id="employee-password"
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
              <label htmlFor="employee-email">Email</label>

              <input
                id="employee-email"
                type="email"
                name="email"
                value={formData.email}
                placeholder="employee@company.com"
                onChange={handleChange}
                autoComplete="email"
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="employee-contact">Contact</label>

              <input
                id="employee-contact"
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
              <label htmlFor="employee-joiningDate">Joining Date</label>

              <input
                id="employee-joiningDate"
                type="date"
                name="joiningDate"
                value={formData.joiningDate}
                onChange={handleChange}
                required
              />
            </div>

          </div>

          <button className="hr-user-submit" type="submit">
            Create Employee
          </button>

          {msg && (
            <p className="hr-user-message">
              {msg}
            </p>
          )}

        </form>

        <aside className="hr-user-note">

          <p className="hr-user-kicker">Role Access</p>

          <h2>Employee Account</h2>

          <p>
            Employees can view assigned projects and tasks, then submit weekly
            timesheets for manager approval.
          </p>

          <ul>
            <li>Status is set to approved automatically.</li>
            <li>The account can log in immediately after creation.</li>
            <li>Manager/project mapping can be added in the next module.</li>
          </ul>

        </aside>

      </div>

    </section>
  );
}