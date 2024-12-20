import { fileURLToPath } from "node:url";

import tsconfigPaths from "vite-tsconfig-paths";
import { defineConfig, mergeConfig } from "vitest/config";

import viteConfig from "./vite.config";

export default mergeConfig(
  viteConfig,
  defineConfig({
    plugins: [tsconfigPaths()],
    resolve: {
      alias: {
        //"@": fileURLToPath(new URL("./src", import.meta.url)),
        "@": "./src",
      },
    },
    test: {
      environment: "jsdom",
      include: ["./app/**/*.{test,spec}.{ts,tsx}"],
      root: fileURLToPath(new URL("./", import.meta.url)),
      server: {
        deps: {
          inline: ["vuetify"],
        },
      },
    },
  })
);
