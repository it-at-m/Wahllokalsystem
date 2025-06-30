import { createTestingPinia } from "@pinia/testing";
import { useKonfigurationsparameterTestDataFactory } from "@tests/utils/infomanagement/KonfigurationsparameterTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getKonfigurationsparameter: vi.fn(),
}));

vi.mock("@/composables/infomanagement/konfigurationsparameterService", () => ({
  useKonfigurationsparameterService: () => ({
    getKonfigurationsparameter: mockDefinitions.getKonfigurationsparameter,
  }),
}));

const { createKonfigurationsparameterList, prepareKonfigurationsparameter } =
  useKonfigurationsparameterTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

describe("infomanagementStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useInfomanagementStore>;
  let userStore: ReturnType<typeof useUserStore>;

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    userStore = useUserStore(testPinia);
    unitUnderTest = useInfomanagementStore(testPinia);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("initKonfigurationsparameter", () => {
    it("should_loadKonfigurations_when_called", async () => {
      const mockedMappedKonfigurationparameter =
        createKonfigurationsparameterList(3);

      mockDefinitions.getKonfigurationsparameter.mockReturnValue(
        mockedMappedKonfigurationparameter
      );

      await unitUnderTest.initKonfigurationsparameter();
      await nextTick();

      expect(unitUnderTest.konfigurationsparameter).toStrictEqual(
        mockedMappedKonfigurationparameter
      );
    });

    it("should_notUpdateKonfigurationsparameter_when_serviceCallFailed", async () => {
      mockDefinitions.getKonfigurationsparameter.mockRejectedValueOnce(
        new Error("service call failed")
      );

      await expect(() =>
        unitUnderTest.initKonfigurationsparameter()
      ).rejects.toThrowError();
      expect(unitUnderTest.konfigurationsparameter).toStrictEqual(null);
    });
  });

  describe("dateTimeToCheckAnwesenheit", () => {
    it("should_beUndefined_when_userHasBlankCurrentWahltag", async () => {
      userStore.setUser(prepareUser().wahltag("").build());

      await flushPromises();

      expect(unitUnderTest.dateTimeToCheckAnwesenheit).toBeUndefined();
    });

    it("should_beUndefined_when_userHasUndefinedCurrentWahltag", async () => {
      userStore.setUser(prepareUser().wahltag(undefined).build());

      await flushPromises();

      expect(unitUnderTest.dateTimeToCheckAnwesenheit).toBeUndefined();
    });

    it("should_beUndefined_when_userHasCurrentWahltagButConfigParamIsNotGiven", async () => {
      userStore.setUser(prepareUser().wahltag("2025-06-26").build());
      unitUnderTest.konfigurationsparameter = [];

      await flushPromises();

      expect(unitUnderTest.dateTimeToCheckAnwesenheit).toBeUndefined();
    });

    it("should_beAtTimeOfWahltag_when_wahltagAndConfigParamIsGiven", async () => {
      const wahltagDateString = "2025-06-26";
      userStore.setUser(prepareUser().wahltag(wahltagDateString).build());

      const anwesenheitCheckTimeString = "12:11:23";
      unitUnderTest.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("MELDUNGSZEIT_ANWESENHEIT_CHECK")
          .wert(anwesenheitCheckTimeString)
          .build(),
      ];

      await flushPromises();

      expect(unitUnderTest.dateTimeToCheckAnwesenheit?.getTime()).toStrictEqual(
        new Date(`${wahltagDateString}T${anwesenheitCheckTimeString}`).getTime()
      );
    });

    it.each([
      { invalidTime: "" },
      { invalidTime: "   " },
      { invalidTime: "13" },
      { invalidTime: "12:1" },
      { invalidTime: "12:61" },
    ])(
      "should_beUndefined_when_value'$invalidTime'IsNotATimeFormat",
      async (args) => {
        userStore.setUser(prepareUser().wahltag("2025-06-26").build());
        unitUnderTest.konfigurationsparameter = [
          prepareKonfigurationsparameter()
            .schluessel("MELDUNGSZEIT_ANWESENHEIT_CHECK")
            .wert(args.invalidTime)
            .build(),
        ];

        await flushPromises();

        expect(unitUnderTest.dateTimeToCheckAnwesenheit).toBeUndefined();
      }
    );
  });
});
