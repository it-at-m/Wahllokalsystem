import type { Preview } from "@storybook/vue3";

import { setup } from "@storybook/vue3";

import "vue3-toastify/dist/index.css";

import Vue3Toastify from "vue3-toastify";

import { registerPlugins } from "../src/plugins";

setup((app) => {
  // Registers your app's plugins into Storybook
  app.use(Vue3Toastify);
  registerPlugins(app);
});

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
};

export default preview;
