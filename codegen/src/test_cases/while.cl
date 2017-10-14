class Main {
	a : Object;
	main() : Object {{
		a <- while true loop {new IO@IO.out_int(5);} pool;
		a;
	}};
};
