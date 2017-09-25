class A {
	a : Int;
	b() : Int {
		5
	};
};

class B inherits A {
	a : String;
	b() : String {
		""
	};
};

class C inherits A {
	b(i : Int) : Int {
		i
	};
};
