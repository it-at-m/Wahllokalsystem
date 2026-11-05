import { describe, expect, it } from "vitest";

import { useUrlTools } from "@/composables/common/urlTools.ts";

describe("urlTools.ts", () => {
  const { getPathSegments } = useUrlTools();

  describe("getPathSegments", () => {
    it("should_returnNonEmptyPathSegments_when_urlContainsPathAndQueryParameters", () => {
      const url = new URL(
        "https://example.test//cache/resource/identifier/?parameter=value"
      );

      expect(getPathSegments(url)).toStrictEqual([
        "cache",
        "resource",
        "identifier",
      ]);
    });
  });
});
