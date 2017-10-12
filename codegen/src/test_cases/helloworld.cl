class A {
	a : Int;
	b() : Int {3};
};

class B inherits A {bb() : Int { 3};};

class C inherits B {};

class Main {
	a : Int <- 5;
	b : Int <- 3;
	c : C;
	str : String;
	main2(cc : Int, d : Int) : Int {
		{cc <- 3; cc <- 6; new IO@IO.out_int(cc); 3;}
	};
	main() : Int {
		{
			self@Main.main2(5, 6);
			a <- 6;
			new IO@IO.out_int(a);
			6+7;
		}
	};
};
