import{_ as i,c as e,a0 as n,o as a}from"./chunks/framework.CKPRWGvD.js";const g=JSON.parse('{"title":"Naming Convention für Tests","description":"","frontmatter":{},"headers":[],"relativePath":"technik/coding_conventions/tests_naming.md","filePath":"technik/coding_conventions/tests_naming.md"}'),t={name:"technik/coding_conventions/tests_naming.md"};function h(l,s,p,r,d,k){return a(),e("div",null,s[0]||(s[0]=[n(`<h1 id="naming-convention-fur-tests" tabindex="-1">Naming Convention für Tests <a class="header-anchor" href="#naming-convention-fur-tests" aria-label="Permalink to &quot;Naming Convention für Tests&quot;">​</a></h1><h2 id="kontext" tabindex="-1">Kontext <a class="header-anchor" href="#kontext" aria-label="Permalink to &quot;Kontext&quot;">​</a></h2><p>Aktuell gibt es keine Struktur oder Vorgaben bei der Benennung von Tests. Die meisten Namen sind sehr kurz gehalten und wenig aussagekräftig, wie zum Beispiel:</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">void</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> dataFound</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {}</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">void</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> noDataFound</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {}</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">void</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> serviceCalled</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {}</span></span></code></pre></div><p>Damit der Gesamtcode im Projekt übersichtlicher und einheitlicher ist, sollen Naming Conventions eingesetzt werden. Grundlage für die Einführung sind unter anderem auch sich wiederholende Tests mit gleichem Inhalt in den verschiedenen Services. So wird gewährleistet, dass deren Kontext schneller klar ist, ohne den Code lesen zu müssen und die Wartung und Erweiterung des Codes wird erleichtert.</p><h2 id="entscheidung" tabindex="-1">Entscheidung <a class="header-anchor" href="#entscheidung" aria-label="Permalink to &quot;Entscheidung&quot;">​</a></h2><p>Die Bezeichnungen sollen dem Schema <code>should_&lt;result&gt;_when_&lt;input&gt;</code> folgen, wobei Result (= ExpectedBehavior, bzw. erwartetes Ergebnis) und Input (= StateUnderTest, bzw. zu testender Zustand) in CamelCase gehalten werden. Dem Schema entsprechend sind die Testnamen auch auf englisch zu formulieren.</p><p>Wir haben uns darauf geeinigt, die zu testenden Methoden mit <code>@Nested</code> zu gruppieren. Im Fall von überladenen Methoden werden diese innerhalb der Methodenklasse zusätzlich verschachtelt und ebenfalls mit <code>@Nested</code> annotiert.</p><h3 id="beispiele" tabindex="-1">Beispiele <a class="header-anchor" href="#beispiele" aria-label="Permalink to &quot;Beispiele&quot;">​</a></h3><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">//   should_expectedResultInCamelCase_when_inputInCamelCase() {}</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">void</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> should_returnDTO_when_givenValidId</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {}</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">void</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> should_notThrowException_when_newDataSaved</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {}</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">void</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> should_throwAccessDeniedException_whenAuthoritiesMissing</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {}</span></span></code></pre></div><h5 id="gruppierung-uberladener-methoden" tabindex="-1">Gruppierung überladener Methoden: <a class="header-anchor" href="#gruppierung-uberladener-methoden" aria-label="Permalink to &quot;Gruppierung überladener Methoden:&quot;">​</a></h5><p>Vereinfachter Pseudocode! Beispiel aus dem Vorfälle und Vorkommnisse Service.</p><div class="vp-code-group vp-adaptive-theme"><div class="tabs"><input type="radio" name="group-0vyXk" id="tab-TrVJps2" checked><label for="tab-TrVJps2">MapperTest.java</label><input type="radio" name="group-0vyXk" id="tab-PmfuCML"><label for="tab-PmfuCML">Mapper.java</label></div><div class="blocks"><div class="language-java vp-adaptive-theme active"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">class</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> EreignisModelMapperTest</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> {</span></span>
<span class="line highlighted"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  @</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">Nested</span></span>
<span class="line highlighted"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">  class</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> ToEntity</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> {                      </span><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">// Name der zu testenden Methode</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    </span></span>
<span class="line highlighted"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">     @</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">Nested</span></span>
<span class="line highlighted"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">     class</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> ToEreignisEntity</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> {           </span><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">// nested overload 1</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">         @</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">Test</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">         void</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> should_returnEreignis_when_givenEreignisModel</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {}</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">     }</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  </span></span>
<span class="line highlighted"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">     @</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">Nested</span></span>
<span class="line highlighted"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">     class</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> ToListOfEreignisEntity</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> {     </span><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">// nested overload 2</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">         @</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">Test</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">         void</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> should_returnListOfEreignis_when_givenEreignisseWriteModel</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {}</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">     }</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  }</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">}</span></span></code></pre></div><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">@</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">Mapper</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">public</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> interface</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> EreignisModelMapper</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> {</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  Ereignis </span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">toEntity</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(EreignisModel </span><span style="--shiki-light:#E36209;--shiki-dark:#FFAB70;">model</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);    </span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    </span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  List&lt;</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">Ereignis</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">&gt; </span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">toEntity</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(EreignisseWriteModel </span><span style="--shiki-light:#E36209;--shiki-dark:#FFAB70;">model</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);  </span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">}</span></span></code></pre></div></div></div><h2 id="konsequenzen" tabindex="-1">Konsequenzen <a class="header-anchor" href="#konsequenzen" aria-label="Permalink to &quot;Konsequenzen&quot;">​</a></h2><h3 id="positiv" tabindex="-1">positiv <a class="header-anchor" href="#positiv" aria-label="Permalink to &quot;positiv&quot;">​</a></h3><p>Durch die Einführung dieser Naming Conventions wird die Lesbarkeit und Verständlichkeit unserer Tests verbessert. Die einheitliche Namensgebung erleichtert es anderen Entwicklern, den Code zu verstehen und zu warten. Außerdem wird die Zusammenarbeit innerhalb des Teams effizienter, da jeder bei Bedarf schnell die relevanten Tests finden und bearbeiten kann. Darüber hinaus ermöglicht die Verwendung von <code>@Nested</code> eine bessere Strukturierung der Tests und eine einfachere Überprüfung der Testergebnisse. Insgesamt erwarten wir eine höhere Codequalität und eine schnellere Fehlererkennung durch die Einführung dieser Convention.</p><h3 id="negativ" tabindex="-1">negativ <a class="header-anchor" href="#negativ" aria-label="Permalink to &quot;negativ&quot;">​</a></h3><p>Gegebenenfalls müssen - neben der Anpassung der Namen - einige bestehende Tests umgeschrieben werden, um der neuen Bezeichnung zu entsprechen.</p>`,18)]))}const c=i(t,[["render",h]]);export{g as __pageData,c as default};
