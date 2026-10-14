package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AddressBookViewController {

    @Autowired
    private AddressBookRepository addressBookRepo;

    // Return a rudimentary GUI view using Thymeleaf
    @GetMapping("/addressbook/{id}")
    public String viewAddressBook(@PathVariable Long id, Model model) {
        AddressBook book = addressBookRepo.findById(id).orElseThrow();
        model.addAttribute("addressBook", book);
        return "addressbook";
    }
}