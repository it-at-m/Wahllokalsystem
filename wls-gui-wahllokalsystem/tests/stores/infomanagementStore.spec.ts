import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
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
const { generateRandomString } = useCommonTestDataFactory();

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

    const DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_UW = "08:00";
    const DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_BW = "15:00";

    beforeEach(() => {
      userStore = useUserStore();
      infomanagementStore = useInfomanagementStore();
    });

    it("should_returnFruehesteEroeffnungszeitUWValue_when_wahlbezirkArtIsUWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      const konfigKeyValueUW = generateRandomString(8);
      const konfigKeyValueBW = generateRandomString(8);
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_EROEFFNUNGSZEIT_UW")
          .wert(konfigKeyValueUW)
          .build(),
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_EROEFFNUNGSZEIT_BW")
          .wert(konfigKeyValueBW)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        konfigKeyValueUW
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsUWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_UW
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
        DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_UW
      );
    });

    it("should_returnFruehesteEroeffnungszeitBWValue_when_wahlbezirkArtIsBWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      const konfigKeyValueBW = generateRandomString(8);
      const konfigKeyValueUW = generateRandomString(8);
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_EROEFFNUNGSZEIT_BW")
          .wert(konfigKeyValueBW)
          .build(),
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_EROEFFNUNGSZEIT_UW")
          .wert(konfigKeyValueUW)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        konfigKeyValueBW
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsBWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.fruehesteEroeffnungsuhrzeit).toStrictEqual(
        DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_BW
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
        DEFAULT_FRUEHESTE_EROEFFNUNGSZEIT_BW
      );
    });
  });

  describe("spaetesteEroeffnungsuhrzeit", () => {
    let userStore: ReturnType<typeof useUserStore>;
    let infomanagementStore: ReturnType<typeof useInfomanagementStore>;

    const DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_UW = "17:59";
    const DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_BW = "17:59";

    beforeEach(() => {
      userStore = useUserStore();
      infomanagementStore = useInfomanagementStore();
    });

    it("should_returnSpaetesteEroeffnungszeitUWValue_when_wahlbezirkArtIsUWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      const konfigKeyValueUW = generateRandomString(8);
      const konfigKeyValueBW = generateRandomString(8);
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("SPAETESTE_EROEFFNUNGSZEIT_UW")
          .wert(konfigKeyValueUW)
          .build(),
        prepareKonfigurationsparameter()
          .schluessel("SPAETESTE_EROEFFNUNGSZEIT_BW")
          .wert(konfigKeyValueBW)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.spaetesteEroeffnungsuhrzeit).toStrictEqual(
        konfigKeyValueUW
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsUWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.spaetesteEroeffnungsuhrzeit).toStrictEqual(
        DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_UW
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsUWBButKonfigKeyHasEmptyValue", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("SPAETESTE_EROEFFNUNGSZEIT_UW")
          .wert("")
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.spaetesteEroeffnungsuhrzeit).toStrictEqual(
        DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_UW
      );
    });

    it("should_returnSpaetesteEroeffnungszeitBWValue_when_wahlbezirkArtIsBWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      const konfigKeyValueBW = generateRandomString(8);
      const konfigKeyValueUW = generateRandomString(8);
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("SPAETESTE_EROEFFNUNGSZEIT_BW")
          .wert(konfigKeyValueBW)
          .build(),
        prepareKonfigurationsparameter()
          .schluessel("SPAETESTE_EROEFFNUNGSZEIT_UW")
          .wert(konfigKeyValueUW)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.spaetesteEroeffnungsuhrzeit).toStrictEqual(
        konfigKeyValueBW
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsBWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.spaetesteEroeffnungsuhrzeit).toStrictEqual(
        DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_BW
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsBWBButKonfigKeyHasEmptyValue", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("SPAETESTE_EROEFFNUNGSZEIT_BW")
          .wert("")
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.spaetesteEroeffnungsuhrzeit).toStrictEqual(
        DEFAULT_SPAETESTE_EROEFFNUNGSZEIT_BW
      );
    });
  });

  describe("fruehesteSchliessungsuhrzeit", () => {
    let userStore: ReturnType<typeof useUserStore>;
    let infomanagementStore: ReturnType<typeof useInfomanagementStore>;

    const DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_UW = "18:00";
    const DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_BW = "18:00";

    beforeEach(() => {
      userStore = useUserStore();
      infomanagementStore = useInfomanagementStore();
    });

    it("should_returnFruehesteSchliessungszeitUWValue_when_wahlbezirkArtIsUWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      const konfigKeyValueUW = generateRandomString(8);
      const konfigKeyValueBW = generateRandomString(8);
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_SCHLIESSUNGSZEIT_UW")
          .wert(konfigKeyValueUW)
          .build(),
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_SCHLIESSUNGSZEIT_BW")
          .wert(konfigKeyValueBW)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        konfigKeyValueUW
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsUWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_UW
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
        DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_UW
      );
    });

    it("should_returnFruehesteSchliessungszeitBWValue_when_wahlbezirkArtIsBWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      const konfigKeyValueBW = generateRandomString(8);
      const konfigKeyValueUW = generateRandomString(8);
      infomanagementStore.konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_SCHLIESSUNGSZEIT_BW")
          .wert(konfigKeyValueBW)
          .build(),
        prepareKonfigurationsparameter()
          .schluessel("FRUEHESTE_SCHLIESSUNGSZEIT_UW")
          .wert(konfigKeyValueUW)
          .build(),
      ];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        konfigKeyValueBW
      );
    });

    it("should_returnDefaultValue_when_wahlbezirkArtIsBWBButKonfigKeyDoesNotExists", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      infomanagementStore.konfigurationsparameter = [];

      await nextTick();

      expect(infomanagementStore.fruehesteSchliessungsuhrzeit).toStrictEqual(
        DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_BW
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
        DEFAULT_FRUEHESTE_SCHLIESSUNGSZEIT_BW
      );
    });
  });
});
