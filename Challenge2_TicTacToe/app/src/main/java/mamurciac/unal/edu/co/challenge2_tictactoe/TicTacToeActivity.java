package mamurciac.unal.edu.co.challenge2_tictactoe;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class TicTacToeActivity extends AppCompatActivity{
    //It represents the game's internal state
    private TicTacToeGame ticTacToeGame;
    //The buttons make up the board
    private Button boardButtons[];
    //Text displayed as game's information (Turn and winner's game)
    private TextView infoGame;

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
        infoGame.setText("¡¡You are first!!");
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
                    infoGame.setText("¡¡It's Android's turn!!");
                    int move = ticTacToeGame.getComputerMove();
                    setMove(TicTacToeGame.computerPlayer, move);
                    winner = ticTacToeGame.checkForWinner();
                }

                if(winner == TicTacToeGame.gameNotFinished){
                    infoGame.setText("¡¡It's your turn!!");
                }else if(winner == TicTacToeGame.gameTied){
                    infoGame.setText("¡¡It's a tie!!");
                }else if(winner == TicTacToeGame.gameWithHumanWinner){
                    infoGame.setText("¡¡You won!!");
                }else{
                    infoGame.setText("¡¡Android won!!");
                }
            }
        }
    }
}