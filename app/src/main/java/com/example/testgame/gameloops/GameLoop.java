package com.example.testgame.gameloops;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

import com.example.testgame.datacontainers.CoinData;
import com.example.testgame.views.MainGameView;

import java.util.Random;

/**
 * Created by Chethan.n on 19/03/16.
 */
public class GameLoop extends Thread {
    private MainGameView gameView;
    private boolean isCharacterMoving = false;
    private boolean shouldMoveUp = false;
    private int cyclesTillNextCoin = 50;
    private int cyclesPassed = 0;
    private int coinPositionY;
    private int coinSpeed;
    private static final int minCycleBetweenCoins = 20;
    private static final int maxCycleBetweenCoins = 80;
    private static final int maxCoinSpeed = 40;
    private static final int minCoinSpeed = 5;
    public GameLoop(MainGameView mainGameView){
        this.gameView = mainGameView;
        coinPositionY = gameView.getScreenHeight()/2;
    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {
        super.run();
        long ticksPS = 30;
        long startTime = 0;
        long sleepTime;

        while (true){
            Canvas canvas = null;
            startTime = System.currentTimeMillis();
            updateCharacterPosition();
            moveBackground();
            updateCoins();
            try {
                canvas = gameView.getHolder().lockCanvas();
                synchronized (gameView.getHolder()) {
                    gameView.post(new Runnable() {
                        @Override
                        public void run() {
                            gameView.invalidate();
                        }
                    });
                }
            }
            finally {
                if (canvas != null) {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }

            //sleep
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0) {
                    sleep(sleepTime);
                }
                else {
                    sleep(0);
                }
            }
            catch (Exception e) {}
        }
    }

    private void updateCharacterPosition(){
        if(gameView.getCharacterPositionY() < 0){
            shouldMoveUp = false;
        } else if(gameView.getCharacterPositionY() > gameView.getScreenHeight() - gameView.getCharacterHeight()){
            gameView.setCharacterPositionY(gameView.getDefaultCharacterPositionY());
            isCharacterMoving = false;
        }

        if(isCharacterMoving){
            if(shouldMoveUp){
                gameView.setCharacterPositionY(gameView.getCharacterPositionY() - 20);
            } else {
                gameView.setCharacterPositionY(gameView.getCharacterPositionY() + 20);
            }
        }
    }

    private void moveBackground(){
        if(gameView.getBackgroundOffset() < - gameView.getScreenWidth()){
            gameView.setBackgroundOffset(0);
        }
        gameView.setBackgroundOffset(gameView.getBackgroundOffset() - 30);
    }

    private void updateCoins(){
        if(cyclesPassed < cyclesTillNextCoin){
            cyclesPassed ++;
            for(CoinData coindata : gameView.coinList){
                if(coindata.positionX < -1000){
                    continue;
                }
                coindata.positionX = coindata.positionX - coindata.speed;
            }
            return;
        }
        cyclesPassed = 0;
        cyclesTillNextCoin = new Random().nextInt((maxCycleBetweenCoins - minCycleBetweenCoins) + 1) + minCycleBetweenCoins;
        coinSpeed = new Random().nextInt((maxCoinSpeed - minCoinSpeed) + 1) + minCoinSpeed;
        coinPositionY = new Random().nextInt(gameView.getScreenHeight()) - gameView.getCoinWidth();
        addCoin();
    }

    public synchronized void addCoin() {
        gameView.addCoin(new CoinData(gameView.getScreenWidth(), coinPositionY, coinSpeed));
    }

    public void moveUp(){
        shouldMoveUp = true;
    }

    public void stopMoveUp(){
        shouldMoveUp = false;
    }

    public void moveCharacter(){
        isCharacterMoving = true;
    }

    public boolean isCharacterMoving(){
        return isCharacterMoving;
    }
}
