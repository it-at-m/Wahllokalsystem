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
        type: { summary: "Wahltag" },
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
    wahltag: new Date("1988-02-27"),
    events: [
      {
        wahltagID: "1",
        beschreibung: "S",
        nummer: "1.1",
      },
    ],
  },
  {
    wahltag: new Date("1998-01-31"),
    events: [
      {
        wahltagID: "2",
        beschreibung: "D",
        nummer: "2.1",
      },
    ],
  },
  {
    wahltag: new Date("2000-04-04"),
    events: [
      {
        wahltagID: "3",
        beschreibung: "V",
        nummer: "3.1",
      },
    ],
  },
  {
    wahltag: new Date("1999-10-31"),
    events: [
      {
        wahltagID: "4",
        beschreibung: "G",
        nummer: "4.1",
      },
    ],
  },
  {
    wahltag: new Date("1969-11-05"),
    events: [
      {
        wahltagID: "5",
        beschreibung: "R",
        nummer: "5.1",
      },
    ],
  },
  {
    wahltag: new Date("1977-05-14"),
    events: [
      {
        wahltagID: "6",
        beschreibung: "N",
        nummer: "6.1",
      },
    ],
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

export const NoData: Story = {
  args: {
    ...Default.args,
    items: [],
  },
};
