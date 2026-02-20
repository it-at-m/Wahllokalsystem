// @ts-expect-error: "TS2307 cannot find module" is a false positive here
import "vuetify/styles";

import { mdiRefresh } from "@mdi/js";
import { createVuetify } from "vuetify";
import { aliases, mdi } from "vuetify/iconsets/mdi-svg";
import { de } from "vuetify/locale";

export default createVuetify({
  icons: {
    defaultSet: "mdi",
    aliases: {
      ...aliases,
      refresh: mdiRefresh,
    },
    sets: {
      mdi,
    },
  },
  locale: {
    locale: "de",
    messages: { de },
  },
  date: {
    formats: {
      /* alias and object by options:
      https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#syntax
      example:
      wlsDateFormat: {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
      }
      */
    },
  },
  theme: {
    themes: {
      light: {
        colors: {
          primary: "#333333",
          secondary: "#FFCC00",
          accent: "#7BA4D9",
          success: "#69BE28",
          error: "#FF0000",
        },
      },
    },
  },
  defaults: {
    VAutocomplete: {
      variant: "outlined",
    },
    VProgressLinear: {
      height: "16",
    },
    VDialog: {
      persistent: true,
      maxWidth: 600,
    },
    VBtn: {
      variant: "elevated",
      activeColor: "primary",
    },
    VCardActions: {
      VBtn: {
        variant: "elevated",
        activeColor: "primary",
      },
    },
  },
});
