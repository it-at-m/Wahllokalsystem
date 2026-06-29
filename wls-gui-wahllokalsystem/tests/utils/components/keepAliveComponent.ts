import type { Component } from "vue";

import { defineComponent, h, KeepAlive } from "vue";

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
