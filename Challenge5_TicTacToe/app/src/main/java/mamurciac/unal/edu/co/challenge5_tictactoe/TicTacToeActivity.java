package mamurciac.unal.edu.co.challenge5_tictactoe;

import android.app.Dialog;
import android.content.*;
import android.media.MediaPlayer;
import android.support.v7.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.lang.reflect.*;

public class TicTacToeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    //It represents the game's internal state
    private TicTacToeGame ticTacToeGame;
    private BoardView boardView;
    private MediaPlayer humanGambleMediaPlayer, computerGambleMediaPlayer;

    //Text displayed as game's information (Turn and winner's game)
    private TextView infoGame, infoHumanWins, infoAndroidWins, infoTies;

    private char playerTurn;
    private int turn = 1, humanWins = 0, androidWins = 0, ties = 0;
    private boolean gameOver;

    //Menu options
    static final int dialogDifficultySuccessId = 0, dialogDifficultyFailureId = 1, dialogAboutGameId = 2, dialogQuitId = 3;
    private static String popupConstant = "mPopup", popupForceShowIcon = "setForceShowIcon";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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
        startNewGame();
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
        infoHumanWins.setText("Human Wins: " + humanWins);
        infoAndroidWins.setText("Android Wins: " + androidWins);
        infoTies.setText("Ties: " + ties);
    }

    private boolean setMove(char player, int location){
        if(player == TicTacToeGame.humanPlayer){
            humanGambleMediaPlayer.start();
        }else{
            computerGambleMediaPlayer.start();
        }

        if(ticTacToeGame.setMove(player, location) == true){
            boardView.invalidate(); //It redraws the board
            return true;
        }
        return false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        humanGambleMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.x_sound);
        computerGambleMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.o_sound);
    }

    @Override
    protected void onPause(){
        super.onPause();
        humanGambleMediaPlayer.release();
        computerGambleMediaPlayer.release();
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
                            infoGame.setText(R.string.turn_computer);
                            playerTurn = TicTacToeGame.computerPlayer;

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable(){
                                @Override
                                public void run(){
                                    int move = ticTacToeGame.getComputerMove();
                                    setMove(TicTacToeGame.computerPlayer, move);
                                    int winner = ticTacToeGame.checkForWinner();

                                    if(winner == TicTacToeGame.gameNotFinished){
                                        infoGame.setText(R.string.turn_human);
                                        playerTurn = TicTacToeGame.humanPlayer;
                                    }else if(winner == TicTacToeGame.gameTied){
                                        infoGame.setText(R.string.result_tie);
                                        gameOver = true;
                                        ties++;
                                        infoTies.setText("Ties: " + ties);
                                    }else if(winner == TicTacToeGame.gameWithHumanWinner){
                                        infoGame.setText(R.string.result_human_wins);
                                        gameOver = true;
                                        humanWins++;
                                        infoHumanWins.setText("Human Wins: " + humanWins);
                                    }else{
                                        infoGame.setText(R.string.result_computer_wins);
                                        gameOver = true;
                                        androidWins++;
                                        infoAndroidWins.setText("Android Wins: " + androidWins);
                                    }
                                }
                            },1750);
                        }else if(winner == TicTacToeGame.gameTied){
                            infoGame.setText(R.string.result_tie);
                            gameOver = true;
                            ties++;
                            infoTies.setText("Ties: " + ties);
                        }else if(winner == TicTacToeGame.gameWithHumanWinner){
                            infoGame.setText(R.string.result_human_wins);
                            gameOver = true;
                            humanWins++;
                            infoHumanWins.setText("Human Wins: " + humanWins);
                        }else{
                            infoGame.setText(R.string.result_computer_wins);
                            gameOver = true;
                            androidWins++;
                            infoAndroidWins.setText("Android Wins: " + androidWins);
                        }
                        return true;
                    }
                }
            }
            //So we aren't notified of continued events when finger is moved
            return false;
        }
    };
}