package mamurciac.unal.edu.co.challenge2_tictactoe;

import java.util.Random;

public class TicTacToeGame{
    public static final char humanPlayer = 'X', computerPlayer = 'O', openSpot = ' ';
    public static char gameBoard[] = {'?', '?', '?', '?', '?', '?', '?', '?', '?'};
    public static final int numberSpots = 9, gameNotFinished = 0, gameTied = 1, gameWithHumanWinner = 2, gameWithComputerWinner = 3;
    public boolean gameOver = false;

    private Random numberGenerator = new Random();

    //It clears the board of all X's and O's by setting all spots to openSpot
    public void clearBoard(){
        for(int spot = 0; spot < numberSpots; spot++){
            gameBoard[spot] = openSpot;
        }
    }

    //It sets the given player at the given location on the gameboard. The location must be available or the board won't be changed
    public void setMove(char player, int location){
        gameBoard[location] = player;
    }

    //It computes the best move for the computer to make. You must call setMove() to actually make the computer move to that location
    public int getComputerMove(){
        int move;

        //It checks whether there's a move computerPlayer can make to win
        for(int spot = 0; spot < numberSpots; spot++){
            if(gameBoard[spot] != humanPlayer && gameBoard[spot] != computerPlayer){
                char currentSpot = gameBoard[spot];
                gameBoard[spot] = computerPlayer;
                if(checkForWinner() == gameWithComputerWinner){
                    return spot;
                }else{
                    gameBoard[spot] = currentSpot;
                }
            }
        }

        //It checks whether there's a move computerPlayer can make to block humanPlayer from winning
        for(int spot = 0; spot < numberSpots; spot++){
            if(gameBoard[spot] != humanPlayer && gameBoard[spot] != computerPlayer){
                char currentSpot = gameBoard[spot];
                gameBoard[spot] = humanPlayer;
                if(checkForWinner() == gameWithHumanWinner){
                    return spot;
                }else{
                    gameBoard[spot] = currentSpot;
                }
            }
        }

        //It generates a random move. This move must be available to make a right gamble
        do{
            move = numberGenerator.nextInt(numberSpots);
        }while(gameBoard[move] == humanPlayer || gameBoard[move] == computerPlayer);
        return move;
    }

    //It checks for a winner and return a status value indicating who has won. It returns 0 if no winner or tie yet, 1 if it's a tie, 2 if HUMAN_PLAYER won or 3 if COMPUTER_PLAYER won
    public int checkForWinner(){
        //Row Spot Review
        for(int spot = 0; spot <= 6; spot += 3){
            if(gameBoard[spot] == humanPlayer && gameBoard[spot + 1] == humanPlayer && gameBoard[spot + 2] == humanPlayer){
                gameOver = true;
                return gameWithHumanWinner;
            }
            if(gameBoard[spot] == computerPlayer && gameBoard[spot + 1] == computerPlayer && gameBoard[spot + 2] == computerPlayer){
                gameOver = true;
                return gameWithComputerWinner;
            }
        }

        //Column Spot Review
        for(int spot = 0; spot <= 2; spot++){
            if(gameBoard[spot] == humanPlayer && gameBoard[spot + 3] == humanPlayer && gameBoard[spot + 6] == humanPlayer){
                gameOver = true;
                return gameWithHumanWinner;
            }
            if(gameBoard[spot] == computerPlayer && gameBoard[spot + 3] == computerPlayer && gameBoard[spot + 6] == computerPlayer){
                gameOver = true;
                return gameWithComputerWinner;
            }
        }

        //Diagonal Spot Review
        if((gameBoard[0] == humanPlayer && gameBoard[4] == humanPlayer && gameBoard[8] == humanPlayer) || (gameBoard[2] == humanPlayer && gameBoard[4] == humanPlayer && gameBoard[6] == humanPlayer)){
            gameOver = true;
            return gameWithHumanWinner;
        }
        if((gameBoard[0] == computerPlayer && gameBoard[4] == computerPlayer && gameBoard[8] == computerPlayer) || (gameBoard[2] == computerPlayer && gameBoard[4] == computerPlayer && gameBoard[6] == computerPlayer)){
            gameOver = true;
            return gameWithComputerWinner;
        }

        //Possible Tie Review
        for(int spot = 0; spot < numberSpots; spot++){
            //If a spot isn't fill, then no one has won yet
            if(gameBoard[spot] != humanPlayer && gameBoard[spot] != computerPlayer){
                return gameNotFinished;
            }
        }

        //If all places are taken, so it's a tie
        gameOver = true;
        return gameTied;
    }
}