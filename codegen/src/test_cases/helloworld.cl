class A {
	a : Int;
	b() : Int {3};
};

class B inherits A {bb() : Int { 3};};

class C inherits B {};

class Main {
	a : Int <- 5;
	b : Int <- 3;
	c : B;
	d : Object <- new Object;
	e : IO <- new IO;
	f : Object <- new B;
	str : String;
	g : B;
	main2(cc : Int, d : Int) : Int {
		{cc <- 3; cc <- 6; new IO@IO.out_int(cc); 3;}
	};
	main() : Object {
		{
			if isvoid g then e@IO.out_string("IS NULL") else e@IO.out_string("NOT NULL") fi;
			--c@A.b();
			e@IO.out_int(5/1);
			c <- new C;
			self@Main.main2(5, 6);
			a <- 6;
			e@IO.out_int(a);
			str <- e@IO.in_string();
			e@IO.out_string(str);
			e@IO.out_string("\n");
			e@IO.out_int(str@String.length());
			5;
		}
	};
};
