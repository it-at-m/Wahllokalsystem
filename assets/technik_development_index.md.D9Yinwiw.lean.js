import{_ as l,D as i,c as o,b as s,w as t,a3 as d,j as e,a,a2 as c,o as n,I as m}from"./chunks/framework.CNEjLQJj.js";const h="/Wahllokalsystem/keycloak/deleteRealmAction.png",u="/Wahllokalsystem/keycloak/createRealmTrigger.png",k="/Wahllokalsystem/keycloak/exampleOfKeycloakmigrationRun.png",C=JSON.parse('{"title":"Entwicklungsumgebung","description":"","frontmatter":{},"headers":[],"relativePath":"technik/development/index.md","filePath":"technik/development/index.md"}'),g={name:"technik/development/index.md"},p=e("h1",{id:"entwicklungsumgebung",tabindex:"-1"},[a("Entwicklungsumgebung "),e("a",{class:"header-anchor",href:"#entwicklungsumgebung","aria-label":'Permalink to "Entwicklungsumgebung"'},"​")],-1),b=e("h2",{id:"zusammenspiel-ide-mit-docker",tabindex:"-1"},[a("Zusammenspiel IDE mit Docker "),e("a",{class:"header-anchor",href:"#zusammenspiel-ide-mit-docker","aria-label":'Permalink to "Zusammenspiel IDE mit Docker"'},"​")],-1),w=c("",28);function f(_,A,D,R,y,B){const r=i("Mermaid");return n(),o("div",null,[p,b,(n(),s(d,null,{default:t(()=>[m(r,{id:"mermaid-6",class:"mermaid my-class",graph:"flowchart%20LR%0A%20%20%20%20%0A%20%20%20%20subgraph%20Dev-PC%20%0A%20%20%20%20%20%20%20%20subgraph%20IDE%20%0A%20%20%20%20%20%20%20%20%20%20%20%20wlsService%0A%20%20%20%20%20%20%20%20end%0A%20%20%20%20%20%20%20%20%0A%20%20%20%20%20%20%20%20subgraph%20Docker%20%0A%20%20%20%20%20%20%20%20%20%20%20%20keycloak%5BKeycloak%5D%0A%20%20%20%20%20%20%20%20%20%20%20%20keycloakDB%5Bdb-postgres-keycloak%5D%0A%20%20%20%20%20%20%20%20%20%20%20%20keycloakInit%5Binit-keycloak%5D%0A%20%20%20%20%20%20%20%20%20%20%20%20oracleDB%5BOracle%20DB%5D%0A%20%20%20%20%20%20%20%20end%0A%0A%20%20%20%20%20%20%20%20wlsService%20---%7COAuth2%7C%20keycloak%0A%20%20%20%20%20%20%20%20wlsService%20---%3E%7Cpersisting%7CoracleDB%0A%0A%20%20%20%20%20%20%20%20keycloak--%3E%7Cpersisting%7C%20keycloakDB%0A%20%20%20%20%20%20%20%20keycloakInit--%3E%7Csetup%20of%7C%20keycloak%0A%20%20%20%20end%0A"})]),fallback:t(()=>[a(" Loading... ")]),_:1})),w])}const q=l(g,[["render",f]]);export{C as __pageData,q as default};
