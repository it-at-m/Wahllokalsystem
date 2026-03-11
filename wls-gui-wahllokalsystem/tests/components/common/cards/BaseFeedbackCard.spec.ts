import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getBorderColorForInputFeedbackType: vi.fn(),
  getIconColorForInputFeedbackType: vi.fn(),
  getIconForInputFeedbackType: vi.fn(),
  getBackgroundColorAndBoldTextForInputFeedbackType: vi.fn(),
}));

vi.mock("@/composables/common/inputFeedbackUtils.ts", () => ({
  useInputFeedbackUtils: () => ({
    getBorderColorForInputFeedbackType:
      mockDefinitions.getBorderColorForInputFeedbackType,
    getIconColorForInputFeedbackType:
      mockDefinitions.getIconColorForInputFeedbackType,
    getIconForInputFeedbackType: mockDefinitions.getIconForInputFeedbackType,
    getBackgroundColorAndBoldTextForInputFeedbackType:
      mockDefinitions.getBackgroundColorAndBoldTextForInputFeedbackType,
  }),
}));

describe("BaseFeedbackCard.vue", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    setupDefaultMockBehaviorForInputFeedbackUtils();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithoutAdditionalFeedback_when_additionalFeedbackSlotIsNotUsed", async (context) => {
      const feedbackType = InputFeedbackTypeEnum.error;

      wrapper = mount(BaseFeedbackCard, {
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

    it("should_renderWithAdditionalFeedback_when_additionalFeedbackSlotIsUsed", async (context) => {
      const feedbackType = InputFeedbackTypeEnum.error;

      wrapper = mount(BaseFeedbackCard, {
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

    it("should_renderWithSubmitButton_when_submitButtonTextPropertyIsUsed", async (context) => {
      const feedbackType = InputFeedbackTypeEnum.error;

      wrapper = mount(BaseFeedbackCard, {
        global: {
          plugins: [vuetify],
        },
        props: {
          title: "test title for component under test",
          type: feedbackType,
          submitButtonText: "Button-Text",
        },
        slots: {
          default: "the default slot content",
        },
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  function setupDefaultMockBehaviorForInputFeedbackUtils() {
    mockDefinitions.getBorderColorForInputFeedbackType.mockReturnValue(
      "mockedBorderColor"
    );
    mockDefinitions.getIconColorForInputFeedbackType.mockReturnValue(
      "mockedIconColor"
    );
    mockDefinitions.getIconForInputFeedbackType.mockReturnValue("$error");
    mockDefinitions.getBackgroundColorAndBoldTextForInputFeedbackType.mockReturnValue(
      "mockedBgColor font-weight-bold"
    );
  }
});
