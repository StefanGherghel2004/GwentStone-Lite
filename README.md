##### Student - Gherghel Stefan-Ciprian 323CA

# Assignment 0 OOP  - GwentStone Lite

#### Assignment Link: [https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/tema](https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/tema)

Throughout this project, I implemented several classes that interact with each other to create a functional game system.
Here’s a breakdown of how these classes work together:

## Interaction of Classes

#### InputHandler Class
The **InputHandler** class is a crucial part of the game, responsible for the initial setup of the game and managing
and processing various inputs related to the game's setup, debugging, statistics, and in-game actions. It is designed
to interpret commands that are received as input and generate appropriate responses, which are in the form of JSON
objects. These responses provide feedback to the user or modify the game state accordingly.

#### Game Class
The **Game** class The Game class in your code represents the core logic and mechanics of a card game between two
players. It maintains the game's state, manages the flow of turns, and handles various actions like drawing cards,
managing mana, and updating player stats. It contains the **Board** class instance that represents the actual state of
the game, containing the players and the table of cards.
#### Board Class
This class provides functionality for accessing, manipulating, and displaying the board's state in JSON format,
respectively the cards on the board and the heroes of the players.

#### Player Class
The **Player** class contains the decks, usingDeck(the one picked at the beginning of the game), hand and hero fields,
offering functionality for placing a card on the table and representing some data in JSON format

#### Deck Class
The **Deck** class contains an ArrayList of cards, containing the functionality to shuffle the cards with a given seed. 

#### Card and its subclasses (MinionCard, HeroCard)
The **Card** class serves as a prototype for MinionCard and HeroCard classes and can be made abstract.
The methods in subclasses of **Card** class offer functionality to attack other cards or ArrayList of cards based
on current game context, updating the JSON output in case of an error or if the game ends when a hero is killed.


## Potential Improvements
While implementing this project, I realized that the structure could have benefited from a more careful application of
object-oriented design principles. One key improvement would have been the creation of a **skeleton structure** before
diving into the actual implementation, defined by a more strategic use of inheritance and polymorphism to 
handle different types of cards (e.g., MinionCard, HeroCard) and game actions. This would help reduce redundancy and 
increase flexibility for future changes and expansions.
