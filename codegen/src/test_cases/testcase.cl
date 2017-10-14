class A {
	fact(n : Int) : Int {
		if n < 0 then 1 else
		if n = 0 then 1 else 
		n*self@A.fact(n-1) fi fi
	};
	value : Int;
	facti(n : Int) : Int {
		if n < 0 then 1 else {
			value <- 1;
			while 0 < n loop {
				value <- value * n;
				n <- n-1;
				--new IO@IO.out_int(n);
			} pool;
			value;
		} fi
	};
};

class B inherits IO {
	a : Int;
	b : String;
	f : A <- new A;
	input() : String {{
		self@IO.out_string("Enter your name : ");
		b <- self@IO.in_string();
		self@IO.out_string("Enter your age : ");
		a <- self@IO.in_int();
		b;
	}};
	output() : Object {{
		self@IO.out_string("Hi ");
		self@IO.out_string(b);
		self@IO.out_string(", Factorial of your age is ");
		self@IO.out_int(f@A.fact(a));
		self@IO.out_string("\n");
	}};
};

class C inherits B {
	input() : String {{
		self@IO.out_string("Overidden function does nothing\n");
		"Return";
	}};
};

class Main {
	a : A <- new A;
	c : C <- new C;
	choice : Int;
	io : IO <- new IO;
	n : Int;
	main() : Object {{
		io@IO.out_string("Enter choice : \n");
		io@IO.out_string("1. Find factorial of some number\n");
		io@IO.out_string("2. Find factorial of your age\n");
		choice <- io@IO.in_int();
		if choice = 1 then {
			io@IO.out_string("Enter a number : ");
			n <- io@IO.in_int();
			io@IO.out_int(a@A.facti(n));
			io@IO.out_string("\n");
			new Object;
		} else if choice = 2 then {
			c@B.input();
			c@B.output();
			c@C.input();
			new Object;
		} else {
			io@IO.out_string("Invalid input\n");
			new Object;
		} fi fi;
		new Object;
	}};
	--main() : Object {new Object};
};