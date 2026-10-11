//TODO welche Options sind noch denkbar? clearOnFailure; clearBeforeRefresh, ..., defaultWhenFailed
export interface DataServiceOptionForceRefresh {
  forceRefresh: boolean;
}

export function getDefaultDataServiceOptionForceRefresh(): DataServiceOptionForceRefresh {
  return {
    forceRefresh: false,
  };
}
