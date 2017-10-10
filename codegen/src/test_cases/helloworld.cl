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
		cc+d
	};
	main() : Int {
		{
			c@B.bb();
			isvoid c;
			if a < 3 then c else c fi;
			if b < 3 then if a < 3 then 5 else 2 fi else 3 fi;
			2+3;
			4+5;
			6+7;
		}
	};
};
