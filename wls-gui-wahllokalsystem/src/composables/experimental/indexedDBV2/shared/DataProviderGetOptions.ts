//TODO welche Options sind noch denkbar? clearOnFailure; clearBeforeRefresh, ..., defaultWhenFailed
export interface DataProviderGetOptions {
  forceRefresh: boolean;
}

export function getDefaultDataProviderGetOptions(): DataProviderGetOptions {
  return {
    forceRefresh: false,
  };
}
