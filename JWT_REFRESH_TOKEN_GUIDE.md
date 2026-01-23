# Guide JWT & Refresh Token - CampusMaster

## Qu'est-ce qu'un Refresh Token ?

Le **refresh token** est un jeton de sécurité qui permet de renouveler le **token d'accès (access token)** sans demander à l'utilisateur de se reconnecter.

## Pourquoi utiliser un Refresh Token ?

### Problème sans Refresh Token
- Le token JWT expire après 24 heures
- L'utilisateur doit se reconnecter manuellement toutes les 24 heures
- Mauvaise expérience utilisateur

### Solution avec Refresh Token
- Le token JWT expire après 24 heures (sécurité)
- Le refresh token expire après 7 jours
- L'application renouvelle automatiquement le token sans redemander les identifiants

## Différence entre Access Token et Refresh Token

| Caractéristique | Access Token | Refresh Token |
|----------------|--------------|---------------|
| **Durée de vie** | 24 heures (court) | 7 jours (long) |
| **Usage** | Authentifier chaque requête API | Obtenir un nouveau access token |
| **Fréquence d'utilisation** | À chaque requête | Seulement quand l'access token expire |
| **Exposition** | Envoyé à chaque requête | Stocké de manière sécurisée |
| **Risque si compromis** | Limité (expire vite) | Plus élevé (expire lentement) |

## Configuration actuelle dans CampusMaster

```java
// JwtService.java
@Value("${jwt.expiration:86400000}")  // 24 heures = 86 400 000 ms
private long jwtExpiration;

@Value("${jwt.refresh-expiration:604800000}")  // 7 jours = 604 800 000 ms
private long refreshExpiration;
```

## Comment utiliser le Refresh Token ?

### Scénario d'utilisation

```
1. Connexion initiale
   ├─> POST /auth/login
   └─> Réponse: {
         "token": "eyJhbGc...",           // Access Token (24h)
         "refreshToken": "eyJhbGc...",    // Refresh Token (7j)
         "userId": 2,
         "username": "Yane",
         ...
       }

2. Utilisation normale (jour 1-6)
   ├─> L'application utilise le token pour chaque requête
   └─> Header: Authorization: Bearer eyJhbGc...

3. Token expiré (après 24h)
   ├─> Requête échoue avec 401 Unauthorized
   └─> L'application détecte l'expiration

4. Renouvellement automatique
   ├─> POST /auth/refresh
   ├─> Body: { "refreshToken": "eyJhbGc..." }
   └─> Réponse: {
         "token": "NOUVEAU_TOKEN",         // Nouveau Access Token (24h)
         "refreshToken": "NOUVEAU_REFRESH", // Nouveau Refresh Token (7j)
         ...
       }

5. Refresh Token expiré (après 7j)
   ├─> /auth/refresh échoue avec 401
   └─> L'utilisateur doit se reconnecter avec /auth/login
```

## Exemple d'implémentation Frontend (JavaScript/TypeScript)

### Stockage sécurisé des tokens

```javascript
// Après connexion réussie
const authResponse = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
}).then(res => res.json());

// Stocker les tokens de manière sécurisée
localStorage.setItem('accessToken', authResponse.token);
localStorage.setItem('refreshToken', authResponse.refreshToken);
```

### Intercepteur pour renouveler automatiquement

```javascript
// Axios Interceptor (recommandé)
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Ajouter le token à chaque requête
api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Gérer l'expiration du token
api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    // Si 401 et pas encore réessayé
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Renouveler le token
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(
          'http://localhost:8080/api/auth/refresh',
          { refreshToken }
        );

        // Mettre à jour les tokens
        const { token, refreshToken: newRefreshToken } = response.data;
        localStorage.setItem('accessToken', token);
        localStorage.setItem('refreshToken', newRefreshToken);

        // Réessayer la requête originale avec le nouveau token
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh token expiré, rediriger vers login
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

### Exemple d'utilisation dans React

```typescript
// hooks/useAuth.ts
import { useState, useEffect } from 'react';
import api from './api';

