package github.muhsenerdev.commons.core.util;

import java.util.Random;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomUtil {
    private static final Random random = new Random();

    public static String generateRandomCode(int length) {
        return String.format("%0" + length + "d", random.nextInt((int) Math.pow(10, length)));
    }

    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

}
