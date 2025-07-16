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
    onConfirmEdit: {
      description:
        "Wird ausgelöst wenn der Edit-Button geklickt wird und gibt den Namen des ConfigParameters zurück",
      action: "confirmEdit",
      name: "confirmEdit",
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

    onConfirmEdit: fn(),
  },
};

export default meta;

type Story = StoryObj<typeof BaseCardConfigParameterDisplay>;

export const WithValue: Story = {};

export const NoValue: Story = {
  args: {
    configParameter: {
      name: "Willkommenstext",
      beschreibung: "Begrüßungstext auf der Anmeldemaske",
      defaultValue: "Herzlich willkommen zur Testwahl!",
    } as InfomanagementConfigParameter,

    onConfirmEdit: fn(),
  },
};
