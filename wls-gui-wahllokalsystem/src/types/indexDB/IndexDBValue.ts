export interface IndexDBValue {
  data: ArrayBuffer | string | null;
  contentType: string | null;
  httpStatus?: number;
  dirty?: boolean;
  timestamp?: number;
}
