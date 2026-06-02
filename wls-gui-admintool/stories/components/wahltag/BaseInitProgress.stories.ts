import type { Meta, StoryObj } from "@storybook/vue3-vite";

import BaseInitProgress from "@/components/wahltag/BaseInitProgress.vue";

const meta: Meta<typeof BaseInitProgress> = {
  component: BaseInitProgress,
};

export default meta;

type Story = StoryObj<typeof BaseInitProgress>;

export const Default: Story = {
  args: {
    awerte: {
      active: true,
      total: 100,
      finished: 10,
    },
    referendumvorschlaege: {
      active: true,
      finished: 23,
      total: 123,
    },
    wahlvorschlaege: {
      active: true,
      finished: 42,
      total: 123,
    },
  },
};

export const NoReferendumvorschlaegeToLoad: Story = {
  args: {
    awerte: {
      active: true,
      finished: 10,
      total: 100,
    },
    referendumvorschlaege: undefined,
    wahlvorschlaege: {
      active: true,
      finished: 42,
      total: 123,
    },
  },
};

export const AWerteFullyLoaded: Story = {
  args: {
    awerte: {
      active: false,
      finished: 100,
      total: 100,
    },
    referendumvorschlaege: {
      finished: 23,
      total: 123,
    },
    wahlvorschlaege: {
      finished: 42,
      total: 123,
    },
  },
};

export const NotFinishedButAlsoNotActive: Story = {
  args: {
    wahlvorschlaege: {
      active: false,
      finished: 10,
      total: 42,
    },
  },
};

export const FinishedWithTotalZero: Story = {
  args: {
    wahlvorschlaege: {
      active: false,
      finished: 12,
      total: 0,
    },
  },
};
