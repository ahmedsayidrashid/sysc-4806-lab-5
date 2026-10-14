package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AddressBookTest {
    @Test
    void testAddBuddy() {
        AddressBook book = new AddressBook();
        BuddyInfo buddy = new BuddyInfo("Jerry", "613-555-0100");
        assertDoesNotThrow(() -> book.addBuddy(buddy));
    }
}