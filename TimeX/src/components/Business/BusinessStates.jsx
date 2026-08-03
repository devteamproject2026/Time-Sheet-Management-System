export function BusinessAlert({ feedback }) {
  if (!feedback?.message) return null;

  return (
    <div
      className={`business-alert business-alert--${feedback.type || "info"}`}
      role="alert"
      aria-live="polite"
    >
      {feedback.message}
    </div>
  );
}

export function BusinessLoading({ message = "Loading data..." }) {
  return (
    <div className="business-state" role="status">
      <div className="spinner-border text-success" aria-hidden="true"></div>
      <p>{message}</p>
    </div>
  );
}

export function BusinessEmpty({ title, message }) {
  return (
    <div className="business-state business-state--empty">
      <strong>{title}</strong>
      <p>{message}</p>
    </div>
  );
}

export function StatusBadge({ status }) {
  const className = String(status || "unknown").toLowerCase().replace("_", "-");

  return (
    <span className={`business-status business-status--${className}`}>
      {String(status || "Unknown").replace("_", " ")}
    </span>
  );
}
