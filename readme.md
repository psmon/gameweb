# PSMON's LightWeight WebGame Kit

# Game Demo1 : Card Friends

![card](/doc/cards-1.JPG)

Rule: Win with a unique card shape winner (at least 3 players)

Shuffle Condition: The game begins with a unique card shape and the rest of the card shape.

Game Step:
- Receive one random card
- Action from the left side of the dealer. One turn ends at the dealer.
- It takes 1 turn to the dealer and ends by 2 turns. Dealer changed in the same direction as the action at the start of the game
- You can do two actions in your own action time: change the opponent and the card or not.
strategy :
- You have to exchange information while exchanging cards to get and get unique card shape information.
- If the swapped card is the same as the opponent, you can assume that this card can not be a victory card.
- If the card you exchanged is different from the opponent, both cards can be a victory card, but you can not know the information with only one exchange.
- The turn is most advantageous because it is finished at the dealer, so it is better to bully the dealer for information concealment.
- It is better to exchange cards with players faster than your actions, but you may not get any useful information.
 
전략예:
 
     1.ply1(Dealer): X , ply2 : X , ply3 : X , ply4 : Y
     2.ply1(Dealer): X , ply2 : X , ply3 : X , ply4 : Y
     3.ply1(Dealer): Y , ply2 : X , ply3 : X , ply4 : X
     4.ply1(Dealer): X , ply2 : Y , ply3 : X , ply4 : X
     
 1. If you have received a card like 1, nobody knows if your card is a victory card yet.
 2. If ply2 and ply3 change cards, you can know that the victory card is Y.
 3. ply1 and ply4 have changed cards, but they do not know what the victory cards are.
 4. ply1 and ply2 changed cards. ply2 already knows that Y is a victory card, and ply1 is now known. However, ply2 from the dealer can not keep this card.
 5. ply4 may end the game without even knowing what the victory card is.
 
