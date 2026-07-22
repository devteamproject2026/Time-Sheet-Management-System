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

    fetch("http://localhost:9000/pending-hr")
      .then((resp) => resp.json())
      .then((data) => {
        setHrs(data);
        setLoading(false);
      })
      .catch(() => {
        setLoading(false);
        setMessage("Unable to fetch pending requests.");
      });
  };

  const approveHr = (id) => {
    if (!window.confirm("Approve this HR request?")) return;

    fetch(`http://localhost:9000/approve-hr/${id}`, {
      method: "PUT",
    }).then(() => {
      setHrs((prev) => prev.filter((hr) => hr.user_id !== id));
      setMessage("HR approved successfully.");
    });
  };

  const rejectHr = (id) => {
    if (!window.confirm("Reject this HR request?")) return;

    fetch(`http://localhost:9000/reject-hr/${id}`, {
      method: "PUT",
    }).then(() => {
      setHrs((prev) => prev.filter((hr) => hr.user_id !== id));
      setMessage("HR rejected successfully.");
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
            <div className="alert alert-success">
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
                  <tr key={hr.user_id}>

                    <td>{hr.user_id}</td>

                    <td>{hr.username}</td>

                    <td>{hr.email}</td>

                    <td className="text-center">

                      <button
                        className="btn btn-success btn-sm"
                        onClick={() => approveHr(hr.user_id)}
                      >
                        ✔ Approve
                      </button>

                    </td>

                    <td className="text-center">

                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => rejectHr(hr.user_id)}
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