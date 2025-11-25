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
- Skapat en ```getAllEvents``` endpoint som är kopplat till ``KvittUserId``
- Hade extrema problem med detta då den vägrade att låta mig komma åt endpoints markerat med @GetMapping. För att lösa detta så stängde jag ner alla connections mot PORT:8080 och började om.

**2025-10-19**
- Fixade logger

**2025-10-21**
- Fixade ``getTotalIncome`` endpoint.
  - Hade några extrema problem med denna då den efter mycket strul fick reda på att spring inte läste av Controller-filen. Eftersom att det blivit en ändring från ``boolean`` till ``BigDecimal`` vägrade spring läsa av det. Den valde då att bygga projektet från en tidigare build fil som inte innehöll de nya ändringarna.


**2025-10-30**
- Skapade en `config` fil med en `PasswordEncoder`.
- Skapade `getExpense` och `getFinancials`.
- Raderade onödiga variabler för `kvittUser` entiteten.
- Det gick rätt smärtfritt och stötte inte på några alltför stora problem.

**2025-11-11**
- Börjat med att implementera JWT till projektet.
- Bytta från WebFlux till Web MVC då det inte fanns en anledning att jobb asynkront.

**2025-11-24**
- Har under en längre tid krigat på med JWT och roller. Men igår kunde jag stolt säga att ``JWT`` samt roller är nu en del av ``Kvitt``.
- 