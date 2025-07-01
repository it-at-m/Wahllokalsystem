import { createTestingPinia } from "@pinia/testing";
import { useKonfigurationsparameterTestDataFactory } from "@tests/utils/infomanagement/KonfigurationsparameterTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

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

  describe("fruehesteEroeffnungsuhrzeit", () => {
    let userStore: ReturnType<typeof useUserStore>;
    let infomanagementStore: ReturnType<typeof useInfomanagementStore>;

    beforeEach(() => {
      userStore = useUserStore();
      infomanagementStore = useInfomanagementStore();
    });

    it("should_returnFRUEHESTE_EROEFFNUNGSZEIT_UWValue_when_wahlbezirkArtIsUWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      const konfigKeyValue = "12:34:51";
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_EROEFFNUNGSZEIT_UW")
          .wert(konfigKeyValue)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        konfigKeyValue
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsUWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        "00:00:00"
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsUWBButKonfigKeyHasEmptyValue", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_EROEFFNUNGSZEIT_UW")
          .wert("")
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        "00:00:00"
      );
    });

    it("should_returnFRUEHESTE_EROEFFNUNGSZEIT_BWValue_when_wahlbezirkArtIsBWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      const konfigKeyValue = "12:34:51";
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_EROEFFNUNGSZEIT_BW")
          .wert(konfigKeyValue)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        konfigKeyValue
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsBWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        "00:00:00"
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsBWBButKonfigKeyHasEmptyValue", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_EROEFFNUNGSZEIT_BW")
          .wert("")
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        "00:00:00"
      );
    });
  });

  describe("fruehesteSchliessungsuhrzeit", () => {
    let userStore: ReturnType<typeof useUserStore>;
    let infomanagementStore: ReturnType<typeof useInfomanagementStore>;

    beforeEach(() => {
      userStore = useUserStore();
      infomanagementStore = useInfomanagementStore();
    });

    it("should_returnFRUEHESTE_SCHLIESSUNGSZEIT_UWValue_when_wahlbezirkArtIsUWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      const konfigKeyValue = "12:34:51";
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_SCHLIESSUNGSZEIT_UW")
          .wert(konfigKeyValue)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        konfigKeyValue
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsUWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        "00:00:00"
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsUWBButKonfigKeyHasEmptyValue", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_SCHLIESSUNGSZEIT_UW")
          .wert("")
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        "00:00:00"
      );
    });

    it("should_returnFRUEHESTE_SCHLIESSUNGSZEIT_BWValue_when_wahlbezirkArtIsBWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      const konfigKeyValue = "12:34:51";
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_SCHLIESSUNGSZEIT_BW")
          .wert(konfigKeyValue)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        konfigKeyValue
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsBWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        "00:00:00"
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsBWBButKonfigKeyHasEmptyValue", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_SCHLIESSUNGSZEIT_BW")
          .wert("")
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        "00:00:00"
      );
    });
  });
});
