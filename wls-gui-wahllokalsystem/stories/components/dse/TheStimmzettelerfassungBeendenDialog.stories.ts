import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { delay, http, HttpResponse } from "msw";
import { fn } from "storybook/test";

import TheStimmzettelerfassungBeendenDialog from "@/components/dse/TheStimmzettelerfassungBeendenDialog.vue";

const { generateRandomString } = useCommonTestDataFactory();

const meta = {
  component: TheStimmzettelerfassungBeendenDialog,
  argTypes: {
    onCancel: {
      // todo TS2353: Object literal may only specify known properties, and onCancel does not exist in type
      table: {
        category: "events",
      },
    },
    onConfirm: {
      table: {
        category: "events",
      },
    },
  },
  args: {
    onCancel: fn(),
    onConfirm: fn(),
  },
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
} satisfies Meta<typeof TheStimmzettelerfassungBeendenDialog>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    modelValue: false,
    wahlId: generateRandomString(10),
    wahlbezirkId: generateRandomString(10),
    teamId: generateRandomString(10),
  },
};
