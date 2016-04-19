package com.example.testgame.datacontainers;

import java.io.Serializable;

/**
 * Created by Chethan.n on 19/03/16.
 */
public class CoinData implements Serializable {
    public CoinData(int positionX, int positionY, int speed){
        this.positionX = positionX;
        this.positionY = positionY;
        this.speed = speed;
    }
    public int positionX;
    public int positionY;
    public int speed;
}
