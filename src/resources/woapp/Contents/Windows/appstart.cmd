:: For more information on the shell builtins used in this script, open a DOS
:: command prompt, and type "help <builtinName>":
:: - call
:: - cd
:: - cmd
:: - del
:: - dir
:: - echo
:: - endlocal
:: - exit
:: - findstr
:: - for
:: - goto
:: - if
:: - pause
:: - prompt
:: - rename
:: - rmdir
:: - set
:: - setlocal
:: - shift
:: - time
:: - title
:: - type
::
:: Some interesting Windows scripting notes that will be of help in
:: understanding this launch script:
:: - If you invoke echo with a variable that evaluates to the empty string,
::   echo will print a status message, not an empty string itself.
::   "help echo" for more info.
:: - Since the equals sign is a metacharacter used in string substitutions,
::   it's very difficult to replace it with something else.  Also, when
::   doing text processing, particularly with invocations of the set builtin,
::   equals signs are interpreted as spaces; this is inexplicable but very
::   much the case.  We hedge against this below by replacing the equals sign
::   character in arguments (e.g. "-Dx=y") with a unique token so that it may
::   be restored later.
:: - Although Windows scripting now allows you to perform blocks of operations
::   within a conditional or loop, the command processor isn't intelligent
::   about handling instances of the block metacharacters (the parenthesis
::   characters, in this case) as values in dereferenced variables.  This
::   causes problems if you want to, say, pass an ASCII property list
::   representation of a string as a value for, say, NSProjectSearchPath.
::   As with the equals sign, we replace these characters with unique tokens
::   so that the characters may be restored later.  This token replacement
::   is much simpler for these characters than it is for the equals sign
::   character, though.
:: - When you echo the character sequence "%%", it collapses to a single "%".
:: - We need to create temporary, helper scripts, and sometimes these scripts
::   echo text to another file.  This is a problem when generating the helper
::   script on the fly, because the act of generating the script itself
::   involves echo text to a file; you run into the difficulty of having
::   stdout redirected twice on the same line, which would confuse the command
::   interpreter.  We rely on an old DOS trick of abusing the prompt builtin to
::   hack together sketchy character sequences in a subshell and harvest the
::   resulting prompt and echo *that* to the script.  All uses of the prompt
::   builtin address this problem alone.  You'll notice that the script line
::   up to the "prompt" keyword is identical in every instance; for more info
::   check "help cmd", "help for" and "help prompt".  This is a well-documented
::   that should be documented in various Windows scripting sites on the WWW.
:: - Windows scripting doesn't treat single quotes specially.  Don't use single
::   quotes to quote arguments (e.g. arguments that contain spaces) to pass to
::   this launch script.
:: - Setting a variable like "set BLAH=" is the same semantic for undefining a
::   variable.  Instead of checks for whether the value of the variable is
::   empty, all we have to do is check whether the variable is defined.
::   Unfortunately, this sort of check doesn't work for automatic variables
::   such as those used by for loops (e.g. "%%a", etc).
:: - If you echo a variable where the last character of the value is a number,
::   and you redirect this echo to a file, the command processor will try to
::   interpret the number as a file descriptor (e.g. "1>" means stdout, "2>"
::   means stderr), regardless of the number, and will effectively eat the
::   number.  This causes big problems if you pass arguments to the script
::   like "-DNSDebugLevel=1".  You need to ensure that a space always follows
::   the echo invocation before the redirection.

:: Turn off echo.
@echo off
::@echo on

:: Use the setlocal builtin to test whether we're running on a supported version
:: of Windows.  If not, exit.
set BADWINVR=no
setlocal ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION
if errorlevel 1 set BADWINVR=yes
if not "%BADWINVR%"=="no" (
   echo This version of WebObjects is not supported on this version of Windows.  Terminating.
   set BADWINVER=
   if not "%NOPAUSE%"=="yes" pause
   exit /B 1
)
endlocal
set BADWINVER=

:: Unfortunately, the scripting environment isn't very intelligent about what
:: changes it considers "local".  If setlocal is active, changes made in for
:: loops have no cumulative effect (the changes are local to that iteration of
:: the for loop).  So we can't use setlocal for its main behavior.  We will
:: also unset any variables that might have been left laying around in a
:: previous launch in this same shell and that aren't explicitly cleared or
:: initialized before their first use.
set ARGLIST=
set CLSSPATH=
set CPATH=
set CURRTIME=
set JAVAARGS=
set JVARGS=
set JXARGS=
set LENGTH=
set LENGTH1=
set LENGTH2=
set NONXARGS=
set PTHCOMPC=
set PTHCOMPD=
set PTHCOMPM=
set PTHCOMPS=
set STRTTIME=
set TEMPARGS=
set THEARGS=

:: Grab the path to the current working directory.
for /F "tokens=*" %%a in ('cd') do set CURRDIR=%%~fa

:: Change directory to the location of the WebObjects application.
:: The first line changes the drive letter.
%~d0
cd "%~p0"

:: Change the titlebar for this window.
title %~n0.woa

:: Argument for the Windows launch script only:
::   -WONoPause YES: Do not execute pauses for user input during startup
set NOPAUSE=no
echo %* | findstr /I /R /C:"WONoPause  *true" /C:"WONoPause  *YES" /C:"WONoPause=true" /C:"WONoPause=YES" > nul 2> nul
if not errorlevel 1 set NOPAUSE=yes

