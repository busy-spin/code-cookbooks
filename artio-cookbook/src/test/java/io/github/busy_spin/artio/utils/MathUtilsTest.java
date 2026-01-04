package io.github.busy_spin.artio.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathUtilsTest {

    @Test
    void toLong() {
        long value = MathUtils.toLong("1767543699030".toCharArray());
    }
}