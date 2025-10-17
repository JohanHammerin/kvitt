## Loggbok

<!-- ## User Stories
- Som användare vill jag kunna logga in för att få ett helikopterperspektiv gällande min ekonomi.
- Som användare vill jag kunna lägga in utgifter/inkomster för att se om jag är kvitt med mig själv.
 -->


**2025-10-09**
- Skapade upp repository.
- Skapade Spring projekt med hjälp av https://start.spring.io
- Skapade 4 st issues för mitt repo.
  - Koppla ihop databasen med IntelliJ
  - Arkitektur
  - event klass
  - user klass
- Skapade en databas via MongoDB Cloud.

**2025-10-12**
- Kopplat ihop projekt med databasen med hjälp av en ``DB_CONNECTION_STRING``.
  - Fick lite problem då IntelliJ inte kunde hitta användarnamn eller lösenord då det är gömt i en ``.env`` fil.

**2025-10-14**
- Skapade strukturen för projektet med packages.
- Skapade 2 entitetsklasser ``User`` och `Event`.

**2025-10-16**
- Skapat upp nedanstående filer för `KvittUser` & `Event`
  - Repository
  - Service
  - Controller
  - ObjectMapper
  - DTO
  - createKvittUser & createEvent metoder med tillhörande endpoints.
- Hade lite problem med ``application.properties`` då den hängde med i min git push. Men efter ett ändrat lösenord för min ``Connection String`` till MongoDB så kunde jag lösa det.

**2025-10-17**