echo Configuring launch environment for %~n0 ...

:: Verify and/or configure the script constants, script variables and
:: environment variables used by this script.
:: These may vary for each system and/or user.

:: Keep track of the failure state.  Try to log as many error conditions
:: at once as possible.
set FAILURE=no

if not defined TEMP echo TEMP environment variable is not defined -- define it and relaunch!
if not defined TEMP set FAILURE=yes

:: Figure out where the launch configuration activity needs to occur.
:: Scratch files will be created below there.
:: We prefer to use the short version of the launch dir path (SDRNM).
set DIRNM=%TEMP%\%~n0-%RANDOM%.LCH
if not exist %DIRNM% mkdir "%DIRNM%"
for /F "tokens=*" %%a in ("%DIRNM%") do set SDRNM=%%~fsa

:: These script variables never vary (used as constants only).

set ARSTR=APPROOT
set CLSNM=CLSSPATH.TXT
set HRSTR=HOMEROOT
set LCKNM=LOCKED
set LENNM=LENGTH.TXT
set LRSTR=LOCALROOT
set MAXLEN=2000
set NRSTR=WOROOT
set NUCNM=NEWCPATH.TXT
set SUBNM=SUBPATHS.TXT

:: These are all of the scratch files used to configure and launch the app.
:: NOTE: There had better be no spaces in any of these directories
::       or filenames, hence the use of SDRNM.
set ARGCMD1FL=%SDRNM%\ARGCMDF1.CMD
set ARGCMD2FL=%SDRNM%\ARGCMDF2.CMD
set ARGCMD3FL=%SDRNM%\ARGCMDF3.CMD
set ARG1FL=%SDRNM%\ARGS1.TXT
set ARG2FL=%SDRNM%\ARGS2.TXT
set ARG3FL=%SDRNM%\ARGS3.TXT
set ARG4FL=%SDRNM%\ARGS4.TXT
set ARG5FL=%SDRNM%\ARGS5.TXT
set ARG6FL=%SDRNM%\ARGS6.TXT
set CLSFL=CONTENTS\WINDOWS\%CLSNM%
set CMCFL=%SDRNM%\CMPCFL.CMD
set CMDFL=%SDRNM%\LAUNCH.CMD
set CMSFL=%SDRNM%\CMPSFL.CMD
set CSBFL=%SDRNM%\SUBDRVS.TXT
set JARGFL=%SDRNM%\JAVAARGS.TXT
set JXARGFL=%SDRNM%\JXARGS.TXT
set KCPFL=%SDRNM%\CHKCLPTH.TXT
set LCKFL=%SDRNM%\%LCKNM%
set LENFL=%SDRNM%\%LENNM%
set NUCFL=%SDRNM%\%NUCNM%
set NUSFL=%SDRNM%\NEWSPTHS.TXT
set OLCFL=%SDRNM%\%CLSNM%
set OLSFL=%SDRNM%\%SUBNM%
set SUBFL=CONTENTS\WINDOWS\%SUBNM%
set TCPFL=%SDRNM%\TMPCLPTH.TXT
set TMCFL=%SDRNM%\TMPCLPTH.CMD
set TMPLCKFL=%SDRNM%\FAKELOCK

:: Argument for the Windows launch script only:
::   clean: Delete the scratch directories.  Then quit.
set DOCLEAN=no
if '%1'=='clean' set DOCLEAN=yes
if not "%DOCLEAN%"=="no" for /F "tokens=*" %%a in ("%TEMP%") do set TMPSNM=%%~fsa
if not "%DOCLEAN%"=="no" echo Deleting scratch directories like %TMPSNM%\*.LCH ...
if not "%DOCLEAN%"=="no" for /D %%a in (%TMPSNM%\*.LCH) do rmdir /Q /S %%~fsa
if not "%DOCLEAN%"=="no" echo Now terminating!
if not "%DOCLEAN%"=="no" if not "%NOPAUSE%"=="yes" pause
if not "%DOCLEAN%"=="no" exit /B 1

:: Argument for the Windows launch script only:
::   clear: Undo any SUBST mappings we have made, or all of them if we find
::          none.  Then quit.
:: NOTE: This is the soonest point we can invoke CLRDRVS.
set DOCLEAR=no
if '%1'=='clear' set DOCLEAR=yes
if not "%DOCLEAR%"=="no" call :CLRDRVS
if not "%DOCLEAR%"=="no" echo Now terminating!
:: NOTE: Don't remove the scratch directory if it's somehow being reused.
if not "%DOCLEAR%"=="no" if not exist %LCKFL% rmdir /Q /S %SDRNM%
if not "%DOCLEAR%"=="no" if not "%NOPAUSE%"=="yes" pause
if not "%DOCLEAR%"=="no" exit /B 1

:: These environment variables vary for each system and/or user.
:: The script variables set as a result will be used to create real
:: paths out of the abstractions in the classpath file.
:: NOTE: Do not reset FAILURE from above yet.  There might be a relevant
::       complaint about the lack of the TEMP environment variable.

if not defined NEXT_ROOT echo NEXT_ROOT is not defined -- define it and relaunch!
if not defined NEXT_ROOT set FAILURE=yes

