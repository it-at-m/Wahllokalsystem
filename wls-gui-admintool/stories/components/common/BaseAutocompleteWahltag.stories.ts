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
    multiple: {
      description: "ermöglicht Mehrfachauswahl",
      control: {
        type: "boolean",
      },
    },
    modelValue: {
      description: "Aktuell gewählte(r) Wert(e)",
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
    beschreibung: "Sebastian",
    nummer: "1.1",
  },
  {
    wahltagID: "2",
    wahltag: "31.01.1998",
    beschreibung: "Daniel",
    nummer: "2.1",
  },
  {
    wahltagID: "3",
    wahltag: "04.04.2000",
    beschreibung: "Viviane",
    nummer: "3.1",
  },
];

export const Default: Story = {
  args: {
    items: wahltage, //wahltage.map((tag) => tag.wahltag),
    multiple: true,
  },
};

/**
 * Nur ein Wahltag kann ausgewählt werden.
 */
export const SingleSelect: Story = {
  args: {
    ...Default.args,
    multiple: false, // überschreibt den Wert aus ...Default.args
  },
};

/**
 * Ein Wahltag ist vorausgewählt.
 */
export const PreSelect: Story = {
  args: {
    ...Default.args,
    modelValue: {
      wahltagID: "3",
      wahltag: "04.04.2000",
      beschreibung: "Viviane",
      nummer: "3.1",
    }, // ergänzt die Werte aus ...Default.args
  },
};
