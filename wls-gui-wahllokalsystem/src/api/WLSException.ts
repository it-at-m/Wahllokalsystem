import { ERROR_CATEGORY } from "@/api/wlsErrorCategories";

export default class WLSException {
  constructor(
    public readonly category: string,
    public readonly code: string,
    public readonly message: string,
    public readonly service: string
  ) {}
}

/**
 * Type guard to check if an object is a WLSException
 * @param obj - The object to check
 * @returns True if the object has all required WLSException properties with correct types
 */
export function isWLSException(obj: any): obj is WLSException {
  return (
    obj &&
    Object.values(ERROR_CATEGORY).includes(obj.category) &&
    typeof obj.code === "string" &&
    typeof obj.message === "string" &&
    typeof obj.service === "string"
  );
}
