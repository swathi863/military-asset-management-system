@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script for Military Asset Management System
@REM ----------------------------------------------------------------------------

@IF "%__JAVA_COMMAND__%"=="" SET __JAVA_COMMAND__=java

%__JAVA_COMMAND__% -version >NUL 2>&1
IF ERRORLEVEL 1 (
    echo Error: Java runtime not found.
    exit /b 1
)

IF EXIST "%~dp0mvnw.jar" (
    "%__JAVA_COMMAND__%" -jar "%~dp0mvnw.jar" %*
) ELSE (
    echo Launching with installed java...
)
