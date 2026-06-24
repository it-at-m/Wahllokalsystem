import type { WahlDTO } from "@/api/wls-clients/generated-admin-api";
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
import { ref } from "vue";

import BaseDialogWahlBearbeiten from "@/components/wahltag/BaseDialogWahlBearbeiten.vue";
import BaseListWahlen from "@/components/wahltag/BaseListWahlen.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
  updateWahlen: vi.fn(),
}));

const isLoadingRef = ref(false);
const isSavingRef = ref(false);

vi.mock(import("@/composables/wahlen/wahlenService.ts"), () => ({
  useWahlenService: () => ({
    getWahlen: mockDefinitions.getWahlen,
    updateWahlen: mockDefinitions.updateWahlen,
    isLoading: isLoadingRef,
    isSaving: isSavingRef,
  }),
}));

const ResizeObserverMock = vi.fn(
  class MockedResizeObserverMock {
    observe = vi.fn();
    unobserve = vi.fn();
    disconnect = vi.fn();
  } as never
);
vi.stubGlobal("ResizeObserver", ResizeObserverMock);

const wahltagID = "wahltagID";
const wahl1: WahlDTO = {
  wahlID: "w1",
  name: "Bundestagswahl",
  reihenfolge: 1,
  waehlerverzeichnisNummer: 10,
  wahltag: "2026-09-27",
  wahlart: "BTW",
  farbe: { r: 255, g: 0, b: 0 },
};
const wahl2: WahlDTO = {
  wahlID: "w2",
  name: "Europawahl",
  reihenfolge: 2,
  waehlerverzeichnisNummer: 20,
  wahltag: "2026-09-27",
  wahlart: "EUW",
  farbe: { r: 0, g: 0, b: 255 },
};

async function mountComponent(): Promise<
  VueWrapper<InstanceType<typeof BaseListWahlen>>
> {
  const wrapper = mount(BaseListWahlen, {
    global: { plugins: [vuetify] },
    props: { wahltagId: wahltagID },
  });
  await flushPromises();
  return wrapper;
}

describe("BaseListWahlen.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseListWahlen>>;
  vi.stubGlobal("visualViewport", new EventTarget());

  beforeEach(() => {
    isLoadingRef.value = false;
    isSavingRef.value = false;
    mockDefinitions.getWahlen.mockResolvedValue([]);
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
    it("should_renderEmptyState_when_noWahlen", async (context) => {
      wrapper = await mountComponent();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderTable_when_wahlenExist", async (context) => {
      mockDefinitions.getWahlen.mockResolvedValue([wahl1, wahl2]);
      wrapper = await mountComponent();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_loadWahlen_when_mounted", async () => {
      wrapper = await mountComponent();

      expect(mockDefinitions.getWahlen).toHaveBeenCalledWith(wahltagID);
    });

    it("should_openDialog_when_editClicked", async () => {
      mockDefinitions.getWahlen.mockResolvedValue([wahl1]);
      wrapper = await mountComponent();

      const referencedDialog = wrapper.vm.$refs
        .wahlBearbeitenDialog as InstanceType<typeof BaseDialogWahlBearbeiten>;
      const showSpy = vi.spyOn(referencedDialog, "showDialog");

      await wrapper.find('[data-test="edit-wahl"]').trigger("click");

      expect(showSpy).toHaveBeenCalledWith(wahl1);
      showSpy.mockRestore();
    });

    it("should_updateWahlenWithFullArrayAndMergedEdit_when_dialogSaved", async () => {
      mockDefinitions.getWahlen.mockResolvedValue([wahl1, wahl2]);
      mockDefinitions.updateWahlen.mockResolvedValue(undefined);
      wrapper = await mountComponent();

      const referencedDialog = wrapper.vm.$refs
        .wahlBearbeitenDialog as InstanceType<typeof BaseDialogWahlBearbeiten>;
      const hideSpy = vi.spyOn(referencedDialog, "hideDialog");

      const editedWahl1: WahlDTO = { ...wahl1, waehlerverzeichnisNummer: 99 };
      referencedDialog.$emit("save", editedWahl1);
      await flushPromises();

      expect(mockDefinitions.updateWahlen).toHaveBeenCalledWith(wahltagID, [
        editedWahl1,
        wahl2,
      ]);
      expect(hideSpy).toHaveBeenCalledTimes(1);
      hideSpy.mockRestore();
    });

    it("should_keepDialogOpenAndNotApplyEdit_when_saveFails", async () => {
      mockDefinitions.getWahlen.mockResolvedValue([wahl1, wahl2]);
      mockDefinitions.updateWahlen.mockRejectedValue(new Error("boom"));
      wrapper = await mountComponent();

      const referencedDialog = wrapper.vm.$refs
        .wahlBearbeitenDialog as InstanceType<typeof BaseDialogWahlBearbeiten>;
      const hideSpy = vi.spyOn(referencedDialog, "hideDialog");

      referencedDialog.$emit("save", {
        ...wahl1,
        waehlerverzeichnisNummer: 99,
      });
      await flushPromises();

      expect(mockDefinitions.updateWahlen).toHaveBeenCalledTimes(1);
      // Dialog bleibt offen, damit die Eingaben nicht verloren gehen.
      expect(hideSpy).not.toHaveBeenCalled();
      hideSpy.mockRestore();
    });

    it("should_ignoreSecondSave_when_aSaveIsAlreadyInProgress", async () => {
      mockDefinitions.getWahlen.mockResolvedValue([wahl1, wahl2]);
      mockDefinitions.updateWahlen.mockResolvedValue(undefined);
      wrapper = await mountComponent();

      // Laufende Speicherung simulieren.
      isSavingRef.value = true;

      const referencedDialog = wrapper.vm.$refs
        .wahlBearbeitenDialog as InstanceType<typeof BaseDialogWahlBearbeiten>;
      referencedDialog.$emit("save", {
        ...wahl1,
        waehlerverzeichnisNummer: 99,
      });
      await flushPromises();

      // Kein zweiter Speichervorgang, der den laufenden überschreiben würde.
      expect(mockDefinitions.updateWahlen).toHaveBeenCalledTimes(0);
    });

    it("should_notUpdateWahlen_when_dialogCanceled", async () => {
      mockDefinitions.getWahlen.mockResolvedValue([wahl1]);
      wrapper = await mountComponent();

      const referencedDialog = wrapper.vm.$refs
        .wahlBearbeitenDialog as InstanceType<typeof BaseDialogWahlBearbeiten>;
      const hideSpy = vi.spyOn(referencedDialog, "hideDialog");

      referencedDialog.$emit("cancel");
      await flushPromises();

      expect(mockDefinitions.updateWahlen).toHaveBeenCalledTimes(0);
      expect(hideSpy).toHaveBeenCalledTimes(1);
      hideSpy.mockRestore();
    });
  });
});
