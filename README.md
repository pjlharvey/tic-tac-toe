# tic-tac-toe

A RESTful service to allow multiple concurrent games of tic-tac-toe to be played between users, as per the specification found [here](https://github.com/michaeldfallen/coding-tests/blob/master/Noughts_and_crosses.md)

NB: One deviation from the specification is that this program counts a vertical line as a win, whereas the specification says "Play continues until a player gets three of their marks in a row (horizontally or diagonally)". I have assumed this was an error in the specification, but would clarify this in 'real life'.

## Getting Started

This is a maven SpringBoot project with an embedded Tomcat server, which will start be default on port 8080. To run it, either

* Run from an IDE with maven support, such as Eclipse, by running the `com.pharvey.Application.java` class as a Java Application.
* Build with maven, then execute
    `java -jar target/tic-tac-toe-0.0.1-SNAPSHOT.jar`
* Use the Springboot maven plugin, and execute :
    `mvn spring-boot:run`

### Prerequisites

You will need Maven and Java 8 installed and configured

## Running the tests

The project has unit tests for the individual classes, as well as a set of integration tests for testing the REST service whilst deployed to the embedded Tomcat server. All tests will be executed by

  * Running the maven test goal `mvn clean test`
  * Alternatively, the tests will be run during the build goal `mvn clean install`

## The REST operations

When the Sprinboot application is running (see Getting started), a REST service will be available on `localhost:8080` for running the following operations :

### Start new game
----
  Starts a new game of tic-tac-toe, returning JSON representing a game, including the gameId which must be used on all subsequent requests relating to this game.

* **URL**

  /game

* **Method:**

  `POST`
  
*  **URL Params**
 
  None

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{
    "gameId": 1,
    "board": {
        "cellStates": [
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        ],
        "nextPlayer": "CROSS"
    },
    "gameStatus": "IN_PROGRESS",
    "gameWinner": null
}`
 
* **Error Response:**

  None, other than system generated 500
  
### Make a move
----
  Make a move during a game of tic-tac-toe. Supply the gameId to identify your game, as well as the player you are and the co-ordinates you want to move to, on a 3 by 3 board using the row and column request body parameters, as shown below.

* **URL**

  /game/:gameId/move

* **Method:**

  `PUT`
  
*  **URL Params**
 
  None

* **Data Params**

  `{"player" 	: "CROSS",
 "row" 		: "1",
 "column" 	: "1"}`
 
 or
 
 `{"player" 	: "NOUGHT",
 "row" 		: "1",
 "column" 	: "2"}`

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{
    "gameId": 1,
    "board": {
        "cellStates": [
            "CROSS",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        ],
        "nextPlayer": "NOUGHT"
    },
    "gameStatus": "IN_PROGRESS",
    "gameWinner": null
}`
 
* **Error Response:**
 
* **Code:** 404 NOT FOUND <br />
**Content:** `Game with ID 2 not found in list of current games`
    
* **Code:** 400 BAD REQUEST <br />
**Content:** `Illegal move : The next valid player is NOUGHT, and not CROSS`
* **Code:** 400 BAD REQUEST <br />
**Content:** `Illegal move : Row number must be between 1, 2 or 3. You supplied 4`
* **Code:** 400 BAD REQUEST <br />
**Content:** `Illegal move : Column number must be between 1, 2 or 3. You supplied 4`
* **Code:** 400 BAD REQUEST <br />
**Content:** `Illegal move : Position row 1 column 2 is already occupied with a CROSS`
* **Code:** 400 BAD REQUEST <br />
**Content:** `Illegal move : Game ID 1 is not currently in progress. Its status is WON`
    
    
  
### Get game details
----
 Returns the JSON representing a game, including the gameStatus which will tell the client whether noughts, crosses or no-one has won the game so far.

* **URL**

  /game/:gameId

* **Method:**

  `GET`
  
*  **URL Params**
 
 `gameId=[integer]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{
    "gameId": 1,
    "board": {
        "cellStates": [
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        ],
        "nextPlayer": "CROSS"
    },
    "gameStatus": "IN_PROGRESS",
    "gameWinner": null
}`
 
* **Error Response:**
  
  * **Code:** 404 NOT FOUND <br />
    **Content:** `Game with ID 2 not found in list of current games`
