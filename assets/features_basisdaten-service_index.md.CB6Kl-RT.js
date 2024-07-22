import{_ as e,c as a,o as n,a2 as t}from"./chunks/framework.BnQJprnk.js";const f=JSON.parse('{"title":"Basisdaten-Service","description":"","frontmatter":{},"headers":[],"relativePath":"features/basisdaten-service/index.md","filePath":"features/basisdaten-service/index.md"}'),i={name:"features/basisdaten-service/index.md"},r=t('<h1 id="basisdaten-service" tabindex="-1">Basisdaten-Service <a class="header-anchor" href="#basisdaten-service" aria-label="Permalink to &quot;Basisdaten-Service&quot;">​</a></h1><p>Service zur Bereitstellung folgender Basisdaten:</p><ul><li>Wahltage</li><li>Wahlen</li><li>Wahlbezirke</li><li>Wahlvorschläge</li><li>Kopfdaten</li><li>Handbuch</li><li>Ungültige Wahlscheine</li></ul><p>Wahlen, Wahlbezirke und Kopfdaten können in der Service-Datenbank gespeichert werden.</p><h2 id="abhangigkeiten" tabindex="-1">Abhängigkeiten <a class="header-anchor" href="#abhangigkeiten" aria-label="Permalink to &quot;Abhängigkeiten&quot;">​</a></h2><p>Folgende Services werden zum Betrieb benötigt:</p><ul><li>EAI-Service</li><li>Infomanagement-Service</li></ul><h2 id="handbuch" tabindex="-1">Handbuch <a class="header-anchor" href="#handbuch" aria-label="Permalink to &quot;Handbuch&quot;">​</a></h2><p>In dem Service werden Handbücher verwaltet. Je Wahl und Wahlbezirkart kann ein Handbuch hinterlegt werden.</p><p>Bei dem Handbuch soll es sich um ein PDF-Dokument handeln.</p><h2 id="ungultige-wahlscheine" tabindex="-1">Ungültige Wahlscheine <a class="header-anchor" href="#ungultige-wahlscheine" aria-label="Permalink to &quot;Ungültige Wahlscheine&quot;">​</a></h2><p>Für Wahltage können Listen mit ungültigen Wahlscheinen verwaltet werden. Die Übermittlung der Daten erfolgt im csv-Format. Dieses umfasst eine Headerzeile, gefolgt von den Daten in den Spalten Name, Vorname und Nummer.</p><h2 id="konfigurationsparameter" tabindex="-1">Konfigurationsparameter <a class="header-anchor" href="#konfigurationsparameter" aria-label="Permalink to &quot;Konfigurationsparameter&quot;">​</a></h2><p>Alle Konfigurationsparameter beginnen mit <code>service.config</code></p><table tabindex="0"><thead><tr><th>Name</th><th>Beschreibung</th><th>Default</th></tr></thead><tbody><tr><td>ungueltigewahlscheine.filenamesuffix</td><td>Dateinamenssuffix für die ungueltigen Wahlscheine</td><td>Ungueltigews.csv</td></tr><tr><td>manual.filenamesuffix</td><td>Dateinamenssuffix für das Handbuch</td><td>Handbuch.pdf</td></tr></tbody></table>',15),l=[r];function s(d,h,c,o,u,g){return n(),a("div",null,l)}const m=e(i,[["render",s]]);export{f as __pageData,m as default};
