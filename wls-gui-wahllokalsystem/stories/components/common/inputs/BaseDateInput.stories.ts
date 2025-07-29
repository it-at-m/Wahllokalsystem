import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseDateInput from "@/components/common/inputs/BaseDateInput.vue";

const meta: Meta<typeof BaseDateInput> = {
  component: BaseDateInput,
  argTypes: {
    modelValue: {
      description: "Aktuell gewählter Wert",
      table: {
        category: "props",
        type: { summary: "Date" },
        required: true,
      },
    },
    "onUpdate:modelValue": {
      name: "update:modelValue", // to show name of event not eventHandler
      description:
        "Wird ausgelöst wenn sich der aktuelle Wert ändert und gibt den neuen Wert zurück",
      table: {
        category: "events",
      },
    },
  },
  args: {
    "onUpdate:modelValue": fn(),
    modelValue: new Date(),
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {};
