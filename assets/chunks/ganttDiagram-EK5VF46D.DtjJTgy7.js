import{_ as l,g as ke,s as me,t as ye,q as ve,a as ge,b as pe,c as nt,d as yt,aC as Te,aD as xe,aE as be,e as we,R as _e,aF as De,aG as R,l as pt,aH as Se,aI as Ht,aJ as Ut,aK as Ce,aL as Ee,aM as Me,aN as Ie,aO as Ae,aP as Le,aQ as Fe,aR as qt,aS as Xt,aT as jt,aU as Zt,aV as Qt,aW as Ye,k as We,j as Oe,z as Pe,u as Ve}from"./theme.CupqRD_u.js";import"./framework.DRZkEhKb.js";var St="day",ze="week",Re="year",Ne="YYYY-MM-DDTHH:mm:ssZ",Be="isoweek";const Ge=function(t,e,r){var s=function(p,D){var v=(D?r.utc:r)().year(p).startOf(Re),M=4-v.isoWeekday();return v.isoWeekday()>4&&(M+=7),v.add(M,St)},a=function(p){return p.add(4-p.isoWeekday(),St)},c=e.prototype;c.isoWeekYear=function(){var g=a(this);return g.year()},c.isoWeek=function(g){if(!this.$utils().u(g))return this.add((g-this.isoWeek())*7,St);var p=a(this),D=s(this.isoWeekYear(),this.$u);return p.diff(D,ze)+1},c.isoWeekday=function(g){return this.$utils().u(g)?this.day()||7:this.day(this.day()%7?g:g-7)};var d=c.startOf;c.startOf=function(g,p){var D=this.$utils(),v=D.u(p)?!0:p,M=D.p(g);return M===Be?v?this.date(this.date()-(this.isoWeekday()-1)).startOf("day"):this.date(this.date()-1-(this.isoWeekday()-1)+7).endOf("day"):d.bind(this)(g,p)}};var He=function(e){return e.replace(/(\[[^\]]+])|(MMMM|MM|DD|dddd)/g,function(r,s,a){return s||a.slice(1)})},Ue={LTS:"h:mm:ss A",LT:"h:mm A",L:"MM/DD/YYYY",LL:"MMMM D, YYYY",LLL:"MMMM D, YYYY h:mm A",LLLL:"dddd, MMMM D, YYYY h:mm A"},qe=function(e,r){return e.replace(/(\[[^\]]+])|(LTS?|l{1,4}|L{1,4})/g,function(s,a,c){var d=c&&c.toUpperCase();return a||r[c]||Ue[c]||He(r[d])})},Xe=/(\[[^[]*\])|([-_:/.,()\s]+)|(A|a|Q|YYYY|YY?|ww?|MM?M?M?|Do|DD?|hh?|HH?|mm?|ss?|S{1,3}|z|ZZ?)/g,$t=/\d/,lt=/\d\d/,je=/\d{3}/,Ze=/\d{4}/,U=/\d\d?/,Qe=/[+-]?\d+/,$e=/[+-]\d\d:?(\d\d)?|Z/,ut=/\d*[^-_:/,()\s\d]+/,rt={},ae=function(e){return e=+e,e+(e>68?1900:2e3)};function Ke(t){if(!t||t==="Z")return 0;var e=t.match(/([+-]|\d\d)/g),r=+(e[1]*60)+(+e[2]||0);return r===0?0:e[0]==="+"?-r:r}var V=function(e){return function(r){this[e]=+r}},Kt=[$e,function(t){var e=this.zone||(this.zone={});e.offset=Ke(t)}],Ct=function(e){var r=rt[e];return r&&(r.indexOf?r:r.s.concat(r.f))},Jt=function(e,r){var s,a=rt,c=a.meridiem;if(!c)s=e===(r?"pm":"PM");else for(var d=1;d<=24;d+=1)if(e.indexOf(c(d,0,r))>-1){s=d>12;break}return s},Je={A:[ut,function(t){this.afternoon=Jt(t,!1)}],a:[ut,function(t){this.afternoon=Jt(t,!0)}],Q:[$t,function(t){this.month=(t-1)*3+1}],S:[$t,function(t){this.milliseconds=+t*100}],SS:[lt,function(t){this.milliseconds=+t*10}],SSS:[je,function(t){this.milliseconds=+t}],s:[U,V("seconds")],ss:[U,V("seconds")],m:[U,V("minutes")],mm:[U,V("minutes")],H:[U,V("hours")],h:[U,V("hours")],HH:[U,V("hours")],hh:[U,V("hours")],D:[U,V("day")],DD:[lt,V("day")],Do:[ut,function(t){var e=rt,r=e.ordinal,s=t.match(/\d+/);if(this.day=s[0],!!r)for(var a=1;a<=31;a+=1)r(a).replace(/\[|\]/g,"")===t&&(this.day=a)}],w:[U,V("week")],ww:[lt,V("week")],M:[U,V("month")],MM:[lt,V("month")],MMM:[ut,function(t){var e=Ct("months"),r=Ct("monthsShort"),s=(r||e.map(function(a){return a.slice(0,3)})).indexOf(t)+1;if(s<1)throw new Error;this.month=s%12||s}],MMMM:[ut,function(t){var e=Ct("months"),r=e.indexOf(t)+1;if(r<1)throw new Error;this.month=r%12||r}],Y:[Qe,V("year")],YY:[lt,function(t){this.year=ae(t)}],YYYY:[Ze,V("year")],Z:Kt,ZZ:Kt};function tr(t){var e=t.afternoon;if(e!==void 0){var r=t.hours;e?r<12&&(t.hours+=12):r===12&&(t.hours=0),delete t.afternoon}}function er(t){t=qe(t,rt&&rt.formats);for(var e=t.match(Xe),r=e.length,s=0;s<r;s+=1){var a=e[s],c=Je[a],d=c&&c[0],g=c&&c[1];g?e[s]={regex:d,parser:g}:e[s]=a.replace(/^\[|\]$/g,"")}return function(p){for(var D={},v=0,M=0;v<r;v+=1){var O=e[v];if(typeof O=="string")M+=O.length;else{var A=O.regex,F=O.parser,T=p.slice(M),z=A.exec(T),G=z[0];F.call(D,G),p=p.replace(G,"")}}return tr(D),D}}var rr=function(e,r,s,a){try{if(["x","X"].indexOf(r)>-1)return new Date((r==="X"?1e3:1)*e);var c=er(r),d=c(e),g=d.year,p=d.month,D=d.day,v=d.hours,M=d.minutes,O=d.seconds,A=d.milliseconds,F=d.zone,T=d.week,z=new Date,G=D||(!g&&!p?z.getDate():1),j=g||z.getFullYear(),q=0;g&&!p||(q=p>0?p-1:z.getMonth());var Z=v||0,Q=M||0,$=O||0,K=A||0;if(F)return new Date(Date.UTC(j,q,G,Z,Q,$,K+F.offset*60*1e3));if(s)return new Date(Date.UTC(j,q,G,Z,Q,$,K));var y;return y=new Date(j,q,G,Z,Q,$,K),T&&(y=a(y).week(T).toDate()),y}catch{return new Date("")}};const ar=function(t,e,r){r.p.customParseFormat=!0,t&&t.parseTwoDigitYear&&(ae=t.parseTwoDigitYear);var s=e.prototype,a=s.parse;s.parse=function(c){var d=c.date,g=c.utc,p=c.args;this.$u=g;var D=p[1];if(typeof D=="string"){var v=p[2]===!0,M=p[3]===!0,O=v||M,A=p[2];M&&(A=p[2]),rt=this.$locale(),!v&&A&&(rt=r.Ls[A]),this.$d=rr(d,D,g,r),this.init(),A&&A!==!0&&(this.$L=this.locale(A).$L),O&&d!=this.format(D)&&(this.$d=new Date("")),rt={}}else if(D instanceof Array)for(var F=D.length,T=1;T<=F;T+=1){p[1]=D[T-1];var z=r.apply(this,p);if(z.isValid()){this.$d=z.$d,this.$L=z.$L,this.init();break}T===F&&(this.$d=new Date(""))}else a.call(this,c)}},sr=function(t,e){var r=e.prototype,s=r.format;r.format=function(a){var c=this,d=this.$locale();if(!this.isValid())return s.bind(this)(a);var g=this.$utils(),p=a||Ne,D=p.replace(/\[([^\]]+)]|Q|wo|ww|w|WW|W|zzz|z|gggg|GGGG|Do|X|x|k{1,2}|S/g,function(v){switch(v){case"Q":return Math.ceil((c.$M+1)/3);case"Do":return d.ordinal(c.$D);case"gggg":return c.weekYear();case"GGGG":return c.isoWeekYear();case"wo":return d.ordinal(c.week(),"W");case"w":case"ww":return g.s(c.week(),v==="w"?1:2,"0");case"W":case"WW":return g.s(c.isoWeek(),v==="W"?1:2,"0");case"k":case"kk":return g.s(String(c.$H===0?24:c.$H),v==="k"?1:2,"0");case"X":return Math.floor(c.$d.getTime()/1e3);case"x":return c.$d.getTime();case"z":return"["+c.offsetName()+"]";case"zzz":return"["+c.offsetName("long")+"]";default:return v}});return s.bind(this)(D)}};var Et=function(){var t=l(function(E,u,f,k){for(f=f||{},k=E.length;k--;f[E[k]]=u);return f},"o"),e=[6,8,10,12,13,14,15,16,17,18,20,21,22,23,24,25,26,27,28,29,30,31,33,35,36,38,40],r=[1,26],s=[1,27],a=[1,28],c=[1,29],d=[1,30],g=[1,31],p=[1,32],D=[1,33],v=[1,34],M=[1,9],O=[1,10],A=[1,11],F=[1,12],T=[1,13],z=[1,14],G=[1,15],j=[1,16],q=[1,19],Z=[1,20],Q=[1,21],$=[1,22],K=[1,23],y=[1,25],_=[1,35],b={trace:l(function(){},"trace"),yy:{},symbols_:{error:2,start:3,gantt:4,document:5,EOF:6,line:7,SPACE:8,statement:9,NL:10,weekday:11,weekday_monday:12,weekday_tuesday:13,weekday_wednesday:14,weekday_thursday:15,weekday_friday:16,weekday_saturday:17,weekday_sunday:18,weekend:19,weekend_friday:20,weekend_saturday:21,dateFormat:22,inclusiveEndDates:23,topAxis:24,axisFormat:25,tickInterval:26,excludes:27,includes:28,todayMarker:29,title:30,acc_title:31,acc_title_value:32,acc_descr:33,acc_descr_value:34,acc_descr_multiline_value:35,section:36,clickStatement:37,taskTxt:38,taskData:39,click:40,callbackname:41,callbackargs:42,href:43,clickStatementDebug:44,$accept:0,$end:1},terminals_:{2:"error",4:"gantt",6:"EOF",8:"SPACE",10:"NL",12:"weekday_monday",13:"weekday_tuesday",14:"weekday_wednesday",15:"weekday_thursday",16:"weekday_friday",17:"weekday_saturday",18:"weekday_sunday",20:"weekend_friday",21:"weekend_saturday",22:"dateFormat",23:"inclusiveEndDates",24:"topAxis",25:"axisFormat",26:"tickInterval",27:"excludes",28:"includes",29:"todayMarker",30:"title",31:"acc_title",32:"acc_title_value",33:"acc_descr",34:"acc_descr_value",35:"acc_descr_multiline_value",36:"section",38:"taskTxt",39:"taskData",40:"click",41:"callbackname",42:"callbackargs",43:"href"},productions_:[0,[3,3],[5,0],[5,2],[7,2],[7,1],[7,1],[7,1],[11,1],[11,1],[11,1],[11,1],[11,1],[11,1],[11,1],[19,1],[19,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,1],[9,2],[9,2],[9,1],[9,1],[9,1],[9,2],[37,2],[37,3],[37,3],[37,4],[37,3],[37,4],[37,2],[44,2],[44,3],[44,3],[44,4],[44,3],[44,4],[44,2]],performAction:l(function(u,f,k,h,x,n,o){var i=n.length-1;switch(x){case 1:return n[i-1];case 2:this.$=[];break;case 3:n[i-1].push(n[i]),this.$=n[i-1];break;case 4:case 5:this.$=n[i];break;case 6:case 7:this.$=[];break;case 8:h.setWeekday("monday");break;case 9:h.setWeekday("tuesday");break;case 10:h.setWeekday("wednesday");break;case 11:h.setWeekday("thursday");break;case 12:h.setWeekday("friday");break;case 13:h.setWeekday("saturday");break;case 14:h.setWeekday("sunday");break;case 15:h.setWeekend("friday");break;case 16:h.setWeekend("saturday");break;case 17:h.setDateFormat(n[i].substr(11)),this.$=n[i].substr(11);break;case 18:h.enableInclusiveEndDates(),this.$=n[i].substr(18);break;case 19:h.TopAxis(),this.$=n[i].substr(8);break;case 20:h.setAxisFormat(n[i].substr(11)),this.$=n[i].substr(11);break;case 21:h.setTickInterval(n[i].substr(13)),this.$=n[i].substr(13);break;case 22:h.setExcludes(n[i].substr(9)),this.$=n[i].substr(9);break;case 23:h.setIncludes(n[i].substr(9)),this.$=n[i].substr(9);break;case 24:h.setTodayMarker(n[i].substr(12)),this.$=n[i].substr(12);break;case 27:h.setDiagramTitle(n[i].substr(6)),this.$=n[i].substr(6);break;case 28:this.$=n[i].trim(),h.setAccTitle(this.$);break;case 29:case 30:this.$=n[i].trim(),h.setAccDescription(this.$);break;case 31:h.addSection(n[i].substr(8)),this.$=n[i].substr(8);break;case 33:h.addTask(n[i-1],n[i]),this.$="task";break;case 34:this.$=n[i-1],h.setClickEvent(n[i-1],n[i],null);break;case 35:this.$=n[i-2],h.setClickEvent(n[i-2],n[i-1],n[i]);break;case 36:this.$=n[i-2],h.setClickEvent(n[i-2],n[i-1],null),h.setLink(n[i-2],n[i]);break;case 37:this.$=n[i-3],h.setClickEvent(n[i-3],n[i-2],n[i-1]),h.setLink(n[i-3],n[i]);break;case 38:this.$=n[i-2],h.setClickEvent(n[i-2],n[i],null),h.setLink(n[i-2],n[i-1]);break;case 39:this.$=n[i-3],h.setClickEvent(n[i-3],n[i-1],n[i]),h.setLink(n[i-3],n[i-2]);break;case 40:this.$=n[i-1],h.setLink(n[i-1],n[i]);break;case 41:case 47:this.$=n[i-1]+" "+n[i];break;case 42:case 43:case 45:this.$=n[i-2]+" "+n[i-1]+" "+n[i];break;case 44:case 46:this.$=n[i-3]+" "+n[i-2]+" "+n[i-1]+" "+n[i];break}},"anonymous"),table:[{3:1,4:[1,2]},{1:[3]},t(e,[2,2],{5:3}),{6:[1,4],7:5,8:[1,6],9:7,10:[1,8],11:17,12:r,13:s,14:a,15:c,16:d,17:g,18:p,19:18,20:D,21:v,22:M,23:O,24:A,25:F,26:T,27:z,28:G,29:j,30:q,31:Z,33:Q,35:$,36:K,37:24,38:y,40:_},t(e,[2,7],{1:[2,1]}),t(e,[2,3]),{9:36,11:17,12:r,13:s,14:a,15:c,16:d,17:g,18:p,19:18,20:D,21:v,22:M,23:O,24:A,25:F,26:T,27:z,28:G,29:j,30:q,31:Z,33:Q,35:$,36:K,37:24,38:y,40:_},t(e,[2,5]),t(e,[2,6]),t(e,[2,17]),t(e,[2,18]),t(e,[2,19]),t(e,[2,20]),t(e,[2,21]),t(e,[2,22]),t(e,[2,23]),t(e,[2,24]),t(e,[2,25]),t(e,[2,26]),t(e,[2,27]),{32:[1,37]},{34:[1,38]},t(e,[2,30]),t(e,[2,31]),t(e,[2,32]),{39:[1,39]},t(e,[2,8]),t(e,[2,9]),t(e,[2,10]),t(e,[2,11]),t(e,[2,12]),t(e,[2,13]),t(e,[2,14]),t(e,[2,15]),t(e,[2,16]),{41:[1,40],43:[1,41]},t(e,[2,4]),t(e,[2,28]),t(e,[2,29]),t(e,[2,33]),t(e,[2,34],{42:[1,42],43:[1,43]}),t(e,[2,40],{41:[1,44]}),t(e,[2,35],{43:[1,45]}),t(e,[2,36]),t(e,[2,38],{42:[1,46]}),t(e,[2,37]),t(e,[2,39])],defaultActions:{},parseError:l(function(u,f){if(f.recoverable)this.trace(u);else{var k=new Error(u);throw k.hash=f,k}},"parseError"),parse:l(function(u){var f=this,k=[0],h=[],x=[null],n=[],o=this.table,i="",I=0,S=0,C=2,P=1,L=n.slice.call(arguments,1),Y=Object.create(this.lexer),J={yy:{}};for(var bt in this.yy)Object.prototype.hasOwnProperty.call(this.yy,bt)&&(J.yy[bt]=this.yy[bt]);Y.setInput(u,J.yy),J.yy.lexer=Y,J.yy.parser=this,typeof Y.yylloc>"u"&&(Y.yylloc={});var wt=Y.yylloc;n.push(wt);var fe=Y.options&&Y.options.ranges;typeof J.yy.parseError=="function"?this.parseError=J.yy.parseError:this.parseError=Object.getPrototypeOf(this).parseError;function he(B){k.length=k.length-2*B,x.length=x.length-B,n.length=n.length-B}l(he,"popStack");function Bt(){var B;return B=h.pop()||Y.lex()||P,typeof B!="number"&&(B instanceof Array&&(h=B,B=h.pop()),B=f.symbols_[B]||B),B}l(Bt,"lex");for(var N,at,H,_t,it={},kt,tt,Gt,mt;;){if(at=k[k.length-1],this.defaultActions[at]?H=this.defaultActions[at]:((N===null||typeof N>"u")&&(N=Bt()),H=o[at]&&o[at][N]),typeof H>"u"||!H.length||!H[0]){var Dt="";mt=[];for(kt in o[at])this.terminals_[kt]&&kt>C&&mt.push("'"+this.terminals_[kt]+"'");Y.showPosition?Dt="Parse error on line "+(I+1)+`:
`+Y.showPosition()+`
Expecting `+mt.join(", ")+", got '"+(this.terminals_[N]||N)+"'":Dt="Parse error on line "+(I+1)+": Unexpected "+(N==P?"end of input":"'"+(this.terminals_[N]||N)+"'"),this.parseError(Dt,{text:Y.match,token:this.terminals_[N]||N,line:Y.yylineno,loc:wt,expected:mt})}if(H[0]instanceof Array&&H.length>1)throw new Error("Parse Error: multiple actions possible at state: "+at+", token: "+N);switch(H[0]){case 1:k.push(N),x.push(Y.yytext),n.push(Y.yylloc),k.push(H[1]),N=null,S=Y.yyleng,i=Y.yytext,I=Y.yylineno,wt=Y.yylloc;break;case 2:if(tt=this.productions_[H[1]][1],it.$=x[x.length-tt],it._$={first_line:n[n.length-(tt||1)].first_line,last_line:n[n.length-1].last_line,first_column:n[n.length-(tt||1)].first_column,last_column:n[n.length-1].last_column},fe&&(it._$.range=[n[n.length-(tt||1)].range[0],n[n.length-1].range[1]]),_t=this.performAction.apply(it,[i,S,I,J.yy,H[1],x,n].concat(L)),typeof _t<"u")return _t;tt&&(k=k.slice(0,-1*tt*2),x=x.slice(0,-1*tt),n=n.slice(0,-1*tt)),k.push(this.productions_[H[1]][0]),x.push(it.$),n.push(it._$),Gt=o[k[k.length-2]][k[k.length-1]],k.push(Gt);break;case 3:return!0}}return!0},"parse")},w=function(){var E={EOF:1,parseError:l(function(f,k){if(this.yy.parser)this.yy.parser.parseError(f,k);else throw new Error(f)},"parseError"),setInput:l(function(u,f){return this.yy=f||this.yy||{},this._input=u,this._more=this._backtrack=this.done=!1,this.yylineno=this.yyleng=0,this.yytext=this.matched=this.match="",this.conditionStack=["INITIAL"],this.yylloc={first_line:1,first_column:0,last_line:1,last_column:0},this.options.ranges&&(this.yylloc.range=[0,0]),this.offset=0,this},"setInput"),input:l(function(){var u=this._input[0];this.yytext+=u,this.yyleng++,this.offset++,this.match+=u,this.matched+=u;var f=u.match(/(?:\r\n?|\n).*/g);return f?(this.yylineno++,this.yylloc.last_line++):this.yylloc.last_column++,this.options.ranges&&this.yylloc.range[1]++,this._input=this._input.slice(1),u},"input"),unput:l(function(u){var f=u.length,k=u.split(/(?:\r\n?|\n)/g);this._input=u+this._input,this.yytext=this.yytext.substr(0,this.yytext.length-f),this.offset-=f;var h=this.match.split(/(?:\r\n?|\n)/g);this.match=this.match.substr(0,this.match.length-1),this.matched=this.matched.substr(0,this.matched.length-1),k.length-1&&(this.yylineno-=k.length-1);var x=this.yylloc.range;return this.yylloc={first_line:this.yylloc.first_line,last_line:this.yylineno+1,first_column:this.yylloc.first_column,last_column:k?(k.length===h.length?this.yylloc.first_column:0)+h[h.length-k.length].length-k[0].length:this.yylloc.first_column-f},this.options.ranges&&(this.yylloc.range=[x[0],x[0]+this.yyleng-f]),this.yyleng=this.yytext.length,this},"unput"),more:l(function(){return this._more=!0,this},"more"),reject:l(function(){if(this.options.backtrack_lexer)this._backtrack=!0;else return this.parseError("Lexical error on line "+(this.yylineno+1)+`. You can only invoke reject() in the lexer when the lexer is of the backtracking persuasion (options.backtrack_lexer = true).
`+this.showPosition(),{text:"",token:null,line:this.yylineno});return this},"reject"),less:l(function(u){this.unput(this.match.slice(u))},"less"),pastInput:l(function(){var u=this.matched.substr(0,this.matched.length-this.match.length);return(u.length>20?"...":"")+u.substr(-20).replace(/\n/g,"")},"pastInput"),upcomingInput:l(function(){var u=this.match;return u.length<20&&(u+=this._input.substr(0,20-u.length)),(u.substr(0,20)+(u.length>20?"...":"")).replace(/\n/g,"")},"upcomingInput"),showPosition:l(function(){var u=this.pastInput(),f=new Array(u.length+1).join("-");return u+this.upcomingInput()+`
`+f+"^"},"showPosition"),test_match:l(function(u,f){var k,h,x;if(this.options.backtrack_lexer&&(x={yylineno:this.yylineno,yylloc:{first_line:this.yylloc.first_line,last_line:this.last_line,first_column:this.yylloc.first_column,last_column:this.yylloc.last_column},yytext:this.yytext,match:this.match,matches:this.matches,matched:this.matched,yyleng:this.yyleng,offset:this.offset,_more:this._more,_input:this._input,yy:this.yy,conditionStack:this.conditionStack.slice(0),done:this.done},this.options.ranges&&(x.yylloc.range=this.yylloc.range.slice(0))),h=u[0].match(/(?:\r\n?|\n).*/g),h&&(this.yylineno+=h.length),this.yylloc={first_line:this.yylloc.last_line,last_line:this.yylineno+1,first_column:this.yylloc.last_column,last_column:h?h[h.length-1].length-h[h.length-1].match(/\r?\n?/)[0].length:this.yylloc.last_column+u[0].length},this.yytext+=u[0],this.match+=u[0],this.matches=u,this.yyleng=this.yytext.length,this.options.ranges&&(this.yylloc.range=[this.offset,this.offset+=this.yyleng]),this._more=!1,this._backtrack=!1,this._input=this._input.slice(u[0].length),this.matched+=u[0],k=this.performAction.call(this,this.yy,this,f,this.conditionStack[this.conditionStack.length-1]),this.done&&this._input&&(this.done=!1),k)return k;if(this._backtrack){for(var n in x)this[n]=x[n];return!1}return!1},"test_match"),next:l(function(){if(this.done)return this.EOF;this._input||(this.done=!0);var u,f,k,h;this._more||(this.yytext="",this.match="");for(var x=this._currentRules(),n=0;n<x.length;n++)if(k=this._input.match(this.rules[x[n]]),k&&(!f||k[0].length>f[0].length)){if(f=k,h=n,this.options.backtrack_lexer){if(u=this.test_match(k,x[n]),u!==!1)return u;if(this._backtrack){f=!1;continue}else return!1}else if(!this.options.flex)break}return f?(u=this.test_match(f,x[h]),u!==!1?u:!1):this._input===""?this.EOF:this.parseError("Lexical error on line "+(this.yylineno+1)+`. Unrecognized text.
`+this.showPosition(),{text:"",token:null,line:this.yylineno})},"next"),lex:l(function(){var f=this.next();return f||this.lex()},"lex"),begin:l(function(f){this.conditionStack.push(f)},"begin"),popState:l(function(){var f=this.conditionStack.length-1;return f>0?this.conditionStack.pop():this.conditionStack[0]},"popState"),_currentRules:l(function(){return this.conditionStack.length&&this.conditionStack[this.conditionStack.length-1]?this.conditions[this.conditionStack[this.conditionStack.length-1]].rules:this.conditions.INITIAL.rules},"_currentRules"),topState:l(function(f){return f=this.conditionStack.length-1-Math.abs(f||0),f>=0?this.conditionStack[f]:"INITIAL"},"topState"),pushState:l(function(f){this.begin(f)},"pushState"),stateStackSize:l(function(){return this.conditionStack.length},"stateStackSize"),options:{"case-insensitive":!0},performAction:l(function(f,k,h,x){switch(h){case 0:return this.begin("open_directive"),"open_directive";case 1:return this.begin("acc_title"),31;case 2:return this.popState(),"acc_title_value";case 3:return this.begin("acc_descr"),33;case 4:return this.popState(),"acc_descr_value";case 5:this.begin("acc_descr_multiline");break;case 6:this.popState();break;case 7:return"acc_descr_multiline_value";case 8:break;case 9:break;case 10:break;case 11:return 10;case 12:break;case 13:break;case 14:this.begin("href");break;case 15:this.popState();break;case 16:return 43;case 17:this.begin("callbackname");break;case 18:this.popState();break;case 19:this.popState(),this.begin("callbackargs");break;case 20:return 41;case 21:this.popState();break;case 22:return 42;case 23:this.begin("click");break;case 24:this.popState();break;case 25:return 40;case 26:return 4;case 27:return 22;case 28:return 23;case 29:return 24;case 30:return 25;case 31:return 26;case 32:return 28;case 33:return 27;case 34:return 29;case 35:return 12;case 36:return 13;case 37:return 14;case 38:return 15;case 39:return 16;case 40:return 17;case 41:return 18;case 42:return 20;case 43:return 21;case 44:return"date";case 45:return 30;case 46:return"accDescription";case 47:return 36;case 48:return 38;case 49:return 39;case 50:return":";case 51:return 6;case 52:return"INVALID"}},"anonymous"),rules:[/^(?:%%\{)/i,/^(?:accTitle\s*:\s*)/i,/^(?:(?!\n||)*[^\n]*)/i,/^(?:accDescr\s*:\s*)/i,/^(?:(?!\n||)*[^\n]*)/i,/^(?:accDescr\s*\{\s*)/i,/^(?:[\}])/i,/^(?:[^\}]*)/i,/^(?:%%(?!\{)*[^\n]*)/i,/^(?:[^\}]%%*[^\n]*)/i,/^(?:%%*[^\n]*[\n]*)/i,/^(?:[\n]+)/i,/^(?:\s+)/i,/^(?:%[^\n]*)/i,/^(?:href[\s]+["])/i,/^(?:["])/i,/^(?:[^"]*)/i,/^(?:call[\s]+)/i,/^(?:\([\s]*\))/i,/^(?:\()/i,/^(?:[^(]*)/i,/^(?:\))/i,/^(?:[^)]*)/i,/^(?:click[\s]+)/i,/^(?:[\s\n])/i,/^(?:[^\s\n]*)/i,/^(?:gantt\b)/i,/^(?:dateFormat\s[^#\n;]+)/i,/^(?:inclusiveEndDates\b)/i,/^(?:topAxis\b)/i,/^(?:axisFormat\s[^#\n;]+)/i,/^(?:tickInterval\s[^#\n;]+)/i,/^(?:includes\s[^#\n;]+)/i,/^(?:excludes\s[^#\n;]+)/i,/^(?:todayMarker\s[^\n;]+)/i,/^(?:weekday\s+monday\b)/i,/^(?:weekday\s+tuesday\b)/i,/^(?:weekday\s+wednesday\b)/i,/^(?:weekday\s+thursday\b)/i,/^(?:weekday\s+friday\b)/i,/^(?:weekday\s+saturday\b)/i,/^(?:weekday\s+sunday\b)/i,/^(?:weekend\s+friday\b)/i,/^(?:weekend\s+saturday\b)/i,/^(?:\d\d\d\d-\d\d-\d\d\b)/i,/^(?:title\s[^\n]+)/i,/^(?:accDescription\s[^#\n;]+)/i,/^(?:section\s[^\n]+)/i,/^(?:[^:\n]+)/i,/^(?::[^#\n;]+)/i,/^(?::)/i,/^(?:$)/i,/^(?:.)/i],conditions:{acc_descr_multiline:{rules:[6,7],inclusive:!1},acc_descr:{rules:[4],inclusive:!1},acc_title:{rules:[2],inclusive:!1},callbackargs:{rules:[21,22],inclusive:!1},callbackname:{rules:[18,19,20],inclusive:!1},href:{rules:[15,16],inclusive:!1},click:{rules:[24,25],inclusive:!1},INITIAL:{rules:[0,1,3,5,8,9,10,11,12,13,14,17,23,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52],inclusive:!0}}};return E}();b.lexer=w;function m(){this.yy={}}return l(m,"Parser"),m.prototype=b,b.Parser=m,new m}();Et.parser=Et;var ir=Et;R.extend(Ge);R.extend(ar);R.extend(sr);var te={friday:5,saturday:6},X="",Lt="",Ft=void 0,Yt="",dt=[],ft=[],Wt=new Map,Ot=[],Tt=[],ct="",Pt="",se=["active","done","crit","milestone","vert"],Vt=[],ht=!1,zt=!1,Rt="sunday",xt="saturday",Mt=0,nr=l(function(){Ot=[],Tt=[],ct="",Vt=[],vt=0,At=void 0,gt=void 0,W=[],X="",Lt="",Pt="",Ft=void 0,Yt="",dt=[],ft=[],ht=!1,zt=!1,Mt=0,Wt=new Map,Pe(),Rt="sunday",xt="saturday"},"clear"),or=l(function(t){Lt=t},"setAxisFormat"),cr=l(function(){return Lt},"getAxisFormat"),lr=l(function(t){Ft=t},"setTickInterval"),ur=l(function(){return Ft},"getTickInterval"),dr=l(function(t){Yt=t},"setTodayMarker"),fr=l(function(){return Yt},"getTodayMarker"),hr=l(function(t){X=t},"setDateFormat"),kr=l(function(){ht=!0},"enableInclusiveEndDates"),mr=l(function(){return ht},"endDatesAreInclusive"),yr=l(function(){zt=!0},"enableTopAxis"),vr=l(function(){return zt},"topAxisEnabled"),gr=l(function(t){Pt=t},"setDisplayMode"),pr=l(function(){return Pt},"getDisplayMode"),Tr=l(function(){return X},"getDateFormat"),xr=l(function(t){dt=t.toLowerCase().split(/[\s,]+/)},"setIncludes"),br=l(function(){return dt},"getIncludes"),wr=l(function(t){ft=t.toLowerCase().split(/[\s,]+/)},"setExcludes"),_r=l(function(){return ft},"getExcludes"),Dr=l(function(){return Wt},"getLinks"),Sr=l(function(t){ct=t,Ot.push(t)},"addSection"),Cr=l(function(){return Ot},"getSections"),Er=l(function(){let t=ee();const e=10;let r=0;for(;!t&&r<e;)t=ee(),r++;return Tt=W,Tt},"getTasks"),ie=l(function(t,e,r,s){return s.includes(t.format(e.trim()))?!1:r.includes("weekends")&&(t.isoWeekday()===te[xt]||t.isoWeekday()===te[xt]+1)||r.includes(t.format("dddd").toLowerCase())?!0:r.includes(t.format(e.trim()))},"isInvalidDate"),Mr=l(function(t){Rt=t},"setWeekday"),Ir=l(function(){return Rt},"getWeekday"),Ar=l(function(t){xt=t},"setWeekend"),ne=l(function(t,e,r,s){if(!r.length||t.manualEndTime)return;let a;t.startTime instanceof Date?a=R(t.startTime):a=R(t.startTime,e,!0),a=a.add(1,"d");let c;t.endTime instanceof Date?c=R(t.endTime):c=R(t.endTime,e,!0);const[d,g]=Lr(a,c,e,r,s);t.endTime=d.toDate(),t.renderEndTime=g},"checkTaskDates"),Lr=l(function(t,e,r,s,a){let c=!1,d=null;for(;t<=e;)c||(d=e.toDate()),c=ie(t,r,s,a),c&&(e=e.add(1,"d")),t=t.add(1,"d");return[e,d]},"fixTaskDates"),It=l(function(t,e,r){r=r.trim();const a=/^after\s+(?<ids>[\d\w- ]+)/.exec(r);if(a!==null){let d=null;for(const p of a.groups.ids.split(" ")){let D=st(p);D!==void 0&&(!d||D.endTime>d.endTime)&&(d=D)}if(d)return d.endTime;const g=new Date;return g.setHours(0,0,0,0),g}let c=R(r,e.trim(),!0);if(c.isValid())return c.toDate();{pt.debug("Invalid date:"+r),pt.debug("With date format:"+e.trim());const d=new Date(r);if(d===void 0||isNaN(d.getTime())||d.getFullYear()<-1e4||d.getFullYear()>1e4)throw new Error("Invalid date:"+r);return d}},"getStartDate"),oe=l(function(t){const e=/^(\d+(?:\.\d+)?)([Mdhmswy]|ms)$/.exec(t.trim());return e!==null?[Number.parseFloat(e[1]),e[2]]:[NaN,"ms"]},"parseDuration"),ce=l(function(t,e,r,s=!1){r=r.trim();const c=/^until\s+(?<ids>[\d\w- ]+)/.exec(r);if(c!==null){let v=null;for(const O of c.groups.ids.split(" ")){let A=st(O);A!==void 0&&(!v||A.startTime<v.startTime)&&(v=A)}if(v)return v.startTime;const M=new Date;return M.setHours(0,0,0,0),M}let d=R(r,e.trim(),!0);if(d.isValid())return s&&(d=d.add(1,"d")),d.toDate();let g=R(t);const[p,D]=oe(r);if(!Number.isNaN(p)){const v=g.add(p,D);v.isValid()&&(g=v)}return g.toDate()},"getEndDate"),vt=0,ot=l(function(t){return t===void 0?(vt=vt+1,"task"+vt):t},"parseId"),Fr=l(function(t,e){let r;e.substr(0,1)===":"?r=e.substr(1,e.length):r=e;const s=r.split(","),a={};Nt(s,a,se);for(let d=0;d<s.length;d++)s[d]=s[d].trim();let c="";switch(s.length){case 1:a.id=ot(),a.startTime=t.endTime,c=s[0];break;case 2:a.id=ot(),a.startTime=It(void 0,X,s[0]),c=s[1];break;case 3:a.id=ot(s[0]),a.startTime=It(void 0,X,s[1]),c=s[2];break}return c&&(a.endTime=ce(a.startTime,X,c,ht),a.manualEndTime=R(c,"YYYY-MM-DD",!0).isValid(),ne(a,X,ft,dt)),a},"compileData"),Yr=l(function(t,e){let r;e.substr(0,1)===":"?r=e.substr(1,e.length):r=e;const s=r.split(","),a={};Nt(s,a,se);for(let c=0;c<s.length;c++)s[c]=s[c].trim();switch(s.length){case 1:a.id=ot(),a.startTime={type:"prevTaskEnd",id:t},a.endTime={data:s[0]};break;case 2:a.id=ot(),a.startTime={type:"getStartDate",startData:s[0]},a.endTime={data:s[1]};break;case 3:a.id=ot(s[0]),a.startTime={type:"getStartDate",startData:s[1]},a.endTime={data:s[2]};break}return a},"parseData"),At,gt,W=[],le={},Wr=l(function(t,e){const r={section:ct,type:ct,processed:!1,manualEndTime:!1,renderEndTime:null,raw:{data:e},task:t,classes:[]},s=Yr(gt,e);r.raw.startTime=s.startTime,r.raw.endTime=s.endTime,r.id=s.id,r.prevTaskId=gt,r.active=s.active,r.done=s.done,r.crit=s.crit,r.milestone=s.milestone,r.vert=s.vert,r.order=Mt,Mt++;const a=W.push(r);gt=r.id,le[r.id]=a-1},"addTask"),st=l(function(t){const e=le[t];return W[e]},"findTaskById"),Or=l(function(t,e){const r={section:ct,type:ct,description:t,task:t,classes:[]},s=Fr(At,e);r.startTime=s.startTime,r.endTime=s.endTime,r.id=s.id,r.active=s.active,r.done=s.done,r.crit=s.crit,r.milestone=s.milestone,r.vert=s.vert,At=r,Tt.push(r)},"addTaskOrg"),ee=l(function(){const t=l(function(r){const s=W[r];let a="";switch(W[r].raw.startTime.type){case"prevTaskEnd":{const c=st(s.prevTaskId);s.startTime=c.endTime;break}case"getStartDate":a=It(void 0,X,W[r].raw.startTime.startData),a&&(W[r].startTime=a);break}return W[r].startTime&&(W[r].endTime=ce(W[r].startTime,X,W[r].raw.endTime.data,ht),W[r].endTime&&(W[r].processed=!0,W[r].manualEndTime=R(W[r].raw.endTime.data,"YYYY-MM-DD",!0).isValid(),ne(W[r],X,ft,dt))),W[r].processed},"compileTask");let e=!0;for(const[r,s]of W.entries())t(r),e=e&&s.processed;return e},"compileTasks"),Pr=l(function(t,e){let r=e;nt().securityLevel!=="loose"&&(r=Oe(e)),t.split(",").forEach(function(s){st(s)!==void 0&&(de(s,()=>{window.open(r,"_self")}),Wt.set(s,r))}),ue(t,"clickable")},"setLink"),ue=l(function(t,e){t.split(",").forEach(function(r){let s=st(r);s!==void 0&&s.classes.push(e)})},"setClass"),Vr=l(function(t,e,r){if(nt().securityLevel!=="loose"||e===void 0)return;let s=[];if(typeof r=="string"){s=r.split(/,(?=(?:(?:[^"]*"){2})*[^"]*$)/);for(let c=0;c<s.length;c++){let d=s[c].trim();d.startsWith('"')&&d.endsWith('"')&&(d=d.substr(1,d.length-2)),s[c]=d}}s.length===0&&s.push(t),st(t)!==void 0&&de(t,()=>{Ve.runFunc(e,...s)})},"setClickFun"),de=l(function(t,e){Vt.push(function(){const r=document.querySelector(`[id="${t}"]`);r!==null&&r.addEventListener("click",function(){e()})},function(){const r=document.querySelector(`[id="${t}-text"]`);r!==null&&r.addEventListener("click",function(){e()})})},"pushFun"),zr=l(function(t,e,r){t.split(",").forEach(function(s){Vr(s,e,r)}),ue(t,"clickable")},"setClickEvent"),Rr=l(function(t){Vt.forEach(function(e){e(t)})},"bindFunctions"),Nr={getConfig:l(()=>nt().gantt,"getConfig"),clear:nr,setDateFormat:hr,getDateFormat:Tr,enableInclusiveEndDates:kr,endDatesAreInclusive:mr,enableTopAxis:yr,topAxisEnabled:vr,setAxisFormat:or,getAxisFormat:cr,setTickInterval:lr,getTickInterval:ur,setTodayMarker:dr,getTodayMarker:fr,setAccTitle:pe,getAccTitle:ge,setDiagramTitle:ve,getDiagramTitle:ye,setDisplayMode:gr,getDisplayMode:pr,setAccDescription:me,getAccDescription:ke,addSection:Sr,getSections:Cr,getTasks:Er,addTask:Wr,findTaskById:st,addTaskOrg:Or,setIncludes:xr,getIncludes:br,setExcludes:wr,getExcludes:_r,setClickEvent:zr,setLink:Pr,getLinks:Dr,bindFunctions:Rr,parseDuration:oe,isInvalidDate:ie,setWeekday:Mr,getWeekday:Ir,setWeekend:Ar};function Nt(t,e,r){let s=!0;for(;s;)s=!1,r.forEach(function(a){const c="^\\s*"+a+"\\s*$",d=new RegExp(c);t[0].match(d)&&(e[a]=!0,t.shift(1),s=!0)})}l(Nt,"getTaskTags");var Br=l(function(){pt.debug("Something is calling, setConf, remove the call")},"setConf"),re={monday:Fe,tuesday:Le,wednesday:Ae,thursday:Ie,friday:Me,saturday:Ee,sunday:Ce},Gr=l((t,e)=>{let r=[...t].map(()=>-1/0),s=[...t].sort((c,d)=>c.startTime-d.startTime||c.order-d.order),a=0;for(const c of s)for(let d=0;d<r.length;d++)if(c.startTime>=r[d]){r[d]=c.endTime,c.order=d+e,d>a&&(a=d);break}return a},"getMaxIntersections"),et,Hr=l(function(t,e,r,s){const a=nt().gantt,c=nt().securityLevel;let d;c==="sandbox"&&(d=yt("#i"+e));const g=c==="sandbox"?yt(d.nodes()[0].contentDocument.body):yt("body"),p=c==="sandbox"?d.nodes()[0].contentDocument:document,D=p.getElementById(e);et=D.parentElement.offsetWidth,et===void 0&&(et=1200),a.useWidth!==void 0&&(et=a.useWidth);const v=s.db.getTasks();let M=[];for(const y of v)M.push(y.type);M=K(M);const O={};let A=2*a.topPadding;if(s.db.getDisplayMode()==="compact"||a.displayMode==="compact"){const y={};for(const b of v)y[b.section]===void 0?y[b.section]=[b]:y[b.section].push(b);let _=0;for(const b of Object.keys(y)){const w=Gr(y[b],_)+1;_+=w,A+=w*(a.barHeight+a.barGap),O[b]=w}}else{A+=v.length*(a.barHeight+a.barGap);for(const y of M)O[y]=v.filter(_=>_.type===y).length}D.setAttribute("viewBox","0 0 "+et+" "+A);const F=g.select(`[id="${e}"]`),T=Te().domain([xe(v,function(y){return y.startTime}),be(v,function(y){return y.endTime})]).rangeRound([0,et-a.leftPadding-a.rightPadding]);function z(y,_){const b=y.startTime,w=_.startTime;let m=0;return b>w?m=1:b<w&&(m=-1),m}l(z,"taskCompare"),v.sort(z),G(v,et,A),we(F,A,et,a.useMaxWidth),F.append("text").text(s.db.getDiagramTitle()).attr("x",et/2).attr("y",a.titleTopMargin).attr("class","titleText");function G(y,_,b){const w=a.barHeight,m=w+a.barGap,E=a.topPadding,u=a.leftPadding,f=_e().domain([0,M.length]).range(["#00B9FA","#F95002"]).interpolate(De);q(m,E,u,_,b,y,s.db.getExcludes(),s.db.getIncludes()),Z(u,E,_,b),j(y,m,E,u,w,f,_),Q(m,E),$(u,E,_,b)}l(G,"makeGantt");function j(y,_,b,w,m,E,u){y.sort((o,i)=>o.vert===i.vert?0:o.vert?1:-1);const k=[...new Set(y.map(o=>o.order))].map(o=>y.find(i=>i.order===o));F.append("g").selectAll("rect").data(k).enter().append("rect").attr("x",0).attr("y",function(o,i){return i=o.order,i*_+b-2}).attr("width",function(){return u-a.rightPadding/2}).attr("height",_).attr("class",function(o){for(const[i,I]of M.entries())if(o.type===I)return"section section"+i%a.numberSectionStyles;return"section section0"}).enter();const h=F.append("g").selectAll("rect").data(y).enter(),x=s.db.getLinks();if(h.append("rect").attr("id",function(o){return o.id}).attr("rx",3).attr("ry",3).attr("x",function(o){return o.milestone?T(o.startTime)+w+.5*(T(o.endTime)-T(o.startTime))-.5*m:T(o.startTime)+w}).attr("y",function(o,i){return i=o.order,o.vert?a.gridLineStartPadding:i*_+b}).attr("width",function(o){return o.milestone?m:o.vert?.08*m:T(o.renderEndTime||o.endTime)-T(o.startTime)}).attr("height",function(o){return o.vert?v.length*(a.barHeight+a.barGap)+a.barHeight*2:m}).attr("transform-origin",function(o,i){return i=o.order,(T(o.startTime)+w+.5*(T(o.endTime)-T(o.startTime))).toString()+"px "+(i*_+b+.5*m).toString()+"px"}).attr("class",function(o){const i="task";let I="";o.classes.length>0&&(I=o.classes.join(" "));let S=0;for(const[P,L]of M.entries())o.type===L&&(S=P%a.numberSectionStyles);let C="";return o.active?o.crit?C+=" activeCrit":C=" active":o.done?o.crit?C=" doneCrit":C=" done":o.crit&&(C+=" crit"),C.length===0&&(C=" task"),o.milestone&&(C=" milestone "+C),o.vert&&(C=" vert "+C),C+=S,C+=" "+I,i+C}),h.append("text").attr("id",function(o){return o.id+"-text"}).text(function(o){return o.task}).attr("font-size",a.fontSize).attr("x",function(o){let i=T(o.startTime),I=T(o.renderEndTime||o.endTime);if(o.milestone&&(i+=.5*(T(o.endTime)-T(o.startTime))-.5*m,I=i+m),o.vert)return T(o.startTime)+w;const S=this.getBBox().width;return S>I-i?I+S+1.5*a.leftPadding>u?i+w-5:I+w+5:(I-i)/2+i+w}).attr("y",function(o,i){return o.vert?a.gridLineStartPadding+v.length*(a.barHeight+a.barGap)+60:(i=o.order,i*_+a.barHeight/2+(a.fontSize/2-2)+b)}).attr("text-height",m).attr("class",function(o){const i=T(o.startTime);let I=T(o.endTime);o.milestone&&(I=i+m);const S=this.getBBox().width;let C="";o.classes.length>0&&(C=o.classes.join(" "));let P=0;for(const[Y,J]of M.entries())o.type===J&&(P=Y%a.numberSectionStyles);let L="";return o.active&&(o.crit?L="activeCritText"+P:L="activeText"+P),o.done?o.crit?L=L+" doneCritText"+P:L=L+" doneText"+P:o.crit&&(L=L+" critText"+P),o.milestone&&(L+=" milestoneText"),o.vert&&(L+=" vertText"),S>I-i?I+S+1.5*a.leftPadding>u?C+" taskTextOutsideLeft taskTextOutside"+P+" "+L:C+" taskTextOutsideRight taskTextOutside"+P+" "+L+" width-"+S:C+" taskText taskText"+P+" "+L+" width-"+S}),nt().securityLevel==="sandbox"){let o;o=yt("#i"+e);const i=o.nodes()[0].contentDocument;h.filter(function(I){return x.has(I.id)}).each(function(I){var S=i.querySelector("#"+I.id),C=i.querySelector("#"+I.id+"-text");const P=S.parentNode;var L=i.createElement("a");L.setAttribute("xlink:href",x.get(I.id)),L.setAttribute("target","_top"),P.appendChild(L),L.appendChild(S),L.appendChild(C)})}}l(j,"drawRects");function q(y,_,b,w,m,E,u,f){if(u.length===0&&f.length===0)return;let k,h;for(const{startTime:S,endTime:C}of E)(k===void 0||S<k)&&(k=S),(h===void 0||C>h)&&(h=C);if(!k||!h)return;if(R(h).diff(R(k),"year")>5){pt.warn("The difference between the min and max time is more than 5 years. This will cause performance issues. Skipping drawing exclude days.");return}const x=s.db.getDateFormat(),n=[];let o=null,i=R(k);for(;i.valueOf()<=h;)s.db.isInvalidDate(i,x,u,f)?o?o.end=i:o={start:i,end:i}:o&&(n.push(o),o=null),i=i.add(1,"d");F.append("g").selectAll("rect").data(n).enter().append("rect").attr("id",function(S){return"exclude-"+S.start.format("YYYY-MM-DD")}).attr("x",function(S){return T(S.start)+b}).attr("y",a.gridLineStartPadding).attr("width",function(S){const C=S.end.add(1,"day");return T(C)-T(S.start)}).attr("height",m-_-a.gridLineStartPadding).attr("transform-origin",function(S,C){return(T(S.start)+b+.5*(T(S.end)-T(S.start))).toString()+"px "+(C*y+.5*m).toString()+"px"}).attr("class","exclude-range")}l(q,"drawExcludeDays");function Z(y,_,b,w){let m=Se(T).tickSize(-w+_+a.gridLineStartPadding).tickFormat(Ht(s.db.getAxisFormat()||a.axisFormat||"%Y-%m-%d"));const u=/^([1-9]\d*)(millisecond|second|minute|hour|day|week|month)$/.exec(s.db.getTickInterval()||a.tickInterval);if(u!==null){const f=u[1],k=u[2],h=s.db.getWeekday()||a.weekday;switch(k){case"millisecond":m.ticks(Qt.every(f));break;case"second":m.ticks(Zt.every(f));break;case"minute":m.ticks(jt.every(f));break;case"hour":m.ticks(Xt.every(f));break;case"day":m.ticks(qt.every(f));break;case"week":m.ticks(re[h].every(f));break;case"month":m.ticks(Ut.every(f));break}}if(F.append("g").attr("class","grid").attr("transform","translate("+y+", "+(w-50)+")").call(m).selectAll("text").style("text-anchor","middle").attr("fill","#000").attr("stroke","none").attr("font-size",10).attr("dy","1em"),s.db.topAxisEnabled()||a.topAxis){let f=Ye(T).tickSize(-w+_+a.gridLineStartPadding).tickFormat(Ht(s.db.getAxisFormat()||a.axisFormat||"%Y-%m-%d"));if(u!==null){const k=u[1],h=u[2],x=s.db.getWeekday()||a.weekday;switch(h){case"millisecond":f.ticks(Qt.every(k));break;case"second":f.ticks(Zt.every(k));break;case"minute":f.ticks(jt.every(k));break;case"hour":f.ticks(Xt.every(k));break;case"day":f.ticks(qt.every(k));break;case"week":f.ticks(re[x].every(k));break;case"month":f.ticks(Ut.every(k));break}}F.append("g").attr("class","grid").attr("transform","translate("+y+", "+_+")").call(f).selectAll("text").style("text-anchor","middle").attr("fill","#000").attr("stroke","none").attr("font-size",10)}}l(Z,"makeGrid");function Q(y,_){let b=0;const w=Object.keys(O).map(m=>[m,O[m]]);F.append("g").selectAll("text").data(w).enter().append(function(m){const E=m[0].split(We.lineBreakRegex),u=-(E.length-1)/2,f=p.createElementNS("http://www.w3.org/2000/svg","text");f.setAttribute("dy",u+"em");for(const[k,h]of E.entries()){const x=p.createElementNS("http://www.w3.org/2000/svg","tspan");x.setAttribute("alignment-baseline","central"),x.setAttribute("x","10"),k>0&&x.setAttribute("dy","1em"),x.textContent=h,f.appendChild(x)}return f}).attr("x",10).attr("y",function(m,E){if(E>0)for(let u=0;u<E;u++)return b+=w[E-1][1],m[1]*y/2+b*y+_;else return m[1]*y/2+_}).attr("font-size",a.sectionFontSize).attr("class",function(m){for(const[E,u]of M.entries())if(m[0]===u)return"sectionTitle sectionTitle"+E%a.numberSectionStyles;return"sectionTitle"})}l(Q,"vertLabels");function $(y,_,b,w){const m=s.db.getTodayMarker();if(m==="off")return;const E=F.append("g").attr("class","today"),u=new Date,f=E.append("line");f.attr("x1",T(u)+y).attr("x2",T(u)+y).attr("y1",a.titleTopMargin).attr("y2",w-a.titleTopMargin).attr("class","today"),m!==""&&f.attr("style",m.replace(/,/g,";"))}l($,"drawToday");function K(y){const _={},b=[];for(let w=0,m=y.length;w<m;++w)Object.prototype.hasOwnProperty.call(_,y[w])||(_[y[w]]=!0,b.push(y[w]));return b}l(K,"checkUnique")},"draw"),Ur={setConf:Br,draw:Hr},qr=l(t=>`
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
`,"getStyles"),Xr=qr,Qr={parser:ir,db:Nr,renderer:Ur,styles:Xr};export{Qr as diagram};