for /F "tokens=*" %%a in ("%NEXT_ROOT%") do set WOROOT=%%~fa

:: NOTE: We don't fail here.  It could be that the effective user is a daemon
::       user that doesn't really have a home directory.  Also, the HOMEROOT
::       abstraction is almost never used.  We'll choose defaults, if nothing
::       is specified.
if not defined HOMEDRIVE echo HOMEDRIVE is not defined -- setting to default value of "C:"
if not defined HOMEDRIVE set HOMEDRIVE=C:
if not defined HOMEPATH  echo HOMEPATH is not defined -- setting to default value of "\"
if not defined HOMEPATH  set HOMEPATH=\

for /F "tokens=*" %%a in ("%HOMEDRIVE%\%HOMEPATH%") do set HOMEROOT=%%~fa

for /F "tokens=*" %%a in ("%NEXT_ROOT%\Local") do set LOCALROOT=%%~fa

:: Quit if there were problems getting any of the environment variables.
:: NOTE: Don't remove the scratch directory if it's somehow being reused.
if not "%FAILURE%"=="no" if not exist %LCKFL% rmdir /Q /S %SDRNM%
if not "%FAILURE%"=="no" if not "%NOPAUSE%"=="yes" pause
if not "%FAILURE%"=="no" exit /B 1

:: When we run an app, we might be in the top-level directory or in the
:: Contents\Windows directory.  We want to be in the top-level directory, so
:: if we cannot find the CLSSPATH.TXT file in Contents\Windows\CLSSPATH.TXT,
:: then we change directory two levels up.
if not exist %CLSFL% cd ..\..

:DOLOCK
:: Attempt to lock the scratch directory (and thus the ability to launch).
:: This is only relevant if we happen to have two launch instances that end
:: up with the same value of RANDOM (unlikely, but not impossible).
set LCKED=no
type nul > %TMPLCKFL%
rename %TMPLCKFL% %LCKNM% > nul 2> nul
if errorlevel 1 set LCKED=yes
if not "%LCKED%"=="no" call :TRYAGAIN
if not "%LCKED%"=="no" goto DOLOCK

:: Delete any scratch files previously used by launch configuration or attempt.
:: Do not delete CSBFL (yet) or any of the source files from the .woa wrapper.
del /F /Q %ARG1FL% %ARG2FL% %ARG3FL% %ARG4FL% %ARG5FL% %ARG6FL% %ARGCMD1FL% %ARGCMD2FL% %ARGCMD3FL% %CMCFL% %CMDFL% %CMSFL% %JARGFL% %JXARGFL% %KCPFL% %LENFL% %NUCFL% %NUSFL% %OLCFL% %OLSFL% %TCPFL% %TMCFL% > nul 2> nul

:: This script variable varies depending on the location of the application.
:: We assume that we have changed to the top level directory in the .woa
:: wrapper, and the APPROOT is the Contents directory below that.
for /F "tokens=*" %%a in ('cd') do set APPROOT=%%~fa\Contents

:: These are the arguments passed to the script.
set THEARGS=%*

:: If there are no command-line args, skip argument processing.
if not defined THEARGS goto ENDHANDLEARGS

:: Several characters that might be in the argument are script metacharacters.
:: If so, replace them with unique tokens for later reversion.
if defined THEARGS set THEARGS=%THEARGS:(=LeftParenthesisToken%
if defined THEARGS set THEARGS=%THEARGS:)=RightParenthesisToken%

:: Determine which launch args we want to pass directly to the JVM (beginning
:: with -D and containing an = sign) and build a string with just those args.
:: This requires two steps:
:: First, we need to substitute a special delimiter string for the '='
:: character because, while we can use %1, %2, ... to handle spaces in our
:: arguments, those conventions handle the '=' sign in a very strange way.
:: NOTE: Besides the complication of handling spaces in argument values,
::       some old-style arguments start with "-D" (e.g. "-D2WSomeOption YES").
echo %THEARGS% > %ARG1FL%

echo @echo off> %ARGCMD1FL%
::echo @echo on> %ARGCMD1FL%
echo set ARGLIST=>> %ARGCMD1FL%
echo :SUBSTEQ>> %ARGCMD1FL%
echo for /F "tokens=1* delims==" %%%%i in (%ARG1FL%) do (>> %ARGCMD1FL%
echo     if defined ARGLIST (>> %ARGCMD1FL%
echo         set ARGLIST=!ARGLIST!EqualsDelimiterToken>> %ARGCMD1FL%
echo     )>> %ARGCMD1FL%
echo     set ARGLIST=!ARGLIST!%%%%i>> %ARGCMD1FL%
echo     if not '%%%%j'=='' (>> %ARGCMD1FL%
cmd /A /X /C for /L %%v in (1,1,2) do prompt echo %%%%j $g %ARG1FL% $_| findstr /V "$g" | findstr /V /R "^$">> %ARGCMD1FL%
echo         goto :SUBSTEQ>> %ARGCMD1FL%
echo     )>> %ARGCMD1FL%
echo )>> %ARGCMD1FL%
cmd /A /X /C for /L %%v in (1,1,2) do prompt if defined ARGLIST echo !ARGLIST! $g %ARG2FL% $_| findstr /V "$g" | findstr /V /R "^$">> %ARGCMD1FL%

