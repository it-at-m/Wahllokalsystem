import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useBeschlussfassungStartenDialogUtils } from "@/composables/dse/beschlussfassungStartenDialogUtils.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getStimmzettel: vi.fn(),
  saveDseWorkflowStatus: vi.fn(),
  routerPush: vi.fn(),
}));

vi.mock(import("@/composables/dse/stimmzettelService.ts"), () => ({
  useStimmzettelService: () => ({
    getStimmzettel: mockDefinitions.getStimmzettel,
    saveStimmzettel: vi.fn(),
  }),
}));

vi.mock(import("@/composables/dse/dseWorkflowStatusService.ts"), () => ({
  useDseWorkflowStatusService: () => ({
    saveDseWorkflowStatus: mockDefinitions.saveDseWorkflowStatus,
    loadDseWorkflowStatus: vi.fn(),
  }),
}));

router.push = mockDefinitions.routerPush;

describe("beschlussfassungStartenDialogUtils.ts", () => {
  const { createStimmzettel } = useStimmzettelTestDataFactory();
  const { prepareStimmzettelerfassungStatus } =
    useStimmzettelerfassungStatusTestDataFactory();

  let unitUnderTest: ReturnType<typeof useBeschlussfassungStartenDialogUtils>;

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";

  beforeEach(() => {
    vi.clearAllMocks();

    unitUnderTest = useBeschlussfassungStartenDialogUtils();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });
  describe("loadStimmzettelCount", () => {
    it("should_countStimmzettel_when_oneTeamInTeamstatusList", async () => {
      const teamstatusList = [
        {
          teamID: "A",
          status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
        },
      ];

      mockDefinitions.getStimmzettel.mockReturnValue([
        createStimmzettel(),
        createStimmzettel(),
      ]);

      const result = await unitUnderTest.loadStimmzettelCount(
        wahlID,
        wahlbezirkID,
        teamstatusList
      );

      expect(mockDefinitions.getStimmzettel.mock.calls.length).toStrictEqual(1);
      expect(result).toStrictEqual(2);
    });

    it("should_countStimmzettel_when_multipleTeamsInTeamstatusList", async () => {
      const teamstatusList = [
        {
          teamID: "A",
          status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
        },
        {
          teamID: "B",
          status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
        },
      ];

      mockDefinitions.getStimmzettel.mockReturnValue([
        createStimmzettel(),
        createStimmzettel(),
      ]);

      const result = await unitUnderTest.loadStimmzettelCount(
        wahlID,
        wahlbezirkID,
        teamstatusList
      );

      expect(mockDefinitions.getStimmzettel.mock.calls.length).toStrictEqual(2);
      expect(result).toStrictEqual(4);
    });
  });

  describe("updateWorkflowStatusAndNavigate", () => {
    it("should_updateStateAndNavigate_when_called", async () => {
      await unitUnderTest.updateWorkflowStatusAndNavigate(wahlID, wahlbezirkID);

      expect(mockDefinitions.saveDseWorkflowStatus.mock.calls[0]).toStrictEqual(
        [
          wahlID,
          wahlbezirkID,
          prepareStimmzettelerfassungStatus()
            .status(StimmzettelerfassungStatusEnum.SteAbgeschlossen)
            .build(),
        ]
      );
      expect(mockDefinitions.routerPush.mock.calls.length).toStrictEqual(1);
    });
  });
});
