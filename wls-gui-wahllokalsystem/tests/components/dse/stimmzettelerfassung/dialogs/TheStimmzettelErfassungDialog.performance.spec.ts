import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { createTestingPinia } from "@pinia/testing";
import {
  mockAndStubResizeObserver,
  stubVisualViewport,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";
import { h, ref } from "vue";
import { VApp } from "vuetify/components";

import TheStimmzettelErfassungDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelErfassungDialog.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

vi.mock("vue-router", () => ({
  useRoute: () => ({ params: { wahlId: "performance-wahl" } }),
}));

const WAHLVORSCHLAG_COUNT = 12;
const KANDIDAT_COUNT_PER_WAHLVORSCHLAG = 60;
const NENNUNGEN_PER_KANDIDAT = 3;
const performanceIt =
  process.env.RUN_PERFORMANCE_TESTS === "true" ? it : it.skip;

describe("TheStimmzettelErfassungDialog.performance", () => {
  mockAndStubResizeObserver();
  stubVisualViewport();

  afterEach(() => {
    document.body.innerHTML = "";
    document.head.innerHTML = "";
    vi.resetAllMocks();
  });

  performanceIt(
    "should_reportPreloadedDialogOpenDuration_forLargeStimmzettel",
    async () => {
      const pinia = createTestingPinia({
        createSpy: vi.fn,
        stubActions: false,
      });
      useUserStore(pinia).user.teamName = "Testteam";

      const isDialogVisible = ref(false);
      const activeStimmzettel = ref<PersistedStimmzettel | null>(null);
      const wrapper = mount(VApp, {
        global: {
          plugins: [pinia, vuetify],
        },
        slots: {
          default: () =>
            h(TheStimmzettelErfassungDialog, {
              modelValue: isDialogVisible.value,
              "onUpdate:modelValue": (visible) => {
                isDialogVisible.value = visible ?? false;
              },
              stimmzettel: activeStimmzettel.value,
              wahlvorschlaege: createWahlvorschlaege(),
            }),
        },
      });
      await flushPromises();
      const startedAt = performance.now();
      activeStimmzettel.value = {
        stimmzettelkennung: 1,
        teamID: "Testteam",
        wahlvorschlaege: [],
        invalideVotes: 0,
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        wahlvorstandBeschlussvorschlag: [],
        systemBeschlussvorschlag: [],
        beschlussfassung: null,
      };
      isDialogVisible.value = true;
      await flushPromises();
      const durationInMilliseconds = performance.now() - startedAt;

      const kandidatCount =
        WAHLVORSCHLAG_COUNT *
        KANDIDAT_COUNT_PER_WAHLVORSCHLAG *
        NENNUNGEN_PER_KANDIDAT;
      console.debug(
        `Preloaded dialog open: ${durationInMilliseconds.toFixed(2)} ms for ${WAHLVORSCHLAG_COUNT} Wahlvorschlaege and ${kandidatCount} Kandidaturen.`
      );

      expect(document.body.textContent).toContain("Kurzbefehl");
      wrapper.unmount();
    },
    60_000
  );
});

function createWahlvorschlaege(): Wahlvorschlag[] {
  return Array.from({ length: WAHLVORSCHLAG_COUNT }, (_, index) => ({
    identifikator: `wahlvorschlag-${index + 1}`,
    ordnungszahl: index + 1,
    kurzname: `WV ${index + 1}`,
    erhaeltStimmen: true,
    kandidaten: Array.from(
      { length: KANDIDAT_COUNT_PER_WAHLVORSCHLAG },
      (_, kandidatIndex): Kandidat => ({
        identifikator: `wahlvorschlag-${index + 1}-kandidat-${kandidatIndex + 1}`,
        name: `Kandidat ${kandidatIndex + 1}`,
        listenposition: kandidatIndex + 1,
        direktkandidat: false,
        tabellenSpalteInNiederschrift: kandidatIndex + 1,
        einzelbewerber: false,
        anzahlNennungen: NENNUNGEN_PER_KANDIDAT,
      })
    ),
  }));
}
