<template>
  <v-row>
    <v-col
      cols="1"
      class="text-center mt-5"
      >{{ lineNumber }}</v-col
    >
    <v-col cols="9">
      <v-row>
        <v-col cols="4">
          <v-autocomplete
            v-model="ereignisModel.validity"
            item-title="title"
            item-value="value"
            label="Gueltigkeit"
            :items="validityDropdownItems"
            auto-select-first
            :clearable="false"
            :rules="[required]"
            :data-test="`bedenklicherStimmzettel-input-${lineNumber - 1}`"
          />
        </v-col>
        <v-col cols="5">
          <v-checkbox
            class="text-no-wrap"
            v-model="ereignisModel.supplements"
            :disabled="supplementSelectionDisabled"
            :label="
              supplementEnumToDisplayString(
                SupplementEnum.TOO_MANY_LISTENKREUZE
              )
            "
            value="TOO_MANY_LISTENKREUZE"
            density="compact"
            hide-details
          />
          <v-checkbox
            v-model="ereignisModel.supplements"
            :disabled="supplementSelectionDisabled"
            value="TOO_MANY_SINGLE_KANDIDAT_VOTES"
            density="compact"
            hide-details
          >
            <template #label>
              <span class="text-no-wrap"> {{
                  supplementEnumToDisplayString(
                SupplementEnum.TOO_MANY_SINGLE_KANDIDAT_VOTES)
                }}
              </span>
            </template>
          </v-checkbox>
        </v-col>
      </v-row>
    </v-col>
    <v-col
      cols="1"
      class="d-flex justify-end align-self-start"
    >
      <v-icon
        class="ma-6"
        data-test="delete-ereignis-icon"
        icon="$delete"
        title="Löschen"
        @click="onDeleteIconClicked"
      />
    </v-col>
    <v-col
      cols="1"
      class="d-flex justify-end align-self-start"
    >
      <the-stimmzettel-beschlussfassung-row-status-icon
        :index="lineNumber - 1"
        :model-value="ereignisModel"
        class="ma-6"
      />
    </v-col>
    <v-divider class="ma-3" />
  </v-row>
</template>

<script setup lang="ts">
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";
import type { PropType } from "vue";

import { computed, watch } from "vue";

import TheStimmzettelBeschlussfassungRowStatusIcon from "@/components/ergebnismeldung/MBW/stapelE/TheStimmzettelBeschlussfassungRowStatusIcon.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useBedenklicherStimmzettelMapper } from "@/composables/ergebnismeldung/MBW/bedenklicherStimmzettelMapper.ts";
import { SupplementEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/SupplementEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";

const { required } = useRules();

const { validityEnumToDisplayString, supplementEnumToDisplayString } =
  useBedenklicherStimmzettelMapper();

defineProps({
  lineNumber: {
    type: Number,
    required: true,
  },
});

const validityDropdownItems = computed(() => {
  return Object.values(ValidityEnum).map((enumValue) => ({
    title: validityEnumToDisplayString(enumValue),
    value: enumValue,
  }));
});

const ereignisModel = defineModel({
  type: Object as PropType<BedenklicherStimmzettel>,
  required: true,
});

const supplementSelectionDisabled = computed((): boolean => {
  return ValidityEnum.PARTIAL_VALID !== ereignisModel.value.validity;
});

watch(
  () => ereignisModel.value.validity,
  (newValidity, oldValidity) => {
    if (
      oldValidity === ValidityEnum.PARTIAL_VALID &&
      newValidity !== ValidityEnum.PARTIAL_VALID
    ) {
      ereignisModel.value.supplements = [];
    }
  }
);

const emit = defineEmits<{
  delete: [bedenklicherStimmzettel: BedenklicherStimmzettel];
}>();

function onDeleteIconClicked() {
  emit("delete", ereignisModel.value);
}
</script>
