/**
 * Temporary page used while a module's real frontend is being developed.
 *
 * Keeping this as one reusable component prevents every unfinished sidebar
 * link from showing the generic 404 page. Replace each route's placeholder
 * with its real page when that module is implemented.
 */
export default function ModulePlaceholder({ title, description }) {
  return (
    <section className="container py-4">
      <div className="card border-0 shadow-sm">
        <div className="card-body p-4">
          <p className="text-uppercase text-primary fw-semibold mb-2">
            Frontend route ready
          </p>

          <h1 className="h3 mb-3">{title}</h1>

          <p className="text-secondary mb-0">
            {description}
          </p>
        </div>
      </div>
    </section>
  );
}
