import { createDefaultWlsError } from "@/api/WLSError";

/**
 * Returns a default Config for fetch
 */
export function fetchConfig(): RequestInit {
  return {
    headers: getHeaders(),
    mode: "cors",
    credentials: "same-origin",
    redirect: "manual",
  };
}

/**
 * Returns a default PUT-Config for fetch
 * If available, the version of the entity to be updated is included in this as an "If-Match" header.
 * @param body Optional body to be transferred
 */
// eslint-disable-next-line
export function putConfig(body: any): RequestInit {
  const headers = getHeaders();
  if (body.version) {
    headers.append("If-Match", body.version);
  }
  return {
    method: "PUT",
    body: body ? JSON.stringify(body) : undefined,
    headers,
    mode: "cors",
    credentials: "same-origin",
    redirect: "manual",
  };
}

/**
 * Covers the default handling of a response. This includes:
 *
 * - Error with missing authorizations --> HTTP 403
 * - Reload the app at session timeout --> HTTP 3xx
 * - Default error for all HTTP codes nicht 2xx
 *
 * @param response The response from the fetch command to be checked.
 * @param errorMessage The error message to be displayed for an HTTP code != 2xx.
 */
export function defaultResponseHandler(
  response: Response,
  errorMessage = "Es ist ein unbekannter Fehler aufgetreten."
): void {
  if (!response.ok) {
    if (response.status === 403) {
      throw createDefaultWlsError({
        message:
          "Sie haben nicht die nötigen Rechte um diese Aktion durchzuführen.",
        code: response.status.toString(),
      });
    } else if (response.type === "opaqueredirect") {
      location.reload();
    }
    throw createDefaultWlsError({
      message: errorMessage,
      code: response.status.toString(),
    });
  }
}

/**
 * Default catch handler for all service requests.
 * Currently only throws an WLSError
 * @param error The error object from fetch command
 * @param errorMessage The error message to be included in the WLSError object.
 */
export function defaultCatchHandler(
  error: Error,
  errorMessage = "Es ist ein unbekannter Fehler aufgetreten."
): PromiseLike<never> {
  throw createDefaultWlsError({
    message: errorMessage,
  });
}

/**
 * Builds the headers for the request.
 * @returns {Headers}
 */
function getHeaders(): Headers {
  const headers = new Headers({
    "Content-Type": "application/json",
  });
  const csrfCookie = getXSRFToken();
  if (csrfCookie !== "") {
    headers.append("X-XSRF-TOKEN", csrfCookie);
  }
  return headers;
}

/**
 * Returns the XSRF-TOKEN.
 * @returns {string|string}
 */
function getXSRFToken(): string {
  const help = document.cookie.match(
    "(^|;)\\s*" + "XSRF-TOKEN" + "\\s*=\\s*([^;]+)"
  );
  return (help ? help.pop() : "") as string;
}
