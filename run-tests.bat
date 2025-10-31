@echo off
echo ========================================
echo   VIOLATION FORM TEST LAUNCHER
echo ========================================
echo.

echo Choose your testing approach:
echo.
echo 1. Open Interactive Test Page (HTML)
echo 2. Run Automated API Tests (PowerShell)
echo 3. Verify MongoDB Database Separation
echo 4. Start Frontend Development Server
echo 5. View Test Guide Documentation
echo 6. Run Quick Backend Health Check
echo 0. Exit
echo.

set /p choice="Enter your choice (0-6): "

if "%choice%"=="1" (
    echo.
    echo Opening test-violation-form.html in default browser...
    start "" "test-violation-form.html"
    echo.
    echo ✅ Test page opened!
    echo.
    echo INSTRUCTIONS:
    echo 1. Click "Login as Admin" to get JWT token
    echo 2. Click "Fill Test Data" to populate form
    echo 3. Click "Register Violation" to submit
    echo 4. Check for success message
    echo.
    pause
    goto :menu
)

if "%choice%"=="2" (
    echo.
    echo Running automated API tests...
    echo.
    echo NOTE: You need a JWT token for authenticated tests.
    echo Getting token first...
    echo.
    powershell -ExecutionPolicy Bypass -File "test-backend.ps1"
    echo.
    set /p token="Enter the JWT token from above (or press Enter to skip): "
    if not "%token%"=="" (
        echo.
        echo Running full test suite with authentication...
        powershell -ExecutionPolicy Bypass -File "test-violation-workflow.ps1" -Token "%token%"
    ) else (
        echo.
        echo Running tests without authentication (some may fail)...
        powershell -ExecutionPolicy Bypass -File "test-violation-workflow.ps1"
    )
    echo.
    pause
    goto :menu
)

if "%choice%"=="3" (
    echo.
    echo Verifying MongoDB database separation...
    echo.
    powershell -ExecutionPolicy Bypass -File "verify-mongodb-separation.ps1"
    echo.
    pause
    goto :menu
)

if "%choice%"=="4" (
    echo.
    echo Starting frontend development server...
    echo.
    echo This will run: npm run dev in frontend folder
    echo Press Ctrl+C to stop the server when done testing.
    echo.
    cd frontend
    npm run dev
    cd ..
    pause
    goto :menu
)

if "%choice%"=="5" (
    echo.
    echo Opening test documentation...
    echo.
    if exist "HOW_TO_TEST.md" (
        start "" "HOW_TO_TEST.md"
        echo ✅ Opened HOW_TO_TEST.md
    )
    if exist "TEST_GUIDE.md" (
        start "" "TEST_GUIDE.md"
        echo ✅ Opened TEST_GUIDE.md
    )
    echo.
    pause
    goto :menu
)

if "%choice%"=="6" (
    echo.
    echo Running quick backend health check...
    echo.
    powershell -ExecutionPolicy Bypass -File "test-backend.ps1"
    echo.
    pause
    goto :menu
)

if "%choice%"=="0" (
    echo.
    echo Goodbye!
    timeout /t 2 >nul
    exit
)

echo.
echo Invalid choice. Please try again.
timeout /t 2 >nul

:menu
cls
goto :start
