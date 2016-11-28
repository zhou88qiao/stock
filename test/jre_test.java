package test;

class person
{
	private String name;
	private int age=15;
	public person()
	{
		
	}
	public person (int age,String name)
	{
		this.name=name;
		this.age=age;
	}
	public void set_name(String name)
	{
		this.name=name;
	}
	public void set_age(int age)
	{
		this.age=age;
	}
	public void talk()
	{
		System.out.println("name:"+name+"age:"+age);
	}
	boolean compare(person p)
	{
		if(this.name.equals(p.name) && this.age==p.age)
			return true;
		else
			return false;
		
	}
	public void fun1()
	{
		System.out.println("1.Person{func()1}");
	}
	public void fun2()
	{
		System.out.println("2.Person{func()2}");
	}
}

class student extends person
{
	String school;
	public student()
	{
		System.out.println("student111 !");
		
	}
	public student(String name,int age,String school)
	{
		super.set_name(name);
		super.set_age(age);
		this.school=school;
		System.out.println("student 22!");
	}
	public void printstudent()
	{
		super.talk();
		//System.out.println("dfaf");
	}
	public void prinkk()
	{
		System.out.println("afdfdfasf");
	}
	public void talk()
	{
		System.out.println("school:"+school);
	}
	public void fun1()
	{
		System.out.println("3.student{func()1}");
	}
	public void fun3()
	{
		System.out.println("4.student{func()3}");
	}
}

public class jre_test {
	private int te;
	public void ll()
	{
		System.out.println("123456");
	}
	
	 public static void main(String[] args) {
		 jre_test jt = new jre_test();
		 jt.te=1;
		 jt.ll();
		 person te1=new person(45,"zah");
	//	 te1.name="111";
		 person te2=new person(34,"zah");
		 if(te1.compare(te2))
			 System.out.println("equal");
		 else
			 System.out.println("not equal");
	    
	 	student stu=new student("zhouqiao",27,"sy");
	 	stu.school="233";
	  	stu.talk();
	 	
	 	stu.printstudent();
	 	
	 	
	 	person p =new student();
	 	p.fun1();
	 	p.fun2();
	 	
	 	
	 	java_test jt1 = new java_test("111",12);
	 	jt1.age=14;
	 	
	 	try
	 	{
	 		if(jt1.age==14)
	 			throw new ArithmeticException("算术异常");
	 		else
	 			System.out.println("tetttt");
	 	}
	 	catch(ArithmeticException e)
	 	{
	 		System.out.println("抛出异常为："+e);
	 	}
	 } 	
	 	
}