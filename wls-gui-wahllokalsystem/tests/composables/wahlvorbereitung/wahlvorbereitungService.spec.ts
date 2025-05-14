import type { UrnenwahlSchliessungsUhrzeitWriteDTO } from "@/api/wls-clients/generated-wahlvorbereitung-api";

import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorbereitungService } from "@/composables/wahlvorbereitung/wahlvorbereitungService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  toDTO: vi.fn(),
  postUrnenwahlSchliessungsUhrzeit: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-wahlvorbereitung-api", () => ({
  UrnenwahlSchliessungsUhrzeitControllerApi: vi.fn().mockImplementation(() => ({
    postUrnenwahlSchliessungsUhrzeit:
      mockDefinitions.postUrnenwahlSchliessungsUhrzeit,
  })),
  Configuration: vi.fn(),
}));

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

vi.mock("@/composables/wahlvorbereitung/wahlvorbereitungMapper.ts", () => ({
  useWahlvorbereitungMapper: () => ({
    toDTO: mockDefinitions.toDTO,
  }),
}));

const { postUrnenwahlSchliessungsuhrzeit } = useWahlvorbereitungService();

describe("wahlvorbereitungService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("postUrnenwahlSchliessungsuhrzeit", () => {
    it("should_throwErrorAndCallNotificationService_when_apiCallFails", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const schliessungsuhrzeit = "2025-04-29T12:12:42";

      const utcDate = new Date(schliessungsuhrzeit);
      const expectedDate = new Date(utcDate.getTime());

      const mockedSchliessungsuhrzeitAsDTO: UrnenwahlSchliessungsUhrzeitWriteDTO =
        { schliessungsuhrzeit };
      mockDefinitions.toDTO.mockReturnValue(mockedSchliessungsuhrzeitAsDTO);

      mockDefinitions.postUrnenwahlSchliessungsUhrzeit.mockRejectedValue(
        new Error("API Error")
      );

      await expect(
        postUrnenwahlSchliessungsuhrzeit(
          wahlbezirkID,
          new Date(schliessungsuhrzeit)
        )
      ).rejects.toThrow("API Error");

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(mockDefinitions.toDTO.mock.calls).toStrictEqual([[expectedDate]]);
    });
  });
});
