import type {
  KonfigurierterWahltagDTO,
  WahltagDTO,
} from "@/api/wls-clients/generated-admin-api";

import { spyOn } from "@storybook/test";
import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import useWahltagService from "@/composables/wahltag/wahltagService.ts";

const mockDefinitions = vi.hoisted(() => ({
  apiGetWahltage: vi.fn(),
  apiGetKonfigurierteWahltage: vi.fn(),
  wahltageControllerApi: vi.fn(),
  compareByNummerAsc: vi.fn(),
  mapGroupedWahltagDtosToWahltage: vi.fn(),
  groupWahltagDtosByWahltag: vi.fn(),
  addNotification: vi.fn(),
  wahltagControllerApiConstructor: vi.fn().mockImplementation(() => {
    return {
      getWahltage: mockDefinitions.apiGetWahltage,
    };
  }),
  adminApiConfigurationConstructor: vi.fn().mockImplementation(() => {
    return {};
  }),
  konfigurierterWahltagControllerApiConstructor: vi
    .fn()
    .mockImplementation(() => {
      return {
        getKonfigurierteWahltage: mockDefinitions.apiGetKonfigurierteWahltage,
      };
    }),
}));

vi.mock("@/api/wls-clients/generated-admin-api", () => ({
  Configuration: mockDefinitions.adminApiConfigurationConstructor,
  WahltageControllerApi: mockDefinitions.wahltagControllerApiConstructor,
  KonfigurierteWahltageControllerApi:
    mockDefinitions.konfigurierterWahltagControllerApiConstructor,
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("@/composables/wahltag/wahltagDtoUtils.ts", () => ({
  useWahltagDtoUtils: () => ({
    groupWahltagDtosByWahltag: mockDefinitions.groupWahltagDtosByWahltag,
  }),
}));
vi.mock("@/composables/wahltag/wahltagMapper.ts", () => ({
  useWahltagMapper: () => ({
    mapGroupedWahltagDtosToWahltage:
      mockDefinitions.mapGroupedWahltagDtosToWahltage,
  }),
}));
vi.mock("@/types/wahltag/WahltagEvent.ts", () => ({
  compareByNummerAsc: mockDefinitions.compareByNummerAsc,
}));

const {
  createWahltagComplete,
  prepareKonfigurierterWahltagDTO,
  prepareWahltagDtoComplete,
} = useWahltagTestDataFactory();

const unitUnderTest = useWahltagService();

describe("wahltagService.ts", () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("getWahltage", () => {
    it("should_returnWahltageFromTheAdminAPI_when_called", async () => {
      const { mockedWahltage, mockedResponseGetWahltage, mockGroupedWahltage } =
        useMockSetupForSuccessfulLoadWahltage();

      const result = await unitUnderTest.getWahltage();

      expect(result).toStrictEqual(mockedWahltage);

      expect(mockDefinitions.groupWahltagDtosByWahltag).toHaveBeenCalledWith(
        mockedResponseGetWahltage
      );
      expect(
        mockDefinitions.mapGroupedWahltagDtosToWahltage
      ).toHaveBeenCalledWith(mockGroupedWahltage);
      expect(
        mockDefinitions.compareByNummerAsc.mock.calls.length
      ).toBeGreaterThan(0);
    });

    it("should_changeTheValueOfLoading_when_loadingIsNotUndefined", async () => {
      useMockSetupForSuccessfulLoadWahltage();

      const loadingRef = ref(false);
      const spyOnValueSetterOfRef = spyOn(loadingRef, "value", "set");

      await unitUnderTest.getWahltage(loadingRef);

      expect(spyOnValueSetterOfRef.mock.calls).toStrictEqual([[true], [false]]);

      spyOnValueSetterOfRef.mockRestore();
    });

    it("should_returnEmptyArray_when_anExceptionOccurred", async () => {
      mockDefinitions.apiGetWahltage.mockRejectedValue(
        new Error("api call failed")
      );

      const result = await unitUnderTest.getWahltage();

      expect(result).toStrictEqual([]);
    });

    it("should_returnEmptyArray_when_apiReturned204WithUndefined", async () => {
      useMockSetupForSuccessfulLoadWahltage();
      mockDefinitions.apiGetWahltage.mockReturnValue(
        Promise.resolve({ data: undefined })
      );

      const result = await unitUnderTest.getWahltage();

      expect(result).toStrictEqual([]);
    });

    it("should_triggerToastyWithError_when_anExceptionOccurred", async () => {
      mockDefinitions.apiGetWahltage.mockRejectedValue(
        new Error("api call failed")
      );

      await unitUnderTest.getWahltage();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Error",
      ]);
    });

    it("should_useAdminUrl_when_initializingWahltageControllerApi", () => {
      useWahltagService();

      const configurationConstructorParameter =
        mockDefinitions.adminApiConfigurationConstructor.mock.calls[0][0];

      expect(configurationConstructorParameter["basePath"]).toStrictEqual(
        "/api/admin-service"
      );
    });
  });

  describe("isKonfigurierterWahltag", () => {
    it("should_returnTrue_when_wahltagIDIsPartOfInfomanagementResponse", async () => {
      const wahltagID = "wahltagID";

      const mockResponseKonfigurierteWahltag: KonfigurierterWahltagDTO[] = [
        prepareKonfigurierterWahltagDTO().wahltagID(wahltagID).build(),
        prepareKonfigurierterWahltagDTO().wahltagID(`${wahltagID}0815`).build(),
      ];
      mockDefinitions.apiGetKonfigurierteWahltage.mockReturnValue(
        Promise.resolve({ data: mockResponseKonfigurierteWahltag })
      );

      const result = await unitUnderTest.isKonfigurierterWahltag(wahltagID);

      expect(result).toStrictEqual(true);
    });

    it("should_returnFalse_when_wahltagIDIsNotPartOfInfomanagementResponse", async () => {
      const wahltagID = "wahltagID";

      const mockResponseKonfigurierteWahltag: KonfigurierterWahltagDTO[] = [];
      mockDefinitions.apiGetKonfigurierteWahltage.mockReturnValue(
        Promise.resolve({ data: mockResponseKonfigurierteWahltag })
      );

      const result = await unitUnderTest.isKonfigurierterWahltag(wahltagID);

      expect(result).toStrictEqual(false);
    });

    it("should_addNotificationAndReturnFalse_when_anExceptionOccurred", async () => {
      const wahltagID = "wahltagID";

      mockDefinitions.apiGetKonfigurierteWahltage.mockRejectedValue(
        new Error("api call failed")
      );

      const result = await unitUnderTest.isKonfigurierterWahltag(wahltagID);

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        "Error",
      ]);
      expect(result).toStrictEqual(false);
    });
  });
});

function useMockSetupForSuccessfulLoadWahltage() {
  const mockedResponseGetWahltage = [prepareWahltagDtoComplete().build()];
  const mockGroupedWahltage = new Map<string, WahltagDTO[]>([
    ["wahltag1", [prepareWahltagDtoComplete().build()]],
    [
      "wahltag2",
      [
        prepareWahltagDtoComplete().build(),
        prepareWahltagDtoComplete().build(),
      ],
    ],
  ]);
  const mockedWahltage = [createWahltagComplete(1), createWahltagComplete(2)];

  mockDefinitions.apiGetWahltage.mockReturnValue(
    Promise.resolve({ data: mockedResponseGetWahltage })
  );
  mockDefinitions.groupWahltagDtosByWahltag.mockReturnValue(
    mockGroupedWahltage
  );
  mockDefinitions.mapGroupedWahltagDtosToWahltage.mockReturnValue(
    mockedWahltage
  );
  mockDefinitions.compareByNummerAsc.mockReturnValue(0);

  return {
    mockedResponseGetWahltage,
    mockGroupedWahltage,
    mockedWahltage,
  };
}
