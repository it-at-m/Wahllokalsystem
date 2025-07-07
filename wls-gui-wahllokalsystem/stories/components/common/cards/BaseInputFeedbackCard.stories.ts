import type { Meta, StoryObj } from "@storybook/vue3";

import BaseInputFeedbackCard from "@/components/common/cards/BaseInputFeedbackCard.vue";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const meta = {
  component: BaseInputFeedbackCard,
  args: {
    default: "Der Inhalt des Defaultslot",
    title: "Titel des Dialoges",
  },
} satisfies Meta<typeof BaseInputFeedbackCard>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Error: Story = {
  args: {
    title: "Titel zu einem Fehler",
    type: InputFeedbackTypeEnum.error,
  },
};

export const ErrorWithAdditionalFeedback: Story = {
  args: {
    additionalFeedback: "Es gibt noch mehr zu dem Fehler zu sagen",
    title: "Titel zu einem Fehler",
    type: InputFeedbackTypeEnum.error,
  },
};

export const Information: Story = {
  args: {
    title: "Titel zu einem Hinweis",
    type: InputFeedbackTypeEnum.information,
  },
};
