package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AddressBookRestController {

    @Autowired
    private AddressBookRepository addressBookRepo;

    @Autowired
    private BuddyInfoRepository buddyInfoRepo;

    // Create an AddressBook
    @PostMapping("/addressbook")
    public AddressBook createAddressBook() {
        return addressBookRepo.save(new AddressBook());
    }

    // Create a BuddyInfo
    @PostMapping("/buddy")
    public BuddyInfo createBuddy(@RequestBody BuddyInfo buddy) {
        return buddyInfoRepo.save(buddy);
    }

    // Add a specific Buddy to a specific AddressBook
    @PutMapping("/addressbook/{bookId}/buddy/{buddyId}")
    public AddressBook addBuddyToAddressBook(@PathVariable Long bookId, @PathVariable Long buddyId) {
        AddressBook book = addressBookRepo.findById(bookId).orElseThrow();
        BuddyInfo buddy = buddyInfoRepo.findById(buddyId).orElseThrow();

        book.addBuddy(buddy);
        return addressBookRepo.save(book);
    }
}