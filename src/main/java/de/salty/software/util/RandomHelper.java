package de.salty.software.util;

import javax.inject.Named;
import java.util.Random;

@Named
public class RandomHelper {
    private static Random random = new Random();

    public static int getInteger(int min, int max){
        return random.nextInt(max - min + 1) + min;
    }

    public static double getDouble(double min, double max){
        return ((max - min) * random.nextDouble()) + min;
    }

    public static float getFloat(float min, float max){
        return ((max - min) * random.nextFloat()) + min;
    }

    public static boolean hitPercentChance(double chance){
        return getDouble(0.,100.) <= chance;
    }
}

