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
	a : A;
	init() : Int {
		{a <- new A; 1;}
	};
	set(i : Int) : Int {
		a@A.set(i)
		-- 4
	};
	get() : Int {
		a@A.get()
	};
};

class Main inherits IO {
	a : B;
	b : B;
	c : A;
	d : A;
	init() : Int {
		{
			a <- new B;
			a@B.init();
			b <- new B;
			b@B.init();
			c <- new A;
			d <- new A;
			1;
		}
	};
	main() : IO {
		{
			self@Main.init();
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
