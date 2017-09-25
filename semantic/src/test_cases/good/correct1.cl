(*)using namespace std;
int main(){
        int n, fact = 1;
        cout<<"Enter the number"<<endl;
        cin>>n;
        while(n>1){
                fact = fact * n;
                n--;
        }
        cout<<"The factorial of n is "<<fact<"\n";
}*)


-- Program to calculate n!
class Main inherits IO {
        main(): Object {
                (let n : Int , fact : Int <- 1 in {
                        out_string("Enter a whole number <11 \n");
                        n <- in_int();
                        while 1 < n loop {                                      --while(n>1){
                                fact <- fact * n;                               --fact = fact * n;
                                n <- n - 1;                                     --n--;
                        }
                        pool;
                        out_string("The factorial of n is ");                   -- Output ans
                        out_int(fact);
                        out_string("\n");
                }
                )
        };
};
