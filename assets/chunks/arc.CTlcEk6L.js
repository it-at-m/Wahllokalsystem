import{w as ln,c as Q}from"./path.CbwjOpE9.js";import{aM as an,aN as X,aO as O,aP as rn,aQ as y,T as on,aR as B,aS as _,aT as un,aU as t,aV as sn,aW as tn,aX as fn}from"../app.DdECDOU9.js";function cn(l){return l.innerRadius}function yn(l){return l.outerRadius}function gn(l){return l.startAngle}function mn(l){return l.endAngle}function pn(l){return l&&l.padAngle}function dn(l,h,D,S,v,R,U,a){var E=D-l,i=S-h,n=U-v,m=a-R,r=m*E-n*i;if(!(r*r<y))return r=(n*(h-R)-m*(l-v))/r,[l+r*E,h+r*i]}function K(l,h,D,S,v,R,U){var a=l-D,E=h-S,i=(U?R:-R)/B(a*a+E*E),n=i*E,m=-i*a,r=l+n,s=h+m,f=D+n,c=S+m,V=(r+f)/2,o=(s+c)/2,p=f-r,g=c-s,A=p*p+g*g,T=v-R,P=r*c-f*s,I=(g<0?-1:1)*B(fn(0,T*T*A-P*P)),M=(P*g-p*I)/A,N=(-P*p-g*I)/A,w=(P*g+p*I)/A,d=(-P*p+g*I)/A,x=M-V,e=N-o,u=w-V,W=d-o;return x*x+e*e>u*u+W*W&&(M=w,N=d),{cx:M,cy:N,x01:-n,y01:-m,x11:M*(v/T-1),y11:N*(v/T-1)}}function vn(){var l=cn,h=yn,D=Q(0),S=null,v=gn,R=mn,U=pn,a=null,E=ln(i);function i(){var n,m,r=+l.apply(this,arguments),s=+h.apply(this,arguments),f=v.apply(this,arguments)-rn,c=R.apply(this,arguments)-rn,V=un(c-f),o=c>f;if(a||(a=n=E()),s<r&&(m=s,s=r,r=m),!(s>y))a.moveTo(0,0);else if(V>on-y)a.moveTo(s*X(f),s*O(f)),a.arc(0,0,s,f,c,!o),r>y&&(a.moveTo(r*X(c),r*O(c)),a.arc(0,0,r,c,f,o));else{var p=f,g=c,A=f,T=c,P=V,I=V,M=U.apply(this,arguments)/2,N=M>y&&(S?+S.apply(this,arguments):B(r*r+s*s)),w=_(un(s-r)/2,+D.apply(this,arguments)),d=w,x=w,e,u;if(N>y){var W=sn(N/r*O(M)),C=sn(N/s*O(M));(P-=W*2)>y?(W*=o?1:-1,A+=W,T-=W):(P=0,A=T=(f+c)/2),(I-=C*2)>y?(C*=o?1:-1,p+=C,g-=C):(I=0,p=g=(f+c)/2)}var j=s*X(p),z=s*O(p),F=r*X(T),G=r*O(T);if(w>y){var H=s*X(g),J=s*O(g),L=r*X(A),Y=r*O(A),q;if(V<an)if(q=dn(j,z,L,Y,H,J,F,G)){var Z=j-q[0],$=z-q[1],k=H-q[0],b=J-q[1],nn=1/O(tn((Z*k+$*b)/(B(Z*Z+$*$)*B(k*k+b*b)))/2),en=B(q[0]*q[0]+q[1]*q[1]);d=_(w,(r-en)/(nn-1)),x=_(w,(s-en)/(nn+1))}else d=x=0}I>y?x>y?(e=K(L,Y,j,z,s,x,o),u=K(H,J,F,G,s,x,o),a.moveTo(e.cx+e.x01,e.cy+e.y01),x<w?a.arc(e.cx,e.cy,x,t(e.y01,e.x01),t(u.y01,u.x01),!o):(a.arc(e.cx,e.cy,x,t(e.y01,e.x01),t(e.y11,e.x11),!o),a.arc(0,0,s,t(e.cy+e.y11,e.cx+e.x11),t(u.cy+u.y11,u.cx+u.x11),!o),a.arc(u.cx,u.cy,x,t(u.y11,u.x11),t(u.y01,u.x01),!o))):(a.moveTo(j,z),a.arc(0,0,s,p,g,!o)):a.moveTo(j,z),!(r>y)||!(P>y)?a.lineTo(F,G):d>y?(e=K(F,G,H,J,r,-d,o),u=K(j,z,L,Y,r,-d,o),a.lineTo(e.cx+e.x01,e.cy+e.y01),d<w?a.arc(e.cx,e.cy,d,t(e.y01,e.x01),t(u.y01,u.x01),!o):(a.arc(e.cx,e.cy,d,t(e.y01,e.x01),t(e.y11,e.x11),!o),a.arc(0,0,r,t(e.cy+e.y11,e.cx+e.x11),t(u.cy+u.y11,u.cx+u.x11),o),a.arc(u.cx,u.cy,d,t(u.y11,u.x11),t(u.y01,u.x01),!o))):a.arc(0,0,r,T,A,o)}if(a.closePath(),n)return a=null,n+""||null}return i.centroid=function(){var n=(+l.apply(this,arguments)+ +h.apply(this,arguments))/2,m=(+v.apply(this,arguments)+ +R.apply(this,arguments))/2-an/2;return[X(m)*n,O(m)*n]},i.innerRadius=function(n){return arguments.length?(l=typeof n=="function"?n:Q(+n),i):l},i.outerRadius=function(n){return arguments.length?(h=typeof n=="function"?n:Q(+n),i):h},i.cornerRadius=function(n){return arguments.length?(D=typeof n=="function"?n:Q(+n),i):D},i.padRadius=function(n){return arguments.length?(S=n==null?null:typeof n=="function"?n:Q(+n),i):S},i.startAngle=function(n){return arguments.length?(v=typeof n=="function"?n:Q(+n),i):v},i.endAngle=function(n){return arguments.length?(R=typeof n=="function"?n:Q(+n),i):R},i.padAngle=function(n){return arguments.length?(U=typeof n=="function"?n:Q(+n),i):U},i.context=function(n){return arguments.length?(a=n??null,i):a},i}export{vn as a};
