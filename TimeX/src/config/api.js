// API addresses come from Vite environment variables instead of being repeated
// in every component. This lets us change an address in one place.
const removeTrailingSlash = (url) => url?.replace(/\/+$/, "");

export const AUTH_API_URL = removeTrailingSlash(
  import.meta.env.VITE_AUTH_API_URL
);

export const BUSINESS_API_URL = removeTrailingSlash(
  import.meta.env.VITE_BUSINESS_API_URL
);
