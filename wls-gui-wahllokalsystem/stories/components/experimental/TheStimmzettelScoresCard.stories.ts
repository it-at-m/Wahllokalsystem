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
      .wahlvorschlaege(
        new Set([
          prepareWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten(createTestDataKandidaten())
            .build(),
          prepareWahlvorschlag().ordnungszahl(2).build(),
        ])
      )
      .build(),
  };
  console.log(`createTestData`, result);
  return result;
}

function createTestDataKandidaten() {
  const countKandidaten = 10;
  const kandidatenSet = new Set<Kandidat>();

  for (let i = 0; i < countKandidaten; i++) {
    kandidatenSet.add(
      prepareKandidat()
        .listenposition(i + 1)
        .build()
    );
  }

  return kandidatenSet;
}
