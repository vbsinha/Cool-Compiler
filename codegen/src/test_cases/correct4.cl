-- program  for custom operators > and >=
class Main inherits IO {
        main(): Object {
                (let a : Int, b : Int in{
                        out_string("Please enter 2 integers \n");               --take 2 numbers as ip
                        a <- in_int();
                        b <- in_int();
                        if gt(a,b)                                              -- Print if a > b
                                then {
                                        out_int(a);
                                        out_string(" is greater than ");
                                        out_int(b);
                                        out_string("\n");
                                }
                        else {
                                out_int(a);
                                out_string(" is not greater than ");
                                out_int(b);
                                out_string("\n");
                        }
                        fi;
                        if gteq(a,b)                                            -- Print if a >= b
                        then {
                                out_int(a);
                                out_string(" is greater than or equal to ");
                                out_int(b);
                                out_string("\n");
                        }
                        else {
                                out_int(a);
                                out_string(" is not greater than or equal to ");
                                out_int(b);
                                out_string("\n");
                        }
                        fi;
                })
        };
        -- greater than fn
        gt (a : Int, b: Int) : Bool{
                if a <= b then false                                            -- Uses not of <=
                else true
                fi
        };
        -- greater than equal to fn
        gteq (a : Int, b: Int) : Bool{
                if a < b then false                                             -- Uses not of <
                else true
                fi
        };
};
