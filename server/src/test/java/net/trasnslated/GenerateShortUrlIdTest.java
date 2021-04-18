package net.trasnslated;

import net.traslated.util.GenerateShortUrlId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;


public class GenerateShortUrlIdTest {

    @Test
    void getShortUrlsId() {
         Optional<String> shortUrlsId = GenerateShortUrlId.INSTANCE.getShortUrlsId(1L);
        Assertions.assertTrue(shortUrlsId.isPresent());
        Assertions.assertEquals(shortUrlsId.get(), "baaaab");

        shortUrlsId = GenerateShortUrlId.INSTANCE.getShortUrlsId(2176782535L);
        Assertions.assertTrue(shortUrlsId.isEmpty());
    }
}