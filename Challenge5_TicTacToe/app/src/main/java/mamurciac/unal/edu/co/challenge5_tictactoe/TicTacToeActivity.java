package mamurciac.unal.edu.co.challenge5_tictactoe;

import android.app.Dialog;
import android.content.*;
import android.media.MediaPlayer;
import android.support.v7.app.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.lang.reflect.*;

public class TicTacToeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    //It represents the game's internal state
    private TicTacToeGame ticTacToeGame;

    private SharedPreferences preferences;

    private BoardView boardView;
    private MediaPlayer humanGambleMediaPlayer, computerGambleMediaPlayer;
    Handler handler = new Handler();
    Runnable runnable;

    //Text displayed as game's information (Turn and winner's game)
    private TextView infoGame, infoHumanWins, infoAndroidWins, infoTies;

    static final int difficultyEasy = 0, difficultyMedium = 1, difficultyHard = 2;

    private char playerTurn;
    private int turn = 1, humanWins = 0, androidWins = 0, ties = 0, difficultyLevel = difficultyHard;
    private boolean gameOver;

    //Menu options
    static final int dialogDifficultySuccessId = 0, dialogDifficultyFailureId = 1, dialogRestoreScoreId = 2, dialogAboutGameId = 3, dialogQuitId = 4;
    private static String popupConstant = "mPopup", popupForceShowIcon = "setForceShowIcon";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //It restores the scores
        preferences = getSharedPreferences("tictactoe_preferences", MODE_PRIVATE);
        humanWins = preferences.getInt("humanWins",0);
        androidWins = preferences.getInt("androidWins",0);
        ties = preferences.getInt("ties",0);
        difficultyLevel = preferences.getInt("difficultyLevel", difficultyHard);

        setContentView(R.layout.activity_tic_tac_toe);
        humanGambleMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.x_sound);
        computerGambleMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.o_sound);

        infoGame = findViewById(R.id.game_information);
        infoHumanWins = findViewById(R.id.human_wins);
        infoAndroidWins = findViewById(R.id.android_wins);
        infoTies = findViewById(R.id.number_ties);
        ticTacToeGame = new TicTacToeGame();

        gameOver = false;
        boardView = findViewById(R.id.game_board);
        boardView.setGame(ticTacToeGame);
        //It listens for touches on the board
        boardView.setOnTouchListener(touchListener);

        if(savedInstanceState == null){
            startNewGame();
        }else{
            //It restores the game's state
            ticTacToeGame.setBoardState(savedInstanceState.getCharArray("boardGame"));
            gameOver = savedInstanceState.getBoolean("gameOver");
            infoGame.setText(savedInstanceState.getCharSequence("infoGame"));
            humanWins = savedInstanceState.getInt("humanWins");
            androidWins = savedInstanceState.getInt("androidWins");
            ties = savedInstanceState.getInt("ties");
            difficultyLevel = savedInstanceState.getInt("difficultyLevel");
            playerTurn = savedInstanceState.getChar("playerTurn");

            if(playerTurn == TicTacToeGame.computerPlayer && gameOver == false){
                performComputerGamble(true);
            }
        }
        ticTacToeGame.setIdDifficultyLevel(difficultyLevel);
        displayScores();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        ticTacToeGame.setBoardState(savedInstanceState.getCharArray("boardGame"));
        gameOver = savedInstanceState.getBoolean("gameOver");
        infoGame.setText(savedInstanceState.getCharSequence("infoGame"));
        humanWins = savedInstanceState.getInt("humanWins");
        androidWins = savedInstanceState.getInt("androidWins");
        ties = savedInstanceState.getInt("ties");
        difficultyLevel = savedInstanceState.getInt("difficultyLevel");
        playerTurn = savedInstanceState.getChar("playerTurn");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putCharArray("boardGame", ticTacToeGame.getBoardState());
        outState.putBoolean("gameOver", gameOver);
        outState.putInt("humanWins", Integer.valueOf(humanWins));
        outState.putInt("androidWins", Integer.valueOf(androidWins));
        outState.putInt("ties", Integer.valueOf(ties));
        outState.putCharSequence("infoGame", infoGame.getText());
        outState.putInt("difficultyLevel", Integer.valueOf(difficultyLevel));
        outState.putChar("playerTurn", playerTurn);
    }

    private void startNewGame(){
        ticTacToeGame.clearBoard();
        boardView.invalidate(); //It redraws the board
        gameOver = false;

        //According to the turn, human or computer player goes first
        if(turn % 2 == 1){
            infoGame.setText(R.string.first_turn_human);
        }else{
            playerTurn = TicTacToeGame.computerPlayer;
            infoGame.setText(R.string.first_turn_computer);
            int move = ticTacToeGame.getComputerMove();
            ticTacToeGame.setMove(TicTacToeGame.computerPlayer, move);
            infoGame.setText(R.string.turn_human);
        }
        playerTurn = TicTacToeGame.humanPlayer;
        displayScores();
    }

    //It updates the game's scores
    private void displayScores(){
        infoHumanWins.setText("Human Wins: " + Integer.toString(humanWins));
        infoAndroidWins.setText("Android Wins: " + Integer.toString(androidWins));
        infoTies.setText("Ties: " + Integer.toString(ties));
    }

    private void performComputerGamble(final boolean isGambleMadeByComputer){
        infoGame.setText(R.string.turn_computer);
        playerTurn = TicTacToeGame.computerPlayer;

        runnable = new Runnable(){
            public void run(){
                if(isGambleMadeByComputer == false){
                    int move = ticTacToeGame.getComputerMove();
                    setMove(TicTacToeGame.computerPlayer, move);
                }

                int winner = ticTacToeGame.checkForWinner();
                if(winner == TicTacToeGame.gameNotFinished){
                    infoGame.setText(R.string.turn_human);
                    playerTurn = TicTacToeGame.humanPlayer;
                }else if(winner == TicTacToeGame.gameTied){
                    infoGame.setText(R.string.result_tie);
                    gameOver = true;
                    ties++;
                }else if(winner == TicTacToeGame.gameWithHumanWinner){
                    infoGame.setText(R.string.result_human_wins);
                    gameOver = true;
                    humanWins++;
                }else{
                    infoGame.setText(R.string.result_computer_wins);
                    gameOver = true;
                    androidWins++;
                }
                displayScores();
                boardView.invalidate(); //It redraws the board

                try{
                    computerGambleMediaPlayer.start();
                }catch(Exception exception){
                    Log.v("Admin", exception.toString());
                }
            }
        };
        handler.postDelayed(runnable,1750);
    }

    private boolean setMove(char player, int location){
        if(ticTacToeGame.setMove(player, location) == true){
            if(player == TicTacToeGame.humanPlayer){
                humanGambleMediaPlayer.start();
            }
            boardView.invalidate(); //It redraws the board
            return true;
        }
        return false;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        humanGambleMediaPlayer.release();
        computerGambleMediaPlayer.release();
    }

    @Override
    protected void onStop(){
        super.onStop();
        //It saves the current scores
        SharedPreferences.Editor editorPreferences = preferences.edit();
        editorPreferences.putInt("humanWins", humanWins);
        editorPreferences.putInt("androidWins", androidWins);
        editorPreferences.putInt("ties", ties);
        editorPreferences.putInt("difficultyLevel", difficultyLevel);
        editorPreferences.commit();
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
                        ticTacToeGame.setIdDifficultyLevel(item);

                        //It stores difficulty level's changes
                        SharedPreferences.Editor preferencesEditor = preferences.edit();
                        preferencesEditor.putInt("difficultyLevel", item);
                        preferencesEditor.apply();

                        difficultyLevel = item;
                        Toast.makeText(getApplicationContext(),"Game's Difficulty: " + levels[item], Toast.LENGTH_SHORT).show();
                    }
                });
                dialog = builder.create();
                break;
            case dialogDifficultyFailureId:
                builder.setMessage(R.string.difficulty_not_changeable).setCancelable(true).setPositiveButton(R.string.ok,null);
                dialog = builder.create();
                break;
            case dialogRestoreScoreId:
                builder.setMessage(R.string.restore_score_question).setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //It saves the new initial scores
                        humanWins = 0;
                        androidWins = 0;
                        ties = 0;
                        SharedPreferences.Editor editorPreferences = preferences.edit();
                        editorPreferences.putInt("humanWins", humanWins);
                        editorPreferences.putInt("androidWins", androidWins);
                        editorPreferences.putInt("ties", ties);
                        editorPreferences.commit();
                        displayScores();
                    }
                }).setNegativeButton(R.string.no,null);
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
            case R.id.restore_score:
                showDialog(dialogRestoreScoreId);
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

    //It listens for touches on the board
    private View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event){
            if(playerTurn == TicTacToeGame.humanPlayer){
                //It determines which cell was touched
                int column = (int) event.getX() / boardView.getBoardCellWidth();
                int row = (int) event.getY() / boardView.getBoardCellHeight();
                int spot = row * 3 + column;

                if(ticTacToeGame.getBoardOccupant(spot) != TicTacToeGame.humanPlayer && ticTacToeGame.getBoardOccupant(spot) != TicTacToeGame.computerPlayer){
                    if(gameOver == false && setMove(TicTacToeGame.humanPlayer, spot) == true){
                        //If there isn't winner yet, then it lets the computer make a move
                        int winner = ticTacToeGame.checkForWinner();
                        if(winner == TicTacToeGame.gameNotFinished){
                            performComputerGamble(false);
                        }else if(winner == TicTacToeGame.gameTied){
                            infoGame.setText(R.string.result_tie);
                            gameOver = true;
                            ties++;
                        }else if(winner == TicTacToeGame.gameWithHumanWinner){
                            infoGame.setText(R.string.result_human_wins);
                            gameOver = true;
                            humanWins++;
                        }else{
                            infoGame.setText(R.string.result_computer_wins);
                            gameOver = true;
                            androidWins++;
                        }
                        displayScores();
                        return true;
                    }
                }
            }
            //So we aren't notified of continued events when finger is moved
            return false;
        }
    };
}