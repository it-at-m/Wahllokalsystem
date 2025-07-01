import type { Meta, StoryObj } from "@storybook/vue3";

import { useKonfigurationsparameterTestDataFactory } from "@tests/utils/infomanagement/KonfigurationsparameterTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";

import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import BaseWahleroeffnungCard from "@/components/wahlvorbereitung/BaseWahleroeffnungCard.vue";
import pinia from "@/plugins/pinia";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { createUserLocalDevelopment } from "@/types/User.ts";

const meta = {
  component: BaseWahleroeffnungCard,
  args: {},
  argTypes: {
    userHint: {
      description:
        "Hinweis für den User zur Bedeutung der zu erfassenden Uhrzeit",
      type: "string",
    },
  },
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
} satisfies Meta<typeof BaseWahleroeffnungCard>;

const {} = useKonfigurationsparameterTestDataFactory(); //TODO

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  async beforeEach() {
    const infomanagementStore = useInfomanagementStore(pinia);
    const user = createUserLocalDevelopment();
    user.wahlbezirksArt = "BWB";
    infomanagementStore.konfigurationsparameter = [{}];
  },
  args: {
    userHint:
      "Ein nützlicher Hinweis für den User wie er die zu erfassenden Uhrzeit bestimmen kann",
  },
};
