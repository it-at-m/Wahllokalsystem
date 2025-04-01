import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useUserStore } from "@/stores/user";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

const { getEreignisse, saveEreignisse } = useEreignisService();

export const storeID = "vorfaelleundvorkommnisse";

export const useEreignisStore = defineStore(storeID, () => {
  const userStore = useUserStore();

  const wahlbezirkEreignisse = ref<WahlbezirkEreignisse>(
    WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse()
  );

  function addEreignis() {
    wahlbezirkEreignisse.value.ereigniseintraege?.push({
      uhrzeit: new Date(),
      beschreibung: "",
      ereignisart: "VORFALL",
    });
  }

  async function loadEreignisse() {
    const currentUserWahlbezirkID = getUsersWahlbezirkID();
    if (currentUserWahlbezirkID) {
      wahlbezirkEreignisse.value = await getEreignisse(currentUserWahlbezirkID);
    }
  }

  async function sendEreignisse() {
    const currentUserWahlbezirkID = getUsersWahlbezirkID();
    if (currentUserWahlbezirkID) {
      await saveEreignisse(currentUserWahlbezirkID, wahlbezirkEreignisse.value);
    }
  }

  function getUsersWahlbezirkID(): string | undefined {
    return userStore.getUser?.wahlbezirkID;
  }

  return {
    wahlbezirkEreignisse,
    loadEreignisse,
    sendEreignisse,
    addEreignis,
  };
});
