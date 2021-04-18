package net.traslated.util;

import java.util.Optional;

public enum GenerateShortUrlId {
     INSTANCE;

    private static final long GREATEST_BASE36_VALUE = 2176782335L;
    private static final long SMALLEST_BASE36_VALUE_WITH_6_DIGITS = 60466176;
    private final char[] map = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    /**
     * Covert an number in a 6 digit base 36 representation.
     * @param lastCounter
     * @return 6 digit representation
     */
    public Optional<String> getShortUrlsId(Long lastCounter) {
        long shortUrlId = SMALLEST_BASE36_VALUE_WITH_6_DIGITS + lastCounter;
        if(shortUrlId > GREATEST_BASE36_VALUE) {
            return Optional.empty();
        }
        StringBuilder shortUrl = new StringBuilder();
        while (shortUrlId > 0) {
            int index = (int) (shortUrlId % map.length);
            shortUrl.append(map[index]);
            shortUrlId = shortUrlId / map.length;
        }

        return Optional.of(shortUrl.toString());
    }


}
