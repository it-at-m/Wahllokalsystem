import{p as U}from"./chunk-RYO7GUH3.BCI3RtYO.js";import{W as y,N as z,aG as j,B as q,p as H,q as Z,s as J,g as K,c as Q,b as X,_ as u,l as G,t as Y,d as tt,D as et,H as at,a4 as rt,k as nt}from"../app.BC7lO6YG.js";import{p as it}from"./gitGraph-YCYPL57B.BObgLiPK.js";import{d as P}from"./arc.ByiVjaHU.js";import{o as st}from"./ordinal.BYWQX77i.js";import"./framework.rHWNTPhC.js";import"./theme.CuCJE8Gd.js";import"./baseUniq.D4jFbqbe.js";import"./basePickBy.CVvuQu4N.js";import"./clone.SApgZt-I.js";import"./init.Gi6I4Gst.js";function ot(t,a){return a<t?-1:a>t?1:a>=t?0:NaN}function lt(t){return t}function ct(){var t=lt,a=ot,m=null,o=y(0),g=y(z),x=y(0);function i(e){var r,l=(e=j(e)).length,c,A,h=0,p=new Array(l),n=new Array(l),v=+o.apply(this,arguments),w=Math.min(z,Math.max(-z,g.apply(this,arguments)-v)),f,T=Math.min(Math.abs(w)/l,x.apply(this,arguments)),$=T*(w<0?-1:1),d;for(r=0;r<l;++r)(d=n[p[r]=r]=+t(e[r],r,e))>0&&(h+=d);for(a!=null?p.sort(function(S,D){return a(n[S],n[D])}):m!=null&&p.sort(function(S,D){return m(e[S],e[D])}),r=0,A=h?(w-l*$)/h:0;r<l;++r,v=f)c=p[r],d=n[c],f=v+(d>0?d*A:0)+$,n[c]={data:e[c],index:r,value:d,startAngle:v,endAngle:f,padAngle:T};return n}return i.value=function(e){return arguments.length?(t=typeof e=="function"?e:y(+e),i):t},i.sortValues=function(e){return arguments.length?(a=e,m=null,i):a},i.sort=function(e){return arguments.length?(m=e,a=null,i):m},i.startAngle=function(e){return arguments.length?(o=typeof e=="function"?e:y(+e),i):o},i.endAngle=function(e){return arguments.length?(g=typeof e=="function"?e:y(+e),i):g},i.padAngle=function(e){return arguments.length?(x=typeof e=="function"?e:y(+e),i):x},i}var R=q.pie,W={sections:new Map,showData:!1,config:R},b=W.sections,F=W.showData,pt=structuredClone(R),ut=u(()=>structuredClone(pt),"getConfig"),gt=u(()=>{b=new Map,F=W.showData,Y()},"clear"),dt=u(({label:t,value:a})=>{b.has(t)||(b.set(t,a),G.debug(`added new section: ${t}, with value: ${a}`))},"addSection"),ft=u(()=>b,"getSections"),mt=u(t=>{F=t},"setShowData"),ht=u(()=>F,"getShowData"),I={getConfig:ut,clear:gt,setDiagramTitle:H,getDiagramTitle:Z,setAccTitle:J,getAccTitle:K,setAccDescription:Q,getAccDescription:X,addSection:dt,getSections:ft,setShowData:mt,getShowData:ht},vt=u((t,a)=>{U(t,a),a.setShowData(t.showData),t.sections.map(a.addSection)},"populateDb"),St={parse:u(async t=>{const a=await it("pie",t);G.debug(a),vt(a,I)},"parse")},yt=u(t=>`
  .pieCircle{
    stroke: ${t.pieStrokeColor};
    stroke-width : ${t.pieStrokeWidth};
    opacity : ${t.pieOpacity};
  }
  .pieOuterCircle{
    stroke: ${t.pieOuterStrokeColor};
    stroke-width: ${t.pieOuterStrokeWidth};
    fill: none;
  }
  .pieTitleText {
    text-anchor: middle;
    font-size: ${t.pieTitleTextSize};
    fill: ${t.pieTitleTextColor};
    font-family: ${t.fontFamily};
  }
  .slice {
    font-family: ${t.fontFamily};
    fill: ${t.pieSectionTextColor};
    font-size:${t.pieSectionTextSize};
    // fill: white;
  }
  .legend text {
    fill: ${t.pieLegendTextColor};
    font-family: ${t.fontFamily};
    font-size: ${t.pieLegendTextSize};
  }
`,"getStyles"),xt=yt,At=u(t=>{const a=[...t.entries()].map(o=>({label:o[0],value:o[1]})).sort((o,g)=>g.value-o.value);return ct().value(o=>o.value)(a)},"createPieArcs"),wt=u((t,a,m,o)=>{G.debug(`rendering pie chart
`+t);const g=o.db,x=tt(),i=et(g.getConfig(),x.pie),e=40,r=18,l=4,c=450,A=c,h=at(a),p=h.append("g");p.attr("transform","translate("+A/2+","+c/2+")");const{themeVariables:n}=x;let[v]=rt(n.pieOuterStrokeWidth);v??(v=2);const w=i.textPosition,f=Math.min(A,c)/2-e,T=P().innerRadius(0).outerRadius(f),$=P().innerRadius(f*w).outerRadius(f*w);p.append("circle").attr("cx",0).attr("cy",0).attr("r",f+v/2).attr("class","pieOuterCircle");const d=g.getSections(),S=At(d),D=[n.pie1,n.pie2,n.pie3,n.pie4,n.pie5,n.pie6,n.pie7,n.pie8,n.pie9,n.pie10,n.pie11,n.pie12],C=st(D);p.selectAll("mySlices").data(S).enter().append("path").attr("d",T).attr("fill",s=>C(s.data.label)).attr("class","pieCircle");let N=0;d.forEach(s=>{N+=s}),p.selectAll("mySlices").data(S).enter().append("text").text(s=>(s.data.value/N*100).toFixed(0)+"%").attr("transform",s=>"translate("+$.centroid(s)+")").style("text-anchor","middle").attr("class","slice"),p.append("text").text(g.getDiagramTitle()).attr("x",0).attr("y",-(c-50)/2).attr("class","pieTitleText");const M=p.selectAll(".legend").data(C.domain()).enter().append("g").attr("class","legend").attr("transform",(s,k)=>{const E=r+l,_=E*C.domain().length/2,B=12*r,V=k*E-_;return"translate("+B+","+V+")"});M.append("rect").attr("width",r).attr("height",r).style("fill",C).style("stroke",C),M.data(S).append("text").attr("x",r+l).attr("y",r-l).text(s=>{const{label:k,value:E}=s.data;return g.getShowData()?`${k} [${E}]`:k});const L=Math.max(...M.selectAll("text").nodes().map(s=>(s==null?void 0:s.getBoundingClientRect().width)??0)),O=A+e+r+l+L;h.attr("viewBox",`0 0 ${O} ${c}`),nt(h,c,O,i.useMaxWidth)},"draw"),Dt={draw:wt},Nt={parser:St,db:I,renderer:Dt,styles:xt};export{Nt as diagram};
