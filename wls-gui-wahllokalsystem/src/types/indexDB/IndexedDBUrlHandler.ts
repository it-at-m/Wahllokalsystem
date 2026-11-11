export interface IndexedDBUrlHandler {
  canHandle: (url: URL) => boolean;
  getLabel: (url: URL) => string;
}
