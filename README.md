# Overview #

 Smart Campus Sensor & Room Management API
**5COSC022W -- Client-Server Architectures | University of Westminster**
**Student:** Maurya Patel (W2112200)
A RESTful API built with JAX-RS (Jersey 2.32) and deployed on Apache Tomcat 9.
It manages physical rooms and IoT sensors on a smart campus. You can register
rooms, attach sensors to them, post sensor readings, and the API enforces business
rules like blocking readings from sensors under maintenance and refusing to delete
rooms that still have live sensors attached.
No database. No Spring Boot. Just Jersey, Jackson, ConcurrentHashMaps, and java.util.logging.

# Stack
| Layer | Technology |
|-------|-----------|
| REST framework | Jersey 2.32 (JAX-RS 2.1, javax.ws.rs.*) |
| JSON | Jackson via jerseyedia-json-jackson |
| Server | Apache Tomcat 9.0.100 (WAR deployment) |
| Build | Maven 3, WAR packaging |
| Java | Java 8 (source + target) |
| IDE | Apache NetBeans (Maven Web Application) |


# Build and Run
### Prerequisites- JDK 8 or later- Apache NetBeans with Apache Tomcat 9.0.x configured- Maven (bundled with NetBeans)
### Steps
1. **Clone the repo**
```bash
git clone https://github.com/Maurya1112-sudo/smart-campus-api.git
```
2. **Open in NetBeans**
File > Open Project > select the `smart-campus-api` folder
3. **Clean and Build**
Right-click the project > Clean and Build
Wait for `BUILD SUCCESS` in the output panel.
4. **Run**
Right-click the project > Run (or press F6)
Tomcat starts and deploys the WAR automatically.
5. **Verify**
Open a browser or Postman and hit:
```
GET http://localhost:8080/smart-campus-api/api/v1
```
You should get a JSON discovery response with links to rooms and sensors.
**Base URL:** `http://localhost:8080/smart-campus-api/api/v1`

## Endpoints
| Method | Path | Description | Status Codes |
|--------|------|-------------|--------------|
| GET | `/` | Discovery endpoint, API metadata and HATEOAS links | 200 |
| GET | `/rooms` | List all rooms as full objects | 200 |
| POST | `/rooms` | Create a new room | 201, 415 |
| GET | `/rooms/{roomId}` | Get a specific room by ID | 200, 404 |
| DELETE | `/rooms/{roomId}` | Delete a room (blocked if sensors exist) | 204, 404, 409 |
| POST | `/sensors` | Register a sensor, validates roomId | 201, 415, 422 |
| GET | `/sensors` | List all sensors, optional ?type= filter | 200 |
| GET | `/sensors/{sensorId}` | Get a specific sensor by ID | 200, 404 |
| GET | `/sensors/{sensorId}/readings` | Get reading history for a sensor | 200, 404 |
| POST | `/sensors/{sensorId}/readings` | Add a new reading | 201, 403, 404 |


## curl Commands
```bash
# 1 - Discovery endpoint: confirms HATEOAS links to /rooms and /sensors
curl -s http://localhost:8080/smart-campus-api/api/v1    
# 2 - List all rooms: returns LIB-301 and LAB-101 as full objects
curl -s http://localhost:8080/smart-campus-api/api/v1/rooms    
# 3 - Create a room: expects 201 Created plus Location header
curl -s -i -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \-H "Content-Type: application/json" \-d '{"id":"HALL-01","name":"Main Hall","capacity":200}'
# 4 - Delete room with sensors (LIB-301 has TEMP-001): expects 409 Conflict
curl -s -i -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/LIB-301
# 5 - Delete empty room: expects 204 No Content
curl -s -i -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/HALL-01
# 6 - Create a sensor linked to LAB-101: expects 201 Created
curl -s -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \-H "Content-Type: application/json" \-d '{"id":"OCC-001","type":"Occupancy","status":"ACTIVE","currentValue":0,"roomId":"LAB-101"}'
# 7 - Create sensor with invalid roomId: expects 422 Unprocessable Entity
curl -s -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \-H "Content-Type: application/json" \-d '{"id":"X1","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"DOES-NOT-EXIST"}' \
   
# 8 - Filter sensors by type: expects only CO2-001 in the result
curl -s "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2" 

# 9 - Post a reading to an ACTIVE sensor: expects 201 plus UUID and timestamp
curl -s -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings \-H "Content-Type: application/json" \-d '{"value":23.7}'
# 10 - Post a reading to a MAINTENANCE sensor (CO2-001): expects 403 Forbidden
curl -s -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings \-H "Content-Type: application/json" \-d '{"value":500}' \

```

## Pre-Loaded Sample Data
| Type | ID | Details |
|------|----|---------|
| Room | LIB-301 | Library Quiet Study, capacity 50 |
| Room | LAB-101 | Computer Lab, capacity 30 |
| Sensor | TEMP-001 | Temperature, ACTIVE, 22.5 degrees C, in LIB-301 |
| Sensor | CO2-001 | CO2, MAINTENANCE, 450.0 ppm, in LAB-101 |

