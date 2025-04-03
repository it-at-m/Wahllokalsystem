import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";

const meta: Meta<typeof BaseAutocompleteWahltag> = {
  component: BaseAutocompleteWahltag,
  argTypes: {
    items: {
      description: "Liste der verfügbaren Wahltage",
      control: {
        type: "object",
      },
    },
    modelValue: {
      description: "Aktuell gewählter Wert",
      control: {
        type: "object",
      },
      table: {
        category: "props",
        type: { summary: "WahltagDTO" },
      },
    },
    "onUpdate:modelValue": {
      description:
        "Wird ausgelöst wenn sich der aktuelle Wert ändert und gibt den neuen Wert zurück",
      name: "update:modelValue", // to show name of event not eventHandler
      table: {
        category: "events",
      },
    },
  },
};

export default meta;

type Story = StoryObj<typeof meta>;

const wahltage: Wahltag[] = [
  {
    wahltag: "27.02.1988",
    events: [],
  },
  {
    wahltag: "31.01.1998",
    events: [],
  },
  {
    wahltag: "04.04.2000",
    events: [],
  },
  {
    wahltag: "31.10.1999",
    events: [],
  },
  {
    wahltag: "05.11.1969",
    events: [],
  },
  {
    wahltag: "14.05.1977",
    events: [],
  },
];

export const Default: Story = {
  args: {
    items: wahltage,
    // is required so that storybook can render/process the event
    "onUpdate:modelValue": fn(),
  },
};

/**
 * Ein Wahltag ist vorausgewählt.
 */
export const PreSelect: Story = {
  args: {
    ...Default.args,
    modelValue: wahltage[2],
  },
};
