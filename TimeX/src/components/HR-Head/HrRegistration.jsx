import { useState } from "react";
import { NavLink } from "react-router-dom";
import "./HrRegistration.css";

export default function HrRegistration() {

    const [formData, setFormData] = useState({
        username: "",
        password: "",
        first_name: "",
        last_name: "",
        email: "",
        contact: ""
    });

    const [msg, setMsg] = useState("");

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = (e) => {

        e.preventDefault();

        const reqOptions = {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData)
        };

        fetch("http://localhost:9000/register-hr", reqOptions)
            .then((resp) => {

                if (resp.status === 201) {
                    setMsg("Registration Request Submitted Successfully");
                } else {
                    setMsg("Registration Failed");
                }

                return resp.text();
            })
            .catch((err) => {
                console.log(err);
                setMsg("Something Went Wrong");
            });
    };

    return (
        <main className="hr-register-page">
            <section className="register-info">
                <NavLink className="register-brand" to="/">
                    <span>WP</span>
                    <div>
                        <strong>WorkPulse</strong>
                        <small>Timesheet Management</small>
                    </div>
                </NavLink>

                <div className="register-copy">
                    <p className="register-kicker">HR Access Request</p>
                    <h1>Register your HR account for approval.</h1>
                    <p>
                        Submit your details to request HR Head access. After admin
                        approval, you can create managers, employees, and manage
                        organization workflows.
                    </p>
                </div>

                <div className="register-steps" aria-label="Registration process">
                    <div>
                        <strong>1</strong>
                        <span>Submit request</span>
                    </div>
                    <div>
                        <strong>2</strong>
                        <span>Admin review</span>
                    </div>
                    <div>
                        <strong>3</strong>
                        <span>Start managing users</span>
                    </div>
                </div>
            </section>

            <section className="register-panel">
                <div className="register-card">
                    <div className="register-card-top">
                        <NavLink className="back-home-link" to="/">
                            Back to home
                        </NavLink>
                        <NavLink className="login-link" to="/login">
                            Login
                        </NavLink>
                    </div>

                    <div className="register-heading">
                        <p>Create request</p>
                        <h2>HR Registration</h2>
                    </div>

                    <form className="register-form" onSubmit={handleSubmit}>
                        <div className="form-row">
                            <div className="form-field">
                                <label htmlFor="fname">First name</label>
                                <input
                                    id="fname"
                                    type="text"
                                    name="fname"
                                    value={formData.fname}
                                    onChange={handleChange}
                                    placeholder="Enter first name"
                                    required
                                />
                            </div>

                            <div className="form-field">
                                <label htmlFor="lname">Last name</label>
                                <input
                                    id="lname"
                                    type="text"
                                    name="lname"
                                    value={formData.lname}
                                    onChange={handleChange}
                                    placeholder="Enter last name"
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-field">
                            <label htmlFor="username">Username</label>
                            <input
                                id="username"
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                placeholder="Choose username"
                                autoComplete="username"
                                required
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="password">Password</label>
                            <input
                                id="password"
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Create password"
                                autoComplete="new-password"
                                required
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="email">Email</label>
                            <input
                                id="email"
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="name@company.com"
                                autoComplete="email"
                                required
                            />
                        </div>

                        <div className="form-field">
                            <label htmlFor="contact">Contact</label>
                            <input
                                id="contact"
                                type="tel"
                                name="contact"
                                value={formData.contact}
                                onChange={handleChange}
                                placeholder="Enter contact number"
                                autoComplete="tel"
                                required
                            />
                        </div>

                        <button className="register-button" type="submit">
                            Submit Request
                        </button>
                    </form>

                    {msg && <p className="register-message">{msg}</p>}
                </div>
            </section>
        </main>
    );
}
