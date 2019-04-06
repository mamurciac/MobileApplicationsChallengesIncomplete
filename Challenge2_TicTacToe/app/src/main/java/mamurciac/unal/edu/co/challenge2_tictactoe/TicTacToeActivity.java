package mamurciac.unal.edu.co.challenge2_tictactoe;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class TicTacToeActivity extends AppCompatActivity{
    //It represents the game's internal state
    private TicTacToeGame ticTacToeGame;
    //The buttons make up the board
    private Button boardButtons[];
    //Text displayed as game's information (Turn and winner's game)
    private TextView infoGame, infoHumanWins, infoAndroidWins, infoTies;

    private int turn = 1, humanWins = 0, androidWins = 0, ties = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        boardButtons = new Button[TicTacToeGame.numberSpots];
        boardButtons[0] = findViewById(R.id.spot_one);
        boardButtons[1] = findViewById(R.id.spot_two);
        boardButtons[2] = findViewById(R.id.spot_three);
        boardButtons[3] = findViewById(R.id.spot_four);
        boardButtons[4] = findViewById(R.id.spot_five);
        boardButtons[5] = findViewById(R.id.spot_six);
        boardButtons[6] = findViewById(R.id.spot_seven);
        boardButtons[7] = findViewById(R.id.spot_eight);
        boardButtons[8] = findViewById(R.id.spot_nine);
        infoGame = findViewById(R.id.game_information);
        infoHumanWins = findViewById(R.id.human_wins);
        infoAndroidWins = findViewById(R.id.android_wins);
        infoTies = findViewById(R.id.number_ties);
        ticTacToeGame = new TicTacToeGame();
        startNewGame();
    }

    private void startNewGame(){
        ticTacToeGame.clearBoard();

        //It resets all buttons
        for(int spot = 0; spot < boardButtons.length; spot++){
            boardButtons[spot].setText("");
            boardButtons[spot].setEnabled(true);
            boardButtons[spot].setOnClickListener(new ButtonClickListener(spot));
        }

        //According to the turn, human or computer player goes first
        if(turn % 2 == 1){
            infoGame.setText(R.string.first_turn_human);
        }else{
            infoGame.setText(R.string.first_turn_computer);
            int move = ticTacToeGame.getComputerMove();
            ticTacToeGame.setMove(TicTacToeGame.computerPlayer, move);
            boardButtons[move].setEnabled(false);
            boardButtons[move].setText(String.valueOf(TicTacToeGame.computerPlayer));
            boardButtons[move].setTextColor(Color.rgb(200,0,0));
            infoGame.setText(R.string.turn_human);
        }
        infoHumanWins.setText("Human Wins: " + humanWins);
        infoAndroidWins.setText("Android Wins: " + androidWins);
        infoTies.setText("Ties: " + ties);
    }

    private void setMove(char player, int location){
        ticTacToeGame.setMove(player, location);
        boardButtons[location].setEnabled(false);
        boardButtons[location].setText(String.valueOf(player));

        if(player == TicTacToeGame.humanPlayer){
            boardButtons[location].setTextColor(Color.rgb(0,200,0));
        }else{
            boardButtons[location].setTextColor(Color.rgb(200,0,0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.new_game);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        turn++;
        startNewGame();
        return true;
    }

    //It handles clicks on the gameboard buttons
    private class ButtonClickListener implements View.OnClickListener{
        int location;
        public ButtonClickListener(int location){
            this.location = location;
        }

        public void onClick(View view){
            if(boardButtons[location].isEnabled()){
                setMove(TicTacToeGame.humanPlayer, location);
                //If there isn't winner yet, then it lets the computer make a move
                int winner = ticTacToeGame.checkForWinner();
                if(winner == TicTacToeGame.gameNotFinished){
                    infoGame.setText(R.string.turn_computer);
                    int move = ticTacToeGame.getComputerMove();
                    setMove(TicTacToeGame.computerPlayer, move);
                    winner = ticTacToeGame.checkForWinner();
                }

                if(winner == TicTacToeGame.gameNotFinished){
                    infoGame.setText(R.string.turn_human);
                }else if(winner == TicTacToeGame.gameTied){
                    infoGame.setText(R.string.result_tie);
                    for(int spot = 0; spot < boardButtons.length; spot++){
                        boardButtons[spot].setEnabled(false);
                    }
                    ties++;
                    infoTies.setText("Ties: " + ties);
                }else if(winner == TicTacToeGame.gameWithHumanWinner){
                    infoGame.setText(R.string.result_human_wins);
                    for(int spot = 0; spot < boardButtons.length; spot++){
                        boardButtons[spot].setEnabled(false);
                    }
                    humanWins++;
                    infoHumanWins.setText("Human Wins: " + humanWins);
                }else{
                    infoGame.setText(R.string.result_computer_wins);
                    for(int spot = 0; spot < boardButtons.length; spot++){
                        boardButtons[spot].setEnabled(false);
                    }
                    androidWins++;
                    infoAndroidWins.setText("Android Wins: " + androidWins);
                }
            }
        }
    }
}