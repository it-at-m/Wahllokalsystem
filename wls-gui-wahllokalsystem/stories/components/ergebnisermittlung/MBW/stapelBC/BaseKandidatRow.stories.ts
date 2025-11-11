import type { Meta, StoryObj } from "@storybook/vue3";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

import BaseKandidatRow from "@/components/ergebnisermittlung/MBW/stapelBC/BaseKandidatRow.vue";

const meta = {
  component: BaseKandidatRow,
  args: {},
  decorators: [
    (story) => {
      return {
        component: { story },
        template:
          "<table style='width: 100%; border: solid 1px black'><story /></table>",
      };
    },
  ],
} satisfies Meta<typeof BaseKandidatRow>;

const { generateRandomNumberInRange } = useCommonTestDataFactory();
const { createErgebnis } = useErgebnisseTestDataFactory();
const { prepareKandidat } = useWahlvorschlaegeTestDataFactory();

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    kandidat: prepareKandidat().build(),
    modelValue: createErgebnis(),
    wahlvorschlagNummer: generateRandomNumberInRange(1, 12),
  },
};
