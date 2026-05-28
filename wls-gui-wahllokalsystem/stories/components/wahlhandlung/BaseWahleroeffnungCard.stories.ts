import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useKonfigurationsparameterTestDataFactory } from "@tests/utils/infomanagement/KonfigurationsparameterTestDataFactory.ts";
import { delay, http, HttpResponse } from "msw";
import { createPinia, setActivePinia } from "pinia";

import BaseWahleroeffnungCard from "@/components/wahlhandlung/BaseWahleroeffnungCard.vue";
import pinia from "@/plugins/pinia";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { createUserLocalDevelopment } from "@/types/User.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

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
} satisfies Meta<typeof BaseWahleroeffnungCard>;

const { prepareKonfigurationsparameter } =
  useKonfigurationsparameterTestDataFactory();

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  async beforeEach() {
    const infomanagementStore = useInfomanagementStore(pinia);
    const user = createUserLocalDevelopment();
    user.wahlbezirksArt = WahlbezirksArtEnum.BWB;
    useUserStore().setUser(user);
    infomanagementStore.konfigurationsparameter = [
      prepareKonfigurationsparameter()
        .schluessel("FRUEHESTE_EROEFFNUNGSZEIT_BW")
        .wert("08:00:00")
        .build(),
      prepareKonfigurationsparameter()
        .schluessel("FRUEHESTE_SCHLIESSUNGSZEIT_BW")
        .wert("16:00:00")
        .build(),
    ];
  },
  args: {
    userHint:
      "Ein nützlicher Hinweis für den User wie er die zu erfassende Uhrzeit bestimmen kann",
  },
};
