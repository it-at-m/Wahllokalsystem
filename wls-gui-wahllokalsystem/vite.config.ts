import { fileURLToPath, URL } from "node:url";

import vue from "@vitejs/plugin-vue";
import UnpluginFonts from "unplugin-fonts/vite";
import { defineConfig } from "vite";
import { VitePWA } from "vite-plugin-pwa";
import vuetify, { transformAssetUrls } from "vite-plugin-vuetify";

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
    VitePWA({
      registerType: "autoUpdate",
      injectRegister: "auto",
      strategies: "injectManifest", // makes it possible to use own service worker
      srcDir: "src", // custom sw file directory
      filename: "wahl-worker.js", // custom sw file
      injectManifest: {
        injectionPoint: undefined, // standard injection at end of sw file
        // globPatterns: ["**/*.{js,ts,css,html,ico,png,svg,json,vue,txt}"], // todo: files anpassen
        sourcemap: true,
      },
      manifest: {
        name: "Wahllokalsystem",
        short_name: "WLS",
        description: "",
        theme_color: "#546e7a",
        display: "standalone",
        icons: [],
      },
    }),
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
  // serves built solution from ./dist folder to preview (no hot reload)
  preview: {
    port: serverPort,
  },
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
});
