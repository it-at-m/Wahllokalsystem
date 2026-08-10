<template>
  <div>
    <v-card>
      <v-card-title class="d-flex align-center justify-space-between">
        <span>Statusübersicht Stimmzettelerfassung</span>
        <div class="d-flex flex-column align-start">
          <base-latest-load-div :last-loading-date="lastLoading" />
        </div>
      </v-card-title>
      <v-card-text>
        <base-progress-linear
          titel="Stimmzettelerfassung abgeschlossen"
          titel-class="d-flex align-center justify-center"
          data-test="base-progress-success"
          :is-loading="isAktualisiserenLoading"
          :current="abgeschlossenNumberOfTeams"
          :total="totalNumberOfTeams"
          color="success"
        />
        <v-list class="pt-0">
          <v-list-item
            key="team"
            class="py-2"
          >
            <v-list-item-title class="font-weight-bold">
              Team
            </v-list-item-title>

            <template #append>
              <div
                class="font-weight-bold d-flex align-center justify-center"
                :style="{ 'min-width': minWidth, textAlign: 'center' }"
              >
                Status
              </div>
            </template>
          </v-list-item>
          <v-divider
            thickness="2"
            color="black"
          />
          <base-team-status-list-item
            v-for="item in teamstatusList"
            :key="item.teamID"
            :min-width="minWidth"
            :team-entry="item"
          />
        </v-list>
      </v-card-text>
      <v-card-actions>
        <base-button-refresh
          :active="isRefreshBtnActive"
          :loading="isAktualisiserenLoading"
          @click="onMonitoringSynchronisierenClicked"
        />
        <base-text-button
          :active="beschlussfassungStartenBtnActive"
          @click="onBeschlussfassungStartenClicked"
          >Beschlussfassung starten</base-text-button
        >
      </v-card-actions>
    </v-card>
    <base-dialog
      :visible="beschlussfassungStartenDialogVisible"
      dialogtitle="Beschlussfassung starten"
      icon="$information"
      confirmtext="Bestätigen"
      canceltext="Abbrechen"
      @cancel="beschlussfassungStartenDialogVisible = false"
    >
      <div>
        <h1>🚧 TBD 🚧</h1>
      </div>
    </base-dialog>
  </div>
</template>
<script setup lang="ts">
import { computed, ref } from "vue";
import { useRoute } from "vue-router";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseLatestLoadDiv from "@/components/common/div/BaseLatestLoadDiv.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import BaseTeamStatusListItem from "@/components/dse/BaseTeamStatusListItem.vue";
import { useMonitoringViewUtils } from "@/composables/dse/monitoringViewUtils.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const minWidth = "220px";
const beschlussfassungStartenDialogVisible = ref(false);

const route = useRoute();
const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";

const {
  teamstatusList,
  lastLoading,
  isAktualisiserenLoading,
  onMonitoringSynchronisierenClicked,
} = useMonitoringViewUtils(wahlID, wahlbezirkID);

const beschlussfassungStartenBtnActive = computed(() =>
  teamstatusList.value.every(
    (team) => team.status === StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
  )
);
const isRefreshBtnActive = computed(
  () => !beschlussfassungStartenBtnActive.value
);

const totalNumberOfTeams = computed(() => teamstatusList.value.length);

const abgeschlossenNumberOfTeams = computed(() => {
  return teamstatusList.value.filter(
    (team) => team.status === StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
  ).length;
});

async function onBeschlussfassungStartenClicked() {
  beschlussfassungStartenDialogVisible.value = true;
}
</script>
