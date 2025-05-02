@echo off
FOR /F "usebackq tokens=1,* delims==" %%i IN (".env") DO (
    SET "%%i=%%j"
)

call mvnw.cmd clean compile deploy