cmd /V:ON /A /X /C %ARGCMD1FL%

set TEMPARGS=
:: NOTE: TEMPARGS now contains all of the command line args with "=" replaced
::       by "EqualsDelimiterToken".
if exist %ARG2FL% for /F "tokens=*" %%a in (%ARG2FL%) do set TEMPARGS=%%a

:: Second, we break the args each on one line for ease of processing,
:: taking advantage of one of the few shell-like behaviors of batch
:: scripting -- arg processing.
:: Then, we separate -X (JVM-specific) args from all others and write them to
:: two different files.
echo @echo off> %ARGCMD2FL%
::echo @echo on> %ARGCMD2FL%
echo :PARSARGS>> %ARGCMD2FL%
echo if not '%%1'=='' (>> %ARGCMD2FL%
cmd /A /C for /L %%v in (1,1,2) do prompt echo %%1 $g$g %ARG3FL% $_| findstr /V "$g" | findstr /V /R "^$">> %ARGCMD2FL%
echo     shift>> %ARGCMD2FL%
echo     goto :PARSARGS>> %ARGCMD2FL%
echo )>> %ARGCMD2FL%
echo set JXARGS=>> %ARGCMD2FL%
echo if exist %ARG3FL% for /F "tokens=*" %%%%a in ('findstr /R "\-X.*" %ARG3FL%') do (>> %ARGCMD2FL%
echo    if not '%%%%a'=='' (>> %ARGCMD2FL%
echo       set JXARGS=!JXARGS!%%%%a>> %ARGCMD2FL%
echo    )>> %ARGCMD2FL%
echo )>> %ARGCMD2FL%
cmd /A /C for /L %%v in (1,1,2) do prompt if defined JXARGS echo !JXARGS! $g$g %JXARGFL% $_| findstr /V "$g" | findstr /V /R "^$">> %ARGCMD2FL%
echo set TEMPARGS=>> %ARGCMD2FL%
echo if exist %ARG3FL% for /F "tokens=*" %%%%a in ('findstr /V /R "\-X.*" %ARG3FL%') do (>> %ARGCMD2FL%
echo    if not '%%%%a'=='' (>> %ARGCMD2FL%
echo       set TEMPARGS=!TEMPARGS!%%%%a>> %ARGCMD2FL%
echo    )>> %ARGCMD2FL%
echo )>> %ARGCMD2FL%
cmd /A /C for /L %%v in (1,1,2) do prompt if defined TEMPARGS echo !TEMPARGS! $g$g %ARG4FL% $_| findstr /V "$g" | findstr /V /R "^$">> %ARGCMD2FL%

cmd /V:ON /A /X /C %ARGCMD2FL% %TEMPARGS%

:: Third, we read back in the separated args.
set TEMPARGS=
:: NOTE: TEMPARGS now contains all of the command line args with "=" replaced
::       by "EqualsDelimiterToken", sans any arguments that start with "-X".
::       "-X" arguments end up in JXARGS.
if exist %ARG4FL% for /F "tokens=*" %%a in (%ARG4FL%) do set TEMPARGS=%%a
set JXARGS=
if exist %JXARGFL% for /F "tokens=*" %%a in (%JXARGFL%) do set JXARGS=%%a
set JXARGS=-Xrs %JXARGS%

:: Fourth, we need to examine each argument to see if it meets our format
:: requirement (-D*=*) and if so then include it in our Java arguments string,
:: and replace the '=' delimiter string at the end.
:: Lastly, read back in all non-"-X" args and repopulate THEARGS.
echo @echo off> %ARGCMD3FL%
::echo @echo on> %ARGCMD3FL%
echo :PARSARGS>> %ARGCMD3FL%
echo if not '%%1'=='' (>> %ARGCMD3FL%
cmd /A /C for /L %%v in (1,1,2) do prompt echo %%1 $g$g %ARG5FL% $_| findstr /V "$g" | findstr /V /R "^$">> %ARGCMD3FL%
echo     shift>> %ARGCMD3FL%
echo     goto :PARSARGS>> %ARGCMD3FL%
echo )>> %ARGCMD3FL%
echo set JVARGS=>> %ARGCMD3FL%
echo for /F "tokens=*" %%%%a in ('findstr /R "\-D.*EqualsDelimiterToken.*" %ARG5FL%') do if not '%%%%a'=='' set JVARGS=!JVARGS!%%%%a>> %ARGCMD3FL% 
cmd /A /X /C for /L %%v in (1,1,2) do prompt if defined JVARGS echo %%JVARGS:EqualsDelimiterToken==%% $g %JARGFL% $_| findstr /V "$g" | findstr /V /R "^$">> %ARGCMD3FL%
echo set NONXARGS=>> %ARGCMD3FL%
echo for /F "tokens=*" %%%%a in (%ARG5FL%) do if not '%%%%a'=='' set NONXARGS=!NONXARGS!%%%%a>> %ARGCMD3FL%
cmd /A /C for /L %%v in (1,1,2) do prompt if defined NONXARGS echo %%NONXARGS:EqualsDelimiterToken==%% $g$g %ARG6FL% $_| findstr /V "$g" | findstr /V /R "^$">> %ARGCMD3FL%

