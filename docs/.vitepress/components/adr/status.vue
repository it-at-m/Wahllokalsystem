<template>
  <div style="margin-top: 4px">
    <span
      ><svg height="24px" viewBox="0 0 24 24">
        <path :d="svgPath" :fill="svgFillColor"></path></svg
    ></span>
    <span class="statusText">{{ statusI18nText }}</span>
  </div>
</template>

<script setup>
import { useStatus, Status } from "../../composables/status";
import { useData } from "vitepress";
import { computed } from "vue";

const props = defineProps({
  status: {
    type: Status,
    required: true,
  },
});

const { lang } = useData();
const statusComposable = useStatus();

const statusI18nText = computed(() =>
  statusComposable.statusToI18NTextOrEmpty(lang.value, props.status),
);

const svgPath = computed(() =>
  statusComposable.statusToIconPathOrEmpty(props.status),
);

const svgFillColor = computed(() =>
  statusComposable.statusToColorOrEmpty(props.status),
);
</script>

<style scoped>
div {
  display: flex;
}

.statusText {
  margin-left: 4px;
}
</style>
