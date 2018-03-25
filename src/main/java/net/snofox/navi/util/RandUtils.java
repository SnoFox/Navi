package net.snofox.navi.util;

import java.util.Random;

public class RandUtils {

    private static Random random = null;

    public static Random getRandom() {
        if(random == null) initRandom();
        return random;
    }

    private static void initRandom() {
        random = new Random();
        for(int i = 0; i < 10; i++) random.nextLong();
        random.setSeed(random.nextLong() ^ random.nextLong());
        for(int i = 0; i < 100; i++) random.nextLong();
    }

}
