import type { User } from "@/types/User.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useCryptoUtils } from "@/composables/crypto/cryptoUtils.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { useUserService } from "@/composables/user/userService.ts";
import { createUserLocalDevelopment } from "@/types/User.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { getUser } = useUserService();
const { importKey } = useCryptoUtils();
const { registerStoreHMR } = useHmrUpdate();

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
    authorities: [],
    wahlMetaData: [
      {
        wahlbezirkID: "",
        wahlnummer: "",
        wahlID: "",
      },
    ],
  };
  const user = ref<User>(defaultUser);
  const cryptoKey = ref();

  async function loadUser() {
    try {
      user.value = await getUser();
    } catch (e) {
      if (import.meta.env.DEV) {
        user.value = createUserLocalDevelopment();
        throw e;
      } else {
        user.value = defaultUser;
        throw e;
      }
    } finally {
      const cryptoKey = await importKey(user.value.pin);
      const indexDBSingleton = useIndexDB();
      indexDBSingleton.setKey(cryptoKey);
      if (navigator.serviceWorker.controller) {
        navigator.serviceWorker.controller.postMessage({
          type: "PIN",
          payload: cryptoKey,
        });
      }
    }
  }

  const currentUserWahlbezirkID = computed((): string => {
    return user.value.wahlbezirkID;
  });

  const currentUserWahltagID = computed((): string => {
    return user.value.wahltagID;
  });

  const currentUserWahltag = computed((): string => {
    return user.value.wahltag;
  });

  const isUWB = computed((): boolean => {
    return user.value.wahlbezirksArt === WahlbezirksArtEnum.UWB;
  });

  const isBWB = computed((): boolean => {
    return user.value.wahlbezirksArt === WahlbezirksArtEnum.BWB;
  });

  const currentUserWahlbezirksArt = computed((): WahlbezirksArtEnum => {
    return user.value.wahlbezirksArt;
  });

  const currentUserWahlbezirkNummer = computed((): string => {
    return user.value.wahlbezirkNummer;
  });

  const currentUserHauptWahlID = computed((): string => {
    const smallestWbidWahlnummerObject = user.value.wahlMetaData?.reduce(
      (smallest, current) => {
        return parseInt(current.wahlnummer) < parseInt(smallest.wahlnummer)
          ? current
          : smallest;
      }
    );
    return smallestWbidWahlnummerObject?.wahlID;
  });

  const currentUserWahlMetadata = computed(() => {
    return user.value.wahlMetaData;
  });

  function getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID: string) {
    const metadata = currentUserWahlMetadata.value.find((metadata) => {
      return metadata.wahlID === wahlID;
    });

    return metadata?.wahlbezirkID;
  }

  function setUser(payload: User): void {
    user.value = payload;
  }

  return {
    user,
    cryptoKey,
    loadUser,
    setUser,
    getWahlbezirkIdFromWahlMetaDataByWahlId,
    currentUserWahlbezirkID,
    currentUserWahltagID,
    currentUserWahltag,
    currentUserWahlbezirksArt,
    currentUserWahlbezirkNummer,
    currentUserHauptWahlID,
    currentUserWahlMetadata,
    isUWB,
    isBWB,
  };
});

registerStoreHMR(useUserStore);
