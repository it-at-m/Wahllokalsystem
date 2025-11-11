import type { Meta, StoryObj } from "@storybook/vue3";

import { useAWerteTestDataFactory } from "@tests/utils/ergebnisermittlung/aWerteTestDataFactory.ts";
import { delay, http, HttpResponse } from "msw";

import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";

const { createAWerte } = useAWerteTestDataFactory();

const meta = {
  component: TheMBWWahlberechtigteAnzeigenCard,
  args: {},
  parameters: {
    msw: {
      handlers: [
        http.all("/api/*", async () => {
          await delay(1000);
          return new HttpResponse(null, {
            status: 200,
          });
        }),
      ],
    },
  },
  decorators: [
    (story) => {
      return {
        components: { story },
        template: "<story />",
      };
    },
  ],
} satisfies Meta<typeof TheMBWWahlberechtigteAnzeigenCard>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    wahlberechtigte: createAWerte(),
  },
};
