import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { fn } from "storybook/test";
import { ref } from "vue";
import { VBtn } from "vuetify/components";

import BaseDialogConfigParameterDisplay from "@/components/config-parameter/BaseDialogConfigParameterDisplay.vue";

const meta: Meta<typeof BaseDialogConfigParameterDisplay> = {
  component: BaseDialogConfigParameterDisplay,
  argTypes: {
    configParameter: {
      description: "Konfigurationsparameter Objekt",
      control: "object",
    },
    onCancelEdit: {
      description:
        "Wird ausgelöst wenn der Cancel-Button geklickt wird und setzt den Wert auf Standardwert zurück",
      action: "cancelEdit",
      name: "cancelEdit",
      table: {
        category: "Events",
      },
    },
    onCommitEdit: {
      description:
        "Wird ausgelöst wenn der Commit-Button geklickt wird und gibt den Wert des ConfigParameters zurück",
      action: "commitEdit",
      name: "commitEdit",
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

    onCancelEdit: fn(),
    onCommitEdit: fn(),
  },
};

export default meta;

type Story = StoryObj<typeof BaseDialogConfigParameterDisplay>;

export const Default: Story = {};

export const ActivateDialogComponentWithButton: Story = {
  render(args) {
    const dialogRef = ref();
    const showDialog = () => {
      dialogRef.value.showDialog();
    };
    return {
      components: {
        VBtn,
        BaseDialogConfigParameterDisplay,
      },
      setup() {
        return { args, dialogRef, showDialog };
      },
      template: `
        <div>
          <v-btn @click="showDialog">
            OPEN DIALOG
          </v-btn>
          <BaseDialogConfigParameterDisplay
          ref="dialogRef"
          v-bind="args"
          />
        </div>`,
    };
  },
};
