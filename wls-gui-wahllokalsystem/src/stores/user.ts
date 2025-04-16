import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

import User from "@/types/User";

export interface UserState {
  user: User | null;
  wahlbezirksArt: WahlbezirksArtEnum;
}

export const useUserStore = defineStore("user", () => {
  const user = ref<User | null>(null);
  const wahlbezirksArt = ref<WahlbezirksArtEnum>("UWB");

  const getUser = computed((): User | null => {
    return user.value;
  });

  const getWahlbezirksArt = computed((): WahlbezirksArtEnum => {
    return wahlbezirksArt.value;
  });

  function setUser(payload: User | null): void {
    user.value = payload;
  }

  function setWahlbezirksArt(art: WahlbezirksArtEnum): void {
    wahlbezirksArt.value = art;
  }

  return { getUser, setUser, getWahlbezirksArt, setWahlbezirksArt };
});