TEMP-001 is active so you can POST readings to it straight away.

CO2-001 is in MAINTENANCE so POSTing a reading to it returns 403 immediately


## Report Answers---

### Part 1.1
**Question:** In your report, explain the default lifecycle of a JAX-RS Resource class. Is a
new
instance instantiated for every incoming request, or does the runtime treat it as a singleton?
Elaborate on how this architectural decision impacts the way you manage and synchronize your
in-memory data structures (maps/lists) to prevent data loss or race conditions.

**Answer:**
JAX-RS uses a per-request lifecycle by default. Jersey creates a brand new instance of each
resource
class (RoomResource, SensorResource, etc.) for every incoming HTTP request, then discards it
once the
response is sent. Anything stored as an instance field is thrown away after each request.
Because of this, shared data like rooms, sensors, and readings cannot live inside the resource
class
itself. It has to go somewhere that survives across requests, which is why the DataStore class
uses
static fields.
The problem with static fields is that multiple threads hit them at the same time. Two
concurrent POST
requests could both read the same HashMap state, both try to write, and corrupt it. A plain
HashMap is
not thread-safe and can cause silent data loss or infinite loops under concurrent access. Using
ConcurrentHashMap fixes this because it handles concurrent reads and writes safely through
internal
segment-level locking. No explicit synchronisation blocks are needed, and multiple threads can
operate
on it without racing each other.

### Part 1.2
**Question:** Why is the provision of 'Hypermedia' (links and navigation within responses)
considered
a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client
developers
compared to static documentation?

**Answer:**
HATEOAS stands for Hypermedia as the Engine of Application State. The idea is that API
responses
include links telling the client where it can go next, rather than the client having those URLs
hardcoded from a document it read somewhere.
In this API, hitting GET /api/v1 returns a resources map with links to /api/v1/rooms and
/api/v1/sensors. A client that follows those links does not need to know the URL structure in
advance.
It discovers everything from the root endpoint.
The benefit over static documentation is that the API becomes self-describing. If a path
changes,
or new resources are added, clients following links pick up the change without needing a
documentation
update. Clients that hardcode URLs break whenever the server changes. HATEOAS places this API
at
Level 3 of Richardson's REST Maturity Model, the highest level, because it fully decouples
clients
from any assumed knowledge of the URL structure.

### Part 2.1
**Question:** When returning a list of rooms, what are the implications of returning only IDs
versus
returning the full room objects? Consider network bandwidth and client side processing.

**Answer:**
GET /rooms returns full Room objects rather than just IDs. The reason comes down to avoiding
the N+1 problem.
If the response only contained IDs, the client would need to fire a separate GET /rooms/{id}
for every
room in the list just to read the actual data. For a list of 10 rooms that is 11 total
requests. On a
slow mobile connection, each round trip adds real latency, so the user waits longer even though
the
data exists and could have come in one go.
Returning only IDs does reduce the size of the initial response, which matters when lists are
very large.
If the room count grew into the tens of thousands, pagination with lightweight summaries would
be the
sensible approach. At campus scale with a bounded number of rooms, the bandwidth cost of full
objects
is small and the gain from skipping all the follow-up requests is significant.

### Part 2.2
**Question:** Is the DELETE operation idempotent in your implementation? Provide a detailed
justification by describing what happens if a client mistakenly sends the exact same DELETE
request for a room multiple times.

**Answer:**
It depends on which scenario you are looking at.
For DELETE /rooms/HALL-01 where the room exists and has no sensors: the first call removes it
and
returns 204 No Content. A second call finds nothing and returns 404. The server state is the
same
both times (the room is gone) but the response code differs. RFC 7231 permits this behaviour
and
considers it acceptable under the definition of idempotency, which is about state rather than
response codes.
For DELETE /rooms/LIB-301 where the room has sensors attached: every call returns 409 Conflict
with
no state change at all. That path is fully idempotent because nothing changes between attempts.
So the end state is consistent across repeated calls, which satisfies the idempotency
requirement,
but the HTTP status code on the second call does differ from the first in the 204 case.

### Part 3.1
**Question:** We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the
POST
method. Explain the technical consequences if a client attempts to send data in a different
format,
such as text/plain or application/xml. How does JAX-RS handle this mismatch?

**Answer:**
If a client sends a POST request with Content-Type: text/plain or Content-Type: application/xml
to a method annotated with @Consumes(MediaType.APPLICATION_JSON), JAX-RS returns 415
Unsupported
Media Type before the method body runs at all.
The check happens at the dispatch layer. Jersey compares the incoming Content-Type header
against
the @Consumes declaration. If they do not match, the resource method is never invoked. No
exception
mapper runs, no custom logic fires. The client gets a clean 415 straight from the runtime.
This is safer than accepting any content type and trying to parse it manually, because the
runtime
handles the rejection consistently. It also means the method body stays focused on processing
valid JSON rather than having to check the content type itself.

