import { useEffect, useState } from "react";
import { AUTH_API_URL } from "../../config/api";
import { readApiError } from "../../utils/apiError";

export default function PendingHrRequests() {
  const [hrs, setHrs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processingId, setProcessingId] = useState(null);
  const [feedback, setFeedback] = useState({ type: "", message: "" });
  const [requestVersion, setRequestVersion] = useState(0);

  // The request function lives inside the effect, so it cannot be referenced
  // before declaration. State changes occur after the asynchronous request.
  useEffect(() => {
    let cancelled = false;

    const loadPendingHRs = async () => {
      try {
        const response = await fetch(`${AUTH_API_URL}/pending-hr`, {
          method: "GET",
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error(
            await readApiError(response, "Unable to load pending HR requests.")
          );
        }

        const data = await response.json();
        if (!cancelled) setHrs(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error(err);

        if (!cancelled) {
          setHrs([]);
          setFeedback({
            type: "error",
            message:
              err.message ||
              "Cannot connect to the Auth Service. Please try again.",
          });
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    loadPendingHRs();

    return () => {
      cancelled = true;
    };
  }, [requestVersion]);

  // A retry starts from a user action, so it is the correct place to update
  // loading state before making the next request.
  const retryPendingHRs = () => {
    setLoading(true);
    setFeedback({ type: "", message: "" });
    setRequestVersion((currentVersion) => currentVersion + 1);
  };

  // Approval and rejection use the same backend workflow. The action value
  // chooses the correct endpoint while keeping loading and error handling equal.
  const updateHrStatus = async (id, action) => {
    const actionLabel = action === "approve" ? "approve" : "reject";
    if (!window.confirm(`${actionLabel} this HR request?`)) return;

    setProcessingId(id);
    setFeedback({ type: "", message: "" });

    try {
      const response = await fetch(
        `${AUTH_API_URL}/${actionLabel}-hr/${id}`,
        {
          method: "PUT",
          credentials: "include",
        }
      );

      if (!response.ok) {
        throw new Error(
          await readApiError(
            response,
            `Unable to ${actionLabel} the HR request.`
          )
        );
      }

      setHrs((previousHrs) =>
        previousHrs.filter((hr) => hr.userId !== id)
      );
      setFeedback({
        type: "success",
        message: `HR ${
          actionLabel === "approve" ? "approved" : "rejected"
        } successfully.`,
      });
    } catch (err) {
      console.error(err);
      setFeedback({
        type: "error",
        message:
          err.message || `Unable to ${actionLabel} the HR request.`,
      });
    } finally {
      setProcessingId(null);
    }
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
          {feedback.message && (
            <div
              className={`alert ${
                feedback.type === "error" ? "alert-danger" : "alert-success"
              }`}
              role="alert"
              aria-live="polite"
            >
              {feedback.message}
            </div>
          )}

          {loading ? (
            <div className="text-center my-5">
              <div
                className="spinner-border text-primary"
                role="status"
                aria-label="Loading pending HR requests"
              ></div>

              <p className="mt-3">Loading pending HR requests...</p>
            </div>
          ) : hrs.length === 0 ? (
            <div className="text-center">
              <div className="alert alert-info">No pending HR requests.</div>

              {feedback.type === "error" && (
                <button
                  className="btn btn-outline-primary"
                  type="button"
                  onClick={retryPendingHRs}
                >
                  Try Again
                </button>
              )}
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
                        type="button"
                        onClick={() => updateHrStatus(hr.userId, "approve")}
                        disabled={processingId !== null}
                      >
                        {processingId === hr.userId
                          ? "Processing..."
                          : "Approve"}
                      </button>
                    </td>

                    <td className="text-center">
                      <button
                        className="btn btn-danger btn-sm"
                        type="button"
                        onClick={() => updateHrStatus(hr.userId, "reject")}
                        disabled={processingId !== null}
                      >
                        {processingId === hr.userId
                          ? "Processing..."
                          : "Reject"}
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
