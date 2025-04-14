# Customer Management Service

REST alapú Spring Boot alkalmazás ügyfelek kezelésére és műveletek naplózására.

## Telepítés és Futtatás

### Előfeltételek
- Java 17
- Maven 3.x
- PostgreSQL 12+

### Adatbázis beállítása
1. Hozzon létre egy új adatbázist:
```sql
CREATE DATABASE customer_db;
```

2. Konfigurálja az alkalmazás.yml fájlt:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/customer_db
    username: your_username
    password: your_password
```

### Alkalmazás indítása
```bash
mvn spring-boot:run
```

## API Végpontok

### 1. Új ügyfél létrehozása

**Végpont:** `POST /api/customers`

**Curl példa:**
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kiss János",
    "age": 30,
    "dateOfBirth": "1994-01-15",
    "address": "1234 Budapest, Példa utca 1.",
    "gender": "M"
  }'
```

**Request body séma:**
```json
{
    "name": "string (kötelező, max 100 karakter)",
    "age": "number (pozitív szám)",
    "dateOfBirth": "string (kötelező, formátum: yyyy-MM-dd)",
    "address": "string (opcionális, max 200 karakter)",
    "gender": "string (opcionális, 'M' vagy 'F')"
}
```

**Sikeres válasz (200 OK):**
```json
{
    "id": 1,
    "name": "Kiss János",
    "age": 30,
    "dateOfBirth": "1994-01-15",
    "address": "1234 Budapest, Példa utca 1.",
    "gender": "M"
}
```

### 2. Ügyfelek listázása

**Végpont:** `GET /api/customers`

**Curl példák:**

Alapértelmezett lekérdezés:
```bash
curl http://localhost:8080/api/customers
```

Oldal méret és szám megadása:
```bash
curl "http://localhost:8080/api/customers?page=0&size=5"
```

Rendezés név szerint csökkenő sorrendben:

URL encodolva:
[{"field","name","direction","desc"}]
```bash
curl "http://localhost:8080/api/customers?size=2&page=1&sort=%5B%7B%22field%22%3A%22name%22%2C%22direction%22%3A%22desc%22%7D%5D"
```

**Query paraméterek:**
- `page`: oldalszám (0-tól kezdődik, alapértelmezett: 0)
- `size`: oldal mérete (alapértelmezett: 10)
- `sort`: rendezés (formátum: property,direction)
  - field: name, age, dateOfBirth, stb.
  - direction: asc vagy desc

### 3. Ügyfél módosítása

**Végpont:** `PUT /api/customers/{id}`

**Curl példa:**
```bash
curl -X PUT http://localhost:8080/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kiss János Módosított",
    "age": 31,
    "dateOfBirth": "1994-01-15",
    "address": "1234 Budapest, Új utca 3.",
    "gender": "M"
  }'
```

### 4. Ügyfél törlése

**Végpont:** `DELETE /api/customers/{id}`

**Curl példa:**
```bash
curl -X DELETE http://localhost:8080/api/customers/1
```

### 5. Audit események lekérdezése

**Végpont:** `GET /actuator/auditevents`

**Curl példák:**

Alapértelmezett lekérdezés:
```bash
curl http://localhost:8080/actuator/auditevents
```

**Végpont:** `GET /actuator/auditevents/pageable?size=2&page=1`

**Curl példák:**

Alapértelmezett lekérdezés:
```bash
curl http://localhost:8080/actuator/auditevents/pageable?size=2&page=1
```

### 6. Alkalmazás állapot ellenőrzése

**Végpont:** `GET /actuator/health`

**Curl példa:**
```bash
curl http://localhost:8080/actuator/health
```

## Hibakezelés

### Általános hibakódok

- **400 Bad Request**: Érvénytelen bemenet
- **404 Not Found**: A kért erőforrás nem található
- **500 Internal Server Error**: Szerver oldali hiba

### Példák hibaüzenetekre

1. Érvénytelen név (túl hosszú):
```json
{
    "timestamp": "2024-03-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "A név hossza nem haladhatja meg a 100 karaktert"
}
```

2. Negatív életkor:
```json
{
    "timestamp": "2024-03-15T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Az életkor pozitív szám kell legyen"
}
```

## Audit naplózás

Az alkalmazás a következő műveleteket naplózza:
- CREATE_CUSTOMER: Új ügyfél létrehozása
- UPDATE_CUSTOMER: Ügyfél módosítása
- DELETE_CUSTOMER: Ügyfél törlése

Audit bejegyzés példa:
```json
{
    "id": 1,
    "action": "CREATE_CUSTOMER",
    "customerId": 1,
    "request": "{\"name\":\"Kiss János\",\"age\":30}",
    "status": "SUCCESS",
    "creationDateTime": "2024-03-15T10:30:00"
}
```

## Validációs szabályok részletesen

### Customer entitás

| Mező | Típus | Validáció | Leírás |
|------|--------|------------|---------|
| name | String | @NotBlank, @Size(max=100) | Kötelező, max 100 karakter |
| age | Integer | @Positive | Pozitív szám |
| dateOfBirth | LocalDate | @NotNull | Kötelező, yyyy-MM-dd formátum |
| address | String | @Size(max=200) | Opcionális, max 200 karakter |
| gender | String | @Pattern(regexp="^[MF]$") | Opcionális, M vagy F |

## Fejlesztői információk

### Használt technológiák
- Spring Boot 3.2.3
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Maven
- JUnit 5
- Lombok


## Tesztkörnyezet

#### Integrációs tesztek
- Az alkalmazás végpontjainak teljes körű tesztelése
- PostgreSQL adatbázist használ
- Valós HTTP kéréseket szimulál MockMvc segítségével
- Tranzakcionális tesztek, minden teszt után visszaáll az eredeti állapot

### Tesztkörnyezet konfigurációja

**test // application.yml:**
```yaml
spring:
   application:
      name: customer_management
   datasource:
      driver-class-name: org.h2.Driver
      password:
      url: jdbc:h2:mem:testdb;NON_KEYWORDS=USER;MODE=PostgreSQL
      username: sa
   jpa:
      open-in-view: false
      database-platform: org.hibernate.dialect.PostgreSQLDialect
```

### Tesztesetek

#### CustomerController Integrációs tesztek:

1. Ügyfél létrehozása:
   - Sikeres létrehozás valid adatokkal
   - Validációs hibák kezelése (név, életkor, stb.)

2. Ügyfél lekérdezése:
   - Lapozás tesztelése (default és egyedi méret)
   - Rendezés tesztelése

3. Ügyfél módosítása:
   - Sikeres módosítás
   - Nem létező ID kezelése
   - Validációs hibák kezelése

4. Ügyfél törlése:
   - Sikeres törlés
   - Nem létező ID kezelése

### Tesztelési best practices

1. Tesztadatok kezelése:
   - Minden teszt tiszta környezetből indul
   - @BeforeEach használata a tesztadatok inicializálására

2. Assertion best practices:
   - Részletes hibaüzenetek használata
   - Több assertion használata egy tesztben

3. Tesztek izolációja:
   - @Transactional annotáció használata
   - Független tesztesetek
   - Mockolt külső függőségek

### Tesztek karbantartása

1. Naming conventions:
   - [TestedMethod]_[Scenario]_[ExpectedResult]
