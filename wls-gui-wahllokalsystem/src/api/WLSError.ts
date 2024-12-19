import { ERROR_CATEGORY } from "@/api/wlsErrorCategories";

export class WLSError extends Error {
  category?: ERROR_CATEGORY;
  code?: string;
  service?: string;
}

/**
 * Creates a default WLSError instance with optional parameters.
 * @param options - The options for creating the WLSError
 * @returns the generated WLSError object with provided or default data
 */
export function createDefaultWlsError({
  message = "Ein unbekannter Fehler ist aufgetreten",
  category = ERROR_CATEGORY.TECHNICAL,
  code = "undefined",
  service = "undefined",
}: {
  message?: string;
  category?: ERROR_CATEGORY;
  code?: string;
  service?: string;
}): WLSError {
  const error = new Error(message) as WLSError;
  error.name = "Default WlsError";
  error.category = category;
  error.code = code;
  error.service = service;
  return error;
}
