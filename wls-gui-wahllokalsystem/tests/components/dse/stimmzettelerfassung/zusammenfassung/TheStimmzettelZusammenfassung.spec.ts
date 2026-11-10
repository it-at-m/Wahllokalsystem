import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { COMPONENT_EVENT_TESTS } from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import TheStimmzettelZusammenfassung from "@/components/dse/stimmzettelerfassung/zusammenfassung/TheStimmzettelZusammenfassung.vue";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  buildNiederschriftTemplateFromDataBWB: vi.fn(),
  buildNiederschriftTemplateFromDataUWB: vi.fn(),
  getInitialStatus: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  logError: vi.fn(),
  prepareDataForNiederschriftDruck: vi.fn(),
  printInWindow: vi.fn(),
  useMbwNiederschriftDruckService: vi.fn(),
}));

const currentUserWahlbezirksArt = ref(WahlbezirksArtEnum.UWB);
const hasRoleSchriftfuehrung = ref(false);

vi.mock("pinia", async (importOriginal) => ({
  ...(await importOriginal<typeof import("pinia")>()),
  storeToRefs: () => ({ currentUserWahlbezirksArt, hasRoleSchriftfuehrung }),
}));
vi.mock("vue-router", async (importOriginal) => ({
  ...(await importOriginal<typeof import("vue-router")>()),
  useRoute: () => ({
    params: { wahlId: "wahl-id", wahlbezirkId: "wahlbezirk-id" },
  }),
}));
vi.mock("@/stores/userStore.ts", () => ({ useUserStore: vi.fn() }));
vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
    },
  }),
}));
vi.mock("@/composables/common/logging.ts", () => ({
  useLogging: () => ({ logError: mockDefinitions.logError }),
}));
vi.mock("@/composables/drucken/commonPrintService.ts", () => ({
  useCommonPrintService: () => ({
    printInWindow: mockDefinitions.printInWindow,
  }),
}));
vi.mock("@/composables/ergebnismeldung/common/statusUtils.ts", () => ({
  useStatusUtils: () => ({
    getInitialStatus: mockDefinitions.getInitialStatus,
  }),
}));
vi.mock(
  "@/composables/ergebnismeldung/MBW/mbwNiederschriftDruckService.ts",
  () => ({
    useMbwNiederschriftDruckService:
      mockDefinitions.useMbwNiederschriftDruckService,
  })
);
vi.mock("@/composables/ergebnismeldung/MBW/niederschriftDruckUWB.ts", () => ({
  useNiederschriftDruckUWB: () => ({
    buildNiederschriftTemplateFromData:
      mockDefinitions.buildNiederschriftTemplateFromDataUWB,
  }),
}));
vi.mock("@/composables/ergebnismeldung/MBW/niederschriftDruckBWB.ts", () => ({
  useNiederschriftDruckBWB: () => ({
    buildNiederschriftTemplateFromData:
      mockDefinitions.buildNiederschriftTemplateFromDataBWB,
  }),
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("@/composables/dse/mbwStimmzettelFilterService.ts", () => ({
  useMbwStimmzettelFilterService: () => ({
    stapelA: ref([]),
    stapelASumGroupedByWahlvorschlag: ref(new Map()),
    stapelB: ref([]),
    stapelBSumGroupedByWahlvorschlag: ref(new Map()),
    stapelC: ref([]),
  }),
}));
vi.mock(
  "@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper.ts",
  () => ({
    useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper: () => ({
      wahlvorschlaegeErgebnisseStapelAAndB: ref([]),
    }),
  })
);
vi.mock(
  "@/composables/dse/stimmzettelerfassung/stimmzettelZusammenfassungUtils.ts",
  () => ({
    useStimmzettelZusammenfassungUtils: () => ({
      wahlvorschlaegeWithKandidatenErgebnissen: ref([]),
    }),
  })
);

describe("TheStimmzettelZusammenfassung", () => {
  const wahl = {} as Wahl;
  const status = { status: "initial" };
  const stimmzettelListe = [] as PersistedStimmzettel[];

  describe(COMPONENT_EVENT_TESTS, () => {
    beforeEach(() => {
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.UWB;
      mockDefinitions.getInitialStatus.mockReturnValue(status);
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(wahl);
      mockDefinitions.useMbwNiederschriftDruckService.mockReturnValue({
        prepareDataForNiederschriftDruck:
          mockDefinitions.prepareDataForNiederschriftDruck,
      });
      mockDefinitions.prepareDataForNiederschriftDruck.mockResolvedValue({});
    });

    afterEach(() => {
      vi.clearAllMocks();
    });

    it("should_printUwbSummary_when_printButtonIsClicked", async () => {
      const template = "uwb-template";
      mockDefinitions.buildNiederschriftTemplateFromDataUWB.mockReturnValue(
        template
      );
      const wrapper = mountSummary();

      await wrapper.find("button").trigger("click");

      expect(
        mockDefinitions.prepareDataForNiederschriftDruck
      ).toHaveBeenCalledWith(status, wahl);
      expect(
        mockDefinitions.buildNiederschriftTemplateFromDataUWB
      ).toHaveBeenCalledWith({});
      expect(mockDefinitions.printInWindow).toHaveBeenCalledWith(template);
    });

    it("should_printBwbSummary_when_printButtonIsClicked", async () => {
      // @ts-expect-error: cannot set readonly
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.BWB;
      const template = "bwb-template";
      mockDefinitions.buildNiederschriftTemplateFromDataBWB.mockReturnValue(
        template
      );
      const wrapper = mountSummary();

      await wrapper.find("button").trigger("click");

      expect(
        mockDefinitions.buildNiederschriftTemplateFromDataBWB
      ).toHaveBeenCalledWith({});
      expect(mockDefinitions.printInWindow).toHaveBeenCalledWith(template);
    });

    it("should_showErrorNotification_when_printingFails", async () => {
      mockDefinitions.prepareDataForNiederschriftDruck.mockRejectedValue(
        new Error("failed")
      );
      const wrapper = mountSummary();

      await wrapper.find("button").trigger("click");

      expect(mockDefinitions.logError).toHaveBeenCalledOnce();
      expect(mockDefinitions.addNotification).toHaveBeenCalledWith(
        "Fehler beim Drucken der Zusammenfassung",
        UserNotificationCategoryEnum.ERROR
      );
    });

    function mountSummary() {
      return mount(TheStimmzettelZusammenfassung, {
        props: { stimmzettelListe, wahlvorschlaege: [] },
        global: {
          stubs: {
            "base-card-ungueltige-stimmen-anzeigen": true,
            "base-card-wahlvorschlaege-kandidatenstimmen-anzeigen": true,
            "base-text-button": { template: "<button><slot /></button>" },
            "the-m-b-w-gueltige-stimmen-anzeigen-niederschrift-table": true,
            "the-m-b-w-waehler-anzeigen-card": true,
            "the-m-b-w-wahlberechtigte-anzeigen-card": true,
            "v-card": { template: "<div><slot /></div>" },
            "v-card-actions": { template: "<div><slot /></div>" },
            "v-card-subtitle": { template: "<div><slot /></div>" },
            "v-card-text": { template: "<div><slot /></div>" },
            "v-card-title": { template: "<div><slot /></div>" },
          },
        },
      });
    }
  });
});
