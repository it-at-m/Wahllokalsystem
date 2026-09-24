import type { BeschlussentscheidungenDruckInput } from "@/types/dse/beschlussfassung/BeschlussentscheidungenDruckInput.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { toGermanDate } = useDateTimeFormatter();

export function useBeschlussentscheidungenDruckTemplateTools() {
  function buildTemplate(data: BeschlussentscheidungenDruckInput): string {
    return `
        <!DOCTYPE html>
        <html lang="de">
        <head>
            <meta charset="utf-8"/>
            ${_getStyling()}
            <title>${data.aktuelleWahl.wahlart} ${data.wahlbezirksArt == WahlbezirksArtEnum.UWB ? "Urnenwahl" : "Briefwahl"} Beschlussentscheidungen</title>
        </head>
        <body>
        <div class="vertical bodycontainer">
            <svg height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>
            <div class="horizontal spaceBetween marginTopBottom_1">
                <div>
                    ${_buildDocumentMetaData(data.wahlbezirksArt, data.wahlbezirkNummer, data.aktuelleWahl.wahltag)}
                </div>
            </div>
            <div class="horizontal spaceBetween marginTopBottom_1">
                <div>
                  ${_buildBeschlussTable(data.stimmzettelWithBeschluss)}
                </div>
            </div>
        </div>
        </body>
        <div class="footer">${data.footer}</div>
        </html>`;
  }

  function _getStyling() {
    return `
        <style type="text/css">
            /****** Print Header ******/
            @page {
                size: auto;
                margin-top: 0.0cm;
                margin-left: 0;
                margin-right: 0;
            }
            @media print {
                div.footer {
                    position: fixed;
                    bottom: 0;
                    left: 1cm;
                    font-size: x-small;
                    z-index: 0;
                }
            }
            /****** Default Value Tags ******/
            body {
                max-width: 21cm;
                margin: 0;
                font-size: 9pt;
                writing-mode: lr-tb;
                text-align: justify;
                font-family: Arial, serif;
            }
            html, body {
                overflow-x: hidden;
            }
            .bodycontainer {
                max-width: 18.9cm;
                margin: 0.0cm 1cm 0.2cm 1cm;
            }
            /****** Table ******/
            table {
                border-collapse: collapse;
            }
  
            .table > tr > th,
            .table > tr > td {
                padding-top: 2px;
                padding-bottom: 2px;
            }
  
            th,
            td {
                font-weight: normal;
                padding: 0.12cm 0.2cm;
                border: 1px solid #000000;
            }
            hr { 
                display: block;
                margin-before: 0.5em;
                margin-after: 0.5em;
                margin-start: auto;
                margin-end: auto;
                overflow: hidden;
                border-style: inset;
                border-width: 2px;
            }
            /****** Borders ******/
            .noBorder {
                border: none;
            }
            .borderBottom {
                border-bottom: 1px solid #000000;
            }
            /****** Flex ******/
            .horizontal {
                display: flex;
            }
            .vertical {
                display: flex;
                flex-direction: column;
            }
            .spaceBetween {
                justify-content: space-between;
            }
            .flexEnd {
                justify-content: flex-end;
            }
            /***** Font-Weight *****/
            .bold {
                font-weight: bold;
            }
            /****** Font-Size ******/
            .fontSize_8 {
                font-size: 8pt;
            }
            .fontSize_14 {
                font-size: 14pt;
            }
            /****** Width ******/
            .width_100 {
                width: 100%;
            }
            /****** Padding ******/
            .paddingLeft {
                padding-left: 0.2cm;
            }
            /****** Margin ******/
            .marginTopBottom_1 {
                margin-top: 0.1cm;
                margin-bottom: 0.1cm;
            }
            .marginBottom_1 {
                margin-bottom: 0.1cm;
            }
            .marginTop_4 {
                margin-top: 0.4cm;
            }
            .marginTop_12 {
                margin-top: 1.2cm;
            }
            .marginBottom_2 {
                margin-bottom: 0.2cm;
            }
            /****** Barcode ******/
            .barcode {
                height: 2.4cm;
                width: 7.5cm;
            }
            /****** Backend-Data ******/
            .backendData {
                font-weight: bold;
                font-style: italic;
                color: #546e7a;
            }
        </style>`;
  }

  function _buildDocumentMetaData(
    wahlbezirksArt: WahlbezirksArtEnum,
    wahlbezirkNummer: string,
    wahltag: string
  ) {
    return `
        <div class="vertical">
            <div class="borderBottom marginBottom_2">Landeshauptstadt München</div>
            <div class="marginTop_1 marginBottom_1 fontSize_8">${
              wahlbezirksArt == WahlbezirksArtEnum.UWB
                ? "Wahlbezirk (Nr./Name)"
                : "Briefwahlvorstand (Nr.)"
            }</div>
            <div class="borderBottom backendData">${wahlbezirkNummer}</div>
        </div>
        ${_buildTitle(wahltag)}`;
  }

  function _buildTitle(wahltag: string) {
    return `
        <div class="bold fontSize_14 marginTop_4">Beschlussentscheidungen <br/> 
            für die Wahl des <br/> Migrationsbeirates <br/> am ${toGermanDate(wahltag)}
        </div>`;
  }

  function _buildBeschlussTable(
    stimmzettelWithBeschluss: PersistedStimmzettel[]
  ) {
    return `
      <table class="table marginTopBottom_1 width_100 fontSize_8">
          <colgroup>
            <col width="15%"/>
            <col width="15%"/>
            <col width="50%"/>
            <col width="10%"/>
            <col width="10%"/>
          </colgroup>
          <tr>
            <th>Stimmzettelkennung</th>
            <th>Beschlussergebnis</th>
            <th>Begründung</th>
            <th>Stimmen dafür</th>
            <th>Stimmen dagegen</th>
          </tr>
        ${stimmzettelWithBeschluss
          .map((stimmzettel) => {
            return `
          ${_buildTableRowForBeschlussergebnis(stimmzettel)}`;
          })
          .join("")}
      </table>
      <div class="marginTop_12">
        <table class="table width_100">
          <colgroup>
            <col width="100%" />
          </colgroup>
          <tr>
            <td class="noBorder borderBottom"></td>
          </tr>
        </table>
        <div class="paddingLeft">Unterschrift Wahlvorsteher*in</div>
      </div>
    `;
  }

  function _buildTableRowForBeschlussergebnis(
    stimmzettel: PersistedStimmzettel
  ) {
    return `
      <tr>
        <td>${stimmzettel.teamID}${stimmzettel.stimmzettelkennung}</td>
        <td>${stimmzettel.gueltigkeit}</td>
        <td>${stimmzettel.beschlussfassung?.text}</td>
        <td>${stimmzettel.beschlussfassung?.pro}</td>
        <td>${stimmzettel.beschlussfassung?.contra}</td>
      </tr>
    `;
  }

  return {
    buildTemplate,
  };
}
