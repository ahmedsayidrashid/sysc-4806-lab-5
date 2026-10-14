package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    // Read all AddressBooks
    @GetMapping("/addressbook")
    public Iterable<AddressBook> getAddressBooks() {
        return addressBookRepo.findAll();
    }

    // Read a specific AddressBook
    @GetMapping("/addressbook/{id}")
    public AddressBook getAddressBook(@PathVariable Long id) {
        return findBook(id);
    }

    // Delete an AddressBook (and its buddies)
    @DeleteMapping("/addressbook/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddressBook(@PathVariable Long id) {
        addressBookRepo.delete(findBook(id));
    }

    // Create a BuddyInfo
    @PostMapping("/buddy")
    public BuddyInfo createBuddy(@RequestBody BuddyInfo buddy) {
        return buddyInfoRepo.save(buddy);
    }

    // Read a specific BuddyInfo
    @GetMapping("/buddy/{id}")
    public BuddyInfo getBuddy(@PathVariable Long id) {
        return findBuddy(id);
    }

    // Update a BuddyInfo
    @PutMapping("/buddy/{id}")
    public BuddyInfo updateBuddy(@PathVariable Long id, @RequestBody BuddyInfo updated) {
        BuddyInfo buddy = findBuddy(id);
        buddy.setName(updated.getName());
        buddy.setPhoneNumber(updated.getPhoneNumber());
        buddy.setAddress(updated.getAddress());
        return buddyInfoRepo.save(buddy);
    }

    // Delete a BuddyInfo, removing it from any AddressBook first
    @DeleteMapping("/buddy/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBuddy(@PathVariable Long id) {
        BuddyInfo buddy = findBuddy(id);
        for (AddressBook book : addressBookRepo.findAll()) {
            if (book.getBuddies().removeIf(b -> b.getId().equals(id))) {
                addressBookRepo.save(book);
            }
        }
        buddyInfoRepo.delete(buddy);
    }

    // Add a specific Buddy to a specific AddressBook
    @PutMapping("/addressbook/{bookId}/buddy/{buddyId}")
    public AddressBook addBuddyToAddressBook(@PathVariable Long bookId, @PathVariable Long buddyId) {
        AddressBook book = findBook(bookId);
        BuddyInfo buddy = findBuddy(buddyId);

        book.addBuddy(buddy);
        return addressBookRepo.save(book);
    }

    // Remove a specific Buddy from a specific AddressBook
    @DeleteMapping("/addressbook/{bookId}/buddy/{buddyId}")
    public AddressBook removeBuddyFromAddressBook(@PathVariable Long bookId, @PathVariable Long buddyId) {
        AddressBook book = findBook(bookId);
        if (!book.getBuddies().removeIf(b -> b.getId().equals(buddyId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Buddy " + buddyId + " not in AddressBook " + bookId);
        }
        return addressBookRepo.save(book);
    }

    private AddressBook findBook(Long id) {
        return addressBookRepo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AddressBook " + id + " not found"));
    }

    private BuddyInfo findBuddy(Long id) {
        return buddyInfoRepo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BuddyInfo " + id + " not found"));
    }
}
