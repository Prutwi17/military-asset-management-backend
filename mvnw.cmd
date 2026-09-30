@REM Maven Wrapper Script
@echo off
if exist "%USERPROFILE%\.m2\wrapper\apache-maven-3.9.8\bin\mvn.cmd" (
    "%USERPROFILE%\.m2\wrapper\apache-maven-3.9.8\bin\mvn.cmd" %*
) else (
    mvn %*
)
