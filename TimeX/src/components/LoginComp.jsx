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

    fetch("http://localhost:8081/api/auth/login", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username,
        password,
      }),
    })
      .then(async (resp) => {
        if (resp.ok) {
          return await resp.json();
        }

        const error = await resp.json();

        setMsg(error.message || "Invalid Username or Password");
        return null;
      })
      .then((data) => {
        if (!data) return;

        dispatch(
          login({
            user: {
              userId: data.userId,
              username: data.username,
              role: data.role,
            },
            token: data.token,
          })
        );

        switch (data.role) {
          case "ADMIN":
            navigate("/admin");
            break;

          case "HR_HEAD":
            navigate("/hr");
            break;

          case "MANAGER":
            navigate("/manager");
            break;

          case "EMPLOYEE":
            navigate("/employee");
            break;

          default:
            navigate("/");
        }
      })
      .catch((err) => {
        console.error(err);
        setMsg("Something went wrong");
      });
  };

  return (
    <main className="login-page">
      <section className="login-visual" aria-hidden="true">
        <div className="brand-mark">WP</div>

        <div>
          <p className="login-kicker">WorkPuls</p>

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
                value={username}
                placeholder="Enter username"
                autoComplete="username"
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="password">Password</label>

              <input
                id="password"
                type="password"
                value={password}
                placeholder="Enter password"
                autoComplete="current-password"
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button className="login-button" type="submit">
              Sign In
            </button>

          </form>

          {msg && <p className="login-message">{msg}</p>}

        </div>
      </section>
    </main>
  );
}