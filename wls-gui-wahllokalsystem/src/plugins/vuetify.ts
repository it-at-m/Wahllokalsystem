import "vuetify/styles";

import {
  mdiHome,
  mdiMessageText,
  mdiRoutes,
  mdiSignalCellular3,
  mdiSignalOff,
} from "@mdi/js";
import { createVuetify } from "vuetify";
import { aliases, mdi } from "vuetify/iconsets/mdi-svg";

export default createVuetify({
  icons: {
    defaultSet: "mdi",
    aliases: {
      ...aliases,
      home: mdiHome,
      routes: mdiRoutes,
      messageText: mdiMessageText,
      signalOffline: mdiSignalOff,
      signalOnline: mdiSignalCellular3,
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
