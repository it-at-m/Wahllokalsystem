import type { VueWrapper } from "@vue/test-utils";

import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import { getSnapshotFilename } from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
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

import BaseDialogWahltagOverrideWahlterminConfirmation from "@/components/wahltag/BaseDialogWahltagOverrideWahlterminConfirmation.vue";
import BaseStepWahltagInit from "@/components/wahltag/BaseStepWahltagInit.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  importWahlterminDaten: vi.fn(),
  deleteAndImportWahlterminDaten: vi.fn(),
  isLoading: false,
  istDeleting: false,
}));

vi.mock("@/composables/wahltermindaten/wahltermindatenService.ts", () => ({
  useWahltermindatenService: () => ({
    importWahlterminDaten: mockDefinitions.importWahlterminDaten,
    deleteAndImportWahlterminDaten:
      mockDefinitions.deleteAndImportWahlterminDaten,
    isLoading: mockDefinitions.isLoading,
    istDeleting: mockDefinitions.istDeleting,
  }),
}));

// Mock the ResizeObserver
const ResizeObserverMock = vi.fn(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}));

// Stub the global ResizeObserver
vi.stubGlobal("ResizeObserver", ResizeObserverMock);

const { prepareWahltagEvent } = useWahltagTestDataFactory();

describe("BaseStepWahltagInit.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseStepWahltagInit>>;

  beforeEach(() => {
    wrapper = mount(BaseStepWahltagInit, {
      global: { plugins: [vuetify] },
      props: {
        wahltagEvent: prepareWahltagEvent().build(),
      },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
    wrapper.unmount();
    document.body.innerHTML = "";
    document.head.innerHTML = "";
  });

  afterAll(() => {
    vi.unstubAllGlobals();
  });

  describe("visual logic", () => {
    it("should_showOverrideDialog_when_overrideIsClicked", async (context) => {
      await wrapper.setProps({
        wahlterminDatenExists: true,
      });

      await wrapper.findComponent('[data-test="override"]').trigger("click");

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderOverrideButton_when_wahltermindatenExists", async (context) => {
      await wrapper.setProps({
        wahlterminDatenExists: true,
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderCreateButton_when_wahltermindatenExists", async (context) => {
      await wrapper.setProps({
        wahlterminDatenExists: false,
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderTextOnly_when_itIsUndefinedIfWahltermindatenExists", async (context) => {
      await wrapper.setProps({
        wahlterminDatenExists: undefined,
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe("behaviour logic", () => {
    it("should_triggerImportWahlterminDaten_when_createButtonWasClicked", async () => {
      const wahltagEvent = prepareWahltagEvent().build();
      await wrapper.setProps({
        wahlterminDatenExists: false,
        wahltagEvent: wahltagEvent,
      });

      const createButton = wrapper.findComponent('[data-test="create"]');

      await createButton.trigger("click");

      expect(mockDefinitions.importWahlterminDaten).toHaveBeenCalledWith(
        wahltagEvent.wahltagID
      );
    });

    it("should_triggerDeleteAndImportWahlterminDaten_when_overrideWasClickedAndConfirmed", async () => {
      const wahltagEvent = prepareWahltagEvent().build();
      await wrapper.setProps({
        wahlterminDatenExists: true,
        wahltagEvent: wahltagEvent,
      });

      const overrideButton = wrapper.findComponent('[data-test="override"]');

      await overrideButton.trigger("click");

      const referencedConfirmDialog = wrapper.vm.$refs
        .wahltageOverrideConfirmationDialog as InstanceType<
        typeof BaseDialogWahltagOverrideWahlterminConfirmation
      >;
      const dialogHideSpy = vi.spyOn(referencedConfirmDialog, "hide");

      referencedConfirmDialog.$emit("confirmDelete");

      await nextTick();

      expect(
        mockDefinitions.deleteAndImportWahlterminDaten
      ).toHaveBeenCalledWith(wahltagEvent.wahltagID);

      expect(dialogHideSpy).toHaveBeenCalledTimes(1);

      dialogHideSpy.mockRestore();
    });

    it("should_notTriggerDeleteAndImportWahlterminDaten_when_overrideWasClickedButConfirmWasCanceled", async () => {
      const wahltagEvent = prepareWahltagEvent().build();
      await wrapper.setProps({
        wahlterminDatenExists: true,
        wahltagEvent: wahltagEvent,
      });

      const overrideButton = wrapper.findComponent('[data-test="override"]');

      await overrideButton.trigger("click");

      const referencedConfirmDialog = wrapper.vm.$refs
        .wahltageOverrideConfirmationDialog as InstanceType<
        typeof BaseDialogWahltagOverrideWahlterminConfirmation
      >;
      const dialogHideSpy = vi.spyOn(referencedConfirmDialog, "hide");
      referencedConfirmDialog.$emit("cancelDelete");

      await nextTick();

      expect(
        mockDefinitions.deleteAndImportWahlterminDaten
      ).toHaveBeenCalledTimes(0);

      expect(dialogHideSpy).toHaveBeenCalledTimes(1);

      dialogHideSpy.mockRestore();
    });
  });
});
