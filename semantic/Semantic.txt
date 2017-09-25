Semantic Analyzer Report 


Sinha Vaibhav Birendrakumar CS15BTECH11034
Prateek Kumar CS15BTECH11031


General Description and Assumptions:


The semantic analyzer developed does a semantic analysis of a COOL program. Please note that it does it does not handle SELF_TYPE.


Design Decisions:


1. We maintain a ClassTable that contains a HashMap from Class’s name to its ClassInfo. ClassInfo basically contains the class (ie. It has a hashmap for attribute as well as for methods). 
2. Change have been made to AST java file, itself. A function handle has been added to most of the classes. It leverages the power of object oriented programming and without much effort we are able to jump to the correct classes’s handle.
3. A BFS traversal generates the inheritance graph.
4. An extra error java file is declared  to make error handling simpler across classes. 


High Level Code Structure:


The following classes have been added : 
1. ClassTable.java -> basically  class that maintains the logistics of the class. It stores the methods and attributes, and checks for a few errors as well. 
2. ClassInfo.java -> A simple class that will contain the data of an individual class.
3. Error.java -> A class developed to help in maintaining errors.
The following classes were modified :
1. Semantic.java -> Performs the core work of semantic analysis.
2. AST.java -> Has been extended to support semantic analysis.


The following is being done by the semantic analyzer:
1. Create Inheritance graph.
2. Detect cycles in the graph.
3. Create the ClassTable that helps with the logistics and points a few errors.
4. Then the program is again traversed this time to detect return types, expression semantics.


Detailed low level details of the program can be easily obtained from the comments.


Creating and validating the inheritance graph:


The semantic analyzer does a BFS over the program’s classes so as to construct a unique tree, having “Object” as its root. So, while if any visited node is seen (cycle is detected), an error is reported. The creation of inheritance graph algorithm also detects invalid inheritance and redefinition of functions.
2 passes are utilized in completing this process. The first is to log all the classes, and the second one is when the inheritance graph is created. The first is required as the classes will be declared in any order regardless of inheritance order.
Once the inheritance graph is created, we populate the ClassTable


Inheritance Checks:


Attributes: Attributes once defined, in parent class can not be redefined in child class. This is being checked in the second pass. In case of redeclaration, the parent’s declaration is retained.


Methods: Methods once defined, in parent class can be redefined in child class. But if overridden then, both the methods must have same return type, same number of arguments and same order of arguments. This is being checked in the second pass.


Naming and Scoping


Errors with Attributes : Multiple definition of attributes is invalid in a class. This is being checked in the second pass.


Errors with methods : Multiple definition of methods is invalid in a class. This is being checked in the second pass. Moreover a method cannot contain a variable that has the same name and has been used twice in the parameter list. This is also checked in second pass.


Scoping : Proper care of scoping of variables has been taken. The scopes are being maintained by the ScopeTable.


Type Conformance : 


Type conformance is being taken care in the third pass.


A few Expression Specific Notes:


Objects : The type is to the object’s type itself.


Type Case: Checked if each branch has unique by its type. Also the type assigned is the least common ancestor of all the branch classes.


Dispatch and Static-dispatch : Type has been retrieved as the return type.


If-else : Type is the least common ancestor of if’s type and else’s type.


Loop : Type is set to Object


For the other expressions, it is pretty much clear what the type should be and can be verified easily from the code.