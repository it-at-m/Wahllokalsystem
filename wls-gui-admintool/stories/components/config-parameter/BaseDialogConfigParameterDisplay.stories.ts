import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { Meta, StoryObj } from "@storybook/vue3";

import { fn } from "@storybook/test";

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

export const Default: Story = {

};
