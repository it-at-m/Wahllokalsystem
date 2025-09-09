import type { WaehleranzahlDTO } from "@/api/wls-clients/generated-monitoring-api";
import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useMonitoringService } from "@/composables/monitoring/monitoringService.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  toDto: vi.fn(),
  toModel: vi.fn(),
  postWahlbeteiligung: vi.fn(),
  getWahlbeteiligung: vi.fn(),
  postLastSeen: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-monitoring-api", () => ({
  WaehleranzahlControllerApi: vi.fn().mockImplementation(() => ({
    postWahlbeteiligung: mockDefinitions.postWahlbeteiligung,
    getWahlbeteiligung: mockDefinitions.getWahlbeteiligung,
  })),
  WahllokalZustandControllerApi: vi.fn().mockImplementation(() => ({
    postLastSeen: mockDefinitions.postLastSeen,
  })),
  Configuration: vi.fn(),
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("@/composables/monitoring/wahlbeteiligungMapper.ts", () => ({
  useWahlbeteiligungMapper: () => ({
    toDto: mockDefinitions.toDto,
    toModel: mockDefinitions.toModel,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { postWahlbeteiligung, getWahlbeteiligung, postLastSeen } =
  useMonitoringService();
const { axiosConfigWrapper } = useCommonApiUtils();

const mockedNow = new Date();

describe("monitoringService.ts", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();

    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  describe("getWahlbeteiligung", () => {
    it("should_returnWaehleranzahl_when_givenWahlIdAndWahlbezirkId", async () => {
      const wahlbezirkID = generateRandomString(10);
      const wahlID = generateRandomString(10);

      const mockedWaehleranzahlDTO: WaehleranzahlDTO = {
        anzahlWaehler: 5,
        uhrzeit: "2025-05-05T12:00:00",
      };
      const mockedWaehleranzahlModel: Waehleranzahl = {
        anzahlWaehler: 5,
        uhrzeit: new Date("2025-05-05T12:00:00"),
      };

      mockDefinitions.getWahlbeteiligung.mockResolvedValue({
        status: 200,
        data: mockedWaehleranzahlDTO,
      });
      mockDefinitions.toModel.mockReturnValue(mockedWaehleranzahlModel);

      const result = await getWahlbeteiligung(wahlID, wahlbezirkID);

      expect(mockDefinitions.getWahlbeteiligung).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID
      );
      expect(result).toEqual(mockedWaehleranzahlModel);
      expect(mockDefinitions.toModel.mock.calls).toStrictEqual([
        [mockedWaehleranzahlDTO],
      ]);
    });

    it.each([
      {
        when: "wahlIdEmpty",
        wahlID: "",
        wahlbezirkID: generateRandomString(10),
      },
      {
        when: "wahlIdBlank",
        wahlID: "  ",
        wahlbezirkID: generateRandomString(10),
      },
      {
        when: "wahlbezirkIdEmpty",
        wahlID: generateRandomString(10),
        wahlbezirkID: "",
      },
      {
        when: "wahlbezirkIdBlank",
        wahlID: generateRandomString(10),
        wahlbezirkID: "  ",
      },
    ])(
      "should_notReturnWaehleranzahl_when_$when",
      async ({ wahlID, wahlbezirkID }) => {
        mockDefinitions.getWahlbeteiligung.mockRejectedValue(
          new Error("mocked api call failed")
        );

        await getWahlbeteiligung(wahlID, wahlbezirkID);

        expect(mockDefinitions.getWahlbeteiligung).toHaveBeenCalledWith(
          wahlID,
          wahlbezirkID
        );
        expect(mockDefinitions.toModel).not.toHaveBeenCalled();
      }
    );

    it("should_returnUndefined_when_apiCallReturnedResponseWithStatus204", async () => {
      const wahlbezirkID = generateRandomString(10);
      const wahlID = generateRandomString(10);

      mockDefinitions.getWahlbeteiligung.mockResolvedValue(
        Promise.resolve({ status: 204, data: null })
      );

      const result = await getWahlbeteiligung(wahlID, wahlbezirkID);

      expect(result).toStrictEqual(undefined);
      expect(mockDefinitions.getWahlbeteiligung).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID
      );
      expect(mockDefinitions.toModel).toHaveBeenCalledTimes(0);
    });
  });

  describe("postWahlbeteiligung", () => {
    it.each([
      {
        when: "wahlIdEmpty",
        wahlID: "",
        wahlbezirkID: generateRandomString(10),
      },
      {
        when: "wahlIdBlank",
        wahlID: "  ",
        wahlbezirkID: generateRandomString(10),
      },
      {
        when: "wahlbezirkIdEmpty",
        wahlID: generateRandomString(10),
        wahlbezirkID: "",
      },
      {
        when: "wahlbezirkIdBlank",
        wahlID: generateRandomString(10),
        wahlbezirkID: "  ",
      },
    ])(
      "should_notPostWaehleranzahl_when_$when",
      async ({ wahlID, wahlbezirkID }) => {
        const waehler = 2;
        const mockedWaehleranzahlDTO: WaehleranzahlDTO = {
          anzahlWaehler: waehler,
          uhrzeit: mockedNow.toISOString(),
        };

        mockDefinitions.toDto.mockReturnValue(mockedWaehleranzahlDTO);
        mockDefinitions.postWahlbeteiligung.mockRejectedValueOnce(
          new Error("mocked api call failed")
        );

        await expect(
          postWahlbeteiligung(wahlbezirkID, wahlID, waehler)
        ).rejects.toThrow("postWahlbeteiligung failed");
      }
    );

    it("should_postWaehleranzahl_when_givenWahlIdAndWahlbezirkId", async () => {
      const wahlbezirkID = generateRandomString(10);
      const wahlID = generateRandomString(10);
      const waehler = 2;

      const mockedWaehleranzahlDTO: WaehleranzahlDTO = {
        anzahlWaehler: waehler,
        uhrzeit: mockedNow.toISOString(),
      };

      mockDefinitions.toDto.mockReturnValue(mockedWaehleranzahlDTO);
      mockDefinitions.postWahlbeteiligung.mockResolvedValue({});

      await postWahlbeteiligung(wahlbezirkID, wahlID, waehler);

      expect(mockDefinitions.postWahlbeteiligung).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        mockedWaehleranzahlDTO
      );
    });
  });

  describe("postLastSeen", () => {
    it("should_throwNoError_when_requestSucceeded", async () => {
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.postLastSeen.mockResolvedValue(null);

      await postLastSeen(wahlbezirkID);

      expect(mockDefinitions.postLastSeen.mock.calls).toStrictEqual([
        [wahlbezirkID, axiosConfigWrapper().requestAsOnlineOnly()],
      ]);
    });

    it("should_throwNewError_when_requestFailed", async () => {
      const wahlbezirkID = "wahlbezirkID";

      const mockedApiError = new Error("mocked api call failed");
      mockDefinitions.postLastSeen.mockRejectedValue(mockedApiError);

      await expect(postLastSeen(wahlbezirkID)).rejects.toThrow(
        "postLastSeen failed"
      );
      expect(mockDefinitions.postLastSeen.mock.calls).toStrictEqual([
        [wahlbezirkID, axiosConfigWrapper().requestAsOnlineOnly()],
      ]);
    });
  });
});
