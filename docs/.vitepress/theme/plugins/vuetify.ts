import "vuetify/styles";

import { createVuetify } from "vuetify";
import { mdi } from "vuetify/iconsets/mdi-svg";

export default createVuetify({
  icons: {
    defaultSet: "mdi",
    sets: {
      mdi,
    },
  },
  theme: {
    themes: {
      light: {
        colors: {
          primary: "#546e7a",
          secondary: "#FFCC00",
          accent: "#7BA4D9",
          success: "#7fbf7f",
          error: "#FF0000",
          warn: "#ffe993",
        },
      },
    },
  },
  defaults: {},
});
