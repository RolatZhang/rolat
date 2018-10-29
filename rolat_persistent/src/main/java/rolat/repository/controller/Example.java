package rolat.repository.controller;


public class Example {

	public static void main(String args[]) {
		Test a=new Test();
		a.a=12;
		change(a);
		System.out.println(a.a);
	}
	
	public static void change(Test a) {
		a.a=18;
		a=new Test();
		a.a=13;
		System.out.println("a"+a.a);
	}
}