export default class WLSException {
  constructor(
    public readonly category: string,
    public readonly code: string,
    public readonly message: string,
    public readonly service: string
  ) {}
}

export function isWLSException(obj: any): obj is WLSException {
  return (
    obj &&
    typeof obj.category === "string" &&
    typeof obj.code === "string" &&
    typeof obj.message === "string" &&
    typeof obj.service === "string"
  );
}