cmd /V:ON /A /X /C %ARGCMD3FL% %TEMPARGS%

:: Reset THEARGS here, because we want to prune all the -X args from the args
:: given to the woapp, not just copy them to the front of the arg list.
if exist %ARG6FL% for /F "tokens=*" %%a in (%ARG6FL%) do set THEARGS=%%a

set JAVAARGS=
if exist %JARGFL% for /F "tokens=*" %%a in (%JARGFL%) do set JAVAARGS=%%a
set JAVAARGS=%JXARGS% %JAVAARGS%

:: Restore script metacharacters in the arguments (see above).
if defined THEARGS set THEARGS=%THEARGS:LeftParenthesisToken=(%
if defined THEARGS set THEARGS=%THEARGS:RightParenthesisToken=)%
if defined JAVAARGS set JAVAARGS=%JAVAARGS:LeftParenthesisToken=(%
if defined JAVAARGS set JAVAARGS=%JAVAARGS:RightParenthesisToken=)%
:ENDHANDLEARGS

:: Even in the case of no command-line args, we need to pass certain arguments
:: used by the WebObjects runtime.
set JAVAARGS=%JAVAARGS% -DWORootDirectory="%WOROOT%" -DWOLocalRootDirectory="%LOCALROOT%" -DWOUserDirectory="%CURRDIR%" 

:: Copy the relevant, unprocessed configuration files from the .woa wrapper
:: to the launch directory.  If anything fails, quit, but try to log as many
:: problems as possible at a time.
set FAILURE=no
set FAILURE1=no
set FAILURE2=no
if not exist %SUBFL% set FAILURE1=yes
if not exist %CLSFL% set FAILURE2=yes
if "%FAILURE1%"=="yes" echo Substition paths file (%SUBNM%) is missing ...
if "%FAILURE2%"=="yes" echo Classpath file (%CLSNM%) is missing ...
if "%FAILURE1%"=="yes" set FAILURE=yes
if "%FAILURE2%"=="yes" set FAILURE=yes
if "%FAILURE%"=="yes" echo Unable to configure.
:: NOTE: Don't remove the scratch directory if it's somehow being reused.
if "%FAILURE%"=="yes" if not exist %LCKFL% rmdir /Q /S %SDRNM%
if "%FAILURE%"=="yes" if not "%NOPAUSE%"=="yes" pause
if "%FAILURE%"=="yes" exit /B 1
copy %SUBFL% %OLSFL% > nul 2> nul
copy %CLSFL% %OLCFL% > nul 2> nul

:: Read in the configuration information from the comment "header" at the
:: beginning of the classpath file.  This information will be used when
:: creating the scratch script that will actually invoke the JVM below.
for /F "tokens=1,2,3*" %%a in (%OLCFL%) do if /I "%%b"=="ApplicationClass" set APPCLS=%%d
for /F "tokens=1,2,3*" %%a in (%OLCFL%) do if /I "%%b"=="JDB" set JDB=%%d
for /F "tokens=1,2,3*" %%a in (%OLCFL%) do if /I "%%b"=="JDBOptions" set JDBOPTS=%%d
for /F "tokens=1,2,3*" %%a in (%OLCFL%) do if /I "%%b"=="JVM" set JVM=%%d
for /F "tokens=1,2,3*" %%a in (%OLCFL%) do if /I "%%b"=="JVMOptions" set JVMOPTS=%%d

:: Set certain configuration information to defaults, if not specified in the
:: comment "header" at the beginning of the classpath file.
if not defined APPCLS set APPCLS=Application
if not defined JDB    set JDB=jdb
if not defined JVM    set JVM=java

:: The classpath argument used during the JVM invocation depends on which
:: JVM is used.  We only know about two different ones: one for the MS JVM,
:: and one for the Sun JVM.
set CPATHARG=-classpath
echo %JVM% | findstr /I /L /C:jview > nul 2> nul
if not errorlevel 1 set CPATHARG=/cp

:: Create the new classpath file with non-abstract paths.
:: Invoke a subroutine to take each line from the original file,
:: process any given path abstraction and write it to the new file.
type nul > %NUCFL%
echo @echo off > %CMCFL%
::echo @echo on> %CMCFL%
for /F "eol=# tokens=*" %%a in (%OLCFL%) do call :MKNUCFL "%%a" %NRSTR% "%WOROOT%" %ARSTR% "%APPROOT%" %HRSTR% "%HOMEROOT%" %LRSTR% "%LOCALROOT%"
cmd /X /C %CMCFL%

:: Determine whether the debugger or the JVM should be invoked.
set DEBUG=no
echo %THEARGS% | findstr /C:NSPBDebug /C:NSJavaDebug > nul 2> nul
if not errorlevel 1 set DEBUG=yes

