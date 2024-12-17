export default class WLSError extends Error {
  readonly category: string;
  readonly code: string;
  readonly service: string;

  constructor({
    message = "Ein unbekannter Fehler ist aufgetreten, bitte den Administrator informieren.",
    category = "T",
    code = "undefined",
    service = "undefined",
  }: {
    message?: string;
    category?: string;
    code?: string;
    service?: string;
  }) {
    // Passes the remaining parameters (including vendor-specific parameters) to the error constructor
    super(message);
    // Retains the correct stack trace for the point at which the error was triggered
    this.stack = new Error().stack;

    // User-defined information
    this.message = message;
    this.category = category;
    this.code = code;
    this.service = service;
  }

  /**
   * Type guard to check if an object is a WLSException
   * @param obj - The object to check
   * @returns True if the object has all required WLSEexception properties with correct types
   */
  static isWLSException(obj: any): obj is WLSError {
    return (
      obj &&
      typeof obj.category === "string" &&
      typeof obj.code === "string" &&
      typeof obj.message === "string" &&
      typeof obj.service === "string"
    );
  }
}
