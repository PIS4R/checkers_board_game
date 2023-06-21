# checkers_board_game
Chceckers game written in java. 
# The rules:

Classic checkers game (also known as Brazilian checkers) is played on an 8x8 board with alternating light and dark colored squares.

Each player starts the game with twelve pieces (one white and one red) placed on the darker squares of the board.

The player with the white pieces makes the first move, after which the players take turns making their moves.

The objective of the game is to capture all of the opponent's pieces (including kings - see below) or to block all the remaining pieces on the board, preventing the opponent from making any more moves. If neither player can achieve this (each player makes 15 king moves without reducing the number of pieces on the board), the game ends in a draw.

Pieces can move diagonally forward by one square to an empty space.

A piece captures an opponent's adjacent piece (or king) by jumping over it to the empty square just beyond it diagonally. Captured pieces are removed from the board after the move is completed.

Pieces can capture both forward and backward.

In a single move, a player is allowed to make multiple captures with the same piece by jumping over consecutive opponent's pieces (or kings).

Captures are mandatory.

When a piece reaches the last row of the board, it becomes a king. However, if the piece reaches the last row as a result of a capture and there is an opportunity for another capture (backward), it must be taken, and the piece does not become a king.

When a piece becomes a king, the turn passes to the opponent.

Kings can move forward or backward diagonally in a single move, any number of squares, stopping on empty spaces.

A king can capture an opponent's piece (or king) from any distance along a diagonal line by jumping over it, as long as there is at least one empty square beyond it. The king can choose any of those squares to continue capturing (in the same or perpendicular line).

When there are multiple possible captures, a player must make the maximum capture (i.e., the one that captures the highest number of opponent's pieces or kings).

During a capture, a piece (or king) cannot jump over the same opponent's piece (or king) more than once.
