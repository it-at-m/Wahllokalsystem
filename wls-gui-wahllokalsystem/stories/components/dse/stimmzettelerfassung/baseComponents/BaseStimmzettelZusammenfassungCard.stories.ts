import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useDseStimmzettelTestDataFactory } from "@tests/utils/dse/DseStimmzettelTestDataFactory.ts";

import BaseStimmzettelZusammenfassungCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelZusammenfassungCard.vue";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const { createDseWahlvorschlag } = useDseStimmzettelTestDataFactory();

const meta = {
  component: BaseStimmzettelZusammenfassungCard,
} satisfies Meta<typeof BaseStimmzettelZusammenfassungCard>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Valid: Story = {
  args: {
    listenstimmen: [createDseWahlvorschlag(), createDseWahlvorschlag()],
    ungueltigestimmen: 0,
    direktstimmen: 1,
    reststimmen: 0,
    streichungen: 1,
    gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
  },
};

export const Invalid: Story = {
  args: {
    listenstimmen: [createDseWahlvorschlag()],
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
