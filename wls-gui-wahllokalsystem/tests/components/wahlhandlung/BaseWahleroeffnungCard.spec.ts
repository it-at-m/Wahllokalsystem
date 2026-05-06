import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import BaseWahleroeffnungCard from "@/components/wahlhandlung/BaseWahleroeffnungCard.vue";
import router from "@/plugins/router.ts";
import vuetify from "@/plugins/vuetify.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    isZuSpaet: boolean;
    onConfirmBegruendung(begruendung: string): void;
  }
}

const mockDefinitions = vi.hoisted(() => ({
  postEroeffnungsuhrzeit: vi.fn(),
  saveEreignisse: vi.fn(),
  routerPush: vi.fn(),
}));

vi.mock("@/composables/vorfaelleundvorkommnisse/ereignisService.ts", () => ({
  useEreignisService: () => ({
    saveEreignisse: mockDefinitions.saveEreignisse,
  }),
}));

vi.mock("@/composables/wahlhandlung/wahlvorbereitungService", () => ({
  useWahlvorbereitungService: () => ({
    postEroeffnungsuhrzeit: mockDefinitions.postEroeffnungsuhrzeit,
  }),
}));

router.push = mockDefinitions.routerPush;

describe("BaseWahleroeffnungCard.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseWahleroeffnungCard>>;

  vi.stubGlobal("visualViewport", new EventTarget());
  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeEach(() => {
    const mockedNow = new Date();
    mockedNow.setHours(7, 30);
    vi.useFakeTimers({
      now: mockedNow,
    });

    wrapper = mount(BaseWahleroeffnungCard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            stubActions: false,
          }),
          vuetify,
        ],
      },
      slots: {
        userHint: "content for slot userHint",
      },
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithDisabledSaveButton_when_noUhrzeitIsEntered", async (context) => {
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit = undefined;

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledSaveButton_when_invalidUhrzeitIsEntered", async (context) => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteEroeffnungsuhrzeit = "07:00:00";
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "08:00:00";

      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit = new Date(
        "2025-05-23T06:30:00"
      );

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithEnabledSaveButton_when_validUhrzeitIsEntered", async (context) => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteEroeffnungsuhrzeit = "07:00:00";
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "08:00:00";

      const date = new Date("2025-05-23T07:30:00");
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit = date;

      await flushPromises(); // update data binding and enable button

      // @ts-expect-error: cannot set readonly
      infomanagementStore.spaetesteEroeffnungsuhrzeit = "07:50:00";

      expect(wrapper.vm.isZuSpaet).toBe(false);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithSaveButtonInLoadingState_when_isSavingIsTrue", async (context) => {
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeitIsSaving = true;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateEroeffnungsuhrzeitInStore_when_validDateIsEntered", async () => {
      const wahlbezirkStore = useWahlbezirkStore();

      expect(
        wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit
      ).toBeUndefined();

      const eroeffnungsuhrzeitTimeInput = wrapper.findComponent(BaseTimeInput);
      const enteredTime = new Date();
      await eroeffnungsuhrzeitTimeInput.setValue(enteredTime);

      expect(
        wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit?.getTime()
      ).toStrictEqual(enteredTime.getTime());
    });

    it("should_callSendEroeffnungsuhrzeit_when_saveButtonIsClicked", async () => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteEroeffnungsuhrzeit = "07:00:00";
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "08:00:00";

      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit = new Date(
        "2025-05-23T07:30:00"
      );

      await flushPromises();

      const sendUhrzeitSpy = vi.spyOn(
        wahlbezirkStore.eroeffnungsuhrzeitActions,
        "sendEroeffnungsuhrzeit"
      );

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      await saveButton.trigger("click");

      mockDefinitions.postEroeffnungsuhrzeit.mockResolvedValue(
        Promise.resolve()
      );

      expect(sendUhrzeitSpy).toHaveBeenCalled();
    });

    it("should_setIsZuSpaet_when_eroeffnungsuhrzeitIsToLate", async () => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteEroeffnungsuhrzeit = "07:00:00";
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "08:00:00";

      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit = new Date();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit.setHours(
        7,
        30,
        0
      );

      await flushPromises();

      // @ts-expect-error: cannot set readonly
      infomanagementStore.spaetesteEroeffnungsuhrzeit = "07:10:00";

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      await saveButton.trigger("click");

      expect(wrapper.vm.isZuSpaet).toBe(true);
    });

    it("should_resetEroeffnungsuhrzeit_when_cancelButtonClicked", async () => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteEroeffnungsuhrzeit = "07:00:00";
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "08:00:00";

      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit = new Date();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit.setHours(
        7,
        30,
        0
      );

      await flushPromises();

      // @ts-expect-error: cannot set readonly
      infomanagementStore.spaetesteEroeffnungsuhrzeit = "07:10:00";

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      await saveButton.trigger("click");

      const cancelButton = wrapper.findComponent(
        '[data-test="basedialogbegruendung-btn-cancel"]'
      );
      await cancelButton.trigger("click");

      expect(wrapper.vm.isZuSpaet).toBe(false);
      expect(
        wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit
      ).toBeUndefined();
    });

    it("should_createEreignis_when_confirmButtonClickedWithBegruendung", async () => {
      const begruendung = "Begründung, weil zu spät eröffnet";
      const expectedBegruendung =
        "Verspätete Eröffnung: Begründung, weil zu spät eröffnet";

      const ereignisStore = useEreignisStore();
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteEroeffnungsuhrzeit = "07:00:00";
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "08:00:00";

      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit = new Date();
      wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit.setHours(
        7,
        30,
        0
      );

      await flushPromises();

      // @ts-expect-error: cannot set readonly
      infomanagementStore.spaetesteEroeffnungsuhrzeit = "07:10:00";

      expect(ereignisStore.wahlbezirkEreignisse.ereigniseintraege.length).toBe(
        0
      );

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      await saveButton.trigger("click");

      await wrapper
        .findComponent('[data-test="basedialogbegruendung-textarea"]')
        .setValue(begruendung);

      const confirmButton = wrapper.findComponent(
        '[data-test="basedialogbegruendung-btn-confirm"]'
      );
      await confirmButton.trigger("click");

      expect(ereignisStore.wahlbezirkEreignisse.ereigniseintraege.length).toBe(
        1
      );
      if (ereignisStore.wahlbezirkEreignisse.ereigniseintraege) {
        expect(
          ereignisStore.wahlbezirkEreignisse.ereigniseintraege[0]?.beschreibung
        ).toBe(expectedBegruendung);
      }
      expect(mockDefinitions.saveEreignisse).toHaveBeenCalled();
    });
  });
});