:: Determine whether the command line will be long enough to require the
:: hassle of shortening it with path-to-drive-letter substitution.
set DOSUBST=no
for /F "tokens=1,2,3*" %%a in ('dir /-C /P %NUCFL%') do if /I "%%d"=="%NUCNM%" set LENGTH1=%%c
if defined CLASSPATH echo %CLASSPATH% > %LENFL%
if not "%DEBUG%"=="yes" echo %JVM% >> %LENFL%
if "%DEBUG%"=="yes" echo %JDB% >> %LENFL%
if defined JVMOPTS echo %JVMOPTS% >> %LENFL%
if "%DEBUG%"=="yes" if defined JDBOPTS echo %JDBOPTS% >> %LENFL%
echo %CPATHARG% >> %LENFL%
echo %APPCLS% >> %LENFL%
if defined THEARGS  echo %THEARGS% >> %LENFL%
if defined JAVAARGS echo %JAVAARGS% >> %LENFL%
for /F "tokens=1,2,3*" %%a in ('dir /-C /P %LENFL%') do if /I "%%d"=="%LENNM%" set LENGTH2=%%c
set /A LENGTH=LENGTH1+LENGTH2
if %LENGTH% GEQ %MAXLEN% set DOSUBST=yes

:: If there's no need to perform such substitution, don't bother processing
:: the path-to-drive-letter substitution mapping file.
if not "%DOSUBST%"=="yes" goto RDCLSPT

:: Create the new path-to-drive-letter substitution mapping file with
:: non-abstract paths.
:: Invoke a subroutine to take each line from the original file,
:: process any given path abstraction and write it to the new file.
type nul > %NUSFL%
echo @echo off > %CMSFL%
::echo @echo on> %CMSFL%
for /F "eol=# tokens=*" %%a in (%OLSFL%) do call :MKNUSFL "%%a" %NRSTR% "%WOROOT%" %ARSTR% "%APPROOT%" %HRSTR% "%HOMEROOT%" %LRSTR% "%LOCALROOT%"
cmd /X /C %CMSFL%

:: Map the paths to substitute to drive letters.  Invokes a subroutine which
:: checks whether the path has already been mapped to a drive letter (we'll
:: reuse it, in that case, to minimize the number of drive letters used) before
:: mapping a drive letter to the path.
for /F "tokens=*" %%a in (%NUSFL%) do call :CHKMAP "%%a"

:: Create a file that captures which drive letter was actually mapped to each
:: path to substitute.  This file is used to pare down the classpath written to
:: the temporary launch script.
:: At this point, go ahead and nuke CSBFL.  We will not need it for calling
:: CLRDRVS, anymore.
type nul > %CSBFL%
for /F "tokens=*" %%a in (%NUSFL%) do call :MKCSBFL "%%a"

:RDCLSPT
:: Assemble the classpath in a script variable.  We use a temporary script to
:: do this due to limitations in the scripting environment.  Grab each line
:: from the new, processed classpath file, as well as the contents of the
:: CLASSPATH environment variable.
set FOUND=no
type nul > %TCPFL%
echo @echo off > %TMCFL%
::echo @echo on> %TMCFL%
for /F "tokens=*" %%a in (%NUCFL%) do call :MKCLSPT "%%a"
set CPHASSTUFF=no
echo %CLASSPATH%:> %KCPFL%
for /F "tokens=*" %%a in (%KCPFL%) do if not "%%a"==":" set CPHASSTUFF=yes
if "%CPHASSTUFF%"=="yes" call :MKCLSPT "%CLASSPATH%"
if not "%DOSUBST%"=="yes" goto FINCPATH
for /F "tokens=1*" %%a in (%CSBFL%) do echo set CLSSPATH=%%CLSSPATH:%%~fb=%%~da\%%>> %TMCFL%
:FINCPATH
echo set CLSSPATH=%%CLSSPATH: ;=;%%>> %TMCFL%
echo set CLSSPATH=%%CLSSPATH:\\=\%%>> %TMCFL%
cmd /A /X /C for /L %%v in (1,1,2) do prompt echo %%CLSSPATH%% $g$g %TCPFL%$_| findstr /V "$g" >> %TMCFL%
cmd /X /C %TMCFL%
for /F "tokens=*" %%a in (%TCPFL%) do set CPATH=%%a

:: If CPATH isn't defined, it probably means that the classpath was still too
:: long to set CLSSPATH in the scratch script, TMCFL.  Notify and exit.
if not defined CPATH (
   echo The classpath is still too long for Windows command scripting to process.
   echo Please use the SUBPATHS.TXT file to specify a more appropriate set of paths to
   echo substitute.  Use the longest paths, particularly if they are common, and
   echo eliminate any paths in SUBPATHS.TXT that you aren't actually using in your
   echo classpath file.  Further, eliminate any paths from the classpath file that
   echo that you don't really need, e.g. "WOROOT/Library/Java".
   echo Terminating!
   :: NOTE: Don't remove the scratch directory if it's somehow being reused.
   if not exist %LCKFL% rmdir /Q /S %SDRNM%
   if not "%NOPAUSE%"=="yes" pause
   exit /B 1
)