export const useAuth = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      // Vérifier le token au chargement
      checkAuth();
    } else {
      setLoading(false);
    }
  }, []);

  const checkAuth = async () => {
    try {
      const response = await api.get('/users/me');
      setUser(response.data);
    } catch (error) {
      // Token invalide ou expiré
      logout();
    } finally {
      setLoading(false);
    }
  };

  const login = async (email: string, password: string) => {
    const response = await api.post('/auth/login', { email, password });
    localStorage.setItem('accessToken', response.data.token);
    localStorage.setItem('refreshToken', response.data.refreshToken);
    setUser(response.data);
    return response.data;
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
  };

  return { user, loading, login, logout };
};
```

## Bonnes pratiques de sécurité

### 1. Stockage des tokens

**❌ Mauvaise pratique:**
```javascript
// Ne jamais stocker dans des cookies non sécurisés
document.cookie = `token=${token}`;
```

**✅ Bonne pratique:**
```javascript
// localStorage pour les applications web simples
localStorage.setItem('accessToken', token);

// Ou httpOnly cookies pour plus de sécurité (nécessite configuration serveur)
// Le serveur envoie le refresh token dans un cookie httpOnly
// JavaScript ne peut pas y accéder (protection contre XSS)
```

### 2. Rotation des Refresh Tokens

Notre implémentation actuelle génère un **nouveau refresh token** à chaque renouvellement:

```java
// AuthService.java - refreshToken()
String newToken = jwtService.generateToken(user.getEmail());
String newRefreshToken = jwtService.generateRefreshToken(user.getEmail()); // Nouveau
```

**Avantages:**
- L'ancien refresh token devient invalide
- Réduit le risque si un refresh token est compromis
- Détecte les tentatives de réutilisation

### 3. Révocation des tokens (Amélioration future)

Pour une sécurité optimale, implémenter:
- Une blacklist de tokens révoqués (Redis)
- Un lien entre refresh token et device/IP
- Déconnexion de tous les devices

## Tester avec Swagger

### Étape 1: Se connecter

1. Allez sur http://localhost:8080/api/swagger-ui/index.html
2. Ouvrez `POST /auth/login`
3. Cliquez sur "Try it out"
4. Entrez:
```json
{
  "email": "fatou@gmail.com",
  "password": "votre_mot_de_passe"
}
```
5. Cliquez "Execute"
6. **Copiez le token** de la réponse

### Étape 2: Autoriser dans Swagger

1. Cliquez sur le bouton **"Authorize"** (🔓) en haut de la page
2. Collez votre token (juste le token, pas "Bearer")
3. Cliquez "Authorize" puis "Close"
4. 🎉 Vous pouvez maintenant tester tous les endpoints protégés!

### Étape 3: Tester le refresh token

1. Attendez que le token expire (24h) ou testez immédiatement
2. Ouvrez `POST /auth/refresh`
3. Entrez:
```json
{
  "refreshToken": "votre_refresh_token"
}
```
4. Vous recevrez de nouveaux tokens

## Résumé

| Quand utiliser ? | Endpoint | Token requis |
|-----------------|----------|--------------|
| **Première connexion** | `POST /auth/login` | ❌ Aucun |
| **Inscription** | `POST /auth/register` | ❌ Aucun |
| **Requêtes normales** | Tous les autres endpoints | ✅ Access Token |
| **Token expiré (24h)** | `POST /auth/refresh` | ✅ Refresh Token |
| **Refresh expiré (7j)** | `POST /auth/login` | ❌ Reconnexion complète |

## Questions fréquentes

**Q: Pourquoi ne pas avoir un token qui dure 7 jours directement?**
- Si ce token est volé, l'attaquant a accès pendant 7 jours
- Avec notre système: le token d'accès change toutes les 24h, réduisant la fenêtre d'attaque

**Q: Où stocker le refresh token côté mobile (iOS/Android)?**
- iOS: Keychain
- Android: EncryptedSharedPreferences
- React Native: react-native-keychain

**Q: Peut-on avoir plusieurs refresh tokens actifs?**
- Oui, c'est utile si l'utilisateur est connecté sur plusieurs devices
- Nécessite une table `refresh_tokens` en base de données (amélioration future)

---

**Documentation mise à jour:** 2025-12-16
