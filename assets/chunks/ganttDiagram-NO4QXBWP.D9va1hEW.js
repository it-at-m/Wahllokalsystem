import{aG as ge,aH as pe,g as Te,s as xe,p as be,o as we,a as De,b as _e,_ as u,c as lt,d as Tt,aJ as Se,aK as Ce,aL as Me,e as Ee,K as Ie,aM as Ye,aN as G,l as at,aO as $e,aP as Ut,aQ as Xt,aR as Le,aS as Fe,aT as Ae,aU as Oe,aV as We,aW as Pe,aX as Ve,aY as qt,aZ as Zt,a_ as Qt,a$ as Kt,b0 as Jt,b1 as Re,k as ze,j as Ne,q as He,z as Be}from"./theme.BV07klyy.js";import"./framework.HWAOx3Ef.js";var Mt="day",Ge="week",je="year",Ue="YYYY-MM-DDTHH:mm:ssZ",Xe="isoweek";const qe=function(t,e,s){var r=function(b,I){var w=(I?s.utc:s)().year(b).startOf(je),O=4-w.isoWeekday();return w.isoWeekday()>4&&(O+=7),w.add(O,Mt)},i=function(b){return b.add(4-b.isoWeekday(),Mt)},c=e.prototype;c.isoWeekYear=function(){var v=i(this);return v.year()},c.isoWeek=function(v){if(!this.$utils().u(v))return this.add((v-this.isoWeek())*7,Mt);var b=i(this),I=r(this.isoWeekYear(),this.$u);return b.diff(I,Ge)+1},c.isoWeekday=function(v){return this.$utils().u(v)?this.day()||7:this.day(this.day()%7?v:v-7)};var h=c.startOf;c.startOf=function(v,b){var I=this.$utils(),w=I.u(b)?!0:b,O=I.p(v);return O===Xe?w?this.date(this.date()-(this.isoWeekday()-1)).startOf("day"):this.date(this.date()-1-(this.isoWeekday()-1)+7).endOf("day"):h.bind(this)(v,b)}};var Ze=function(e){return e.replace(/(\[[^\]]+])|(MMMM|MM|DD|dddd)/g,function(s,r,i){return r||i.slice(1)})},Qe={LTS:"h:mm:ss A",LT:"h:mm A",L:"MM/DD/YYYY",LL:"MMMM D, YYYY",LLL:"MMMM D, YYYY h:mm A",LLLL:"dddd, MMMM D, YYYY h:mm A"},Ke=function(e,s){return e.replace(/(\[[^\]]+])|(LTS?|l{1,4}|L{1,4})/g,function(r,i,c){var h=c&&c.toUpperCase();return i||s[c]||Qe[c]||Ze(s[h])})},Je=/(\[[^[]*\])|([-_:/.,()\s]+)|(A|a|Q|YYYY|YY?|ww?|MM?M?M?|Do|DD?|hh?|HH?|mm?|ss?|S{1,3}|z|ZZ?)/g,te=/\d/,kt=/\d\d/,ts=/\d{3}/,es=/\d{4}/,J=/\d\d?/,ss=/[+-]?\d+/,rs=/[+-]\d\d:?(\d\d)?|Z/,yt=/\d*[^-_:/,()\s\d]+/,rt={},ne=function(e){return e=+e,e+(e>68?1900:2e3)};function is(t){if(!t||t==="Z")return 0;var e=t.match(/([+-]|\d\d)/g),s=+(e[1]*60)+(+e[2]||0);return s===0?0:e[0]==="+"?-s:s}var j=function(e){return function(s){this[e]=+s}},ee=[rs,function(t){var e=this.zone||(this.zone={});e.offset=is(t)}],Et=function(e){var s=rt[e];return s&&(s.indexOf?s:s.s.concat(s.f))},se=function(e,s){var r,i=rt,c=i.meridiem;if(!c)r=e===(s?"pm":"PM");else for(var h=1;h<=24;h+=1)if(e.indexOf(c(h,0,s))>-1){r=h>12;break}return r},as={A:[yt,function(t){this.afternoon=se(t,!1)}],a:[yt,function(t){this.afternoon=se(t,!0)}],Q:[te,function(t){this.month=(t-1)*3+1}],S:[te,function(t){this.milliseconds=+t*100}],SS:[kt,function(t){this.milliseconds=+t*10}],SSS:[ts,function(t){this.milliseconds=+t}],s:[J,j("seconds")],ss:[J,j("seconds")],m:[J,j("minutes")],mm:[J,j("minutes")],H:[J,j("hours")],h:[J,j("hours")],HH:[J,j("hours")],hh:[J,j("hours")],D:[J,j("day")],DD:[kt,j("day")],Do:[yt,function(t){var e=rt,s=e.ordinal,r=t.match(/\d+/);if(this.day=r[0],!!s)for(var i=1;i<=31;i+=1)s(i).replace(/\[|\]/g,"")===t&&(this.day=i)}],w:[J,j("week")],ww:[kt,j("week")],M:[J,j("month")],MM:[kt,j("month")],MMM:[yt,function(t){var e=Et("months"),s=Et("monthsShort"),r=(s||e.map(function(i){return i.slice(0,3)})).indexOf(t)+1;if(r<1)throw new Error;this.month=r%12||r}],MMMM:[yt,function(t){var e=Et("months"),s=e.indexOf(t)+1;if(s<1)throw new Error;this.month=s%12||s}],Y:[ss,j("year")],YY:[kt,function(t){this.year=ne(t)}],YYYY:[es,j("year")],Z:ee,ZZ:ee};function ns(t){var e=t.afternoon;if(e!==void 0){var s=t.hours;e?s<12&&(t.hours+=12):s===12&&(t.hours=0),delete t.afternoon}}function os(t){t=Ke(t,rt&&rt.formats);for(var e=t.match(Je),s=e.length,r=0;r<s;r+=1){var i=e[r],c=as[i],h=c&&c[0],v=c&&c[1];v?e[r]={regex:h,parser:v}:e[r]=i.replace(/^\[|\]$/g,"")}return function(b){for(var I={},w=0,O=0;w<s;w+=1){var L=e[w];if(typeof L=="string")O+=L.length;else{var A=L.regex,V=L.parser,F=b.slice(O),D=A.exec(F),z=D[0];V.call(I,z),b=b.replace(z,"")}}return ns(I),I}}var cs=function(e,s,r,i){try{if(["x","X"].indexOf(s)>-1)return new Date((s==="X"?1e3:1)*e);var c=os(s),h=c(e),v=h.year,b=h.month,I=h.day,w=h.hours,O=h.minutes,L=h.seconds,A=h.milliseconds,V=h.zone,F=h.week,D=new Date,z=I||(!v&&!b?D.getDate():1),K=v||D.getFullYear(),H=0;v&&!b||(H=b>0?b-1:D.getMonth());var Z=w||0,X=O||0,Y=L||0,p=A||0;if(V)return new Date(Date.UTC(K,H,z,Z,X,Y,p+V.offset*60*1e3));if(r)return new Date(Date.UTC(K,H,z,Z,X,Y,p));var m;return m=new Date(K,H,z,Z,X,Y,p),F&&(m=i(m).week(F).toDate()),m}catch{return new Date("")}};const ls=function(t,e,s){s.p.customParseFormat=!0,t&&t.parseTwoDigitYear&&(ne=t.parseTwoDigitYear);var r=e.prototype,i=r.parse;r.parse=function(c){var h=c.date,v=c.utc,b=c.args;this.$u=v;var I=b[1];if(typeof I=="string"){var w=b[2]===!0,O=b[3]===!0,L=w||O,A=b[2];O&&(A=b[2]),rt=this.$locale(),!w&&A&&(rt=s.Ls[A]),this.$d=cs(h,I,v,s),this.init(),A&&A!==!0&&(this.$L=this.locale(A).$L),L&&h!=this.format(I)&&(this.$d=new Date("")),rt={}}else if(I instanceof Array)for(var V=I.length,F=1;F<=V;F+=1){b[1]=I[F-1];var D=s.apply(this,b);if(D.isValid()){this.$d=D.$d,this.$L=D.$L,this.init();break}F===V&&(this.$d=new Date(""))}else i.call(this,c)}},us=function(t,e){var s=e.prototype,r=s.format;s.format=function(i){var c=this,h=this.$locale();if(!this.isValid())return r.bind(this)(i);var v=this.$utils(),b=i||Ue,I=b.replace(/\[([^\]]+)]|Q|wo|ww|w|WW|W|zzz|z|gggg|GGGG|Do|X|x|k{1,2}|S/g,function(w){switch(w){case"Q":return Math.ceil((c.$M+1)/3);case"Do":return h.ordinal(c.$D);case"gggg":return c.weekYear();case"GGGG":return c.isoWeekYear();case"wo":return h.ordinal(c.week(),"W");case"w":case"ww":return v.s(c.week(),w==="w"?1:2,"0");case"W":case"WW":return v.s(c.isoWeek(),w==="W"?1:2,"0");case"k":case"kk":return v.s(String(c.$H===0?24:c.$H),w==="k"?1:2,"0");case"X":return Math.floor(c.$d.getTime()/1e3);case"x":return c.$d.getTime();case"z":return"["+c.offsetName()+"]";case"zzz":return"["+c.offsetName("long")+"]";default:return w}});return r.bind(this)(I)}};var oe={exports:{}};(function(t,e){(function(s,r){t.exports=r()})(ge,function(){var s,r,i=1e3,c=6e4,h=36e5,v=864e5,b=31536e6,I=2628e6,w=/^(-|\+)?P(?:([-+]?[0-9,.]*)Y)?(?:([-+]?[0-9,.]*)M)?(?:([-+]?[0-9,.]*)W)?(?:([-+]?[0-9,.]*)D)?(?:T(?:([-+]?[0-9,.]*)H)?(?:([-+]?[0-9,.]*)M)?(?:([-+]?[0-9,.]*)S)?)?$/,O=/\[([^\]]+)]|YYYY|YY|Y|M{1,2}|D{1,2}|H{1,2}|m{1,2}|s{1,2}|SSS/g,L={years:b,months:I,days:v,hours:h,minutes:c,seconds:i,milliseconds:1,weeks:6048e5},A=function(Y){return Y instanceof Z},V=function(Y,p,m){return new Z(Y,m,p.$l)},F=function(Y){return r.p(Y)+"s"},D=function(Y){return Y<0},z=function(Y){return D(Y)?Math.ceil(Y):Math.floor(Y)},K=function(Y){return Math.abs(Y)},H=function(Y,p){return Y?D(Y)?{negative:!0,format:""+K(Y)+p}:{negative:!1,format:""+Y+p}:{negative:!1,format:""}},Z=function(){function Y(m,$,y){var T=this;if(this.$d={},this.$l=y,m===void 0&&(this.$ms=0,this.parseFromMilliseconds()),$)return V(m*L[F($)],this);if(typeof m=="number")return this.$ms=m,this.parseFromMilliseconds(),this;if(typeof m=="object")return Object.keys(m).forEach(function(n){T.$d[F(n)]=m[n]}),this.calMilliseconds(),this;if(typeof m=="string"){var x=m.match(w);if(x){var g=x.slice(2).map(function(n){return n!=null?Number(n):0});return this.$d.years=g[0],this.$d.months=g[1],this.$d.weeks=g[2],this.$d.days=g[3],this.$d.hours=g[4],this.$d.minutes=g[5],this.$d.seconds=g[6],this.calMilliseconds(),this}}return this}var p=Y.prototype;return p.calMilliseconds=function(){var m=this;this.$ms=Object.keys(this.$d).reduce(function($,y){return $+(m.$d[y]||0)*L[y]},0)},p.parseFromMilliseconds=function(){var m=this.$ms;this.$d.years=z(m/b),m%=b,this.$d.months=z(m/I),m%=I,this.$d.days=z(m/v),m%=v,this.$d.hours=z(m/h),m%=h,this.$d.minutes=z(m/c),m%=c,this.$d.seconds=z(m/i),m%=i,this.$d.milliseconds=m},p.toISOString=function(){var m=H(this.$d.years,"Y"),$=H(this.$d.months,"M"),y=+this.$d.days||0;this.$d.weeks&&(y+=7*this.$d.weeks);var T=H(y,"D"),x=H(this.$d.hours,"H"),g=H(this.$d.minutes,"M"),n=this.$d.seconds||0;this.$d.milliseconds&&(n+=this.$d.milliseconds/1e3,n=Math.round(1e3*n)/1e3);var f=H(n,"S"),k=m.negative||$.negative||T.negative||x.negative||g.negative||f.negative,d=x.format||g.format||f.format?"T":"",M=(k?"-":"")+"P"+m.format+$.format+T.format+d+x.format+g.format+f.format;return M==="P"||M==="-P"?"P0D":M},p.toJSON=function(){return this.toISOString()},p.format=function(m){var $=m||"YYYY-MM-DDTHH:mm:ss",y={Y:this.$d.years,YY:r.s(this.$d.years,2,"0"),YYYY:r.s(this.$d.years,4,"0"),M:this.$d.months,MM:r.s(this.$d.months,2,"0"),D:this.$d.days,DD:r.s(this.$d.days,2,"0"),H:this.$d.hours,HH:r.s(this.$d.hours,2,"0"),m:this.$d.minutes,mm:r.s(this.$d.minutes,2,"0"),s:this.$d.seconds,ss:r.s(this.$d.seconds,2,"0"),SSS:r.s(this.$d.milliseconds,3,"0")};return $.replace(O,function(T,x){return x||String(y[T])})},p.as=function(m){return this.$ms/L[F(m)]},p.get=function(m){var $=this.$ms,y=F(m);return y==="milliseconds"?$%=1e3:$=y==="weeks"?z($/L[y]):this.$d[y],$||0},p.add=function(m,$,y){var T;return T=$?m*L[F($)]:A(m)?m.$ms:V(m,this).$ms,V(this.$ms+T*(y?-1:1),this)},p.subtract=function(m,$){return this.add(m,$,!0)},p.locale=function(m){var $=this.clone();return $.$l=m,$},p.clone=function(){return V(this.$ms,this)},p.humanize=function(m){return s().add(this.$ms,"ms").locale(this.$l).fromNow(!m)},p.valueOf=function(){return this.asMilliseconds()},p.milliseconds=function(){return this.get("milliseconds")},p.asMilliseconds=function(){return this.as("milliseconds")},p.seconds=function(){return this.get("seconds")},p.asSeconds=function(){return this.as("seconds")},p.minutes=function(){return this.get("minutes")},p.asMinutes=function(){return this.as("minutes")},p.hours=function(){return this.get("hours")},p.asHours=function(){return this.as("hours")},p.days=function(){return this.get("days")},p.asDays=function(){return this.as("days")},p.weeks=function(){return this.get("weeks")},p.asWeeks=function(){return this.as("weeks")},p.months=function(){return this.get("months")},p.asMonths=function(){return this.as("months")},p.years=function(){return this.get("years")},p.asYears=function(){return this.as("years")},Y}(),X=function(Y,p,m){return Y.add(p.years()*m,"y").add(p.months()*m,"M").add(p.days()*m,"d").add(p.hours()*m,"h").add(p.minutes()*m,"m").add(p.seconds()*m,"s").add(p.milliseconds()*m,"ms")};return function(Y,p,m){s=m,r=m().$utils(),m.duration=function(T,x){var g=m.locale();return V(T,{$l:g},x)},m.isDuration=A;var $=p.prototype.add,y=p.prototype.subtract;p.prototype.add=function(T,x){return A(T)?X(this,T,1):$.bind(this)(T,x)},p.prototype.subtract=function(T,x){return A(T)?X(this,T,-1):y.bind(this)(T,x)}}})})(oe);var ds=oe.exports;const fs=pe(ds);var Yt=function(){var t=u(function(g,n,f,k){for(f=f||{},k=g.length;k--;f[g[k]]=n);return f},"o"),e=[6,8,10,12,13,14,15,16,17,18,20,21,22,23,24,25,26,27,28,29,30,31,33,35,36,38,40],s=[1,26],r=[1,27],i=[1,28],c=[1,29],h=[1,30],v=[1,31],b=[1,32],I=[1,33],w=[1,34],O=[1,9],L=[1,10],A=[1,11],V=[1,12],F=[1,13],D=[1,14],z=[1,15],K=[1,16],H=[1,19],Z=[1,20],X=[1,21],Y=[1,22],p=[1,23],m=[1,25],$=[1,35],y={trace:u(function(){},"trace"),yy:{},symbols_:{error:2,start:3,gantt:4,document:5,EOF:6,line:7,SPACE:8,statement:9,NL:10,weekday:11,weekday_monday:12,weekday_tuesday:13,weekday_wednesday:14,weekday_thursday:15,weekday_friday:16,weekday_saturday:17,weekday_sunday:18,weekend:19,weekend_friday:20,weekend_saturday:21,dateFormat:22,inclusiveEndDates:23,topAxis:24,axisFormat:25,tickInterval:26,excludes:27,includes:28,todayMarker:29,title:30,acc_title:31,acc_title_value:32,acc_descr:33,acc_descr_value:34,acc_descr_multiline_value:35,section:36,clickStatement:37,taskTxt:38,taskData:39,click:40,callbackname:41,callbackargs:42,href:43,clickStatementDebug:44,$accept:0,$end:1},terminals_:{2:"error",4:"gantt",6:"EOF",8:"SPACE",10:"NL",12:"weekday_monday",13:"weekday_tuesday",14:"weekday_wednesday",15:"weekday_thursday",16:"weekday_friday",17:"weekday_saturday",18:"weekday_sunday",20:"weekend_friday",21:"weekend_saturday",22:"dateFormat",23:"inclusiveEndDates",24:"topAxis",25:"axisFormat",26:"tickInterval",27:"excludes",28:"includes",29:"todayMarker",30:"title",31:"acc_title",32:"acc_title_value",33:"acc_descr",34:"acc_descr_value",35:"acc_descr_multiline_value",36:"section",38:"taskTxt",39:"taskData",40:"click",41:"callbackname",42:"callbackargs",43:"href"},productions_:[0,[3,3],[5,0],[5,2],[7,2],[7,1],[7,1],[7,1],[11,1],[11,1],[11,1],[11,1],[11,1],[11,1],[11,1],[19,1],[19,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,2],[9,2],[9,1],[9,1],[9,1],[9,2],[37,2],[37,3],[37,3],[37,4],[37,3],[37,4],[37,2],[44,2],[44,3],[44,3],[44,4],[44,3],[44,4],[44,2]],performAction:u(function(n,f,k,d,M,a,E){var l=a.length-1;switch(M){case 1:return a[l-1];case 2:this.$=[];break;case 3:a[l-1].push(a[l]),this.$=a[l-1];break;case 4:case 5:this.$=a[l];break;case 6:case 7:this.$=[];break;case 8:d.setWeekday("monday");break;case 9:d.setWeekday("tuesday");break;case 10:d.setWeekday("wednesday");break;case 11:d.setWeekday("thursday");break;case 12:d.setWeekday("friday");break;case 13:d.setWeekday("saturday");break;case 14:d.setWeekday("sunday");break;case 15:d.setWeekend("friday");break;case 16:d.setWeekend("saturday");break;case 17:d.setDateFormat(a[l].substr(11)),this.$=a[l].substr(11);break;case 18:d.enableInclusiveEndDates(),this.$=a[l].substr(18);break;case 19:d.TopAxis(),this.$=a[l].substr(8);break;case 20:d.setAxisFormat(a[l].substr(11)),this.$=a[l].substr(11);break;case 21:d.setTickInterval(a[l].substr(13)),this.$=a[l].substr(13);break;case 22:d.setExcludes(a[l].substr(9)),this.$=a[l].substr(9);break;case 23:d.setIncludes(a[l].substr(9)),this.$=a[l].substr(9);break;case 24:d.setTodayMarker(a[l].substr(12)),this.$=a[l].substr(12);break;case 27:d.setDiagramTitle(a[l].substr(6)),this.$=a[l].substr(6);break;case 28:this.$=a[l].trim(),d.setAccTitle(this.$);break;case 29:case 30:this.$=a[l].trim(),d.setAccDescription(this.$);break;case 31:d.addSection(a[l].substr(8)),this.$=a[l].substr(8);break;case 33:d.addTask(a[l-1],a[l]),this.$="task";break;case 34:this.$=a[l-1],d.setClickEvent(a[l-1],a[l],null);break;case 35:this.$=a[l-2],d.setClickEvent(a[l-2],a[l-1],a[l]);break;case 36:this.$=a[l-2],d.setClickEvent(a[l-2],a[l-1],null),d.setLink(a[l-2],a[l]);break;case 37:this.$=a[l-3],d.setClickEvent(a[l-3],a[l-2],a[l-1]),d.setLink(a[l-3],a[l]);break;case 38:this.$=a[l-2],d.setClickEvent(a[l-2],a[l],null),d.setLink(a[l-2],a[l-1]);break;case 39:this.$=a[l-3],d.setClickEvent(a[l-3],a[l-1],a[l]),d.setLink(a[l-3],a[l-2]);break;case 40:this.$=a[l-1],d.setLink(a[l-1],a[l]);break;case 41:case 47:this.$=a[l-1]+" "+a[l];break;case 42:case 43:case 45:this.$=a[l-2]+" "+a[l-1]+" "+a[l];break;case 44:case 46:this.$=a[l-3]+" "+a[l-2]+" "+a[l-1]+" "+a[l];break}},"anonymous"),table:[{3:1,4:[1,2]},{1:[3]},t(e,[2,2],{5:3}),{6:[1,4],7:5,8:[1,6],9:7,10:[1,8],11:17,12:s,13:r,14:i,15:c,16:h,17:v,18:b,19:18,20:I,21:w,22:O,23:L,24:A,25:V,26:F,27:D,28:z,29:K,30:H,31:Z,33:X,35:Y,36:p,37:24,38:m,40:$},t(e,[2,7],{1:[2,1]}),t(e,[2,3]),{9:36,11:17,12:s,13:r,14:i,15:c,16:h,17:v,18:b,19:18,20:I,21:w,22:O,23:L,24:A,25:V,26:F,27:D,28:z,29:K,30:H,31:Z,33:X,35:Y,36:p,37:24,38:m,40:$},t(e,[2,5]),t(e,[2,6]),t(e,[2,17]),t(e,[2,18]),t(e,[2,19]),t(e,[2,20]),t(e,[2,21]),t(e,[2,22]),t(e,[2,23]),t(e,[2,24]),t(e,[2,25]),t(e,[2,26]),t(e,[2,27]),{32:[1,37]},{34:[1,38]},t(e,[2,30]),t(e,[2,31]),t(e,[2,32]),{39:[1,39]},t(e,[2,8]),t(e,[2,9]),t(e,[2,10]),t(e,[2,11]),t(e,[2,12]),t(e,[2,13]),t(e,[2,14]),t(e,[2,15]),t(e,[2,16]),{41:[1,40],43:[1,41]},t(e,[2,4]),t(e,[2,28]),t(e,[2,29]),t(e,[2,33]),t(e,[2,34],{42:[1,42],43:[1,43]}),t(e,[2,40],{41:[1,44]}),t(e,[2,35],{43:[1,45]}),t(e,[2,36]),t(e,[2,38],{42:[1,46]}),t(e,[2,37]),t(e,[2,39])],defaultActions:{},parseError:u(function(n,f){if(f.recoverable)this.trace(n);else{var k=new Error(n);throw k.hash=f,k}},"parseError"),parse:u(function(n){var f=this,k=[0],d=[],M=[null],a=[],E=this.table,l="",B=0,o=0,_=2,S=1,W=a.slice.call(arguments,1),C=Object.create(this.lexer),R={yy:{}};for(var P in this.yy)Object.prototype.hasOwnProperty.call(this.yy,P)&&(R.yy[P]=this.yy[P]);C.setInput(n,R.yy),R.yy.lexer=C,R.yy.parser=this,typeof C.yylloc>"u"&&(C.yylloc={});var mt=C.yylloc;a.push(mt);var _t=C.options&&C.options.ranges;typeof R.yy.parseError=="function"?this.parseError=R.yy.parseError:this.parseError=Object.getPrototypeOf(this).parseError;function ve(q){k.length=k.length-2*q,M.length=M.length-q,a.length=a.length-q}u(ve,"popStack");function Gt(){var q;return q=d.pop()||C.lex()||S,typeof q!="number"&&(q instanceof Array&&(d=q,q=d.pop()),q=f.symbols_[q]||q),q}u(Gt,"lex");for(var U,it,Q,St,ot={},gt,et,jt,pt;;){if(it=k[k.length-1],this.defaultActions[it]?Q=this.defaultActions[it]:((U===null||typeof U>"u")&&(U=Gt()),Q=E[it]&&E[it][U]),typeof Q>"u"||!Q.length||!Q[0]){var Ct="";pt=[];for(gt in E[it])this.terminals_[gt]&&gt>_&&pt.push("'"+this.terminals_[gt]+"'");C.showPosition?Ct="Parse error on line "+(B+1)+`:
`+C.showPosition()+`
Expecting `+pt.join(", ")+", got '"+(this.terminals_[U]||U)+"'":Ct="Parse error on line "+(B+1)+": Unexpected "+(U==S?"end of input":"'"+(this.terminals_[U]||U)+"'"),this.parseError(Ct,{text:C.match,token:this.terminals_[U]||U,line:C.yylineno,loc:mt,expected:pt})}if(Q[0]instanceof Array&&Q.length>1)throw new Error("Parse Error: multiple actions possible at state: "+it+", token: "+U);switch(Q[0]){case 1:k.push(U),M.push(C.yytext),a.push(C.yylloc),k.push(Q[1]),U=null,o=C.yyleng,l=C.yytext,B=C.yylineno,mt=C.yylloc;break;case 2:if(et=this.productions_[Q[1]][1],ot.$=M[M.length-et],ot._$={first_line:a[a.length-(et||1)].first_line,last_line:a[a.length-1].last_line,first_column:a[a.length-(et||1)].first_column,last_column:a[a.length-1].last_column},_t&&(ot._$.range=[a[a.length-(et||1)].range[0],a[a.length-1].range[1]]),St=this.performAction.apply(ot,[l,o,B,R.yy,Q[1],M,a].concat(W)),typeof St<"u")return St;et&&(k=k.slice(0,-1*et*2),M=M.slice(0,-1*et),a=a.slice(0,-1*et)),k.push(this.productions_[Q[1]][0]),M.push(ot.$),a.push(ot._$),jt=E[k[k.length-2]][k[k.length-1]],k.push(jt);break;case 3:return!0}}return!0},"parse")},T=function(){var g={EOF:1,parseError:u(function(f,k){if(this.yy.parser)this.yy.parser.parseError(f,k);else throw new Error(f)},"parseError"),setInput:u(function(n,f){return this.yy=f||this.yy||{},this._input=n,this._more=this._backtrack=this.done=!1,this.yylineno=this.yyleng=0,this.yytext=this.matched=this.match="",this.conditionStack=["INITIAL"],this.yylloc={first_line:1,first_column:0,last_line:1,last_column:0},this.options.ranges&&(this.yylloc.range=[0,0]),this.offset=0,this},"setInput"),input:u(function(){var n=this._input[0];this.yytext+=n,this.yyleng++,this.offset++,this.match+=n,this.matched+=n;var f=n.match(/(?:\r\n?|\n).*/g);return f?(this.yylineno++,this.yylloc.last_line++):this.yylloc.last_column++,this.options.ranges&&this.yylloc.range[1]++,this._input=this._input.slice(1),n},"input"),unput:u(function(n){var f=n.length,k=n.split(/(?:\r\n?|\n)/g);this._input=n+this._input,this.yytext=this.yytext.substr(0,this.yytext.length-f),this.offset-=f;var d=this.match.split(/(?:\r\n?|\n)/g);this.match=this.match.substr(0,this.match.length-1),this.matched=this.matched.substr(0,this.matched.length-1),k.length-1&&(this.yylineno-=k.length-1);var M=this.yylloc.range;return this.yylloc={first_line:this.yylloc.first_line,last_line:this.yylineno+1,first_column:this.yylloc.first_column,last_column:k?(k.length===d.length?this.yylloc.first_column:0)+d[d.length-k.length].length-k[0].length:this.yylloc.first_column-f},this.options.ranges&&(this.yylloc.range=[M[0],M[0]+this.yyleng-f]),this.yyleng=this.yytext.length,this},"unput"),more:u(function(){return this._more=!0,this},"more"),reject:u(function(){if(this.options.backtrack_lexer)this._backtrack=!0;else return this.parseError("Lexical error on line "+(this.yylineno+1)+`. You can only invoke reject() in the lexer when the lexer is of the backtracking persuasion (options.backtrack_lexer = true).
`+this.showPosition(),{text:"",token:null,line:this.yylineno});return this},"reject"),less:u(function(n){this.unput(this.match.slice(n))},"less"),pastInput:u(function(){var n=this.matched.substr(0,this.matched.length-this.match.length);return(n.length>20?"...":"")+n.substr(-20).replace(/\n/g,"")},"pastInput"),upcomingInput:u(function(){var n=this.match;return n.length<20&&(n+=this._input.substr(0,20-n.length)),(n.substr(0,20)+(n.length>20?"...":"")).replace(/\n/g,"")},"upcomingInput"),showPosition:u(function(){var n=this.pastInput(),f=new Array(n.length+1).join("-");return n+this.upcomingInput()+`
`+f+"^"},"showPosition"),test_match:u(function(n,f){var k,d,M;if(this.options.backtrack_lexer&&(M={yylineno:this.yylineno,yylloc:{first_line:this.yylloc.first_line,last_line:this.last_line,first_column:this.yylloc.first_column,last_column:this.yylloc.last_column},yytext:this.yytext,match:this.match,matches:this.matches,matched:this.matched,yyleng:this.yyleng,offset:this.offset,_more:this._more,_input:this._input,yy:this.yy,conditionStack:this.conditionStack.slice(0),done:this.done},this.options.ranges&&(M.yylloc.range=this.yylloc.range.slice(0))),d=n[0].match(/(?:\r\n?|\n).*/g),d&&(this.yylineno+=d.length),this.yylloc={first_line:this.yylloc.last_line,last_line:this.yylineno+1,first_column:this.yylloc.last_column,last_column:d?d[d.length-1].length-d[d.length-1].match(/\r?\n?/)[0].length:this.yylloc.last_column+n[0].length},this.yytext+=n[0],this.match+=n[0],this.matches=n,this.yyleng=this.yytext.length,this.options.ranges&&(this.yylloc.range=[this.offset,this.offset+=this.yyleng]),this._more=!1,this._backtrack=!1,this._input=this._input.slice(n[0].length),this.matched+=n[0],k=this.performAction.call(this,this.yy,this,f,this.conditionStack[this.conditionStack.length-1]),this.done&&this._input&&(this.done=!1),k)return k;if(this._backtrack){for(var a in M)this[a]=M[a];return!1}return!1},"test_match"),next:u(function(){if(this.done)return this.EOF;this._input||(this.done=!0);var n,f,k,d;this._more||(this.yytext="",this.match="");for(var M=this._currentRules(),a=0;a<M.length;a++)if(k=this._input.match(this.rules[M[a]]),k&&(!f||k[0].length>f[0].length)){if(f=k,d=a,this.options.backtrack_lexer){if(n=this.test_match(k,M[a]),n!==!1)return n;if(this._backtrack){f=!1;continue}else return!1}else if(!this.options.flex)break}return f?(n=this.test_match(f,M[d]),n!==!1?n:!1):this._input===""?this.EOF:this.parseError("Lexical error on line "+(this.yylineno+1)+`. Unrecognized text.
`+this.showPosition(),{text:"",token:null,line:this.yylineno})},"next"),lex:u(function(){var f=this.next();return f||this.lex()},"lex"),begin:u(function(f){this.conditionStack.push(f)},"begin"),popState:u(function(){var f=this.conditionStack.length-1;return f>0?this.conditionStack.pop():this.conditionStack[0]},"popState"),_currentRules:u(function(){return this.conditionStack.length&&this.conditionStack[this.conditionStack.length-1]?this.conditions[this.conditionStack[this.conditionStack.length-1]].rules:this.conditions.INITIAL.rules},"_currentRules"),topState:u(function(f){return f=this.conditionStack.length-1-Math.abs(f||0),f>=0?this.conditionStack[f]:"INITIAL"},"topState"),pushState:u(function(f){this.begin(f)},"pushState"),stateStackSize:u(function(){return this.conditionStack.length},"stateStackSize"),options:{"case-insensitive":!0},performAction:u(function(f,k,d,M){switch(d){case 0:return this.begin("open_directive"),"open_directive";case 1:return this.begin("acc_title"),31;case 2:return this.popState(),"acc_title_value";case 3:return this.begin("acc_descr"),33;case 4:return this.popState(),"acc_descr_value";case 5:this.begin("acc_descr_multiline");break;case 6:this.popState();break;case 7:return"acc_descr_multiline_value";case 8:break;case 9:break;case 10:break;case 11:return 10;case 12:break;case 13:break;case 14:this.begin("href");break;case 15:this.popState();break;case 16:return 43;case 17:this.begin("callbackname");break;case 18:this.popState();break;case 19:this.popState(),this.begin("callbackargs");break;case 20:return 41;case 21:this.popState();break;case 22:return 42;case 23:this.begin("click");break;case 24:this.popState();break;case 25:return 40;case 26:return 4;case 27:return 22;case 28:return 23;case 29:return 24;case 30:return 25;case 31:return 26;case 32:return 28;case 33:return 27;case 34:return 29;case 35:return 12;case 36:return 13;case 37:return 14;case 38:return 15;case 39:return 16;case 40:return 17;case 41:return 18;case 42:return 20;case 43:return 21;case 44:return"date";case 45:return 30;case 46:return"accDescription";case 47:return 36;case 48:return 38;case 49:return 39;case 50:return":";case 51:return 6;case 52:return"INVALID"}},"anonymous"),rules:[/^(?:%%\{)/i,/^(?:accTitle\s*:\s*)/i,/^(?:(?!\n||)*[^\n]*)/i,/^(?:accDescr\s*:\s*)/i,/^(?:(?!\n||)*[^\n]*)/i,/^(?:accDescr\s*\{\s*)/i,/^(?:[\}])/i,/^(?:[^\}]*)/i,/^(?:%%(?!\{)*[^\n]*)/i,/^(?:[^\}]%%*[^\n]*)/i,/^(?:%%*[^\n]*[\n]*)/i,/^(?:[\n]+)/i,/^(?:\s+)/i,/^(?:%[^\n]*)/i,/^(?:href[\s]+["])/i,/^(?:["])/i,/^(?:[^"]*)/i,/^(?:call[\s]+)/i,/^(?:\([\s]*\))/i,/^(?:\()/i,/^(?:[^(]*)/i,/^(?:\))/i,/^(?:[^)]*)/i,/^(?:click[\s]+)/i,/^(?:[\s\n])/i,/^(?:[^\s\n]*)/i,/^(?:gantt\b)/i,/^(?:dateFormat\s[^#\n;]+)/i,/^(?:inclusiveEndDates\b)/i,/^(?:topAxis\b)/i,/^(?:axisFormat\s[^#\n;]+)/i,/^(?:tickInterval\s[^#\n;]+)/i,/^(?:includes\s[^#\n;]+)/i,/^(?:excludes\s[^#\n;]+)/i,/^(?:todayMarker\s[^\n;]+)/i,/^(?:weekday\s+monday\b)/i,/^(?:weekday\s+tuesday\b)/i,/^(?:weekday\s+wednesday\b)/i,/^(?:weekday\s+thursday\b)/i,/^(?:weekday\s+friday\b)/i,/^(?:weekday\s+saturday\b)/i,/^(?:weekday\s+sunday\b)/i,/^(?:weekend\s+friday\b)/i,/^(?:weekend\s+saturday\b)/i,/^(?:\d\d\d\d-\d\d-\d\d\b)/i,/^(?:title\s[^\n]+)/i,/^(?:accDescription\s[^#\n;]+)/i,/^(?:section\s[^\n]+)/i,/^(?:[^:\n]+)/i,/^(?::[^#\n;]+)/i,/^(?::)/i,/^(?:$)/i,/^(?:.)/i],conditions:{acc_descr_multiline:{rules:[6,7],inclusive:!1},acc_descr:{rules:[4],inclusive:!1},acc_title:{rules:[2],inclusive:!1},callbackargs:{rules:[21,22],inclusive:!1},callbackname:{rules:[18,19,20],inclusive:!1},href:{rules:[15,16],inclusive:!1},click:{rules:[24,25],inclusive:!1},INITIAL:{rules:[0,1,3,5,8,9,10,11,12,13,14,17,23,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52],inclusive:!0}}};return g}();y.lexer=T;function x(){this.yy={}}return u(x,"Parser"),x.prototype=y,y.Parser=x,new x}();Yt.parser=Yt;var hs=Yt;G.extend(qe);G.extend(ls);G.extend(us);var re={friday:5,saturday:6},tt="",At="",Ot=void 0,Wt="",dt=[],ft=[],Pt=new Map,Vt=[],wt=[],ht="",Rt="",ce=["active","done","crit","milestone","vert"],zt=[],ct="",vt=!1,Nt=!1,Ht="sunday",Dt="saturday",$t=0,ms=u(function(){Vt=[],wt=[],ht="",zt=[],xt=0,Ft=void 0,bt=void 0,N=[],tt="",At="",Rt="",Ot=void 0,Wt="",dt=[],ft=[],vt=!1,Nt=!1,$t=0,Pt=new Map,ct="",He(),Ht="sunday",Dt="saturday"},"clear"),ks=u(function(t){ct=t},"setDiagramId"),ys=u(function(t){At=t},"setAxisFormat"),vs=u(function(){return At},"getAxisFormat"),gs=u(function(t){Ot=t},"setTickInterval"),ps=u(function(){return Ot},"getTickInterval"),Ts=u(function(t){Wt=t},"setTodayMarker"),xs=u(function(){return Wt},"getTodayMarker"),bs=u(function(t){tt=t},"setDateFormat"),ws=u(function(){vt=!0},"enableInclusiveEndDates"),Ds=u(function(){return vt},"endDatesAreInclusive"),_s=u(function(){Nt=!0},"enableTopAxis"),Ss=u(function(){return Nt},"topAxisEnabled"),Cs=u(function(t){Rt=t},"setDisplayMode"),Ms=u(function(){return Rt},"getDisplayMode"),Es=u(function(){return tt},"getDateFormat"),le=u((t,e)=>{const s=e.toLowerCase().split(/[\s,]+/).filter(r=>r!=="");return[...new Set([...t,...s])]},"mergeTokens"),Is=u(function(t){dt=le(dt,t)},"setIncludes"),Ys=u(function(){return dt},"getIncludes"),$s=u(function(t){ft=le(ft,t)},"setExcludes"),Ls=u(function(){return ft},"getExcludes"),Fs=u(function(){return Pt},"getLinks"),As=u(function(t){ht=t,Vt.push(t)},"addSection"),Os=u(function(){return Vt},"getSections"),Ws=u(function(){let t=ie();const e=10;let s=0;for(;!t&&s<e;)t=ie(),s++;return wt=N,wt},"getTasks"),ue=u(function(t,e,s,r){const i=t.format(e.trim()),c=t.format("YYYY-MM-DD");return r.includes(i)||r.includes(c)?!1:s.includes("weekends")&&(t.isoWeekday()===re[Dt]||t.isoWeekday()===re[Dt]+1)||s.includes(t.format("dddd").toLowerCase())?!0:s.includes(i)||s.includes(c)},"isInvalidDate"),Ps=u(function(t){Ht=t},"setWeekday"),Vs=u(function(){return Ht},"getWeekday"),Rs=u(function(t){Dt=t},"setWeekend"),de=u(function(t,e,s,r){if(!s.length||t.manualEndTime)return;let i;t.startTime instanceof Date?i=G(t.startTime):i=G(t.startTime,e,!0),i=i.add(1,"d");let c;t.endTime instanceof Date?c=G(t.endTime):c=G(t.endTime,e,!0);const[h,v]=zs(i,c,e,s,r);t.endTime=h.toDate(),t.renderEndTime=v},"checkTaskDates"),zs=u(function(t,e,s,r,i){let c=!1,h=null;const v=e.add(1e4,"d");for(;t<=e;){if(c||(h=e.toDate()),c=ue(t,s,r,i),c&&(e=e.add(1,"d"),e>v))throw new Error("Failed to find a valid date that was not excluded by `excludes` after 10,000 iterations.");t=t.add(1,"d")}return[e,h]},"fixTaskDates"),Lt=u(function(t,e,s){if(s=s.trim(),u(v=>{const b=v.trim();return b==="x"||b==="X"},"isTimestampFormat")(e)&&/^\d+$/.test(s))return new Date(Number(s));const c=/^after\s+(?<ids>[\d\w- ]+)/.exec(s);if(c!==null){let v=null;for(const I of c.groups.ids.split(" ")){let w=nt(I);w!==void 0&&(!v||w.endTime>v.endTime)&&(v=w)}if(v)return v.endTime;const b=new Date;return b.setHours(0,0,0,0),b}let h=G(s,e.trim(),!0);if(h.isValid())return h.toDate();{at.debug("Invalid date:"+s),at.debug("With date format:"+e.trim());const v=new Date(s);if(v===void 0||isNaN(v.getTime())||v.getFullYear()<-1e4||v.getFullYear()>1e4)throw new Error("Invalid date:"+s);return v}},"getStartDate"),fe=u(function(t){const e=/^(\d+(?:\.\d+)?)([Mdhmswy]|ms)$/.exec(t.trim());return e!==null?[Number.parseFloat(e[1]),e[2]]:[NaN,"ms"]},"parseDuration"),he=u(function(t,e,s,r=!1){s=s.trim();const c=/^until\s+(?<ids>[\d\w- ]+)/.exec(s);if(c!==null){let w=null;for(const L of c.groups.ids.split(" ")){let A=nt(L);A!==void 0&&(!w||A.startTime<w.startTime)&&(w=A)}if(w)return w.startTime;const O=new Date;return O.setHours(0,0,0,0),O}let h=G(s,e.trim(),!0);if(h.isValid())return r&&(h=h.add(1,"d")),h.toDate();let v=G(t);const[b,I]=fe(s);if(!Number.isNaN(b)){const w=v.add(b,I);w.isValid()&&(v=w)}return v.toDate()},"getEndDate"),xt=0,ut=u(function(t){return t===void 0?(xt=xt+1,"task"+xt):t},"parseId"),Ns=u(function(t,e){let s;e.substr(0,1)===":"?s=e.substr(1,e.length):s=e;const r=s.split(","),i={};Bt(r,i,ce);for(let h=0;h<r.length;h++)r[h]=r[h].trim();let c="";switch(r.length){case 1:i.id=ut(),i.startTime=t.endTime,c=r[0];break;case 2:i.id=ut(),i.startTime=Lt(void 0,tt,r[0]),c=r[1];break;case 3:i.id=ut(r[0]),i.startTime=Lt(void 0,tt,r[1]),c=r[2];break}return c&&(i.endTime=he(i.startTime,tt,c,vt),i.manualEndTime=G(c,"YYYY-MM-DD",!0).isValid(),de(i,tt,ft,dt)),i},"compileData"),Hs=u(function(t,e){let s;e.substr(0,1)===":"?s=e.substr(1,e.length):s=e;const r=s.split(","),i={};Bt(r,i,ce);for(let c=0;c<r.length;c++)r[c]=r[c].trim();switch(r.length){case 1:i.id=ut(),i.startTime={type:"prevTaskEnd",id:t},i.endTime={data:r[0]};break;case 2:i.id=ut(),i.startTime={type:"getStartDate",startData:r[0]},i.endTime={data:r[1]};break;case 3:i.id=ut(r[0]),i.startTime={type:"getStartDate",startData:r[1]},i.endTime={data:r[2]};break}return i},"parseData"),Ft,bt,N=[],me={},Bs=u(function(t,e){const s={section:ht,type:ht,processed:!1,manualEndTime:!1,renderEndTime:null,raw:{data:e},task:t,classes:[]},r=Hs(bt,e);s.raw.startTime=r.startTime,s.raw.endTime=r.endTime,s.id=r.id,s.prevTaskId=bt,s.active=r.active,s.done=r.done,s.crit=r.crit,s.milestone=r.milestone,s.vert=r.vert,s.vert?s.order=-1:(s.order=$t,$t++);const i=N.push(s);bt=s.id,me[s.id]=i-1},"addTask"),nt=u(function(t){const e=me[t];return N[e]},"findTaskById"),Gs=u(function(t,e){const s={section:ht,type:ht,description:t,task:t,classes:[]},r=Ns(Ft,e);s.startTime=r.startTime,s.endTime=r.endTime,s.id=r.id,s.active=r.active,s.done=r.done,s.crit=r.crit,s.milestone=r.milestone,s.vert=r.vert,Ft=s,wt.push(s)},"addTaskOrg"),ie=u(function(){const t=u(function(s){const r=N[s];let i="";switch(N[s].raw.startTime.type){case"prevTaskEnd":{const c=nt(r.prevTaskId);r.startTime=c.endTime;break}case"getStartDate":i=Lt(void 0,tt,N[s].raw.startTime.startData),i&&(N[s].startTime=i);break}return N[s].startTime&&(N[s].endTime=he(N[s].startTime,tt,N[s].raw.endTime.data,vt),N[s].endTime&&(N[s].processed=!0,N[s].manualEndTime=G(N[s].raw.endTime.data,"YYYY-MM-DD",!0).isValid(),de(N[s],tt,ft,dt))),N[s].processed},"compileTask");let e=!0;for(const[s,r]of N.entries())t(s),e=e&&r.processed;return e},"compileTasks"),js=u(function(t,e){let s=e;lt().securityLevel!=="loose"&&(s=Ne(e)),t.split(",").forEach(function(r){nt(r)!==void 0&&(ye(r,()=>{window.open(s,"_self")}),Pt.set(r,s))}),ke(t,"clickable")},"setLink"),ke=u(function(t,e){t.split(",").forEach(function(s){let r=nt(s);r!==void 0&&r.classes.push(e)})},"setClass"),Us=u(function(t,e,s){if(lt().securityLevel!=="loose"||e===void 0)return;let r=[];if(typeof s=="string"){r=s.split(/,(?=(?:(?:[^"]*"){2})*[^"]*$)/);for(let c=0;c<r.length;c++){let h=r[c].trim();h.startsWith('"')&&h.endsWith('"')&&(h=h.substr(1,h.length-2)),r[c]=h}}r.length===0&&r.push(t),nt(t)!==void 0&&ye(t,()=>{Be.runFunc(e,...r)})},"setClickFun"),ye=u(function(t,e){zt.push(function(){const s=ct?`${ct}-${t}`:t,r=document.querySelector(`[id="${s}"]`);r!==null&&r.addEventListener("click",function(){e()})},function(){const s=ct?`${ct}-${t}`:t,r=document.querySelector(`[id="${s}-text"]`);r!==null&&r.addEventListener("click",function(){e()})})},"pushFun"),Xs=u(function(t,e,s){t.split(",").forEach(function(r){Us(r,e,s)}),ke(t,"clickable")},"setClickEvent"),qs=u(function(t){zt.forEach(function(e){e(t)})},"bindFunctions"),Zs={getConfig:u(()=>lt().gantt,"getConfig"),clear:ms,setDateFormat:bs,getDateFormat:Es,enableInclusiveEndDates:ws,endDatesAreInclusive:Ds,enableTopAxis:_s,topAxisEnabled:Ss,setAxisFormat:ys,getAxisFormat:vs,setTickInterval:gs,getTickInterval:ps,setTodayMarker:Ts,getTodayMarker:xs,setAccTitle:_e,getAccTitle:De,setDiagramTitle:we,getDiagramTitle:be,setDiagramId:ks,setDisplayMode:Cs,getDisplayMode:Ms,setAccDescription:xe,getAccDescription:Te,addSection:As,getSections:Os,getTasks:Ws,addTask:Bs,findTaskById:nt,addTaskOrg:Gs,setIncludes:Is,getIncludes:Ys,setExcludes:$s,getExcludes:Ls,setClickEvent:Xs,setLink:js,getLinks:Fs,bindFunctions:qs,parseDuration:fe,isInvalidDate:ue,setWeekday:Ps,getWeekday:Vs,setWeekend:Rs};function Bt(t,e,s){let r=!0;for(;r;)r=!1,s.forEach(function(i){const c="^\\s*"+i+"\\s*$",h=new RegExp(c);t[0].match(h)&&(e[i]=!0,t.shift(1),r=!0)})}u(Bt,"getTaskTags");G.extend(fs);var Qs=u(function(){at.debug("Something is calling, setConf, remove the call")},"setConf"),ae={monday:Ve,tuesday:Pe,wednesday:We,thursday:Oe,friday:Ae,saturday:Fe,sunday:Le},Ks=u((t,e)=>{let s=[...t].map(()=>-1/0),r=[...t].sort((c,h)=>c.startTime-h.startTime||c.order-h.order),i=0;for(const c of r)for(let h=0;h<s.length;h++)if(c.startTime>=s[h]){s[h]=c.endTime,c.order=h+e,h>i&&(i=h);break}return i},"getMaxIntersections"),st,It=1e4,Js=u(function(t,e,s,r){const i=lt().gantt;r.db.setDiagramId(e);const c=lt().securityLevel;let h;c==="sandbox"&&(h=Tt("#i"+e));const v=c==="sandbox"?Tt(h.nodes()[0].contentDocument.body):Tt("body"),b=c==="sandbox"?h.nodes()[0].contentDocument:document,I=b.getElementById(e);st=I.parentElement.offsetWidth,st===void 0&&(st=1200),i.useWidth!==void 0&&(st=i.useWidth);const w=r.db.getTasks(),O=w.filter(y=>!y.vert);let L=[];for(const y of O)L.push(y.type);L=$(L);const A={};let V=2*i.topPadding;if(r.db.getDisplayMode()==="compact"||i.displayMode==="compact"){const y={};for(const x of O)y[x.section]===void 0?y[x.section]=[x]:y[x.section].push(x);let T=0;for(const x of Object.keys(y)){const g=Ks(y[x],T)+1;T+=g,V+=g*(i.barHeight+i.barGap),A[x]=g}}else{V+=O.length*(i.barHeight+i.barGap);for(const y of L)A[y]=O.filter(T=>T.type===y).length}I.setAttribute("viewBox","0 0 "+st+" "+V);const F=v.select(`[id="${e}"]`),D=Se().domain([Ce(w,function(y){return y.startTime}),Me(w,function(y){return y.endTime})]).rangeRound([0,st-i.leftPadding-i.rightPadding]);function z(y,T){const x=y.startTime,g=T.startTime;let n=0;return x>g?n=1:x<g&&(n=-1),n}u(z,"taskCompare"),w.sort(z),K(w,st,V),Ee(F,V,st,i.useMaxWidth),F.append("text").text(r.db.getDiagramTitle()).attr("x",st/2).attr("y",i.titleTopMargin).attr("class","titleText");function K(y,T,x){const g=i.barHeight,n=g+i.barGap,f=i.topPadding,k=i.leftPadding,d=Ie().domain([0,L.length]).range(["#00B9FA","#F95002"]).interpolate(Ye);Z(n,f,k,T,x,y,r.db.getExcludes(),r.db.getIncludes()),Y(k,f,T,x),H(y,n,f,k,g,d,T),p(n,f),m(k,f,T,x)}u(K,"makeGantt");function H(y,T,x,g,n,f,k){y.sort((o,_)=>o.vert===_.vert?0:o.vert?1:-1);const d=y.filter(o=>!o.vert),a=[...new Set(d.map(o=>o.order))].map(o=>d.find(_=>_.order===o));F.append("g").selectAll("rect").data(a).enter().append("rect").attr("x",0).attr("y",function(o,_){return _=o.order,_*T+x-2}).attr("width",function(){return k-i.rightPadding/2}).attr("height",T).attr("class",function(o){for(const[_,S]of L.entries())if(o.type===S)return"section section"+_%i.numberSectionStyles;return"section section0"}).enter();const E=F.append("g").selectAll("rect").data(y).enter(),l=r.db.getLinks();if(E.append("rect").attr("id",function(o){return e+"-"+o.id}).attr("rx",3).attr("ry",3).attr("x",function(o){return o.milestone?D(o.startTime)+g+.5*(D(o.endTime)-D(o.startTime))-.5*n:D(o.startTime)+g}).attr("y",function(o,_){return _=o.order,o.vert?i.gridLineStartPadding:_*T+x}).attr("width",function(o){return o.milestone?n:o.vert?.08*n:D(o.renderEndTime||o.endTime)-D(o.startTime)}).attr("height",function(o){return o.vert?d.length*(i.barHeight+i.barGap)+i.barHeight*2:n}).attr("transform-origin",function(o,_){return _=o.order,(D(o.startTime)+g+.5*(D(o.endTime)-D(o.startTime))).toString()+"px "+(_*T+x+.5*n).toString()+"px"}).attr("class",function(o){const _="task";let S="";o.classes.length>0&&(S=o.classes.join(" "));let W=0;for(const[R,P]of L.entries())o.type===P&&(W=R%i.numberSectionStyles);let C="";return o.active?o.crit?C+=" activeCrit":C=" active":o.done?o.crit?C=" doneCrit":C=" done":o.crit&&(C+=" crit"),C.length===0&&(C=" task"),o.milestone&&(C=" milestone "+C),o.vert&&(C=" vert "+C),C+=W,C+=" "+S,_+C}),E.append("text").attr("id",function(o){return e+"-"+o.id+"-text"}).text(function(o){return o.task}).attr("font-size",i.fontSize).attr("x",function(o){let _=D(o.startTime),S=D(o.renderEndTime||o.endTime);if(o.milestone&&(_+=.5*(D(o.endTime)-D(o.startTime))-.5*n,S=_+n),o.vert)return D(o.startTime)+g;const W=this.getBBox().width;return W>S-_?S+W+1.5*i.leftPadding>k?_+g-5:S+g+5:(S-_)/2+_+g}).attr("y",function(o,_){return o.vert?i.gridLineStartPadding+d.length*(i.barHeight+i.barGap)+60:(_=o.order,_*T+i.barHeight/2+(i.fontSize/2-2)+x)}).attr("text-height",n).attr("class",function(o){const _=D(o.startTime);let S=D(o.endTime);o.milestone&&(S=_+n);const W=this.getBBox().width;let C="";o.classes.length>0&&(C=o.classes.join(" "));let R=0;for(const[mt,_t]of L.entries())o.type===_t&&(R=mt%i.numberSectionStyles);let P="";return o.active&&(o.crit?P="activeCritText"+R:P="activeText"+R),o.done?o.crit?P=P+" doneCritText"+R:P=P+" doneText"+R:o.crit&&(P=P+" critText"+R),o.milestone&&(P+=" milestoneText"),o.vert&&(P+=" vertText"),W>S-_?S+W+1.5*i.leftPadding>k?C+" taskTextOutsideLeft taskTextOutside"+R+" "+P:C+" taskTextOutsideRight taskTextOutside"+R+" "+P+" width-"+W:C+" taskText taskText"+R+" "+P+" width-"+W}),lt().securityLevel==="sandbox"){let o;o=Tt("#i"+e);const _=o.nodes()[0].contentDocument;E.filter(function(S){return l.has(S.id)}).each(function(S){var W=_.querySelector("#"+CSS.escape(e+"-"+S.id)),C=_.querySelector("#"+CSS.escape(e+"-"+S.id+"-text"));const R=W.parentNode;var P=_.createElement("a");P.setAttribute("xlink:href",l.get(S.id)),P.setAttribute("target","_top"),R.appendChild(P),P.appendChild(W),P.appendChild(C)})}}u(H,"drawRects");function Z(y,T,x,g,n,f,k,d){if(k.length===0&&d.length===0)return;let M,a;for(const{startTime:S,endTime:W}of f)(M===void 0||S<M)&&(M=S),(a===void 0||W>a)&&(a=W);if(!M||!a)return;if(G(a).diff(G(M),"year")>5){at.warn("The difference between the min and max time is more than 5 years. This will cause performance issues. Skipping drawing exclude days.");return}const E=r.db.getDateFormat(),l=[];let B=null,o=G(M);for(;o.valueOf()<=a;)r.db.isInvalidDate(o,E,k,d)?B?B.end=o:B={start:o,end:o}:B&&(l.push(B),B=null),o=o.add(1,"d");F.append("g").selectAll("rect").data(l).enter().append("rect").attr("id",S=>e+"-exclude-"+S.start.format("YYYY-MM-DD")).attr("x",S=>D(S.start.startOf("day"))+x).attr("y",i.gridLineStartPadding).attr("width",S=>D(S.end.endOf("day"))-D(S.start.startOf("day"))).attr("height",n-T-i.gridLineStartPadding).attr("transform-origin",function(S,W){return(D(S.start)+x+.5*(D(S.end)-D(S.start))).toString()+"px "+(W*y+.5*n).toString()+"px"}).attr("class","exclude-range")}u(Z,"drawExcludeDays");function X(y,T,x,g){if(x<=0||y>T)return 1/0;const n=T-y,f=G.duration({[g??"day"]:x}).asMilliseconds();return f<=0?1/0:Math.ceil(n/f)}u(X,"getEstimatedTickCount");function Y(y,T,x,g){const n=r.db.getDateFormat(),f=r.db.getAxisFormat();let k;f?k=f:n==="D"?k="%d":k=i.axisFormat??"%Y-%m-%d";let d=$e(D).tickSize(-g+T+i.gridLineStartPadding).tickFormat(Ut(k));const a=/^([1-9]\d*)(millisecond|second|minute|hour|day|week|month)$/.exec(r.db.getTickInterval()||i.tickInterval);if(a!==null){const E=parseInt(a[1],10);if(isNaN(E)||E<=0)at.warn(`Invalid tick interval value: "${a[1]}". Skipping custom tick interval.`);else{const l=a[2],B=r.db.getWeekday()||i.weekday,o=D.domain(),_=o[0],S=o[1],W=X(_,S,E,l);if(W>It)at.warn(`The tick interval "${E}${l}" would generate ${W} ticks, which exceeds the maximum allowed (${It}). This may indicate an invalid date or time range. Skipping custom tick interval.`);else switch(l){case"millisecond":d.ticks(Jt.every(E));break;case"second":d.ticks(Kt.every(E));break;case"minute":d.ticks(Qt.every(E));break;case"hour":d.ticks(Zt.every(E));break;case"day":d.ticks(qt.every(E));break;case"week":d.ticks(ae[B].every(E));break;case"month":d.ticks(Xt.every(E));break}}}if(F.append("g").attr("class","grid").attr("transform","translate("+y+", "+(g-50)+")").call(d).selectAll("text").style("text-anchor","middle").attr("fill","#000").attr("stroke","none").attr("font-size",10).attr("dy","1em"),r.db.topAxisEnabled()||i.topAxis){let E=Re(D).tickSize(-g+T+i.gridLineStartPadding).tickFormat(Ut(k));if(a!==null){const l=parseInt(a[1],10);if(isNaN(l)||l<=0)at.warn(`Invalid tick interval value: "${a[1]}". Skipping custom tick interval.`);else{const B=a[2],o=r.db.getWeekday()||i.weekday,_=D.domain(),S=_[0],W=_[1];if(X(S,W,l,B)<=It)switch(B){case"millisecond":E.ticks(Jt.every(l));break;case"second":E.ticks(Kt.every(l));break;case"minute":E.ticks(Qt.every(l));break;case"hour":E.ticks(Zt.every(l));break;case"day":E.ticks(qt.every(l));break;case"week":E.ticks(ae[o].every(l));break;case"month":E.ticks(Xt.every(l));break}}}F.append("g").attr("class","grid").attr("transform","translate("+y+", "+T+")").call(E).selectAll("text").style("text-anchor","middle").attr("fill","#000").attr("stroke","none").attr("font-size",10)}}u(Y,"makeGrid");function p(y,T){let x=0;const g=Object.keys(A).map(n=>[n,A[n]]);F.append("g").selectAll("text").data(g).enter().append(function(n){const f=n[0].split(ze.lineBreakRegex),k=-(f.length-1)/2,d=b.createElementNS("http://www.w3.org/2000/svg","text");d.setAttribute("dy",k+"em");for(const[M,a]of f.entries()){const E=b.createElementNS("http://www.w3.org/2000/svg","tspan");E.setAttribute("alignment-baseline","central"),E.setAttribute("x","10"),M>0&&E.setAttribute("dy","1em"),E.textContent=a,d.appendChild(E)}return d}).attr("x",10).attr("y",function(n,f){if(f>0)for(let k=0;k<f;k++)return x+=g[f-1][1],n[1]*y/2+x*y+T;else return n[1]*y/2+T}).attr("font-size",i.sectionFontSize).attr("class",function(n){for(const[f,k]of L.entries())if(n[0]===k)return"sectionTitle sectionTitle"+f%i.numberSectionStyles;return"sectionTitle"})}u(p,"vertLabels");function m(y,T,x,g){const n=r.db.getTodayMarker();if(n==="off")return;const f=F.append("g").attr("class","today"),k=new Date,d=f.append("line");d.attr("x1",D(k)+y).attr("x2",D(k)+y).attr("y1",i.titleTopMargin).attr("y2",g-i.titleTopMargin).attr("class","today"),n!==""&&d.attr("style",n.replace(/,/g,";"))}u(m,"drawToday");function $(y){const T={},x=[];for(let g=0,n=y.length;g<n;++g)Object.prototype.hasOwnProperty.call(T,y[g])||(T[y[g]]=!0,x.push(y[g]));return x}u($,"checkUnique")},"draw"),tr={setConf:Qs,draw:Js},er=u(t=>`
  .mermaid-main-font {
        font-family: ${t.fontFamily};
  }

  .exclude-range {
    fill: ${t.excludeBkgColor};
  }

  .section {
    stroke: none;
    opacity: 0.2;
  }

  .section0 {
    fill: ${t.sectionBkgColor};
  }

  .section2 {
    fill: ${t.sectionBkgColor2};
  }

  .section1,
  .section3 {
    fill: ${t.altSectionBkgColor};
    opacity: 0.2;
  }

  .sectionTitle0 {
    fill: ${t.titleColor};
  }

  .sectionTitle1 {
    fill: ${t.titleColor};
  }

  .sectionTitle2 {
    fill: ${t.titleColor};
  }

  .sectionTitle3 {
    fill: ${t.titleColor};
  }

  .sectionTitle {
    text-anchor: start;
    font-family: ${t.fontFamily};
  }


  /* Grid and axis */

  .grid .tick {
    stroke: ${t.gridColor};
    opacity: 0.8;
    shape-rendering: crispEdges;
  }

  .grid .tick text {
    font-family: ${t.fontFamily};
    fill: ${t.textColor};
  }

  .grid path {
    stroke-width: 0;
  }


  /* Today line */

  .today {
    fill: none;
    stroke: ${t.todayLineColor};
    stroke-width: 2px;
  }


  /* Task styling */

  /* Default task */

  .task {
    stroke-width: 2;
  }

  .taskText {
    text-anchor: middle;
    font-family: ${t.fontFamily};
  }

  .taskTextOutsideRight {
    fill: ${t.taskTextDarkColor};
    text-anchor: start;
    font-family: ${t.fontFamily};
  }

  .taskTextOutsideLeft {
    fill: ${t.taskTextDarkColor};
    text-anchor: end;
  }


  /* Special case clickable */

  .task.clickable {
    cursor: pointer;
  }

  .taskText.clickable {
    cursor: pointer;
    fill: ${t.taskTextClickableColor} !important;
    font-weight: bold;
  }

  .taskTextOutsideLeft.clickable {
    cursor: pointer;
    fill: ${t.taskTextClickableColor} !important;
    font-weight: bold;
  }

  .taskTextOutsideRight.clickable {
    cursor: pointer;
    fill: ${t.taskTextClickableColor} !important;
    font-weight: bold;
  }


  /* Specific task settings for the sections*/

  .taskText0,
  .taskText1,
  .taskText2,
  .taskText3 {
    fill: ${t.taskTextColor};
  }

  .task0,
  .task1,
  .task2,
  .task3 {
    fill: ${t.taskBkgColor};
    stroke: ${t.taskBorderColor};
  }

  .taskTextOutside0,
  .taskTextOutside2
  {
    fill: ${t.taskTextOutsideColor};
  }

  .taskTextOutside1,
  .taskTextOutside3 {
    fill: ${t.taskTextOutsideColor};
  }


  /* Active task */

  .active0,
  .active1,
  .active2,
  .active3 {
    fill: ${t.activeTaskBkgColor};
    stroke: ${t.activeTaskBorderColor};
  }

  .activeText0,
  .activeText1,
  .activeText2,
  .activeText3 {
    fill: ${t.taskTextDarkColor} !important;
  }


  /* Completed task */

  .done0,
  .done1,
  .done2,
  .done3 {
    stroke: ${t.doneTaskBorderColor};
    fill: ${t.doneTaskBkgColor};
    stroke-width: 2;
  }

  .doneText0,
  .doneText1,
  .doneText2,
  .doneText3 {
    fill: ${t.taskTextDarkColor} !important;
  }

  /* Done task text displayed outside the bar sits against the diagram background,
     not against the done-task bar, so it must use the outside/contrast color. */
  .doneText0.taskTextOutsideLeft,
  .doneText0.taskTextOutsideRight,
  .doneText1.taskTextOutsideLeft,
  .doneText1.taskTextOutsideRight,
  .doneText2.taskTextOutsideLeft,
  .doneText2.taskTextOutsideRight,
  .doneText3.taskTextOutsideLeft,
  .doneText3.taskTextOutsideRight {
    fill: ${t.taskTextOutsideColor} !important;
  }


  /* Tasks on the critical line */

  .crit0,
  .crit1,
  .crit2,
  .crit3 {
    stroke: ${t.critBorderColor};
    fill: ${t.critBkgColor};
    stroke-width: 2;
  }

  .activeCrit0,
  .activeCrit1,
  .activeCrit2,
  .activeCrit3 {
    stroke: ${t.critBorderColor};
    fill: ${t.activeTaskBkgColor};
    stroke-width: 2;
  }

  .doneCrit0,
  .doneCrit1,
  .doneCrit2,
  .doneCrit3 {
    stroke: ${t.critBorderColor};
    fill: ${t.doneTaskBkgColor};
    stroke-width: 2;
    cursor: pointer;
    shape-rendering: crispEdges;
  }

  .milestone {
    transform: rotate(45deg) scale(0.8,0.8);
  }

  .milestoneText {
    font-style: italic;
  }
  .doneCritText0,
  .doneCritText1,
  .doneCritText2,
  .doneCritText3 {
    fill: ${t.taskTextDarkColor} !important;
  }

  /* Done-crit task text outside the bar — same reasoning as doneText above. */
  .doneCritText0.taskTextOutsideLeft,
  .doneCritText0.taskTextOutsideRight,
  .doneCritText1.taskTextOutsideLeft,
  .doneCritText1.taskTextOutsideRight,
  .doneCritText2.taskTextOutsideLeft,
  .doneCritText2.taskTextOutsideRight,
  .doneCritText3.taskTextOutsideLeft,
  .doneCritText3.taskTextOutsideRight {
    fill: ${t.taskTextOutsideColor} !important;
  }

  .vert {
    stroke: ${t.vertLineColor};
  }

  .vertText {
    font-size: 15px;
    text-anchor: middle;
    fill: ${t.vertLineColor} !important;
  }

  .activeCritText0,
  .activeCritText1,
  .activeCritText2,
  .activeCritText3 {
    fill: ${t.taskTextDarkColor} !important;
  }

  .titleText {
    text-anchor: middle;
    font-size: 18px;
    fill: ${t.titleColor||t.textColor};
    font-family: ${t.fontFamily};
  }
`,"getStyles"),sr=er,ar={parser:hs,db:Zs,renderer:tr,styles:sr};export{ar as diagram};
