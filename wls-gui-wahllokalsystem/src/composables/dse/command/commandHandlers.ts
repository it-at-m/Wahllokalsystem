import type { CommandHandler } from "@/types/dse/command/CommandHandler.ts";

import { useAddInvalidVotesToSingleKandidatHandler } from "@/composables/dse/command/addInvalidVotesToSingleKandidatHandler.ts";
import { useAddStreichungToKandidatenRangeHandler } from "@/composables/dse/command/addStreichungToKandidatenRangeHandler.ts";
import { useAddStreichungToSingleKandidatHandler } from "@/composables/dse/command/addStreichungToSingleKandidatHandler.ts";
import { useAddVotesToKandidatenRangeHandler } from "@/composables/dse/command/addVotesToKandidatenRangeHandler.ts";
import { useAddVotesToSingleKandidatHandler } from "@/composables/dse/command/addVotesToSingleKandidatHandler.ts";
import { useAddVotesToWahlvorschlagHandler } from "@/composables/dse/command/addVotesToWahlvorschlagHandler.ts";
import {
  useRemoveVotesFromSingleKandidatHandler
} from "@/composables/dse/command/removeVotesFromSingleKandidatHandler.ts";

export const COMMAND_HANDLERS: CommandHandler[] = [
  useAddVotesToSingleKandidatHandler(),
  useAddInvalidVotesToSingleKandidatHandler(),
  useAddVotesToKandidatenRangeHandler(),
  useAddStreichungToSingleKandidatHandler(),
  useAddStreichungToKandidatenRangeHandler(),
  useAddVotesToWahlvorschlagHandler(),
  useRemoveVotesFromSingleKandidatHandler(),
];