:: Determine whether we've shortened the command line sufficiently.  If not,
:: notify and exit.
set TOOLONG=no
if not "%DEBUG%"=="yes" echo %JVM% > %LENFL%
if "%DEBUG%"=="yes" echo %JDB% > %LENFL%
if defined JVMOPTS echo %JVMOPTS% >> %LENFL%
if "%DEBUG%"=="yes" if defined JDBOPTS echo %JDBOPTS% >> %LENFL%
if defined JAVAARGS echo %JAVAARGS% >> %LENFL%
echo %CPATHARG% >> %LENFL%
echo %CPATH% >> %LENFL%
echo %APPCLS% >> %LENFL%
if defined THEARGS echo %THEARGS% >> %LENFL%
for /F "tokens=1,2,3*" %%a in ('dir /-C /P %LENFL%') do if /I "%%d"=="%LENNM%" set LENGTH=%%c
if %LENGTH% GEQ %MAXLEN% set TOOLONG=yes
if not "%TOOLONG%"=="no" (
   echo The command line is still too long for Windows command scripting to process.
   echo Please use the SUBPATHS.TXT file to specify a more appropriate set of paths to
   echo substitute.  Use the longest paths, particularly if they are common, and
   echo eliminate any paths in SUBPATHS.TXT that you aren't actually using in your
   echo classpath file.  Further, eliminate any paths from the classpath file that
   echo that you don't really need, e.g. "WOROOT/Library/Java".
   echo If that doesn't suffice, pare down command-line arguments as much as you can.
   echo Terminating!
   :: NOTE: Don't remove the scratch directory if it's somehow being reused.
   if not exist %LCKFL% rmdir /Q /S %SDRNM%
   if not "%NOPAUSE%"=="yes" pause
   exit /B 1
)

:: Create the temporary launch script that will actually invoke the JVM using
:: the processed classpath.
echo @echo off > %CMDFL%
::echo @echo on> %CMDFL%
set JAVAEXE=%JVM%
if "%DEBUG%"=="yes" set JAVAEXE=%JDB%
if "%DEBUG%"=="yes" set JVMOPTS=%JVMOPTS% %JDBOPTS%
echo echo %JAVAEXE% %JVMOPTS% %JAVAARGS% %CPATHARG% "%CPATH%" %APPCLS% %THEARGS% >> %CMDFL%
echo del /F /Q %LCKFL%>>%CMDFL%
echo %JAVAEXE% %JVMOPTS% %JAVAARGS% %CPATHARG% "%CPATH%" %APPCLS% %THEARGS% >> %CMDFL%
echo set RETVAL=%%ERRORLEVEL%%>> %CMDFL%
echo exit /B %%RETVAL%%>> %CMDFL%

:: Launch the application using our new temporary launch script, capture the
:: exit code returned (important in the event of an error), get rid of the
:: scratch directory and exit this script using the exit code.
echo Launching %~n0.
call %CMDFL%
set RETVAL=%ERRORLEVEL%
:: NOTE: Don't remove the scratch directory if it's somehow being reused.
if not exist %LCKFL% rmdir /Q /S %SDRNM%
if not "%NOPAUSE%"=="yes" pause
exit /B %RETVAL%

:: HELPER SUBROUTINES
:: These subroutines all return to the calling location after they complete.

:CHKMAP
:: This subroutine checks whether the path in question has already been
:: been mapped.  If not, it calls the DOMAP subroutine.
set PTHCOMPM=%~f1
subst | findstr /L /E "%PTHCOMPM%" > nul
if errorlevel 1 call :DOMAP "%PTHCOMPM%"
goto :EOF

:CLRDRVS
:: This subroutine clears all of the SUBST drive letter mappings.
echo Clearing drives mapped for %~n0 ...
if exist %CSBFL% for /F "tokens=1,2*" %%a in (%CSBFL%) do subst /D %%a 2> nul
if not exist %CSBFL% call :UNMAPALL
echo Done clearing drives.
goto :EOF

:DOMAP
:: This subroutine finds the first unused drive letter and maps the
:: path parameter to that drive letter using the SUBST shell built-in.
set PTHCOMPD=%~f1
subst D: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst E: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst F: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst G: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst H: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst I: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst J: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst K: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst L: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst M: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst N: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst O: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst P: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst Q: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst R: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst S: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst T: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst U: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst V: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst W: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst X: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst Y: "%PTHCOMPD%" > nul 2> nul
if errorlevel 1 subst Z: "%PTHCOMPD%" > nul 2> nul
goto :EOF

:MKCLSPT
:: This subroutine writes lines to a temporary script that assembles the
:: classpath from the components in the new, processed classpath file.
:: The subroutine is called for each line, so it processes the parameter passed
:: to it and then returns.
if "%FOUND%"=="yes" if exist %1 echo set CLSSPATH=%%CLSSPATH%%;%~f1 >> %TMCFL%
if not "%FOUND%"=="yes" if exist %1 echo set CLSSPATH=%~f1 >> %TMCFL%
set FOUND=yes
goto :EOF

:MKCSBFL
:: This subroutine creates a file that shows which drive letters are mapped to
:: each path to substitute.  This file is used to pare down the classpath
:: that's written to the temporary launch script.
set FOUND=no
for /F "tokens=1,3*" %%a in ('subst') do call :WRCSBFL %%a "%%b %%c" "%~f1"
goto :EOF

