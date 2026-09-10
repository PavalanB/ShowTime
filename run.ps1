# CineMesh: 1-Click Launch Script (PowerShell)
Write-Host "=========================================================================" -ForegroundColor Cyan
Write-Host "   CineMesh: Distributed Multi-Tenant Movie Ticketing SaaS (Spring Boot)   " -ForegroundColor Yellow
Write-Host "   SOAP Web Services | WSDL | Concurrency Control | Microsoft Azure        " -ForegroundColor Cyan
Write-Host "=========================================================================" -ForegroundColor Cyan
Write-Host ""

$mvnCmd = Join-Path $PSScriptRoot "mvn.cmd"

Write-Host "[1/2] Verifying Maven..." -ForegroundColor Green
& $mvnCmd -version

Write-Host "`n[2/2] Starting CineMesh Application on http://localhost:8080 ..." -ForegroundColor Green
& $mvnCmd spring-boot:run
