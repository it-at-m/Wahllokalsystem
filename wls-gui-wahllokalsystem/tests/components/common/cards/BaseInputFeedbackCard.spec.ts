import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import BaseInputFeedbackCard from "@/components/common/cards/BaseInputFeedbackCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getBorderColorForInputFeedbackType: vi.fn(),
  getIconColorForInputFeedbackType: vi.fn(),
  getIconForInputFeedbackType: vi.fn(),
  getTextColorForInputFeedbackType: vi.fn(),
}));

vi.mock("@/composables/common/inputFeedbackUtils.ts", () => ({
  useInputFeedbackUtils: () => ({
    getBorderColorForInputFeedbackType:
      mockDefinitions.getBorderColorForInputFeedbackType,
    getIconColorForInputFeedbackType:
      mockDefinitions.getIconColorForInputFeedbackType,
    getIconForInputFeedbackType: mockDefinitions.getIconForInputFeedbackType,
    getTextColorForInputFeedbackType:
      mockDefinitions.getTextColorForInputFeedbackType,
  }),
}));

describe("BaseInputFeedbackCard", () => {
  let wrapper: VueWrapper;

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithoutAdditionalFeedback_when_slotIsNotUsed", async (context) => {
      const feedbackType = InputFeedbackTypeEnum.error;

      mockDefinitions.getBorderColorForInputFeedbackType.mockReturnValue(
        "mockedBorderColor"
      );
      mockDefinitions.getIconColorForInputFeedbackType.mockReturnValue(
        "mockedIconColor"
      );
      mockDefinitions.getIconForInputFeedbackType.mockReturnValue(
        "$information"
      );
      mockDefinitions.getTextColorForInputFeedbackType.mockReturnValue(
        "mockedTextColor"
      );

      wrapper = mount(BaseInputFeedbackCard, {
        global: {
          plugins: [vuetify],
        },
        props: {
          title: "test title for component under test",
          type: feedbackType,
        },
        slots: {
          default: "the default slot content",
        },
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithAdditionalFeedback_when_slotIsUsed", async (context) => {
      const feedbackType = InputFeedbackTypeEnum.error;

      mockDefinitions.getBorderColorForInputFeedbackType.mockReturnValue(
        "mockedBorderColor"
      );
      mockDefinitions.getIconColorForInputFeedbackType.mockReturnValue(
        "mockedIconColor"
      );
      mockDefinitions.getIconForInputFeedbackType.mockReturnValue(
        "$information"
      );
      mockDefinitions.getTextColorForInputFeedbackType.mockReturnValue(
        "mockedTextColor"
      );

      wrapper = mount(BaseInputFeedbackCard, {
        global: {
          plugins: [vuetify],
        },
        props: {
          title: "test title for component under test",
          type: feedbackType,
        },
        slots: {
          default: "the default slot content",
          additionalFeedback:
            "additional information to give user more feedback to its input",
        },
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
