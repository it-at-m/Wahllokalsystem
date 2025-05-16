import { acceptHMRUpdate, defineStore } from "pinia";
import { computed, ref } from "vue";

import { useUserService } from "@/composables/user/userService.ts";
import { User, UserLocalDevelopment } from "@/types/User";

const { getUser } = useUserService();

export const useUserStore = defineStore("user", () => {
  const user = ref<User | null>(null);

  async function loadUser() {
    try {
      user.value = await getUser();
    } catch {
      if (import.meta.env.DEV) {
        user.value = UserLocalDevelopment();
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

  function setUser(payload: User | null): void {
    user.value = payload;
  }

  return {
    user,
    loadUser,
    setUser,
    currentUserWahlbezirkID,
    currentUserWahltagID,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
