export interface IndexDBValue {
  data: string | null;
  contentType: string | null;
  httpStatus?: number;
  dirty?: boolean;
  timestamp?: number;
}
