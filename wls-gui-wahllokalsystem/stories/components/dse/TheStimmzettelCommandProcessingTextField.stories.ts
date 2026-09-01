import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import TheStimmzettelCommandProcessingTextField from "@/components/dse/TheStimmzettelCommandProcessingTextField.vue";
import { useStimmzettelManager } from "@/composables/dse/stimmzettelManager.ts";

const meta = {
  component: TheStimmzettelCommandProcessingTextField,
} satisfies Meta<typeof TheStimmzettelCommandProcessingTextField>;

export default meta;
type Story = StoryObj<typeof meta>;

const { prepareWahlvorschlag, prepareKandidat } =
  useWahlvorschlaegeTestDataFactory();

function create5WahlvorschlaegeWith10KandidatenEach() {
  const result: Wahlvorschlag[] = [];
  for (let iWahlvorschlag = 1; iWahlvorschlag <= 5; iWahlvorschlag++) {
    const kandidaten: Kandidat[] = [];
    for (let iKandidat = 1; iKandidat <= 10; iKandidat++) {
      kandidaten.push(
        prepareKandidat()
          .anzahlNennungen(3)
          .direktkandidat(false)
          .listenposition(iKandidat)
          .identifikator(`${iKandidat}`)
          .einzelbewerber(true)
          .name(`Kandidat ${iKandidat}`)
          .tabellenSpalteInNiederschrift(1)
          .build()
      );
    }

    result.push(
      prepareWahlvorschlag()
        .kurzname(`W${iWahlvorschlag}`)
        .ordnungszahl(iWahlvorschlag)
        .identifikator(`${iWahlvorschlag}`)
        .kandidaten(kandidaten)
        .build()
    );
  }

  return result;
}

/**
 * Es gibt 5 Wahlvorschläge mit je 10 Kandidaten die jeweils 3 Nennungen haben
 */
export const Default: Story = {
  args: {
    stimmzettelManager: useStimmzettelManager(
      create5WahlvorschlaegeWith10KandidatenEach()
    ),
  },
};
