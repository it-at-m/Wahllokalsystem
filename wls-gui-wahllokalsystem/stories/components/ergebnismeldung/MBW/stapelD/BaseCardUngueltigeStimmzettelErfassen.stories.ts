import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { fn } from "storybook/test";

import BaseCardUngueltigeStimmzettelErfassen from "@/components/ergebnismeldung/MBW/stapelD/BaseCardUngueltigeStimmzettelErfassen.vue";

const meta = {
  component: BaseCardUngueltigeStimmzettelErfassen,
  args: {
    isWahlFinished: false,
    onSave: fn(),
  },
  argTypes: {
    modelValue: {
      description: "zu pflegendes Ergebnis",
      table: {
        category: "props",
        type: { summary: "Ergebnis" },
      },
    },
    "onUpdate:modelValue": {
      description: "Wird ausgelöst wenn sich der aktuelle Wert ändert",
      name: "update:modelValue",
      table: {
        category: "events",
      },
    },
  },
} satisfies Meta<typeof BaseCardUngueltigeStimmzettelErfassen>;

const { generateRandomNumberInRange } = useCommonTestDataFactory();
const { createErgebnis } = useErgebnisseTestDataFactory();

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    modelValue: createErgebnis(),
    ungueltigeStimmzettelNachBeschluss: generateRandomNumberInRange(0, 100),
  },
};
