import{p as et}from"./chunk-JWPE2WC7.BNN3T5de.js";import{g as at,s as rt,a as it,b as nt,q as st,p as ot,_ as l,l as z,c as lt,D as ct,G as dt,H as gt,I as U,J as ht,d as pt,r as ut,K as ft,E as mt}from"./theme.JV3qEoYV.js";import{p as vt}from"./cynefin-OW5HDTMX.BqN8npRm.js";import"./framework.HWAOx3Ef.js";var St=mt.pie,R={sections:new Map,showData:!1},T=R.sections,H=R.showData,xt=structuredClone(St),wt=l(()=>structuredClone(xt),"getConfig"),Ct=l(()=>{T=new Map,H=R.showData,ut()},"clear"),$t=l(({label:t,value:a})=>{if(a<0)throw new Error(`"${t}" has invalid value: ${a}. Negative values are not allowed in pie charts. All slice values must be >= 0.`);T.has(t)||(T.set(t,a),z.debug(`added new section: ${t}, with value: ${a}`))},"addSection"),Dt=l(()=>T,"getSections"),yt=l(t=>{H=t},"setShowData"),Tt=l(()=>H,"getShowData"),q={getConfig:wt,clear:Ct,setDiagramTitle:ot,getDiagramTitle:st,setAccTitle:nt,getAccTitle:it,setAccDescription:rt,getAccDescription:at,addSection:$t,getSections:Dt,setShowData:yt,getShowData:Tt},bt=l((t,a)=>{et(t,a),a.setShowData(t.showData),t.sections.map(a.addSection)},"populateDb"),At={parse:l(async t=>{const a=await vt("pie",t);z.debug(a),bt(a,q)},"parse")},_t=l(t=>`
  .pieCircle{
    stroke: ${t.pieStrokeColor};
    stroke-width : ${t.pieStrokeWidth};
    opacity : ${t.pieOpacity};
  }
  .pieCircle.highlighted{
    scale: 1.05;
    opacity: 1;
  }
  .pieCircle.highlightedOnHover:hover{
    transition-duration: 250ms;
    scale: 1.05;
    opacity: 1;
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
`,"getStyles"),kt=_t,Et=l(t=>{const a=[...t.values()].reduce((s,m)=>s+m,0),L=[...t.entries()].map(([s,m])=>({label:s,value:m})).filter(s=>s.value/a*100>=1);return ft().value(s=>s.value).sort(null)(L)},"createPieArcs"),zt=l((t,a,L,W)=>{var N;z.debug(`rendering pie chart
`+t);const s=W.db,m=lt(),p=ct(s.getConfig(),m.pie),F=40,i=18,c=4,C=450,S=C,b=dt(a),$=b.append("g");$.attr("transform","translate("+S/2+","+C/2+")");const{themeVariables:n}=m;let[G]=gt(n.pieOuterStrokeWidth);G??(G=2);const J=p.legendPosition,M=p.textPosition,K=p.donutHole>0&&p.donutHole<=.9?p.donutHole:0,u=Math.min(S,C)/2-F,V=U().innerRadius(K*u).outerRadius(u),X=U().innerRadius(u*M).outerRadius(u*M),x=$.append("g");x.append("circle").attr("cx",0).attr("cy",0).attr("r",u+G/2).attr("class","pieOuterCircle");const D=s.getSections(),Z=Et(D),j=[n.pie1,n.pie2,n.pie3,n.pie4,n.pie5,n.pie6,n.pie7,n.pie8,n.pie9,n.pie10,n.pie11,n.pie12];let A=0;D.forEach(e=>{A+=e});const O=Z.filter(e=>(e.data.value/A*100).toFixed(0)!=="0"),_=ht(j).domain([...D.keys()]);x.selectAll("mySlices").data(O).enter().append("path").attr("d",V).attr("fill",e=>_(e.data.label)).attr("class",e=>{let r="pieCircle";return p.highlightSlice==="hover"?r+=" highlightedOnHover":p.highlightSlice===e.data.label&&(r+=" highlighted"),r}),x.selectAll("mySlices").data(O).enter().append("text").text(e=>(e.data.value/A*100).toFixed(0)+"%").attr("transform",e=>"translate("+X.centroid(e)+")").style("text-anchor","middle").attr("class","slice");const Q=$.append("text").text(s.getDiagramTitle()).attr("x",0).attr("y",-400/2).attr("class","pieTitleText"),w=[...D.entries()].map(([e,r])=>({label:e,value:r})),f=$.selectAll(".legend").data(w).enter().append("g").attr("class","legend");f.append("rect").attr("width",i).attr("height",i).style("fill",e=>_(e.label)).style("stroke",e=>_(e.label)),f.append("text").attr("x",i+c).attr("y",i-c).text(e=>s.getShowData()?`${e.label} [${e.value}]`:e.label);const v=Math.max(...f.selectAll("text").nodes().map(e=>(e==null?void 0:e.getBoundingClientRect().width)??0));let y=C,k=S+F;const o=i+c,E=w.length*o;switch(J){case"center":f.attr("transform",(e,r)=>{const d=o*w.length/2,g=-v/2-(i+c),h=r*o-d;return"translate("+g+","+h+")"});break;case"top":y+=E,f.attr("transform",(e,r)=>{const d=u,g=-v/2-(i+c),h=r*o-d;return`translate(${g}, ${h})`}),x.attr("transform",()=>`translate(0, ${E+o})`);break;case"bottom":y+=E,f.attr("transform",(e,r)=>{const d=-u-o,g=-v/2-(i+c),h=r*o-d;return"translate("+g+","+h+")"});break;case"left":k+=i+c+v,f.attr("transform",(e,r)=>{const d=o*w.length/2,g=-u-(i+c),h=r*o-d;return"translate("+g+","+h+")"}),x.attr("transform",()=>`translate(${v+i+c}, 0)`);break;case"right":default:k+=i+c+v,f.attr("transform",(e,r)=>{const d=o*w.length/2,g=12*i,h=r*o-d;return"translate("+g+","+h+")"});break}const P=((N=Q.node())==null?void 0:N.getBoundingClientRect().width)??0,Y=S/2-P/2,tt=S/2+P/2,B=Math.min(0,Y),I=Math.max(k,tt)-B;b.attr("viewBox",`${B} 0 ${I} ${y}`),pt(b,y,I,p.useMaxWidth)},"draw"),Rt={draw:zt},Mt={parser:At,db:q,renderer:Rt,styles:kt};export{Mt as diagram};
