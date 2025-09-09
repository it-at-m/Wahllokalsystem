<template>
  <v-hover v-slot="{ isHovering, props }">
    <v-list-item
      v-bind="props"
      :class="
        isHovering && callback
          ? 'px-3 py-0 bg-grey-lighten-3 cursor-pointer'
          : 'px-3 py-0 '
      "
    >
      <base-info-help-list-item-content
        v-if="callback"
        :class="LIST_ITEM_CONTENT_MARGIN"
        :icon="icon"
        :title="title"
        :text="text"
        @click="callback()"
      />
      <base-info-help-list-item-content
        v-else
        :class="LIST_ITEM_CONTENT_MARGIN"
        :icon="icon"
        :title="title"
        :text="text"
      />
    </v-list-item>
  </v-hover>
</template>
<script setup lang="ts">
import BaseInfoHelpListItemContent from "@/components/basisdaten/BaseInfoHelpListItemContent.vue";

const LIST_ITEM_CONTENT_MARGIN = "mx-0 my-1";

defineProps({
  title: { type: String, required: true },
  text: { type: String, required: false, default: null },
  icon: { type: String, required: true },
  callback: { type: Function, required: false, default: null },
});
</script>
