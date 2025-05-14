import { acceptHMRUpdate, defineStore } from "pinia";
import { computed, ref } from "vue";

import { User } from "@/types/User";

export interface UserState {
  user: User | null;
}

export const useUserStore = defineStore("user", () => {
  const user = ref<User | null>(null);

  const currentUserWahlbezirkID = computed((): string | undefined => {
    return user.value?.wahlbezirkID;
  });

  const currentUserWahltagID = computed((): string | undefined => {
    return user.value?.wahltagID;
  });

  const currentUserHauptWahlID = computed((): string | undefined => {
    const smallestWbidWahlnummerObject =
      user.value?.wbid_wahlnummer?.wbid_wahlnummer?.reduce(
        (smallest, current) => {
          return parseInt(current.wahlnummer) < parseInt(smallest.wahlnummer)
            ? current
            : smallest;
        }
      );
    return smallestWbidWahlnummerObject?.wahlID;
  });

  const getUser = computed((): User | null => {
    return user.value;
  });

  function setUser(payload: User | null): void {
    user.value = payload;
  }

  return {
    getUser,
    setUser,
    currentUserWahlbezirkID,
    currentUserWahltagID,
    currentUserHauptWahlID,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
