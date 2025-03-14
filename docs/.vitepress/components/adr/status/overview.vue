<template>
  <div
    v-for="status in Status"
    :key="status"
  >
    <adr-status :status="status"></adr-status>
  </div>
</template>

<script setup>
import { useData } from "vitepress";

import { Status, useStatus } from "../../../composables/status";

const statusComposable = useStatus();
const { lang } = useData();
</script>
