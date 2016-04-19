package com.example.testgame.helpers;

/**
 * Created by Chethan.n on 20/03/16.
 */
public class Utils {
    public static boolean hasCollided(int aMinX, int aMinY, int aMaxX, int aMaxY,
                                      int bMinX, int bMinY, int bMaxX, int bMaxY){
        if ((aMaxX < bMinX) || // A is to the left of B
                (bMaxX < aMinX) || // B is to the left of A
                (aMaxY < bMinY) || // A is above B
                (bMaxY < aMinY))   // B is above A
        {
            return false; // A and B don't intersect
        }
        return true;
    }
}
