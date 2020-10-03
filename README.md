# Backend Technical Assessment

Built using Spring Boot with Hibernate ORM/JPA, and in-memory H2 database. Used lombok, modelmapper, and Hibernate validation to reduce code boiletplates.

## How to run

```shell
cd /path/to/this/
./gradlew bootRun   # or on Windows: gradlew.bat bootRun
```

Go to `http://localhost:8080/api/v1/households` and test the endpoints. The app is preloaded with sample data out of the box for convenience.

## API endpoints

| Id | Endpoint | HTTP request | JSON Body Properties / Query Parameters | Description |
| - | - | - | - | - |
| 1 | `/households` | `POST` | `housingType` | Create a household with no family members |
| 2 | `/households/{householdId}/familymembers` | `POST` | `name`, `gender`, `maritalStatus`, `spouse`, `occupationType`, `annualIncome`, `dob` | Create a family member and add it to the household |
| 3 | `/households` | `GET` | `income_gt`, `income_lt`, `age_gt`, `age_lt`, `couple` | List all households with family members, conditioned by query parameters (see below) |
| 4 | `/households/{householdId}` | `GET` | - | List all family members for the given `householdId` |
| 5 | see #3 | - | - | - | - | 
| Optional 1 | `households/{householdId}` | `DELETE` | - | Delete a household and its family members |
| Optional 2 | `households/{householdId}/familymembers/{familymemberId}` | `DELETE` | - | Delete a family member only if it belongs to the given householdId

### Query parameters for #3

- `income_gt`: Include the household only if the household income is greater than the specified amount
- `income_lt`: Include the household only if the household income is less than the specified amount
- `age_gt`: Include the household only if there exists one or more family members that are older than the specified number
- `age_lt`: Include the household only if there exists one or more family members that are younger than the specified number
- `couple`: Include the household only if it has 2 or more family members with spouse value (pass any value to `couple`)
- Combining one or more query parameters listed above is possible and will narrow the result set.

### Assumption

- For grant schemes i & v, `Househould income` = `Annual income` = the total annual income for a household 
- For grant scheme ii, husband & wife belongs to the same household

## Sample requests

Get households with or without query parameters and single household.

```json
GET http://localhost:8080/api/v1/households
GET http://localhost:8080/api/v1/households/1
GET http://localhost:8080/api/v1/households?income_lt=150000&age_lt=16
GET http://localhost:8080/api/v1/households?age_lt=18&couple=true
GET http://localhost:8080/api/v1/households?age_gt=50
GET http://localhost:8080/api/v1/households?age_lt=5
GET http://localhost:8080/api/v1/households?income_lt=100000
```

Create a household

```json
POST http://localhost:8080/api/v1/households
{
    "housingType": "HDB"
}

201 CREATED
{
    "id": 7,
    "housingType": "HDB"
}
```

Create a family member

```json
POST http://localhost:8080/api/v1/households/7/familymembers
{
    "name": "Ah Gong",
    "gender": "MALE",
    "martialStatus": "SINGLE",
    "occupationType": "EMPLOYED",
    "annualIncome": 88888,
    "dob": "1940-01-01"
}

201 CREATED
{
    "id": 65,
    "name": "Ah Gong",
    "gender": "MALE",
    "martialStatus": "SINGLE",
    "occupationType": "EMPLOYED",
    "annualIncome": 88888,
    "dob": "1940-01-01"
}
```

Delete a household or a family member

```json
DELETE http://localhost:8080/api/v1/households/1
DELETE http://localhost:8080/api/v1/households/7/familymembers/65
```

## Technical notes

- values for `gender`, `martialStatus`, `occupationType`, and `housingType` are case-sensitive and must be in UPPERCASE due to the use of enum naming convention
- `spouse` is a two-way relationship. setting a spouse for a member will set the spouse's spouse too.
- The app uses in-memory H2 database out of the box for convenience, set the properties in [application.properties](src/main/resources/application.properties) to switch to using Postgresql instead e.g.

    ```
    #spring.datasource.url=jdbc:h2:mem:testdb
    #spring.datasource.driverClassName=org.h2.Driver
    #spring.datasource.username=sa
    #spring.datasource.password=
    #spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

    spring.datasource.url=jdbc:postgresql://localhost:5432/momdb
    spring.datasource.driverClassName=org.postgresql.Driver
    spring.datasource.username=mom
    spring.datasource.password=mom
    spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
    spring.jpa.generate-ddl=true
    spring.jpa.hibernate.ddl-auto=create
    ```

- Database schema - two relations, `household` and `family_member`, with a one-to-many optional relationship between them. Technically, a househould can have zero or more family member, and a family member must belong to one and only one household. A family member also has one-to-one optional relationship with another family member, a spouse.
