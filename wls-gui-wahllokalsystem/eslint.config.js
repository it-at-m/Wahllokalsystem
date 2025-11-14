import js from "@eslint/js";
import vuePrettierEslintConfigSkipFormatting from "@vue/eslint-config-prettier/skip-formatting";
import vueTsEslintConfig from "@vue/eslint-config-typescript";
import { ESLint } from "eslint";
import vueEslintConfig from "eslint-plugin-vue";

export default [
  ...ESLint.defaultConfig,
  js.configs.recommended,
  ...vueEslintConfig.configs["flat/recommended"],
  ...vueTsEslintConfig({
    extends: ["strict", "stylistic"],
  }),
  vuePrettierEslintConfigSkipFormatting,
  {
    ignores: [
      "dist",
      "target",
      "node_modules",
      "env.d.ts",
      "src/api/wls-clients/generated-*-api",
      "src/resources/openapis",
    ],
  },
  {
    rules: {
      "no-console": ["error", { allow: ["debug"] }],
      "vue/component-name-in-template-casing": [
        "error",
        "kebab-case",
        { registeredComponentsOnly: false },
      ],
      "no-restricted-exports": [
        "error",
        {
          restrictDefaultExports: {
            direct: true, // restricts `export default abc;`
            named: true, // restricts `export { abc as default };`
          },
        },
      ],
      "vue/html-self-closing": [
        "error",
        {
          html: {
            void: "never",
            normal: "always",
            component: "always",
          },
          svg: "always",
          math: "always",
        },
      ],
      "vue/no-multiple-template-root": ["error"],
      "vue/no-empty-component-block": ["error"],
    },
  },
  // overrides for specific files or directories
  {
    files: [
      ".storybook/*.ts",
      "stories/**",
      "*.config.{ts,js}",
      "src/plugins/**",
    ],
    rules: {
      "no-restricted-exports": "off", // deactivate rule for the above named directories
    },
  },
];
