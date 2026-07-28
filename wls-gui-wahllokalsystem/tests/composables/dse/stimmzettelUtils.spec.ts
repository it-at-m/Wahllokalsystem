import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelUtils } from "@/composables/dse/stimmzettelUtils.ts";

const { prepareStimmzettel } = useStimmzettelTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getStimmzettel: vi.fn(),
}));

vi.mock(import("@/composables/dse/stimmzettelService.ts"), () => ({
  useStimmzettelService: () => ({
    getStimmzettel: mockDefinitions.getStimmzettel,
    saveStimmzettel: vi.fn(),
  }),
}));

describe("stimmzettelUtils.ts", () => {
  const { getNextStimmzettelNumber } = useStimmzettelUtils();

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";
  const teamID = "A";

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getNextStimmzettelNumber", () => {
    it("should_returnNextNumber_when_oneStimmzettelExists", async () => {
      const stimmzettel = prepareStimmzettel().stimmzettelkennung(3).build();
      mockDefinitions.getStimmzettel.mockReturnValue([stimmzettel]);

      const result = await getNextStimmzettelNumber(
        wahlID,
        wahlbezirkID,
        teamID
      );

      expect(mockDefinitions.getStimmzettel.mock.calls.length).toStrictEqual(1);
      expect(mockDefinitions.getStimmzettel.mock.calls[0]).toStrictEqual([
        wahlID,
        wahlbezirkID,
        teamID,
      ]);
      expect(result).toStrictEqual(4);
    });

    it("should_returnNextNumber_when_multipleStimmzettelExists", async () => {
      const stimmzettel1 = prepareStimmzettel().stimmzettelkennung(5).build();
      const stimmzettel2 = prepareStimmzettel().stimmzettelkennung(3).build();
      const stimmzettel3 = prepareStimmzettel().stimmzettelkennung(24).build();
      mockDefinitions.getStimmzettel.mockReturnValue([
        stimmzettel1,
        stimmzettel2,
        stimmzettel3,
      ]);

      const result = await getNextStimmzettelNumber(
        wahlID,
        wahlbezirkID,
        teamID
      );

      expect(mockDefinitions.getStimmzettel.mock.calls.length).toStrictEqual(1);
      expect(mockDefinitions.getStimmzettel.mock.calls[0]).toStrictEqual([
        wahlID,
        wahlbezirkID,
        teamID,
      ]);
      expect(result).toStrictEqual(25);
    });

    it("should_returnNextNumber_when_noStimmzettelExists", async () => {
      mockDefinitions.getStimmzettel.mockReturnValue([]);

      const result = await getNextStimmzettelNumber(
        wahlID,
        wahlbezirkID,
        teamID
      );

      expect(mockDefinitions.getStimmzettel.mock.calls.length).toStrictEqual(1);
      expect(mockDefinitions.getStimmzettel.mock.calls[0]).toStrictEqual([
        wahlID,
        wahlbezirkID,
        teamID,
      ]);
      expect(result).toStrictEqual(1);
    });

    it("should_throwError_when_getStimmzettelFailes", async () => {
      mockDefinitions.getStimmzettel.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        getNextStimmzettelNumber(wahlID, wahlbezirkID, teamID)
      ).rejects.toThrowError();
    });
  });
});
