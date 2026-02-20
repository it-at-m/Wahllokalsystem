import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

import BaseCardConfigParameterDisplay from "@/components/config-parameter/BaseCardConfigParameterDisplay.vue";

const meta: Meta<typeof BaseCardConfigParameterDisplay> = {
  component: BaseCardConfigParameterDisplay,
  argTypes: {
    configParameter: {
      description: "Konfigurationsparameter Objekt",
      control: "object",
    },
    onClickEdit: {
      description:
        "Wird ausgelöst wenn der Edit-Button geklickt wird und gibt den Namen des ConfigParameters zurück",
      action: "clickEdit",
      name: "clickEdit",
      table: {
        category: "Events",
      },
    },
  },
  args: {
    configParameter: {
      name: "Willkommenstext",
      beschreibung: "Begrüßungstext auf der Anmeldemaske",
      wert: "Herzlich willkommen zur Wahl!",
      defaultValue: "Herzlich willkommen zur Testwahl!",
    } as InfomanagementConfigParameter,

    onClickEdit: fn(),
  },
};

export default meta;

type Story = StoryObj<typeof BaseCardConfigParameterDisplay>;

export const Default: Story = {};
