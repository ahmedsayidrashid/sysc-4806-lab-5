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

    static void crudOperations(RestTestClient client) {
        AtomicReference<Long> bookId = new AtomicReference<>();
        client.post().uri("/api/addressbook")
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressBook.class)
                .value(book -> bookId.set(book.getId()));

        Long keepId = createBuddy(client, "Keep", "613-555-1111", "1 Keep Street");
        Long removeId = createBuddy(client, "Remove", "613-555-2222", "2 Remove Street");
        client.put().uri("/api/addressbook/{bookId}/buddy/{buddyId}", bookId.get(), keepId)
                .exchange().expectStatus().isOk();
        client.put().uri("/api/addressbook/{bookId}/buddy/{buddyId}", bookId.get(), removeId)
                .exchange().expectStatus().isOk();

        client.get().uri("/api/addressbook/{id}", bookId.get())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressBook.class)
                .value(book -> assertThat(book.getBuddies())
                        .extracting(BuddyInfo::getId)
                        .containsExactlyInAnyOrder(keepId, removeId));

        client.delete().uri("/api/addressbook/{bookId}/buddy/{buddyId}", bookId.get(), removeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressBook.class)
                .value(book -> assertThat(book.getBuddies())
                        .extracting(BuddyInfo::getId)
                        .containsExactly(keepId));

        client.delete().uri("/api/addressbook/{bookId}/buddy/{buddyId}", bookId.get(), removeId)
                .exchange()
                .expectStatus().isNotFound();

        client.put().uri("/api/buddy/{id}", keepId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BuddyInfo("Kept", "613-555-3333", "3 New Street"))
                .exchange()
                .expectStatus().isOk();

        client.get().uri("/api/buddy/{id}", keepId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BuddyInfo.class)
                .value(buddy -> {
                    assertThat(buddy.getName()).isEqualTo("Kept");
                    assertThat(buddy.getPhoneNumber()).isEqualTo("613-555-3333");
                    assertThat(buddy.getAddress()).isEqualTo("3 New Street");
                });

        client.delete().uri("/api/buddy/{id}", keepId)
                .exchange()
                .expectStatus().isNoContent();

        client.get().uri("/api/buddy/{id}", keepId)
                .exchange()
                .expectStatus().isNotFound();

        client.get().uri("/api/addressbook/{id}", bookId.get())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressBook.class)
                .value(book -> assertThat(book.getBuddies()).isEmpty());

        client.delete().uri("/api/addressbook/{id}", bookId.get())
                .exchange()
                .expectStatus().isNoContent();

        client.get().uri("/api/addressbook/{id}", bookId.get())
                .exchange()
                .expectStatus().isNotFound();
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
