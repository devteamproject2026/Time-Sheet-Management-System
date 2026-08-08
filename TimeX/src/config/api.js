// React communicates only with API Gateway. The Gateway then discovers and
// forwards each path to Auth, Business, or Transaction Service through Eureka.
const removeTrailingSlash = (url) => url?.replace(/\/+$/, "");

export const API_GATEWAY_URL = removeTrailingSlash(
  import.meta.env.VITE_API_GATEWAY_URL
);

export const AUTH_API_URL = `${API_GATEWAY_URL}/api/auth`;
export const BUSINESS_API_URL = `${API_GATEWAY_URL}/api/business`;
export const TRANSACTION_API_URL = `${API_GATEWAY_URL}/api/transactions`;
export const AI_API_URL = `${API_GATEWAY_URL}/api/ai`;
