
@IF EXIST bin\ikvmc.exe GOTO COMPILE

@ECHO Put IKVM into bin\ directory

@GOTO END

:COMPILE

@ECHO 
@ECHO Building first with ANT..
call ant clean
call ant abcl.jar

@ECHO 
@ECHO Work around weird ant failures
call ant abcl.jar

@ECHO 
@ECHO REMOVE COMPILER BUGGED CLASSES
call del build\classes\org\armedbear\lisp\top-level.abcl
call del build\classes\org\armedbear\lisp\error.abcl
call del build\classes\org\armedbear\lisp\assert.abcl
call del build\classes\org\armedbear\lisp\signal.abcl
call del build\classes\org\armedbear\lisp\debug.abcl
rem format_47
call del build\classes\org\armedbear\lisp\format.abcl
call del build\classes\org\armedbear\lisp\print.abcl

@ECHO 
@ECHO MAKING: abclm.exe  (Dll+EXE)
call bin\ikvmc.exe -recurse:build\classes -out:bin\abclm.exe -compressresources  -recurse:build\classes\org\armedbear\lisp\*.lisp -recurse:build\classes\org\armedbear\lisp\*.abcl -main:org.armedbear.lisp.Main lib\asm-all-3.1.jar

@ECHO 
@ECHO MAKING: ABCL.Net.dll
call bin\ikvmc.exe -recurse:build\classes -out:bin\ABCL.Net.dll -compressresources  -recurse:build\classes\org\armedbear\lisp\*.lisp -recurse:build\classes\org\armedbear\lisp\*.* lib\asm-all-3.1.jar

@ECHO 
@ECHO MAKING: abcl.exe
call bin\ikvmc.exe -reference:bin\ABCL.Net.dll -out:bin\abcl.exe -main:org.armedbear.lisp.Main


:END