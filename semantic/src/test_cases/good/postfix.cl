(*
Postfix Expression evaluator
Author : Prateek Kumar
Email : cs15btech11031@iith.ac.in
*)

(* atoi.cl is taken from given examples and used for interger to string and vice-versa conversions *)

class Stack {
	-- Stack implementation using string
	-- Integers are separeted by | character in stack
	string : String <- "|";
	a2i : A2I <- new A2I;
	io : IO <- new IO;

	-- Push to stack
	push(s : Int) : SELF_TYPE {
		{
			-- Conactenate to string along with | character
			string <- string.concat(a2i.i2a(s));
			string <- string.concat("|");
			self;
		}
	};

	-- Pop from stack
	pop() : Int {
		-- Check if stack is empty and print error
		if isEmpty() then {
			io.out_string("Cannot pop from empty stack\n");
			abort();
			0;
		} else let i : Int <- string.length() - 2 in {
			-- Remove a integer from back of string and | character
			while not string.substr(i, 1) = "|" loop i <- i - 1 pool;
			let temp : String <- string.substr(i+1, string.length()-i-2) in {
				string <- string.substr(0, i+1);
				a2i.a2i(temp);
			};
		} fi
	};

	-- Check if stack is empty
	isEmpty() : Bool {
		string = "|"
	};
};

class Main inherits IO {
	-- Expression given by user
	expr : String;
	-- Length of the expression
	len : Int;
	-- Stack used to evaluate postfix expression
	stack : Stack <- new Stack;
	ans : Int;

	main() : Object {
		{
			-- Take input the expression
			expr <- in_string();
			len <- expr.length();
			-- Loop over all characters and evaluate when opaertor is found
			let i : Int <- 0, char : String, oper1 : Int, oper2 : Int in while i < len loop
			{
				char <- expr.substr(i, 1);
				-- If char is 0 to 9 then push to stack
				if char = "0" then stack.push(0) else
				if char = "1" then stack.push(1) else
				if char = "2" then stack.push(2) else
				if char = "3" then stack.push(3) else
				if char = "4" then stack.push(4) else
				if char = "5" then stack.push(5) else
				if char = "6" then stack.push(6) else
				if char = "7" then stack.push(7) else
				if char = "8" then stack.push(8) else
				if char = "9" then stack.push(9) else
				-- For operators pop two operands and push the result into the stack
				if char = "+" then {
					oper2 <- stack.pop();
					oper1 <- stack.pop();
					stack.push(oper1 + oper2);
				} else
				if char = "-" then {
					oper2 <- stack.pop();
					oper1 <- stack.pop();
					stack.push(oper1 - oper2);
				} else
				if char = "*" then {
					oper2 <- stack.pop();
					oper1 <- stack.pop();
					stack.push(oper1 * oper2);
				} else
				if char = "/" then {
					oper2 <- stack.pop();
					oper1 <- stack.pop();
					stack.push(oper1 / oper2);
				} else {
					-- Inavlid expression if contains charcaters other than 0-9,+,-,*,/
					out_string("Invalid expression\n");
					i <- len;
				} fi fi fi fi fi fi fi fi fi fi fi fi fi fi;
				i <- i + 1;
			} pool;
			-- Pop the answer from stack
			ans <- stack.pop();
			-- If stack is not empty and print error else answer
			if not stack.isEmpty() then out_string("Inavlid expression") else out_int(ans) fi;
			out_string("\n");
		}
	};
};
