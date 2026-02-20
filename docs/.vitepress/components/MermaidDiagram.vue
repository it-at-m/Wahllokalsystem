<template>
  <div ref="mermaidContainer"></div>
</template>

<script setup lang="ts">
import mermaid from "mermaid";
import { onMounted, ref, watch } from "vue";

const props = defineProps({
  diagram: {
    type: String,
    required: true,
  },
});

const mermaidContainer = ref(null);

const dia = ref(props.diagram);

const renderMermaid = () => {
  mermaid.initialize({ startOnLoad: true });
  mermaid.contentLoaded();
};

onMounted(() => {
  initMermaid();
  doRender(props.diagram);
});

watch(
  () => props.diagram,
  async (value) => {
    doRender(value);
  }
);

function initMermaid() {
  mermaid.initialize({ startOnLoad: true });
  mermaid.contentLoaded();
}

async function doRender(mermaidGraphCode: string) {
  const { svg, bindFunctions } = await mermaid.render(
    "renderId",
    mermaidGraphCode
  );
  mermaidContainer.value.innerHTML = svg;
  bindFunctions?.(mermaidContainer.value);
}
</script>
