#PokeDeck
![](https://github.com/DVerweij/PokeDeck/blob/master/app/src/main/res/drawable/pokedeck.png)
##By Danyllo Verweij
###As part of Native App Studio's final project
#### ©2016 Pokémon. ©1995-2016 Nintendo/Creatures Inc./GAME FREAK Inc.
#### The Pokémon TCG is owned by Nintendo, Creatures Inc. and GAME FREAK Inc.
#### API used from pokemontcg.io

Welcome to the GitHub page of my app, PokeDeck. This app allows users to look for their favourite Pokémon cards in a huge database provided by pokemontcg.io. These cards can then be looked at and added to your personal virtual deck given that you sign up for this great app. Users are allowed to make entire 60-card pokémon decks without having to own a single card and the app gives users the option to generate random hands and users can then evaluate these hands, giving the decks an overall score if this process is repeated. To keep track of users, Firebase is used to handle the authentication


###Card

To store the individual cards, a personally crafted Card object is used. This Card object contains various variables which reflect card details like it's name, it's id, the set it was released in, it's HP, it's typing, etc. The object is created by giving it's constructor the jSONObject attainted from the API request. The variables are set by parsing this jSONObject. The Card object is used in the ListViews in MainActivity, DeckActivity and HandActivity for displaying the cards in ListView's form, also the Card details will be displayed in the CardActivity when it is triggered.

###
