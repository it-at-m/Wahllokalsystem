import type { CommandHandler } from "@/types/dse/stimmzettelerfassung/command/CommandHandler.ts";

import { useAddVotesToSingleKandidatHandler } from "@/composables/dse/stimmzettelerfassung/command/addVotesToSingleKandidatHandler.ts";

export const COMMAND_HANDLERS: CommandHandler[] = [
  useAddVotesToSingleKandidatHandler(),
];
