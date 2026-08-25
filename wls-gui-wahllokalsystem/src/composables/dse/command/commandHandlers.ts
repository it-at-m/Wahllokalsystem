import type { CommandHandler } from "@/types/dse/command/CommandHandler.ts";

import { useAddVotesToSingleKandidatHandler } from "@/composables/dse/command/addVotesToSingleKandidatHandler.ts";

export const COMMAND_HANDLERS: CommandHandler[] = [
  useAddVotesToSingleKandidatHandler(),
];
