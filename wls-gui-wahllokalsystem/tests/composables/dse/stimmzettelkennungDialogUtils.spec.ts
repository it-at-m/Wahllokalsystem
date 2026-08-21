import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelkennungDialogUtils } from "@/composables/dse/stimmzettelkennungDialogUtils.ts";

const { preparePersistedStimmzettel } = useStimmzettelTestDataFactory();

describe("stimmzettelkennungDialogUtils.ts", () => {
  const { getNextStimmzettelNumber } = useStimmzettelkennungDialogUtils();

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getNextStimmzettelNumber", () => {
    it("should_returnNextNumber_when_oneStimmzettelExists", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .stimmzettelkennung(3)
        .build();

      const result = getNextStimmzettelNumber([stimmzettel]);

      expect(result).toStrictEqual(4);
    });

    it("should_returnNextNumber_when_multipleStimmzettelExists", () => {
      const stimmzettel1 = preparePersistedStimmzettel()
        .stimmzettelkennung(5)
        .build();
      const stimmzettel2 = preparePersistedStimmzettel()
        .stimmzettelkennung(3)
        .build();
      const stimmzettel3 = preparePersistedStimmzettel()
        .stimmzettelkennung(24)
        .build();

      const result = getNextStimmzettelNumber([
        stimmzettel1,
        stimmzettel2,
        stimmzettel3,
      ]);

      expect(result).toStrictEqual(25);
    });

    it("should_returnNextNumber_when_noStimmzettelExists", () => {
      const result = getNextStimmzettelNumber([]);
      expect(result).toStrictEqual(1);
    });
  });
});
