@echo off
echo ========================================
echo CampusMaster - Demarrage avec H2 Database
echo ========================================
echo.
echo Lancement de l'application...
echo.
echo Une fois demarre, accedez a:
echo - API Swagger: http://localhost:8080/api/swagger-ui.html
echo - H2 Console: http://localhost:8080/api/h2-console
echo   JDBC URL: jdbc:h2:mem:campusmaster
echo   Username: sa
echo   Password: (laisser vide)
echo.
echo Appuyez sur Ctrl+C pour arreter
echo ========================================
echo.

mvn spring-boot:run -Dspring-boot.run.profiles=h2
