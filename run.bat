@echo off
title CineMesh - Distributed Movie Ticketing SaaS (Spring Boot)
echo =========================================================================
echo    CineMesh: Distributed Multi-Tenant Movie Ticketing SaaS (Spring Boot)
echo    SOAP Web Services / WSDL / Concurrency Control / Microsoft Azure
echo =========================================================================
echo.
echo [1/2] Checking Maven & Java Environment...
call mvn.cmd -version
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven not found. Please ensure Java 17+ and Maven are configured.
    pause
    exit /b 1
)

echo.
echo [2/2] Launching CineMesh Spring Boot Platform on http://localhost:8080 ...
call mvn.cmd spring-boot:run
pause
