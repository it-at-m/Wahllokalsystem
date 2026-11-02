import { describe, expect, it } from "vitest";

import { useStimmzettelerfassungIndexedDBUrlHandler } from "@/composables/indexDB/stimmzettelerfassungIndexedDBUrlHandler.ts";

describe("stimmzettelerfassungIndexedDBUrlHandler.ts", () => {
  const unitUnderTest = useStimmzettelerfassungIndexedDBUrlHandler();

  describe("canHandle", () => {
    it("should_returnTrue_when_urlContainsStimmzettelerfassung", () => {
      expect(
        unitUnderTest.canHandle(
          new URL(
            "https://example.test/cache/stimmzettelerfassung/election/identifier"
          )
        )
      ).toStrictEqual(true);
    });

    it("should_returnTrue_when_urlContainsStimmzettelerfassungsWorkflow", () => {
      expect(
        unitUnderTest.canHandle(
          new URL(
            "https://example.test/cache/stimmzettelerfassungsWorkflow/election/identifier"
          )
        )
      ).toStrictEqual(true);
    });

    it("should_returnFalse_when_urlDoesNotContainStimmzettelerfassung", () => {
      expect(
        unitUnderTest.canHandle(
          new URL(
            "https://example.test/cache/businessActions/assignTeam/identifier"
          )
        )
      ).toStrictEqual(false);
    });
  });

  describe("getLabel", () => {
    it("should_returnStimmzettelkennung_when_urlContainsStimmzettelkennung", () => {
      expect(
        unitUnderTest.getLabel(
          new URL(
            "https://example.test/cache/stimmzettelerfassung/election/identifier/team/identifier/stimmzettel/4"
          )
        )
      ).toStrictEqual("Stimmzettel 4");
    });

    it("should_returnGenericLabel_when_urlDoesNotContainStimmzettelkennung", () => {
      expect(
        unitUnderTest.getLabel(
          new URL(
            "https://example.test/cache/stimmzettelerfassung/election/identifier/team/identifier/status"
          )
        )
      ).toStrictEqual("Stimmzettelerfassung oder Bearbeitungsstatus");
    });
  });
});
