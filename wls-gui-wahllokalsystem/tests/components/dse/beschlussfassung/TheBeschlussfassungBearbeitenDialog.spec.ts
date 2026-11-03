import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { VueWrapper } from "@vue/test-utils";
import type { Ref } from "vue";

import { createTestingPinia } from "@pinia/testing";
import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import {
  COMPONENT_PERFORMANCE_TESTS,
  COMPONENT_RENDER_TESTS,
  mockAndStubResizeObserver,
  stubVisualViewport,
} from "@tests/utils/testutils.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { h, nextTick, ref } from "vue";
import { VApp } from "vuetify/components";

import TheBeschlussfassungBearbeitenDialog from "@/components/dse/beschlussfassung/TheBeschlussfassungBearbeitenDialog.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useUserStore } from "@/stores/userStore.ts";

vi.mock("vue-router", () => ({
  useRoute: () => ({ params: { wahlId: "performance-wahl" } }),
}));

const WAHLVORSCHLAG_COUNT = 12;
const KANDIDAT_COUNT_PER_WAHLVORSCHLAG = 60;
const NENNUNGEN_PER_KANDIDAT = 3;
const performanceIt =
  process.env.RUN_PERFORMANCE_TESTS === "true" ? it : it.skip;

const { createPersistedStimmzettel } = usePersistedStimmzettelTestDataFactory();

describe("TheBeschlussfassungBearbeitenDialog", () => {
  let wrapper: VueWrapper;
  let isDialogVisible: Ref<boolean>;
  let activeStimmzettel: Ref<PersistedStimmzettel | undefined>;
  let idleCallbacks: IdleRequestCallback[];
  let requestIdleCallback: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    mockAndStubResizeObserver();
    stubVisualViewport();

    idleCallbacks = [];
    requestIdleCallback = vi.fn((callback: IdleRequestCallback) => {
      idleCallbacks.push(callback);
      return idleCallbacks.length;
    });
    vi.stubGlobal("requestIdleCallback", requestIdleCallback);
    vi.stubGlobal("cancelIdleCallback", vi.fn());

    const pinia = createTestingPinia({
      createSpy: vi.fn,
      stubActions: false,
    });
    useUserStore(pinia).user.teamName = "Testteam";

    isDialogVisible = ref(false);
    activeStimmzettel = ref<PersistedStimmzettel>();
    wrapper = mount(VApp, {
      global: {
        plugins: [pinia, vuetify],
      },
      slots: {
        default: () =>
          h(TheBeschlussfassungBearbeitenDialog, {
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
  });

  afterEach(() => {
    wrapper.unmount();
    document.body.innerHTML = "";
    document.head.innerHTML = "";
    vi.unstubAllGlobals();
    vi.resetAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_notPreloadWahlvorschlaegeAgain_when_dialogIsReopened", async () => {
      await processIdleCallbacks();
      const callbacksAfterPreload = requestIdleCallback.mock.calls.length;

      activeStimmzettel.value = createPersistedStimmzettel();
      isDialogVisible.value = true;
      await flushPromises();
      isDialogVisible.value = false;
      await flushPromises();
      isDialogVisible.value = true;
      await flushPromises();

      expect(requestIdleCallback).toHaveBeenCalledTimes(callbacksAfterPreload);
    });
  });

  describe(COMPONENT_PERFORMANCE_TESTS, () => {
    performanceIt(
      "should_preloadDialogInTime_when_openedWithLargeStimmzettel",
      async () => {
        await processIdleCallbacks();

        const startedAt = performance.now();
        activeStimmzettel.value = createPersistedStimmzettel();
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
        expect(document.body.textContent).toContain("Beschluss fassen");
      },
      60_000
    );
  });

  async function processIdleCallbacks() {
    while (idleCallbacks.length > 0) {
      const idleCallback = idleCallbacks.shift();
      idleCallback?.({ didTimeout: false, timeRemaining: () => 50 });
      await nextTick();
    }
  }
});

function createWahlvorschlaege(): Wahlvorschlag[] {
  const { createWahlvorschlag } = useWahlvorschlaegeTestDataFactory();
  return Array.from({ length: WAHLVORSCHLAG_COUNT }, () =>
    createWahlvorschlag()
  );
}
