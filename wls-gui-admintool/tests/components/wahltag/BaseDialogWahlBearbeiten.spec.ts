import type { WahlDTO } from "@/api/wls-clients/generated-admin-api";
import type { VueWrapper } from "@vue/test-utils";

import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import BaseButtonCancel from "@/components/common/BaseButtonCancel.vue";
import BaseButtonConfirm from "@/components/common/BaseButtonConfirm.vue";
import BaseDialogWahlBearbeiten from "@/components/wahltag/BaseDialogWahlBearbeiten.vue";
import vuetify from "@/plugins/vuetify.ts";

const wahlDto: WahlDTO = {
  wahlID: "wahl-1",
  name: "Bundestagswahl",
  reihenfolge: 1,
  waehlerverzeichnisNummer: 10,
  wahltag: "2026-09-27",
  wahlart: "BTW",
  farbe: { r: 255, g: 128, b: 0 },
};

describe("BaseDialogWahlBearbeiten.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseDialogWahlBearbeiten>>;
  vi.stubGlobal("visualViewport", new EventTarget());

  beforeEach(() => {
    wrapper = setupWrapper();
  });

  afterEach(() => {
    cleanUpWrapper(wrapper);
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderNothingVisible_when_mountedUnchanged", async (context) => {
      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderDialog_when_showWasCalled", async (context) => {
      wrapper.vm.showDialog(wahlDto);
      await wrapper.vm.$nextTick();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_hideDialog_when_hideWasCalledAfterShow", async (context) => {
      wrapper.vm.showDialog(wahlDto);
      await wrapper.vm.$nextTick();
      wrapper.vm.hideDialog();
      await wrapper.vm.$nextTick();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    describe("save", () => {
      it("should_emitSaveWithPreservedFieldsAndNumericTypes_when_saveClicked", async () => {
        wrapper.vm.showDialog(wahlDto);
        await wrapper.vm.$nextTick();

        await setFieldValue("wahl-waehlerverzeichnisnummer", "99");
        await setFieldValue("wahl-reihenfolge", "3");
        await setFieldValue("wahl-farbe-r", "10");
        await setFieldValue("wahl-farbe-g", "20");
        await setFieldValue("wahl-farbe-b", "30");

        await wrapper.findComponent(BaseButtonConfirm).trigger("click");

        const emittedSave = wrapper.emitted("save");
        expect(emittedSave).toHaveLength(1);

        const savedWahl = emittedSave?.[0]?.[0] as WahlDTO;
        // Editierbare Felder wurden übernommen und sind numerisch.
        expect(savedWahl.waehlerverzeichnisNummer).toBe(99);
        expect(savedWahl.reihenfolge).toBe(3);
        expect(savedWahl.farbe).toEqual({ r: 10, g: 20, b: 30 });
        // Nicht editierbare Felder bleiben erhalten.
        expect(savedWahl.wahlID).toBe("wahl-1");
        expect(savedWahl.name).toBe("Bundestagswahl");
        expect(savedWahl.wahlart).toBe("BTW");
        expect(savedWahl.wahltag).toBe("2026-09-27");
      });
    });

    describe("cancel", () => {
      it("should_emitCancel_when_cancelClicked", async () => {
        wrapper.vm.showDialog(wahlDto);
        await wrapper.vm.$nextTick();

        await wrapper.findComponent(BaseButtonCancel).trigger("click");

        expect(wrapper.emitted("cancel")).toEqual([[]]);
      });

      it("should_discardChanges_when_reopenedAfterEdit", async () => {
        wrapper.vm.showDialog(wahlDto);
        await wrapper.vm.$nextTick();
        await setFieldValue("wahl-waehlerverzeichnisnummer", "99");

        // Erneutes Öffnen mit dem ursprünglichen Datensatz verwirft Änderungen.
        wrapper.vm.showDialog(wahlDto);
        await wrapper.vm.$nextTick();

        await wrapper.findComponent(BaseButtonConfirm).trigger("click");

        const savedWahl = wrapper.emitted("save")?.[0]?.[0] as WahlDTO;
        expect(savedWahl.waehlerverzeichnisNummer).toBe(10);
      });
    });
  });

  async function setFieldValue(dataTest: string, value: string) {
    // Der Dialog-Inhalt wird per Teleport gerendert, daher über den
    // Komponentenbaum (findComponent) statt über das DOM des Wrappers suchen.
    const field = wrapper.findComponent(`[data-test="${dataTest}"]`);
    await field.find("input").setValue(value);
  }
});

function setupWrapper() {
  return mount(BaseDialogWahlBearbeiten, {
    attachTo: document.body,
    global: { plugins: [vuetify] },
  });
}

function cleanUpWrapper(wrapper: VueWrapper) {
  wrapper.unmount();
  document.body.innerHTML = "";
  document.head.innerHTML = "";
}
