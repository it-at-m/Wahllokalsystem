<template>
  <base-beanstandete-wahlbriefe-tabs
    title-tab-data-input="Wahlbriefe zulassen oder zurückweisen"
    :has-nachlieferungen="false"
    @save="onSaveClicked"
  />
</template>

<script setup lang="ts">
import BaseBeanstandeteWahlbriefeTabs from "@/components/wahlhandlung/beanstandeteWahlbriefe/BaseBeanstandeteWahlbriefeTabs.vue";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import router from "@/plugins/router.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { beanstandeteWahlbriefeActions } = useWahlenStore();
const { getNextRoute } = useNavigationService();

async function onSaveClicked() {
  await beanstandeteWahlbriefeActions.saveBeanstandeteWahlbriefe();
  await router.push(getNextRoute());
}
</script>
