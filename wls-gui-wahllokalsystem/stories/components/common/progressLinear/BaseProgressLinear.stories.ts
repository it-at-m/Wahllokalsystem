import type { Task } from "@/types/tasks/Task.ts";
import type { Meta, StoryObj } from "@storybook/vue3-vite";

import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";

const meta = {
  component: BaseProgressLinear,
  args: {},
} satisfies Meta<typeof BaseProgressLinear>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    titel: "Fortschritt",
    titelClass: "",
    isLoading: true,
    current: 3,
    total: 10,
    tasks: [
      { name: "Daten 1" } as Task,
      { name: "Daten 2" } as Task,
      { name: "Daten 3" } as Task,
    ],
  },
};

export const NotLoading: Story = {
  args: {
    titel: "Fortschritt",
    titelClass: "",
    isLoading: false,
    current: 10,
    total: 10,
    tasks: [
      { name: "Daten 1" } as Task,
      { name: "Daten 2" } as Task,
      { name: "Daten 3" } as Task,
      { name: "Daten 4" } as Task,
      { name: "Daten 5" } as Task,
      { name: "Daten 6" } as Task,
      { name: "Daten 7" } as Task,
      { name: "Daten 8" } as Task,
      { name: "Daten 9" } as Task,
      { name: "Daten 10" } as Task,
    ],
  },
};

export const Color: Story = {
  args: {
    titel: "Fortschritt",
    titelClass: "",
    isLoading: true,
    current: 2,
    total: 10,
    tasks: [{ name: "Daten 1" } as Task, { name: "Daten 2" } as Task],
    color: "success",
  },
};
