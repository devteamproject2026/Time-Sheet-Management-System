import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ChangePassword.css";

export default function ChangePassword() {

  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });

    setError("");
    setMessage("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);
    setError("");
    setMessage("");

    try {
      const response = await fetch(
        "http://localhost:8081/api/auth/change-password",
        {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        }
      );

      const data = await response.text();

      if (response.ok) {
        setMessage(data);

        setFormData({
          oldPassword: "",
          newPassword: "",
          confirmPassword: "",
        });

        setTimeout(() => {
          navigate(-1);
        }, 1500);
      } else {
        setError(data);
      }
    } catch (err) {
      console.error(err);
      setError("Something went wrong. Please try again.");
    }

    setLoading(false);
  };

  return (
    <div className="change-password-container">

      <div className="change-password-card">

        <h2>Change Password</h2>

        <p className="change-password-subtitle">
          Update your account password securely.
        </p>

        <form onSubmit={handleSubmit}>

          <div className="form-group">
            <label>Current Password</label>

            <input
              type="password"
              name="oldPassword"
              value={formData.oldPassword}
              onChange={handleChange}
              placeholder="Enter current password"
              required
            />
          </div>

          <div className="form-group">
            <label>New Password</label>

            <input
              type="password"
              name="newPassword"
              value={formData.newPassword}
              onChange={handleChange}
              placeholder="Enter new password"
              required
            />
          </div>

          <div className="form-group">
            <label>Confirm Password</label>

            <input
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Confirm new password"
              required
            />
          </div>

          {message && (
            <div className="success-message">
              {message}
            </div>
          )}

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <button
            type="submit"
            className="change-password-btn"
            disabled={loading}
          >
            {loading ? "Updating..." : "Change Password"}
          </button>

        </form>

      </div>

    </div>
  );
}