import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";
import type { BWerte } from "@/types/ergebnismeldung/common/BWerte.ts";
import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { ErgebnismeldungDruckInput } from "@/types/ergebnismeldung/MBW/ErgebnismeldungDruckInput.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useNumberFormatter } from "@/composables/common/numberFormatter.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { convertToSixDigitArray } = useNumberFormatter();
const { toGermanDate } = useDateTimeFormatter();

export function useErgebnismeldungDruck() {
  function buildTemplateFromData(data: ErgebnismeldungDruckInput): string {
    return `
        <!DOCTYPE html>
        <html lang="de">
        <head>
            <meta charset="utf-8"/>
            ${_getStyling()}
            <title>${data.aktuelleWahl.wahlart} ${data.wahlbezirksArt == WahlbezirksArtEnum.UWB ? "Urnenwahl" : "Briefwahl"} ${data.meldungsArt == MeldungsArtEnum.Schnellmeldung ? "Schnellmeldung" : "Niederschrift"}</title>
        </head>
        <body>
        <div class="vertical bodycontainer">
            <svg height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>
            <div class="fontSize_11 marginLeft_6_35">Wahlvordruck <span class="bold">V3 MigBW / ${data.wahlbezirksArt == WahlbezirksArtEnum.UWB ? "WR" : "BW"}</span></div>
            <div class="horizontal spaceBetween">
                <div class="vertical widthLeft">
                    ${_buildKennbuchstabenTable(data.wahlbezirksArt, data.bWerte, data.aWerte)}
                </div>
                <div class="vertical spaceBetween widthRightTop marginTopBottom_1">
                    ${_buildDocumentMetaData(data.meldungsArt, data.wahlbezirksArt, data.wahlbezirkNummer, data.aktuelleWahl.wahltag, data.barcode)}
                </div>
            </div>
            <div class="horizontal spaceBetween">
                <div class="vertical widthLeft">
                    ${_buildErgebnisseTable(data.gueltigeStimmenListe, data.wahlbezirksArt, data.sendOk, data.gueltigeStimmenGesamt, data.ungueltigeStimmen, data.alleStimmen)}
                </div>
                <div class="vertical widthRight marginTopBottom_1">
                    ${
                      data.gueltigeStimmenListe.length >= 25
                        ? `<div><br/></div>`
                        : `<div class="width_100 marginTop_2 marginBottom_1 "></div>
                          <!-- todo: der teil ist ausgelagert in _buildDocumentMetaData deswegen ist jetzt mehr abstand zwischen den tabellen, kann das so bleiben? -->
                          ${data.wahlbezirksArt == WahlbezirksArtEnum.UWB ? _buildMeldungUebermitteltAndMeldungErstattenBoxUWB(data.sendOk) : _buildMeldungUebermitteltAndMeldungErstattenBoxBWB(data.sendOk)}`
                    }
                </div>
            </div>
        </div>
        <div class="footer">${data.footer}</div>
        </body>
        </html>`;
  }

  function _buildKennbuchstabenTable(
    wahlbezirksart: WahlbezirksArtEnum,
    bWerte: BWerte,
    aWerte?: AWerte
  ) {
    let a1AsArray = [""];
    let a2AsArray = [""];
    let aAsArray = [""];

    if (aWerte) {
      a1AsArray = convertToSixDigitArray(aWerte.a1);
      a2AsArray = convertToSixDigitArray(aWerte.a2 || 0);
      aAsArray = convertToSixDigitArray(aWerte.a1 + (aWerte.a2 || 0));
    }

    const b1AsArray = convertToSixDigitArray(bWerte.b1);
    const b2AsArray = convertToSixDigitArray(bWerte.b2);
    const bAsArray = convertToSixDigitArray(bWerte.b);

    return `
    <table class="table marginTopBottom_1 width_100 fontSize_8">
      ${_getDefaultColgroup()}
      <tr>
          <th class="arialNarrow">Kennbuchst.</th>
          <th class="arialNarrow">Nach Abschnitt 4 der Wahlniederschrift <span class="fontSize_7">(Vordruck ${wahlbezirksart == WahlbezirksArtEnum.UWB ? "V1" : "V1a"})</span></th>
          <th class="border_bold textAlignCenter" colspan="6">Anzahl</th>
      </tr>
      <!-- rows only for uwb -->
      ${
        wahlbezirksart == WahlbezirksArtEnum.UWB
          ? `
      <tr class="rowHeight">
          <td class="bold">A1</td>
          <td class="paddingLeftRight">Stimmberechtigte <b>ohne</b> Vermerk „W“ (Wahlschein) lt. Wählerverzeichnis</td>
          <td class="borderLeft_bold backendData noPadding textAlignCenter">${a1AsArray[0]}</td>
          <td class="backendData noPadding textAlignCenter">${a1AsArray[1]}</td>
          <td class="borderRight_bold backendData noPadding textAlignCenter">${a1AsArray[2]}</td>
          <td class="backendData noPadding textAlignCenter">${a1AsArray[3]}</td>
          <td class="backendData noPadding textAlignCenter">${a1AsArray[4]}</td>
          <td class="borderRight_bold backendData noPadding textAlignCenter">${a1AsArray[5]}</td>
      </tr>
      <tr class="rowHeight">
          <td class="bold">A2</td>
          <td class="paddingLeftRight">Stimmberechtigte <b>mit</b> Vermerk „W“ (Wahlschein) lt. Wählerverzeichnis</td>
          <td class="borderBottom_bold borderLeft_bold backendData noPadding textAlignCenter">${a2AsArray[0]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${a2AsArray[1]}</td>
          <td class="borderBottom_bold borderRight_bold backendData noPadding textAlignCenter">${a2AsArray[2]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${a2AsArray[3]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${a2AsArray[4]}</td>
          <td class="borderBottom_bold borderRight_bold backendData noPadding textAlignCenter">${a2AsArray[5]}</td>
      </tr>
      <tr class="rowHeight">
          <td class="bold">A 1 + A 2</td>
          <td>Stimmberechtigte <b>zusammen</b></td>
          <td class="borderBottom_bold borderLeft_bold backendData noPadding textAlignCenter">${aAsArray[0]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${aAsArray[1]}</td>
          <td class="borderBottom_bold borderRight_bold backendData noPadding textAlignCenter">${aAsArray[2]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${aAsArray[3]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${aAsArray[4]}</td>
          <td class="borderBottom_bold borderRight_bold backendData noPadding textAlignCenter">${aAsArray[5]}</td>
      </tr>
      <tr class="rowHeight">
          <td class="bold">B1</td>
          <td>Wähler*innen mit Stimmabgabevermerken im Wählerverzeichnis</td>
          <td class="borderLeft_bold backendData noPadding textAlignCenter">${b1AsArray[0]}</td>
          <td class="backendData noPadding textAlignCenter">${b1AsArray[1]}</td>
          <td class="borderRight_bold backendData noPadding textAlignCenter">${b1AsArray[2]}</td>
          <td class="backendData noPadding textAlignCenter">${b1AsArray[3]}</td>
          <td class="backendData noPadding textAlignCenter">${b1AsArray[4]}</td>
          <td class="borderRight_bold backendData noPadding textAlignCenter">${b1AsArray[5]}</td>
      </tr>
      <tr class="rowHeight">
          <td class="bold">B2</td>
          <td>Wähler*innen mit Wahlschein</td>
          <td class="borderBottom_bold borderLeft_bold backendData noPadding textAlignCenter">${b2AsArray[0]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${b2AsArray[1]}</td>
          <td class="borderBottom_bold borderRight_bold backendData noPadding textAlignCenter">${b2AsArray[2]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${b2AsArray[3]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${b2AsArray[4]}</td>
          <td class="borderBottom_bold borderRight_bold backendData noPadding textAlignCenter">${b2AsArray[5]}</td>
      </tr>
      `
          : ``
      }
      <!-- row for uwb + bwb -->
      <tr class="rowHeight">
          <td class="bold">B</td>
          <td>${wahlbezirksart == WahlbezirksArtEnum.UWB ? "Wähler*innen <b>zusammen</b> (B 1 + B 2) = Zahl der abgegebenen Stimmzettel" : "Wähler*innen"}</td>
          <td class="borderLeft_bold borderBottom_bold backendData noPadding textAlignCenter">${bAsArray[0]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${bAsArray[1]}</td>
          <td class="borderBottom_bold borderRight_bold backendData noPadding textAlignCenter">${bAsArray[2]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${bAsArray[3]}</td>
          <td class="borderBottom_bold backendData noPadding textAlignCenter">${bAsArray[4]}</td>
          <td class="borderBottom_bold borderRight_bold backendData noPadding textAlignCenter">${bAsArray[5]}</td>
      </tr>
  </table>`;
  }

  function _buildDocumentMetaData(
    meldungsArt: MeldungsartEnum,
    wahlbezirksArt: WahlbezirksArtEnum,
    wahlbezirkNummer: string,
    wahltag: string,
    barcode: string
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
        ${_buildBarcodeWithTitle(meldungsArt, wahlbezirksArt, wahltag, barcode)}`;
  }

  function _buildBarcodeWithTitle(
    meldungsArt: MeldungsartEnum,
    wahlbezirksArt: WahlbezirksArtEnum,
    wahltag: string,
    barcode: string
  ) {
    return `
        <div class="bold fontSize_14 marginTop_5">${meldungsArt == MeldungsArtEnum.Schnellmeldung ? "Schnellmeldung" : "Niederschrift"} <br/> 
            ${wahlbezirksArt == WahlbezirksArtEnum.UWB ? "Wahlvorstand" : "Briefwahlvorstand"} <br/> 
            für die Wahl des <br/> Migrationsbeirates <br/> am ${toGermanDate(wahltag)}
        </div>
        <div class="horizontal flexEnd">
            <img class="barcode"
                src="${barcode}"
                alt="">
        </div>`;
  }

  function _buildErgebnisseTable(
    gueltigeStimmenListe: MbwErgebnisseAndWahlvorschlag[],
    wahlbezirksArt: WahlbezirksArtEnum,
    sendOk: boolean,
    gueltigeStimmenGesamt: string[],
    ungueltigeStimmen: string[],
    alleStimmen: string[]
  ) {
    return `
        <div class="marginTopBottom_1">Von den <span class="bold">gültigen</span> Stimmzetteln (Stapel a) und Stapel b) hellgrün) entfallen auf</div>
            <table class="table marginTopBottom_1 width_100 fontSize_8">
                ${_getDefaultColgroup()}
                <tr>
                    <th></th>
                    <th>Kurzbezeichnung bzw. Kennwort lt. Stimmzettel</th>
                    <th class="border_bold textAlignCenter" colspan="6">Stimmzettel</th>
                </tr>
                ${gueltigeStimmenListe
                  .map((ergebnisseAndWahlvorschlag, idx) => {
                    const bottomBoldedClass =
                      idx + 1 === gueltigeStimmenListe.length
                        ? "borderBottom_bold "
                        : "";
                    return `
                      ${_buildTableRowForWahlvorschlagWithErgebnis(ergebnisseAndWahlvorschlag, bottomBoldedClass)}
                      ${
                        ergebnisseAndWahlvorschlag.wahlvorschlag.ordnungszahl %
                          25 ===
                        0
                          ? `</table> 
                            </div>
                            <!-- insert the right side tables only on the first page -->
                            ${
                              ergebnisseAndWahlvorschlag.wahlvorschlag
                                .ordnungszahl === 25
                                ? `<div class="vertical widthRight marginTopBottom_1">
                                      <div class="width_100 marginTop_2 marginBottom_1 "></div>
                                      <div class="vertical spaceBetween widthForBarcode marginTopBottom_1">
                                          <!-- todo: hier war im alten code ein zweites mal _buildBarcodeWithTitle drin, warum? -->
                                      </div>
                                      ${wahlbezirksArt == WahlbezirksArtEnum.UWB ? _buildMeldungUebermitteltAndMeldungErstattenBoxUWB(sendOk) : _buildMeldungUebermitteltAndMeldungErstattenBoxBWB(sendOk)}
                                    </div>`
                                : ``
                            }
                          </div><!-- horizontal container first page -->
                        </div> <!-- vertical container all first page -->
                        <div class="vertical bodycontainer">
                          <svg class="page_break" height="25px" width="100%">
                            <rect width="1000" height="25" style="fill: #ffffff;" />
                          </svg>
                          <div class="horizontal spaceBetween">
                            <div class="vertical spaceBetween widthLeft">
                            <table class="table marginTopBottom_1 width_100 fontSize_8">
                              ${_getDefaultColgroup()}`
                          : ""
                      }
                `;
                  })
                  .join("")}        
            </table>
            ${_buildGueltigeUngueltigeAndSumTables(gueltigeStimmenGesamt, ungueltigeStimmen, alleStimmen)}`;
  }

  function _buildMeldungUebermitteltAndMeldungErstattenBoxUWB(sendOk: boolean) {
    return `
        ${_buildMeldungUebermitteltBox(sendOk)}
        <div class="vertical spaceBetween border border_bold ${sendOk ? "" : ""}"><!-- 'hidden' : ''}"> -->
            <div class="paddingLeftRight bold fontSize_11">Die Meldung ist auf schnellstem Wege zu erstatten: </div>
                <div class="paddingLeftRight horizontal spaceBetween fontSize_14 marginTop_4 marginBottom_4">
                    <div class="bold">Tel.-Nr.:</div>
                    <div class="bold">089 233-96233</div>
                </div>
    
                <div class="paddingLeftRight bold fontSize_10 marginTop_2">Bei telefonischer Weitermeldung<br/>Hörer erst auflegen, wenn die Zahlen wiederholt sind.</div>
    
                <div class="paddingLeftRight marginTop_4" >Durchgegeben von:</div>
                <div class="paddingLeftRight marginTop_4">____________________________________</div>
                <div class="paddingLeftRight fontSize_8 textAlignCenter">(Vor- und Familienname)</div>
                <div class="paddingLeftRight marginTop_4">____________________________________</div>
                <div class="paddingLeftRight fontSize_8 textAlignCenter ">(Telefonnummer)</div>
    
                <div class="paddingLeftRight marginTop_4">Uhrzeit:<br/><span class="fontSize_8">bei Durchgabe/Aufnahme der Meldung</span></div>
                <div class="paddingLeftRight marginTop_4">____________________________________</div>
    
                <div class="paddingLeftRight marginTop_4">Aufgenommen von:</div>
                <div class="paddingLeftRight marginTop_4">____________________________________</div>
                <div class="paddingLeftRight fontSize_8 textAlignCenter">(Vor- und Familienname)</div>                                        
        </div>`;
  }

  function _buildMeldungUebermitteltAndMeldungErstattenBoxBWB(sendOk: boolean) {
    return `
        ${_buildMeldungUebermitteltBox(sendOk)}
        <div style="height: 5cm" class="vertical spaceBetween border border_bold marginBottom_4 ${sendOk ? "" : ""}" > <!-- 'hidden' : ''}" > -->
            <div class="paddingLeftRight paddingTop bold fontSize_10 marginTop_2">Die Meldung ist auf schnellstem Wege zu erstatten an das</div>
            <div class="paddingLeftRight paddingTop bold fontSize_24 marginTop_2">Briefwahl-<br/>serviceteam</div>
        </div>
    `;
  }

  function _buildMeldungUebermitteltBox(sendOk: boolean) {
    return `
        <div class="border border_bold marginTop_4 marginBottom_4 ${sendOk ? "" : ""}"><!-- : 'hidden'}"> -->
            <div class="bold fontSize_11 padding" style="height: 3.6cm">Die Meldung wurde <span class="backendData">${sendOk ? "elektronisch" : "telefonisch"}</span> übermittelt</div>
        </div>`;
  }

  function _buildGueltigeUngueltigeAndSumTables(
    gueltige: string[],
    ungueltige: string[],
    gesamt: string[]
  ) {
    return `
    <table class="table marginTopBottom_1 width_100 fontSize_8">
        ${_getDefaultColgroup()}
        <tr class="rowHeight">
            <td class="bold">D</td>
            <td>Gültige Stimmzettel</td>
            <td class="borderLeft_bold borderTop_bold backendData noPadding textAlignCenter">${gueltige[0]}</td>
            <td class="borderTop_bold backendData noPadding textAlignCenter">${gueltige[1]}</td>
            <td class="borderRight_bold borderTop_bold backendData noPadding textAlignCenter">${gueltige[2]}</td>
            <td class="borderTop_bold backendData noPadding textAlignCenter">${gueltige[3]}</td>
            <td class="borderTop_bold backendData noPadding textAlignCenter">${gueltige[4]}</td>
            <td class="borderRight_bold borderTop_bold backendData noPadding textAlignCenter">${gueltige[5]}</td>
        </tr>
        <tr class="rowHeight">
            <td class="bold">C</td>
            <td>Ungültige Stimmzettel (Stapel d))</td>
            <td class="borderLeft_bold borderBottom_bold backendData noPadding textAlignCenter">${ungueltige[0]}</td>
            <td class="borderBottom_bold backendData noPadding textAlignCenter">${ungueltige[1]}</td>
            <td class="borderRight_bold borderBottom_bold backendData noPadding textAlignCenter">${ungueltige[2]}</td>
            <td class="borderBottom_bold backendData noPadding textAlignCenter">${ungueltige[3]}</td>
            <td class="borderBottom_bold backendData noPadding textAlignCenter">${ungueltige[4]}</td>
            <td class="borderRight_bold borderBottom_bold backendData noPadding textAlignCenter">${ungueltige[5]}</td>
        </tr>
    </table>
    <div class="marginTop_2">
        <table class="table width_100">
            <colgroup>
                <col width="53%"/>
                <col width="20%"/>
                <col width="4.5%"/>
                <col width="4.5%"/>
                <col width="4.5%"/>
                <col width="4.5%"/>
                <col width="4.5%"/>
                <col width="4.5%"/>
            </colgroup>
            <tr>
                <td class="noBorder borderBottom"></td>
                <td class="noBorder">zusammen</td>
                <td class="borderLeft_bold borderTop_bold borderBottom_bold backendData noPadding textAlignCenter">${gesamt[0]}</td>
                <td class="borderTop_bold borderBottom_bold backendData noPadding textAlignCenter">${gesamt[1]}</td>
                <td class="borderTop_bold borderRight_bold borderBottom_bold backendData noPadding textAlignCenter">${gesamt[2]}</td>
                <td class="borderTop_bold borderBottom_bold backendData noPadding textAlignCenter">${gesamt[3]}</td>
                <td class="borderTop_bold borderBottom_bold backendData noPadding textAlignCenter">${gesamt[4]}</td>
                <td class="borderTop_bold borderRight_bold borderBottom_bold backendData noPadding textAlignCenter">${gesamt[5]}</td>
            </tr>
        </table>
        <div class="paddingLeft">Unterschrift Briefwahlvorsteher*in</div>
    </div>`;
  }

  function _buildTableRowForWahlvorschlagWithErgebnis(
    ergebnisWithWahlvorschlag: MbwErgebnisseAndWahlvorschlag,
    bottomBoldedClass: string
  ) {
    const sumAsArray = _sumAndFormatErgebnisse(ergebnisWithWahlvorschlag);
    return `
        <tr class="rowHeight">
            <td class="bold">D ${ergebnisWithWahlvorschlag.wahlvorschlag.ordnungszahl}</td>
            <td class="backendData noPadding paddingLeft">${ergebnisWithWahlvorschlag.wahlvorschlag.kurzname}</td>
            <td class="${bottomBoldedClass} borderLeft_bold backendData noPadding textAlignCenter">${sumAsArray[0]}</td>
            <td class="${bottomBoldedClass} backendData noPadding textAlignCenter">${sumAsArray[1]}</td>
            <td class="${bottomBoldedClass} borderRight_bold backendData noPadding textAlignCenter">${sumAsArray[2]}</td>
            <td class="${bottomBoldedClass} backendData noPadding textAlignCenter">${sumAsArray[3]}</td>
            <td class="${bottomBoldedClass} backendData noPadding textAlignCenter">${sumAsArray[4]}</td>
            <td class="${bottomBoldedClass} borderRight_bold backendData noPadding textAlignCenter">${sumAsArray[5]}</td>
        </tr>`;
  }

  function _getDefaultColgroup() {
    return `
        <colgroup>
            <col/>
            <col width="59%"/>
            <col width="4.5%"/>
            <col width="4.5%"/>
            <col width="4.5%"/>
            <col width="4.5%"/>
            <col width="4.5%"/>
            <col width="4.5%"/>
        </colgroup>
    `;
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
            .border {
                border: 1px solid #000000;
            }
            .borderBottom {
                border-bottom: 1px solid #000000;
            }
            .border_bold {
                border-width: 3px;
            }
            .borderTop_bold {
                border-top-width: 3px;
            }
            .borderBottom_bold {
                border-bottom-width: 3px;
            }
            .borderLeft_bold {
                border-left-width: 3px;
            }
            .borderRight_bold {
                border-right-width: 3px;
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
            /****** Text Alignment ******/
            .textAlignCenter {
                text-align: center;
            }
            /****** Font *****/
            .arialNarrow {
                font-family: 'Arial Narrow';
            }
            /***** Font-Weight *****/
            .bold {
                font-weight: bold;
            }
            /****** Font-Size ******/
            .fontSize_7 {
                font-size: 7pt;
            }
            .fontSize_8 {
                font-size: 8pt;
            }
            .fontSize_10 {
                font-size: 10pt;
            }
            .fontSize_11 {
                font-size: 11pt;
            }
            .fontSize_14 {
                font-size: 14pt;
            }
            .fontSize_24 {
                font-size: 24pt;
            }
            /****** Width ******/
            .width_100 {
                width: 100%;
            }
            .widthLeft {
                width: 10.95cm;
            }
            .widthRight {
                width: 7.2cm;
            }
            .widthRightTop {
                width: 6.7cm;
            }
            .widthForBarcode {
                width: 7.5cm;
            }
            /****** Height ******/
            .rowHeight {
                height: 0.62cm;
            }
            /****** Padding ******/
            .noPadding {
                padding: 0;
            }
            .paddingLeft {
                padding-left: 0.2cm;
            }
            .paddingLeftRight {
                padding: 0 0.2cm;
            }
            .padding {
                padding: 0.2cm;
            }
            /****** Margin ******/
            .marginLeft_6_35 {
                margin-left: 6.35cm;
            }
            .marginTopBottom_1 {
                margin-top: 0.1cm;
                margin-bottom: 0.1cm;
            }
            .marginBottom_1 {
                margin-bottom: 0.1cm;
            }
            .marginTop_2 {
                margin-top: 0.2cm;
            }
            .marginTop_4 {
                margin-top: 0.4cm;
            }
            .marginBottom_2 {
                margin-bottom: 0.2cm;
            }
            .marginBottom_4 {
                margin-bottom: 0.4cm;
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
            /****** Hidden ******/
            .hidden {
                visibility: hidden;
            }
            /****** Page Break ******/
            .page_break {
                page-break-before: always;
            }
        </style>`;
  }

  function _sumAndFormatErgebnisse(
    ergebnisseAndWahlvorschlag: MbwErgebnisseAndWahlvorschlag
  ) {
    const sum =
      (ergebnisseAndWahlvorschlag.ergebnisStapelA.ergebnis ?? 0) +
      (ergebnisseAndWahlvorschlag.ergebnisStapelB.ergebnis ?? 0);

    return convertToSixDigitArray(sum);
  }

  return { buildTemplateFromData };
}
