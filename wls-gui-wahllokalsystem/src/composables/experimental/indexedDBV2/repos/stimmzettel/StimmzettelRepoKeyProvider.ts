import type { StimmzettelRepoKey } from "@/composables/experimental/indexedDBV2/repos/stimmzettel/stimmzettelRepoKey.ts";
import type { KeyProvider } from "@/composables/experimental/indexedDBV2/shared/keyProvider.ts";

export function useStimmzettelRepoKeyProvider(): KeyProvider<StimmzettelRepoKey> {
  function createLocalforageKey(key: StimmzettelRepoKey): string {
    const normalizedKey: StimmzettelRepoKey = {
      teamID: key.teamID,
      stimmzettelkennung: key.stimmzettelkennung,
    };
    return JSON.stringify(normalizedKey);
  }

  function readLocalforageKey(key: string): StimmzettelRepoKey {
    return JSON.parse(key) as StimmzettelRepoKey;
  }

  return {
    createLocalforageKey,
    readLocalforageKey,
  };
}
