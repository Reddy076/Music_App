# RevPlay Technical Architecture

RevPlay is built using a **Strict 4-Tier Layered Architecture**. Each layer has a specific responsibility and can only communicate with the layer immediately below it, ensuring a modular and highly maintainable codebase.


## 🏗️ System Architecture

```text
+---------------------------------------------------------------+
|                 1. PRESENTATION LAYER (UI)                    |
|   [MainApp] --> [Menu Controllers] --> [DisplayHelper]        |
|               (Console Input / Output Handling)               |
+-------------------------------+-------------------------------+
                                |
                                v
+-------------------------------+-------------------------------+
|                 2. BUSINESS LOGIC LAYER (Service)             |
|      [UserService] [SongService] [PlaylistService] ...        |
|        (ValidationUtil, Business Rules, Transaction Logic)    |
+-------------------------------+-------------------------------+
                                |
                                v
+-------------------------------+-------------------------------+
|                 3. DATA ACCESS LAYER (DAO)                    |
|      [UserDAO] [SongDAO] ... extends [BaseDAO]                |
|           (JDBC Config, SQL Execution, ResultSet Mapping)     |
+-------------------------------+-------------------------------+
                                |
                                v
+-------------------------------+-------------------------------+
|                 4. INFRASTRUCTURE LAYER                       |
|                   ( MySQL Database Scheme )                   |
+---------------------------------------------------------------+
```



## 🛠️ Detailed Component Analysis

### 1. Presentation Layer (`com.revplay.ui`)
*   **The Hub Concept**: The application uses a "Hub-and-Spoke" model for UI navigation. `MainMenu` acts as the router, delegating control to specialized menus (`SearchMenu`, `ArtistMenu`, etc.).
*   **Lifecycle Management**: `BaseMenu` ensures that every UI component has access to the current `SessionManager` state (who is logged in) and standardizes the `display()` execution loop.
*   **Decoupled Input**: `ConsoleUtil` abstracts the complexities of `Scanner` and console formatting, providing a clean API for reading validated integers, strings, and masked passwords.

### 2. Service Layer (`com.revplay.service`)
*   **Transaction Boundary**: Services acts as the "Traffic Controller." They validate input using `ValidationUtil` BEFORE passing data to the DAOs.
*   **Business Rules**: Logic like "is this user an artist?" or "does this playlist belong to the user?" is enforced here, never in the UI or DAO.
*   **Component Examples**: `UserService` (Auth), `SearchService` (Complex search logic aggregation), `PlaylistService` (Membership management).

### 3. Data Access Layer (`com.revplay.dao`)
*   **JDBC abstraction**: `BaseDAO` provides a safe wrapper around JDBC. It forces the use of **PreparedStatements** and handles the automatic closing of `Connections` and `ResultSets` via **Try-With-Resources**.
*   **ORM Mapping**: DAOs are responsible for mapping SQL `ResultSet` rows into Java `Model` objects. 
*   **State Management**: `DatabaseConfig` manages a Singleton connection to the MySQL database, reading credentials from external properties.

### 4. Domain Models (`com.revplay.model`)
*   **Data Consistency**: These are shared across all layers. They provide a unified "language" (e.g., what a `Song` looks like) so the UI, Service, and DAO are always in sync.

---

## 🔒 Security & Cross-Cutting Concerns

| Feature | Implementation | Responsibility |
| :--- | :--- | :--- |
| **Authentication** | `SessionManager` + `UserService` | Tracks the logged-in user context globally. |
| **Password Security** | `PasswordUtil` | Implements SHA-256 hashing with per-user salts. |
| **Input Validation** | `ValidationUtil` | Regex-based checks for Email, Username, and Type safety. |
| **Logging** | `Log4j2` | Tiered logging (Console for UI, Files for Errors/Database). |
| **Resource Safety** | Try-With-Resources | Prevents Memory Leaks and DB Connection exhaustion. |

## 📡 Example Communication Flow: "Search for a Song"
1.  **UI**: `SearchMenu` captures a keyword from `ConsoleUtil`.
2.  **Service**: `SearchService.searchSongs(keyword)` is called. It might also call `ValidationUtil` to sanitize the string.
3.  **DAO**: `SongDAO.search(keyword)` executes the SQL `WHERE title LIKE %?%`.
4.  **JDBC**: `BaseDAO` prepares the statement, executes it, and returns a `List<Song>`.
5.  **UI**: `SearchMenu` receives the list and uses `DisplayHelper` to render a formatted table to the user.
