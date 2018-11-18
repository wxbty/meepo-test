@REM ----------------------------------------------------------------------------
@REM Copyright 2001-2004 The Apache Software Foundation.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM ----------------------------------------------------------------------------
@REM

@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:repoSetup


if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%BASEDIR%\lib

set CLASSPATH="%BASEDIR%"\conf;"%REPO%"\jta-supports-dubbo-2.0.jar;"%REPO%"\jta-supports-2.0.jar;"%REPO%"\jta-core-2.0.jar;"%REPO%"\javax.jms-api-2.0.jar;"%REPO%"\javax.resource-api-1.7.jar;"%REPO%"\commons-lang3-3.4.jar;"%REPO%"\hessian-4.0.38.jar;"%REPO%"\kryo-3.0.3.jar;"%REPO%"\reflectasm-1.10.1.jar;"%REPO%"\minlog-1.3.0.jar;"%REPO%"\fst-2.56.jar;"%REPO%"\jackson-core-2.8.8.jar;"%REPO%"\javax.annotation-api-1.2.jar;"%REPO%"\javax.inject-1.jar;"%REPO%"\jsqlparser-1.2.jar;"%REPO%"\fastjson-1.2.28.jar;"%REPO%"\cglib-2.2.2.jar;"%REPO%"\asm-3.3.1.jar;"%REPO%"\aspectjweaver-1.6.8.jar;"%REPO%"\druid-1.0.17.jar;"%REPO%"\logback-classic-1.2.3.jar;"%REPO%"\logback-core-1.2.3.jar;"%REPO%"\slf4j-api-1.7.25.jar;"%REPO%"\commons-dbcp2-2.1.1.jar;"%REPO%"\commons-pool2-2.4.2.jar;"%REPO%"\curator-recipes-2.11.0.jar;"%REPO%"\curator-framework-2.11.0.jar;"%REPO%"\curator-client-2.11.0.jar;"%REPO%"\mybatis-3.2.3.jar;"%REPO%"\mybatis-spring-1.2.1.jar;"%REPO%"\spring-context-4.2.4.RELEASE.jar;"%REPO%"\spring-aop-4.2.4.RELEASE.jar;"%REPO%"\aopalliance-1.0.jar;"%REPO%"\spring-beans-4.2.4.RELEASE.jar;"%REPO%"\spring-core-4.2.4.RELEASE.jar;"%REPO%"\spring-expression-4.2.4.RELEASE.jar;"%REPO%"\spring-jdbc-4.2.4.RELEASE.jar;"%REPO%"\spring-tx-4.2.4.RELEASE.jar;"%REPO%"\guava-18.0.jar;"%REPO%"\commons-lang-2.6.jar;"%REPO%"\dubbo-2.6.2.jar;"%REPO%"\javassist-3.20.0-GA.jar;"%REPO%"\netty-3.2.5.Final.jar;"%REPO%"\netty-all-4.1.0.Final.jar;"%REPO%"\mysql-connector-java-5.1.38.jar;"%REPO%"\httpclient-4.5.2.jar;"%REPO%"\httpcore-4.4.4.jar;"%REPO%"\commons-logging-1.2.jar;"%REPO%"\commons-codec-1.9.jar;"%REPO%"\javax.transaction-api-1.2.jar;"%REPO%"\zkclient-0.10.jar;"%REPO%"\zookeeper-3.4.8.jar;"%REPO%"\log4j-1.2.16.jar;"%REPO%"\jline-0.9.94.jar;"%REPO%"\netty-3.7.0.Final.jar;"%REPO%"\junit-4.12.jar;"%REPO%"\sample-txc-dubbo-0.2.1-SNAPSHOT.jar
set EXTRA_JVM_ARGUMENTS=
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %JAVA_OPTS% %EXTRA_JVM_ARGUMENTS% -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dapp.name="client" -Dapp.repo="%REPO%" -Dbasedir="%BASEDIR%" com.tuya.txc.dubbo.Client %CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal

:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
