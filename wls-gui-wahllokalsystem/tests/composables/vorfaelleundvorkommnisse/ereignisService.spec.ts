import type { EreignisseWriteDTO } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";

import { beforeEach, describe, expect, it, vi } from "vitest";

import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  mapToDto: vi.fn(),
  postEreignisse: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-vorfaelleundvorkommnisse-api", () => ({
  EreignisControllerApi: class {
    postEreignisse = mockDefinitions.postEreignisse;
  },
  Configuration: vi.fn(),
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("@/composables/vorfaelleundvorkommnisse/ereignisMapper.ts", () => ({
  useEreignisMapper: () => ({
    toDto: mockDefinitions.mapToDto,
  }),
}));

const { saveEreignisse } = useEreignisService();

describe("ereignisService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("saveEreignisse", () => {
    it("should_notCallNotificationServiceAfterSuccess_when_sendNotificationParameterIsFalse", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const ereignisse =
        WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse();

      const mockedEreignisseAsDTO: EreignisseWriteDTO = {};
      mockDefinitions.mapToDto.mockReturnValue(mockedEreignisseAsDTO);

      await saveEreignisse(wahlbezirkID, ereignisse, false);

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.mapToDto.mock.calls).toStrictEqual([[ereignisse]]);
    });
    it("should_notCallNotificationServiceAfterFailure_when_sendNotificationParameterIsFalse", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const ereignisse =
        WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse();

      const mockedEreignisseAsDTO: EreignisseWriteDTO = {};
      mockDefinitions.mapToDto.mockReturnValue(mockedEreignisseAsDTO);

      mockDefinitions.postEreignisse.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await saveEreignisse(wahlbezirkID, ereignisse, false);

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.mapToDto.mock.calls).toStrictEqual([[ereignisse]]);
    });

    it("should_callNotificationServiceAfterSuccess_when_sendNotificationParameterIsTrue", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const ereignisse =
        WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse();

      const mockedEreignisseAsDTO: EreignisseWriteDTO = {};
      mockDefinitions.mapToDto.mockReturnValue(mockedEreignisseAsDTO);

      await saveEreignisse(wahlbezirkID, ereignisse, true);

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
      expect(mockDefinitions.mapToDto.mock.calls).toStrictEqual([[ereignisse]]);
    });

    it("should_callNotificationServiceAfterFailure_when_sendNotificationParameterIsTrue", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const ereignisse =
        WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse();

      const mockedEreignisseAsDTO: EreignisseWriteDTO = {};
      mockDefinitions.mapToDto.mockReturnValue(mockedEreignisseAsDTO);

      mockDefinitions.postEreignisse.mockRejectedValue(
        new Error("mocked api call failed")
      );

      await saveEreignisse(wahlbezirkID, ereignisse, true);

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(mockDefinitions.mapToDto.mock.calls).toStrictEqual([[ereignisse]]);
    });
  });
});
