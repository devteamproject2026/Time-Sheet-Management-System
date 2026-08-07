import { useState } from "react";
import { login } from "../redux/authslice";
import { useDispatch } from "react-redux";
import { NavLink, useNavigate } from "react-router-dom";
import { AUTH_API_URL } from "../config/api";
import { readApiError } from "../utils/apiError";
import "./LoginComp.css";

export default function LoginComp() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);
    setMsg("");

    try {
      const response = await fetch(`${AUTH_API_URL}/login`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        setMsg(
          await readApiError(response, "Invalid username or password.")
        );
        return;
      }

      const data = await response.json();

      dispatch(
        login({
          user: {
            userId: data.userId,
            username: data.username,
            role: data.role,
          },
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
    } catch (err) {
      console.error(err);
      setMsg("Cannot connect to the Auth Service. Please try again.");
    } finally {
      setLoading(false);
    }
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

          <form className="login-form" onSubmit={handleSubmit}>

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

            <button className="login-button" type="submit" disabled={loading}>
              {loading ? "Signing in..." : "Sign In"}
            </button>

          </form>

          {msg && <p className="login-message">{msg}</p>}

        </div>
      </section>
    </main>
  );
}
