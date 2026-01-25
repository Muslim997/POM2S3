# Étape 1 : Construction
FROM eclipse-temurin:21-jdk AS build-stage

# Répertoire de travail pour la construction
WORKDIR /app

# Copier le fichier POM et d'autres configurations nécessaires (meilleure gestion du cache Docker)
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn

# Télécharger les dépendances (améliore la mise en cache lors des reconstructions)
RUN chmod +x ./mvnw && ./mvnw dependency:resolve

# Copier le code source
COPY src ./src

# Compiler l'application et générer un fichier JAR (sans exécuter les tests)
RUN ./mvnw clean package -DskipTests

# Étape 2 : Production
FROM eclipse-temurin:21-jre

# Répertoire de travail pour l'exécution
WORKDIR /app

# Copier uniquement l'exécutable nécessaire depuis l'étape de build
COPY --from=build-stage /app/target/*.jar app.jar

# Exposer le port de l'application (modifier si nécessaire)
EXPOSE 8080

# Lancer l'application via Java
CMD ["java", "-jar", "app.jar"]