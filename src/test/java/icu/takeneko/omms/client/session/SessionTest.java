package icu.takeneko.omms.client.session;

import icu.takeneko.omms.client.exception.ConnectionFailedException;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

class SessionTest {
    ClientSession session;

    @Test
    void testSession() throws Throwable {
        try {
            ClientInitialSession initialSession = new ClientInitialSession(InetAddress.getByName("localhost"), 50000);
            String token = ClientInitialSession.generateToken("o6n1ywzk");
            System.out.println("token = " + token);
            session = initialSession.init(token);
            CountDownLatch latch = new CountDownLatch(3);
            session.fetchWhitelistFromServer()
                .thenAccept(result -> {
                    System.out.println("whitelists = " + result);
                    latch.countDown();
                });
            session.fetchControllersFromServer()
                .thenAccept(result -> {
                    System.out.println("controllers = " + result);
                    latch.countDown();
                });
            while (latch.getCount() != 1) {
                LockSupport.parkNanos(1);
            }
            latch.await();
            session.join();
        } catch (ConnectionFailedException e) {
            System.out.println("e.getResponse() = " + e.getResponse());
            throw e;
        }
    }
}