class A {
	a() : Int {3};
};

class Main {
	a : A;
	main() : Int {a@A.a()};
};