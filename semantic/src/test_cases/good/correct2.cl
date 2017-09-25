-- Takes the full name and outputs abriged name
class Main inherits IO{
        main(): Object {
                let fr_name : String, mid_name : String, lt_name : String, abr_name : String in {
                        out_string("Enter Firstname : ");                             -- Get first name
                        fr_name <- in_string();
                        out_string("Middle name : ");                           -- Get mid name
                        mid_name <- in_string();
                        out_string("Last name : ");                             -- Get last name
                        lt_name <- in_string();
                        abr_name <- fr_name.substr(0,1).concat(" ").concat(mid_name.substr(0,1)).concat(" ").concat(lt_name);   --Calculate abr_name
                        out_string(abr_name);                                   -- Print abr_name
                        out_string("\n");
                }
        };
};
