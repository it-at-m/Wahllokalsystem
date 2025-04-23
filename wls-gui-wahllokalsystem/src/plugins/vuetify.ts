import "vuetify/styles";

import {
  mdiContentSave,
  mdiDelete,
  mdiDiameterVariant,
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
  defaults: {
    VCardTitle: {
      style: "background-color: #eeeeee; border-bottom: 1px solid #bcbcbc;",
      class: "mb-2",
    },
  },
});
