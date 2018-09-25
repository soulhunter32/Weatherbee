# Weatherbee


This is a Spring Boot / Angular 2 (v 6.1) project


# SERVICES EXPOSED BY BACKEND
-GET http://localhost:8080/boards/{username}
Creates a new user for {username} if he/she doesn't exists and/or loads his/her board with all its locations. 
Returns a Board structure.

-POST http://localhost:8080/boards/{username}/{location}
Queries the Yahoo! API and recovers the location info for location name {location} and forecats for folowing 10 days, adding it for the board of the {username} user.
Returns a Location structure.

-GET http://localhost:8080/boards/{username}/board/{boardId}
Forces the update of all locations of the board {boardId} of the user {username}.

-GET http://localhost:8080/boards/{username}/board/{boardId}?date={date}
Forces the update of all locations of the board {boardId} of the user {username} for the date {date} (format DD/MM/YYYY).

-DELETE http://localhost:8080/boards/{username}/{locationId}
Deletes the location {locationId} for the board of the {username} user.


# WEBSOCKET
A websocket is configured for "http://localhost:8080/socket" were regular updates of the board are published on the queue "weather-updates". This queue is consumed by the client, which
updates the locations of the current user.

# SCHEDULER
A scheduler runs every 60 seconds and updates all boards with fresh information by querying the Yahoo! Api.