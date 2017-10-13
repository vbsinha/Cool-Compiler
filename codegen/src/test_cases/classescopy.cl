class A {
	a : Int;
	set(i : Int) : Int {
		a <- i
		-- 4
	};
	get() : Int {
		a
	};
};

class B {
	a : A <- new A;
	set(i : Int) : Int {
		a@A.set(i)
		-- 4
	};
	get() : Int {
		a@A.get()
	};
};

class Main inherits IO {
	a : B <- new B;
	b : B <- new B;
	c : A <- new A;
	d : A <- new A;
	e : String;
	main() : IO {
		{
			self@IO.out_string(e);
			e <- "Hello";
			self@IO.out_string(e);
			a@B.set(5);
			b@B.set(3);
			a <- b;
			a@B.set(2);
			self@IO.out_int(b@B.get());
			c@A.set(5);
			d@A.set(3);
			c <- d;
			c@A.set(8);
			self@IO.out_int(d@A.get());
			self@IO.out_int(5);
		}
	};
};
