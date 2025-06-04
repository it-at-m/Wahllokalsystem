<template>
  <v-hover>
    <template v-slot:default="{ isHovering, props }">
      <v-list-item
        v-bind="props"
        :class="
          isHovering && callback ? 'px-3 py-0 bg-grey-lighten-3' : 'px-3 py-0 '
        "
      >
        <the-info-help-list-item-content
          v-if="callback"
          :icon="icon"
          :title="title"
          :text="text"
          @click="callback()"
        />

        <the-info-help-list-item-content
          v-else
          :icon="icon"
          :title="title"
          :text="text"
        />
      </v-list-item>
    </template>
  </v-hover>
</template>
<script setup lang="ts">
import { VHover, VListItem } from "vuetify/components";

import TheInfoHelpListItemContent from "@/components/basisdaten/BaseInfoHelpListItemContent.vue";

defineProps({
  title: String,
  text: String,
  icon: String,
  callback: Function,
});
</script>
