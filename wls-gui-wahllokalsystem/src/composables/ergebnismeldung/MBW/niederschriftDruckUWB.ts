// eslint-disable-next-line @typescript-eslint/ban-ts-comment
//@ts-nocheck
import type { NiederschriftDruckInputUWB } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputUWB.ts";

/*eslint-disable no-irregular-whitespace*/
export function useNiederschriftDruckUWB() {
  function buildNiederschriftTemplateFromData(
    data: NiederschriftDruckInputUWB
  ) {
    return `
            <!DOCTYPE html>
            <html lang="de">
            ${_dataForHeader(data)}
            <body>    
            ${_dataForChapterOneWahlvorstand(data)}
            ${_dataForChapterTwo(data)}
            ${_dataForChapterThree(data)}
            ${_dataForChapterFour(data)}
            ${_dataForChapterFive(data)}  
            </body>
            <div class="footer">${data.footer}</div>
            </html>
            
    `;
  }

  function _dataForHeader(data: NiederschriftDruckInputUWB) {
    return `
    <head>
                <meta charset="utf-8"/>
                ${_getStyling()}
                <title>${data.aktuelleWahl.wahlart} Urnenwahl Niederschrift</title>
            </head>
            <body>
            <svg height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>
            <!-- Title -->
            <div class="width_100 textAlignCenter"><span class="bold fontSize_11">V1 MigBW</span></div>
            
            <!-- Header -->
            <div class="gridContainer_3_column_header marginTop_2 marginBottom_5">
                <!-- Barcode -->
                <div >
                    <img class="barcode"
                        src="${data.barcode}"
                        alt="">
                </div>
                <div class="horizontal flexEnd">
                    <img style="height: 1.50cm; margin-top: 0.1cm;"
                        alt=""
                        src="data:image/png;base64,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">
                </div>
                <div class="vertical">
                    <div class="fontSize_13">Landeshauptstadt</div>
                    <div class="fontSize_13">München</div>
                    <div class="fontSize_13 bold">Kreisverwaltungsreferat</div>
                </div>
            </div>
            <div class="gridContainer_2_column_header_first">
                    <div></div>
                    <div class="vertical">
                            <div class="border border_bold padding bold">Stimmbezirk Nr.</div>
                            <div class="backendData noBorderTop border_bold padding">${data.wahlbezirkNummer}</div>
                    </div>
            </div>
            <div class="gridContainer_2_column_header_second marginTop_2"> 
                    <div class="fontSize_13 bold vertical">
                        <div>WAHLNIEDERSCHRIFT / Wahlraum (V1)<br/>für die Wahl des Migrationsbeirates in der Landeshauptstadt München<br/> 
                        <span class="fontSize_11">am ${data.wahltagFormatiert}</span></div>
                    </div>
                    <div>
                        <div class="fontSize_8 border padding">
                            Diese Wahlniederschrift ist unter Nr. 5.5.1 von allen Mitgliedern des Wahlvorstands zu unterschreiben.
                        </div>
                    </div>
            </div>
    `;
  }

  function _dataForChapterOneWahlvorstand(data: NiederschriftDruckInputUWB) {
    return `
    <div class="fontSize_12 bold marginTop_2 marginBottom_2">Wahlhandlung</div>
            <div>
                Über den Ablauf der Wahl einschließlich der Ergebnisermittlung ist nachstehende Wahlniederschrift zu fertigen. 
                Einzelheiten enthält die Wahlanweisung für Wahlvorstandsmitglieder. 
                Im Zweifelsfall sind die Bestimmungen der Migrationsbeiratswahlordnung, der Migrationsbeiratssatzung, 
                des Gemeinde- und Landkreiswahlgesetzes (GLKrWG) und der Gemeinde- und Landkreiswahlordnung (GLKrWO) maßgebend.
            </div>

            <!-- 1. -->
            <div class="horizontal marginTop_5 marginBottom_2">
                <div class="bold widthNumber">1.</div>
                <div class="bold">Wahlvorstand</div>
            </div>
            <div class="horizontal marginBottom_1">
                <div class="widthNumber"></div>
                <div>Zur Wahl des Migrationsbeirates waren vom Wahlvorstand erschienen:</div>
            </div>
            <div class="horizontal marginBottom_1">
                <table class="table marginTop widthCalc">
                    <colgroup>
                        <col width="4.59%"/>
                        <col width="34.24%"/>
                        <col width="36.17%"/>
                        <col width="25%"/>
                    </colgroup>
                    <tr>
                        <td class="blueGrayBG textAlignCenter" colspan="2">Familienname</td>
                        <td class="blueGrayBG textAlignCenter">Vorname</td>
                        <td class="blueGrayBG textAlignCenter">Funktion*</td>
                    </tr>
                    ${data.wahlvorstaende
                      .map((mitglied, idx) => {
                        return `<tr>
                        <td class="blueGrayBG" style="height: 0.9cm;">${idx + 1}.</td>
                        <td class="backendData">${mitglied && mitglied.nachname ? mitglied.nachname : ""}</td>
                        <td class="backendData">${mitglied && mitglied.vorname ? mitglied.vorname : ""}</td>
                        <td class="backendData">${mitglied && mitglied.funktionsName ? mitglied.funktionsName : ""}</td>
                        
                    </tr>
                    `;
                      })
                      .join("")}
                </table>
            </div>
            <div class="horizontal">
                <div class="horizontal">
                    <div class="bold">Hinweis:&nbsp;</div>
                    <div>Bei dem Begriff „Wahlvorstand“ ist das gesamte Gremium gemeint und nicht eine einzelne Person.</div>
                </div>
            </div>

            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>

            <div class="horizontal marginBottom_2 paddingTopNewPage">
                <div class="widthNumber"></div>
                <div>
                    Als Ersatz für nicht erschienene oder ausgefallene Mitglieder des Wahlvorstandes hat die oder der Wahlvorsteher*in folgende Personen zu Mitgliedern des Wahlvorstands ernannt:
                </div>
            </div>
            <div class="horizontal marginBottom_5">
                <table class="table width_100">
                    <colgroup>
                        <col width="4.47%"/>
                        <col width="34.24%"/>
                        <col width="21.3%"/>
                        <col width="20%"/>
                        <col width="20%"/>
                    </colgroup>
                    <tr>
                        <td class="blueGrayBG textAlignCenter" colspan="2">Familienname</td>
                        <td class="blueGrayBG textAlignCenter">Vorname</td>
                        <td class="blueGrayBG textAlignCenter">Funktion</td>
                        <td class="blueGrayBG textAlignCenter">Uhrzeit</td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG">1.</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG">2.</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG">3.</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                </table>
            </div>
    `;
  }

  function _dataForChapterTwo(data: NiederschriftDruckInputUWB) {
    return `
    <!-- 2. -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.</div>
                <div class="bold">Wahlhandlung</div>
            </div>

            <!-- 2.1 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.1</div>
                <div class="bold">
                    Hinweis auf Verpflichtung des Wahlvorstands - Auslegen der Wahlvorschriften - Aushang der Wahlbekanntmachung und des Stimmzettelmusters
                </div>
            </div>
            <div class="horizontal marginBottom_1">
                <div class="widthNumber"></div>
                <div>Die oder der Wahlvorsteher*in eröffnete die Wahlhandlung damit, die übrigen Mitglieder des Wahlvorstands auf
                    ihre Verpflichtung zur unparteiischen Wahrnehmung ihres Amts und zur Verschwiegenheit über die ihnen bei
                    ihrer amtlichen Tätigkeit bekannt gewordenen Angelegenheiten hinzuweisen. Außerdem wurden alle Mitglieder
                    über ihre Aufgaben informiert.
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Textausgaben der Migrationsbeiratswahlordnung, der Migrationsbeiratssatzung, des Gemeinde- und Landkreiswahlgesetzes
                    sowie der Gemeinde- und Landkreiswahlordnung waren im Wahlraum vorhanden. Am oder im
                    Eingang des Gebäudes, in dem sich der Wahlraum befand, wurden ein Abdruck der Wahlbekanntmachung sowie
                    ein Muster des Stimmzettels angebracht.
                </div>
            </div>

            <!-- 2.2 -->            
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.2</div>
                <div class="bold">
                    Wahlurne
                </div>
            </div>            
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Der Wahlvorstand stellte fest, dass sich die Wahlurne in ordnungsgemäßem Zustand befand und leer war. Danach
                    wurde die Wahlurne versiegelt.
                </div>
            </div>

            <!-- 2.3 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.3</div>
                <div class="bold">Sichtblenden (Abstimmungsschutzvorrichtungen)</div>
            </div>
            <div class="gridContainer_2_column_smallright marginBottom_2">
                <div class="horizontal">
                    <div class="widthNumber"></div>
                    <div>
                        Damit die Wähler*innen die Stimmzettel unbeobachtet kennzeichnen konnten, waren im Wahlraum<span class="backendData paddingLeftRight">${data.anzahlWahltische}</span>Tische mit Sichtblenden vorbereitet, die nur vom Wahlraum aus betretbar waren. Vom Tisch des Wahlvorstands konnten die Tische mit Sichtblenden überblickt werden.
                    </div>
                </div>
                <div class="border vertical blueGrayBG negMarginTop">
                    <div class="padding">Beginn der Wahl:</div>
                    <div class="horizontal padding">
                        <div class="backendData paddingLeftRight">${data.eroeffnungsuhrzeit.stunde}</div>
                        <div>Uhr</div>
                        <div class="backendData paddingLeftRight">${data.eroeffnungsuhrzeit.minute}</div>
                        <div>Minuten</div>
                    </div>
                </div>
            </div>

            <!-- 2.4 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.4</div>
                <div class="bold">Berichtigung des Wählerverzeichnisses</div>
            </div>
            <div class="vertical ">
                <div class="horizontal marginBottom_2">
                    <div class="widthNumber">2.4.1</div>
                    <div class="horizontal paddingLeftRight">
                            <div class="backendDataColor fontSize_14 marginCheckbox">${data.wvz && data.wvz.verzeichnisLagVor ? "&#9746;" : "&#9744;"}</div>
                            <div class="paddingLeft">
                                Es gab kein Verzeichnis über nachträglich ausgestellte Wahlscheine. Das Wählerverzeichnis war
                                nicht zu berichtigen.
                            </div>
                    </div>
                </div>
                <div class="horizontal marginBottom_2">
                    <div class="widthNumber">2.4.2</div>
                    <div class="horizontal paddingLeftRight">
                        <div class="backendDataColor fontSize_14 marginCheckbox">${data.wvz && data.wvz.berichtigungVorBeginnDerAbstimmung ? "&#9746;" : "&#9744;"}</div>
                        <div class="paddingLeft">
                            Vor Beginn der Wahl berichtigte die oder der Wahlvorsteher*in das Wählerverzeichnis auf Anweisung des
                            Wahlamtes und hat dazu bei den in diesem Verzeichnis aufgeführten Wahlberechtigten in den Spalten für
                            die Stimmabgabevermerke „Wahlschein“ oder „W“ eingetragen.
                            Auch die Zahlen der Abschlussbeurkundung wurden auf Anweisung des Wahlamtes berichtigt.
                        </div>
                    </div>
                </div>
                <div class="horizontal marginBottom_2">
                    <div class="widthNumber">2.4.3</div>
                    <div class="horizontal paddingLeftRight">
                        <div class="backendDataColor fontSize_14 marginCheckbox">${data.wvz && data.wvz.nachtraeglicheBerichtigung ? "&#9746;" : "&#9744;"}</div>
                        <div class="paddingLeft">
                            Am Wahltag wurden von der Landeshauptstadt München noch Wahlscheine an erkrankte Stimmberechtigte
                            erteilt. Die oder der Wahlvorsteher*in berichtigte auf Anweisung des Wahlamtes das Wählerverzeichnis und
                            die dazugehörige Abschlussbeurkundung (wie in Ziffer 2.4.2).
                        </div>
                    </div>
                </div>
            </div>

            <!-- 2.5 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.5</div>
                <div class="bold">Schluss der Wahl</div>
            </div>
            <div class="marginBottom_2 gridContainer_2_column_smallright">
                <div class="horizontal">
                    <div class="widthNumber"></div>
                    <div class="vertical">
                        <div>
                            Um 18 Uhr gab die oder der Wahlvorsteher*in den Ablauf der Wahlzeit bekannt. Danach wurden nur noch die
                            im Wahlraum anwesenden Wahlberechtigten zur Stimmabgabe zugelassen.
                        </div>
                        <div>
                            Der Zutritt zum Wahlraum wurde solange gesperrt, bis alle noch anwesenden Wahlberechtigten ihre Stimmen
                            abgegeben haben. Sodann erklärte die oder der Wahlvorsteher*in die Wahl für geschlossen. Alle nicht benutzten
                            Stimmzettel wurden entfernt. Der Wahlraum wurde danach sofort wieder für die Öffentlichkeit geöffnet.
                        </div>
                    </div>
                </div>
                <div class="border vertical blueGrayBG negMarginTop">
                    <div class="padding">Schluss der Wahl:</div>
                    <div class="horizontal padding">
                        <div class="backendData paddingLeftRight">${data.schliessungsuhrzeit.stunde}</div>
                        <div>Uhr</div>
                        <div class="backendData paddingLeftRight">${data.schliessungsuhrzeit.minute}</div>
                        <div>Minuten</div>
                    </div>
                </div>
            </div>

            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>       
    `;
  }

  function _dataForChapterThree(data: NiederschriftDruckInputUWB) {
    return `
    <!-- 3 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">3.</div>
                <div class="bold">Ermittlung und Feststellung des Wahlergebnisses</div>
            </div>

            <!-- 3.1 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">3.1</div>
                <div class="bold">Vorbereitung</div>
            </div>
            <div class="marginBottom_2 horizontal">
                <div class="widthNumber"></div>
                <div>
                    Die Ermittlung und Feststellung des Wahlergebnisses wurde <b>unmittelbar nach</b> Schluss der Wahl und ohne
                    Unterbrechung vorgenommen. Die oder der Wahlvorsteher*in öffnete die Wahlurne und entnahm daraus die
                    Stimmzettel. Der Wahlvorstand überzeugte sich, dass die Wahlurne vollständig leer war.
                </div>
            </div>

            <!-- 3.2 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">3.2</div>
                <div class="bold">Wahlberechtigte</div>
            </div>
            <div class="marginBottom_2 horizontal">
                <div class="widthNumber"></div>
                <div>
                    Die oder der Schriftführer*in übertrug aus der - gegebenenfalls berichtigten – Abschlussbeurkundung des Wählerverzeichnisses die Zahl der Wahlberechtigten in Abschnitt 4.1 unter Kennbuchstaben <span class="border">A1</span>,  <span class="border">A2</span>  und  <span
                        class="border">A1 + A2</span> dieser Wahlniederschrift.
                </div>
            </div>

            <!-- 3.3 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">3.3</div>
                <div class="bold">Ermittlung der Zahl der Wähler*innen</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">3.3.1</div>
                <div>Die Zahl der Wähler*innen hat die oder der Schriftführer*in mit folgenden Angaben ermittelt:</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <table class="table borderless">
                    <colgroup>
                        <col width="4%"/>
                        <col width="68%"/>
                        <col width="12%"/>
                        <col width="10%"/>
                    </colgroup>
                    <tr>
                        <td>a)</td>
                        <td>Stimmabgabevermerke im Wählerverzeichnis</td>
                        <td>
                            <div class="border negMarginTop">
                                <div class="horizontal padding">
                                    <div class="backendData paddingLeftRight">${data.anzahlStimmabgabevermerke}</div>
                                </div>
                            </div>    
                        </td>
                        <td><div class="border side_padding negMarginTop">= B1</div></td>
                    </tr>
                    <tr>
                        <td>b)</td>
                        <td>Anzahl der eingenommenen Wahlscheinen</td>
                        <td>
                            <div class="border negMarginTop">
                                <div class="horizontal padding">
                                    <div class="backendData paddingLeftRight">${data.anzahlWahlscheine}</div>
                                </div>
                            </div>    
                        </td>
                        <td><div class="border side_padding negMarginTop">= B2</div></td>
                    </tr>
                    <tr>
                        <td>c)</td>
                        <td>Wähler*innen zusammen (a + b)</td>
                        <td>
                            <div class="border negMarginTop">
                                <div class="horizontal padding">
                                    <div class="backendData paddingLeftRight">${data.anzahlStimmabgabevermerke + data.anzahlWahlscheine}</div>
                                </div>
                            </div>    
                        </td>
                        <td><div class="border side_padding negMarginTop">&nbsp;= B</div></td>
                    </tr>
                </table>
            </div>

            <div class="horizontal marginBottom_2">
                <div class="widthNumber">3.3.2</div>
                <div>Die Stimmzettel wurden aus der Wahlurne entnommen und gezählt.</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Die Zahl der Stimmzettel betrug:<span class="backendData paddingLeftRight underline">${data.anzahlStimmzettel}</span></div>
            </div>

            <div class="horizontal marginBottom_2">
                <div class="widthNumber">3.3.3</div>
                <div>Kontrolle</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Die Zahl der Wähler*innen (Nr. 3.3.1 Buchst. c) stimmte mit der Zahl der Stimmzettel (Nr. 3.3.2)</div>
            </div>
            <div class="horizontal">
                <div class="widthNumber"></div>
                <div class="backendDataColor fontSize_14 marginCheckbox">${data.anzahlStimmabgabevermerke + data.anzahlWahlscheine === data.anzahlStimmzettel ? "&#9746;" : "&#9744;"}</div>
                <div class="paddingLeft">
                    überein
                </div>
            </div>
            <div class="horizontal">
                <div class="widthNumber"></div>
                <div class="backendDataColor fontSize_14 marginCheckbox">${data.anzahlStimmabgabevermerke + data.anzahlWahlscheine !== data.anzahlStimmzettel ? "&#9746;" : "&#9744;"}</div>
                <div class="paddingLeft">
                    aus folgenden Gründen nicht überein:
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div class="backendData underline fontSize_7">
                    ${data.anzahlStimmabgabevermerke + data.anzahlWahlscheine !== data.anzahlStimmzettel && data.begruendungStimmzettelumschlaege.grund ? data.begruendungStimmzettelumschlaege.grund : ""}
                </div>
            </div>

            <div class="horizontal marginBottom_2">
                <div class="widthNumber">3.3.4</div>
                <div>Die Zahl der Wähler*innen wurde in den Abschnitt 4.2 Kennbuchstaben <span class="border">B1</span>,  <span class="border">B2</span>  und  <span class="border">B</span> übertragen.</div>
            </div>
            
            

            <!-- 3.4 -->
            <div class="horizontal marginBottom_2 ">
                <div class="widthNumber bold">3.4</div>
                <div class="bold">Sortieren der Stimmzettel</div>
            </div>
            <div class="horizontal marginBottom_1">
                <div class="widthNumber"></div>
                <div>Die Stimmzettel wurden auf ihre Gültigkeit geprüft und auf folgende Stapeln sortiert:</div>
            </div>
            <div class="horizontal marginBottom_1 yellowBG" style="padding: 2px;">
                <div class="widthNumber yellowBG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">a)</div>
                <div class="whiteBG" style="width: 100%;">
                    zweifelsfrei gültige Stimmzettel, auf denen <b>nur ein Wahlvorschlag</b> unverändert gekennzeichnet wurde
(nur Kopfleistenkreuze), geordnet nach Wahlvorschlägen, &#8594; gelb
                </div>
            </div>
            <div class="horizontal marginBottom_1 greenBG" style="padding: 2px;">
                <div class="widthNumber greenBG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">b)</div>
                <div class="whiteBG" style="width: 100%;">
                    zweifelsfrei gültige Stimmzettel, die <b>innerhalb nur eines Wahlvorschlags</b> verändert gekennzeichnet wurden
(Einzelstimmvergabe mit und ohne Kopfleistenkreuz), geordnet nach Wahlvorschlägen, &#8594; hellgrün
                </div>
            </div>
            <div class="horizontal marginBottom_1 green_3_BG" style="padding: 2px;">
                <div class="widthNumber green_3_BG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">c)</div>
                <div class="whiteBG" style="width: 100%;">
                        zweifelsfrei gültige Stimmzettel, auf denen <b>verschiedene Wahlvorschläge</b> verändert gekennzeichnet wurden
(Einzelstimmvergabe mit und ohne Kopfleistenkreuz), &#8594; dunkelgrün
                </div>
            </div>
            <div class="horizontal marginBottom_1 redBG" style="padding: 2px;">
                <div class="widthNumber redBG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">d)</div>
                <div class="whiteBG" style="width: 100%;" >
                    nicht gekennzeichnete (leere) Stimmzettel, &#8594; rot
                </div>
            </div>
            <div class="horizontal marginBottom_2 purpurBG" style="padding: 2px;">
                <div class="widthNumber purpurBG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">e)</div>
                <div class="whiteBG" style="width: 100%;">
                    gekennzeichnete Stimmzettel (mit Anlass zu Bedenken), über die später vom Wahlvorstand Beschluss zu fassen war &#8594; lila
                </div>
            </div>           

            <!-- 3.5 -->
            <div class="horizontal marginBottom_2 ">
                <div class="widthNumber bold">3.5</div>
                <div class="bold">Bildung von Arbeitsgruppen</div>
            </div>
            <div class="marginBottom_2 horizontal">
                <div class="widthNumber"></div>
                <div>
                    Es wurden Arbeitsgruppen mit je zwei Personen gebildet. Sie bekamen die Zähllisten für bestimmte Wahlvorschläge.
                    Die Namen der Personen der jeweiligen Arbeitsgruppe wurden auf den Zähllisten notiert.
                </div>
            </div>

            <!-- 3.6 -->
            <div class="gridContainer_2_column_miniright marginBottom_2">
                <div>
                    <div class="horizontal">
                        <div class="widthNumber bold">3.6</div>
                        <div class="bold">Behandlung der Stimmzettel, die Anlass zu Bedenken gaben</div>
                    </div>
                    <div class="horizontal marginBottom_1">
                        <div class="widthNumber"></div>
                        <div><span class="bold lilatext">(Stapel gemäß 3.4 Buchst. e) &#8594; lila</span></div>
                    </div>
                    <div class="horizontal marginBottom_1">
                        <div class="widthNumber">3.6.1</div>
                        <div>Die oder der Wahlvorsteher*in zeigte jeden einzelnen Stimmzettel den Mitgliedern des Wahlvorstands und ließ über die Gültigkeit Beschluss fassen. Der Beschluss wurde auf der Rückseite des jeweiligen Stimmzettels mit Unterschrift vermerkt. Dabei wurde auch der Grund angegeben, warum eine Stimmvergabe für ungültig oder für gültig erklärt wurde.</div>
                    </div>
                    <div class="horizontal marginBottom_1">
                        <div class="widthNumber">3.6.2</div>
                        <div>
                            Die für <b>gültig</b> erklärten Stimmzettel wurden <b>gesondert</b> auf einem Stapel gesammelt. Sie werden mit den anderen gültigen Stimmzettel der Stapel (siehe Stapel gemäß 3.4 Buchst. <span class="yellowBG">a)</span>, <span class="greenBG">b)</span> oder <span class="green_3_text">c)</span> ausgewertet. Sie dürfen nicht mit diesen Stapeln vermischt werden.
                        </div>
                    </div>
                    <div class="horizontal ">
                        <div class="widthNumber">3.6.3</div>
                        <div>
                            Die für <b>ungültig</b> erklärten Stimmzettel wurden <b>gesondert</b> gesammelt und nicht mit dem Stapel <span class="redtext">d)</span> vermischt. Nach Beschlussfassung und Zählung der (ungültigen) Stimmzettel (siehe Nr. 3.8) werden diese in die Wahlverhandlungstasche gelegt.
                        </div>
                    </div>
                </div>
                <div class="purpurBG"></div>
            </div>

            <!-- 3.7 -->
            <div class="gridContainer_2_column_miniright marginBottom_2 ">
                <div>
                    <div class="horizontal">
                        <div class="widthNumber bold">3.7</div>
                        <div class="bold">Behandlung der nicht gekennzeichneten Stimmzettel</div>
                    </div>
                    <div class="horizontal marginBottom_1">
                        <div class="widthNumber"></div>
                        <div><span class="bold redtext">(Stapel gemäß 3.4 Buchst. d &#8594; rot)</span></div>
                    </div>
                    <div class="horizontal">
                        <div class="widthNumber"></div>
                        <div>
                            Stapel mit den nicht gekennzeichneten Stimmzetteln wurde geprüft.Die oder der Wahlvorsteher*in sagte
                            zu jedem Stimmzettel jeweils laut an, dass die Stimmvergabe ungültig ist.
                        </div>
                    </div>
                </div>
                <div class="redBG"></div>
            </div>

            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>

            <!-- 3.8 -->
            <div class="horizontal marginBottom_2 paddingTopNewPage">
                <div class="widthNumber bold">3.8</div>
                <div class="bold">Ermittlung der Zahl der ungültigen Stimmzettel</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Zwei Mitglieder des Wahlvorstands zählten unabhängig voneinander die nicht gekennzeichneten Stimmzettel und die durch Beschluss für ungültig erklärten Stimmzettel.</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Die Gesamtzahl der ungültigen Stimmzettel wurde in Abschnitt 4.3 bei Kennbuchstabe <span class="border">C</span> eingetragen.</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Die durch Beschluss für ungültig erklärten Stimmzettel (Nr. 3.6.3) wurden dann <b>sofort</b> in die Wahlverhandlungstasche gelegt. </div>
            </div>

            <!-- 3.9 -->
            <div class="gridContainer_2_column_miniright marginBottom_2">
                <div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber bold">3.9</div>
                        <div class="bold">Behandlung der Stimmzettel, auf denen nur ein Wahlvorschlag unverändert gekennzeichnet wurde <span class="yellowBG">(Stapel gemäß 3.4 Buchst. a – nur Kopfleistenkreuz) &#8594; gelb</span></div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>einziger Schritt: [Zählung der <b>Stimmzettel</b>]</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>Zwei Mitglieder einer Arbeitsgruppe zählten unabhängig voneinander die <b>Stimmzettel des der Arbeitsgruppe zugeteilten Wahlvorschlags</b>. Stimmte das Ergebnis der beiden Zählvorgänge nicht überein, wurde die Zählung wiederholt. Bei allen Zählungen wurde darauf geachtet, dass die Stimmzettel nach den Wahlvorschlägen richtig sortiert waren. Das Ergebnis wurde für jeden Wahlvorschlag in Abschnitt 4.3 bei Kennbuchstabe <span class="border">D1</span> usw. jeweils in Spalte 3 sowie in Abschnitt 4.4 bei Kennbuchstabe <span class="border">F</span> unter lfd. Nr. <span class="border">100</span> usw. eingetragen.</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>Die durch Beschluss für <b>gültig</b> erklärten Stimmzettel (Nr. 3.6.2) mit nur einem unverändert gekennzeichneten Wahlvorschlag wurden gesondert verwahrt. Sie wurden ebenfalls gezählt und bei jedem Wahlvorschlag
                            zur Summe der gültigen Stimmzettel mit Kopfleistenkreuz dazu gezählt. Danach wurden die mit Beschluss
                            für gültig erklärten Stimmzettel in die Wahlverhandlungstasche gelegt.</div>
                    </div>
                    <div class="horizontal">
                        <div class="widthNumber"></div>
                        <div>&#8594; Nach der Erfassung wurden die Stimmzettel von Stapel a) unverzüglich weggepackt und zurück in die
                            Urne gelegt. Sie werden nicht mehr über die Zähllisten erfasst.</div>
                    </div>
                </div>
                <div class="yellowBG"></div>
            </div>           

            <!-- 3.10 -->
            <div class="gridContainer_2_column_miniright marginBottom_2 ">
                <div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber bold">3.10</div>
                        <div class="bold">Behandlung der Stimmzettel, die innerhalb nur eines Wahlvorschlags verändert gekennzeichnet wurden <span class="greenBG">(Stapel gemäß 3.4 Buchst. b) &#8594; hellgrün</span></div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>Schritt 1: [Zählung der <b>Stimmzettel</b>]</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>Zwei Mitglieder einer Arbeitsgruppe zählten unabhängig voneinander die <b>Stimmzettel des der Arbeitsgruppe
                            zugeteilten Wahlvorschlags</b>. Stimmte das Ergebnis der beiden Zählvorgänge nicht überein, wurde
                            die Zählung wiederholt. Bei allen Zählungen wurde darauf geachtet, dass die Stimmzettel richtig nach den
                            Wahlvorschlägen sortiert waren. Das Ergebnis wurde für jeden Wahlvorschlag in Abschnitt 4.3 bei Kennbuchstabe <span class="border">D1</span> usw. jeweils in Spalte 4 eingetragen.</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Die durch Beschluss für <b>gültig</b> erklärten Stimmzettel (Nr. 3.6.2) mit Kennzeichnung innerhalb nur eines
                            Wahlvorschlages wurden gesondert verwahrt. Sie wurden ebenfalls gezählt und bei jedem Wahlvorschlag
                            zur Summe der gültigen Stimmzettel mit Kennzeichnung nur innerhalb eines Wahlvorschlages dazu gezählt.
                            Diese wurden getrennt von den anderen Stapeln aufbewahrt, da sie nach der vollständigen Auswertung in
                            die Wahlverhandlungstasche gelegt werden müssen.
                        </div>
                    </div>
                </div>
                <div class="greenBG"></div>
            </div>

            <!-- 3.11 -->
            <div class="horizontal marginBottom_2 ">
                <div class="widthNumber bold">3.11</div>
                <div class="bold">Schnellmeldung</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Für die Schnellmeldung wurden die Ergebnisse aus Abschnitt 4.3 Spalte 3 und 4 in <b>Spalte 5</b> zusammengezählt und in den hierfür vorgesehenen Wahlvordruck V 3 übertragen. Die Ergebnisse der Schnellmeldung
                    wurden der Wahlleitung übermittelt. Danach wurde die Schnellmeldung in die Wahlverhandlungstasche gelegt. 
                </div>
            </div>
            
            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>
            <!-- 3.12 -->
            <div class="gridContainer_2_column_miniright marginBottom_2 paddingTopNewPage">
                <div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber bold">3.12</div>
                        <div class="bold">Behandlung der Stimmzettel (Zähllisten), die innerhalb nur eines Wahlvorschlags verändert gekennzeichnet wurden <span class="greenBG">(Stapel gemäß 3.4 Buchst. b) &#8594; hellgrün</span></div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>Schritt 2: [Zählung der einzelnen <b>Stimmen</b>]</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Die Arbeitsgruppen erfassen alle Stimmen für die ihnen zugeordneten Wahlvorschläge durch Abstreichen
                            der einzelnen Bewerber*innen in der Zählliste, indem die Stimmen für die einzelnen sich bewerbenden Personen
                            durch eine Person der Arbeitsgruppe einzeln verlesen und von der anderen Person der Arbeitsgruppe
                            sofort bei Verlesung in der Zählliste abgestrichen wird. Die Stimmenzahl wird dabei wiederholt.
                        </div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Die oder der Wahlvorsteher*in und die Stellvertretung überwachen die ordnungsgemäße Führung der Zähllisten.
                            Auf den Zähllisten werden die Namen der Personen der Arbeitsgruppe erfasst. Die Zähllisten sind zu
                            unterschreiben (von Wahlvorsteher*in und Arbeitsgruppe).
                        </div>
                    </div>
                    <div class="horizontal">
                        <div class="widthNumber"></div>
                        <div>
                            Die durch Beschluss für gültig erklärten Stimmzettel (Nr. 3.6.2), die innerhalb nur eines Wahlvorschlags verändert
                            gekennzeichnet sind, wurden als erste bearbeitet und sofort nach der Auswertung und Erfassung in
                            den Zähllisten in die Wahlverhandlungstasche gelegt.
                        </div>
                    </div>
                </div>
                <div class="greenBG"></div>
            </div>

            <!-- 3.13 -->
            <div class="gridContainer_2_column_miniright marginBottom_2">
                <div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber bold">3.13</div>
                        <div class="bold">Behandlung der Stimmzettel, auf denen verschiedene Wahlvorschläge verändert gekennzeichnet wurden <span class="green_3_text">(Stapel gemäß 3.4 Buchst. c) &#8594; dunkelgrün</span></div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>[Zählung der einzelnen <b>Stimmen</b>]</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Wurden verschiedene Wahlvorschläge gekennzeichnet, erfasst zunächst die erste Arbeitsgruppe die Stimmen
                            wie unter 3.12 beschrieben, anschließend wird auf dem Stimmzettel vermerkt, für welchen Wahlvorschlag
                            er ausgewertet wurde (Abhaken im Listenkopf mit dem violetten Kugelschreiber). Dann wird der
                            Stimmzettel an die nächste Arbeitsgruppe zur Auswertung weitergegeben.
                        </div>
                    </div>
                    <div class="horizontal">
                        <div class="widthNumber"></div>
                        <div>
                            Die durch Beschluss für gültig erklärten Stimmzettel (Nr. 3.6.2) auf denen verschiedene Wahlvorschläge verändert
                            gekennzeichnet sind, wurden als erste bearbeitet und <b>sofort</b> nach der Auswertung und Erfassung in
                            den Zähllisten in die Wahlverhandlungstasche gelegt.
                        </div>
                    </div>
                </div>
                <div class="green_3_BG"></div>
            </div>

            <!-- 3.14 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">3.14</div>
                <div class="bold">Bildung der Gesamtsumme aller Stimmen</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    In den Zähllisten wird für jede einzelne sich bewerbende Person die Gesamtzahl der abgestrichenen Stimmen
                    eingetragen. Diese Ergebnisse werden in Abschnitt 4.4 bei Kennbuchstabe <span class="border">F</span> bei den einzelnen sich bewerbenden
                    Personen der jeweiligen Wahlvorschläge eingetragen. Anschließend wird die Gesamtstimmenzahl der
                    auf die einzelnen Wahlvorschläge insgesamt entfallenen gültigen Stimmen ermittelt. Dazu werden alle für die
                    einzelnen Personen abgegebenen gültigen Stimmen zusammen gezählt. Die so ermittelte Gesamtzahl wird in
                    Abschnitt 4.3 bei Kennbuchstaben <span class="border">D1</span> usw. in Spalte 6 eingetragen.
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Die Zähllisten sind zu unterschreiben (von Wahlvorsteher*in und Arbeitsgruppe).
                </div>
            </div>

            <!-- 3.15 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">3.15</div>
                <div class="bold">Feststellung des Ergebnisses im Wahlbezirk</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Der Wahlvorstand hat das Ergebnis aus Abschnitt 4 als das Ergebnis des Wahlbezirks festgestellt. Die oder der
                    Wahlvorsteher*in hat das Ergebnis im Wahlraum mündlich bekannt gegeben.
                </div>
            </div>
            
            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>
    `;
  }

  function _dataForChapterFour(data: NiederschriftDruckInputUWB) {
    return `
    <!-- 4. -->
            <div class="horizontal marginBottom_2 paddingTopNewPage">
                    <div class="widthNumber bold">4.</div>
                    <div class="bold">Wahlergebnis</div>
            </div>
            <div class="horizontal marginTop_5 marginBottom_2">
                    <div class="widthNumber bold">4.1</div>
                    <div class="bold">WAHLBERECHTIGTE (s. 3.2)</div>
            </div>
            <div class="horizontal marginBottom_2">
                <table class="table width_100">
                    <colgroup>
                        <col width="11.0%"/>
                        <col width="72.3%"/>
                        <col width="16.7%"/>
                    </colgroup>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">Kennbuchstabe</td>
                        <td class="blueGrayBG textAlignCenter">Bezeichnung</td>
                        <td class="blueGrayBG textAlignCenter">Anzahl</td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">A 1</td>
                        <td>Wahlberechtigte <span class="bold">ohne</span> Vermerk „W“ (Wahlschein) lt. Wählerverzeichnis</td>
                        <td class="border border_bold backendData textAlignRight">${data.a1}</td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">A 2</td>
                        <td>Wahlberechtigte <span class="bold">mit</span> Vermerk „W“ (Wahlschein) lt. Wählerverzeichnis</td>
                        <td class="border border_bold noBorderTop backendData textAlignRight">${data.a2}</td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">A 1 + A 2</td>
                        <td>Wahlberechtigte <span class="bold">zusammen</span></td>
                        <td class="border border_bold noBorderTop backendData textAlignRight">${data.aWerte}</td>
                    </tr>
                </table>
            </div>

            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">4.2</div>
                <div class="bold">Wähler*innen (s. 3.3)</div>
            </div>
            <div class="horizontal marginBottom_2">
                <table class="table width_100">
                    <colgroup>
                        <col width="11.0%"/>
                        <col width="72.3%"/>
                        <col width="16.7%"/>
                    </colgroup>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">Kennbuchstabe</td>
                        <td class="blueGrayBG textAlignCenter">Bezeichnung</td>
                        <td class="blueGrayBG textAlignCenter">Anzahl</td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">B 1</td>
                        <td>Wähler*innen mit Stimmabgabevermerken im Wählerverzeichnis</td>
                        <td class="border border_bold backendData textAlignRight">${data.b1}</td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">B 2</td>
                        <td>Wähler*innen mit Wahlschein</td>
                        <td class="border border_bold noBorderTop backendData textAlignRight">${data.anzahlWahlscheine}</td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">B</td>
                        <td>Wähler*innen <span class="bold">zusammen</span> (B 1 + B 2) = Zahl der abgegebenen Stimmzettel</td>
                        <td class="border border_bold noBorderTop backendData textAlignRight">${data.bWerte}</td>
                    </tr>
                </table>
            </div>

            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>

            <div class="horizontal marginBottom_2 paddingTopNewPage">
                <div class="widthNumber bold">4.3</div>
                <div class="bold">STIMMEN (s. 3.4 bis 3.15)</div>
            </div>
            
            <table class="table uncollapsed width_100 marginBottom_2">
                <colgroup>
                    <col width="10.5%"/>
                    <col width="38.2%"/>
                    <col width="12%"/>
                    <col width="12.65%"/>
                    <col width="12.65%"/>
                    <col width="14%"/>
                </colgroup>
                <tr>
                    <th rowspan="2" class="blueGrayBG textAlignCenter noPadding">Kennbuchstabe</br>(Ordnungszahl)</th>
                    <th rowspan="2" class="blueGrayBG textAlignCenter noPadding">Name des Wahlvorschlags (Kennwort)</th>
                    <th colspan="3" class="blueGrayBG textAlignCenter noPadding">Gültige Stimm<b>zettel</b></th>
                    <th rowspan="2" class="blueGrayBG textAlignCenter green_3_border border_bold noPadding">Gültige</br><b>kumulierte</b> und </br><b>panaschierte Stimmen</b></br><b>insgesamt</b></br>(keine unveränderten)</th>
                </tr>
                <tr>
                    <th class="blueGrayBG textAlignCenter  yellow_border border_bold noPadding" style="vertical-align: top;" >ein Wahlvorschlag</br><b>unverändert</b> gekennzeichnet</th>
                    <th class="blueGrayBG textAlignCenter  green_border border_bold" style="vertical-align: top; padding: 0cm 0.1cm;">Innerhalb <b>eines</b> Wahlvorschlags <b>verändert</b></th>
                    <th class="blueGrayBG textAlignCenter  border border_bold" style="vertical-align: top; padding: 0cm 0.1cm;">Gültige Stimmzettel für genau <b>einen</b> Wahlvorschlag <b>(Spalte 3 + Spalte 4)</b></th>
                </tr>
                <tr>
                    <th class="blueGrayBG textAlignCenter">1</th>
                    <th class="blueGrayBG textAlignCenter">2</th>
                    <th class="blueGrayBG textAlignCenter yellow_border_noTop border_bold">3</th>
                    <th class="blueGrayBG textAlignCenter green_border_noTop border_bold">4</th>
                    <th class="blueGrayBG textAlignCenter border_noTop border_bold">5</th>
                    <th class="blueGrayBG textAlignCenter green_3_border_noTop border_bold">6</th>
                </tr>
                ${data.gueltigeStimmenListe
                  .map((gueltigeStimmen) => {
                    return `<tr>
                    <td class="blueGrayBG textAlignCenter">D ${gueltigeStimmen.ordnungszahl}</td>
                    <td class="backendData">${gueltigeStimmen.parteiname}</td>
                    <td class="yellow_border_noTop border_bold backendData textAlignRight">${gueltigeStimmen.stapelA}</td>
                    <td class="green_border_noTop border_bold backendData textAlignRight">${gueltigeStimmen.stapelB}</td>
                    <td class="border_noTop border_bold backendData textAlignRight">${gueltigeStimmen.gesamt}</td>
                    <td class="green_3_border_noTop border_bold backendData textAlignRight">${gueltigeStimmen.stapelBC}</td>
                    
                </tr>
                ${
                  gueltigeStimmen.ordnungszahl <
                    data.gueltigeStimmenListe.length &&
                  gueltigeStimmen.ordnungszahl % 25 === 0
                    ? `
                </table>
                <svg class="page_break" height="25px" width="100%">
                    <rect width="1000" height="25" style="fill: #ffffff;" />
                </svg>
                <table class="table uncollapsed width_100 paddingTopNewPage marginBottom_2">
                <colgroup>
                    <col width="10.5%"/>
                    <col width="38.2%"/>
                    <col width="12%"/>
                    <col width="12.65%"/>
                    <col width="12.65%"/>
                    <col width="14%"/>
                </colgroup>
                <tr>
                    <th rowspan="2" class="blueGrayBG textAlignCenter noPadding">Kennbuchstabe</br>(Ordnungszahl)</th>
                    <th rowspan="2" class="blueGrayBG textAlignCenter noPadding">Name des Wahlvorschlagsträgers (Kennwort)</th>
                    <th colspan="3" class="blueGrayBG textAlignCenter noPadding">Gültige Stimm<b>zettel</b></th>
                    <th rowspan="2" class="blueGrayBG textAlignCenter green_3_border border_bold noPadding">Gültige</br><b>kumulierte</b> und </br><b>panaschierte Stimmen</b></br><b>insgesamt</b></br>(keine unveränderten)</th>
                </tr>
                <tr>
                    <th class="blueGrayBG textAlignCenter yellow_border border_bold noPadding" style="vertical-align: top;" >ein Wahlvorschlag</br><b>unverändert</b> gekennzeichnet</th>
                    <th class="blueGrayBG textAlignCenter green_border border_bold" style="vertical-align: top; padding: 0cm 0.1cm;">Innerhalb <b>eines</b> Wahlvorschlags <b>verändert</b></th>
                    <th class="blueGrayBG textAlignCenter border border_bold" style="vertical-align: top; padding: 0cm 0.1cm;">Gültige Stimmzettel für genau <b>einen</b> Wahlvorschlag <b>(Spalte 3 + Spalte 4)</b></th>
                </tr>
                <tr>
                    <th class="blueGrayBG textAlignCenter">1</th>
                    <th class="blueGrayBG textAlignCenter">2</th>
                    <th class="blueGrayBG textAlignCenter yellow_border_noTop border_bold">3</th>
                    <th class="blueGrayBG textAlignCenter green_border_noTop border_bold">4</th>
                    <th class="blueGrayBG textAlignCenter border_noTop border_bold">5</th>
                    <th class="blueGrayBG textAlignCenter green_3_border_noTop border_bold">6</th>
                </tr>
                `
                    : ""
                }
                `;
                  })
                  .join("")}
                <tr>
                    <td class="blueGrayBG textAlignCenter"><span class="bold">D</span></td>
                    <td>
                        <div class="horizontal spaceBetween">
                            <div>
                                <span class="bold">Gültige</span> insgesamt
                            </div>
                            <div class="fontSize_7 alignSelfEnd paddingLeftRight">
                                (Summe aus D${data.gueltigeStimmenListe[0].ordnungszahl} bis
                                D${data.gueltigeStimmenListe[data.gueltigeStimmenListe.length - 1].ordnungszahl})
                            </div>
                        </div>
                    </td>
                    <td class="yellow_border_noTop border_bold backendData textAlignRight">${data.gueltigeStimmenErgebnisGesamt.stapelA}</td>
                    <td class="green_border_noTop border_bold backendData textAlignRight">${data.gueltigeStimmenErgebnisGesamt.stapelB}</td>
                    <td class="border_noTop border_bold backendData textAlignRight">${data.gueltigeStimmenErgebnisGesamt.gesamt}</td>
                    <td class="green_3_border_noTop border_bold backendData textAlignRight">${data.gueltigeStimmenErgebnisGesamt.stapelBC}</td>
                </tr>
            </table>
            
            <div class="horizontal marginBottom_2">
                <table class="table" style="width: 86%">
                    <colgroup>
                            <col width="12.2%"/>
                            <col width="73.1%"/>
                            <col width="14.7%"/>
                            
                    </colgroup>
                    <tr>
                        <td class="red_border border_bold blueGrayBG textAlignCenter">C</td>
                        <td class="red_border border_bold"><span class="bold">Ungültige</span> Stimm<span class="bold">zettel</span>
                        </td>
                        <td class="red_border border_bold backendData textAlignRight">${data.ungueltigeStimmen}</td>
                    </tr>
                </table>
            </div>
            
            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>

            <!-- 4.4 -->
            <div class="horizontal marginBottom_2 paddingTopNewPage">
                <div class="widthNumber bold">4.4</div>
                <div class="bold">KANDIDAT*INNEN – Auswertung</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="horizontal widthNumber">
                    <div class="border blueGrayBG paddingLeftRight bold">F</div>
                    <div></div>
                </div>
                <div>Ergebnis der auf die einzelnen sich bewerbenden Personen entfallenen gültigen Stimmen (siehe Nr. 3.14)</div>
            </div>
            ${data.parteienListe
              .map((partei) => {
                return `<table class="table width_100 marginTop_5 marginBottom_2">
                    <colgroup>
                        <col width="25%"/>
                        <col width="25%"/>
                        <col width="25%"/>
                        <col width="25%"/>
                    </colgroup>
                    <tr>
                        <th class="blueGrayBG textAlignCenter">Wahlvorschlag Nr.</th>
                        <th class="blueGrayBG textAlignCenter">${partei.ordnungszahl}</th>
                        <th class="blueGrayBG textAlignCenter">Kennwort</th>
                        <th class="textAlignCenter">${partei.kurzname}</th>
                    </tr>
                </table>
                <table class="table width_100 marginBottom_2">
                    <colgroup>
                        <col width="7.36%"/>
                        <col width="72%"/>
                        <col width="20.6%"/>
                    </colgroup>
                    <tr>
                        <th class="blueGrayBG textAlignCenter">Lfd. Nr.</th>
                        <th rowspan="2" class="yellowBG textAlignCenter">Unveränderte Stimmzettel<br/>(Die Stimmenzahl wurde aus Abschnitt 4.3 Kennbuchstabe D ${partei.ordnungszahl} Spalte 3 übertragen.)</th>
                        <th rowspan="2" class="textAlignCenter backendData">${
                          data.gueltigeStimmenListe.find((gueltigeStimme) => {
                            return (
                              gueltigeStimme.ordnungszahl ===
                              partei.ordnungszahl
                            );
                          }).stapelA
                        }</th>
                    </tr>
                    <tr>
                        <th class="blueGrayBG textAlignRight">${partei.direktKandMit00 ? partei.direktKandMit00.laufendeNr : parseInt(partei.ordnungszahl) * 100}</th>
                        
                    </tr>
                </table>
                <table class="table uncollapsed width_100 marginBottom_2">
                    <colgroup>
                    ${partei.maxcols
                      .map((col) => {
                        return `<col width="${col.width1}%"/><col width="${col.width2}%"/>`;
                      })
                      .join("")}
                    </colgroup>
                    <tr>
                    ${partei.maxcols
                      .map(() => {
                        return `<th class="blueGrayBG textAlignCenter">Lfd. Nr.</th><th class="blueGrayBG textAlignCenter">Stimmen</th>`;
                      })
                      .join("")}
                    </tr>
                    ${partei._tabledata
                      .map((zeile, zeilenIndex) => {
                        return `<tr>
                            ${partei.maxcols
                              .map((col, idx) => {
                                const kand =
                                  idx < zeile.length && zeile[idx]
                                    ? zeile[idx]
                                    : { laufendeNr: "", ergebnis: "" };
                                kand.laufendeNr = "" + kand.laufendeNr;
                                kand.ergebnis =
                                  null !== kand.ergebnis
                                    ? "" + kand.ergebnis
                                    : "";
                                return `<td class="textAlignRight ${!kand.laufendeNr.length || !kand.ergebnis.length ? "blueGrayBG" : ""}">${kand.laufendeNr}</td><td class="textAlignRight ${!kand.ergebnis.length ? "blueGrayBG" : "backendData"}">${kand.ergebnis}</td>`;
                              })
                              .join("")}
                            </tr>
                            ${
                              (zeilenIndex + 1) % 31 === 0
                                ? `
                                </table>
                                <svg class="page_break" height="25px" width="100%">
                                    <rect width="1000" height="25" style="fill: #ffffff;" />
                                </svg>
                                <table class="table uncollapsed width_100 marginBottom_2 paddingTopNewPage">
                                    <colgroup>
                                    ${partei.maxcols
                                      .map((col) => {
                                        return `<col width="${col.width1}%"/><col width="${col.width2}%"/>`;
                                      })
                                      .join("")}
                                    </colgroup>
                                    <tr>
                                    ${partei.maxcols
                                      .map(() => {
                                        return `<th class="blueGrayBG textAlignCenter">Lfd. Nr.</th><th class="blueGrayBG textAlignCenter">Stimmen</th>`;
                                      })
                                      .join("")}
                                    </tr>
                                `
                                : ""
                            }
                    `;
                      })
                      .join("")}
                    <tr class="noBorder">
                        ${partei.maxcols
                          .map(() => {
                            return `<th class="noBorder"></th><th class="noBorder"></th>`;
                          })
                          .join("")}
                    </tr>
                    <tr>
                        ${partei.maxcols
                          .map((col) => {
                            return `<th class="textAlignCenter">Zus.</th><th class="textAlignRight backendData">${col.colsum}</th>`;
                          })
                          .join("")}
                    </tr>
                </table>
                
                <table class="table uncollapsed width_100 marginBottom_2">
                    <colgroup>
                        <col width="70%"/>
                        <col width="30%"/>
                    </colgroup>
                    <tr>
                        <th class="blueGrayBG textAlignRight bold">Gesamtsumme:</th>
                        <th class="green_3_border border_bold textAlignRight backendData">${
                          data.gueltigeStimmenListe.find((gueltigeStimme) => {
                            return (
                              gueltigeStimme.ordnungszahl ===
                              partei.ordnungszahl
                            );
                          }).stapelBC
                        }</th>
                    </tr>
                </table>
                <div class="marginBottom_2">Die Gesamtstimmenzahl wurde oben in den Abschnitt 4.3 Kennbuchstabe <b>D ${partei.ordnungszahl} in Spalte 6</b> übertragen.</div>
                <svg class="page_break" height="25px" width="100%">
                    <rect width="1000" height="25" style="fill: #ffffff;" />
                </svg>
                `;
              })
              .join("")}
`;
  }

  function _dataForChapterFive(data: NiederschriftDruckInputUWB) {
    return `
    <!-- 5. -->
                
            <div class="horizontal marginBottom_2 paddingTopNewPage">
                <div class="widthNumber bold">5.</div>
                <div class="bold">Abschluss der Feststellung des Abstimmungsergebnisses</div>
            </div>

            <!-- 5.1 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.1</div>
                <div class="bold">Besondere Vorfälle</div>
            </div>
            <div class="horizontal">
                <div class="widthNumber"></div>
                <div class="backendDataColor fontSize_14 marginCheckbox">${!data.ereignisse || !data.ereignisse.hasEreignisse ? "&#9746;" : "&#9744;"}</div>
                <div class="paddingLeft">
                    Es ereigneten sich keine besonderen Vorfälle.
                </div>
            </div>
            <div class="horizontal">
                <div class="widthNumber"></div>
                <div class="backendDataColor fontSize_14 marginCheckbox">${data.ereignisse && data.ereignisse.hasEreignisse ? "&#9746;" : "&#9744;"}</div>
                <div class="paddingLeft">
                    Es ereigneten sich folgende besonderen Vorfälle (z.B. Zurückweisung von Wahlberechtigten &#8594; bei
                    Bedarf eine Anlage beifügen):
                </div>
            </div>
            <div class="horizontal marginBottom_1">
                <div class="widthNumber"></div>
                <div class="width_100"></br><hr></br><hr></br><hr></div>
            </div>
            
            <!-- 5.2 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.2</div>
                <div class="bold">Anwesenheit des Wahlvorstands</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Während der Wahl sowie während der Ermittlung und der Feststellung des Wahlergebnisses waren immer die
                    Mitglieder aus dem Wahlvorstand mit der Funktion Wahlvorsteher*in und Schriftführer*in oder deren Stellvertretung
                    sowie mindestens ein*e Beisitzer*in anwesend.
                </div>
            </div>

            <!-- 5.3 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.3</div>
                <div class="bold">Öffentlichkeit der Wahlhandlung</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Die Wahl sowie die Ermittlung und die Feststellung des Wahlergebnisses waren öffentlich.</div>
            </div>

            <!-- 5.4 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.4</div>
                <div class="bold">Unterschriften der Mitglieder des Wahlvorstands</div>
            </div>
            
            
            <div class="marginBottom_2 gridContainer_2_column_smallright">
                <div class="horizontal">
                    <div class="widthNumber">5.4.1</div>
                    <div>
                        Vorstehende Niederschrift wurde von allen Mitgliedern des Wahlvorstands durch ihre Unterschrift genehmigt.
                    </div>
                </div>
                <div class="border vertical negMarginTop">
                    <div class="padding">Unterschriften der Mitglieder des Wahlvorstands</div>
                </div>
            </div>
            <div class="marginBottom_2 marginTop_5 horizontal">
                <div class="widthNumber"></div>
                <table class="table borderless">
                    <colgroup>
                        <col width="50%"/>
                        <col width="40%"/>
                        <col width="10%"/>
                    </colgroup>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Wahlvorsteher*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Stellvertretung Wahlvorsteher*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Schriftführer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Stellvertretung Schriftführer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.61cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                </table>
            </div>
            <div class="horizontal marginBottom_2">
                    <div class="widthNumber">5.4.2</div>
                    <div>Folgende Mitglieder des Wahlvorstands verweigerten aus nachstehenden Gründen die Unterschrift:</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div class="vertical">
                    <div style="height: 0.61cm;" class="horizontal spacebetween">
                        <div>Name&nbsp;</div>
                        <div>&nbsp;______________________&nbsp;</div>
                        <div>&nbsp;Grund</div>
                        <div>&nbsp;_______________________________________</div>
                    </div>
                    <div style="height: 0.61cm;" class="horizontal spacebetween">
                        <div>Name&nbsp;</div>
                        <div>&nbsp;______________________&nbsp;</div>
                        <div>&nbsp;Grund</div>
                        <div>&nbsp;_______________________________________</div>
                    </div>
                    <div style="height: 0.61cm;" class="horizontal spacebetween">
                        <div>Name&nbsp;</div>
                        <div>&nbsp;______________________&nbsp;</div>
                        <div>&nbsp;Grund</div>
                        <div>&nbsp;_______________________________________</div>
                    </div>
                </div>
            </div>    

            <!-- 5.5 -->
            <div class="horizontal marginBottom_2 ">
                    
                <div class="widthNumber bold">5.5</div>
                <div class="bold">Ordnen und Verpacken der Wahlunterlagen</div>
            </div>
            <div class="marginBottom_2">
                <div class="horizontal">
                    <div class="widthNumber"></div>
                    <div>
                        Nach Feststellung des Wahlergebnisses wurden alle Stimmzettel, die nicht dieser Wahlniederschrift als Anlage
                        beigefügt sind, wie folgt geordnet und verpackt:
                    </div>
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.5.1</div>
                <div class="backendDataColor fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingLeft">
                    ein Paket mit den gültigen Stimmzetteln (nur solche ohne Beschluss). Es wurden getrennt nach Wahlvorschlägen</br>
                    - die unveränderten Stimmzettel mit nur einem Kopfleistenkreuz <span class="yellowBG">(Stapel a) gelb)</span>,</br>
                    - die nur innerhalb eines Wahlvorschlages gekennzeichneten Stimmzettel <span class="greenBG">(Stapel b) hellgrün)</span>,</br> 
                    verpackt.
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.5.2</div>
                <div class="backendDataColor fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingLeft">
                    ein Paket mit den gültigen Stimmzettel, auf denen verschiedene Wahlvorschläge verändert gekennzeichnet wurden <span class="green_3_text">(Stapel c) dunkelgrün)</span>,
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.5.3</div>
                <div class="backendDataColor fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingLeft">
                    ein Paket mit den nicht gekennzeichneten Stimmzetteln (Stapel gemäß 3.4 → <span class="bold redtext">Stapel d</span>),
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.5.4</div>
                <div class="backendDataColor fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingLeft">
                    die beschlussmäßig behandelten Stimmzettel (gültig und ungültig) <b>wurden spätestens jetzt in die Wahlverhandlungstasche</b> eingelegt (Stapel gemäß 3.4 → <span class="bold lilatext">Stapel e</span>),
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.5.5</div>
                <div class="backendDataColor fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingLeft">
                    ein Paket mit den unbenutzten Stimmzetteln.
                </div>
            </div>
            

            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill:#ffffff;" />
            </svg>
            
            <!-- 5.6 -->
            <div class="horizontal marginBottom_2 paddingTopNewPage">
                <div class="widthNumber bold">5.6</div>
                <div class="bold">Übergabe der Wahlunterlagen</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.6.1</div>
                <div>
                    In die Wahlverhandlungstasche wurden die nachfolgenden Unterlagen eingelegt:
                </div>
            </div>
            <div class="vertical marginBottom_2">
                <div class="horizontal">
                    <div class="widthNumber"></div>
                    <div class="vertical">
                        <div>- diese (unterschriebene) Niederschrift,</div>
                        <div>- die Schnellmeldung V3,</div>
                        <div>- die beschlussmäßig behandelten Stimmzettel,</div>
                        <div>- Zähllisten für alle Wahlvorschläge</div>
                        <div>- eingenommene Wahlscheine.</div>
                    </div>
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Anschließend wurde die Wahlverhandlungstasche mit einem Klebesiegel versiegelt und in die Urne gelegt.
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Das Wählerverzeichnis wurde ebenfalls in die Urne gelegt.
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.6.2</div>
                <div>
                    Die Stimmzettelpakete wie in Nr. 5.5 beschrieben wurden in die Urne gelegt und diese Urne wurde dann versiegelt.
                </div>
            </div>
            
            ${
              data.ereignisse &&
              data.ereignisse.vorfaelle &&
              data.ereignisse.vorfaelle.length > 0
                ? `<svg class="page_break" height="25px" width="100%">
                    <rect width="1000" height="25" style="fill: #ffffff;" />
                </svg><br/><h1>Anlage V1 Vorfälle und Vorkommnisse</h1><br/>` +
                  data.ereignisse.vorfaelle
                    .map((vorfall, i) => {
                      const counter = i + 1;
                      return (
                        `<div>Um ` +
                        vorfall.uhrzeit +
                        ` ereignete sich folgendes Vorfall: Vf` +
                        counter +
                        ` ` +
                        vorfall.beschreibung +
                        `</div>`
                      );
                    })
                    .join("")
                : ""
            }

            ${
              data.ereignisse &&
              data.ereignisse.vorkommnisse &&
              data.ereignisse.vorkommnisse.length > 0
                ? `<svg class="page_break" height="25px" width="100%">
                    <rect width="1000" height="25" style="fill: #ffffff;" />
                </svg><br/><h1>Anlage V1 Vorfälle und Vorkommnisse</h1><br/>` +
                  data.ereignisse.vorkommnisse
                    .map((vorkommnis, i) => {
                      const counter = i + data.ereignisse.vorfaelle.length + 1;
                      return (
                        `<div>Um ` +
                        vorkommnis.uhrzeit +
                        ` ereignete sich folgendes Vorkommnis: Vk` +
                        counter +
                        ` ` +
                        vorkommnis.beschreibung +
                        `</div>`
                      );
                    })
                    .join("")
                : ""
            }
    `;
  }

  function _getStyling() {
    return `
    <style type="text/css">
                    /****** Print Header ******/
                    @page {
                        size: A4;
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
                        max-width: 17cm;
                        margin-top: 0cm;
                        margin-right: 2cm;
                        margin-bottom: 0.2cm;
                        margin-left: 2cm;
                        font-size: 9pt;
                        writing-mode: horizontal-tb;
                        font-family: Arial, serif;
                        -ms-hyphens: auto;
                        -webkit-hyphens: auto;
                        hyphens: auto;
                    }

                    /****** Table ******/
                    table {
                        border-collapse: collapse;
                    }

                    table.uncollapsed {
                        border-collapse: separate;
                        border-spacing: 0px;
                    }

                    table.borderless {
                        border-width: 0px;
                    }

                    table.borderless th {
                        border-width: 0px;
                    }

                    table.borderless td {
                        border-width: 0px ;
                    }

                    table.backandborder {
                        border: 1px solid #546e7a;
                    }

                    table.backandborder th {
                        border: 1px solid #546e7a;
                    }

                    table.backandborder td {
                        border: 1px solid #546e7a;
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

                    .linestable {
                        width: 80%;
                    }

                    .linestable th {
                        border-top: none;
                        border-left: none;
                        border-right: none;
                        height: 1cm;
                    }

                    .linestable td {
                        border-top: none;
                        border-left: none;
                        border-right: none;
                        height: 1cm;
                    }

                    hr { 
                        display: block;
                        margin-before: 0.5em;
                        margin-after: 0.5em;
                        margin-start: auto;
                        margin-end: auto;
                        overflow: hidden;
                        border-style: inset;
                        border-width: 1px;
                    }

                    /****** Borders ******/
                    .noBorder {
                        border-bottom: none;
                        border-left: none;
                        border-right: none;
                        border-top: none;
                    }

                    .noBorderTop {
                        border-bottom: 1px solid black;
                        border-left: 1px solid black;
                        border-right: 1px solid black;
                        border-top: none;
                    }

                    .border {
                        border: 1px solid #000000;
                    }

                    .border_noTop {
                        border: 1px solid #000000;
                        border-top: none;
                    }

                    .yellow_border {
                        border: 1px solid #ffff99;
                    }

                    .yellow_border_noTop {
                        border: 1px solid #ffff99;
                        border-top: none;
                    }

                    .green_border {
                        border: 1px solid #99ff66;
                    }

                    .green_border_noTop {
                        border: 1px solid #99ff66;
                        border-top: none;
                    }

                    .green_3_border {
                        border: 1px solid #29cc29;
                    }

                    .green_3_border_noTop {
                        border: 1px solid #29cc29;
                        border-top: none;
                    }

                    .red_border {
                        border: 1px solid #ff3333;
                    }

                    .purpur_border {
                        border: 1px solid #9900ff;
                    }
                    
                    .onlyBottomBorder {
                        border-bottom: 1px solid black;
                        border-left: none;
                        border-right: none;
                        border-top: none;
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

                    .flexCenter {
                        justify-content: center;
                    }

                    .flexEnd {
                        justify-content: flex-end;
                    }

                    .alignItemsCenter {
                        align-items: center;
                    }

                    .alignSelfCenter {
                        align-self: center;
                    }

                    .alignSelfEnd {
                        align-self: flex-end;
                    }

                    /****** Grid ******/
                    .gridContainer_2_column_miniright {
                        display: grid;
                        grid-template-columns: 16.60cm 0.25cm;
                        grid-column-gap: 0.15cm;
                    }

                    .gridContainer_2_column_smallright {
                        display: grid;
                        grid-template-columns: 12cm 4cm;
                        grid-column-gap: 1cm;
                    }

                    .gridContainer_3_column_header {
                        display: grid;
                        grid-template-columns: 7.5cm 3.85cm 6.25cm;
                        grid-column-gap: 0.3cm;
                        min-height: 3cm;
                    }

                    .gridContainer_2_column_header_first {
                        display: grid;
                        grid-template-columns: 10.5cm 5.25cm;
                        grid-column-gap: 1.25cm;
                        min-height: 1.5cm;
                    }

                    .gridContainer_2_column_header_second {
                        display: inline-grid;
                        grid-template-columns: 10.5cm 5.25cm;
                        grid-column-gap: 1.25cm;
                        min-height: 1.5cm;
                    }

                    /****** Text Alignment ******/
                    .textAlignCenter {
                        text-align: center;
                    }
                    .textAlignRight {
                        text-align: right;
                    }

                    /***** Font-Weight *****/
                    .bold {
                        font-weight: bold;
                    }

                    /****** Text-Decoration ******/
                    .underline {
                        text-decoration: underline;
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

                    .fontSize_12 {
                        font-size: 12pt;
                    }

                    .fontSize_13 {
                        font-size: 13pt;
                    }

                    .fontSize_14 {
                        font-size: 14pt;
                    }

                    /****** BackgroundColor ******/
                    .blueGrayBG {
                        background-color: #e6e6ff;
                    }

                    /****** Width ******/
                    .width_100 {
                        width: 100%;
                    }

                    .widthNumber {
                        width: 1.25cm;
                        min-width: 1.25cm;
                    }

                    .widthCalc {
                        width: calc(100% - 0.8cm);
                    }

                    /****** Padding ******/
                    .noPadding {
                        padding: 0;
                    }

                    .noPaddingTopBottom {
                        padding-top: 0;
                        padding-bottom: 0;
                    }

                    .paddingLeft {
                        padding-left: 0.2cm;
                    }

                    .paddingLeftCheckbox {
                        padding-left: 0.5cm;
                    }

                    .paddingTable {
                        padding: 6px 2px;
                    }

                    .paddingLeftRight {
                        padding: 0 0.2cm;
                    }

                    .paddingLeft_1 {
                        padding: 0.2cm 0 0.2cm 0.1cm;
                    }

                    .paddingTopBottom {
                        padding: 0.2cm 0;
                    }

                    .padding {
                        padding: 0.1cm;
                    }
                    
                    .side_padding {
                        padding: 0cm 0.1cm;
                    }

                    .paddingHeader {
                        padding: 0.1cm 0.2cm;
                    }

                    .paddingTopNewPage {
                        padding-top: 0.2cm;
                    }

                    /****** Margin ******/
                    .marginLeft {
                        margin-left: 0.2cm;
                    }

                    .marginLeft_10 {
                        margin-left: 1cm;
                    }

                    .marginTopBottom_1 {
                        margin-top: 0.1cm;
                        margin-bottom: 0.1cm;
                    }

                    .marginTopBottom_2 {
                        margin-top: 0.2cm;
                        margin-bottom: 0.2cm;
                    }

                    .marginTopBottom_3 {
                        margin-top: 0.3cm;
                        margin-bottom: 0.3cm;
                    }

                    .marginTop_1 {
                        margin-top: 0.1cm;
                    }

                    .marginTop_2 {
                        margin-top: 0.2cm;
                    }

                    .marginTop_5 {
                        margin-top: 0.5cm;
                    }

                    .marginBottom_1 {
                        margin-bottom: 0.1cm;
                    }

                    .marginBottom_2 {
                        margin-bottom: 0.2cm;
                    }

                    .marginBottom_5 {
                        margin-bottom: 0.6cm;
                    }

                    .marginBottom_6 {
                        margin-bottom: 0.6cm;
                    }

                    .marginCheckbox {
                        margin-top: -0.05cm;
                    }

                    .negMarginTop {
                        margin-top: -0.2cm;
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

                    .backendDataColor {
                        font-weight: bold;
                        color: #546e7a;
                    }

                    .whiteBG {
                        background-color: #ffffff !important;
                    }

                    .yellowBG {
                        background-color: #ffff99;
                        
                    }

                    .greenBG {
                        background-color: #99ff66;
                    }

                    .green_3_BG {
                        background-color: #29cc29;
                    }

                    .redBG {
                        background-color: #ff3333;
                    }

                    .purpurBG {
                        background-color: #9900ff;
                    }

                    .greentext {
                        color: #99ff66;
                    }

                    .green_3_text {
                        color: #29cc29;
                    }

                    .redtext {
                        color: #ff0000;
                    }

                    .lilatext {
                        color: #a37acc;
                    }

                    /****** Page Break ******/
                    .page_break {
                        page-break-before: always;
                    }

                </style>
    `;
  }

  return {
    buildNiederschriftTemplateFromData,
  };
}
