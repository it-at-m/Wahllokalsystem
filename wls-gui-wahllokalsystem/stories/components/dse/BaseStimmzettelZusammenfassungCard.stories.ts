import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";

import BaseStimmzettelZusammenfassungCard from "@/components/dse/BaseStimmzettelZusammenfassungCard.vue";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

const { createStimmzettelWahlvorschlag } = useStimmzettelTestDataFactory();

const meta = {
  component: BaseStimmzettelZusammenfassungCard,
} satisfies Meta<typeof BaseStimmzettelZusammenfassungCard>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Valid: Story = {
  args: {
    listenstimmen: [
      createStimmzettelWahlvorschlag(),
      createStimmzettelWahlvorschlag(),
    ],
    ungueltigestimmen: 0,
    direktstimmen: 1,
    reststimmen: 0,
    streichungen: 1,
    gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
  },
};

export const Invalid: Story = {
  args: {
    listenstimmen: [createStimmzettelWahlvorschlag()],
    ungueltigestimmen: 0,
    direktstimmen: 1,
    reststimmen: 0,
    streichungen: 1,
    gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
  },
};

export const BeschlussAusstehend: Story = {
  args: {
    listenstimmen: [],
    ungueltigestimmen: 0,
    direktstimmen: 1,
    reststimmen: 0,
    streichungen: 1,
    gueltigkeit: StimmzettelGueltigkeitEnum.BeschlussAusstehend,
  },
};
