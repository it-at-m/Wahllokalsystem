import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { App } from "vue";

import { createTestingPinia } from "@pinia/testing";
import { useDseStimmzettelTestDataFactory } from "@tests/utils/dse/DseStimmzettelTestDataFactory.ts";
import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { withSetup } from "@tests/utils/testutils.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  useStimmzettelManager: vi.fn(),
}));

vi.mock("@/composables/dse/stimmzettelerfassung/stimmzettelManager.ts", () => ({
  useStimmzettelManager: mockDefinitions.useStimmzettelManager,
}));

vi.mock("vue-router", () => ({
  useRoute: () => ({ params: { wahlId: "wahl-1" } }),
}));

describe("useStimmzettelerfassungDialogUtils", () => {
  const { createDseStimmzettel } = useDseStimmzettelTestDataFactory();
  const { createPersistedStimmzettel } =
    usePersistedStimmzettelTestDataFactory();
  const teamName = "Testteam";
  const emit = vi.fn();
  const stimmzettelManager = {
    stimmzettelBeforeEdit: ref<PersistedStimmzettel | null>(null),
    hasStimmzettelBeenEdited: ref(false),
    startNewStimmzettel: vi.fn(),
    startEditStimmzettel: vi.fn(),
    getStimmzettelSnapshot: vi.fn(),
    bearbeitenDialogStimmzettelUtils: {
      hasAnyValuesSet: ref(false),
      stimmzettel: ref(createDseStimmzettel()),
      resetStimmzettelAndHistory: vi.fn(),
    },
  };
  let unitUnderTest: ReturnType<typeof useStimmzettelerfassungDialogUtils>;
  let app: App;

  beforeEach(() => {
    createTestingPinia({ createSpy: vi.fn });
    useUserStore().user.teamName = teamName;
    stimmzettelManager.stimmzettelBeforeEdit.value = null;
    stimmzettelManager.hasStimmzettelBeenEdited.value = false;
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value = false;
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value =
      createDseStimmzettel();
    mockDefinitions.useStimmzettelManager.mockReturnValue(stimmzettelManager);
    [unitUnderTest, app] = withSetup(() =>
      useStimmzettelerfassungDialogUtils([], emit)
    );
  });

  afterEach(() => {
    app.unmount();
    vi.clearAllMocks();
  });

  describe("isSaveDisabled", () => {
    it.each([
      [StimmzettelGueltigkeitEnum.Valid, true],
      [StimmzettelGueltigkeitEnum.Invalid, true],
      [StimmzettelGueltigkeitEnum.BeschlussAusstehend, false],
      [StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag, false],
      [StimmzettelGueltigkeitEnum.Leer, false],
    ] as const)(
      "should_checkGueltigkeit%sAndReturn%s_when_noValuesAreSet",
      (gueltigkeit, expected) => {
        stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value.gueltigkeit =
          gueltigkeit;

        expect(unitUnderTest.isSaveDisabled.value).toBe(expected);
      }
    );

    it("should_enableSave_when_newStimmzettelHasValuesSet", () => {
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value = true;
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value.gueltigkeit =
        StimmzettelGueltigkeitEnum.Valid;

      expect(unitUnderTest.isSaveDisabled.value).toBe(false);
    });

    it.each([
      StimmzettelGueltigkeitEnum.Valid,
      StimmzettelGueltigkeitEnum.Invalid,
      StimmzettelGueltigkeitEnum.BeschlussAusstehend,
      StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag,
      StimmzettelGueltigkeitEnum.Leer,
    ])(
      "should_disableSave_when_existingStimmzettelWithGueltigkeit%sHasValuesButIsUnchanged",
      (gueltigkeit) => {
        stimmzettelManager.stimmzettelBeforeEdit.value =
          createPersistedStimmzettel();
        stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value = true;
        stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value.gueltigkeit =
          gueltigkeit;

        expect(unitUnderTest.isSaveDisabled.value).toBe(true);
      }
    );

    it("should_enableSave_when_existingStimmzettelHasBeenEditedAndHasValuesSet", () => {
      stimmzettelManager.stimmzettelBeforeEdit.value =
        createPersistedStimmzettel();
      stimmzettelManager.hasStimmzettelBeenEdited.value = true;
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value = true;
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value.gueltigkeit =
        StimmzettelGueltigkeitEnum.Valid;

      expect(unitUnderTest.isSaveDisabled.value).toBe(false);
    });
  });

  describe("isResetDisabled", () => {
    it.each([
      {
        hasStimmzettelBeforeEdit: false,
        hasAnyValuesSet: false,
        hasStimmzettelBeenEdited: false,
        expected: true,
      },
      {
        hasStimmzettelBeforeEdit: false,
        hasAnyValuesSet: false,
        hasStimmzettelBeenEdited: true,
        expected: true,
      },
      {
        hasStimmzettelBeforeEdit: false,
        hasAnyValuesSet: true,
        hasStimmzettelBeenEdited: false,
        expected: false,
      },
      {
        hasStimmzettelBeforeEdit: false,
        hasAnyValuesSet: true,
        hasStimmzettelBeenEdited: true,
        expected: false,
      },
      {
        hasStimmzettelBeforeEdit: true,
        hasAnyValuesSet: false,
        hasStimmzettelBeenEdited: false,
        expected: true,
      },
      {
        hasStimmzettelBeforeEdit: true,
        hasAnyValuesSet: false,
        hasStimmzettelBeenEdited: true,
        expected: false,
      },
      {
        hasStimmzettelBeforeEdit: true,
        hasAnyValuesSet: true,
        hasStimmzettelBeenEdited: false,
        expected: true,
      },
      {
        hasStimmzettelBeforeEdit: true,
        hasAnyValuesSet: true,
        hasStimmzettelBeenEdited: true,
        expected: false,
      },
    ])(
      "should_return$expected_when_hasStimmzettelBeforeEditIs$hasStimmzettelBeforeEditAndHasAnyValuesSetIs$hasAnyValuesSetAndHasStimmzettelBeenEditedIs$hasStimmzettelBeenEdited",
      ({
        hasStimmzettelBeforeEdit,
        hasAnyValuesSet,
        hasStimmzettelBeenEdited,
        expected,
      }) => {
        stimmzettelManager.stimmzettelBeforeEdit.value =
          hasStimmzettelBeforeEdit ? createPersistedStimmzettel() : null;
        stimmzettelManager.hasStimmzettelBeenEdited.value =
          hasStimmzettelBeenEdited;
        stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value =
          hasAnyValuesSet;

        expect(unitUnderTest.isResetDisabled.value).toBe(expected);
      }
    );
  });

  describe("onResetClicked", () => {
    it.each([
      { hasStimmzettelBeforeEdit: false, hasStimmzettelBeenEdited: false },
      { hasStimmzettelBeforeEdit: false, hasStimmzettelBeenEdited: true },
      { hasStimmzettelBeforeEdit: true, hasStimmzettelBeenEdited: false },
      { hasStimmzettelBeforeEdit: true, hasStimmzettelBeenEdited: true },
    ])(
      "should_resetStimmzettelAndHistory_when_hasStimmzettelBeforeEditIs$hasStimmzettelBeforeEditAndHasStimmzettelBeenEditedIs$hasStimmzettelBeenEdited",
      ({ hasStimmzettelBeforeEdit, hasStimmzettelBeenEdited }) => {
        const stimmzettelBeforeEdit = hasStimmzettelBeforeEdit
          ? createPersistedStimmzettel()
          : null;
        stimmzettelManager.stimmzettelBeforeEdit.value = stimmzettelBeforeEdit;
        stimmzettelManager.hasStimmzettelBeenEdited.value =
          hasStimmzettelBeenEdited;

        unitUnderTest.onResetClicked();

        expect(
          stimmzettelManager.bearbeitenDialogStimmzettelUtils
            .resetStimmzettelAndHistory.mock.calls
        ).toStrictEqual([
          hasStimmzettelBeforeEdit && hasStimmzettelBeenEdited
            ? [stimmzettelBeforeEdit]
            : [],
        ]);
      }
    );
  });

  describe("onCancelClicked", () => {
    it("should_closeDialog_when_anyValuesAreSet", () => {
      unitUnderTest.isDialogVisible.value = true;
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value = true;

      unitUnderTest.onCancelClicked();

      expect(unitUnderTest.isDialogVisible.value).toBe(false);
      expect(unitUnderTest.isCancelConfirmationDialogVisible.value).toBe(false);
      expect(emit).not.toHaveBeenCalled();
    });

    it("should_showCancelConfirmationDialog_when_noValuesAreSet", () => {
      unitUnderTest.isDialogVisible.value = true;

      unitUnderTest.onCancelClicked();

      expect(unitUnderTest.isDialogVisible.value).toBe(true);
      expect(unitUnderTest.isCancelConfirmationDialogVisible.value).toBe(true);
      expect(emit).not.toHaveBeenCalled();
    });
  });

  describe("showDialog", () => {
    it.each([false, true])(
      "should_startNewStimmzettelAndSelectSaveAndContinue_when_givenKennungAndReadonlyIs%s",
      (readonly) => {
        const stimmzettel = createPersistedStimmzettel();
        stimmzettelManager.getStimmzettelSnapshot.mockReturnValue(stimmzettel);
        unitUnderTest.currentAction.value = unitUnderTest.actions[1];
        unitUnderTest.isReadonly.value = !readonly;

        unitUnderTest.showDialog(stimmzettel.stimmzettelkennung, readonly);

        expect(unitUnderTest.isDialogVisible.value).toBe(true);
        expect(unitUnderTest.isReadonly.value).toBe(readonly);
        expect(unitUnderTest.stimmzettelkennung.value).toBe(
          stimmzettel.stimmzettelkennung
        );
        expect(stimmzettelManager.startNewStimmzettel.mock.calls).toStrictEqual(
          [[teamName]]
        );
        expect(stimmzettelManager.startEditStimmzettel).not.toHaveBeenCalled();
        expect(unitUnderTest.currentAction.value).toStrictEqual(
          unitUnderTest.actions[0]
        );

        unitUnderTest.currentAction.value.action();

        expect(
          stimmzettelManager.getStimmzettelSnapshot
        ).toHaveBeenCalledExactlyOnceWith();
        expect(emit.mock.calls).toStrictEqual([["confirmNext", stimmzettel]]);
      }
    );

    it.each([false, true])(
      "should_editStimmzettelAndSelectSaveAndClose_when_givenStimmzettelAndReadonlyIs%s",
      (readonly) => {
        const stimmzettel = createPersistedStimmzettel();
        stimmzettelManager.getStimmzettelSnapshot.mockReturnValue(stimmzettel);
        unitUnderTest.isReadonly.value = !readonly;

        unitUnderTest.showDialog(stimmzettel, readonly);

        expect(unitUnderTest.isDialogVisible.value).toBe(true);
        expect(unitUnderTest.isReadonly.value).toBe(readonly);
        expect(unitUnderTest.stimmzettelkennung.value).toBe(
          stimmzettel.stimmzettelkennung
        );
        expect(
          stimmzettelManager.startEditStimmzettel.mock.calls
        ).toStrictEqual([[stimmzettel]]);
        expect(stimmzettelManager.startNewStimmzettel).not.toHaveBeenCalled();
        expect(unitUnderTest.currentAction.value).toStrictEqual(
          unitUnderTest.actions[1]
        );

        unitUnderTest.currentAction.value.action();

        expect(
          stimmzettelManager.getStimmzettelSnapshot
        ).toHaveBeenCalledExactlyOnceWith();
        expect(emit.mock.calls).toStrictEqual([["confirmClose", stimmzettel]]);
      }
    );

    it("should_disableReadonly_when_readonlyIsOmitted", () => {
      unitUnderTest.isReadonly.value = true;

      unitUnderTest.showDialog(1);

      expect(unitUnderTest.isReadonly.value).toBe(false);
    });
  });
});
