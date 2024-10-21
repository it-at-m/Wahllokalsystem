import{_ as a,c as n,a2 as t,o as i}from"./chunks/framework.rHWNTPhC.js";const r="/Wahllokalsystem/fachlichesDatenmodell/vermutetesFachlichesDatenmodell_20241008.png",l="/Wahllokalsystem/fachlichesDatenmodell/Wahlkreiskarte_BTW2025.png",s="/Wahllokalsystem/fachlichesDatenmodell/Stimmkreiskarte_LTW_V1.jpg",b=JSON.parse('{"title":"Features","description":"","frontmatter":{},"headers":[],"relativePath":"features/index.md","filePath":"features/index.md"}'),h={name:"features/index.md"};function d(m,e,u,o,c,g){return i(),n("div",null,e[0]||(e[0]=[t('<h1 id="features" tabindex="-1">Features <a class="header-anchor" href="#features" aria-label="Permalink to &quot;Features&quot;">​</a></h1><p>🚧 in Progress</p><p>Für jeden Service gibt es eine separate Beschreibung zu dessen Aufgaben und Funktionen.</p><h2 id="fachliches-datenmodell" tabindex="-1">Fachliches Datenmodell <a class="header-anchor" href="#fachliches-datenmodell" aria-label="Permalink to &quot;Fachliches Datenmodell&quot;">​</a></h2><p>Das folgende Datenmodell wurde mit Betrachtung der gesetzlichen Grundlage verschiedener Wahlen, sowie des örtlichen Kommunal- und/oder Landes- Wahlrechts erstellt.</p><p><img src="'+r+'" alt="Datenmodell:"></p><p><code>Wahltermin</code> - ein Datum an dem eine Wahl oder auch mehrere Wahlen oder Abstimmungen im gleichen Wahllokal starten. Diese enden in der Regel auch am gleichen Tag, es gibt aber auch Wahltermine, die meherere Tage andauern, wie es bei der Kommunalwahl der Fall ist. In der Praxis, auch wenn dies seltener der Fall ist, können am gleichen Tag sogar mehrere Wahltermine stattfinden. Diese Notwendigkeit entsteht, wenn sich die berechtigten Wählergruppen am gleichen Tag in den rechtlichen Grundlagen ihrer Wahlberechtigung stark unterscheiden, um fehlerhaftes Aufteilen, Ausfüllen und Vermischung der Stimmzettel unterschidlicher Wählergruppen zu vermeiden. Zum Beispiel wird eine Migrationsbeiratswahl, bei der Bürger mit unterschiedlichen Staatsangehörigkeiten wahlberechtigt sind, im Unterschied zu einer Bundestagswahl in separaten Wahlbezirken organisiert.</p><p>Wenn aber am gleichen Datum eine Bundestagswahl und ein Bürgerentscheid stattfinden, können diese dem gleichen Wahltermin zugeteilt und somit in gemeinsamen Wahllokalen organisiert werden.</p><p><code>Stimmzettelgebiet</code> - siehe hierzu und vergleiche die unten aufgefürten Bedeutungen in verschiedenen Wahlarten;</p><p><code>Vorschlag</code> - entspricht einer Partei;</p><p><code>Vorlage</code> - entspricht einer Frage / einem Thema eines Volks- oder Bürgerentscheides;</p><p><code>Wahlbezirk</code> - auch als Stimmbezirk bezeichnet, entspricht einem Wahllokal (Wahlraum), geführt von einem Wahlvorstand.</p><p><a href="https://stadt.muenchen.de/rathaus/politik/wahlen.html" target="_blank" rel="noreferrer">Quellen und mehr Informationen</a></p><h3 id="erlauterung-zu-einzelnen-wahlarten" tabindex="-1">Erläuterung zu einzelnen Wahlarten <a class="header-anchor" href="#erlauterung-zu-einzelnen-wahlarten" aria-label="Permalink to &quot;Erläuterung zu einzelnen Wahlarten&quot;">​</a></h3><h4 id="bundestagswahl" tabindex="-1">Bundestagswahl <a class="header-anchor" href="#bundestagswahl" aria-label="Permalink to &quot;Bundestagswahl&quot;">​</a></h4><p><code>Stimmzettelgebiet</code> - das im Model als solches bezeichnete Gebiet ist im Bundeswahlrecht der &quot;Wahlkreis&quot;. Die Landeshauptstadt München hat 4 Wahlkreise: 217 - München Nord, 218 - München Ost, 219 - München Süd und 220 - München-West/Mitte. In jedem Wahlkreis gibt es dann mehrere Wahlbezirke (Wahllokale), diese haben in einem Wahlkreis den gleichen Stimmzettel. Jedes Wahllokal befindet sich geografisch auch in einem der 25 Stadtbezirke der Landeshauptstadt München. Der Inhalt eines Stimmzettels bestimmt sich aus der Zugehörigkeit zum Wahlkreis und nicht zum Stadtbezirk.</p><h5 id="beispiel-bundestagswahl" tabindex="-1">Beispiel Bundestagswahl <a class="header-anchor" href="#beispiel-bundestagswahl" aria-label="Permalink to &quot;Beispiel Bundestagswahl&quot;">​</a></h5><p><img src="'+l+'" alt="Wahlkreise in der Landeshauptstadt München"></p><p><a href="https://stadt.muenchen.de/infos/bundestagswahlen.html" target="_blank" rel="noreferrer">Mehr Informationen zur Bundestagswahl</a></p><h4 id="landtagswahl" tabindex="-1">Landtagswahl <a class="header-anchor" href="#landtagswahl" aria-label="Permalink to &quot;Landtagswahl&quot;">​</a></h4><p><code>Stimmzettelgebiet</code> - entspricht einem Stimmkreis. Bayern ist in Regierungsbezirke (Wahlkreise), in Landkreise und kreisfreie Städte bzw. in Wahlkreise und Stimmkreise (je max. 125.000 Einwohner) eingeteilt. Landeshauptstadt München liegt im Wahlkreis Oberbayern und umfasst 9 (Stand 2024) Stimmkreise von 101 bis 109. In jedem Stimmkreis gibt es dann mehrere Wahlbezirke (Wahllokale), diese haben in einem Stimmkreis den gleichen Stimmzettel. Jedes Wahllokal befindet sich geografisch auch in einem der 25 Stadtbezirke der Landeshauptstadt München. Der Inhalt eines Stimmzettels bestimmt aber seine Zugehörigkeit zum Stimmkreis und nicht zum Stadtbezirk.</p><h5 id="beispiel-landtagswahl" tabindex="-1">Beispiel Landtagswahl <a class="header-anchor" href="#beispiel-landtagswahl" aria-label="Permalink to &quot;Beispiel Landtagswahl&quot;">​</a></h5><p><img src="'+s+'" alt="Stimmkreiskarte in der Landeshauptstadt München"></p><p><a href="https://stadt.muenchen.de/infos/landtagswahlen-und-bezirkswahlen-teil-ii.html" target="_blank" rel="noreferrer">Mehr Informationen zur Landtagswahl.</a></p><h4 id="europawahl" tabindex="-1">Europawahl <a class="header-anchor" href="#europawahl" aria-label="Permalink to &quot;Europawahl&quot;">​</a></h4><p><code>Stimmzettelgebiet</code> - ist die Bundesrepublik Deutschland. Für das ganze Gebiet der Stadt München mit ihren 25 Stadtbezirken, jeweils mehrere Wahlbezirke/Stimmbezirke umfassend, gibt es also ein einziges Stimmzettelgebiet. Jedes Wahllokal befindet sich geografisch auch in einem der 25 Stadtbezirke der Landeshauptstadt München. Der Inhalt eines Stimmzettels ist in allen Wahllokalen der Landeshauptstadt München identsich.</p><p><a href="https://stadt.muenchen.de/infos/europawahlen.html" target="_blank" rel="noreferrer">Mehr Informationen zur Europawahl</a></p>',27)]))}const k=a(h,[["render",d]]);export{b as __pageData,k as default};
