class Main inherits IO {
	main() : Int {{
		self@IO.out_string("Hello world!\n");
		self@IO.out_string(self@Object.type_name());
		self@IO.out_string("\n");
		2;
	}};
};