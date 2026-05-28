import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";

import TheEreignisseRows from "@/components/vorfaelleundvorkommnisse/TheEreignisseRows.vue";
import pinia from "@/plugins/pinia.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const meta = {
  component: TheEreignisseRows,
  args: {},
  decorators: [
    (story) => {
      return {
        components: { story },
        template: "<story />",
      };
    },
  ],
} satisfies Meta<typeof TheEreignisseRows>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  async beforeEach() {
    const ereignisStore = useEreignisStore(pinia);
    ereignisStore.wahlbezirkEreignisse =
      useVorfaelleundvorkommnisseTestDataFactory()
        .prepareWahlbezirkEreignisse()
        .build();

    await flushPromises();
  },
  args: {},
};
