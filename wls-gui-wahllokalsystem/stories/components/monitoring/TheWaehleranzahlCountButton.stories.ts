import type { Meta, StoryObj } from "@storybook/vue3-vite";

import TheWaehleranzahlCountButton from "@/components/monitoring/TheWaehleranzahlCountButton.vue";

const meta = {
  component: TheWaehleranzahlCountButton,
  args: {},
} satisfies Meta<typeof TheWaehleranzahlCountButton>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};
