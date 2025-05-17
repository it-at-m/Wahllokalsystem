import { acceptHMRUpdate, defineStore } from "pinia";
import { computed, ref } from "vue";

import { User } from "@/types/User";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface UserState {
  user: User | null;
}

export const useUserStore = defineStore("user", () => {
  const user = ref<User>({
    authorities: [],
    department: "",
    displayName: "",
    email: "",
    givenname: "",
    lhmObjectID: "",
    memberof: [],
    preferred_username: "",
    sub: "",
    surname: "",
    telephoneNumber: "",
    user_roles: [],
    username: "",
    wahlbezirkID: "",
    wahlbezirksArt: WahlbezirksArtEnum.UWB,
    wahltagID: "",
  });

  const currentUserWahlbezirkID = computed((): string | undefined => {
    return user.value?.wahlbezirkID;
  });

  const currentUserWahltagID = computed((): string | undefined => {
    return user.value?.wahltagID;
  });

  const currentUserWahlbezirkArt = computed(() => {
    return user.value.wahlbezirksArt;
  });

  const getUser = computed((): User | null => {
    return user.value;
  });

  function setUser(payload: User): void {
    user.value = payload;
  }

  return {
    getUser,
    setUser,
    currentUserWahlbezirkID,
    currentUserWahltagID,
    currentUserWahlbezirkArt,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
