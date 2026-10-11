import type { StimmzettelRepoKey } from "@/composables/experimental/indexedDBV2/repos/stimmzettel/stimmzettelRepoKey.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { useAbstractRepo } from "@/composables/experimental/indexedDBV2/repos/abstractRepo.ts";
import { useStimmzettelRepoKeyProvider } from "@/composables/experimental/indexedDBV2/repos/stimmzettel/StimmzettelRepoKeyProvider.ts";

const stimmzettelRepoKeyProvider = useStimmzettelRepoKeyProvider();

export function useStimmzettelRepo(wahlID: string, wahlbezirkID: string) {
  const storeName = `stimmzettel_${wahlID}_${wahlbezirkID}`;
  const repo = useAbstractRepo<StimmzettelRepoKey, PersistedStimmzettel>(
    storeName,
    { keyProvider: stimmzettelRepoKeyProvider }
  );

  return {
    ...repo,
  };
}
