<template>
  <div v-for="status in Status" :key="status">
    <adr-status :status="status"></adr-status>
  </div>
</template>

<script setup>
import {Status, useStatus} from "../../../composables/status";
import {useData} from "vitepress";

const statusComposable = useStatus();
const {lang} = useData();

</script>