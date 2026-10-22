import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  mockAndStubResizeObserver,
  stubVisualViewport,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import TheStimmzettelErfassungDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelErfassungDialog.vue";
import vuetify from "@/plugins/vuetify.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

let stimmzettelGueltigkeit = ref<StimmzettelGueltigkeitEnum>(
  StimmzettelGueltigkeitEnum.Valid
);

vi.mock("vue-router", () => ({
  useRoute: () => ({ params: { wahlId: "test-wahl" } }),
}));

vi.mock(
  "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts",
  () => ({
    useStimmzettelerfassungDialogUtils: () => ({
      stimmzettelManager: {
        hasStimmzettelBeenEdited: ref(false),
        stimmzettelBeforeEdit: ref(null),
        bearbeitenDialogStimmzettelUtils: {
          hasAnyValuesSet: ref(true),
          stimmzettel: ref({ gueltigkeit: stimmzettelGueltigkeit.value }),
        },
        getStimmzettelSnapshot: () => persistedStimmzettel,
        startNewStimmzettel: vi.fn(),
        startEditStimmzettel: vi.fn(),
      },
    }),
  })
);

const persistedStimmzettel: PersistedStimmzettel = {
  stimmzettelkennung: 1,
  teamID: "Testteam",
  wahlvorschlaege: [],
  invalideVotes: 0,
  gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
  wahlvorstandBeschlussvorschlag: [],
  systemBeschlussvorschlag: [],
  beschlussfassung: null,
};

describe("TheStimmzettelErfassungDialogSaveConfirmation", () => {
  describe(COMPONENT_EVENT_TESTS, () => {
    mockAndStubResizeObserver();
    stubVisualViewport();

    beforeEach(() => {
      stimmzettelGueltigkeit = ref<StimmzettelGueltigkeitEnum>(
        StimmzettelGueltigkeitEnum.Valid
      );
    });

    afterEach(() => {
      document.body.innerHTML = "";
      document.head.innerHTML = "";
      vi.clearAllMocks();
    });

    it("should_saveAfterConfirmation_when_stimmzettelNeedsBeschlussAndSaveAndContinueIsSelected", async () => {
      stimmzettelGueltigkeit.value =
        StimmzettelGueltigkeitEnum.BeschlussAusstehend;
      const wrapper = await createWrapper();

      await clickButton("save-and-continue");

      expect(wrapper.emitted("confirmNext")).toBeUndefined();
      expect(document.body.textContent).toContain(
        "Bitte legen Sie den Stimmzettel für die Beschlussfassung separat ab."
      );

      await clickButton("basedialog-btn-confirm");

      expect(wrapper.emitted("confirmNext")).toEqual([[persistedStimmzettel]]);
    });

    it("should_saveAfterConfirmation_when_stimmzettelNeedsBeschlussAndSaveAndCloseIsSelected", async () => {
      stimmzettelGueltigkeit.value =
        StimmzettelGueltigkeitEnum.BeschlussAusstehend;
      const wrapper = await createWrapper();

      await clickButton("save-and-close");
      await clickButton("basedialog-btn-confirm");

      expect(wrapper.emitted("confirmClose")).toEqual([[persistedStimmzettel]]);
    });

    it("should_saveImmediately_when_stimmzettelDoesNotNeedBeschluss", async () => {
      const wrapper = await createWrapper();

      await clickButton("save-and-continue");

      expect(wrapper.emitted("confirmNext")).toEqual([[persistedStimmzettel]]);
      expect(document.body.textContent).not.toContain(
        "Bitte legen Sie den Stimmzettel für die Beschlussfassung separat ab."
      );
    });
  });

  async function createWrapper() {
    const wrapper = mount(TheStimmzettelErfassungDialog, {
      attachTo: document.body,
      props: {
        wahlvorschlaege: [],
      },
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn }), vuetify],
        stubs: {
          BaseStimmzettelErfassungCardContent: {
            methods: {
              focusCommandProcessingTextField: vi.fn(),
            },
            template: "<div />",
          },
          BaseSaveButtonWithActionMenu: {
            props: ["actions", "disabled", "modelValue"],
            template: `
            <div>
              <button data-test="save-and-continue" @click="actions[0].action()" />
              <button data-test="save-and-close" @click="actions[1].action()" />
            </div>
          `,
          },
        },
      },
    });
    wrapper.vm.showDialog(1);
    await flushPromises();
    return wrapper;
  }

  async function clickButton(dataTest: string) {
    const button = document.body.querySelector(`[data-test="${dataTest}"]`);

    expect(button).not.toBeNull();
    button?.dispatchEvent(new MouseEvent("click", { bubbles: true }));
    await flushPromises();
  }
});
