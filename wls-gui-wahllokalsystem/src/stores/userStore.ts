import { acceptHMRUpdate, defineStore } from "pinia";
import { computed, ref } from "vue";

import { useUserService } from "@/composables/user/userService.ts";
import { createUserLocalDevelopment, User } from "@/types/User";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { getUser } = useUserService();

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

  async function loadUser() {
    try {
      user.value = await getUser();
    } catch {
      if (import.meta.env.DEV) {
        user.value = createUserLocalDevelopment();
      } else {
        user.value = null;
      }
    }
  }

  const currentUserWahlbezirkID = computed((): string | undefined => {
    return user.value?.wahlbezirkID;
  });

  const currentUserWahltagID = computed((): string | undefined => {
    return user.value?.wahltagID;
  });

  const currentUserWahlbezirksArt = computed(() => {
    return user.value.wahlbezirksArt;
  });

  function setUser(payload: User): void {
    user.value = payload;
  }

  return {
    user,
    loadUser,
    setUser,
    currentUserWahlbezirkID,
    currentUserWahltagID,
    currentUserWahlbezirksArt,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
