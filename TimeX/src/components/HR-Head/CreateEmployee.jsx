
import { useState } from "react";
import "./HrUserForm.css";

export default function CreateEmployee() {
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

    fetch("http://localhost:9000/create-employee", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    })
      .then((resp) => {
        if (resp.status === 201) {
          setMsg("Employee Created Successfully");
        } else {
          setMsg("Employee Creation Failed");
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
            Add an employee account so they can access assigned work and submit
            weekly timesheets.
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
              <label htmlFor="employee-fname">First name</label>
              <input
                id="employee-fname"
                name="fname"
                value={formData.fname}
                placeholder="Enter first name"
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="employee-lname">Last name</label>
              <input
                id="employee-lname"
                name="lname"
                value={formData.lname}
                placeholder="Enter last name"
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="employee-username">Username</label>
              <input
                id="employee-username"
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
              <label htmlFor="employee-email">Email</label>
              <input
                id="employee-email"
                name="email"
                type="email"
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
            Create Employee
          </button>

          {msg && <p className="hr-user-message">{msg}</p>}
        </form>

        <aside className="hr-user-note">
          <p className="hr-user-kicker">Role Access</p>
          <h2>Employee account</h2>
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
