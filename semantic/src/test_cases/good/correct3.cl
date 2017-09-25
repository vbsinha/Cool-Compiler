--Implementation of and or and xor (since they are not directly avaliable)
class Main inherits IO {
        main(): Object {
                (let c : Bool in{
                        -- Create a table for the 4 possible combinations
                        --And for each combination populate the table
                        out_string("Printing table for boolean operators \n op1\top2\tand\tor\txor \n");
                        out_string("true\ttrue\t");
                        print_bool(boolean_and(true,true));
                        out_string("\t");
                        print_bool(boolean_or(true,true));
                        out_string("\t");
                        print_bool(boolean_xor (true,true));
                        out_string("\t\n");
                        out_string("true\tfalse\t");
                        print_bool(boolean_and(true,false));
                        out_string("\t");
                        print_bool(boolean_or(true,false));
                        out_string("\t");
                        print_bool(boolean_xor (true,false));
                        out_string("\t\n");
                        out_string("false\ttrue\t");
                        print_bool(boolean_and(false,true));
                        out_string("\t");
                        print_bool(boolean_or(false,true));
                        out_string("\t");
                        print_bool(boolean_xor (false,true));
                        out_string("\t\n");
                        out_string("false\tfalse\t");
                        print_bool(boolean_and(false,false));
                        out_string("\t");
                        print_bool(boolean_or(false,false));
                        out_string("\t");
                        print_bool(boolean_xor (false,false));
                        out_string("\t\n");
                })
        };
        -- Prints a bool
        print_bool (a : Bool) : Bool{
                if a = true then { out_string("true"); true;}           -- If bool is true print true
                else {out_string("false"); true;}                       -- Else print false
                fi
        };
        -- Takes boolean and of a, b
        boolean_and (a : Bool, b : Bool) : Bool {
                if a = false then false                                 --If a is F retun F
                else if b = false then false                            --If b is F return F
                        else true                                       -- Else T
                fi fi
        };
        -- Takes boolean or of a, b
        boolean_or (a: Bool, b : Bool) : Bool {
                if a = true then true                                   --If a is T retun T
                else if b = true then true                              --If b is T return T
                        else false                                      -- Else F
                fi fi
        };
        -- Takes boolean xor of a, b
        boolean_xor (a: Bool, b : Bool) : Bool {
                if a = true
                        then {
                                if b = false then true                  -- T xor F -> T
                                else false                              -- T xor T -> F
                                fi;
                        }
                else if b = true then true                              -- F xor T -> T
                        else false                                      -- F xor F -> F
                fi fi
        };
};
