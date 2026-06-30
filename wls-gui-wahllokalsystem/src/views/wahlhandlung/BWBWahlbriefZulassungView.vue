<template>
    <the-beanstandete-wahlbriefe-tabs
        title-tab-one="Wahlbriefe zulassen oder zurückweisen"
    >
      <the-beanstandete-wahlbriefe-erfassen-card />
    </the-beanstandete-wahlbriefe-tabs>
</template>

<script setup lang="ts">
import TheBeanstandeteWahlbriefeErfassenCard from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeErfassenCard.vue";
import TheBeanstandeteWahlbriefeTabs
  from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeTabs.vue";
</script>
