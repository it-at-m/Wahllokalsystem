import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  mockAndStubResizeObserver,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { nextTick } from "vue";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import BaseWahlschliessungCard from "@/components/wahlhandlung/BaseWahlschliessungCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

const mockDefinitions = vi.hoisted(() => ({
  postUrnenwahlSchliessungsuhrzeit: vi.fn(),
  resetAllAnwesenheiten: vi.fn(),
}));

vi.mock("@/composables/wahlhandlung/wahlvorbereitungService", () => ({
  useWahlvorbereitungService: () => ({
    postUrnenwahlSchliessungsuhrzeit:
      mockDefinitions.postUrnenwahlSchliessungsuhrzeit,
  }),
}));

vi.mock("@/stores/wahlvorstandStore.ts", () => ({
  useWahlvorstandStore: () => ({
    resetAllAnwesenheiten: mockDefinitions.resetAllAnwesenheiten,
  }),
}));

describe("BaseWahlschliessungCard.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseWahlschliessungCard>>;

  mockAndStubResizeObserver();

  beforeEach(() => {
    const mockedNow = new Date();
    mockedNow.setHours(17, 31);
    vi.setSystemTime(mockedNow);

    wrapper = mount(BaseWahlschliessungCard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            stubActions: false,
          }),
          vuetify,
        ],
      },
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });
  afterAll(() => vi.unstubAllGlobals());

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithDisabledSaveButton_when_noUhrzeitIsEntered", async (context) => {
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit = undefined;
      const ereignisStore = useEreignisStore();
      ereignisStore.isVorfaelleMaintained = true;

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledSaveButton_when_invalidUhrzeitIsEntered", async (context) => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "18:00:00";

      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit = new Date(
        "2025-05-23T17:30:00"
      );
      const ereignisStore = useEreignisStore();
      ereignisStore.isVorfaelleMaintained = true;

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithEnabledSaveButton_when_validUhrzeitIsEntered", async (context) => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "17:00:00";

      const date = new Date("2025-05-23T17:30:00");
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit = date;
      const ereignisStore = useEreignisStore();
      ereignisStore.isVorfaelleMaintained = true;

      await flushPromises(); //update databinding and enabled button

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithErinnerungCard_VorfaelleAktualisieren_when_vorfaelleAreMaintained", async (context) => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "17:00:00";
      const date = new Date("2025-05-23T17:30:00");
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit = date;
      const ereignisStore = useEreignisStore();
      ereignisStore.isVorfaelleMaintained = true;
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [
        {
          uhrzeit: new Date("2025-05-23T17:00:00"),
          ereignisart: EreignisartEnum.Vorfall,
          beschreibung: "Testeintrag",
        },
      ];
      await flushPromises();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_render_WithErinnerungCard_VorfaelleMelden_when_vorfaelleAreNotMaintained", async (context) => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "17:00:00";
      const date = new Date("2025-05-23T17:30:00");
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit = date;
      const ereignisStore = useEreignisStore();
      ereignisStore.isVorfaelleMaintained = false;
      await flushPromises();
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithSaveButtonInLoadingState_when_isSavingIsTrue", async (context) => {
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeitIsSaving = true;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateSchliessungsuhrzeitInStore_when_validDateIsEntered", async () => {
      const wahlbezirkStore = useWahlbezirkStore();

      expect(
        wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit
      ).toBeUndefined();

      const schliessungsuhrzeitTimeInput = wrapper.findComponent(BaseTimeInput);
      const enteredTime = new Date();
      schliessungsuhrzeitTimeInput.vm.$emit("update:modelValue", enteredTime);

      expect(
        wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit?.getTime()
      ).toStrictEqual(enteredTime.getTime());
    });

    it("should_callSendSchliessungsuhrzeitAndResetAnwesenheiten_when_saveButtonIsClicked", async () => {
      const infomanagementStore = useInfomanagementStore();
      // @ts-expect-error: cannot set readonly
      infomanagementStore.fruehesteSchliessungsuhrzeit = "17:00:00";

      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit = new Date(
        "2025-05-23T17:30:00"
      );
      const ereignisStore = useEreignisStore();
      ereignisStore.isVorfaelleMaintained = true;

      await flushPromises();

      const sendUhrzeitSpy = vi.spyOn(
        wahlbezirkStore.schliessungsuhrzeitActions,
        "sendSchliessungsuhrzeit"
      );

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      await saveButton.trigger("click");

      mockDefinitions.postUrnenwahlSchliessungsuhrzeit.mockResolvedValue(
        Promise.resolve()
      );

      expect(sendUhrzeitSpy).toHaveBeenCalled();
      expect(mockDefinitions.resetAllAnwesenheiten).toHaveBeenCalled();
    });
  });
});
