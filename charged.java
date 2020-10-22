import java.util.*;
class charged
{
public static void main(String args[])
{
Scanner sc=new Scanner(System.in);
ArrayList<Integer> al=new ArrayList<>();
int t=sc.nextInt();
for(int i=0;i<t;i++)
{
int n=sc.nextInt();
int a[]=new int[n];
for(int j=0;j<n;j++)
{
a[j]=sc.nextInt();
}

int l=a.length;
for(int j=0;j<n;j++)
{
if(a[j]>l)
{
al.add(a[j]);
}
}

int sum=0;
for(int k=0;k<al.size();k++)
{
sum+=al.get(k);
}
System.out.println(sum);
al.clear();
}

}
}