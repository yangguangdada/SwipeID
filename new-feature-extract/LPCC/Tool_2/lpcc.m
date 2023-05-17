function[y]=lpcc(input)
p=12;
NJ=256;
for m=0:1:p
    r(m+1)=0;
    for i=1:1:(NJ-m)
        r(m+1)=r(m+1)+input(i)*input(i+m);
    end
end
R=zeros(1,p);
R(1,(1:p))=r(1,(2:p+1));
R=R';
E=zeros(1,p);
k=E;
a=zeros(p,p);
e=r(1);          %Durbin算法初始化
k(1)=r(2)/r(1);
a(1,1)=k(1);
E(1)=(1-k(1)^2)*e;
l=2;
for i=2:1:12;    %算法开始
    m_t=sum(a((i-1:-1:1),i-1).*R((1:1:i-1),1));
    k(i)=(R(i)-m_t)/E(i-1);
    a(i,i)=k(i);
    a((1:1:i-1),l)=a((1:1:i-1),i-1)-k(i)*a((i-1:-1:1),i-1);
    E(i)=(1-k(i)^2)*E(i-1);
    l=l+1;
end
y=a(:,12)';            %预测系数
A=zeros(1,12);         %计算LPCC参数，使用12阶
A=y(1,end:-1:1);
c=zeros(1,12);
c(1)=A(1);
for n=2:1:12
    c(n)=A(n)+sum((1:1:n-1)/12.*c(1:1:n-1).*A(n-(1:1:n-1)));
end      
y=c;            