package mamurciac.unal.edu.co.challenge4_tictactoe;

import android.app.Dialog;
import android.content.*;
import android.graphics.Color;
import android.support.v7.app.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.lang.reflect.*;

public class TicTacToeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    //It represents the game's internal state
    private TicTacToeGame ticTacToeGame;
    //The buttons make up the board
    private Button boardButtons[];
    //Text displayed as game's information (Turn and winner's game)
    private TextView infoGame, infoHumanWins, infoAndroidWins, infoTies;

    private int turn = 1, humanWins = 0, androidWins = 0, ties = 0;

    //Menu options
    static final int dialogDifficultySuccessId = 0, dialogDifficultyFailureId = 1, dialogAboutGameId = 2, dialogQuitId = 3;
    private static String popupConstant = "mPopup", popupForceShowIcon = "setForceShowIcon";

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id){
            case dialogDifficultySuccessId:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {getResources().getString(R.string.difficulty_easy), getResources().getString(R.string.difficulty_medium), getResources().getString(R.string.difficulty_hard)};
                builder.setSingleChoiceItems(levels, ticTacToeGame.getDifficultyLevel().ordinal(), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        dialog.dismiss();
                        ticTacToeGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.valueOf(String.valueOf(levels[item])));
                        Toast.makeText(getApplicationContext(),"Game's Difficulty: " + levels[item], Toast.LENGTH_SHORT).show();
                    }
                });
                dialog = builder.create();
                break;
            case dialogDifficultyFailureId:
                builder.setMessage(R.string.difficulty_not_changeable).setCancelable(true).setPositiveButton(R.string.ok,null);
                dialog = builder.create();
                break;
            case dialogAboutGameId:
                //builder = new AlertDialog.Builder(this);
                //Context context = getApplicationContext();
                //LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                //View layout = inflater.inflate(R.layout.about_dialog, null);
                //builder.setView(layout);
                //builder.setPositiveButton("OK", null);
                //dialog = builder.create();
                dialog = new Dialog(TicTacToeActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.about_dialog);
                Button okButton = dialog.findViewById(R.id.ok_button);
                okButton.setEnabled(true);
                final Dialog finalDialog = dialog;
                okButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        finalDialog.cancel();
                    }
                });
                break;
            case dialogQuitId:
                builder.setMessage(R.string.quit_question).setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        TicTacToeActivity.this.finish();
                    }
                }).setNegativeButton(R.string.no,null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch(item.getItemId()){
            case R.id.new_game:
                turn++;
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                if(ticTacToeGame.checkWhetherHumanPlayed() == false){
                    showDialog(dialogDifficultySuccessId);
                }else{
                    showDialog(dialogDifficultyFailureId);
                }
                return true;
            case R.id.about:
                showDialog(dialogAboutGameId);
                return true;
            case R.id.quit:
                showDialog(dialogQuitId);
                return true;
        }
        return false;
    }

    public boolean onMenuItemSelect(MenuItem item){
        showPopup(findViewById(item.getItemId()));
        return true;
    }

    public void showPopup(View view){
        PopupMenu popup = new PopupMenu(TicTacToeActivity.this, view);
        try{
            //It forces to show the menu items' icons
            Field[] fields = popup.getClass().getDeclaredFields();
            for(Field field : fields){
                if(field.getName().equals(popupConstant)){
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(popupForceShowIcon, boolean.class);
                    setForceIcons.invoke(menuPopupHelper,true);
                    break;
                }
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }

        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
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