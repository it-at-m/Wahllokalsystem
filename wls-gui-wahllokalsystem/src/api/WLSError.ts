import { ERROR_CATEGORY } from "@/api/wlsErrorCategories";

export interface WLSError extends Error {
  readonly category?: ERROR_CATEGORY;
  readonly code?: string;
  readonly service?: string;
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
  return {
    name: "Default WlsError",
    message: message,
    category: category,
    code: code,
    service: service,
  };
}

/**
 * Generates a WLSError instance from a JSON object.
 * @param content - The JSON-object with all relevant information
 * @returns the generated WLSError object from JSON data
 */
export function generateWlsExceptionFromJson(content: any): WLSError {
  return {
    name: "WLS Exception",
    message: content.message,
    category: content.category,
    code: content.code,
    service: content.service,
  };
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
