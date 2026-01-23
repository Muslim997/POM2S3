#!/bin/bash

echo "========================================"
echo "CampusMaster - Démarrage avec H2 Database"
echo "========================================"
echo ""
echo "Lancement de l'application..."
echo ""
echo "Une fois démarré, accédez à:"
echo "- API Swagger: http://localhost:8080/api/swagger-ui.html"
echo "- H2 Console: http://localhost:8080/api/h2-console"
echo "  JDBC URL: jdbc:h2:mem:campusmaster"
echo "  Username: sa"
echo "  Password: (laisser vide)"
echo ""
echo "Appuyez sur Ctrl+C pour arrêter"
echo "========================================"
echo ""

mvn spring-boot:run -Dspring-boot.run.profiles=h2
