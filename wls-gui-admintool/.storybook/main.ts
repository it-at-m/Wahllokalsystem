;
// This file has been automatically migrated to valid ESM format by Storybook.
import path, { dirname } from "node:path";
import { fileURLToPath } from "node:url";



import type { StorybookConfig } from "@storybook/vue3-vite";



import { mergeConfig } from "vitest/config";





const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const config: StorybookConfig = {
  stories: ["../stories/**/*.stories.@(js|jsx|mjs|ts|tsx)"],
  addons: [
    "@storybook/addon-onboarding",
    "@chromatic-com/storybook",
    "@storybook/addon-docs"
  ],
  framework: {
    name: "@storybook/vue3-vite",
    options: {},
  },
  core: {
    builder: "@storybook/builder-vite",
    disableTelemetry: true,
  },
  staticDirs: ["../.msw/public"],
  async viteFinal(config) {
    return mergeConfig(config, {
      resolve: {
        alias: {
          "@": path.resolve(__dirname, "../src"),
        },
      },
    });
  },
};

export default config;
