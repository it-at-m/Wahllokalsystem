import type { VueWrapper } from "@vue/test-utils";

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

import BaseDialogWahllokalBenutzerDeleteConfirmation from "@/components/wahltag/BaseDialogWahllokalBenutzerDeleteConfirmation.vue";
import BaseStepWahllokalBenutzer from "@/components/wahltag/BaseStepWahllokalBenutzer.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  generateBenutzer: vi.fn(),
  exportBenutzer: vi.fn(),
  deleteBenutzer: vi.fn(),
}));

const isGeneratingRef = ref(false);
const isExportingRef = ref(false);
const isDeletingRef = ref(false);

vi.mock(
  import("@/composables/wahllokalbenutzer/wahllokalbenutzerService.ts"),
  () => ({
    useWahllokalBenutzerService: () => ({
      generateBenutzer: mockDefinitions.generateBenutzer,
      exportBenutzer: mockDefinitions.exportBenutzer,
      deleteBenutzer: mockDefinitions.deleteBenutzer,
      isGenerating: isGeneratingRef,
      isExporting: isExportingRef,
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

const wahltagID = "wahltagID";

describe("BaseStepWahllokalBenutzer.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseStepWahllokalBenutzer>>;
  vi.stubGlobal("visualViewport", new EventTarget());

  beforeEach(() => {
    wrapper = mount(BaseStepWahllokalBenutzer, {
      global: { plugins: [vuetify] },
      props: {
        wahltagID: wahltagID,
      },
    });
    isGeneratingRef.value = false;
    isExportingRef.value = false;
    isDeletingRef.value = false;
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
    it("should_renderAllButtons_when_mounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderLoadingButtons_when_actionsInProgress", async (context) => {
      isGeneratingRef.value = true;
      isExportingRef.value = true;
      isDeletingRef.value = true;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_triggerGenerateBenutzer_when_generateButtonWasClicked", async () => {
      const generateButton = wrapper.findComponent(
        '[data-test="generate-benutzer"]'
      );

      await generateButton.trigger("click");

      expect(mockDefinitions.generateBenutzer).toHaveBeenCalledWith(wahltagID);
    });

    it("should_triggerExportBenutzer_when_exportButtonWasClicked", async () => {
      const exportButton = wrapper.findComponent(
        '[data-test="export-benutzer"]'
      );

      await exportButton.trigger("click");

      expect(mockDefinitions.exportBenutzer).toHaveBeenCalledWith(wahltagID);
    });

    it("should_triggerDeleteBenutzer_when_deleteWasClickedAndConfirmed", async () => {
      const deleteButton = wrapper.findComponent(
        '[data-test="delete-benutzer"]'
      );

      await deleteButton.trigger("click");

      const referencedConfirmDialog = wrapper.vm.$refs
        .benutzerDeleteConfirmationDialog as InstanceType<
        typeof BaseDialogWahllokalBenutzerDeleteConfirmation
      >;
      const dialogHideSpy = vi.spyOn(referencedConfirmDialog, "hideDialog");

      referencedConfirmDialog.$emit("confirmDelete");

      await nextTick();

      expect(mockDefinitions.deleteBenutzer).toHaveBeenCalledWith(wahltagID);
      expect(dialogHideSpy).toHaveBeenCalledTimes(1);

      dialogHideSpy.mockRestore();
    });

    it("should_notTriggerDeleteBenutzer_when_deleteWasClickedButConfirmWasCanceled", async () => {
      const deleteButton = wrapper.findComponent(
        '[data-test="delete-benutzer"]'
      );

      await deleteButton.trigger("click");

      const referencedConfirmDialog = wrapper.vm.$refs
        .benutzerDeleteConfirmationDialog as InstanceType<
        typeof BaseDialogWahllokalBenutzerDeleteConfirmation
      >;
      const dialogHideSpy = vi.spyOn(referencedConfirmDialog, "hideDialog");

      referencedConfirmDialog.$emit("cancelDelete");

      await nextTick();

      expect(mockDefinitions.deleteBenutzer).toHaveBeenCalledTimes(0);
      expect(dialogHideSpy).toHaveBeenCalledTimes(1);

      dialogHideSpy.mockRestore();
    });
  });
});
