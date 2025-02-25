import type { Meta, StoryObj } from "@storybook/vue3";

import { createPinia, setActivePinia } from "pinia";

import TheWahlvorstandLastSendDiv from "../../../src/components/wahlvorstand/TheWahlvorstandLastSendDiv.vue";
import pinia from "../../../src/plugins/pinia";
import { useWahlvorstandStore } from "../../../src/stores/wahlvorstandStore";

// More on how to set up stories at: https://storybook.js.org/docs/writing-stories
const meta = {
  component: TheWahlvorstandLastSendDiv,
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
} satisfies Meta<typeof TheWahlvorstandLastSendDiv>;

export default meta;
type Story = StoryObj<typeof meta>;

export const LastSendingIsNow: Story = {
  async beforeEach() {
    const store = useWahlvorstandStore(pinia);
    store.lastSending = new Date();
  },
};

export const LastSendingWithLeadingZeros: Story = {
  async beforeEach() {
    const store = useWahlvorstandStore(pinia);
    store.lastSending = new Date("2025-02-01T01:02:03.004");
  },
};

export const LastSendingIsNull: Story = {
  beforeEach() {
    const store = useWahlvorstandStore(pinia);
    store.lastSending = null;
  },
};
