package com.example.testgame.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.testgame.R;
import com.example.testgame.datacontainers.CoinData;
import com.example.testgame.gameloops.GameLoop;
import com.example.testgame.helpers.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chethan.n on 19/03/16.
 */
public class MainGameView extends SurfaceView {
    private GameLoop gameLoop;
    private Bitmap background;
    private Bitmap character;
    private Bitmap coin;
    private SurfaceHolder surfaceHolder;
    private int screenWidth, screenHeight;
    private int backgroundOffset = 0;
    private Context context;
    private int characterPositionX, characterPositionY;
    private boolean isCharacterMoving = false;
    private Paint scorePaint;
    MediaPlayer backgroundMusic, jumpMusic ,coinMusic;
    private int dragCoinX,dragCoinY;
    int score = 0;
    private boolean isDragging;
    public List<CoinData> coinList = new ArrayList<>();
    public MainGameView(Context context) {
        super(context);
        this.context = context;
        surfaceHolder = getHolder();
        initScreen();
        initGameLoops();
        addSurfaceCallbacks();
        scorePaint = new Paint();
        scorePaint.setColor(Color.GRAY);
        scorePaint.setStrokeWidth(100);
        setWillNotDraw(false);
        initMusic();
    }

    private void initGameLoops(){
        gameLoop = new GameLoop(this);
    }

    private void initMusic(){
        backgroundMusic = MediaPlayer.create(context, R.raw.game);
        jumpMusic = MediaPlayer.create(context, R.raw.jump);
        coinMusic = MediaPlayer.create(context, R.raw.cointake);
        backgroundMusic.start();
        backgroundMusic.setLooping(true);
    }

    private void initScreen(){
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, 2 * screenWidth, screenHeight, true);
        character = BitmapFactory.decodeResource(getResources(), R.drawable.character);
        character = Bitmap.createScaledBitmap(character, screenWidth / 7, screenWidth / 7, true);
        coin = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
        coin = Bitmap.createScaledBitmap(coin, screenWidth/14, screenWidth/14, true);
        initPositions();
    }

    private void initPositions(){
        characterPositionX = screenWidth/10;
        characterPositionY = screenHeight - character.getHeight();
        dragCoinX = screenWidth - 200;
        dragCoinY = screenHeight - 200;
    }

    private void addSurfaceCallbacks(){
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (!gameLoop.isAlive()) {
                    gameLoop.start();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX(), y = (int) event.getY();
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(!isCharacterMoving){
                gameLoop.moveUp();
                gameLoop.moveCharacter();
                jumpMusic.start();
            }
            isDragging = Utils.hasCollided(x,y,x,y,dragCoinX,dragCoinY,dragCoinX+coin.getWidth(),dragCoinY+coin.getHeight());
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            gameLoop.stopMoveUp();
            isDragging = false;
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(isDragging) {
                setDrag(x, y);
            }
        }
        return true;
    }

    private void setDrag(int x, int y){
        dragCoinY = y;
        dragCoinX = x;
    }

    @SuppressLint("WrongCall")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawCoins(canvas);
        drawCharacter(canvas);
        displayScore(canvas);
        displayDragCoin(canvas);
    }

    private void displayDragCoin(Canvas canvas){
        canvas.drawBitmap(coin,dragCoinX,dragCoinY,null);
    }

    private void drawBackground(Canvas canvas){
        canvas.drawBitmap(background, backgroundOffset, 0, null);
    }

    private void drawCharacter(Canvas canvas){
        canvas.drawBitmap(character, characterPositionX, characterPositionY, null);
    }

    private void displayScore(Canvas canvas){
        canvas.drawText("Score " + score, screenWidth/20, screenHeight/20, scorePaint);
    }

    private void drawCoins(Canvas canvas){
        int characterMinX = characterPositionX;
        int characterMinY = characterPositionY;
        int characterMaxX = characterPositionX + character.getWidth();
        int characterMaxY = characterPositionY + character.getHeight();
        for(CoinData coinData : coinList){
            int coinMinX = coinData.positionX;
            int coinMinY = coinData.positionY;
            int coinMaxX = coinData.positionX + coin.getWidth();
            int coinMaxY = coinData.positionY + coin.getHeight();
            if(Utils.hasCollided(characterMinX, characterMinY, characterMaxX, characterMaxY,
                    coinMinX, coinMinY, coinMaxX, coinMaxY)){
                coinData.positionX = -1000;
                coinMusic.start();
                score++;
                continue;
            }
            canvas.drawBitmap(coin, coinData.positionX, coinData.positionY, null);
        }
    }

    public int getCharacterPositionX(){
        return this.characterPositionX;
    }

    public void setCharacterPositionX(int positionX){
        this.characterPositionX = positionX;
    }

    public int getCharacterPositionY(){
        return this.characterPositionY;
    }

    public void setCharacterPositionY(int positionY){
        this.characterPositionY = positionY;
    }

    public int getDefaultCharacterPositionX(){
        return screenWidth/10;
    }

    public int getDefaultCharacterPositionY(){
        return screenHeight - character.getHeight();
    }

    public int getBackgroundOffset(){
        return backgroundOffset;
    }

    public void setBackgroundOffset(int backgroundOffset){
        this.backgroundOffset = backgroundOffset;
    }

    public int getScreenWidth(){
        return this.screenWidth;
    }

    public int getScreenHeight(){
        return this.screenHeight;
    }

    public void addCoin(CoinData coinData){
        coinList.add(coinData);
    }

    public synchronized void removeCoin(CoinData coinData){
        coinList.remove(coinData);
    }

    public int getCoinWidth(){
        return coin.getWidth();
    }

    public int getCharacterHeight(){
        return character.getHeight();
    }
}
