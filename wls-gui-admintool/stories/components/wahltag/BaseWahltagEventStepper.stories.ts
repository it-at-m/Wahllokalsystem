import type { Meta, StoryObj } from "@storybook/vue3";

import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import { delay, http, HttpResponse } from "msw";

import { default as StoryComponent } from "@/components/wahltag/BaseWahltagEventStepper.vue";

const { prepareWahltagEvent } = useWahltagTestDataFactory();

const meta: Meta<typeof StoryComponent> = {
  component: StoryComponent,
  argTypes: {
    konfigurierteWahltage: {
      description:
        "Key: WahltagID<br />Value: true oder false wenn geprüft wurde ob es zu der ID " +
        "einen konfigurierten Wahltag gibt, sonst undefined",
    },
  },
  args: {
    wahltagEvents: [
      prepareWahltagEvent().wahltagID("wahltagID1").build(),
      prepareWahltagEvent().wahltagID("wahltagID2").build(),
      prepareWahltagEvent().wahltagID("wahltagID3").build(),
      prepareWahltagEvent().wahltagID("wahltagID4").build(),
    ],
    konfigurierteWahltage: new Map<string, boolean | undefined>([
      ["wahltagID1", undefined],
      ["wahltagID2", true],
      ["wahltagID3", false],
      ["wahltagID4", false],
    ]),
  },
  parameters: {
    msw: {
      handlers: [
        http.all("/api/*", async () => {
          await delay(2000);
          return new HttpResponse(null, {
            status: 200,
          });
        }),
      ],
    },
  },
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {},
};
