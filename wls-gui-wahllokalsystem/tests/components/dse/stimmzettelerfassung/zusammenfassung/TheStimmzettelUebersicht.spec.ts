import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import { computed, ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseStimmzettelUebersichtTable from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelUebersichtTable.vue";
import TheStimmzettelUebersicht from "@/components/dse/stimmzettelerfassung/zusammenfassung/TheStimmzettelUebersicht.vue";
import vuetify from "@/plugins/vuetify.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

const mocks = vi.hoisted(() => ({
  ensureStatusInBearbeitung: vi.fn(),
  reloadTeamStatus: vi.fn(),
  sendStatusUnterbrochen: vi.fn(),
  startNewEmptyStimmzettelWithStimmzettelkennung: vi.fn(),
}));

vi.mock("vue-router", async (importOriginal) => ({
  ...(await importOriginal<typeof import("vue-router")>()),
  useRoute: () => ({
    params: { wahlId: "wahl-id", wahlbezirkId: "wahlbezirk-id" },
  }),
}));

vi.mock(
  "@/composables/dse/stimmzettelerfassung/stimmzettelErfassungViewUtils.ts",
  () => ({
    useStimmzettelErfassungViewUtils: () => ({
      teamStatus: ref({
        status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
      }),
      activeStimmzettel: ref(null),
      beendenBtnActive: computed(() => false),
      isErfassungsDialogVisible: ref(false),
      isKennungsDialogVisible: ref(false),
      isStatusLoading: ref(false),
      startenBtnActive: computed(() => true),
      unterbrechenBtnIsDisabled: computed(() => false),
      ensureStatusInBearbeitung: mocks.ensureStatusInBearbeitung,
      sendStatusUnterbrochen: mocks.sendStatusUnterbrochen,
      startNewEmptyStimmzettelWithStimmzettelkennung:
        mocks.startNewEmptyStimmzettelWithStimmzettelkennung,
      reloadTeamStatus: mocks.reloadTeamStatus,
    }),
  })
);

vi.mock("@/stores/userStore.ts", () => ({
  useUserStore: () => ({ currentUserTeamName: "Testteam" }),
}));

vi.mock("@/stores/workflowStore.ts", () => ({
  useWorkflowStore: () => ({ isElectionFinished: () => false }),
}));

describe("TheStimmzettelUebersicht", () => {
  describe("COMPONENT_RENDER_TESTS", () => {
    it("should_preloadErfassungsDialog_when_wahlvorschlaegeHaveFinishedLoading", async () => {
      const { createWahlvorschlag } = useWahlvorschlaegeTestDataFactory();
      const wrapper = mountOverview({
        isWahlvorschlaegeLoading: false,
        wahlvorschlaege: [createWahlvorschlag()],
      });

      await flushPromises();

      expect(
        wrapper.find("the-stimmzettel-erfassung-dialog-stub").exists()
      ).toBe(true);
    });

    it("should_waitWithErfassungsDialogPreload_when_wahlvorschlaegeAreLoading", async () => {
      const { createWahlvorschlag } = useWahlvorschlaegeTestDataFactory();
      const wrapper = mountOverview({
        isWahlvorschlaegeLoading: true,
        wahlvorschlaege: [createWahlvorschlag()],
      });

      await flushPromises();

      expect(
        wrapper.find("the-stimmzettel-erfassung-dialog-stub").exists()
      ).toBe(false);

      await wrapper.setProps({ isWahlvorschlaegeLoading: false });
      await flushPromises();

      expect(
        wrapper.find("the-stimmzettel-erfassung-dialog-stub").exists()
      ).toBe(true);
    });

    it("should_disableEditingAndStart_when_wahlvorschlaegeAreLoading", () => {
      const wrapper = mountOverview({
        isWahlvorschlaegeLoading: true,
        wahlvorschlaege: [],
      });

      expect(
        wrapper
          .findComponent(BaseStimmzettelUebersichtTable)
          .props("bearbeitungDisabled")
      ).toBe(true);
      expect(wrapper.findComponent(BaseTextButton).props("isDisabled")).toBe(
        true
      );
    });
  });
});

function mountOverview({
  isWahlvorschlaegeLoading,
  wahlvorschlaege,
}: {
  isWahlvorschlaegeLoading: boolean;
  wahlvorschlaege: Wahlvorschlag[];
}) {
  return mount(TheStimmzettelUebersicht, {
    props: {
      teamId: "team-id",
      stimmzettelListe: [] as PersistedStimmzettel[],
      isStimmzettelLoading: false,
      isWahlvorschlaegeLoading,
      hasStimmzettel: false,
      wahlvorschlaege,
      saveStimmzettel: vi.fn(),
      forceRefreshStimmzettel: vi.fn(),
    },
    global: {
      plugins: [vuetify],
      stubs: {
        "base-stimmzettel-uebersicht-table": true,
        "base-text-button": true,
        "base-feedback-card": true,
        "the-stimmzettelkennung-dialog": true,
        "the-stimmzettel-erfassung-dialog": true,
        "the-stimmzettelerfassung-beenden-dialog": true,
      },
    },
  });
}
