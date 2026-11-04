import type { IndexedDBUrlHandler } from "@/types/indexDB/IndexedDBUrlHandler.ts";

import { useUrlTools } from "@/composables/common/urlTools.ts";

const { getPathSegments } = useUrlTools();

export function useBusinessActionsIndexedDBUrlHandler(): IndexedDBUrlHandler {
  const REGEX_HYPHEN = /-/g;
  const REGEX_CAMEL_CASE = /([a-z])([A-Z])/g;
  const REGEX_FIRST_CHAR = /\b\w/g;

  function canHandle(url: URL): boolean {
    return getPathSegments(url).includes("businessActions");
  }

  function getLabel(url: URL): string {
    const pathSegments = getPathSegments(url);
    const businessActionsIndex = pathSegments.indexOf("businessActions");
    const labelFromUrl = pathSegments[businessActionsIndex + 1];

    return labelFromUrl
      .replace(REGEX_HYPHEN, " ") // replaces "-" with " "
      .replace(REGEX_CAMEL_CASE, "$1 $2") // splits "camelCase" to "camel Case"
      .replace(REGEX_FIRST_CHAR, (character) => character.toUpperCase()); // replaces lowercase "camel" with uppercase "Camel"
  }

  return {
    canHandle,
    getLabel,
  };
}
