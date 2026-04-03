# AGENTS.md

Configuration des agents pour le projet Tereroa. Chaque agent a un role precis et des responsabilites claires.

## Architecture

```
Manager (Claude principal)
  ├── Dev Agent     — implemente les changements
  ├── Test Agent    — ecrit et execute les tests
  └── Review Agent  — revue qualite du code
```

Le Manager orchestre : il dispatche les agents, consolide les resultats, et corrige les problemes post-review.

## Agents

### Dev Agent

**Role** : Implementer les modifications de code (features, refactorings, bugfixes).

**Responsabilites** :
- Modifier le code source selon les instructions du Manager
- Verifier la compilation apres chaque changement (`./mvnw clean compile`)
- Executer les tests existants (`./mvnw clean test`) et corriger les regressions
- Commiter le travail avec un message descriptif en fin de tache

**Regles** :
- Ne pas creer de fichiers inutiles
- Respecter l'architecture existante (controllers → services → repositories)
- Utiliser l'injection par constructeur (`@RequiredArgsConstructor` + `private final`)
- Les mappers utilisent `componentModel = "spring"`
- Les entites JPA initialisent leurs collections (`= new HashSet<>()`)
- Les prix/montants sont en `BigDecimal`, les dates en `LocalDate`
- Commiter avec le format : `<type>: <description>` (ex: `fix: Correction du chevauchement de dates`)

**Verification obligatoire avant commit** :
```bash
./mvnw clean test
```

### Test Agent

**Role** : Ecrire des tests robustes et verifier la couverture.

**Responsabilites** :
- Ajouter des tests unitaires couvrant les cas nominaux et les cas limites
- Suivre le pattern GIVEN / WHEN / THEN
- Executer tous les tests et corriger les echecs
- Commiter les nouveaux tests

**Regles** :
- Tests unitaires purs : `@ExtendWith(MockitoExtension.class)` avec `@Mock` / `@InjectMocks`
- Tests controller : `@WebMvcTest` avec `@MockBean`
- Tests mapper (Spring) : `@SpringBootTest` avec `@Autowired`
- Ne jamais utiliser `@SpringBootTest` pour un test unitaire de service
- JUnit 5 + Mockito, pas de JUnit 4

**Verification obligatoire avant commit** :
```bash
./mvnw clean test
```

### Review Agent

**Role** : Revue qualite du code apres modifications.

**Type** : `feature-dev:code-reviewer`

**Responsabilites** :
- Relire tous les fichiers modifies
- Detecter les bugs, failles de securite, problemes JPA (lazy loading, N+1)
- Verifier la coherence des imports, des dependances, et de l'architecture
- Rapporter uniquement les issues a **haute confiance** avec fichier, ligne, severite, et fix propose

**Regles** :
- Ne pas modifier le code (lecture seule)
- Classer les issues : CRITICAL > IMPORTANT > MINOR
- Verifier que la compilation passe (`./mvnw clean compile`)

## Workflow standard

1. **Manager** analyse la demande et definit les taches
2. **Dev Agent** implemente, verifie la compilation, lance les tests, commite
3. **Test Agent** + **Review Agent** lances en parallele :
   - Test Agent : ajoute des tests, les execute, commite
   - Review Agent : releve les problemes restants
4. **Manager** corrige les issues du Review Agent, relance les tests, commite

## Commandes de verification

```bash
# Compilation seule
./mvnw clean compile

# Tests
./mvnw clean test

# Build complet (compile + test + package)
./mvnw clean package
```