### Part 3.2
**Question:** You implemented this filtering using @QueryParam. Contrast this with an
alternative
design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the
query
parameter approach generally considered superior for filtering and searching collections?

**Answer:**
There are a few reasons why @QueryParam is the better choice here.
First, query parameters are optional by default. GET /sensors returns everything;
GET /sensors?type=CO2 returns filtered results. With a path-based design you would need
separate
resource methods to handle the filtered and unfiltered cases, which duplicates logic.
Second, query parameters compose naturally. If you later needed to filter by both type and
status,
you could add GET /sensors?type=CO2&status;=ACTIVE. The path version would become
GET /sensors/type/CO2/status/ACTIVE, which looks like a deeply nested sub-resource and makes
no
semantic sense.
Third, the URL path is meant to identify a resource. /sensors is the collection.
/sensors/type/CO2 implies that type/CO2 is a named resource inside the sensors collection,
which
it is not. A query parameter says 'give me the sensors collection, but narrow it down this
way.'
That matches what is actually happening.

### Part 4.1
**Question:** Discuss the architectural benefits of the Sub-Resource Locator pattern. How does
delegating logic to separate classes help manage complexity in large APIs compared to defining
every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

**Answer:**
The sub-resource locator in SensorResource is the method annotated with
@Path("/{sensorId}/readings") that has no HTTP verb annotation. It just creates and returns a
new SensorReadingResource instance, and Jersey dispatches the actual HTTP method to that class.
The alternative is putting every endpoint in one class. SensorResource would end up handling
sensor
CRUD, reading history, reading creation, status checks, and anything else nested under
/sensors.
That class grows without limit and becomes hard to read, test, or change without breaking
something else.
With the locator pattern, SensorResource handles sensor-level operations and
SensorReadingResource
handles reading lifecycle. Each class has one job. If the reading logic needs changing, only
SensorReadingResource is touched. If you later add /sensors/{id}/alerts, that gets its own
class too.
The hierarchy in the code matches the hierarchy in the URLs, which makes it easier to reason
about.

### Part 5.2
**Question:** Why is HTTP 422 often considered more semantically accurate than a standard 404
when the issue is a missing reference inside a valid JSON payload?

**Answer:**
404 Not Found means the URL the client requested does not exist. But when a POST arrives at
/api/v1/sensors, that endpoint is there and working fine. The problem is not with the URL.
The problem is inside the request body. The roomId field references a room that does not exist
in the system. The JSON is syntactically valid, the Content-Type is correct, and the endpoint
was found. The server just cannot act on the request because the data inside it is logically
broken.
422 Unprocessable Entity is the right code for that situation. It tells the client: your
request
was well-formed and the server understood it, but the semantic content cannot be processed.
A 404 response would make the client think they sent the request to the wrong URL, which would
send them debugging in the wrong direction entirely.

### Part 5.4
**Question:** From a cybersecurity standpoint, explain the risks associated with exposing
internal
Java stack traces to external API consumers. What specific information could an attacker gather
from such a trace?

**Answer:**
A stack trace gives an attacker a map of the system without them needing access to the source
code.
The first problem is technology fingerprinting. A trace shows the Java version, Jersey version,
Tomcat version, and any other framework in the call stack. An attacker searches those version
numbers
against public CVE databases and finds known exploits targeting that exact configuration.
The second problem is architecture exposure. Package names and class names like
com.smartcampus.store.DataStore or com.smartcampus.resource.SensorResource reveal the internal
code
structure, naming conventions, and where the data lives. That helps an attacker understand what
to target.
The third problem is logic exposure. The sequence of method calls in a stack trace shows how
the
server processes a request internally, which helps someone craft inputs designed to reach
specific
code paths.
The GenericExceptionMapper handles this by logging the full error server-side with
Logger.severe()
where only developers can see it, and returning only a generic message to the client. The
client
learns something went wrong but nothing about the system.

### Part 5.5
**Question:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like
logging,
rather than manually inserting Logger.info() statements inside every single resource method?

**Answer:**
The main issue with manual logging is reliability. If you add Logger.info() calls inside each
resource method, you have to remember every method, including ones you add later. One missing
call
creates a blind spot in the logs. An error path handled by an exception mapper would never get
logged at all because the resource method may not have run to completion.
A ContainerRequestFilter and ContainerResponseFilter pair runs on every single request and
response
automatically, including error paths. It is written once in one class annotated with @Provider,
and Jersey registers it without any extra configuration.
It also keeps the resource methods clean. RoomResource.deleteRoom() should handle deletion
logic.
It should not be mixed up with log formatting. Separating the two makes both easier to read and
maintain. If the log format needs to change later, one class gets edited rather than every
method
across the whole codebase.
