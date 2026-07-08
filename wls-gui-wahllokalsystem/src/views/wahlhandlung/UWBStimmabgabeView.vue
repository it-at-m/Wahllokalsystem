<template>
  <v-card>
    <the-ungueltige-wahlscheine-verify-card />
    <base-wahlschliessung-card
      :type="
        isVorfaelleMaintained
          ? InputFeedbackTypeEnum.information
          : InputFeedbackTypeEnum.error
      "
    />
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseWahlschliessungCard from "@/components/wahlhandlung/BaseWahlschliessungCard.vue";
import TheUngueltigeWahlscheineVerifyCard from "@/components/wahlhandlung/TheUngueltigeWahlscheineVerifyCard.vue";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const { isVorfaelleMaintained } = storeToRefs(useEreignisStore());
</script>
