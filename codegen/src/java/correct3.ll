; I am a comment in LLVM-IR. Feel free to remove me.
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-unknown-linux-gnu"
@Abortdivby0 = private unnamed_addr constant [22 x i8] c"Error: Division by 0\0A\00", align 1
@Abortdispvoid = private unnamed_addr constant [25 x i8] c"Error: Dispatch on void\0A\00", align 1

declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
declare i32 @strcmp(i8*, i8*)
declare i8* @strcat(i8*, i8*)
declare i8* @strcpy(i8*, i8*)
declare i8* @strncpy(i8*, i8*, i32)
declare i64 @strlen(i8*)
declare i8* @malloc(i64)
declare void @exit(i32)
@strformatstr = private unnamed_addr constant [3 x i8] c"%s\00", align 1
@intformatstr = private unnamed_addr constant [3 x i8] c"%d\00", align 1

%class.Object = type {  }
%class.IO = type { %class.Object }
%class.Int = type { %class.Object }
%class.String = type { %class.Object }
%class.Bool = type { %class.Object }
%class.Main = type { %class.IO, i32 }
define %class.Object* @_ZN6Object5abort( %class.Object* %this ) noreturn {
entry:
	call void @exit( i32 1 )
	ret %class.Object* null
}

define [1024 x i8]* @_ZN6Object9type_name( %class.Object* %this ) {
entry:
	%0 = getelementptr inbounds %class.Object, %class.Object* %this, i32 0, i32 0
	%1 = load i32, i32* %0
	%2 = getelementptr inbounds [8 x [1024 x i8]], [8 x [1024 x i8]]* @classnames, i32 0, i32 %1
	%retval = call [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %2 )
	ret [1024 x i8]* %retval
}

define %class.Object* @_ZN6Object4copy( %class.Object* %this ) {
entry:
	%call = call i8* @malloc( i64 32 )
	%retval = bitcast i8* %call to %class.Object*
	%0 = getelementptr inbounds %class.Object, %class.Object* %retval, i32 0, i32 0
	store i32 0, i32* %0
	%1 = getelementptr inbounds %class.Object, %class.Object* %retval, i32 0, i32 1
	store i8* bitcast ( [3 x i8*]* @VTObject to i8*), i8** %1
	%2 = getelementptr inbounds %class.Object, %class.Object* %retval, i32 0, i32 2
	%3 = getelementptr inbounds %class.Object, %class.Object* %this, i32 0, i32 2
	call void @_ZN6Object4copyTo( %classbaseObject* %2, %classbaseObject* %3 )
	ret %class.Object* %retval
}

define %class.IO* @_ZN2IO10out_string( %class.IO* %this, [1024 x i8]* %str ) {
entry:
	%0 = call i32 (i8*, ...) @printf( i8* bitcast ( [3 x i8]* @strformatstr to i8* ), [1024 x i8]* %str )
	ret %class.IO* %this
}

define %class.IO* @_ZN2IO7out_int( %class.IO* %this, i32 %int ) {
entry:
	%0 = call i32 (i8*, ...) @printf( i8* bitcast ( [3 x i8]* @intformatstr to i8* ), i32 %int )
	ret %class.IO* %this
}

define [1024 x i8]* @_ZN2IO9in_string( %class.IO* %this ) {
entry:
	%0 = call i8* @malloc( i64 1024 )
	%retval = bitcast i8* %0 to [1024 x i8]*
	%1 = call i32 (i8*, ...) @scanf( i8* bitcast ( [3 x i8]* @strformatstr to i8* ), [1024 x i8]* %retval )
	ret [1024 x i8]* %retval
}

define i32 @_ZN2IO9in_int( %class.IO* %this ) {
entry:
	%0 = call i8* @malloc( i64 4 )
	%1 = bitcast i8* %0 to i32*
	%2 = call i32 (i8*, ...) @scanf( i8* bitcast ( [3 x i8]* @intformatstr to i8* ), i32* %1 )
	%retval = load i32, i32* %1
	ret i32 %retval
}

define i32 @_ZN6String6length( [1024 x i8]* %this ) {
	entry:
	%0 = bitcast [1024 x i8]* %this to i8*
	%1 = call i64 @strlen( i8* %0 )
	%retval = trunc i64 %1 to i32
	ret i32 %retval
}

define [1024 x i8]* @_ZN6String6concat( [1024 x i8]* %this, [1024 x i8]* %that ) {
entry:
	%retval = call [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %this )
	%0 = bitcast [1024 x i8]* %retval to i8*
	%1 = bitcast [1024 x i8]* %that to i8*
	%2 = call i8* @strcat( i8* %0, i8* %1 )
	ret [1024 x i8]* %retval
}

define [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %this ) {
entry:
	%0 = call i8* @malloc( i64 1024 )
	%retval = bitcast i8* %0 to [1024 x i8]*
	%1 = bitcast [1024 x i8]* %this to i8*
	%2 = bitcast [1024 x i8]* %retval to i8*
	%3 = call i8* @strcpy( i8* %2, i8* %1)
	ret [1024 x i8]* %retval
}

define [1024 x i8]* @_ZN6String6substr( [1024 x i8]* %this, i32 %start, i32 %len ) {
entry:
	%0 = getelementptr inbounds [1024 x i8], [1024 x i8]* %this, i32 0, i32 %start
	%1 = call i8* @malloc( i64 1024 )
	%retval = bitcast i8* %1 to [1024 x i8]*
	%2 = bitcast [1024 x i8]* %retval to i8*
	%3 = call i8* @strncpy( i8* %2, i8* %0, i32 %len )
	%4 = getelementptr inbounds [1024 x i8], [1024 x i8]* %retval, i32 0, i32 %len
	store i8 0, i8* %4
	ret [1024 x i8]* %retval
}

define i32 @_ZN4Main10print_bool( %class.Main* %self, i32 %a ){
}
define i32 @_ZN4Main11boolean_xor( %class.Main* %self, i32 %a, i32 %b ){
}
define i32 @_ZN4Main11boolean_and( %class.Main* %self, i32 %a, i32 %b ){
}
define %class.Object @_ZN4Main4main( %class.Main* %self ){
}
define i32 @_ZN4Main10boolean_or( %class.Main* %self, i32 %a, i32 %b ){
}
