import type { User } from "@/types/User.ts";

import { acceptHMRUpdate, defineStore } from "pinia";
import { computed, ref } from "vue";

import { useUserService } from "@/composables/user/userService.ts";
import { createUserLocalDevelopment } from "@/types/User";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { getUser } = useUserService();

export const useUserStore = defineStore("user", () => {
  const defaultUser: User = {
    username: "",
    email: "",
    userEnabled: false,
    wahltagID: "",
    wahltag: "",
    wahlbezirkID: "",
    wahlbezirkNummer: "",
    wahlbezirksArt: WahlbezirksArtEnum.UWB,
    pin: "",
    authorities: new Set<string>(),
    wahlMetaData: [
      {
        wahlbezirkID: "",
        wahlnummer: "",
        wahlID: "",
      },
    ],
  };
  const user = ref<User>(defaultUser);

  async function loadUser() {
    try {
      user.value = await getUser();
    } catch {
      if (import.meta.env.DEV) {
        user.value = createUserLocalDevelopment();
      } else {
        user.value = defaultUser;
      }
    }
  }

  const currentUserWahlbezirkID = computed((): string | undefined => {
    return user.value?.wahlbezirkID;
  });

  const currentUserWahltagID = computed((): string | undefined => {
    return user.value?.wahltagID;
  });

  const currentUserWahltag = computed((): string | undefined => {
    return user.value.wahltag;
  });

  const currentUserWahlbezirksArt = computed((): WahlbezirksArtEnum => {
    return user.value.wahlbezirksArt;
  });

  const currentUserWahlbezirkNummer = computed((): string | undefined => {
    return user.value?.wahlbezirkNummer;
  });

  const currentUserHauptWahlID = computed((): string | undefined => {
    const smallestWbidWahlnummerObject = user.value?.wahlMetaData?.reduce(
      (smallest, current) => {
        return parseInt(current.wahlnummer) < parseInt(smallest.wahlnummer)
          ? current
          : smallest;
      }
    );
    return smallestWbidWahlnummerObject?.wahlID;
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
    currentUserWahltag,
    currentUserWahlbezirksArt,
    currentUserWahlbezirkNummer,
    currentUserHauptWahlID,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
