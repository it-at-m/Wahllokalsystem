import type { Meta, StoryObj } from "@storybook/vue3-vite";

import { fn } from "storybook/test";
import { ref } from "vue";
import { VBtn } from "vuetify/components";

import { default as StoryComponent } from "@/components/wahltag/BaseDialogConfirmation.vue";

const meta: Meta<typeof StoryComponent> = {
  component: StoryComponent,
  argTypes: {
    onCancelDelete: {
      table: {
        disable: true,
      },
    },
    onConfirmDelete: {
      table: {
        disable: true,
      },
    },
  },
  args: {
    onCancelDelete: fn(),
    onConfirmDelete: fn(),
  },
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render(args) {
    const dialogRef = ref();
    const showDialog = () => {
      dialogRef.value.showDialog();
    };
    const onCancelDelete = () => {
      dialogRef.value.hideDialog();
    };
    return {
      components: {
        VBtn,
        StoryComponent,
      },
      setup() {
        return { args, dialogRef, showDialog, onCancelDelete };
      },
      template: `
        <div>
          <v-btn @click="showDialog">
            OPEN DIALOG
          </v-btn>
          <StoryComponent
          ref="dialogRef"
          v-bind="args"
          @cancel-delete="onCancelDelete"
          >
            Hi, i am the default slot content
          </StoryComponent>
        </div>`,
    };
  },
};
