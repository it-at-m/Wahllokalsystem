import { ERROR_CATEGORY } from "@/api/wlsErrorCategories";

export interface WLSError extends Error {
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

/**
 * Generates a WLSError instance from a JSON object.
 * @param content - The JSON-object with all relevant information
 * @returns the generated WLSError object from JSON data
 */
export function generateWlsExceptionFromJson(content: any): WLSError {
  if (!content?.message) {
    throw new Error("Invalid content: message is required");
  }
  const error = new Error(content.message) as WLSError;
  error.name = "WLS Exception";
  error.category = content.category;
  error.code = content.code;
  error.service = content.service;
  return error;
}

/**
 * Type guard to check if an object is a WLSException
 * @param obj - The object to check
 * @returns True if the object has all required WLSException properties with correct types
 */
export function isWLSException(obj: any): obj is WLSError {
  return (
    obj &&
    Object.values(ERROR_CATEGORY).includes(obj.category) &&
    typeof obj.code === "string" &&
    typeof obj.message === "string" &&
    typeof obj.service === "string"
  );
}
