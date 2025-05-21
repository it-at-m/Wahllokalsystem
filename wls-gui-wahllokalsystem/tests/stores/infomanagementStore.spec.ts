import { createTestingPinia } from "@pinia/testing";
import { useKonfigurationsparameterTestDataFactory } from "@tests/utils/infomanagement/KonfigurationsparameterTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getKonfigurationsparameter: vi.fn(),
}));

vi.mock("@/composables/infomanagement/konfigurationsparameterService", () => ({
  useKonfigurationsparameterService: () => ({
    getKonfigurationsparameter: mockDefinitions.getKonfigurationsparameter,
  }),
}));

const { createKonfigurationDtoList, mapDtosToModel } =
  useKonfigurationsparameterTestDataFactory();

describe("infomanagementStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useInfomanagementStore>;

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useInfomanagementStore(testPinia);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("getKonfigurations", () => {
    it("should_loadKonfigurations_when_called", async () => {
      const mockedKonfigurationDtos = createKonfigurationDtoList(3);
      const expectedResult = mapDtosToModel(mockedKonfigurationDtos);

      mockDefinitions.getKonfigurationsparameter.mockReturnValue(
        mockedKonfigurationDtos
      );

      await unitUnderTest.initKonfigurationsparameter();
      await nextTick();

      expect(unitUnderTest.konfigurationsparameter).toStrictEqual(
        expectedResult
      );
      expect(unitUnderTest.konfigurationsparameterReady).toBe(true);
    });

    it("should_notUpdateKonfigurationsparameter_when_serviceCallFailed", async () => {
      mockDefinitions.getKonfigurationsparameter.mockRejectedValueOnce(
        new Error("service call failed")
      );

      await expect(() =>
        unitUnderTest.initKonfigurationsparameter()
      ).rejects.toThrowError();
      expect(unitUnderTest.konfigurationsparameter).toStrictEqual(null);
      expect(unitUnderTest.konfigurationsparameterReady).toStrictEqual(false);
    });
  });
});
