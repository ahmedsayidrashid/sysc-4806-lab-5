package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lab3Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }

    @Bean
    public CommandLineRunner demo(AddressBookRepository repository) {
        return (args) -> {
            BuddyInfo buddy1 = new BuddyInfo("Tom", "613-555-0199", "1125 Colonel By Drive");
            BuddyInfo buddy2 = new BuddyInfo("Jerry", "613-555-0100", "123 Main Street");

            AddressBook addressBook = new AddressBook();
            addressBook.addBuddy(buddy1);
            addressBook.addBuddy(buddy2);

            // Using the repository interface instead of EntityManager
            repository.save(addressBook);

            System.out.println("Saved AddressBook with ID: " + addressBook.getId());

            System.out.println("Retrieving all AddressBooks:");
            for (AddressBook book : repository.findAll()) {
                System.out.println("AddressBook ID " + book.getId() + " contains " + book.getBuddies().size() + " buddies.");
            }
        };
    }
}