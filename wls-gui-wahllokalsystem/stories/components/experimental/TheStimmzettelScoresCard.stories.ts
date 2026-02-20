import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Meta, StoryObj } from "@storybook/vue3";

import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import TheStimmzettelScoresCard from "@/components/experimental/TheStimmzettelScoresCard.vue";

const { prepareWahlvorschlaege, prepareWahlvorschlag, prepareKandidat } =
  useWahlvorschlaegeTestDataFactory();

const meta = {
  component: TheStimmzettelScoresCard,
  args: {},
} satisfies Meta<typeof TheStimmzettelScoresCard>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: createTestData(),
};

function createTestData() {
  const result = {
    wahlvorschlaege: prepareWahlvorschlaege()
      .wahlvorschlaege([
        prepareWahlvorschlag()
          .ordnungszahl(1)
          .kandidaten(createTestDataKandidaten())
          .build(),
        prepareWahlvorschlag()
          .ordnungszahl(2)
          .kandidaten(createTestDataKandidaten(7))
          .build(),
      ])
      .build(),
  };
  return result;
}

function createTestDataKandidaten(countKandidaten = 10) {
  const kandidatenSet: Kandidat[] = [];

  for (let i = 0; i < countKandidaten; i++) {
    kandidatenSet.push(
      prepareKandidat()
        .listenposition(i + 1)
        .name(`Kandidat ${i + 1}`)
        .build()
    );
  }

  return kandidatenSet;
}
