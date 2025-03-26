import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Meta, StoryObj } from "@storybook/vue3";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";

const meta = {
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
    },
  },
  args: {},
} satisfies Meta<typeof BaseAutocompleteWahltag>;

export default meta;

type Story = StoryObj<typeof meta>;

const wahltage: WahltagDTO[] = [
  {
    wahltagID: "1",
    wahltag: "27.02.1988",
    beschreibung: "S",
    nummer: "1.1",
  },
  {
    wahltagID: "2",
    wahltag: "31.01.1998",
    beschreibung: "D",
    nummer: "2.1",
  },
  {
    wahltagID: "3",
    wahltag: "04.04.2000",
    beschreibung: "V",
    nummer: "3.1",
  },
  {
    wahltagID: "4",
    wahltag: "31.10.1999",
    beschreibung: "G",
    nummer: "4.1",
  },
  {
    wahltagID: "5",
    wahltag: "05.11.1969",
    beschreibung: "R",
    nummer: "5.1",
  },
];

export const Default: Story = {
  args: {
    items: wahltage,
  },
};

/**
 * Ein Wahltag ist vorausgewählt.
 */
export const PreSelect: Story = {
  args: {
    ...Default.args,
    modelValue: wahltage[2], // ergänzt oder überschreibt die Werte aus ...Default.args / der Default Story
  },
};
