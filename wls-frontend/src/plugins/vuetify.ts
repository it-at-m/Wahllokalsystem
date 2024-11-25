import "vuetify/styles";

import {createVuetify} from "vuetify";
import {aliases, mdi} from "vuetify/iconsets/mdi-svg";
import {mdiHome} from "@mdi/js";

export default createVuetify({
  icons: {
    defaultSet: "mdi",
    aliases: {
      ...aliases,
      home: mdiHome,
    },
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
          success: "#69BE28",
          error: "#FF0000",
        },
      },
    },
  },
});
