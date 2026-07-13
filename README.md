# Media Archive Manager

A Java Spring Boot backend for managing media assets and metadata.

The application was designed as a portfolio project inspired by professional sports-media and broadcast workflows. It supports video ingestion, metadata persistence, media search, duplicate detection, pagination, file download, metadata updates, and physical file deletion.

## Features

* Upload MP4 video files using multipart form data
* Store video files in local file storage
* Store searchable metadata in a relational database
* PostgreSQL support for production-style persistence
* H2 support for local development
* SHA-256 duplicate-file detection
* Search media by title, team, player, and stadium
* Pagination and sorting
* Update media metadata without re-uploading the file
* Download stored media files
* Delete both the database record and physical file
* DTO-based API responses
* Centralized exception handling
* Structured logging with SLF4J
* Appropriate HTTP response codes

## Technology Stack

* Java 26
* Spring Boot 4.1
* Spring Web MVC
* Spring Data JPA
* Hibernate
* PostgreSQL
* H2 Database
* Maven
* SLF4J and Logback
* Postman
* IntelliJ IDEA

## Architecture

```text
Client / Postman
        |
        v
MediaAssetController
        |
        v
MediaAssetService
   |            |
   v            v
FileStorage   ChecksumUtil
Service
   |
   v
Local Storage

MediaAssetService
        |
        v
MediaAssetRepository
        |
        v
PostgreSQL / H2
```

The project follows a layered architecture:

* Controller layer: receives HTTP requests and returns responses
* Service layer: contains business logic and coordinates workflows
* Repository layer: communicates with the database through Spring Data JPA
* Model layer: represents database entities
* DTO layer: controls data exposed through the API
* Storage layer: manages physical media files
* Utility layer: provides reusable functionality such as checksum generation
* Exception layer: provides centralized error handling

## Media Ingestion Workflow

```text
Upload video
    |
    v
Validate file and metadata
    |
    v
Store video on disk
    |
    v
Generate SHA-256 checksum
    |
    v
Check for duplicates
    |
    v
Create media metadata
    |
    v
Save metadata in database
    |
    v
Return 201 Created
```

The video file is stored in the local `storage` directory.

The database stores metadata such as:

* ID
* Filename
* Title
* Team
* Player
* Stadium
* Game date
* Codec
* File size
* Storage path
* SHA-256 checksum
* Upload date
* Status

## API Endpoints

### Health Check

```http
GET /
```

```http
GET /api/health
```

### Upload a Media Asset

```http
POST /api/media-assets/upload
```

Request type:

```text
multipart/form-data
```

Fields:

```text
file       File   Required MP4 file
title      Text   Required
team       Text   Optional
player     Text   Optional
stadium    Text   Optional
gameDate   Text   Optional, format YYYY-MM-DD
codec      Text   Optional
```

Example response:

```json
{
  "id": 1,
  "filename": "NFL_reel.mp4",
  "title": "NFL Rules Explained",
  "team": "Kansas City Chiefs",
  "player": "Patrick Mahomes",
  "stadium": "Arrowhead Stadium",
  "gameDate": "2026-10-18",
  "codec": "H.264",
  "fileSizeBytes": 15686760,
  "uploadedAt": "2026-07-13T10:39:23",
  "status": "ARCHIVED"
}
```

Possible responses:

```text
201 Created
400 Bad Request
409 Conflict
500 Internal Server Error
```

### Get All Media Assets

```http
GET /api/media-assets
```

### Get One Media Asset

```http
GET /api/media-assets/{id}
```

Example:

```http
GET /api/media-assets/1
```

### Get Paginated Media Assets

```http
GET /api/media-assets/page?page=0&size=10&sortBy=uploadedAt&direction=desc
```

Parameters:

```text
page       Page number, beginning at 0
size       Number of results per page
sortBy     Entity field used for sorting
direction  asc or desc
```

### Search by Team

```http
GET /api/media-assets/search/team?team=Chiefs
```

### Search by Player

```http
GET /api/media-assets/search/player?player=Mahomes
```

### Search by Title

```http
GET /api/media-assets/search/title?title=touchdown
```

### Search by Stadium

```http
GET /api/media-assets/search/stadium?stadium=Arrowhead
```

### Update Metadata

```http
PATCH /api/media-assets/{id}
```

Request body:

```json
{
  "title": "Updated NFL Rules Explanation",
  "status": "PUBLISHED"
}
```

Only supplied fields are updated.

### Download a Media File

```http
GET /api/media-assets/{id}/download
```

Returns the physical media file.

### Delete a Media Asset

```http
DELETE /api/media-assets/{id}
```

Deletes:

* The database record
* The physical file from local storage

Successful response:

```text
204 No Content
```

## HTTP Status Codes

```text
200 OK                 Request completed successfully
201 Created            Media asset created successfully
204 No Content         Media asset deleted successfully
400 Bad Request        Invalid file or metadata
404 Not Found          Media asset does not exist
409 Conflict           Duplicate media file detected
500 Internal Server Error
```

## Duplicate Detection

Every uploaded file receives a SHA-256 checksum.

The application checks the checksum against existing database records before saving the asset.

```text
Same filename, different content
→ accepted

Different filename, identical content
→ rejected as duplicate
```

A duplicate upload returns:

```text
409 Conflict
```

## Database Configuration

### Default H2 Profile

The default application configuration uses an H2 file database.

```properties
spring.datasource.url=jdbc:h2:file:./data/mediaarchive
spring.datasource.username=sa
spring.datasource.password=
```

The H2 console is available at:

```text
http://localhost:8080/h2-console
```

### PostgreSQL Profile

Create a PostgreSQL database named:

```text
mediaarchive
```

The PostgreSQL configuration uses:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mediaarchive
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
```

Set these environment variables:

```text
SPRING_PROFILES_ACTIVE=postgres
DB_PASSWORD=your_postgresql_password
```

Do not commit database passwords to source control.

## Running the Application

Clone the repository:

```bash
git clone https://github.com/Dibello80/media-archive-manager.git
```

Enter the project:

```bash
cd media-archive-manager
```

Run using the Maven Wrapper on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Run using the Maven Wrapper on macOS or Linux:

```bash
./mvnw spring-boot:run
```

The application starts at:

```text
http://localhost:8080
```

## Local Files Excluded From Git

The following are excluded using `.gitignore`:

```text
storage/
data/
target/
.idea/
*.iml
*.mv.db
*.trace.db
```

This prevents uploaded media, local databases, build output, and IDE configuration files from being committed.

## Example Interview Explanation

I designed and built a media asset management backend using Java and Spring Boot. The application stores physical video files separately from their metadata, which is persisted using Spring Data JPA and PostgreSQL.

The upload workflow validates the incoming MP4, stores it on disk, calculates a SHA-256 checksum, checks the database for duplicate content, and then saves searchable metadata. The API supports searching, pagination, metadata updates, downloads, and deletion of both the metadata record and physical file.

I used a layered architecture with controller, service, repository, entity, DTO, storage, utility, and exception-handling components. I also added structured logging and PostgreSQL configuration through environment variables.

## Future Improvements

* AWS S3 or Azure Blob Storage
* Elasticsearch or OpenSearch
* FFmpeg metadata extraction
* Automatic thumbnail generation
* Proxy video generation
* Authentication and role-based authorization
* Asynchronous media processing
* Docker and Docker Compose
* Automated unit and integration tests
* OpenAPI documentation
* Cloud deployment

## Author

Angelo R. Dibello

© 2026 Angelo R. Dibello

Portfolio project demonstrating a production-inspired media asset management backend built with Java, Spring Boot, JPA, and PostgreSQL.
