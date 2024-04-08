import{_ as r,E as i,c as o,b as d,w as a,a3 as c,m as e,a as t,a2 as l,o as n,J as u}from"./chunks/framework.B1QLjYJX.js";const D=JSON.parse('{"title":"Entwicklungsumgebung","description":"","frontmatter":{},"headers":[],"relativePath":"technik/development/index.md","filePath":"technik/development/index.md"}'),h={name:"technik/development/index.md"},k=e("h1",{id:"entwicklungsumgebung",tabindex:"-1"},[t("Entwicklungsumgebung "),e("a",{class:"header-anchor",href:"#entwicklungsumgebung","aria-label":'Permalink to "Entwicklungsumgebung"'},"​")],-1),m=e("h2",{id:"zusammenspiel-ide-mit-docker",tabindex:"-1"},[t("Zusammenspiel IDE mit Docker "),e("a",{class:"header-anchor",href:"#zusammenspiel-ide-mit-docker","aria-label":'Permalink to "Zusammenspiel IDE mit Docker"'},"​")],-1),p=l('<h2 id="benutzer" tabindex="-1">Benutzer <a class="header-anchor" href="#benutzer" aria-label="Permalink to &quot;Benutzer&quot;">​</a></h2><table><thead><tr><th>Name</th><th>Passwort</th><th>Beschreibung</th></tr></thead><tbody><tr><td>keycloak_test</td><td>test</td><td>Ein Benutzer ohne weitere Rechte</td></tr><tr><td>wls_all</td><td>test</td><td>Ein Benutzer mit allen Rechten</td></tr></tbody></table><h2 id="beispiel-requests" tabindex="-1">Beispiel-Requests <a class="header-anchor" href="#beispiel-requests" aria-label="Permalink to &quot;Beispiel-Requests&quot;">​</a></h2><p>Im Soap-UI-Projekt (<code>DockerTest-soapui-project</code>) und <code>docker.keycloak.http</code> sind Beispielrequests vorhanden. Es kann für den jeweiligen Nutzer ein Token geholt werden. Außerdem ist die Anfrage an den UserInfo-Endpoint hinterlegt.</p>',4);function _(b,g,A,f,B,y){const s=i("Mermaid");return n(),o("div",null,[k,m,(n(),d(c,null,{default:a(()=>[u(s,{id:"mermaid-6",class:"mermaid my-class",graph:"flowchart%20LR%0A%20%20%20%20%0A%20%20%20%20subgraph%20Dev-PC%20%0A%20%20%20%20%20%20%20%20subgraph%20IDE%20%0A%20%20%20%20%20%20%20%20%20%20%20%20wlsService%0A%20%20%20%20%20%20%20%20end%0A%20%20%20%20%20%20%20%20%0A%20%20%20%20%20%20%20%20subgraph%20Docker%20%0A%20%20%20%20%20%20%20%20%20%20%20%20keycloak%5BKeycloak%5D%0A%20%20%20%20%20%20%20%20%20%20%20%20keycloakDB%5Bdb-postgres-keycloak%5D%0A%20%20%20%20%20%20%20%20%20%20%20%20keycloakInit%5Binit-keycloak%5D%0A%20%20%20%20%20%20%20%20end%0A%0A%20%20%20%20%20%20%20%20wlsService%20---%7COAuth2%7C%20keycloak%0A%0A%20%20%20%20%20%20%20%20keycloak--%3E%7Cpersisting%7C%20keycloakDB%0A%20%20%20%20%20%20%20%20keycloakInit--%3E%7Csetup%20of%7C%20keycloak%0A%20%20%20%20end%0A"})]),fallback:a(()=>[t(" Loading... ")]),_:1})),p])}const E=r(h,[["render",_]]);export{D as __pageData,E as default};