import type { Wahlvorschlag } from "@/types/wahlvorschlag/Wahlvorschlag";
import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import { default as StoryComponent } from "@/components/experimental/wahlvorschlag/WahlvorschlagListenValuesInput.vue";

const defaultCountEntries = 80;
const wahlvorschlage: Wahlvorschlag[] = [];
for (let i = 1; i <= defaultCountEntries; i++) {
  wahlvorschlage.push({
    name: `Kandidat ${i}`,
    nummer: 100 + i,
  });
}

const meta: Meta<typeof StoryComponent> = {
  component: StoryComponent,
  args: {
    wahlvorschlagListe: {
      name: "Liste 1",
      wahlvorschlaege: wahlvorschlage,
    },
    list: "Liste 1",
    onAddVote: fn(),
  },
};

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
