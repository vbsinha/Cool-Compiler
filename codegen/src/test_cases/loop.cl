class Main {

	i : Int;
	io : IO <- new IO;
	main() : Object {{
		while i < 10 loop
		{
			io@IO.out_int(i);
			io@IO.out_string("\n");
			i <- i+1;
		}
		pool;
	}};

};
