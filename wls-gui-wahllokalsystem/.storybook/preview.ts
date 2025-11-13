import type { Preview } from "@storybook/vue3";

import { setup } from "@storybook/vue3";
import { initialize, mswLoader } from "msw-storybook-addon";

import { registerPlugins } from "../src/plugins";

setup((app) => {
  // Registers your app's plugins into Storybook
  registerPlugins(app);
});

initialize();

const preview: Preview = {
  tags: ["autodocs"],
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
  loaders: [mswLoader],
};

export default preview;
