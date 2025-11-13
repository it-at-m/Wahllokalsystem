import type { Meta, StoryObj } from "@storybook/vue3";

import BaseRowObwStapelA from "@/components/ergebnisermittlung/OBW/stapelA/BaseRowOBWStapelA.vue";

const meta = {
  component: BaseRowObwStapelA,
  argTypes: {
    modelValue: {
      description: "Ergebnis das erfasst wird",
      table: {
        category: "props",
        type: {
          summary: "Ergebnis",
        },
        required: true,
      },
    },
    "onUpdate:modelValue": {
      description: "Update des erfassten Ergebnisses",
      name: "update:modelValue",
      table: {
        category: "events",
      },
    },
  },
  args: {
    modelValue: {
      wahlvorschlagID: "1",
      kandidatID: null,
      wahlvorschlagsOrdnungszahl: null,
      ergebnis: null,
      numIndex: null,
    },
    wahlvorschlag: {
      identifikator: "1",
      ordnungszahl: 1,
      kurzname: "Wahlvorschlag Kurzname",
      erhaeltStimmen: true,
      kandidaten: [
        {
          identifikator: "1",
          name: "Kandidat",
          listenposition: 1,
          direktkandidat: false,
          tabellenSpalteInNiederschrift: 1,
          einzelbewerber: false,
        },
      ],
    },
    ergebnisStapelC: 0,
  },
  decorators: [
    (story) => {
      return {
        components: { story },
        template: "<table style='width:100%'><story /></table>",
      };
    },
  ],
} satisfies Meta<typeof BaseRowObwStapelA>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {},
};

export const WithErgebnisSetAndStapelCGueltigValue: Story = {
  args: {
    modelValue: {
      wahlvorschlagID: "1",
      kandidatID: null,
      wahlvorschlagsOrdnungszahl: null,
      ergebnis: 33,
      numIndex: null,
    },
    ergebnisStapelC: 7,
  },
};
