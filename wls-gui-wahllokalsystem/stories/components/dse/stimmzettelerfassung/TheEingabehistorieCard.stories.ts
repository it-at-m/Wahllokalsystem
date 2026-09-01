import type { Meta, StoryObj } from "@storybook/vue3-vite";

import TheEingabehistorieCard from "@/components/dse/stimmzettelerfassung/TheEingabehistorieCard.vue";
import { InputHistoryTypeEnum } from "@/types/dse/stimmzettelerfassung/InputHistoryTypeEnum.ts";

const meta = {
  component: TheEingabehistorieCard,
} satisfies Meta<typeof TheEingabehistorieCard>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    changeHistory: [
      {
        type: InputHistoryTypeEnum.SET_WAHLVORSCHLAG,
        text: ["Gemeinsam Stark"],
      },
      {
        type: InputHistoryTypeEnum.ADD_USER_VOTE,
        text: ["403", "Andrei Ivanov"],
      },
      {
        type: InputHistoryTypeEnum.SET_WAHLVORSCHLAG,
        text: ["Integration Aktiv"],
      },
      {
        type: InputHistoryTypeEnum.REMOVE_USER_VOTE,
        text: ["301", "Dimitrios Papadopoulos"],
      },
      {
        type: InputHistoryTypeEnum.DISCARD_KANDIDAT,
        text: ["405", "Nikolai Volkov"],
      },
    ],
  },
};

export const EmptyHistory: Story = {
  args: {
    changeHistory: [],
  },
};
