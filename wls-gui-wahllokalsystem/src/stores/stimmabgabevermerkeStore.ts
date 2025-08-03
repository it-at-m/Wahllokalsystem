import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

export const useStimmabgabevermerkeStore = defineStore(
  "stimmabgabevermerke",
  () => {
    const stimmabgabevermerke = ref<Stimmabgabevermerke | null>(null);

    return { stimmabgabevermerke };
  }
);
