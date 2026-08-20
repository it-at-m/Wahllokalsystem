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
              <div class="font-weight-bold align-center justify-center">
                Stimmzettelerfassung
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
            :wahl-i-d="wahlID"
            :wahlbezirk-i-d="wahlbezirkID"
            :is-wieder-oeffnen-button-disabled="
              item.status !==
                StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN ||
              workflowStatus?.status ===
                StimmzettelerfassungStatusEnum.SteAbgeschlossen
            "
            @open-stimmzettelerfassung="
              onOpenStimmzettelerfassungClicked(item.teamID)
            "
          />
        </v-list>
      </v-card-text>
      <v-card-actions>
        <base-button-refresh
          :active="isRefreshBtnActive"
          :loading="isAktualisiserenLoading"
          @click="onAktualisierenClicked"
        />
        <base-text-button
          :active="beschlussfassungStartenBtnActive"
          :is-disabled="isBeschlussfassungStartenBtnDisabled"
          @click="onBeschlussfassungStartenClicked"
          >Beschlussfassung starten</base-text-button
        >
      </v-card-actions>
    </v-card>
    <the-beschlussfassung-starten-dialog
      v-model="beschlussfassungStartenDialogVisible"
      :wahl-id="wahlID"
      :wahlbezirk-id="wahlbezirkID"
      :teamstatus-list="teamstatusList"
    />
  </div>
</template>
<script setup lang="ts">
import { computed, ref } from "vue";
import { useRoute } from "vue-router";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseLatestLoadDiv from "@/components/common/div/BaseLatestLoadDiv.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import BaseTeamStatusListItem from "@/components/dse/BaseTeamStatusListItem.vue";
import TheBeschlussfassungStartenDialog from "@/components/dse/TheBeschlussfassungStartenDialog.vue";
import { useDseWorkflowStatusService } from "@/composables/dse/dseWorkflowStatusService.ts";
import { useMonitoringViewUtils } from "@/composables/dse/monitoringViewUtils.ts";
import { useStimmzettelErfassungViewUtils } from "@/composables/dse/stimmzettelErfassungViewUtils.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";
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
const { workflowStatus, loadDseWorkflowStatus } = useDseWorkflowStatusService();

const beschlussfassungStartenBtnActive = computed(() =>
  teamstatusList.value.every(
    (team) => team.status === StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
  )
);
const isBeschlussfassungStartenBtnDisabled = computed(
  () =>
    !beschlussfassungStartenBtnActive.value ||
    workflowStatus.value?.status !==
      StimmzettelerfassungStatusEnum.SteBearbeitung
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
async function onAktualisierenClicked() {
  await loadDseWorkflowStatus(wahlID, wahlbezirkID, false);
  await onMonitoringSynchronisierenClicked(true);
}

async function onOpenStimmzettelerfassungClicked(teamID: string) {
  const { sendStatusInBearbeitung } = useStimmzettelErfassungViewUtils(
    wahlID,
    wahlbezirkID,
    teamID
  );

  await sendStatusInBearbeitung(true);
  await onMonitoringSynchronisierenClicked();
}
</script>
