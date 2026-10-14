package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BuddyInfoTest {
    @Test
    void testGetters() {
        BuddyInfo buddy = new BuddyInfo("Jerry", "613-555-0100", "123 Main Street");
        assertEquals("Jerry", buddy.getName());
        assertEquals("613-555-0100", buddy.getPhoneNumber());
        assertEquals("123 Main Street", buddy.getAddress());
    }
}