@echo off
set "JAVA_HOME=C:\Users\yelag\.jdks\ms-21.0.12.1"
call gradlew.bat compileJava processResources --console=plain
if errorlevel 1 pause
call gradlew.bat runClient --console=plain
pause
