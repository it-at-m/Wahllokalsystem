export interface KeyProvider<KEY_TYPE> {
  createLocalforageKey: (key: KEY_TYPE) => string;
  readLocalforageKey: (key: string) => KEY_TYPE;
}
