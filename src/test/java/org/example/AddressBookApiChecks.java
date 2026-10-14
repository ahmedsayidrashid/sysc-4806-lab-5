package org.example;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

final class AddressBookApiChecks {

    private AddressBookApiChecks() { }

    static void createBookAndAddBuddies(RestTestClient client) {
        AtomicReference<Long> bookId = new AtomicReference<>();
        client.post().uri("/api/addressbook")
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressBook.class)
                .value(book -> {
                    assertThat(book.getId()).isNotNull();
                    bookId.set(book.getId());
                });

        Long ahmedId = createBuddy(client, "Ahmed", "613-555-0123", "1125 Colonel By Drive");
        Long babakId = createBuddy(client, "Babak", "613-555-0999", "123 Main Street");

        client.put().uri("/api/addressbook/{bookId}/buddy/{buddyId}", bookId.get(), ahmedId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressBook.class)
                .value(book -> assertThat(book.getBuddies())
                        .extracting(BuddyInfo::getAddress)
                        .contains("1125 Colonel By Drive"));

        client.put().uri("/api/addressbook/{bookId}/buddy/{buddyId}", bookId.get(), babakId)
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/addressbook/{id}", bookId.get())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertThat(html).contains("Ahmed");
                    assertThat(html).contains("613-555-0123");
                    assertThat(html).contains("1125 Colonel By Drive");
                    assertThat(html).contains("Babak");
                    assertThat(html).contains("123 Main Street");
                });
    }

    private static Long createBuddy(RestTestClient client, String name, String phone, String address) {
        AtomicReference<Long> id = new AtomicReference<>();
        client.post().uri("/api/buddy")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BuddyInfo(name, phone, address))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BuddyInfo.class)
                .value(buddy -> {
                    assertThat(buddy.getName()).isEqualTo(name);
                    assertThat(buddy.getPhoneNumber()).isEqualTo(phone);
                    assertThat(buddy.getAddress()).isEqualTo(address);
                    assertThat(buddy.getId()).isNotNull();
                    id.set(buddy.getId());
                });
        return id.get();
    }
}
