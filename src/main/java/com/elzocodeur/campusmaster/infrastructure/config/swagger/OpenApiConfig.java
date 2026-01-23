package com.elzocodeur.campusmaster.infrastructure.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {

    private final Environment environment;

    public OpenApiConfig(Environment environment) {
        this.environment = environment;
    }

    @Value("${app.name:CampusMaster}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.description:Campus Management System}")
    private String appDescription;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";

        // Détection automatique du profil actif
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProd = Arrays.asList(activeProfiles).contains("prod");
        boolean isDev = Arrays.asList(activeProfiles).contains("dev");
        boolean isH2 = Arrays.asList(activeProfiles).contains("h2");

        // Construction dynamique de la liste des serveurs
        List<Server> servers = buildServerList(isProd, isDev, isH2);

        // Badge de profil dans la description
        String profileBadge = getProfileBadge(isProd, isDev, isH2);

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Entrez le token JWT. Exemple: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                        )
                )
                .info(new Info()
                        .title(appName + " API " + profileBadge)
                        .version(appVersion)
                        .description(appDescription + " - API REST Documentation\n\n" +
                                "**Profil actif:** " + getActiveProfileName() + "\n\n" +
                                "## Authentication\n" +
                                "1. Utilisez `/auth/register` pour créer un compte\n" +
                                "2. Utilisez `/auth/login` pour obtenir votre token JWT\n" +
                                "3. Cliquez sur le bouton 'Authorize' 🔒 en haut à droite\n" +
                                "4. Entrez votre token (sans le préfixe 'Bearer')\n" +
                                "5. Le token expire après 24h, utilisez `/auth/refresh` pour le renouveler\n\n" +
                                "## CORS\n" +
                                "Les origines autorisées sont configurées dans application.yml\n" +
                                "En développement: toutes les origines (*)\n" +
                                "En production: domaines spécifiques uniquement")
                        .contact(new Contact()
                                .name("CampusMaster Team")
                                .email("contact@campusmaster.com")
                                .url("https://github.com/campusmaster"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(servers);
    }

    /**
     * Construction de la liste des serveurs selon le profil actif
     */
    private List<Server> buildServerList(boolean isProd, boolean isDev, boolean isH2) {
        List<Server> servers = new ArrayList<>();

        if (isProd) {
            // En production, afficher uniquement le serveur de production
            servers.add(new Server()
                    .url("https://campusmaster-campusmaster-v1.onrender.com" + contextPath)
                    .description("🚀 Production Server (Render)"));
        } else if (isDev || isH2) {
            // En développement, afficher le serveur local et optionnellement le serveur de prod
            servers.add(new Server()
                    .url("http://localhost:" + serverPort + contextPath)
                    .description("🔧 Local Development Server"));

            // Ajouter le serveur de production pour tester
            servers.add(new Server()
                    .url("https://campusmaster-campusmaster-v1.onrender.com" + contextPath)
                    .description("🚀 Production Server (Render)"));
        } else {
            // Par défaut, afficher les deux
            servers.add(new Server()
                    .url("http://localhost:" + serverPort + contextPath)
                    .description("🔧 Local Development Server"));
            servers.add(new Server()
                    .url("https://campusmaster-campusmaster-v1.onrender.com" + contextPath)
                    .description("🚀 Production Server (Render)"));
        }

        return servers;
    }

    /**
     * Récupère le nom du profil actif
     */
    private String getActiveProfileName() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length > 0) {
            return String.join(", ", profiles).toUpperCase();
        }
        return "DEFAULT";
    }

    /**
     * Génère un badge pour le profil actif
     */
    private String getProfileBadge(boolean isProd, boolean isDev, boolean isH2) {
        if (isProd) {
            return "[PRODUCTION]";
        } else if (isDev) {
            return "[DEVELOPMENT]";
        } else if (isH2) {
            return "[H2/TEST]";
        }
        return "";
    }
}
