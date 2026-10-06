import { useStimmzettelerfassungTeamStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungTeamStatusTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelerfassungTeamStatusState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusState.ts";

const mockDefinitions = vi.hoisted(() => ({
  isSaving: { value: false },
  loadErfassungTeamStatus: vi.fn(),
  postErfassungTeamStatus: vi.fn(),
}));

vi.mock(
  "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts",
  () => ({
    useStimmzettelerfassungTeamStatusService: () => ({
      isSaving: mockDefinitions.isSaving,
      loadErfassungTeamStatus: mockDefinitions.loadErfassungTeamStatus,
      postErfassungTeamStatus: mockDefinitions.postErfassungTeamStatus,
    }),
  })
);

describe("stimmzettelerfassungTeamStatusState.ts", () => {
  const { createStimmzettelerfassungTeamStatusModel } =
    useStimmzettelerfassungTeamStatusTestDataFactory();

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";
  const teamID = "teamID";

  let unitUnderTest: ReturnType<typeof useStimmzettelerfassungTeamStatusState>;

  beforeEach(() => {
    unitUnderTest = useStimmzettelerfassungTeamStatusState(
      wahlID,
      wahlbezirkID,
      teamID
    );
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
    mockDefinitions.isSaving.value = false;
  });

  it("should_haveInitialState_when_created", () => {
    expect(unitUnderTest.teamStatus.value).toBeNull();
    expect(unitUnderTest.isSaving).toBe(mockDefinitions.isSaving);
  });

  describe("loadErfassungTeamStatus", () => {
    it("should_loadTeamStatusAndUpdateState_when_serviceSucceeds", async () => {
      const mockedTeamStatus = createStimmzettelerfassungTeamStatusModel();
      mockDefinitions.loadErfassungTeamStatus.mockResolvedValue(
        mockedTeamStatus
      );

      await unitUnderTest.loadErfassungTeamStatus();

      expect(mockDefinitions.loadErfassungTeamStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        teamID,
        true
      );
      expect(unitUnderTest.teamStatus.value).toEqual(mockedTeamStatus);
    });

    it("should_forwardSendNotification_when_loadErfassungTeamStatusIsCalled", async () => {
      const mockedTeamStatus = createStimmzettelerfassungTeamStatusModel();
      mockDefinitions.loadErfassungTeamStatus.mockResolvedValue(
        mockedTeamStatus
      );

      await unitUnderTest.loadErfassungTeamStatus(false);

      expect(mockDefinitions.loadErfassungTeamStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        teamID,
        false
      );
      expect(unitUnderTest.teamStatus.value).toEqual(mockedTeamStatus);
    });

    it("should_setTeamStatusToNull_when_serviceReturnsNull", async () => {
      mockDefinitions.loadErfassungTeamStatus.mockResolvedValue(null);

      await unitUnderTest.loadErfassungTeamStatus();

      expect(mockDefinitions.loadErfassungTeamStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        teamID,
        true
      );
      expect(unitUnderTest.teamStatus.value).toBeNull();
    });

    it("should_rethrowAndKeepPreviousState_when_serviceFails", async () => {
      const initialTeamStatus = createStimmzettelerfassungTeamStatusModel();
      const error = new Error("loading failed");

      unitUnderTest.teamStatus.value = initialTeamStatus;
      mockDefinitions.loadErfassungTeamStatus.mockRejectedValue(error);

      await expect(unitUnderTest.loadErfassungTeamStatus()).rejects.toBe(error);

      expect(mockDefinitions.loadErfassungTeamStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        teamID,
        true
      );
      expect(unitUnderTest.teamStatus.value).toEqual(initialTeamStatus);
    });
  });

  describe("postErfassungTeamStatus", () => {
    it("should_postTeamStatusAndUpdateState_when_serviceSucceeds", async () => {
      const teamStatusToSend = createStimmzettelerfassungTeamStatusModel();
      const persistedTeamStatus = createStimmzettelerfassungTeamStatusModel();

      mockDefinitions.postErfassungTeamStatus.mockResolvedValue(
        persistedTeamStatus
      );

      await unitUnderTest.postErfassungTeamStatus(teamStatusToSend);

      expect(mockDefinitions.postErfassungTeamStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        teamID,
        teamStatusToSend,
        true
      );
      expect(unitUnderTest.teamStatus.value).toEqual(persistedTeamStatus);
    });

    it("should_forwardSendNotification_when_postErfassungTeamStatusIsCalled", async () => {
      const teamStatusToSend = createStimmzettelerfassungTeamStatusModel();
      const persistedTeamStatus = createStimmzettelerfassungTeamStatusModel();

      mockDefinitions.postErfassungTeamStatus.mockResolvedValue(
        persistedTeamStatus
      );

      await unitUnderTest.postErfassungTeamStatus(teamStatusToSend, false);

      expect(mockDefinitions.postErfassungTeamStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        teamID,
        teamStatusToSend,
        false
      );
      expect(unitUnderTest.teamStatus.value).toEqual(persistedTeamStatus);
    });

    it("should_rethrowAndKeepPreviousState_when_serviceFails", async () => {
      const initialTeamStatus = createStimmzettelerfassungTeamStatusModel();
      const teamStatusToSend = createStimmzettelerfassungTeamStatusModel();
      const error = new Error("saving failed");

      unitUnderTest.teamStatus.value = initialTeamStatus;
      mockDefinitions.postErfassungTeamStatus.mockRejectedValue(error);

      await expect(
        unitUnderTest.postErfassungTeamStatus(teamStatusToSend)
      ).rejects.toBe(error);

      expect(mockDefinitions.postErfassungTeamStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        teamID,
        teamStatusToSend,
        true
      );
      expect(unitUnderTest.teamStatus.value).toEqual(initialTeamStatus);
    });
  });
});
