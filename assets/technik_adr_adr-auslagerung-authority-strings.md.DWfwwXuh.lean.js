import{_ as i,c as t,j as s,a,G as r,a0 as h,B as l,o as d}from"./chunks/framework.CpUGaJbD.js";const b=JSON.parse('{"title":"Auslagerung von Authority Strings","description":"","frontmatter":{},"headers":[],"relativePath":"technik/adr/adr-auslagerung-authority-strings.md","filePath":"technik/adr/adr-auslagerung-authority-strings.md"}'),o={name:"technik/adr/adr-auslagerung-authority-strings.md"};function u(p,e,g,k,c,E){const n=l("adr-status");return d(),t("div",null,[e[0]||(e[0]=s("h1",{id:"auslagerung-von-authority-strings",tabindex:"-1"},[a("Auslagerung von Authority Strings "),s("a",{class:"header-anchor",href:"#auslagerung-von-authority-strings","aria-label":'Permalink to "Auslagerung von Authority Strings"'},"​")],-1)),e[1]||(e[1]=s("h2",{id:"status",tabindex:"-1"},[a("Status "),s("a",{class:"header-anchor",href:"#status","aria-label":'Permalink to "Status"'},"​")],-1)),r(n,{status:"rejected"}),e[2]||(e[2]=h(`<h2 id="kontext" tabindex="-1">Kontext <a class="header-anchor" href="#kontext" aria-label="Permalink to &quot;Kontext&quot;">​</a></h2><p>Es stand die Überlegung im Raum, die in den Services verwendeten Authority Strings als Konstanten auszulagern. Beispiel:</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">// Code bisher:</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">@</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">PreAuthorize</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;hasAuthority(&#39;VorfaelleUndVorkommnisse_WRITE_Ereignisse&#39;)&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">// Vorschlag:</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">String AUTHORITY_WRITE_EREIGNIS </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;"> &quot;hasAuthority(&#39;VorfaelleUndVorkommnisse_WRITE_Ereignisse&#39;)&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">;</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">@</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">PreAuthorize</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(AUTHORITY_WRITE_EREIGNIS)</span></span></code></pre></div><p>Dies hätte die Vorteile der Wiederverwendbarkeit, besseren Lesbarkeit und ggf. der Reduzierung von Errors durch Tippfehler, würde aber auf der Anderen Seite die Komplexität etwas erhöhen und zu ein paar mehr Codezeilen führen.</p><h2 id="entscheidung" tabindex="-1">Entscheidung <a class="header-anchor" href="#entscheidung" aria-label="Permalink to &quot;Entscheidung&quot;">​</a></h2><p>In einer gemeinsamen Diskussion wurde Entschieden, die Auslagerung nicht vorzunehmen. Sie hat keinen signifikanten Vorteil, da die meisten Strings nur einmal verwendet werden. Der Aufwand, den bestehenden Code anzupassen kann nicht mit dem geringen Nutzen der Änderung gerechtfertigt werden.</p><h2 id="konsequenzen" tabindex="-1">Konsequenzen <a class="header-anchor" href="#konsequenzen" aria-label="Permalink to &quot;Konsequenzen&quot;">​</a></h2><h3 id="positiv" tabindex="-1">positiv <a class="header-anchor" href="#positiv" aria-label="Permalink to &quot;positiv&quot;">​</a></h3><p>Es müssen durch den Entschluss keine bestehenden Services angepasst werden. Es entsteht kein zusätzlicher Handlungsbedarf.</p><h3 id="negativ" tabindex="-1">negativ <a class="header-anchor" href="#negativ" aria-label="Permalink to &quot;negativ&quot;">​</a></h3><p>Möglicherweise ist der Code weniger übersichtlich und es ergeben sich teilweise redundante/doppelte Codezeilen mit gleichem Inhalt.</p>`,11))])}const v=i(o,[["render",u]]);export{b as __pageData,v as default};
