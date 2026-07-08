import type { VueWrapper } from "@vue/test-utils";

import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
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
import { nextTick, ref } from "vue";

import BaseDialogConfirmation from "@/components/wahltag/BaseDialogConfirmation.vue";
import BaseStepWahltagInit from "@/components/wahltag/BaseStepWahltagInit.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  importWahlterminDaten: vi.fn(),
  deleteAndImportWahlterminDaten: vi.fn(),
  isLoading: true,
  isDeleting: false,
}));

const isLoadingRefDefaultValue = false;
const isLoadingRef = ref(isLoadingRefDefaultValue);

const isDeletingRefDefaultValue = false;
const isDeletingRef = ref(isDeletingRefDefaultValue);

vi.mock(
  import("@/composables/wahltermindaten/wahltermindatenService.ts"),
  () => ({
    useWahltermindatenService: () => ({
      importWahlterminDaten: mockDefinitions.importWahlterminDaten,
      deleteAndImportWahlterminDaten:
        mockDefinitions.deleteAndImportWahlterminDaten,
      isLoading: isLoadingRef,
      isDeleting: isDeletingRef,
    }),
  })
);

// Mock the ResizeObserver
const ResizeObserverMock = vi.fn(
  class MockedResizeObserverMock {
    observe = vi.fn();
    unobserve = vi.fn();
    disconnect = vi.fn();
  } as never
);

// Stub the global ResizeObserver
vi.stubGlobal("ResizeObserver", ResizeObserverMock);

const { prepareWahltagEvent } = useWahltagTestDataFactory();

describe("BaseStepWahltagInit.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseStepWahltagInit>>;
  vi.stubGlobal("visualViewport", new EventTarget());

  beforeEach(() => {
    wrapper = mount(BaseStepWahltagInit, {
      global: { plugins: [vuetify] },
      props: {
        wahltagEvent: prepareWahltagEvent().build(),
      },
    });
    isDeletingRef.value = isDeletingRefDefaultValue;
    isLoadingRef.value = isLoadingRefDefaultValue;
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

  describe(COMPONENT_RENDER_TESTS, () => {
    describe("wahlterminDatenExists is false", () => {
      it("should_renderCreateButton_when_loadingIsFalse", async (context) => {
        await wrapper.setProps({
          wahlterminDatenExists: false,
        });

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderCreateButton_when_loadingIsTrue", async (context) => {
        await wrapper.setProps({
          wahlterminDatenExists: false,
        });
        isLoadingRef.value = true;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });
    });

    describe("wahlterminDatenExists is true", () => {
      it("should_renderOverrideButton_when_loadingIsFalse", async (context) => {
        await wrapper.setProps({
          wahlterminDatenExists: true,
        });

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderOverrideButton_when_loadingIsTrue", async (context) => {
        await wrapper.setProps({
          wahlterminDatenExists: true,
        });
        isLoadingRef.value = true;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });

      it("should_renderOverrideButton_when_deletingIsTrue", async (context) => {
        await wrapper.setProps({
          wahlterminDatenExists: true,
        });
        isDeletingRef.value = true;

        await nextTick();

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });
    });

    describe("wahlterminDatenExists is undefined", () => {
      it("should_renderTextOnly_when_wahltermindatenExistsIsUndefined", async (context) => {
        await wrapper.setProps({
          wahlterminDatenExists: undefined,
        });

        await expect(wrapper.html()).toMatchFileSnapshot(
          getSnapshotFilename(context)
        );
      });
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
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

      expect(wrapper.emitted()).toHaveProperty("importWahlterminDatenDone");
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
        typeof BaseDialogConfirmation
      >;
      const dialogHideSpy = vi.spyOn(referencedConfirmDialog, "hideDialog");

      referencedConfirmDialog.$emit("confirmDelete");

      await nextTick();

      expect(
        mockDefinitions.deleteAndImportWahlterminDaten
      ).toHaveBeenCalledWith(wahltagEvent.wahltagID);

      expect(dialogHideSpy).toHaveBeenCalledTimes(1);

      expect(wrapper.emitted()).toHaveProperty("importWahlterminDatenDone");

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
        typeof BaseDialogConfirmation
      >;
      const dialogHideSpy = vi.spyOn(referencedConfirmDialog, "hideDialog");
      referencedConfirmDialog.$emit("cancelDelete");

      await nextTick();

      expect(
        mockDefinitions.deleteAndImportWahlterminDaten
      ).toHaveBeenCalledTimes(0);

      expect(dialogHideSpy).toHaveBeenCalledTimes(1);

      expect(wrapper.emitted()).not.toHaveProperty("importWahlterminDatenDone");

      dialogHideSpy.mockRestore();
    });
  });
});
