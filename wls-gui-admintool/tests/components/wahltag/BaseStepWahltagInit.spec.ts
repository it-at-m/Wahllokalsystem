import type { VueWrapper } from "@vue/test-utils";

import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import { getSnapshotFilename } from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";

import BaseStepWahltagInit from "@/components/wahltag/BaseStepWahltagInit.vue";
import vuetify from "@/plugins/vuetify.ts";

const { prepareWahltagEvent } = useWahltagTestDataFactory();

describe("BaseStepWahltagInit.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseStepWahltagInit>>;

  beforeEach(() => {
    wrapper = mount(BaseStepWahltagInit, {
      global: { plugins: [vuetify] },
      props: {
        wahlterminDatenExists: true,
        wahltagEvent: prepareWahltagEvent().build(),
      },
    });
  });
  describe("visual logic", () => {
    it("should_showOverrideDialog_when_overrideIsClicked", async (context) => {
      await wrapper.findComponent('[data-test="override"]').trigger("click");

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
