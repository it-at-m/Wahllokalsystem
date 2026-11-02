import { describe, expect, it } from "vitest";

import { useBusinessActionsIndexedDBUrlHandler } from "@/composables/indexDB/businessActionsIndexedDBUrlHandler.ts";

describe("businessActionsIndexedDBUrlHandler.ts", () => {
  const unitUnderTest = useBusinessActionsIndexedDBUrlHandler();

  describe("canHandle", () => {
    it("should_returnTrue_when_urlContainsBusinessActions", () => {
      expect(
        unitUnderTest.canHandle(
          new URL(
            "https://example.test/cache/businessActions/assignTeam/identifier"
          )
        )
      ).toStrictEqual(true);
    });

    it("should_returnFalse_when_urlDoesNotContainBusinessActions", () => {
      expect(
        unitUnderTest.canHandle(
          new URL(
            "https://example.test/cache/stimmzettelerfassung/election/identifier"
          )
        )
      ).toStrictEqual(false);
    });
  });

  describe("getLabel", () => {
    it("should_returnFormattedAction_when_urlContainsBusinessAction", () => {
      expect(
        unitUnderTest.getLabel(
          new URL(
            "https://example.test/cache/businessActions/sendResults/identifier"
          )
        )
      ).toStrictEqual("Send Results");
    });
  });
});
