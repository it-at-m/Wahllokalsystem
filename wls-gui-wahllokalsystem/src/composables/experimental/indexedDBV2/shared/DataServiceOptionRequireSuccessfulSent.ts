export interface DataServiceOptionRequireSuccessfulSent {
  requireSuccessfulSent: boolean;
}

export function getDefaultDataServiceOptionRequireSuccessfulSent(): DataServiceOptionRequireSuccessfulSent {
  return {
    requireSuccessfulSent: false,
  };
}
