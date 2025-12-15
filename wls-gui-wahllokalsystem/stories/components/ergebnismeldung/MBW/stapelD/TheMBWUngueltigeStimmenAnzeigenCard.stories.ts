import type { Meta, StoryObj } from "@storybook/vue3";

import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/commonErgebnismeldungTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { http, HttpResponse } from "msw";

import { BezirkUndWahlIDStapelartDTOStapelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";

const { prepareErgebnisDTO, prepareErgebnisseDTO } =
  useErgebnisseTestDataFactory();
const { createBezirkUndWahlIDStapelartDTO } =
  useCommonErgebnismeldungTestDataFactory();

const wahlID = "wahlID";
const wahlbezirkID = "wahlbezirkID";

const body = JSON.stringify(
  prepareErgebnisseDTO()
    .bezirkUndWahlIDStapelart(
      createBezirkUndWahlIDStapelartDTO(
        BezirkUndWahlIDStapelartDTOStapelartEnum.MbwDUngueltig
      )
    )
    .ergebnisse([prepareErgebnisDTO().ergebnis(3).build()])
    .build()
);

const meta = {
  component: TheMBWUngueltigeStimmenAnzeigenCard,
  parameters: {
    msw: {
      handlers: [
        http.get(
          "/api/ergebnismeldung-service/businessActions/ergebnisse/*",
          async () => {
            return new HttpResponse(body, {
              status: 200,
              headers: {
                "Content-Type": "application/json",
              },
            });
          }
        ),
      ],
    },
  },
  args: {},
} satisfies Meta<typeof TheMBWUngueltigeStimmenAnzeigenCard>;

export default meta;

type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {
    wahlId: wahlID,
    wahlbezirkId: wahlbezirkID,
  },
};
