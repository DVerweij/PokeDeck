#PokeDeck
![](https://github.com/DVerweij/PokeDeck/blob/master/app/src/main/res/drawable/pokedeck.png)
##By Danyllo Verweij
###As part of Native App Studio's final project
#### ©2016 Pokémon. ©1995-2016 Nintendo/Creatures Inc./GAME FREAK Inc.
#### The Pokémon TCG is owned by Nintendo, Creatures Inc. and GAME FREAK Inc.
#### API used from pokemontcg.io

Welcome to the GitHub page of my app, PokeDeck. This app allows users to look for their favourite Pokémon cards in a huge database provided by pokemontcg.io. These cards can then be looked at and added to your personal virtual deck given that you sign up for this great app. Users are allowed to make entire 60-card pokémon decks without having to own a single card and the app gives users the option to generate random hands and users can then evaluate these hands, giving the decks an overall score if this process is repeated. To keep track of users, Firebase is used to handle the authentication.


###Card

To store the individual cards, a personally crafted Card object is used. This Card object contains various variables which reflect card details like it's name, it's id, the set it was released in, it's HP, it's typing, etc. The object is created by giving it's constructor the jSONObject attainted from the API request. The variables are set by parsing this jSONObject. The Card object is used in the ListViews in MainActivity, DeckActivity and HandActivity for displaying the cards in ListView's form, also the Card details will be displayed in the CardActivity when it is triggered.

###Deck

A Deck object is used to contain the Card objects and serve as the user's virtual deck. The deck's size is already kept track of. Further more, as part of the functionality that allows user's to rate the generated hands, the deck's rating and it's count (so the rating can be averaged). The Deck is used in CardActivity, so cards can be added to it, DeckActivity, where the deck is displayed, and HandActivity, where the deck is used to generate the random hands. The deck has a certain restrictions on adding new cards based on the ruleset of the actual trading card game. The deck needs to be sixty cards, so not more than sixty cards can be in a deck at a time, ad the hand generation function does not work on an incomplete deck. Only up to 4 cards of cards that aren't basic energy can be used in a deck. The deck also needs at least one basic Pokémon as the game cannot be played without a basic pokemon.

##MainActivity

![]()

The MainActivity handles the search functionality of the app. 

Some key factors of this Activity:

-If not logged in, the LoginActivity will be opened before this activity prompting the user to sign in or up, or choose to continue not logged in.
-The MainActivity can directly go to the DeckActivity if logged in.
-The search button converts the text given by the user to an API request to pokemontcg.io
-The cards found are added to a ListView where the card names and IDs are displayed as well as the typing of the pokemon and basic energy cards.
-The ListView items can be clicked and clicking will bring the user to the CardActivity.

##CardActivity

![]()

The CardActivity shows the Card's picture and information (found in the Card object).

-ASyncTask is used to parse the URL and attain a BitMap and this BitMap is placed in the ImageView.
-From the CardActivity, you can go to the DeckActivity or add the card shown in the Activity to the deck.
-The buttons to add or go to the decklist are only displayed if the activity is reached from the MainActivity. If reached from the Deck or HandActivity, the buttons will be made invisible.

##DeckActivity

![]()

The DeckActivity shows a list of the cards you have in the deck and allows you to generate hands and rate said hands.

-You can go back to the MainActivity from this page.
-Signing out from this page brings the user back to the MainActivity.
-The generate random hand button triggers the HandActivity which handles the hand generation.
-You can also clear your deck and delete single list items by long-clicking.
-Clicking a card brings the user to the CardActivity.

##HandActivity

![]()

The HandActivity takes the deck attained from DeckActivity and generates a random 7 card hand.

-The rating given is saved to the deck object and is then returned to the DeckActivity.
-If the 7 card hand does NOT contain a Basic Pokémon, the hand is recursively regenerated.
-Clicking on a card in the hand brings the user to the CardActivity.

##LoginActivity

![]()

The LoginActivity handles both the signing up and signing in of users.

-The authentication is kept quite simple with the signing up ad signing in in the same activity.
-No verification or password recovery implemented yet.
-Offline button allows users to not log in at the cost of some user-only functionalities.


