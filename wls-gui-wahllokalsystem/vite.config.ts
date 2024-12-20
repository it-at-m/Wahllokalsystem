import * as node_fs from "fs";
import * as fs from "node:fs";
import path from "node:path";

import vue from "@vitejs/plugin-vue";
import { parse } from "jsonc-parser";
import UnpluginFonts from "unplugin-fonts/vite";
import { defineConfig } from "vite";
import vuetify, { transformAssetUrls } from "vite-plugin-vuetify";
import tsconfigPaths from "vite-tsconfig-paths";

const serverPort = 8400;

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue({
      template: { transformAssetUrls },
      features: {
        optionsAPI: false,
      },
    }),
    vuetify({
      autoImport: false,
    }),
    UnpluginFonts({
      google: {
        families: [
          {
            name: "Roboto",
            styles: "wght@100;300;400;500;700;900",
          },
        ],
      },
    }),
    tsconfigPaths(),
  ],
  server: {
    port: serverPort,
    proxy: {
      "/api": "http://localhost:8083",
      "/actuator": "http://localhost:8083",
    },
    host: true,
    hmr: {
      clientPort: serverPort,
    },
  },
  resolve: {
    //getPathsFromTsConfig(),
    alias: {
      //"@": fileURLToPath(new URL("./src", import.meta.url)),
      "@": "./src",
    },
  },
});

/*
// siehe https://stackoverflow.com/questions/77249074/how-do-i-use-typescript-path-aliases-in-vite/78658715#78658715
interface TsConfig {
  compilerOptions: {
    paths: {
      [key: string]: string[];
    };
  };
}

function get_paths_from_tsconfig(): Record<string, string> {
  const tsconfig_s: string = node_fs
    .readFileSync("./tsconfig.json", "utf-8")
    .replace(/\/\/.*$/gm, ""); // Entfernen von Kommentaren
  const tsconfig: TsConfig = JSON.parse(tsconfig_s);
  const aliases: Record<string, string> = {};
  for (const [key, value] of Object.entries(tsconfig.compilerOptions.paths)) {
    if (Array.isArray(value) && value.length > 0) {
      aliases[key] = path.resolve(__dirname, value[0]);
    }
  }
  return aliases;
}*/

// siehe https://stackoverflow.com/questions/77249074/how-do-i-use-typescript-path-aliases-in-vite/79171421#79171421
interface TsConfig {
  compilerOptions: {
    paths: Record<string, string[]>;
  };
}

function getPathsFromTsConfig(): Record<string, string> {
  const tsconfig: TsConfig = parse(fs.readFileSync("./tsconfig.json", "utf-8"));
  const aliases: Record<string, string> = {};
  for (const [key, value] of Object.entries(tsconfig.compilerOptions.paths)) {
    const cleanKey = key.replace("/*", "");
    const cleanValue = value[0].replace("/*", "");
    aliases[cleanKey] = path.resolve(__dirname, cleanValue);
  }
  return aliases;
}
