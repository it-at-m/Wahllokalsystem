import type { CommandHandler } from "@/types/dse/stimmzettelerfassung/command/CommandHandler.ts";

import { useAddInvalidVotesToSingleKandidatHandler } from "@/composables/dse/stimmzettelerfassung/command/addInvalidVotesToSingleKandidatHandler.ts";
import { useAddStreichungToKandidatenRangeHandler } from "@/composables/dse/stimmzettelerfassung/command/addStreichungToKandidatenRangeHandler.ts";
import { useAddStreichungToSingleKandidatHandler } from "@/composables/dse/stimmzettelerfassung/command/addStreichungToSingleKandidatHandler.ts";
import { useAddVotesToKandidatenRangeHandler } from "@/composables/dse/stimmzettelerfassung/command/addVotesToKandidatenRangeHandler.ts";
import { useAddVotesToSingleKandidatHandler } from "@/composables/dse/stimmzettelerfassung/command/addVotesToSingleKandidatHandler.ts";
import { useAddVotesToWahlvorschlagHandler } from "@/composables/dse/stimmzettelerfassung/command/addVotesToWahlvorschlagHandler.ts";
import { useRemoveVotesFromSingleKandidatHandler } from "@/composables/dse/stimmzettelerfassung/command/removeVotesFromSingleKandidatHandler.ts";

export const COMMAND_HANDLERS: CommandHandler[] = [
  useAddVotesToSingleKandidatHandler(),
  useAddInvalidVotesToSingleKandidatHandler(),
  useAddVotesToKandidatenRangeHandler(),
  useAddStreichungToSingleKandidatHandler(),
  useAddStreichungToKandidatenRangeHandler(),
  useAddVotesToWahlvorschlagHandler(),
  useRemoveVotesFromSingleKandidatHandler(),
];
