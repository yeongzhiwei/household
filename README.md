# Backend Technical Assessment

## API endpoints

| Id | Endpoint | HTTP request | JSON Body Properties / Query Parameters | Description |
| - | - | - | - | - |
| 1 | `/households` | `POST` | `housingType` | Create a household with no family members |
| 2 | `/households/{householdId}/familymembers` | `POST` | `name`, `gender`, `maritalStatus`, `spouse`, `occupationType`, `annualIncome`, `dob` | Create a family member and add it to the household |
| 3 | `/households` | `GET` | `income_gt`, `income_lt`, `age_gt`, `age_lt` | List all households with family members, conditioned by query parameters (see below) |
| 4 | `/households/{householdId}` | `GET` | - | List all family members for the given `householdId` |
| 5 | see #3

### Query parameters for #3

- `income_gt`: Include the household only if the household income is greater than the specified amount
- `income_lt`: Include the household only if the household income is less than the specified amount
- `age_gt`: Include the household only if there exists one or more family members that are older than the specified number
- `age_lt`: Include the household only if there exists one or more family members that are younger than the specified number
- Combining one or more query parameters listed above is possible and will narrow the result set.

## Database schema

Based on the requirements, I define two relations, `household` and `family_member`, with a one-to-many optional relationship between them. Technically, a househould can have zero or more family member, and a family member must belong to one and only one household.
