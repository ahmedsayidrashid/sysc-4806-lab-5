package org.example;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestTestClient
class AddressBookMockTest {

    @Autowired
    private RestTestClient client;

    @Test
    void requestsMatchLabScript() {
        AddressBookApiChecks.createBookAndAddBuddies(client);
    }
}
