import { useState } from "react";
import { login } from "../redux/authslice";
import { useDispatch } from "react-redux";
import { NavLink, useNavigate } from "react-router-dom";
import "./LoginComp.css";

export default function LoginComp() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState("");

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handelSubmit = (e) => {
    e.preventDefault();

    const reqoptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username,
        password,
      }),
    };

    fetch("http://localhost:9000/login", reqoptions)
      .then((resp) => {
        if (resp.status === 200) {
          return resp.json();
        } else if (resp.status === 404) {
          setMsg("Invalid Username or Password");
          return null;
        }
      })
      .then((data) => {
        if (!data) return;

        dispatch(
          login({
            user: data.user,
            token: data.token,
          })
        );

        const role = data.user.role;

        if (role === "ADMIN") {
          navigate("/admin");
        } 
        else if (role === "HR_HEAD") {
          navigate("/hr");
        } 
        else if (role === "MANAGER") {
          navigate("/manager");
        } 
        else if (role === "EMPLOYEE") {
          navigate("/employee");
        } 
        else {
          navigate("/");
        }
      })
      .catch((err) => {
        console.log(err);
        setMsg("Something went wrong");
      });
  };

  return (
    <main className="login-page">
      <section className="login-visual" aria-hidden="true">
        <div className="brand-mark">WP</div>
        <div>
          <p className="login-kicker">WorkPulse</p>
          <h1>Time Sheet Management System</h1>
          <p>
            Track work hours, review requests, and keep team activity organized
            from one role-based workspace.
          </p>
        </div>
      </section>

      <section className="login-panel">
        <div className="login-card">
          <NavLink className="back-home-link" to="/">
            Back to home
          </NavLink>

          <div className="login-heading">
            <p>Welcome back</p>
            <h2>Sign in to your account</h2>
          </div>

          <form className="login-form" onSubmit={handelSubmit}>
            <div className="form-field">
              <label htmlFor="username">Username</label>
              <input
                id="username"
                type="text"
                name="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter username"
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
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
                autoComplete="current-password"
                required
              />
            </div>

            <button className="login-button" type="submit">
              Sign in
            </button>
          </form>

          {msg && <p className="login-message">{msg}</p>}
        </div>
      </section>
    </main>
  );
}
