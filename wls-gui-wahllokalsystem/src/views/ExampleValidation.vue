<template>
  <v-card>
    <v-col class="text-center">
      <h2>This view shows how validation with rules and forms work</h2>
    </v-col>
    <div>
      <v-tabs
        v-model="tab"
        align-tabs="center"
      >
        <v-tab value="0">input fields</v-tab>
        <v-tab value="1">form</v-tab>
      </v-tabs>
      <v-tabs-window v-model="tab">
        <v-tabs-window-item value="0">
          <v-container class="d-flex flex-column align-center mt-5 mb-5">
            <v-text-field
              min-width="400"
              label="Input required"
              clearable
              :rules="[REQUIRED]"
            ></v-text-field>
            <v-text-field
              min-width="400"
              label="Input has to be between 2 and 10"
              :rules="[MIN_LENGTH(2), MAX_LENGTH(10)]"
            ></v-text-field>
          </v-container>
        </v-tabs-window-item>
        <v-tabs-window-item value="1">
          <v-container class="d-flex flex-column align-center mt-5 mb-5">
            <p>
              Validation will only trigger, when the submit button is clicked
            </p>
            <v-form
              class="mt-5"
              validate-on="submit lazy"
              @submit.prevent="submit"
            >
              <v-text-field
                :rules="[REQUIRED]"
                clearable
                label="Input required"
                min-width="400"
              ></v-text-field>
              <v-text-field
                :rules="[MAX_LENGTH(20)]"
                label="Input cant be longer then 20"
                min-width="400"
              ></v-text-field>
              <v-text-field
                :rules="[MIN_LENGTH(5)]"
                label="Input cant be shorter than 5"
                min-width="400"
              ></v-text-field>
              <v-btn
                primary
                class="mt-2"
                text="Submit"
                type="submit"
                block
              ></v-btn>
            </v-form>
          </v-container>
        </v-tabs-window-item>
      </v-tabs-window>
    </div>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  VBtn,
  VCard,
  VCol,
  VForm,
  VTab,
  VTabs,
  VTabsWindow,
  VTabsWindowItem,
  VTextField,
  VContainer,
} from "vuetify/components";

import { MAX_LENGTH, MIN_LENGTH, REQUIRED } from "@/util/rules";

const tab = ref(null);

function submit() {
  // do nothing
}
</script>
