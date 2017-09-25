class Math inherits IO{

        s : Int;

        --Fn for recursive gcd calc
        gcd_r(a: Int, b: Int) : Int{
                if b=0 then a
                else gcd_r(b, mod(a,b))                                         -- Use gcd(a,b) = gcd(b,a%b);
                fi
        };
        --Fn for mod  a > 0,b > 2
        mod(a : Int, b : Int) : Int{
                a - b*(a/b)
        };
        -- Fn for lcm a,b >1
        lcm(a: Int, b: Int) : Int{
                a*b / gcd(a,b)                                                  -- Use lcm = ab / gcd
        };
        --  Fn for gcd a,b >1
        gcd(a: Int,b : Int) : Int{
                if a < b then gcd_r(b,a)                                        -- Cover fn for gcd
                else gcd_r(a,b)
                fi
        };
        --Fn for absolute value
        abs(a : Int) : Int{
                if a < 0 then (~1)*a                                            -- for negative give -a
                else a                                                          -- Else a
                fi
        };
        --Fn for max
        max(a : Int, b : Int) : Int {
                if b < a then a                                                 -- Check and appropriately return
                else b
                fi
        };
        --Fn for min
        min(a : Int, b : Int) : Int {
                if b < a then b                                                 -- Check and appropriately return
                else a
                fi
        };
        --Fn for int greater than eq to sqrt for positive numbers
        sqrt(a : Int) : Int{
                let i : Int <- 1 in{                                            -- Keep incrementing i untill i^2 < a
                        while i*i < a loop {i <- i+1;}
                        pool;
                        i;
                }
        };
        -- Fn for power of positive number
        pow(a : Int, b : Int) : Int {
                let result : Int <- 1 in{
                        while 0 < b loop{                                       -- Multiply a b times
                                result <- result*a;
                                b <- b-1;
                        }
                        pool;
                        result;
                }
        };
        -- Check if p is prime for positive number
        isPrime (p : Int) : Bool{
                (let i : Int <- 2 ,result : Bool <- true in{
                        while i < p loop{                                       --Check with all numbers less than p
                                if mod(p,i) = 0 then {
                                        i <- i+1;
                                        result <- false;
                                }
                                else {i <- i+1; }
                                fi;
                        }
                        pool;
                        result;
                })
        };
        -- Bitwise shift_left for positive number
        shift_left (a: Int, shift : Int) : Int{
                a*pow(2,shift)
        };
        -- Bitwise shift_right for positive number
        shift_right (a: Int, shift : Int) : Int{
                let result : Int <- a in{
                        while 0 < shift loop{
                                result <- result/2;
                                shift <- shift-1;
                        }
                        pool;
                        result;
                }
        };
        --Fn to print bools
        print_bool (a : Bool) : Bool{
                if a = true then { out_string("true"); true;}
                else {out_string("false"); true;}
                fi
        };
};
class Main inherits Math{
        main() : SELF_TYPE{
                let a : Int in{
                        --test for all funs to verify they are working
                        out_string("gcd of 34 and 6 : ");
                        out_int(gcd(34,6));
                        out_string("\n");
                        out_string("gcd of 4 and 28 : ");
                        out_int(gcd(4,28));
                        out_string("\n");
                        out_string("gcd of 1 and 3 : ");
                        out_int(gcd(1,3));
                        out_string("\n");

                        out_string("lcm of 34 and 6 : ");
                        out_int(lcm(34,6));
                        out_string("\n");
                        out_string("lcm of 4 and 28 : ");
                        out_int(lcm(4,28));
                        out_string("\n");
                        out_string("lcm of 1 and 3 : ");
                        out_int(lcm(1,3));
                        out_string("\n");

                        out_string("3^4 : ");
                        out_int(pow(3,4));
                        out_string("\n");
                        out_string("1^4 : ");
                        out_int(pow(1,4));
                        out_string("\n");

                        out_string("abs(~2) : ");
                        out_int(abs(~2));
                        out_string("\n");
                        out_string("abs(2) : ");
                        out_int(abs(2));
                        out_string("\n");

                        out_string("mod(34,6) : ");
                        out_int(mod(34,6));
                        out_string("\n");
                        out_string("max(34,6) : ");
                        out_int(max(34,6));
                        out_string("\n");
                        out_string("min(34,6) : ");
                        out_int(min(34,6));
                        out_string("\n");

                        out_string("sqrt(4) : ");
                        out_int(sqrt(4));
                        out_string("\n");
                        out_string("sqrt(5) : ");
                        out_int(sqrt(5));
                        out_string("\n");

                        out_string("Is 7 prime : ");
                        print_bool(isPrime(7));
                        out_string("\n");
                        out_string("Is 8 prime : ");
                        print_bool(isPrime(8));
                        out_string("\n");

                        out_string("shift_right(5,2) : ");
                        out_int(shift_right(5,2));
                        out_string("\n");
                        out_string("shift_left(5,2) : ");
                        out_int(shift_left(5,2));
                        out_string("\n");
                }
        };
};
