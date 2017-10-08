-- calculate the first n fibo numbers
class Main inherits IO {
        main(): Object {
                (let a : Int, b : Int, n : Int in{
                        out_string("Please enter the n (>0 and a natural number <25) upto which you want to calculate fibonacci numbers \n");
                        out_string("The fibonacci series is assumed to begin with index 0 \n");
                        n <- in_int();                                          -- get the n
                        a <- 0;                                                 --Set the first 2 varibales
                        b <- 1;
                        out_int(0);
                        out_string("\n");
                        out_int(1);
                        out_string("\n");
                        while 1 < n loop{                                       -- now loooping through calculate the next values of fibo numbers
                                b <- a+b;                                       --b = a+b;
                                a <- b-a;                                       --a = b-a
                                out_int(b);                                     --Print the values
                                out_string("\n");
                                n <- n-1;
                        }
                        pool;
                        self;
                })
        };
};
