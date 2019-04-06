package mamurciac.unal.edu.co.challenge5_tictactoe;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class BoardView extends View{
    private TicTacToeGame ticTacToeGame;

    //It is the board grid lines' width
    public static final int gridWidth = 8;

    private Bitmap humanFigure;
    private Bitmap computerFigure;
    private Paint boardPaint;

    public BoardView(Context context){
        super(context);
        initialize();
    }
    public BoardView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        initialize();
    }
    public BoardView(Context context, AttributeSet attributeSet, int definitedStyleAttributes){
        super(context, attributeSet, definitedStyleAttributes);
        initialize();
    }

    public void initialize(){
        humanFigure = BitmapFactory.decodeResource(getResources(), R.drawable.icon_player_x);
        computerFigure = BitmapFactory.decodeResource(getResources(), R.drawable.icon_player_o);
        boardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setGame(TicTacToeGame game){
        ticTacToeGame = game;
    }

    public int getBoardCellWidth(){
        return getWidth() / 3;
    }

    public int getBoardCellHeight(){
        return getHeight() / 3;
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //It determines the view's width and height
        int boardWidth = getWidth();
        int boardHeight = getHeight();

        //It makes thick and light gray lines
        boardPaint.setColor(Color.BLACK);
        boardPaint.setStrokeWidth(gridWidth);

        //It draws the two vertical board lines
        int cellWidth = boardWidth / 3;
        canvas.drawLine(cellWidth,0, cellWidth, boardHeight, boardPaint);
        canvas.drawLine(cellWidth * 2,0,cellWidth * 2, boardHeight, boardPaint);

        //It draws the two horizontal board lines
        int cellHeight = boardHeight / 3;
        canvas.drawLine(0, cellHeight, boardWidth, cellHeight, boardPaint);
        canvas.drawLine(0,cellHeight * 2, boardWidth,cellHeight * 2, boardPaint);

        //It draws all the X's and O's images
        for(int spot = 0; spot < TicTacToeGame.numberSpots; spot++){
            int column = spot % 3;
            int row = spot / 3;

            //It defines the boundaries of a destination rectangle for the image
            int left = column * cellWidth;
            int top = row * cellHeight;
            int right = left + cellWidth;
            int bottom = top + cellHeight;

            if(ticTacToeGame != null && ticTacToeGame.getBoardOccupant(spot) == TicTacToeGame.humanPlayer){
                canvas.drawBitmap(humanFigure,null, new Rect(left, top, right, bottom),null);
            }else if(ticTacToeGame != null && ticTacToeGame.getBoardOccupant(spot) == TicTacToeGame.computerPlayer){
                canvas.drawBitmap(computerFigure,null, new Rect(left, top, right, bottom),null);
            }
        }
    }
}