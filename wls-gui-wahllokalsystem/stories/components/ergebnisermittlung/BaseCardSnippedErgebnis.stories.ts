import type { Meta, StoryObj } from "@storybook/vue3";

import BaseCardSnippedErgebnis from "@/components/ergebnisermittlung/BaseCardSnippedErgebnis.vue";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const wahlId = "wahlID";

const meta = {
  component: BaseCardSnippedErgebnis,
  args: {
    wahlId,
  },
  decorators: [
    (story) => {
      return {
        components: { story },
        template: "<story />",
      };
    },
  ],
} satisfies Meta<typeof BaseCardSnippedErgebnis>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    wahlId: wahlId,
    stapelArt: StapelArtEnum.MbwD,
    snippedTitle: "Ungültige Stimmzettel",
  },
};

export const MinMaxValue: Story = {
  args: {
    wahlId: wahlId,
    stapelArt: StapelArtEnum.MbwD,
    snippedTitle: "Ungültige Stimmzettel",
    minValue: 5,
    maxValue: 20,
  },
};
