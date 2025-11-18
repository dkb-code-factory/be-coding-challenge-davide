# Read Me First

All the information about the code challenge is in [CODE_CHALLENGE.md](./CODE_CHALLENGE.md)

# Getting Started

1. start docker-compose with postgres
2. start the app
3. Hit the following endpoints to test the service:

```bash

curl -X POST -H "Content-Type: application/json" localhost:8080/register -d '{ "id": "bcce103d-fc52-4a88-90d3-9578e9721b36", "notifications": ["type1","type5"]}'
curl -X POST -H "Content-Type: application/json" localhost:8080/notify -d '{ "userId": "bcce103d-fc52-4a88-90d3-9578e9721b36", "notificationType": "type5", "message": "your app rocks!"}'

```

------------------------------

# Notifications System Refactor Solution

Thank you for the nice challenge DKB team, I had a lot of fun thinking about the problem and coding a solution. I'm really eager to hear from you soon.

I treated the challenge as if I'm going through a development process. I followed this flow:

`new feature/ branch ->  tests locally, commit and merge in develop branch -> test in develop and merge in uat branch -> test in uat and PR for main branch (ready for release).
`

I also changed the file structure to be more organized and divided, for the sake of the challenge it was cleaner and easier to read but in a working environment I would always propose changes like this before refactoring myself.

The system was redesigned to move from a legacy per-user notifications column to a clean, scalable, category-based subscription model.
I also thought about a very quick fix that does not alter the structure and requires minimal changes to the code, but I think it's not a feasible solution in the long run. We can discuss it during the interview if you'd like.

- **DB CHANGES:**
  - Notification_types table holds all valid types and their category.
  - | type  | category   |
    |-------|------------|
    | type3 | CATEGORY_A |
    | type5 | CATEGORY_B |

  - User_categories table tracks which categories each user is subscribed to.
  - I'm changing the db.changelog as they are for simplicity (I don't have enough information for the full refactor). 
  - In a production environment we would have to create appropriate drop columns/tables changelogs and create migration scripts to move existing user notification data to "user_categories".


- **BUSINESS LOGIC:**
  - Services now consult CategoryService instead of the notifications field directly. This allows automatic inclusion of new notification types for existing users in a category.


- **MIGRATION**
  - Existing user subscriptions must be migrated from users.notifications to user_categories. Validation queries are needed to ensure all users have correct categories.


- **KAFKA INTEGRATION**
  - The NotificationSubscriber is currently deactivated. Once enabled, the system has to ensure idempotent processing, handle retries, and avoid DB bottlenecks,


- **PERFORMANCE**
  - CategoryService.isUserAllowedForType() uses a native DB query per notification and it's a very fast performing query. 
  - Could be optimized with a caching layer (@Cacheable) for notificationType -> category and userId -> categories.


- **SCALABILITY**
  - New notification types can be added with minimal code changes, just inserting into notification_types and adding the new type to NotificationType.
  - New categories should follow the same DB-driven logic.


- **POTENTIAL BOTTLENECKS**
  - High volume of notifications could trigger DB queries per message.
  - Kafka consumer should batch or cache to avoid repeated lookups.

------------------------------------------------


Thank you for reading through this and for the opportunity.

Even if you decide to not go through the process, I would still love to hear a feedback, I'm always looking to improve and learn.
have an amazing day DKB team,

_Davide_

