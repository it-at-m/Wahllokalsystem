import type { Component } from "vue";

import { defineComponent, h, KeepAlive } from "vue";

import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";

export function createKeepAliveComponent(
  wahlId: string,
  wahlbezirkId: string,
  component: Component
) {
  return defineComponent({
    render() {
      return h(KeepAlive, null, {
        default: () =>
          h(component, {
            wahlId,
            wahlbezirkId,
          }),
      });
    },
  });
}
