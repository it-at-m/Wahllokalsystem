import type { IndexedDBUrlHandler } from "@/types/indexDB/IndexedDBUrlHandler.ts";

import { useUrlTools } from "@/composables/common/urlTools.ts";

const { getPathSegments } = useUrlTools();

export function useStimmzettelerfassungIndexedDBUrlHandler(): IndexedDBUrlHandler {
  function canHandle(url: URL): boolean {
    return getPathSegments(url).some((segment) =>
      segment.startsWith("stimmzettelerfassung")
    );
  }

  function getLabel(url: URL): string {
    const pathSegments = getPathSegments(url);
    const stimmzettelIndex = pathSegments.indexOf("stimmzettel");
    const stimmzettelkennung = pathSegments[stimmzettelIndex + 1];

    return stimmzettelIndex !== -1 && stimmzettelkennung
      ? `Stimmzettel ${stimmzettelkennung}`
      : "Stimmzettelerfassung oder Bearbeitungsstatus";
  }

  return {
    canHandle,
    getLabel,
  };
}
