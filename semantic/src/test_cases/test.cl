class B inherits A {
	b() : Int {
		5
	};
	f(i : Int) : Int {
		2
	};
};

class A {
	a : Int;
	b() : Int {
		c <- 5
	};
	f() : String {
		""
	};
	c() : String {
		c <- a + "Str"
	};
	d() : IO {
		{
			a <- isvoid a;
			if (a) then (new IO) else (new String) fi;
			while (not false) loop a pool;
			while ("asd") loop out_string("Hello") pool;
			(new IO).out_string("Hello");
			self.c(5);
			(new B)@C.b();
			1;
		}
	};
};
