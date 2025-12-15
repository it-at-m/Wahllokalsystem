import type { WahlvorschlaegeDTO } from "@/api/wls-clients/generated-basisdaten-api";
import type { ErgebnisseDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Meta, StoryObj } from "@storybook/vue3";

import { delay, http, HttpResponse } from "msw";

import TheWahlvorschlaegeKandidatenStimmenCard from "@/components/ergebnismeldung/MBW/stapelBC/TheWahlvorschlaegeKandidatenStimmenCard.vue";
import {
  BASISDATEN_SERVICE_API_URL,
  ERGEBNISMELDUNG_SERVICE_API_URL,
} from "@/constants.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const wahlbezirkID = "wahlbezirkID";
const wahlID = "wahlID";
const stimmzettelgebietID = "stimmzettelgebietID";

const meta = {
  component: TheWahlvorschlaegeKandidatenStimmenCard,
  args: {
    wahlID: "wahlID",
    wahlbezirkID: "wahlbezirkID",
  },
  parameters: {
    msw: {
      handlers: [
        http.get(
          `${BASISDATEN_SERVICE_API_URL}/businessActions/wahlvorschlaege/${wahlID}/${wahlbezirkID}`,
          async () => {
            await delay(500);
            return new HttpResponse(JSON.stringify(createWahlvorschlaege()), {
              status: 200,
            });
          }
        ),
        http.get(
          `${ERGEBNISMELDUNG_SERVICE_API_URL}/businessActions/ergebnisse/${wahlbezirkID}/${wahlID}/${StapelArtEnum.MbwBC}`,
          async () => {
            await delay(500);
            return getDefaultHttpResponse(StapelArtEnum.MbwBC);
          }
        ),
        http.get(
          `${ERGEBNISMELDUNG_SERVICE_API_URL}/businessActions/ergebnisse/${wahlbezirkID}/${wahlID}/${StapelArtEnum.MbwA}`,
          async () => {
            await delay(500);
            return getDefaultHttpResponse(StapelArtEnum.MbwA);
          }
        ),
        http.get(
          `${ERGEBNISMELDUNG_SERVICE_API_URL}/businessActions/ergebnisse/${wahlbezirkID}/${wahlID}/${StapelArtEnum.MbwB}`,
          async () => {
            await delay(500);
            return getDefaultHttpResponse(StapelArtEnum.MbwB);
          }
        ),

        http.post(`/api/*`, async () => {
          await delay(2000);
          return new HttpResponse(null, {
            status: 200,
          });
        }),
      ],
    },
  },
} satisfies Meta<typeof TheWahlvorschlaegeKandidatenStimmenCard>;

export default meta;
type Story = StoryObj<typeof meta>;
export const Default: Story = {
  args: {},
};

function getDefaultHttpResponse(stapel: StapelArtEnum) {
  return new HttpResponse(
    JSON.stringify({
      bezirkUndWahlIDStapelart: {
        wahlID,
        wahlbezirkID,
        stapelart: stapel,
      },
      ergebnisse: [],
    } as ErgebnisseDTO),
    {
      status: 200,
    }
  );
}

function createWahlvorschlaege(): WahlvorschlaegeDTO {
  return {
    wahlvorschlaege: [
      {
        erhaeltStimmen: true,
        kurzname: "Wahlvorschlag 2",
        ordnungszahl: 2,
        kandidaten: [
          {
            identifikator: "Kandidat22",
            direktkandidat: false,
            name: "Kandidat 22",
            einzelbewerber: false,
            listenposition: 2,
            tabellenSpalteInNiederschrift: 1,
          },
          {
            identifikator: "Kandidat21",
            direktkandidat: false,
            name: "Kandidat 21",
            einzelbewerber: false,
            listenposition: 1,
            tabellenSpalteInNiederschrift: 1,
          },
        ],
        identifikator: "wahlvorschlagID2",
      },
      {
        erhaeltStimmen: true,
        kurzname: "Wahlvorschlag 1",
        ordnungszahl: 1,
        kandidaten: [
          {
            identifikator: "Kandidat12",
            direktkandidat: false,
            name: "Kandidat 12",
            einzelbewerber: false,
            listenposition: 2,
            tabellenSpalteInNiederschrift: 1,
          },
          {
            identifikator: "Kandidat11",
            direktkandidat: false,
            name: "Kandidat 11",
            einzelbewerber: false,
            listenposition: 1,
            tabellenSpalteInNiederschrift: 1,
          },
        ],
        identifikator: "wahlvorschlagID1",
      },
    ],
    wahlID,
    wahlbezirkID,
    stimmzettelgebietID,
  };
}
