import{_ as i,c as a,j as n,a as r,G as s,a0 as d,B as o,o as c}from"./chunks/framework.CpUGaJbD.js";const k=JSON.parse('{"title":"Verbesserung der Einstiegsfreundlichkeit durch neue Default-Werte","description":"","frontmatter":{},"headers":[],"relativePath":"technik/adr/adr-improve-getting-started-of-services.md","filePath":"technik/adr/adr-improve-getting-started-of-services.md"}'),u={name:"technik/adr/adr-improve-getting-started-of-services.md"};function l(h,e,g,f,p,m){const t=o("adr-status");return c(),a("div",null,[e[0]||(e[0]=n("h1",{id:"verbesserung-der-einstiegsfreundlichkeit-durch-neue-default-werte",tabindex:"-1"},[r("Verbesserung der Einstiegsfreundlichkeit durch neue Default-Werte "),n("a",{class:"header-anchor",href:"#verbesserung-der-einstiegsfreundlichkeit-durch-neue-default-werte","aria-label":'Permalink to "Verbesserung der Einstiegsfreundlichkeit durch neue Default-Werte"'},"​")],-1)),e[1]||(e[1]=n("h2",{id:"status",tabindex:"-1"},[r("Status "),n("a",{class:"header-anchor",href:"#status","aria-label":'Permalink to "Status"'},"​")],-1)),s(t,{status:"accepted"}),e[2]||(e[2]=d('<h2 id="kontext" tabindex="-1">Kontext <a class="header-anchor" href="#kontext" aria-label="Permalink to &quot;Kontext&quot;">​</a></h2><p>Wenn wir OpenSource auf Github entwickeln und somit anderen Personen die Möglichkeit geben unsere Anwendung zu verwenden, sollten wir dafür sorgen, dass die Einstiegshürde so gering wie möglich ist.</p><p>Aktuell ist nicht standardisiert welche Datenbank wann verwendet wird. Abhängig von dem beim Start verwendeten Profil kann entweder eine H2- oder Oracle-DB verwendet werden. Während H2 ohne zusätzliche Infrastruktur in Memory läuft benötigt Oracle einen Datenbankserver.</p><p>Des Weiteren ist aktuell das Profil <code>local</code> notwendig um die Services zu starten. Die in den Services hinterlegten Startskripte sind daher wichtig, um die Services zu starten.</p><p>Wenn man mehrere Services lokal starten möchte, muss man eigenständig auf die Ports achten und diese gegenfalls anpassen.</p><h2 id="entscheidung" tabindex="-1">Entscheidung <a class="header-anchor" href="#entscheidung" aria-label="Permalink to &quot;Entscheidung&quot;">​</a></h2><p>Ohne zusätzliche Konfiguration soll beim Start eines Service per Default die H2-InMemory-Datenbank verwendet werden. Oracle kann durch zusätzliche Konfiguration verwendet werden.</p><p>Durch technische Profile kann die Art der Datenbank geändert werden.</p><p>Zum Start muss kein explizites Profil angegeben werden. Ein simple <code>java -jar Microservice.jar</code> soll ausreichend sein.</p><p>Jeder Microservice bekommt einen eigenen Port per Default, beginnend bei <code>39146</code> für den Broadcast-Service und dann weiter mit <code>39147</code> für den Briefwahlservice, <code>39148</code> für den Infomanagement-Service und <code>39149</code> für den EAI-Service. Weitere Services reihen sich entsprechend nachfolgend ein.</p><h2 id="konsequenzen" tabindex="-1">Konsequenzen <a class="header-anchor" href="#konsequenzen" aria-label="Permalink to &quot;Konsequenzen&quot;">​</a></h2><p>Die bereits erstellen Services müssen angepasst werden.</p><h3 id="positiv" tabindex="-1">positiv <a class="header-anchor" href="#positiv" aria-label="Permalink to &quot;positiv&quot;">​</a></h3><p>Leichterer Einstieg für Dritte in die Anwendung weil nun ein Service ohne Anpassung der Konfiguration und zusätzliche Infrastruktur direkt gestartet werden kann .</p><h3 id="negativ" tabindex="-1">negativ <a class="header-anchor" href="#negativ" aria-label="Permalink to &quot;negativ&quot;">​</a></h3><p>Bei der Erstellung eines Services müssen weitere Regeln beachtet werden. Services die miteinander Kommunizieren müssen aufeinander abgestimmt werden (z.B. beim Port).</p>',16))])}const b=i(u,[["render",l]]);export{k as __pageData,b as default};
