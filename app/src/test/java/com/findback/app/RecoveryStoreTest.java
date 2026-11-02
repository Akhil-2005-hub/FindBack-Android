package com.findback.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** JVM unit tests for recovery-code validation and WhatsApp command parsing. */
public class RecoveryStoreTest {
    @Test public void acceptsExactlyEightDigits() {
        assertTrue(RecoveryStore.validCode("91740628"));
    }

    @Test public void rejectsCodesWithWrongLengthOrCharacters() {
        assertFalse(RecoveryStore.validCode("1234567"));
        assertFalse(RecoveryStore.validCode("917406280"));
        assertFalse(RecoveryStore.validCode("1234AB78"));
        assertFalse(RecoveryStore.validCode(null));
    }

    @Test public void extractsUppercaseCommand() {
        assertEquals("91740628", RecoveryStore.extract("FIND 91740628"));
    }

    @Test public void extractsCommandWithoutCaseSensitivity() {
        assertEquals("91740628", RecoveryStore.extract("find 91740628"));
    }

    @Test public void rejectsTextWithoutCommand() {
        assertNull(RecoveryStore.extract("Please locate my phone 91740628"));
    }

    @Test public void rejectsCommandWithWrongDigitCount() {
        assertNull(RecoveryStore.extract("FIND 1234567"));
        assertNull(RecoveryStore.extract("FIND 917406280"));
    }
}
