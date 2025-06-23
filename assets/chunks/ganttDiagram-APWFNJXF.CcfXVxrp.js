import{_ as c,g as ke,s as me,t as ye,q as ve,a as ge,b as pe,c as nt,d as yt,aw as Te,ax as xe,ay as be,e as we,R as _e,az as De,aA as R,l as pt,aB as Se,aC as Ht,aD as Ut,aE as Ce,aF as Ee,aG as Me,aH as Ie,aI as Ae,aJ as Le,aK as Fe,aL as qt,aM as Xt,aN as jt,aO as Zt,aP as Qt,aQ as Ye,k as We,j as Oe,z as Pe,u as Ve}from"./theme.B6nD8aLc.js";import"./framework.BNvo0jdt.js";var St="day",ze="week",Re="year",Be="YYYY-MM-DDTHH:mm:ssZ",Ne="isoweek";const Ge=function(t,e,r){var a=function(g,D){var p=(D?r.utc:r)().year(g).startOf(Re),E=4-p.isoWeekday();return p.isoWeekday()>4&&(E+=7),p.add(E,St)},s=function(g){return g.add(4-g.isoWeekday(),St)},o=e.prototype;o.isoWeekYear=function(){var v=s(this);return v.year()},o.isoWeek=function(v){if(!this.$utils().u(v))return this.add((v-this.isoWeek())*7,St);var g=s(this),D=a(this.isoWeekYear(),this.$u);return g.diff(D,ze)+1},o.isoWeekday=function(v){return this.$utils().u(v)?this.day()||7:this.day(this.day()%7?v:v-7)};var u=o.startOf;o.startOf=function(v,g){var D=this.$utils(),p=D.u(g)?!0:g,E=D.p(v);return E===Ne?p?this.date(this.date()-(this.isoWeekday()-1)).startOf("day"):this.date(this.date()-1-(this.isoWeekday()-1)+7).endOf("day"):u.bind(this)(v,g)}};var He=function(e){return e.replace(/(\[[^\]]+])|(MMMM|MM|DD|dddd)/g,function(r,a,s){return a||s.slice(1)})},Ue={LTS:"h:mm:ss A",LT:"h:mm A",L:"MM/DD/YYYY",LL:"MMMM D, YYYY",LLL:"MMMM D, YYYY h:mm A",LLLL:"dddd, MMMM D, YYYY h:mm A"},qe=function(e,r){return e.replace(/(\[[^\]]+])|(LTS?|l{1,4}|L{1,4})/g,function(a,s,o){var u=o&&o.toUpperCase();return s||r[o]||Ue[o]||He(r[u])})},Xe=/(\[[^[]*\])|([-_:/.,()\s]+)|(A|a|Q|YYYY|YY?|ww?|MM?M?M?|Do|DD?|hh?|HH?|mm?|ss?|S{1,3}|z|ZZ?)/g,Kt=/\d/,lt=/\d\d/,je=/\d{3}/,Ze=/\d{4}/,U=/\d\d?/,Qe=/[+-]?\d+/,Ke=/[+-]\d\d:?(\d\d)?|Z/,ut=/\d*[^-_:/,()\s\d]+/,rt={},ae=function(e){return e=+e,e+(e>68?1900:2e3)};function $e(t){if(!t||t==="Z")return 0;var e=t.match(/([+-]|\d\d)/g),r=+(e[1]*60)+(+e[2]||0);return r===0?0:e[0]==="+"?-r:r}var V=function(e){return function(r){this[e]=+r}},$t=[Ke,function(t){var e=this.zone||(this.zone={});e.offset=$e(t)}],Ct=function(e){var r=rt[e];return r&&(r.indexOf?r:r.s.concat(r.f))},Jt=function(e,r){var a,s=rt,o=s.meridiem;if(!o)a=e===(r?"pm":"PM");else for(var u=1;u<=24;u+=1)if(e.indexOf(o(u,0,r))>-1){a=u>12;break}return a},Je={A:[ut,function(t){this.afternoon=Jt(t,!1)}],a:[ut,function(t){this.afternoon=Jt(t,!0)}],Q:[Kt,function(t){this.month=(t-1)*3+1}],S:[Kt,function(t){this.milliseconds=+t*100}],SS:[lt,function(t){this.milliseconds=+t*10}],SSS:[je,function(t){this.milliseconds=+t}],s:[U,V("seconds")],ss:[U,V("seconds")],m:[U,V("minutes")],mm:[U,V("minutes")],H:[U,V("hours")],h:[U,V("hours")],HH:[U,V("hours")],hh:[U,V("hours")],D:[U,V("day")],DD:[lt,V("day")],Do:[ut,function(t){var e=rt,r=e.ordinal,a=t.match(/\d+/);if(this.day=a[0],!!r)for(var s=1;s<=31;s+=1)r(s).replace(/\[|\]/g,"")===t&&(this.day=s)}],w:[U,V("week")],ww:[lt,V("week")],M:[U,V("month")],MM:[lt,V("month")],MMM:[ut,function(t){var e=Ct("months"),r=Ct("monthsShort"),a=(r||e.map(function(s){return s.slice(0,3)})).indexOf(t)+1;if(a<1)throw new Error;this.month=a%12||a}],MMMM:[ut,function(t){var e=Ct("months"),r=e.indexOf(t)+1;if(r<1)throw new Error;this.month=r%12||r}],Y:[Qe,V("year")],YY:[lt,function(t){this.year=ae(t)}],YYYY:[Ze,V("year")],Z:$t,ZZ:$t};function tr(t){var e=t.afternoon;if(e!==void 0){var r=t.hours;e?r<12&&(t.hours+=12):r===12&&(t.hours=0),delete t.afternoon}}function er(t){t=qe(t,rt&&rt.formats);for(var e=t.match(Xe),r=e.length,a=0;a<r;a+=1){var s=e[a],o=Je[s],u=o&&o[0],v=o&&o[1];v?e[a]={regex:u,parser:v}:e[a]=s.replace(/^\[|\]$/g,"")}return function(g){for(var D={},p=0,E=0;p<r;p+=1){var O=e[p];if(typeof O=="string")E+=O.length;else{var A=O.regex,F=O.parser,T=g.slice(E),z=A.exec(T),G=z[0];F.call(D,G),g=g.replace(G,"")}}return tr(D),D}}var rr=function(e,r,a,s){try{if(["x","X"].indexOf(r)>-1)return new Date((r==="X"?1e3:1)*e);var o=er(r),u=o(e),v=u.year,g=u.month,D=u.day,p=u.hours,E=u.minutes,O=u.seconds,A=u.milliseconds,F=u.zone,T=u.week,z=new Date,G=D||(!v&&!g?z.getDate():1),j=v||z.getFullYear(),q=0;v&&!g||(q=g>0?g-1:z.getMonth());var Z=p||0,Q=E||0,K=O||0,$=A||0;if(F)return new Date(Date.UTC(j,q,G,Z,Q,K,$+F.offset*60*1e3));if(a)return new Date(Date.UTC(j,q,G,Z,Q,K,$));var y;return y=new Date(j,q,G,Z,Q,K,$),T&&(y=s(y).week(T).toDate()),y}catch{return new Date("")}};const ar=function(t,e,r){r.p.customParseFormat=!0,t&&t.parseTwoDigitYear&&(ae=t.parseTwoDigitYear);var a=e.prototype,s=a.parse;a.parse=function(o){var u=o.date,v=o.utc,g=o.args;this.$u=v;var D=g[1];if(typeof D=="string"){var p=g[2]===!0,E=g[3]===!0,O=p||E,A=g[2];E&&(A=g[2]),rt=this.$locale(),!p&&A&&(rt=r.Ls[A]),this.$d=rr(u,D,v,r),this.init(),A&&A!==!0&&(this.$L=this.locale(A).$L),O&&u!=this.format(D)&&(this.$d=new Date("")),rt={}}else if(D instanceof Array)for(var F=D.length,T=1;T<=F;T+=1){g[1]=D[T-1];var z=r.apply(this,g);if(z.isValid()){this.$d=z.$d,this.$L=z.$L,this.init();break}T===F&&(this.$d=new Date(""))}else s.call(this,o)}},sr=function(t,e){var r=e.prototype,a=r.format;r.format=function(s){var o=this,u=this.$locale();if(!this.isValid())return a.bind(this)(s);var v=this.$utils(),g=s||Be,D=g.replace(/\[([^\]]+)]|Q|wo|ww|w|WW|W|zzz|z|gggg|GGGG|Do|X|x|k{1,2}|S/g,function(p){switch(p){case"Q":return Math.ceil((o.$M+1)/3);case"Do":return u.ordinal(o.$D);case"gggg":return o.weekYear();case"GGGG":return o.isoWeekYear();case"wo":return u.ordinal(o.week(),"W");case"w":case"ww":return v.s(o.week(),p==="w"?1:2,"0");case"W":case"WW":return v.s(o.isoWeek(),p==="W"?1:2,"0");case"k":case"kk":return v.s(String(o.$H===0?24:o.$H),p==="k"?1:2,"0");case"X":return Math.floor(o.$d.getTime()/1e3);case"x":return o.$d.getTime();case"z":return"["+o.offsetName()+"]";case"zzz":return"["+o.offsetName("long")+"]";default:return p}});return a.bind(this)(D)}};var Et=function(){var t=c(function(C,l,f,k){for(f=f||{},k=C.length;k--;f[C[k]]=l);return f},"o"),e=[6,8,10,12,13,14,15,16,17,18,20,21,22,23,24,25,26,27,28,29,30,31,33,35,36,38,40],r=[1,26],a=[1,27],s=[1,28],o=[1,29],u=[1,30],v=[1,31],g=[1,32],D=[1,33],p=[1,34],E=[1,9],O=[1,10],A=[1,11],F=[1,12],T=[1,13],z=[1,14],G=[1,15],j=[1,16],q=[1,19],Z=[1,20],Q=[1,21],K=[1,22],$=[1,23],y=[1,25],w=[1,35],b={trace:c(function(){},"trace"),yy:{},symbols_:{error:2,start:3,gantt:4,document:5,EOF:6,line:7,SPACE:8,statement:9,NL:10,weekday:11,weekday_monday:12,weekday_tuesday:13,weekday_wednesday:14,weekday_thursday:15,weekday_friday:16,weekday_saturday:17,weekday_sunday:18,weekend:19,weekend_friday:20,weekend_saturday:21,dateFormat:22,inclusiveEndDates:23,topAxis:24,axisFormat:25,tickInterval:26,excludes:27,includes:28,todayMarker:29,title:30,acc_title:31,acc_title_value:32,acc_descr:33,acc_descr_value:34,acc_descr_multiline_value:35,section:36,clickStatement:37,taskTxt:38,taskData:39,click:40,callbackname:41,callbackargs:42,href:43,clickStatementDebug:44,$accept:0,$end:1},terminals_:{2:"error",4:"gantt",6:"EOF",8:"SPACE",10:"NL",12:"weekday_monday",13:"weekday_tuesday",14:"weekday_wednesday",15:"weekday_thursday",16:"weekday_friday",17:"weekday_saturday",18:"weekday_sunday",20:"weekend_friday",21:"weekend_saturday",22:"dateFormat",23:"inclusiveEndDates",24:"topAxis",25:"axisFormat",26:"tickInterval",27:"excludes",28:"includes",29:"todayMarker",30:"title",31:"acc_title",32:"acc_title_value",33:"acc_descr",34:"acc_descr_value",35:"acc_descr_multiline_value",36:"section",38:"taskTxt",39:"taskData",40:"click",41:"callbackname",42:"callbackargs",43:"href"},productions_:[0,[3,3],[5,0],[5,2],[7,2],[7,1],[7,1],[7,1],[11,1],[11,1],[11,1],[11,1],[11,1],[11,1],[11,1],[19,1],[19,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,2],[9,2],[9,1],[9,1],[9,1],[9,2],[37,2],[37,3],[37,3],[37,4],[37,3],[37,4],[37,2],[44,2],[44,3],[44,3],[44,4],[44,3],[44,4],[44,2]],performAction:c(function(l,f,k,h,x,n,d){var i=n.length-1;switch(x){case 1:return n[i-1];case 2:this.$=[];break;case 3:n[i-1].push(n[i]),this.$=n[i-1];break;case 4:case 5:this.$=n[i];break;case 6:case 7:this.$=[];break;case 8:h.setWeekday("monday");break;case 9:h.setWeekday("tuesday");break;case 10:h.setWeekday("wednesday");break;case 11:h.setWeekday("thursday");break;case 12:h.setWeekday("friday");break;case 13:h.setWeekday("saturday");break;case 14:h.setWeekday("sunday");break;case 15:h.setWeekend("friday");break;case 16:h.setWeekend("saturday");break;case 17:h.setDateFormat(n[i].substr(11)),this.$=n[i].substr(11);break;case 18:h.enableInclusiveEndDates(),this.$=n[i].substr(18);break;case 19:h.TopAxis(),this.$=n[i].substr(8);break;case 20:h.setAxisFormat(n[i].substr(11)),this.$=n[i].substr(11);break;case 21:h.setTickInterval(n[i].substr(13)),this.$=n[i].substr(13);break;case 22:h.setExcludes(n[i].substr(9)),this.$=n[i].substr(9);break;case 23:h.setIncludes(n[i].substr(9)),this.$=n[i].substr(9);break;case 24:h.setTodayMarker(n[i].substr(12)),this.$=n[i].substr(12);break;case 27:h.setDiagramTitle(n[i].substr(6)),this.$=n[i].substr(6);break;case 28:this.$=n[i].trim(),h.setAccTitle(this.$);break;case 29:case 30:this.$=n[i].trim(),h.setAccDescription(this.$);break;case 31:h.addSection(n[i].substr(8)),this.$=n[i].substr(8);break;case 33:h.addTask(n[i-1],n[i]),this.$="task";break;case 34:this.$=n[i-1],h.setClickEvent(n[i-1],n[i],null);break;case 35:this.$=n[i-2],h.setClickEvent(n[i-2],n[i-1],n[i]);break;case 36:this.$=n[i-2],h.setClickEvent(n[i-2],n[i-1],null),h.setLink(n[i-2],n[i]);break;case 37:this.$=n[i-3],h.setClickEvent(n[i-3],n[i-2],n[i-1]),h.setLink(n[i-3],n[i]);break;case 38:this.$=n[i-2],h.setClickEvent(n[i-2],n[i],null),h.setLink(n[i-2],n[i-1]);break;case 39:this.$=n[i-3],h.setClickEvent(n[i-3],n[i-1],n[i]),h.setLink(n[i-3],n[i-2]);break;case 40:this.$=n[i-1],h.setLink(n[i-1],n[i]);break;case 41:case 47:this.$=n[i-1]+" "+n[i];break;case 42:case 43:case 45:this.$=n[i-2]+" "+n[i-1]+" "+n[i];break;case 44:case 46:this.$=n[i-3]+" "+n[i-2]+" "+n[i-1]+" "+n[i];break}},"anonymous"),table:[{3:1,4:[1,2]},{1:[3]},t(e,[2,2],{5:3}),{6:[1,4],7:5,8:[1,6],9:7,10:[1,8],11:17,12:r,13:a,14:s,15:o,16:u,17:v,18:g,19:18,20:D,21:p,22:E,23:O,24:A,25:F,26:T,27:z,28:G,29:j,30:q,31:Z,33:Q,35:K,36:$,37:24,38:y,40:w},t(e,[2,7],{1:[2,1]}),t(e,[2,3]),{9:36,11:17,12:r,13:a,14:s,15:o,16:u,17:v,18:g,19:18,20:D,21:p,22:E,23:O,24:A,25:F,26:T,27:z,28:G,29:j,30:q,31:Z,33:Q,35:K,36:$,37:24,38:y,40:w},t(e,[2,5]),t(e,[2,6]),t(e,[2,17]),t(e,[2,18]),t(e,[2,19]),t(e,[2,20]),t(e,[2,21]),t(e,[2,22]),t(e,[2,23]),t(e,[2,24]),t(e,[2,25]),t(e,[2,26]),t(e,[2,27]),{32:[1,37]},{34:[1,38]},t(e,[2,30]),t(e,[2,31]),t(e,[2,32]),{39:[1,39]},t(e,[2,8]),t(e,[2,9]),t(e,[2,10]),t(e,[2,11]),t(e,[2,12]),t(e,[2,13]),t(e,[2,14]),t(e,[2,15]),t(e,[2,16]),{41:[1,40],43:[1,41]},t(e,[2,4]),t(e,[2,28]),t(e,[2,29]),t(e,[2,33]),t(e,[2,34],{42:[1,42],43:[1,43]}),t(e,[2,40],{41:[1,44]}),t(e,[2,35],{43:[1,45]}),t(e,[2,36]),t(e,[2,38],{42:[1,46]}),t(e,[2,37]),t(e,[2,39])],defaultActions:{},parseError:c(function(l,f){if(f.recoverable)this.trace(l);else{var k=new Error(l);throw k.hash=f,k}},"parseError"),parse:c(function(l){var f=this,k=[0],h=[],x=[null],n=[],d=this.table,i="",I=0,S=0,M=2,P=1,L=n.slice.call(arguments,1),Y=Object.create(this.lexer),J={yy:{}};for(var bt in this.yy)Object.prototype.hasOwnProperty.call(this.yy,bt)&&(J.yy[bt]=this.yy[bt]);Y.setInput(l,J.yy),J.yy.lexer=Y,J.yy.parser=this,typeof Y.yylloc>"u"&&(Y.yylloc={});var wt=Y.yylloc;n.push(wt);var fe=Y.options&&Y.options.ranges;typeof J.yy.parseError=="function"?this.parseError=J.yy.parseError:this.parseError=Object.getPrototypeOf(this).parseError;function he(N){k.length=k.length-2*N,x.length=x.length-N,n.length=n.length-N}c(he,"popStack");function Nt(){var N;return N=h.pop()||Y.lex()||P,typeof N!="number"&&(N instanceof Array&&(h=N,N=h.pop()),N=f.symbols_[N]||N),N}c(Nt,"lex");for(var B,at,H,_t,it={},kt,tt,Gt,mt;;){if(at=k[k.length-1],this.defaultActions[at]?H=this.defaultActions[at]:((B===null||typeof B>"u")&&(B=Nt()),H=d[at]&&d[at][B]),typeof H>"u"||!H.length||!H[0]){var Dt="";mt=[];for(kt in d[at])this.terminals_[kt]&&kt>M&&mt.push("'"+this.terminals_[kt]+"'");Y.showPosition?Dt="Parse error on line "+(I+1)+`:
`+Y.showPosition()+`
Expecting `+mt.join(", ")+", got '"+(this.terminals_[B]||B)+"'":Dt="Parse error on line "+(I+1)+": Unexpected "+(B==P?"end of input":"'"+(this.terminals_[B]||B)+"'"),this.parseError(Dt,{text:Y.match,token:this.terminals_[B]||B,line:Y.yylineno,loc:wt,expected:mt})}if(H[0]instanceof Array&&H.length>1)throw new Error("Parse Error: multiple actions possible at state: "+at+", token: "+B);switch(H[0]){case 1:k.push(B),x.push(Y.yytext),n.push(Y.yylloc),k.push(H[1]),B=null,S=Y.yyleng,i=Y.yytext,I=Y.yylineno,wt=Y.yylloc;break;case 2:if(tt=this.productions_[H[1]][1],it.$=x[x.length-tt],it._$={first_line:n[n.length-(tt||1)].first_line,last_line:n[n.length-1].last_line,first_column:n[n.length-(tt||1)].first_column,last_column:n[n.length-1].last_column},fe&&(it._$.range=[n[n.length-(tt||1)].range[0],n[n.length-1].range[1]]),_t=this.performAction.apply(it,[i,S,I,J.yy,H[1],x,n].concat(L)),typeof _t<"u")return _t;tt&&(k=k.slice(0,-1*tt*2),x=x.slice(0,-1*tt),n=n.slice(0,-1*tt)),k.push(this.productions_[H[1]][0]),x.push(it.$),n.push(it._$),Gt=d[k[k.length-2]][k[k.length-1]],k.push(Gt);break;case 3:return!0}}return!0},"parse")},_=function(){var C={EOF:1,parseError:c(function(f,k){if(this.yy.parser)this.yy.parser.parseError(f,k);else throw new Error(f)},"parseError"),setInput:c(function(l,f){return this.yy=f||this.yy||{},this._input=l,this._more=this._backtrack=this.done=!1,this.yylineno=this.yyleng=0,this.yytext=this.matched=this.match="",this.conditionStack=["INITIAL"],this.yylloc={first_line:1,first_column:0,last_line:1,last_column:0},this.options.ranges&&(this.yylloc.range=[0,0]),this.offset=0,this},"setInput"),input:c(function(){var l=this._input[0];this.yytext+=l,this.yyleng++,this.offset++,this.match+=l,this.matched+=l;var f=l.match(/(?:\r\n?|\n).*/g);return f?(this.yylineno++,this.yylloc.last_line++):this.yylloc.last_column++,this.options.ranges&&this.yylloc.range[1]++,this._input=this._input.slice(1),l},"input"),unput:c(function(l){var f=l.length,k=l.split(/(?:\r\n?|\n)/g);this._input=l+this._input,this.yytext=this.yytext.substr(0,this.yytext.length-f),this.offset-=f;var h=this.match.split(/(?:\r\n?|\n)/g);this.match=this.match.substr(0,this.match.length-1),this.matched=this.matched.substr(0,this.matched.length-1),k.length-1&&(this.yylineno-=k.length-1);var x=this.yylloc.range;return this.yylloc={first_line:this.yylloc.first_line,last_line:this.yylineno+1,first_column:this.yylloc.first_column,last_column:k?(k.length===h.length?this.yylloc.first_column:0)+h[h.length-k.length].length-k[0].length:this.yylloc.first_column-f},this.options.ranges&&(this.yylloc.range=[x[0],x[0]+this.yyleng-f]),this.yyleng=this.yytext.length,this},"unput"),more:c(function(){return this._more=!0,this},"more"),reject:c(function(){if(this.options.backtrack_lexer)this._backtrack=!0;else return this.parseError("Lexical error on line "+(this.yylineno+1)+`. You can only invoke reject() in the lexer when the lexer is of the backtracking persuasion (options.backtrack_lexer = true).
`+this.showPosition(),{text:"",token:null,line:this.yylineno});return this},"reject"),less:c(function(l){this.unput(this.match.slice(l))},"less"),pastInput:c(function(){var l=this.matched.substr(0,this.matched.length-this.match.length);return(l.length>20?"...":"")+l.substr(-20).replace(/\n/g,"")},"pastInput"),upcomingInput:c(function(){var l=this.match;return l.length<20&&(l+=this._input.substr(0,20-l.length)),(l.substr(0,20)+(l.length>20?"...":"")).replace(/\n/g,"")},"upcomingInput"),showPosition:c(function(){var l=this.pastInput(),f=new Array(l.length+1).join("-");return l+this.upcomingInput()+`
`+f+"^"},"showPosition"),test_match:c(function(l,f){var k,h,x;if(this.options.backtrack_lexer&&(x={yylineno:this.yylineno,yylloc:{first_line:this.yylloc.first_line,last_line:this.last_line,first_column:this.yylloc.first_column,last_column:this.yylloc.last_column},yytext:this.yytext,match:this.match,matches:this.matches,matched:this.matched,yyleng:this.yyleng,offset:this.offset,_more:this._more,_input:this._input,yy:this.yy,conditionStack:this.conditionStack.slice(0),done:this.done},this.options.ranges&&(x.yylloc.range=this.yylloc.range.slice(0))),h=l[0].match(/(?:\r\n?|\n).*/g),h&&(this.yylineno+=h.length),this.yylloc={first_line:this.yylloc.last_line,last_line:this.yylineno+1,first_column:this.yylloc.last_column,last_column:h?h[h.length-1].length-h[h.length-1].match(/\r?\n?/)[0].length:this.yylloc.last_column+l[0].length},this.yytext+=l[0],this.match+=l[0],this.matches=l,this.yyleng=this.yytext.length,this.options.ranges&&(this.yylloc.range=[this.offset,this.offset+=this.yyleng]),this._more=!1,this._backtrack=!1,this._input=this._input.slice(l[0].length),this.matched+=l[0],k=this.performAction.call(this,this.yy,this,f,this.conditionStack[this.conditionStack.length-1]),this.done&&this._input&&(this.done=!1),k)return k;if(this._backtrack){for(var n in x)this[n]=x[n];return!1}return!1},"test_match"),next:c(function(){if(this.done)return this.EOF;this._input||(this.done=!0);var l,f,k,h;this._more||(this.yytext="",this.match="");for(var x=this._currentRules(),n=0;n<x.length;n++)if(k=this._input.match(this.rules[x[n]]),k&&(!f||k[0].length>f[0].length)){if(f=k,h=n,this.options.backtrack_lexer){if(l=this.test_match(k,x[n]),l!==!1)return l;if(this._backtrack){f=!1;continue}else return!1}else if(!this.options.flex)break}return f?(l=this.test_match(f,x[h]),l!==!1?l:!1):this._input===""?this.EOF:this.parseError("Lexical error on line "+(this.yylineno+1)+`. Unrecognized text.
`+this.showPosition(),{text:"",token:null,line:this.yylineno})},"next"),lex:c(function(){var f=this.next();return f||this.lex()},"lex"),begin:c(function(f){this.conditionStack.push(f)},"begin"),popState:c(function(){var f=this.conditionStack.length-1;return f>0?this.conditionStack.pop():this.conditionStack[0]},"popState"),_currentRules:c(function(){return this.conditionStack.length&&this.conditionStack[this.conditionStack.length-1]?this.conditions[this.conditionStack[this.conditionStack.length-1]].rules:this.conditions.INITIAL.rules},"_currentRules"),topState:c(function(f){return f=this.conditionStack.length-1-Math.abs(f||0),f>=0?this.conditionStack[f]:"INITIAL"},"topState"),pushState:c(function(f){this.begin(f)},"pushState"),stateStackSize:c(function(){return this.conditionStack.length},"stateStackSize"),options:{"case-insensitive":!0},performAction:c(function(f,k,h,x){switch(h){case 0:return this.begin("open_directive"),"open_directive";case 1:return this.begin("acc_title"),31;case 2:return this.popState(),"acc_title_value";case 3:return this.begin("acc_descr"),33;case 4:return this.popState(),"acc_descr_value";case 5:this.begin("acc_descr_multiline");break;case 6:this.popState();break;case 7:return"acc_descr_multiline_value";case 8:break;case 9:break;case 10:break;case 11:return 10;case 12:break;case 13:break;case 14:this.begin("href");break;case 15:this.popState();break;case 16:return 43;case 17:this.begin("callbackname");break;case 18:this.popState();break;case 19:this.popState(),this.begin("callbackargs");break;case 20:return 41;case 21:this.popState();break;case 22:return 42;case 23:this.begin("click");break;case 24:this.popState();break;case 25:return 40;case 26:return 4;case 27:return 22;case 28:return 23;case 29:return 24;case 30:return 25;case 31:return 26;case 32:return 28;case 33:return 27;case 34:return 29;case 35:return 12;case 36:return 13;case 37:return 14;case 38:return 15;case 39:return 16;case 40:return 17;case 41:return 18;case 42:return 20;case 43:return 21;case 44:return"date";case 45:return 30;case 46:return"accDescription";case 47:return 36;case 48:return 38;case 49:return 39;case 50:return":";case 51:return 6;case 52:return"INVALID"}},"anonymous"),rules:[/^(?:%%\{)/i,/^(?:accTitle\s*:\s*)/i,/^(?:(?!\n||)*[^\n]*)/i,/^(?:accDescr\s*:\s*)/i,/^(?:(?!\n||)*[^\n]*)/i,/^(?:accDescr\s*\{\s*)/i,/^(?:[\}])/i,/^(?:[^\}]*)/i,/^(?:%%(?!\{)*[^\n]*)/i,/^(?:[^\}]%%*[^\n]*)/i,/^(?:%%*[^\n]*[\n]*)/i,/^(?:[\n]+)/i,/^(?:\s+)/i,/^(?:%[^\n]*)/i,/^(?:href[\s]+["])/i,/^(?:["])/i,/^(?:[^"]*)/i,/^(?:call[\s]+)/i,/^(?:\([\s]*\))/i,/^(?:\()/i,/^(?:[^(]*)/i,/^(?:\))/i,/^(?:[^)]*)/i,/^(?:click[\s]+)/i,/^(?:[\s\n])/i,/^(?:[^\s\n]*)/i,/^(?:gantt\b)/i,/^(?:dateFormat\s[^#\n;]+)/i,/^(?:inclusiveEndDates\b)/i,/^(?:topAxis\b)/i,/^(?:axisFormat\s[^#\n;]+)/i,/^(?:tickInterval\s[^#\n;]+)/i,/^(?:includes\s[^#\n;]+)/i,/^(?:excludes\s[^#\n;]+)/i,/^(?:todayMarker\s[^\n;]+)/i,/^(?:weekday\s+monday\b)/i,/^(?:weekday\s+tuesday\b)/i,/^(?:weekday\s+wednesday\b)/i,/^(?:weekday\s+thursday\b)/i,/^(?:weekday\s+friday\b)/i,/^(?:weekday\s+saturday\b)/i,/^(?:weekday\s+sunday\b)/i,/^(?:weekend\s+friday\b)/i,/^(?:weekend\s+saturday\b)/i,/^(?:\d\d\d\d-\d\d-\d\d\b)/i,/^(?:title\s[^\n]+)/i,/^(?:accDescription\s[^#\n;]+)/i,/^(?:section\s[^\n]+)/i,/^(?:[^:\n]+)/i,/^(?::[^#\n;]+)/i,/^(?::)/i,/^(?:$)/i,/^(?:.)/i],conditions:{acc_descr_multiline:{rules:[6,7],inclusive:!1},acc_descr:{rules:[4],inclusive:!1},acc_title:{rules:[2],inclusive:!1},callbackargs:{rules:[21,22],inclusive:!1},callbackname:{rules:[18,19,20],inclusive:!1},href:{rules:[15,16],inclusive:!1},click:{rules:[24,25],inclusive:!1},INITIAL:{rules:[0,1,3,5,8,9,10,11,12,13,14,17,23,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52],inclusive:!0}}};return C}();b.lexer=_;function m(){this.yy={}}return c(m,"Parser"),m.prototype=b,b.Parser=m,new m}();Et.parser=Et;var ir=Et;R.extend(Ge);R.extend(ar);R.extend(sr);var te={friday:5,saturday:6},X="",Lt="",Ft=void 0,Yt="",dt=[],ft=[],Wt=new Map,Ot=[],Tt=[],ct="",Pt="",se=["active","done","crit","milestone"],Vt=[],ht=!1,zt=!1,Rt="sunday",xt="saturday",Mt=0,nr=c(function(){Ot=[],Tt=[],ct="",Vt=[],vt=0,At=void 0,gt=void 0,W=[],X="",Lt="",Pt="",Ft=void 0,Yt="",dt=[],ft=[],ht=!1,zt=!1,Mt=0,Wt=new Map,Pe(),Rt="sunday",xt="saturday"},"clear"),or=c(function(t){Lt=t},"setAxisFormat"),cr=c(function(){return Lt},"getAxisFormat"),lr=c(function(t){Ft=t},"setTickInterval"),ur=c(function(){return Ft},"getTickInterval"),dr=c(function(t){Yt=t},"setTodayMarker"),fr=c(function(){return Yt},"getTodayMarker"),hr=c(function(t){X=t},"setDateFormat"),kr=c(function(){ht=!0},"enableInclusiveEndDates"),mr=c(function(){return ht},"endDatesAreInclusive"),yr=c(function(){zt=!0},"enableTopAxis"),vr=c(function(){return zt},"topAxisEnabled"),gr=c(function(t){Pt=t},"setDisplayMode"),pr=c(function(){return Pt},"getDisplayMode"),Tr=c(function(){return X},"getDateFormat"),xr=c(function(t){dt=t.toLowerCase().split(/[\s,]+/)},"setIncludes"),br=c(function(){return dt},"getIncludes"),wr=c(function(t){ft=t.toLowerCase().split(/[\s,]+/)},"setExcludes"),_r=c(function(){return ft},"getExcludes"),Dr=c(function(){return Wt},"getLinks"),Sr=c(function(t){ct=t,Ot.push(t)},"addSection"),Cr=c(function(){return Ot},"getSections"),Er=c(function(){let t=ee();const e=10;let r=0;for(;!t&&r<e;)t=ee(),r++;return Tt=W,Tt},"getTasks"),ie=c(function(t,e,r,a){return a.includes(t.format(e.trim()))?!1:r.includes("weekends")&&(t.isoWeekday()===te[xt]||t.isoWeekday()===te[xt]+1)||r.includes(t.format("dddd").toLowerCase())?!0:r.includes(t.format(e.trim()))},"isInvalidDate"),Mr=c(function(t){Rt=t},"setWeekday"),Ir=c(function(){return Rt},"getWeekday"),Ar=c(function(t){xt=t},"setWeekend"),ne=c(function(t,e,r,a){if(!r.length||t.manualEndTime)return;let s;t.startTime instanceof Date?s=R(t.startTime):s=R(t.startTime,e,!0),s=s.add(1,"d");let o;t.endTime instanceof Date?o=R(t.endTime):o=R(t.endTime,e,!0);const[u,v]=Lr(s,o,e,r,a);t.endTime=u.toDate(),t.renderEndTime=v},"checkTaskDates"),Lr=c(function(t,e,r,a,s){let o=!1,u=null;for(;t<=e;)o||(u=e.toDate()),o=ie(t,r,a,s),o&&(e=e.add(1,"d")),t=t.add(1,"d");return[e,u]},"fixTaskDates"),It=c(function(t,e,r){r=r.trim();const s=/^after\s+(?<ids>[\d\w- ]+)/.exec(r);if(s!==null){let u=null;for(const g of s.groups.ids.split(" ")){let D=st(g);D!==void 0&&(!u||D.endTime>u.endTime)&&(u=D)}if(u)return u.endTime;const v=new Date;return v.setHours(0,0,0,0),v}let o=R(r,e.trim(),!0);if(o.isValid())return o.toDate();{pt.debug("Invalid date:"+r),pt.debug("With date format:"+e.trim());const u=new Date(r);if(u===void 0||isNaN(u.getTime())||u.getFullYear()<-1e4||u.getFullYear()>1e4)throw new Error("Invalid date:"+r);return u}},"getStartDate"),oe=c(function(t){const e=/^(\d+(?:\.\d+)?)([Mdhmswy]|ms)$/.exec(t.trim());return e!==null?[Number.parseFloat(e[1]),e[2]]:[NaN,"ms"]},"parseDuration"),ce=c(function(t,e,r,a=!1){r=r.trim();const o=/^until\s+(?<ids>[\d\w- ]+)/.exec(r);if(o!==null){let p=null;for(const O of o.groups.ids.split(" ")){let A=st(O);A!==void 0&&(!p||A.startTime<p.startTime)&&(p=A)}if(p)return p.startTime;const E=new Date;return E.setHours(0,0,0,0),E}let u=R(r,e.trim(),!0);if(u.isValid())return a&&(u=u.add(1,"d")),u.toDate();let v=R(t);const[g,D]=oe(r);if(!Number.isNaN(g)){const p=v.add(g,D);p.isValid()&&(v=p)}return v.toDate()},"getEndDate"),vt=0,ot=c(function(t){return t===void 0?(vt=vt+1,"task"+vt):t},"parseId"),Fr=c(function(t,e){let r;e.substr(0,1)===":"?r=e.substr(1,e.length):r=e;const a=r.split(","),s={};Bt(a,s,se);for(let u=0;u<a.length;u++)a[u]=a[u].trim();let o="";switch(a.length){case 1:s.id=ot(),s.startTime=t.endTime,o=a[0];break;case 2:s.id=ot(),s.startTime=It(void 0,X,a[0]),o=a[1];break;case 3:s.id=ot(a[0]),s.startTime=It(void 0,X,a[1]),o=a[2];break}return o&&(s.endTime=ce(s.startTime,X,o,ht),s.manualEndTime=R(o,"YYYY-MM-DD",!0).isValid(),ne(s,X,ft,dt)),s},"compileData"),Yr=c(function(t,e){let r;e.substr(0,1)===":"?r=e.substr(1,e.length):r=e;const a=r.split(","),s={};Bt(a,s,se);for(let o=0;o<a.length;o++)a[o]=a[o].trim();switch(a.length){case 1:s.id=ot(),s.startTime={type:"prevTaskEnd",id:t},s.endTime={data:a[0]};break;case 2:s.id=ot(),s.startTime={type:"getStartDate",startData:a[0]},s.endTime={data:a[1]};break;case 3:s.id=ot(a[0]),s.startTime={type:"getStartDate",startData:a[1]},s.endTime={data:a[2]};break}return s},"parseData"),At,gt,W=[],le={},Wr=c(function(t,e){const r={section:ct,type:ct,processed:!1,manualEndTime:!1,renderEndTime:null,raw:{data:e},task:t,classes:[]},a=Yr(gt,e);r.raw.startTime=a.startTime,r.raw.endTime=a.endTime,r.id=a.id,r.prevTaskId=gt,r.active=a.active,r.done=a.done,r.crit=a.crit,r.milestone=a.milestone,r.order=Mt,Mt++;const s=W.push(r);gt=r.id,le[r.id]=s-1},"addTask"),st=c(function(t){const e=le[t];return W[e]},"findTaskById"),Or=c(function(t,e){const r={section:ct,type:ct,description:t,task:t,classes:[]},a=Fr(At,e);r.startTime=a.startTime,r.endTime=a.endTime,r.id=a.id,r.active=a.active,r.done=a.done,r.crit=a.crit,r.milestone=a.milestone,At=r,Tt.push(r)},"addTaskOrg"),ee=c(function(){const t=c(function(r){const a=W[r];let s="";switch(W[r].raw.startTime.type){case"prevTaskEnd":{const o=st(a.prevTaskId);a.startTime=o.endTime;break}case"getStartDate":s=It(void 0,X,W[r].raw.startTime.startData),s&&(W[r].startTime=s);break}return W[r].startTime&&(W[r].endTime=ce(W[r].startTime,X,W[r].raw.endTime.data,ht),W[r].endTime&&(W[r].processed=!0,W[r].manualEndTime=R(W[r].raw.endTime.data,"YYYY-MM-DD",!0).isValid(),ne(W[r],X,ft,dt))),W[r].processed},"compileTask");let e=!0;for(const[r,a]of W.entries())t(r),e=e&&a.processed;return e},"compileTasks"),Pr=c(function(t,e){let r=e;nt().securityLevel!=="loose"&&(r=Oe(e)),t.split(",").forEach(function(a){st(a)!==void 0&&(de(a,()=>{window.open(r,"_self")}),Wt.set(a,r))}),ue(t,"clickable")},"setLink"),ue=c(function(t,e){t.split(",").forEach(function(r){let a=st(r);a!==void 0&&a.classes.push(e)})},"setClass"),Vr=c(function(t,e,r){if(nt().securityLevel!=="loose"||e===void 0)return;let a=[];if(typeof r=="string"){a=r.split(/,(?=(?:(?:[^"]*"){2})*[^"]*$)/);for(let o=0;o<a.length;o++){let u=a[o].trim();u.startsWith('"')&&u.endsWith('"')&&(u=u.substr(1,u.length-2)),a[o]=u}}a.length===0&&a.push(t),st(t)!==void 0&&de(t,()=>{Ve.runFunc(e,...a)})},"setClickFun"),de=c(function(t,e){Vt.push(function(){const r=document.querySelector(`[id="${t}"]`);r!==null&&r.addEventListener("click",function(){e()})},function(){const r=document.querySelector(`[id="${t}-text"]`);r!==null&&r.addEventListener("click",function(){e()})})},"pushFun"),zr=c(function(t,e,r){t.split(",").forEach(function(a){Vr(a,e,r)}),ue(t,"clickable")},"setClickEvent"),Rr=c(function(t){Vt.forEach(function(e){e(t)})},"bindFunctions"),Br={getConfig:c(()=>nt().gantt,"getConfig"),clear:nr,setDateFormat:hr,getDateFormat:Tr,enableInclusiveEndDates:kr,endDatesAreInclusive:mr,enableTopAxis:yr,topAxisEnabled:vr,setAxisFormat:or,getAxisFormat:cr,setTickInterval:lr,getTickInterval:ur,setTodayMarker:dr,getTodayMarker:fr,setAccTitle:pe,getAccTitle:ge,setDiagramTitle:ve,getDiagramTitle:ye,setDisplayMode:gr,getDisplayMode:pr,setAccDescription:me,getAccDescription:ke,addSection:Sr,getSections:Cr,getTasks:Er,addTask:Wr,findTaskById:st,addTaskOrg:Or,setIncludes:xr,getIncludes:br,setExcludes:wr,getExcludes:_r,setClickEvent:zr,setLink:Pr,getLinks:Dr,bindFunctions:Rr,parseDuration:oe,isInvalidDate:ie,setWeekday:Mr,getWeekday:Ir,setWeekend:Ar};function Bt(t,e,r){let a=!0;for(;a;)a=!1,r.forEach(function(s){const o="^\\s*"+s+"\\s*$",u=new RegExp(o);t[0].match(u)&&(e[s]=!0,t.shift(1),a=!0)})}c(Bt,"getTaskTags");var Nr=c(function(){pt.debug("Something is calling, setConf, remove the call")},"setConf"),re={monday:Fe,tuesday:Le,wednesday:Ae,thursday:Ie,friday:Me,saturday:Ee,sunday:Ce},Gr=c((t,e)=>{let r=[...t].map(()=>-1/0),a=[...t].sort((o,u)=>o.startTime-u.startTime||o.order-u.order),s=0;for(const o of a)for(let u=0;u<r.length;u++)if(o.startTime>=r[u]){r[u]=o.endTime,o.order=u+e,u>s&&(s=u);break}return s},"getMaxIntersections"),et,Hr=c(function(t,e,r,a){const s=nt().gantt,o=nt().securityLevel;let u;o==="sandbox"&&(u=yt("#i"+e));const v=o==="sandbox"?yt(u.nodes()[0].contentDocument.body):yt("body"),g=o==="sandbox"?u.nodes()[0].contentDocument:document,D=g.getElementById(e);et=D.parentElement.offsetWidth,et===void 0&&(et=1200),s.useWidth!==void 0&&(et=s.useWidth);const p=a.db.getTasks();let E=[];for(const y of p)E.push(y.type);E=$(E);const O={};let A=2*s.topPadding;if(a.db.getDisplayMode()==="compact"||s.displayMode==="compact"){const y={};for(const b of p)y[b.section]===void 0?y[b.section]=[b]:y[b.section].push(b);let w=0;for(const b of Object.keys(y)){const _=Gr(y[b],w)+1;w+=_,A+=_*(s.barHeight+s.barGap),O[b]=_}}else{A+=p.length*(s.barHeight+s.barGap);for(const y of E)O[y]=p.filter(w=>w.type===y).length}D.setAttribute("viewBox","0 0 "+et+" "+A);const F=v.select(`[id="${e}"]`),T=Te().domain([xe(p,function(y){return y.startTime}),be(p,function(y){return y.endTime})]).rangeRound([0,et-s.leftPadding-s.rightPadding]);function z(y,w){const b=y.startTime,_=w.startTime;let m=0;return b>_?m=1:b<_&&(m=-1),m}c(z,"taskCompare"),p.sort(z),G(p,et,A),we(F,A,et,s.useMaxWidth),F.append("text").text(a.db.getDiagramTitle()).attr("x",et/2).attr("y",s.titleTopMargin).attr("class","titleText");function G(y,w,b){const _=s.barHeight,m=_+s.barGap,C=s.topPadding,l=s.leftPadding,f=_e().domain([0,E.length]).range(["#00B9FA","#F95002"]).interpolate(De);q(m,C,l,w,b,y,a.db.getExcludes(),a.db.getIncludes()),Z(l,C,w,b),j(y,m,C,l,_,f,w),Q(m,C),K(l,C,w,b)}c(G,"makeGantt");function j(y,w,b,_,m,C,l){const k=[...new Set(y.map(d=>d.order))].map(d=>y.find(i=>i.order===d));F.append("g").selectAll("rect").data(k).enter().append("rect").attr("x",0).attr("y",function(d,i){return i=d.order,i*w+b-2}).attr("width",function(){return l-s.rightPadding/2}).attr("height",w).attr("class",function(d){for(const[i,I]of E.entries())if(d.type===I)return"section section"+i%s.numberSectionStyles;return"section section0"});const h=F.append("g").selectAll("rect").data(y).enter(),x=a.db.getLinks();if(h.append("rect").attr("id",function(d){return d.id}).attr("rx",3).attr("ry",3).attr("x",function(d){return d.milestone?T(d.startTime)+_+.5*(T(d.endTime)-T(d.startTime))-.5*m:T(d.startTime)+_}).attr("y",function(d,i){return i=d.order,i*w+b}).attr("width",function(d){return d.milestone?m:T(d.renderEndTime||d.endTime)-T(d.startTime)}).attr("height",m).attr("transform-origin",function(d,i){return i=d.order,(T(d.startTime)+_+.5*(T(d.endTime)-T(d.startTime))).toString()+"px "+(i*w+b+.5*m).toString()+"px"}).attr("class",function(d){const i="task";let I="";d.classes.length>0&&(I=d.classes.join(" "));let S=0;for(const[P,L]of E.entries())d.type===L&&(S=P%s.numberSectionStyles);let M="";return d.active?d.crit?M+=" activeCrit":M=" active":d.done?d.crit?M=" doneCrit":M=" done":d.crit&&(M+=" crit"),M.length===0&&(M=" task"),d.milestone&&(M=" milestone "+M),M+=S,M+=" "+I,i+M}),h.append("text").attr("id",function(d){return d.id+"-text"}).text(function(d){return d.task}).attr("font-size",s.fontSize).attr("x",function(d){let i=T(d.startTime),I=T(d.renderEndTime||d.endTime);d.milestone&&(i+=.5*(T(d.endTime)-T(d.startTime))-.5*m),d.milestone&&(I=i+m);const S=this.getBBox().width;return S>I-i?I+S+1.5*s.leftPadding>l?i+_-5:I+_+5:(I-i)/2+i+_}).attr("y",function(d,i){return i=d.order,i*w+s.barHeight/2+(s.fontSize/2-2)+b}).attr("text-height",m).attr("class",function(d){const i=T(d.startTime);let I=T(d.endTime);d.milestone&&(I=i+m);const S=this.getBBox().width;let M="";d.classes.length>0&&(M=d.classes.join(" "));let P=0;for(const[Y,J]of E.entries())d.type===J&&(P=Y%s.numberSectionStyles);let L="";return d.active&&(d.crit?L="activeCritText"+P:L="activeText"+P),d.done?d.crit?L=L+" doneCritText"+P:L=L+" doneText"+P:d.crit&&(L=L+" critText"+P),d.milestone&&(L+=" milestoneText"),S>I-i?I+S+1.5*s.leftPadding>l?M+" taskTextOutsideLeft taskTextOutside"+P+" "+L:M+" taskTextOutsideRight taskTextOutside"+P+" "+L+" width-"+S:M+" taskText taskText"+P+" "+L+" width-"+S}),nt().securityLevel==="sandbox"){let d;d=yt("#i"+e);const i=d.nodes()[0].contentDocument;h.filter(function(I){return x.has(I.id)}).each(function(I){var S=i.querySelector("#"+I.id),M=i.querySelector("#"+I.id+"-text");const P=S.parentNode;var L=i.createElement("a");L.setAttribute("xlink:href",x.get(I.id)),L.setAttribute("target","_top"),P.appendChild(L),L.appendChild(S),L.appendChild(M)})}}c(j,"drawRects");function q(y,w,b,_,m,C,l,f){if(l.length===0&&f.length===0)return;let k,h;for(const{startTime:S,endTime:M}of C)(k===void 0||S<k)&&(k=S),(h===void 0||M>h)&&(h=M);if(!k||!h)return;if(R(h).diff(R(k),"year")>5){pt.warn("The difference between the min and max time is more than 5 years. This will cause performance issues. Skipping drawing exclude days.");return}const x=a.db.getDateFormat(),n=[];let d=null,i=R(k);for(;i.valueOf()<=h;)a.db.isInvalidDate(i,x,l,f)?d?d.end=i:d={start:i,end:i}:d&&(n.push(d),d=null),i=i.add(1,"d");F.append("g").selectAll("rect").data(n).enter().append("rect").attr("id",function(S){return"exclude-"+S.start.format("YYYY-MM-DD")}).attr("x",function(S){return T(S.start)+b}).attr("y",s.gridLineStartPadding).attr("width",function(S){const M=S.end.add(1,"day");return T(M)-T(S.start)}).attr("height",m-w-s.gridLineStartPadding).attr("transform-origin",function(S,M){return(T(S.start)+b+.5*(T(S.end)-T(S.start))).toString()+"px "+(M*y+.5*m).toString()+"px"}).attr("class","exclude-range")}c(q,"drawExcludeDays");function Z(y,w,b,_){let m=Se(T).tickSize(-_+w+s.gridLineStartPadding).tickFormat(Ht(a.db.getAxisFormat()||s.axisFormat||"%Y-%m-%d"));const l=/^([1-9]\d*)(millisecond|second|minute|hour|day|week|month)$/.exec(a.db.getTickInterval()||s.tickInterval);if(l!==null){const f=l[1],k=l[2],h=a.db.getWeekday()||s.weekday;switch(k){case"millisecond":m.ticks(Qt.every(f));break;case"second":m.ticks(Zt.every(f));break;case"minute":m.ticks(jt.every(f));break;case"hour":m.ticks(Xt.every(f));break;case"day":m.ticks(qt.every(f));break;case"week":m.ticks(re[h].every(f));break;case"month":m.ticks(Ut.every(f));break}}if(F.append("g").attr("class","grid").attr("transform","translate("+y+", "+(_-50)+")").call(m).selectAll("text").style("text-anchor","middle").attr("fill","#000").attr("stroke","none").attr("font-size",10).attr("dy","1em"),a.db.topAxisEnabled()||s.topAxis){let f=Ye(T).tickSize(-_+w+s.gridLineStartPadding).tickFormat(Ht(a.db.getAxisFormat()||s.axisFormat||"%Y-%m-%d"));if(l!==null){const k=l[1],h=l[2],x=a.db.getWeekday()||s.weekday;switch(h){case"millisecond":f.ticks(Qt.every(k));break;case"second":f.ticks(Zt.every(k));break;case"minute":f.ticks(jt.every(k));break;case"hour":f.ticks(Xt.every(k));break;case"day":f.ticks(qt.every(k));break;case"week":f.ticks(re[x].every(k));break;case"month":f.ticks(Ut.every(k));break}}F.append("g").attr("class","grid").attr("transform","translate("+y+", "+w+")").call(f).selectAll("text").style("text-anchor","middle").attr("fill","#000").attr("stroke","none").attr("font-size",10)}}c(Z,"makeGrid");function Q(y,w){let b=0;const _=Object.keys(O).map(m=>[m,O[m]]);F.append("g").selectAll("text").data(_).enter().append(function(m){const C=m[0].split(We.lineBreakRegex),l=-(C.length-1)/2,f=g.createElementNS("http://www.w3.org/2000/svg","text");f.setAttribute("dy",l+"em");for(const[k,h]of C.entries()){const x=g.createElementNS("http://www.w3.org/2000/svg","tspan");x.setAttribute("alignment-baseline","central"),x.setAttribute("x","10"),k>0&&x.setAttribute("dy","1em"),x.textContent=h,f.appendChild(x)}return f}).attr("x",10).attr("y",function(m,C){if(C>0)for(let l=0;l<C;l++)return b+=_[C-1][1],m[1]*y/2+b*y+w;else return m[1]*y/2+w}).attr("font-size",s.sectionFontSize).attr("class",function(m){for(const[C,l]of E.entries())if(m[0]===l)return"sectionTitle sectionTitle"+C%s.numberSectionStyles;return"sectionTitle"})}c(Q,"vertLabels");function K(y,w,b,_){const m=a.db.getTodayMarker();if(m==="off")return;const C=F.append("g").attr("class","today"),l=new Date,f=C.append("line");f.attr("x1",T(l)+y).attr("x2",T(l)+y).attr("y1",s.titleTopMargin).attr("y2",_-s.titleTopMargin).attr("class","today"),m!==""&&f.attr("style",m.replace(/,/g,";"))}c(K,"drawToday");function $(y){const w={},b=[];for(let _=0,m=y.length;_<m;++_)Object.prototype.hasOwnProperty.call(w,y[_])||(w[y[_]]=!0,b.push(y[_]));return b}c($,"checkUnique")},"draw"),Ur={setConf:Nr,draw:Hr},qr=c(t=>`
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
`,"getStyles"),Xr=qr,Qr={parser:ir,db:Br,renderer:Ur,styles:Xr};export{Qr as diagram};
