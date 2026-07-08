import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { delay, http, HttpResponse } from "msw";

import BaseDialogConfirmation from "@/components/wahltag/BaseDialogConfirmation.vue";
import { default as StoryComponent } from "@/components/wahltag/BaseStepWahltagInit.vue";

const meta: Meta<typeof StoryComponent> = {
  component: StoryComponent,
  subcomponents: { BaseDialogConfirmation },
  args: {
    wahltagEvent: {
      nummer: "Nummer 1",
      beschreibung: "Beschreibung",
      wahltagID: "wahltagID",
    },
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

export const Default: Story = {};

export const WahltermindatenDoesNotExist: Story = {
  args: {
    wahlterminDatenExists: false,
  },
};

export const WahlterminDatenDoesExist: Story = {
  args: {
    wahlterminDatenExists: true,
  },
};
