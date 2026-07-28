import { useEffect, useState } from "react";

export default function PendingHrRequests() {
  const [hrs, setHrs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  useEffect(() => {
    loadPendingHRs();
  }, []);

  const loadPendingHRs = () => {
    setLoading(true);

    fetch("http://localhost:8081/api/auth/pending-hr", {
      method: "GET",
      credentials: "include",
    })
      .then(async (resp) => {
        if (!resp.ok) {
          throw new Error("Failed to load pending HR requests.");
        }

        return await resp.json();
      })
      .then((data) => {
        setHrs(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
        setMessage("Unable to fetch pending requests.");
      });
  };

  const approveHr = (id) => {
    if (!window.confirm("Approve this HR request?")) return;

    fetch(`http://localhost:8081/api/auth/approve-hr/${id}`, {
      method: "PUT",
      credentials: "include",
    })
      .then(async (resp) => {
        if (!resp.ok) {
          throw new Error("Unable to approve HR.");
        }

        setHrs((prev) => prev.filter((hr) => hr.userId !== id));
        setMessage("HR approved successfully.");
      })
      .catch((err) => {
        console.error(err);
        setMessage("Unable to approve HR.");
      });
  };

  const rejectHr = (id) => {
    if (!window.confirm("Reject this HR request?")) return;

    fetch(`http://localhost:8081/api/auth/reject-hr/${id}`, {
      method: "PUT",
      credentials: "include",
    })
      .then(async (resp) => {
        if (!resp.ok) {
          throw new Error("Unable to reject HR.");
        }

        setHrs((prev) => prev.filter((hr) => hr.userId !== id));
        setMessage("HR rejected successfully.");
      })
      .catch((err) => {
        console.error(err);
        setMessage("Unable to reject HR.");
      });
  };

  return (
    <div className="container mt-4">
      <div className="card shadow-lg border-0">

        <div className="card-header bg-primary text-white d-flex justify-content-between align-items-center">
          <h3 className="mb-0">Pending HR Requests</h3>

          <span className="badge bg-warning text-dark fs-6">
            {hrs.length} Pending
          </span>
        </div>

        <div className="card-body">

          {message && (
            <div className="alert alert-info">
              {message}
            </div>
          )}

          {loading ? (
            <div className="text-center my-5">
              <div
                className="spinner-border text-primary"
                role="status"
              ></div>

              <p className="mt-3">Loading...</p>
            </div>
          ) : hrs.length === 0 ? (
            <div className="alert alert-info text-center">
              No pending HR requests.
            </div>
          ) : (
            <table className="table table-hover table-bordered align-middle">

              <thead className="table-dark">
                <tr>
                  <th>ID</th>
                  <th>Username</th>
                  <th>Email</th>
                  <th className="text-center">Approve</th>
                  <th className="text-center">Reject</th>
                </tr>
              </thead>

              <tbody>

                {hrs.map((hr) => (
                  <tr key={hr.userId}>

                    <td>{hr.userId}</td>

                    <td>{hr.username}</td>

                    <td>{hr.email}</td>

                    <td className="text-center">
                      <button
                        className="btn btn-success btn-sm"
                        onClick={() => approveHr(hr.userId)}
                      >
                        ✔ Approve
                      </button>
                    </td>

                    <td className="text-center">
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => rejectHr(hr.userId)}
                      >
                        ✖ Reject
                      </button>
                    </td>

                  </tr>
                ))}

              </tbody>

            </table>
          )}

        </div>
      </div>
    </div>
  );
}