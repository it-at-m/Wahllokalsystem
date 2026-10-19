import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_PERFORMANCE_TESTS,
  mockAndStubResizeObserver,
  stubVisualViewport,
} from "@tests/utils/testutils.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
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

describe("TheStimmzettelErfassungDialog", () => {
  describe(COMPONENT_PERFORMANCE_TESTS, () => {
    mockAndStubResizeObserver();
    stubVisualViewport();

    afterEach(() => {
      document.body.innerHTML = "";
      document.head.innerHTML = "";
      vi.resetAllMocks();
    });

    performanceIt(
      "should_preloadDialogInTime_when_openedWithLargeStimmzettel",
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
                "onUpdate:modelValue": (visible: boolean | undefined) => {
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

        expect(durationInMilliseconds).toBeLessThan(3000);
        expect(document.body.textContent).toContain("Kurzbefehl");
        wrapper.unmount();
      },
      60_000
    );
  });
});

function createWahlvorschlaege(): Wahlvorschlag[] {
  const { createWahlvorschlag } = useWahlvorschlaegeTestDataFactory();
  return Array.from({ length: WAHLVORSCHLAG_COUNT }, () =>
    createWahlvorschlag()
  );
}
