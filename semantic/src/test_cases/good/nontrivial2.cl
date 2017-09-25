-- Class for fractions (+ve) Any operations done on invalid frac are not defined and may lead to error
class Frac inherits IO{
        num : Int;
        den : Int;

        -- init func
        init(a: Int, b : Int) : Bool{
                {
                        if b = 0 then { num <- 1; den <- 1; out_string("Invalid frac \n"); false;}         --initialize
                        else {num <- a;}
                        fi;
                        den <- b;
                        true;
                }
        };
        --return numertor
        getNumerator() : Int{
                num
        };
        --return denominator
        getDenominator() : Int{
                den
        };
        --return gcd
        gcd(a: Int,b : Int) : Int{
                if a < b then gcd_r(b,a)                                        --Wrapper fun
                else gcd_r(a,b)
                fi
        };
        --gcd recursive
        gcd_r(a: Int, b: Int) : Int{
                if b=0 then a
                else gcd_r(b, mod(a,b))
                fi
        };
        -- modulo fn
        mod(a : Int, b : Int) : Int{
                a - b*(a/b)
        };
        --Function to add 2 fractions
        add(f : Frac): SELF_TYPE {
                let g : Int in{
                        num <- num*f.getDenominator() + f.getNumerator()*den;
                        den <- den * f.getDenominator();
                        g <- gcd(num,den);                                      --Convert to simplest form
                        num <- num/g;
                        den <- den/g;
                        self;
                }
        };
        -- fn for >=
        gteq(f: Frac) : Bool{
                if den*f.getNumerator() <= num*f.getDenominator() then true
                else false
                fi
        };
        --Function to sub 2 fractions if first fraction is less than second fraction - undefined behaviour
        sub(f : Frac): SELF_TYPE {
                let g : Int in{
                        if gteq(f) then{
                                num <- num*f.getDenominator() - f.getNumerator()*den;
                                den <- den * f.getDenominator();
                                g <- gcd(num,den);                              --Convert to simplest form
                                num <- num/g;
                                den <- den/g;
                                self;
                        }
                        else self
                        fi;
                }
        };
        --Funcition to multiply
        mul(f : Frac) : SELF_TYPE{
                let g : Int in{
                        num <- num * f.getNumerator();
                        den <- den * f.getDenominator();
                        if num = 0 then { self;}                                -- * 0
                        else {den <- den;}
                        fi;
                        g <- gcd(num,den);                                      --Convert to simplest form
                        num <- num/g;
                        den <- den/g;
                        self;
                }
        };
        --Funcition to divide division by 0 not defined
        div(f : Frac) : SELF_TYPE{
                let g : Int in{
                        num <- num * f.getDenominator();
                        den <- den * f.getNumerator();
                        if den = 0 then { den <- 1; num <- 1;  out_string("Error -- div by zero \n");}             -- /0
                        else { den <- den;}
                        fi;
                        g <- gcd(num,den);                                      --Convert to simplest form
                        num <- num/g;
                        den <- den/g;

                        self;
                }
        };
        --fn to print the fraction
        show(): SELF_TYPE{
                {
                        out_int(num);
                        out_string("/");
                        out_int(den);
                        self;
                }
        };
};
class Main inherits Frac{
        main() : SELF_TYPE{
                --take ip as 2 fraction and perform all operation
                let f : Frac <- new Frac, g : Frac <- new Frac ,forg : Frac <- new Frac ,n1 : Int , d1 : Int , n2 : Int, d2 : Int in{
                        out_string("Please enter the numerator and denominator for fraction 1 \n");
                        n1 <- in_int();
                        d1 <- in_int();
                        out_string("Please enter the numerator and denominator for fraction 2 \n");
                        n2 <- in_int();
                        d2 <- in_int();

                        f.init(n1,d1);
                        g.init(n2,d2);
                        forg.init(n1,d1);

                        f.add(g);
                        out_string("The sum is ");
                        f.show();
                        f.sub(g);
                        out_string("\n");

                        f.sub(g);
                        out_string("The difference is ");
                        f.show();
                        f.init(n1,d1);
                        out_string("\n");

                        f.mul(g);
                        out_string("The product is ");
                        f.show();
                        f.init(n1,d1);
                        out_string("\n");

                        f.div(g);
                        out_string("The quotient is ");
                        f.show();
                        f.init(n1,d1);
                        out_string("\n");

                        self;
                }
        };
};
