import{f as n,c as a,o as e,aq as p}from"./chunks/framework.D9EfIZ-r.js";const b=JSON.parse('{"title":"Aktualisieren der embedded-LDAP User","description":"","frontmatter":{},"headers":[],"relativePath":"technik/guides/update-ldif-file-data.md","filePath":"technik/guides/update-ldif-file-data.md"}'),l={name:"technik/guides/update-ldif-file-data.md"};function i(r,s,c,t,o,d){return e(),a("div",null,[...s[0]||(s[0]=[p(`<h1 id="aktualisieren-der-embedded-ldap-user" tabindex="-1">Aktualisieren der embedded-LDAP User <a class="header-anchor" href="#aktualisieren-der-embedded-ldap-user" aria-label="Permalink to &quot;Aktualisieren der embedded-LDAP User&quot;">​</a></h1><h2 id="kontext" tabindex="-1">Kontext <a class="header-anchor" href="#kontext" aria-label="Permalink to &quot;Kontext&quot;">​</a></h2><p>Der <a href="/Wahllokalsystem/services/backend-services/auth-service/">Auth-Service</a> verwendet einen embedded LDAP-Server, wenn keine LDAP-Verbindung konfiguriert ist. Die Daten dieses Servers werden über ein <a href="https://de.wikipedia.org/wiki/LDAP_Data_Interchange_Format" target="_blank" rel="noreferrer">LDIF</a>-File definiert. Wenn bei der Eingabe von Wahltermindaten neue Benutzer erstellt werden, müssen diese im LDIF-File ergänzt werden.</p><h2 id="kurzbeschreibung-des-vorgehens" tabindex="-1">Kurzbeschreibung des Vorgehens <a class="header-anchor" href="#kurzbeschreibung-des-vorgehens" aria-label="Permalink to &quot;Kurzbeschreibung des Vorgehens&quot;">​</a></h2><p>Über das Skript <code>generateLdif.sh</code> im Ordner <code>stack/ldap</code> des Repositories kann eine LDIF-Datei erstellt werden. Als Input benötigt das Skript eine Datei, die zeilenweise die Benutzer enthält. Nach dem Ausführen des Skriptes sind die neuen Benutzer und die Zuordnung der Nutzer zu einer Gruppe aus der erzeugten Datei zu kopieren und in das bestehende Skript zu integrieren. Danach kann man sich zusätzlich mit den generierten Usern einloggen.</p><h2 id="beschreibung-im-detail" tabindex="-1">Beschreibung im Detail <a class="header-anchor" href="#beschreibung-im-detail" aria-label="Permalink to &quot;Beschreibung im Detail&quot;">​</a></h2><h3 id="ausgangslage" tabindex="-1">Ausgangslage <a class="header-anchor" href="#ausgangslage" aria-label="Permalink to &quot;Ausgangslage&quot;">​</a></h3><p>Gehen wir davon aus, es gibt folgendes LDIF-File:</p><div class="language-text vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">text</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span>dn: ou=groups,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: organizationalUnit</span></span>
<span class="line"><span>ou: groups</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: organizationalUnit</span></span>
<span class="line"><span>ou: people</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: uid=wls_all_uwb,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: person</span></span>
<span class="line"><span>objectclass: organizationalPerson</span></span>
<span class="line"><span>objectclass: inetOrgPerson</span></span>
<span class="line"><span>cn: wls_all_uwb</span></span>
<span class="line"><span>sn: uwb</span></span>
<span class="line"><span>uid: wls_all_uwb</span></span>
<span class="line"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: uid=wls_all_bwb,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: person</span></span>
<span class="line"><span>objectclass: organizationalPerson</span></span>
<span class="line"><span>objectclass: inetOrgPerson</span></span>
<span class="line"><span>cn: wls_all_bwb</span></span>
<span class="line"><span>sn: bwb</span></span>
<span class="line"><span>uid: wls_all_bwb</span></span>
<span class="line"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: cn=user,ou=groups,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: groupOfNames</span></span>
<span class="line"><span>cn: user</span></span>
<span class="line"><span>member: uid=wls_all_uwb,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>member: uid=wls_all_bwb,ou=people,dc=springframework,dc=org</span></span></code></pre></div><p>Über dieses File werden zwei User (<code>wls_all_uwb</code> und <code>wls_all_bwb</code>) definiert. Nur mit diesen beiden Usern ist ein Login möglich.</p><h3 id="generierung-eines-neuen-wahltermins" tabindex="-1">Generierung eines neuen Wahltermins <a class="header-anchor" href="#generierung-eines-neuen-wahltermins" aria-label="Permalink to &quot;Generierung eines neuen Wahltermins&quot;">​</a></h3><p>Wenn ein neuer Wahltermin durch das Wahllokalsystem unterstützt werden soll, muss dieser Wahltermin initialisiert werden. Das erfolgt über die Admin-GUI. Im Rahmen des Initialisierungsprozesses werden auch Benutzer generiert. Für das Beispiel gehen wir davon aus, dass folgende Benutzer generiert wurden:</p><div class="language-text vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">text</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span>fzh56-wahlbezirk0001</span></span>
<span class="line"><span>ujt9a-wahlbezirk0002</span></span>
<span class="line"><span>78nmr-wahlbezirk0003</span></span></code></pre></div><h3 id="erzeugung-des-neuen-ldif-files" tabindex="-1">Erzeugung des neuen LDIF-Files <a class="header-anchor" href="#erzeugung-des-neuen-ldif-files" aria-label="Permalink to &quot;Erzeugung des neuen LDIF-Files&quot;">​</a></h3><p>Die Benutzer sollten in einer Datei vorliegen. Im Beispiel gehen wir davon aus, dass die Datei den Namen <code>exportusers.csv</code> hat und im selben Ordner wie das Skript liegt.</p><div class="language-bash vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">bash</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">./generateLdif.sh</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;"> exportusers.csv</span></span></code></pre></div><p><em>Befehl zum Ausführen des Bash-Skriptes zur Erzeugung des LDIF-Files</em></p><p>Nach erfolgreicher Beendigung des Skripts liegt die Datei <code>output.ldif</code> mit dem generierten LDIF-File vor.</p><h3 id="zusammenfuhren-der-ldif" tabindex="-1">Zusammenführen der LDIF <a class="header-anchor" href="#zusammenfuhren-der-ldif" aria-label="Permalink to &quot;Zusammenführen der LDIF&quot;">​</a></h3><div class="language-text vp-adaptive-theme line-numbers-mode"><button title="Copy Code" class="copy"></button><span class="lang">text</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span>dn: ou=groups,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: organizationalUnit</span></span>
<span class="line"><span>ou: groups</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: organizationalUnit</span></span>
<span class="line"><span>ou: people</span></span>
<span class="line"><span></span></span>
<span class="line highlighted"><span>dn: uid=fzh56-wahlbezirk0001,ou=people,dc=springframework,dc=org</span></span>
<span class="line highlighted"><span>objectclass: top</span></span>
<span class="line highlighted"><span>objectclass: person</span></span>
<span class="line highlighted"><span>objectclass: organizationalPerson</span></span>
<span class="line highlighted"><span>objectclass: inetOrgPerson</span></span>
<span class="line highlighted"><span>cn: fzh56-wahlbezirk0001</span></span>
<span class="line highlighted"><span>sn: fzh56-wahlbezirk0001</span></span>
<span class="line highlighted"><span>uid: fzh56-wahlbezirk0001</span></span>
<span class="line highlighted"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line highlighted"><span>dn: uid=ujt9a-wahlbezirk0002,ou=people,dc=springframework,dc=org</span></span>
<span class="line highlighted"><span>objectclass: top</span></span>
<span class="line highlighted"><span>objectclass: person</span></span>
<span class="line highlighted"><span>objectclass: organizationalPerson</span></span>
<span class="line highlighted"><span>objectclass: inetOrgPerson</span></span>
<span class="line highlighted"><span>cn: ujt9a-wahlbezirk0002</span></span>
<span class="line highlighted"><span>sn: ujt9a-wahlbezirk0002</span></span>
<span class="line highlighted"><span>uid: ujt9a-wahlbezirk0002</span></span>
<span class="line highlighted"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line highlighted"><span>dn: uid=78nmr-wahlbezirk0003,ou=people,dc=springframework,dc=org</span></span>
<span class="line highlighted"><span>objectclass: top</span></span>
<span class="line highlighted"><span>objectclass: person</span></span>
<span class="line highlighted"><span>objectclass: organizationalPerson</span></span>
<span class="line highlighted"><span>objectclass: inetOrgPerson</span></span>
<span class="line highlighted"><span>cn: 78nmr-wahlbezirk0003</span></span>
<span class="line highlighted"><span>sn: 78nmr-wahlbezirk0003</span></span>
<span class="line highlighted"><span>uid: 78nmr-wahlbezirk0003</span></span>
<span class="line highlighted"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: cn=user,ou=groups,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: groupOfNames</span></span>
<span class="line"><span>cn: user</span></span>
<span class="line highlighted"><span>member: uid=fzh56-wahlbezirk0001,ou=people,dc=springframework,dc=org</span></span>
<span class="line highlighted"><span>member: uid=ujt9a-wahlbezirk0002,ou=people,dc=springframework,dc=org</span></span>
<span class="line highlighted"><span>member: uid=78nmr-wahlbezirk0003,ou=people,dc=springframework,dc=org</span></span></code></pre><div class="line-numbers-wrapper" aria-hidden="true"><span class="line-number">1</span><br><span class="line-number">2</span><br><span class="line-number">3</span><br><span class="line-number">4</span><br><span class="line-number">5</span><br><span class="line-number">6</span><br><span class="line-number">7</span><br><span class="line-number">8</span><br><span class="line-number">9</span><br><span class="line-number">10</span><br><span class="line-number">11</span><br><span class="line-number">12</span><br><span class="line-number">13</span><br><span class="line-number">14</span><br><span class="line-number">15</span><br><span class="line-number">16</span><br><span class="line-number">17</span><br><span class="line-number">18</span><br><span class="line-number">19</span><br><span class="line-number">20</span><br><span class="line-number">21</span><br><span class="line-number">22</span><br><span class="line-number">23</span><br><span class="line-number">24</span><br><span class="line-number">25</span><br><span class="line-number">26</span><br><span class="line-number">27</span><br><span class="line-number">28</span><br><span class="line-number">29</span><br><span class="line-number">30</span><br><span class="line-number">31</span><br><span class="line-number">32</span><br><span class="line-number">33</span><br><span class="line-number">34</span><br><span class="line-number">35</span><br><span class="line-number">36</span><br><span class="line-number">37</span><br><span class="line-number">38</span><br><span class="line-number">39</span><br><span class="line-number">40</span><br><span class="line-number">41</span><br><span class="line-number">42</span><br><span class="line-number">43</span><br><span class="line-number">44</span><br><span class="line-number">45</span><br><span class="line-number">46</span><br><span class="line-number">47</span><br></div></div><p>Aus dem erzeugten File müssen die erzeugten User und die Zuordnung zur Gruppe (siehe den hervorgehobenen Bereich) in das bestehende LDIF-File übertragen werden.</p><p>Nach einem Neustart des Auth-Service kann zusätzlich ein Login mit den neuen Usern erfolgen.</p><h3 id="das-finale-ldif-file" tabindex="-1">Das finale LDIF-File <a class="header-anchor" href="#das-finale-ldif-file" aria-label="Permalink to &quot;Das finale LDIF-File&quot;">​</a></h3><div class="language-text vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">text</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span>dn: ou=groups,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: organizationalUnit</span></span>
<span class="line"><span>ou: groups</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: organizationalUnit</span></span>
<span class="line"><span>ou: people</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: uid=wls_all_uwb,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: person</span></span>
<span class="line"><span>objectclass: organizationalPerson</span></span>
<span class="line"><span>objectclass: inetOrgPerson</span></span>
<span class="line"><span>cn: wls_all_uwb</span></span>
<span class="line"><span>sn: uwb</span></span>
<span class="line"><span>uid: wls_all_uwb</span></span>
<span class="line"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: uid=wls_all_bwb,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: person</span></span>
<span class="line"><span>objectclass: organizationalPerson</span></span>
<span class="line"><span>objectclass: inetOrgPerson</span></span>
<span class="line"><span>cn: wls_all_bwb</span></span>
<span class="line"><span>sn: bwb</span></span>
<span class="line"><span>uid: wls_all_bwb</span></span>
<span class="line"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: uid=fzh56-wahlbezirk0001,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: person</span></span>
<span class="line"><span>objectclass: organizationalPerson</span></span>
<span class="line"><span>objectclass: inetOrgPerson</span></span>
<span class="line"><span>cn: fzh56-wahlbezirk0001</span></span>
<span class="line"><span>sn: fzh56-wahlbezirk0001</span></span>
<span class="line"><span>uid: fzh56-wahlbezirk0001</span></span>
<span class="line"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: uid=ujt9a-wahlbezirk0002,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: person</span></span>
<span class="line"><span>objectclass: organizationalPerson</span></span>
<span class="line"><span>objectclass: inetOrgPerson</span></span>
<span class="line"><span>cn: ujt9a-wahlbezirk0002</span></span>
<span class="line"><span>sn: ujt9a-wahlbezirk0002</span></span>
<span class="line"><span>uid: ujt9a-wahlbezirk0002</span></span>
<span class="line"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: uid=78nmr-wahlbezirk0003,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: person</span></span>
<span class="line"><span>objectclass: organizationalPerson</span></span>
<span class="line"><span>objectclass: inetOrgPerson</span></span>
<span class="line"><span>cn: 78nmr-wahlbezirk0003</span></span>
<span class="line"><span>sn: 78nmr-wahlbezirk0003</span></span>
<span class="line"><span>uid: 78nmr-wahlbezirk0003</span></span>
<span class="line"><span>userPassword: test</span></span>
<span class="line"><span></span></span>
<span class="line"><span>dn: cn=user,ou=groups,dc=springframework,dc=org</span></span>
<span class="line"><span>objectclass: top</span></span>
<span class="line"><span>objectclass: groupOfNames</span></span>
<span class="line"><span>cn: user</span></span>
<span class="line"><span>member: uid=wls_all_uwb,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>member: uid=wls_all_bwb,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>member: uid=fzh56-wahlbezirk0001,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>member: uid=ujt9a-wahlbezirk0002,ou=people,dc=springframework,dc=org</span></span>
<span class="line"><span>member: uid=78nmr-wahlbezirk0003,ou=people,dc=springframework,dc=org</span></span></code></pre></div>`,24)])])}const h=n(l,[["render",i]]);export{b as __pageData,h as default};
