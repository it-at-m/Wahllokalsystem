import type { NachbesetzungsDruckInput } from "@/types/wahlvorstand/NachbesetzungsDruckInput.ts";

export function useWahlvorstandNachbesetzungsDruck() {
  function buildTemplateFromData(data: NachbesetzungsDruckInput): string {
    return `
      <!DOCTYPE html>
        <html lang="de">
        <head>
          <meta charset="utf-8"/>
          <style>
            /****** Print Header ******/
            @page {
                size: auto;
                margin-top: 1cm;
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
                text-align: left;
                font-family: Arial, serif;
            }
            html, body {
                overflow-x: hidden;
            }
            /****** Container ******/
            .bodycontainer {
                max-width: 20cm;
                height: 26cm;
                margin-left: 1cm;
                margin-right: 1cm;
            }
            .horizontal {
                display: flex;
            }
            .vertical {
                display: flex;
                flex-direction: column;
            }
            .verticalContainer {
                display: flex;
                flex-direction: column;                       
            }
            .horizontalContainer {
                display: flex;
                align-items: flex-start;
                justify-content: space-between;
            }
            .horizontalTeilContainer {
                width: 70%;
                display: flex;
                align-items: flex-start;
                justify-content: space-between;
            }
            /****** Table ******/
            table {
                border-collapse: collapse;
                font-size: 11pt;
            }
            table.borderless th {
                border-width: 0;
            }
            table.borderless td {
                border-width: 0 ;
            }
            table.backandborder th {
                border: 1px solid #546e7a;
            }
            table.backandborder td {
                border: 1px solid #546e7a;
            }
            .table > tr > th,
            .table > tr > td {
                padding-top: 1px;
                padding-bottom: 1px;
                font-size: 11pt;
            }                    
            th, td {
                font-weight: normal;
                padding: 0.1cm 0.2cm;
                border: 1px solid #000000;
                font-size: 11pt;
            }
            /****** Margin/Padding ******/
            .marginCheckbox {
                margin-top: -0.05cm;
                margin-right: 0.2cm;
            }
            .marginTop3mm {
                margin-top: 3mm
            }
            .paddingRight03cm {
                padding-right: 0.3cm;
            }
            /****** Text ******/
            .fontSize_10 {
                font-size: 12pt;
            }
            .fontSize_12 {
                font-size: 12pt;
            }
            .fontSize_14 {
                font-size: 14pt;
            }
            .fontSize_16 {
                font-size: 16pt;
            }
            .textAlignCenter {
                text-align: center;
            }
            /****** Width/Height ******/
            .widthCalc {
                width: calc(100% - 0.0cm);
            }
            .height04cm {
                height: 0.4cm;
            }
            /****** Other ******/
            .backendData {
                font-weight: bold;
                font-style: italic;
                color: #546e7a;
            }
            .blackData {
                font-weight: bold;
                font-style: italic;
                color: #000000;
            }
            .blueGrayBG {
                background-color: #e6e6ff;
            }
            .border_bold {
                border-width: 1.5px;
                border-style: solid;
                border-color: #000000;
            }
          </style>
          <title>Besetzung und Anwesenheit Briefwahlvorstand ${data.wahlbezirknummer}</title>
        </head>
        <body>                    
          <div class="verticalContainer bodycontainer" style="margin-top: 6.5mm">
            <div class="vertical">
              <div class="horizontalContainer fontSize_16">
                <div class="fontSize_16"><b>Besetzung und Anwesenheit Briefwahlvorstand</b></div>
                <div class="fontSize_16"><b>${data.wahlbezirknummer}</b></div>
              </div>
              <div class="horizontalTeilContainer fontSize_16 marginTop3mm">
                <div>Briefwahltisch-Nummer:</div>
                <div style="min-width: 40%;">
                  <div class="border_bold" style="height: 25pt;"></div>
                  <div class="fontSize_10"><i>(- bitte Tisch-Nummer eingeben)</i></div>
                </div>
              </div>
              <div class="horizontal fontSize_16 marginTop3mm">
                <div>${data.wahlName} – Auszählung am ${data.wahlTag}</div>
              </div>
              <div class="horizontal fontSize_12" style="margin: 0.5cm 0 0.5cm 0;">
                <div><b>Wichtig:</b> Füllen Sie dieses Formular vollständig aus und geben es bei Ihrer Tischbetreuung ab. 
                                     Notieren Sie, ob das Gremium vollständig ist, Rollen verändert wurden und/oder Sie 
                                     noch eine oder mehrere Personen brauchen. 
                                     Ihre Tischbetreuung unterstützt Sie gern beim Befüllen dieses Blattes.
                </div>
              </div>
              <div class="horizontal fontSize_14 marginTop3mm">
                <div><b>Besetzung Wahlvorstand – Änderungen</b></div>
              </div>
              <div class="horizontal fontSize_12" style="margin-top: 1mm;">
                <div class="fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingRight03cm">Team vollständig</div>
                <div class="fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingRight03cm">Rollen verändert</div>
                <div class="fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingRight03cm">Es werden noch __ Person/en benötigt</div>
              </div>
              <div class="horizontal">
                <table class="table widthCalc">
                  <colgroup>
                    <col width="12.8%"/>
                    <col width="22.33%"/>
                    <col width="23.8%"/>
                    <col width="12.15%"/>
                    <col width="28.9%"/>
                  </colgroup>
                  <tr>
                    <td class="blueGrayBG textAlignCenter">Funktion</td>
                    <td class="blueGrayBG textAlignCenter">Nachname</td>
                    <td class="blueGrayBG textAlignCenter">Vorname</td>
                    <td class="blueGrayBG textAlignCenter">Fehlt ${data.druckZeitpunkt} Uhr (X)</td>
                    <td class="blueGrayBG textAlignCenter">Nachgerückt aus Gremium (Name)</td> 
                  </tr>
                  ${data.wahlvorstaende
                    .map((mitglied) => {
                      return `<tr>
                      <td class="backendData">${mitglied && mitglied.funktionsname ? mitglied.funktionsname : ""}</td>
                      <td class="blackData height04cm">${mitglied && mitglied.familienname ? mitglied.familienname : ""}</td>
                      <td class="blackData">${mitglied && mitglied.vorname ? mitglied.vorname : ""}</td>
                      <td class="backendData textAlignCenter">${mitglied && mitglied.anwesend ? "" : "<b>X</b>"}</td>
                      <td></td>
                    </tr>`;
                    })
                    .join("")}
                </table>
              </div>                      
            </div>
            <div style="margin-top: 1cm;">
              <div class="fontSize_14"><b>Nachbesetzung Wahlvorstand</b></div> 
              <div class="fontSize_10"><i>(wird vom Briefwahlserviceteam ausgefüllt)</i></div>
            </div>
            <div class="horizontal">
              <table class="table widthCalc">
                <colgroup>
                  <col width="14.8%"/>
                  <col width="23.33%"/>
                  <col width="24.8%"/>
                </colgroup>
                <tr>
                  <td class="blueGrayBG textAlignCenter">Funktion</td>
                  <td class="blueGrayBG textAlignCenter">Nachname</td>
                  <td class="blueGrayBG textAlignCenter">Vorname</td>
                </tr>
                <tr>
                  <td class="height04cm">Beisitzer*in</td>
                  <td></td>
                  <td></td>
                </tr>
                <tr>
                  <td class="height04cm">Beisitzer*in</td>
                  <td></td>
                  <td></td>
                </tr>
                <tr>
                  <td class="height04cm">Beisitzer*in</td>
                  <td></td>
                  <td></td>
                </tr>
              </table>
              <div class="horizontal" style="margin-left: 1cm">
                <div class="fontSize_14 marginCheckbox">&#9744;</div>
                <div class="fontSize_12">an WOS-Team per Webex übermittelt</div>    
              </div>
            </div>                    
          </div>
          </body>
        </html>`;
  }

  return { buildTemplateFromData };
}
