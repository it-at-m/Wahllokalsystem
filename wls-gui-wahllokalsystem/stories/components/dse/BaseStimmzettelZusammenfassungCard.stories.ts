import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import BaseStimmzettelZusammenfassungCard from "@/components/dse/BaseStimmzettelZusammenfassungCard.vue";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

const { createWahlvorschlag } = useWahlvorschlaegeTestDataFactory();

const meta = {
  component: BaseStimmzettelZusammenfassungCard,
} satisfies Meta<typeof BaseStimmzettelZusammenfassungCard>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Valid: Story = {
  args: {
    listenstimmen: [createWahlvorschlag(), createWahlvorschlag()],
    gesamtstimmen: 1,
    ungueltigestimmen: 0,
    direktstimmen: 1,
    reststimmen: 0,
    streichungen: 1,
    gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
  },
};

export const Invalid: Story = {
  args: {
    listenstimmen: [createWahlvorschlag()],
    gesamtstimmen: 1,
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
    gesamtstimmen: 1,
    ungueltigestimmen: 0,
    direktstimmen: 1,
    reststimmen: 0,
    streichungen: 1,
    gueltigkeit: StimmzettelGueltigkeitEnum.BeschlussAusstehend,
  },
};
