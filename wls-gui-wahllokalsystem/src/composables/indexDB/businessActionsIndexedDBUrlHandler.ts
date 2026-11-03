import type { IndexedDBUrlHandler } from "@/types/indexDB/IndexedDBUrlHandler.ts";

import { useUrlTools } from "@/composables/common/urlTools.ts";

const { getPathSegments } = useUrlTools();

export function useBusinessActionsIndexedDBUrlHandler(): IndexedDBUrlHandler {
  function canHandle(url: URL): boolean {
    return getPathSegments(url).includes("businessActions");
  }

  function getLabel(url: URL): string {
    const pathSegments = getPathSegments(url);
    const businessActionsIndex = pathSegments.indexOf("businessActions");
    const labelFromUrl = pathSegments[businessActionsIndex + 1];

    return labelFromUrl
      .replace(/-/g, " ")
      .replace(/([a-z])([A-Z])/g, "$1 $2")
      .replace(/\b\w/g, (character) => character.toUpperCase());
  }

  return {
    canHandle,
    getLabel,
  };
}
