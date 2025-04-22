import "vuetify/styles";

import {
  mdiContentSave,
  mdiDelete,
  mdiDiameterVariant,
  mdiEmail,
  mdiHome,
  mdiMessageText,
  mdiPlus,
  mdiPrinter,
  mdiReload,
  mdiRoutes,
  mdiSend,
  mdiSignalCellular3,
  mdiSignalOff,
  mdiTextBoxCheck,
  mdiToaster,
  mdiUpdate,
  mdiVote,
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
      textBoxCheck: mdiTextBoxCheck,
      signalOffline: mdiSignalOff,
      signalOnline: mdiSignalCellular3,
      reload: mdiReload,
      save: mdiContentSave,
      delete: mdiDelete,
      add: mdiPlus,
      send: mdiSend,
      updateTime: mdiUpdate,
      toaster: mdiToaster,
      printer: mdiPrinter,
      invalid: mdiDiameterVariant,
      wahlbezirksartUWB: mdiVote,
      wahlbezirksartBWB: mdiEmail,
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
