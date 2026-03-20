import type { Meta, StoryObj } from "@storybook/vue3";

import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const meta = {
  component: BaseFeedbackCard,
  argTypes: {
    default: {
      description: "Inhalt, der neben dem Icon angezeigt wird",
    },
    additionalFeedback: {
      description:
        "Optionale zusätzliche Informationen, die unterhalb des Icons angezeigt werden",
    },
    actions: {
      description:
        "Optionale Card-Actions, die am unteren Rand der Karte angezeigt werden",
    },
  },
  args: {
    default: "Der Inhalt des Defaultslot",
    title: "Titel des Dialoges",
  },
} satisfies Meta<typeof BaseFeedbackCard>;

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

export const ErrorWithAction: Story = {
  args: {
    actions: "Abschließend wird eine Aktion angeboten",
    title: "Titel zu einem Fehler",
    type: InputFeedbackTypeEnum.error,
  },
};

export const ErrorWithAdditionalFeedbackAndAction: Story = {
  args: {
    additionalFeedback: "Es gibt noch mehr zu dem Fehler zu sagen",
    actions: "Abschließend wird eine Aktion angeboten",
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

export const Success: Story = {
  args: {
    title: "Titel zu einer Erfolgsmeldung",
    type: InputFeedbackTypeEnum.success,
  },
};

export const Warning: Story = {
  args: {
    title: "Titel zu einer Warnung",
    type: InputFeedbackTypeEnum.warning,
  },
};
