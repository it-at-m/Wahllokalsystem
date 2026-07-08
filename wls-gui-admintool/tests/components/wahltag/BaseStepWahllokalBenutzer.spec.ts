import type BaseDialogConfirmation from "@/components/wahltag/BaseDialogConfirmation.vue";
import type { VueWrapper } from "@vue/test-utils";

import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount } from "@vue/test-utils";
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

import BaseStepWahllokalBenutzer from "@/components/wahltag/BaseStepWahllokalBenutzer.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  generateBenutzer: vi.fn(),
  exportBenutzer: vi.fn(),
  deleteBenutzer: vi.fn(),
  loadBenutzer: vi.fn(),
}));

const isGeneratingRef = ref(false);
const isExportingRef = ref(false);
const isDeletingRef = ref(false);
const isLoadingRef = ref(false);

vi.mock(
  import("@/composables/wahllokalbenutzer/wahllokalbenutzerService.ts"),
  () => ({
    useWahllokalBenutzerService: () => ({
      generateBenutzer: mockDefinitions.generateBenutzer,
      exportBenutzer: mockDefinitions.exportBenutzer,
      deleteBenutzer: mockDefinitions.deleteBenutzer,
      loadBenutzer: mockDefinitions.loadBenutzer,
      isGenerating: isGeneratingRef,
      isExporting: isExportingRef,
      isDeleting: isDeletingRef,
      isLoading: isLoadingRef,
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
const csvWithUsers = "kueh-0001\nmfpz-0002";

async function mountComponent(): Promise<
  VueWrapper<InstanceType<typeof BaseStepWahllokalBenutzer>>
> {
  const wrapper = mount(BaseStepWahllokalBenutzer, {
    global: { plugins: [vuetify] },
    props: {
      wahltagId: wahltagID,
    },
  });
  // onMounted lädt bestehende Benutzer asynchron
  await flushPromises();
  return wrapper;
}

describe("BaseStepWahllokalBenutzer.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseStepWahllokalBenutzer>>;
  vi.stubGlobal("visualViewport", new EventTarget());

  beforeEach(() => {
    isGeneratingRef.value = false;
    isExportingRef.value = false;
    isDeletingRef.value = false;
    isLoadingRef.value = false;
    mockDefinitions.loadBenutzer.mockResolvedValue("");
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
    wrapper?.unmount();
    document.body.innerHTML = "";
    document.head.innerHTML = "";
  });

  afterAll(() => {
    vi.unstubAllGlobals();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderCreateButtonOnly_when_noBenutzerExist", async (context) => {
      wrapper = await mountComponent();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderOverwriteAndActions_when_benutzerExist", async (context) => {
      mockDefinitions.loadBenutzer.mockResolvedValue(csvWithUsers);
      wrapper = await mountComponent();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_loadExistingBenutzer_when_mounted", async () => {
      wrapper = await mountComponent();

      expect(mockDefinitions.loadBenutzer).toHaveBeenCalledWith(wahltagID);
    });

    it("should_showCreateLabel_when_noBenutzerExist", async () => {
      wrapper = await mountComponent();

      expect(wrapper.find('[data-test="generate-benutzer"]').text()).toContain(
        "erstellen"
      );
      expect(wrapper.find('[data-test="export-benutzer"]').exists()).toBe(
        false
      );
      expect(wrapper.find('[data-test="delete-benutzer"]').exists()).toBe(
        false
      );
      expect(
        wrapper.find('[data-test="list-wahllokalbenutzer"]').exists()
      ).toBe(false);
    });

    it("should_showOverwriteLabelAndActions_when_benutzerExist", async () => {
      mockDefinitions.loadBenutzer.mockResolvedValue(csvWithUsers);
      wrapper = await mountComponent();

      expect(wrapper.find('[data-test="generate-benutzer"]').text()).toContain(
        "überschreiben"
      );
      expect(wrapper.find('[data-test="export-benutzer"]').exists()).toBe(true);
      expect(wrapper.find('[data-test="delete-benutzer"]').exists()).toBe(true);
      expect(
        wrapper.find('[data-test="list-wahllokalbenutzer"]').exists()
      ).toBe(true);
    });

    it("should_triggerGenerateBenutzerAndShowList_when_generateClicked", async () => {
      mockDefinitions.generateBenutzer.mockResolvedValue(csvWithUsers);
      wrapper = await mountComponent();

      await wrapper
        .findComponent('[data-test="generate-benutzer"]')
        .trigger("click");
      await flushPromises();

      expect(mockDefinitions.generateBenutzer).toHaveBeenCalledWith(wahltagID);
      expect(
        wrapper.find('[data-test="list-wahllokalbenutzer"]').exists()
      ).toBe(true);
    });

    it("should_triggerExportBenutzer_when_exportClicked", async () => {
      mockDefinitions.loadBenutzer.mockResolvedValue(csvWithUsers);
      mockDefinitions.exportBenutzer.mockResolvedValue(csvWithUsers);
      wrapper = await mountComponent();

      await wrapper
        .findComponent('[data-test="export-benutzer"]')
        .trigger("click");

      expect(mockDefinitions.exportBenutzer).toHaveBeenCalledWith(wahltagID);
    });

    it("should_triggerDeleteBenutzerAndClearList_when_deleteConfirmed", async () => {
      mockDefinitions.loadBenutzer.mockResolvedValue(csvWithUsers);
      wrapper = await mountComponent();

      await wrapper
        .findComponent('[data-test="delete-benutzer"]')
        .trigger("click");

      const referencedConfirmDialog = wrapper.vm.$refs
        .benutzerDeleteConfirmationDialog as InstanceType<
        typeof BaseDialogConfirmation
      >;
      const dialogHideSpy = vi.spyOn(referencedConfirmDialog, "hideDialog");

      referencedConfirmDialog.$emit("confirmDelete");
      await flushPromises();

      expect(mockDefinitions.deleteBenutzer).toHaveBeenCalledWith(wahltagID);
      expect(dialogHideSpy).toHaveBeenCalledTimes(1);
      expect(
        wrapper.find('[data-test="list-wahllokalbenutzer"]').exists()
      ).toBe(false);

      dialogHideSpy.mockRestore();
    });

    it("should_notTriggerDeleteBenutzer_when_deleteCanceled", async () => {
      mockDefinitions.loadBenutzer.mockResolvedValue(csvWithUsers);
      wrapper = await mountComponent();

      await wrapper
        .findComponent('[data-test="delete-benutzer"]')
        .trigger("click");

      const referencedConfirmDialog = wrapper.vm.$refs
        .benutzerDeleteConfirmationDialog as InstanceType<
        typeof BaseDialogConfirmation
      >;
      const dialogHideSpy = vi.spyOn(referencedConfirmDialog, "hideDialog");

      referencedConfirmDialog.$emit("cancelDelete");
      await nextTick();

      expect(mockDefinitions.deleteBenutzer).toHaveBeenCalledTimes(0);
      expect(dialogHideSpy).toHaveBeenCalledTimes(1);
      expect(
        wrapper.find('[data-test="list-wahllokalbenutzer"]').exists()
      ).toBe(true);

      dialogHideSpy.mockRestore();
    });
  });
});
