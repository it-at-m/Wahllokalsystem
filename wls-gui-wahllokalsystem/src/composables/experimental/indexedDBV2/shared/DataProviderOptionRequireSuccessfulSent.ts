export interface DataProviderOptionRequireSuccessfulSent {
  requireSuccessfulSent: boolean;
}

export function getDefaultDataProviderOptionRequireSuccessfulSent(): DataProviderOptionRequireSuccessfulSent {
  return {
    requireSuccessfulSent: false,
  };
}