:MKNUCFL
:: This subroutine takes each line from the original classpath file,
:: processes any given path abstraction and invokes another subroutine
:: to write the processed line to the new file.
:: NOTE: For some reason, the for loop that calls this subroutine wants to give
::       it an empty line on the last iteration.  Check for this condition and
::       return without doing anything if that has happened.
if %1=="" goto :EOF
echo set PTHCOMPC=%1 >> %CMCFL%
echo set PTHCOMPC=%%PTHCOMPC:%4=%~f5%% >> %CMCFL%
echo set PTHCOMPC=%%PTHCOMPC:%6=%~f7%% >> %CMCFL%
echo set PTHCOMPC=%%PTHCOMPC:%2=%~f3%% >> %CMCFL%
echo set PTHCOMPC=%%PTHCOMPC:%8=%~f9%% >> %CMCFL%
echo for /F "tokens=*" %%%%a in ("%%PTHCOMPC%%") do set PTHCOMPC=%%%%~fa >> %CMCFL%
cmd /A /X /C for /L %%v in (1,1,2) do prompt if exist "%%PTHCOMPC%%" for /F "tokens=*" %%%%a in ("%%PTHCOMPC%%") do echo %%%%~fa $g$g %NUCFL%$_ | findstr /V "$g" >> %CMCFL%
goto :EOF

:MKNUSFL
:: This subroutine takes each line from the original path-to-drive-letter
:: substitution mapping file, processes any given path abstraction and invokes
:: another subroutine to write the processed line to the new file.
:: NOTE: For some reason, the for loop that calls this subroutine wants to give
::       it an empty line on the last iteration.  Check for this condition and
::       return without doing anything if that has happened.
if %1=="" goto :EOF
echo set PTHCOMPS=%1 >> %CMSFL%
echo set PTHCOMPS=%%PTHCOMPS:%4=%~f5%% >> %CMSFL%
echo set PTHCOMPS=%%PTHCOMPS:%6=%~f7%% >> %CMSFL%
echo set PTHCOMPS=%%PTHCOMPS:%2=%~f3%% >> %CMSFL%
echo set PTHCOMPS=%%PTHCOMPS:%8=%~f9%% >> %CMSFL%
echo for /F "tokens=*" %%%%a in ("%%PTHCOMPS%%") do set PTHCOMPS=%%%%~fa >> %CMSFL%
cmd /A /X /C for /L %%v in (1,1,2) do prompt if exist "%%PTHCOMPS%%" for /F "tokens=*" %%%%a in ("%%PTHCOMPS%%") do echo %%%%~fa $g$g %NUSFL%$_ | findstr /V "$g" >> %CMSFL%
goto :EOF

:TRYAGAIN
:: This subroutine is called if the app is launched before a previous
:: instance can finish the startup process.
echo Windows launch configuration in progress for another instance of %~n0...
echo Waiting for one minute.  Please do not interrupt.
call :WAIT1MIN
if not exist %LCKFL% goto :EOF
echo Finished waiting, and trying to configure again.
echo If unable to launch successfully for long periods, another launch attempt
echo may have stalled or failed that is using this scratch directory:
echo    %SDRNM%
echo To clear the scratch directory, type the following at the DOS command prompt:
echo    %~nx0 clean
echo This will forcibly remove all scratch directories for this WebObjects
echo  application and should allow you to launch successfully again.
echo You can always stop a WebObjects application started with this launch
echo script at any time by typing CTRL+C in the window.
if not "%NOPAUSE%"=="yes" pause
goto :EOF

:UNMAPALL
:: This subroutine clears every drive letter that has been used to map a path.
echo Unable to clear only drives mapped for %~n0.
echo Clearing all drives mapped using SUBST ...
subst /D D: > nul 2> nul
subst /D E: > nul 2> nul
subst /D F: > nul 2> nul
subst /D G: > nul 2> nul
subst /D H: > nul 2> nul
subst /D I: > nul 2> nul
subst /D J: > nul 2> nul
subst /D K: > nul 2> nul
subst /D L: > nul 2> nul
subst /D M: > nul 2> nul
subst /D N: > nul 2> nul
subst /D O: > nul 2> nul
subst /D P: > nul 2> nul
subst /D Q: > nul 2> nul
subst /D R: > nul 2> nul
subst /D S: > nul 2> nul
subst /D T: > nul 2> nul
subst /D U: > nul 2> nul
subst /D V: > nul 2> nul
subst /D W: > nul 2> nul
subst /D X: > nul 2> nul
subst /D Y: > nul 2> nul
subst /D Z: > nul 2> nul
goto :EOF

:WAIT1MIN
:: This subroutine waits for one minute.  It establishes a starting second
:: (STRTTIME), waits one second so that the current second is different, then
:: waits for the current second (CURRTIME) to become the same again.  That
:: means one minute has passed.
for /F "tokens=1,2,3* delims=:." %%a in ('echo %TIME%') do set STRTTIME=%%c
:SEC1LOOP
for /F "tokens=1,2,3* delims=:." %%a in ('echo %TIME%') do set CURRTIME=%%c
if "%STRTTIME%"=="%CURRTIME%" goto SEC1LOOP
:WAITLOOP
for /F "tokens=1,2,3* delims=:." %%a in ('echo %TIME%') do set CURRTIME=%%c
if not "%STRTTIME%"=="%CURRTIME%" goto WAITLOOP
goto :EOF

:WRCSBFL
:: This subroutine writes each line in the file that shows which drive letters
:: map to each path to substitute.
if "%FOUND%"=="yes" goto :EOF
if "%~f3"=="%~f2" echo %~d1 %~f2 >> %CSBFL%
if "%~f3"=="%~f2" set FOUND=yes
goto :EOF
