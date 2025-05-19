import { acceptHMRUpdate, defineStore } from "pinia";
import { computed, ref } from "vue";

import { useUserService } from "@/composables/user/userService.ts";
import { createUserLocalDevelopment, User } from "@/types/User";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { getUser } = useUserService();

export const useUserStore = defineStore("user", () => {
  const user = ref<User | null>(null);

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

  const currentUserWahlbezirksArt = computed((): WahlbezirksArtEnum => {
    return user.value?.wahlbezirksArt ?? WahlbezirksArtEnum.BWB;
  });

    const currentUserWahlbezirkNummer = computed((): string | undefined => {
        return user.value?.wahlbezirkNummer;
    });

  function setUser(payload: User | null): void {
    user.value = payload;
  }

  return {
    user,
    loadUser,
    setUser,
    currentUserWahlbezirkID,
    currentUserWahltagID,
    currentUserWahlbezirksArt,
      currentUserWahlbezirkNummer,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
