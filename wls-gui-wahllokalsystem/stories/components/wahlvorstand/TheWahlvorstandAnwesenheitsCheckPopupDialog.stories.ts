import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";
import type { Meta, StoryObj } from "@storybook/vue3";

import { createPinia, setActivePinia } from "pinia";

import TheWahlvorstandAnwesenheitsCheckPopupDialog from "@/components/wahlvorstand/TheWahlvorstandAnwesenheitsCheckPopupDialog.vue";
import pinia from "@/plugins/pinia.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

const meta = {
  component: TheWahlvorstandAnwesenheitsCheckPopupDialog,
  args: {},
  decorators: [
    (story) => {
      const pinia = createPinia();
      setActivePinia(pinia);
      return {
        component: { story },
        template: "<story />",
      };
    },
  ],
} satisfies Meta<typeof TheWahlvorstandAnwesenheitsCheckPopupDialog>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  async beforeEach() {
    const store = useInfomanagementStore(pinia);
    store.konfigurationsparameter = [
      {
        schluessel: "MELDUNGSZEIT_ANWESENHEIT_CHECK",
        wert: "14:32:00",
      } as Konfigurationsparameter,
    ];
  },
};
