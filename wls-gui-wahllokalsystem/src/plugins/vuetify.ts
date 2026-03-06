import "vuetify/styles";

import {
  mdiAlert,
  mdiArrowRightBold,
  mdiCancel,
  mdiCheckCircle,
  mdiCircleOffOutline,
  mdiCheckCircleOutline,
  mdiCloseCircleOutline,
  mdiCloudUpload,
  mdiContentSave,
  mdiContentSaveCheck,
  mdiDelete,
  mdiDiameterVariant,
  mdiEmail,
  mdiFileDocumentOutline,
  mdiHelpCircleOutline,
  mdiHome,
  mdiInformationOutline,
  mdiLockOutline,
  mdiLogout,
  mdiMapSearch,
  mdiPencil,
  mdiPhone,
  mdiPlus,
  mdiPlusCircle,
  mdiPrinter,
  mdiReload,
  mdiRemoteDesktop,
  mdiRoutes,
  mdiSend,
  mdiSignalCellular3,
  mdiSignalOff,
  mdiTextBoxCheck,
  mdiTextBoxOutline,
  mdiToaster,
  mdiUpdate,
  mdiVote,
} from "@mdi/js";
import { createVuetify } from "vuetify";
import { aliases, mdi } from "vuetify/iconsets/mdi-svg";

import { PRIMARY_COLOR } from "@/constants.ts";

export default createVuetify({
  icons: {
    defaultSet: "mdi",
    aliases: {
      ...aliases,
      home: mdiHome,
      routes: mdiRoutes,
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
      information: mdiInformationOutline,
      addCircle: mdiPlusCircle,
      help: mdiHelpCircleOutline,
      phone: mdiPhone,
      fileDocument: mdiFileDocumentOutline,
      mapSearch: mdiMapSearch,
      remoteDesktop: mdiRemoteDesktop,
      edit: mdiPencil,
      valid: mdiCheckCircle,
      summary: mdiTextBoxOutline,
      alert: mdiAlert,
      continue: mdiArrowRightBold,
      cloudUpload: mdiCloudUpload,
      logout: mdiLogout,
      locked: mdiLockOutline,
      saveSuccess: mdiContentSaveCheck,
      disabled: mdiCircleOffOutline,
      kandidatNonRemoved: mdiCheckCircleOutline,
      kandidatRemoved: mdiCloseCircleOutline,
    },
    sets: {
      mdi,
    },
  },
  theme: {
    themes: {
      light: {
        colors: {
          primary: PRIMARY_COLOR,
          secondary: "#FFCC00",
          accent: "#7BA4D9",
          info: "#0000f0",
          success: "#008c00",
          error: "#FF0000",
          warning: "#E07400",
        },
      },
    },
  },
  defaults: {
    VCardTitle: {
      class: "bg-grey-lighten-3 border-b border-grey-lighten-1 mb-2",
    },
    VNumberInput: {
      persistentClear: true,
      clearable: true,
    },
    VProgressLinear: {
      height: 50,
      color: "primary",
    },
    VAutocomplete: {
      persistentClear: true,
      clearable: true,
    },
    VTextField: {
      persistentClear: true,
      clearable: true,
    },
    VTextarea: {
      persistentClear: true,
      clearable: true,
    },
    VListItem: {
      prependGap: 10,
    },
  },
});
