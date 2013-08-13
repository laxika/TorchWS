package io.torch.test.cookie;

import io.torch.cookie.CookieVariable;
import io.torch.cookie.ReadOnlyCookieDataStorage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ReadOnlyCookieDataStorageTest {

    private ReadOnlyCookieDataStorage storage;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        storage = new ReadOnlyCookieDataStorage("SESSID=somesessid; TESTVAL=asmalltestvalue");
    }

    @Test
    public void testStorageInitialisation() throws Exception {
        assert storage.getCookie("SESSID").getName().equals("SESSID") : "Cookie test 1";
        assert storage.getCookie("SESSID").getValue().equals("somesessid") : "Cookie test 2";
        assert storage.getCookie("TESTVAL").getName().equals("TESTVAL") : "Cookie test 3";
        assert storage.getCookie("TESTVAL").getValue().equals("asmalltestvalue") : "Cookie test 4";
        assert storage.getCookie("someval") == null : "Cookie test 5";

        storage = new ReadOnlyCookieDataStorage(null);
        assert storage.getCookie("someval") == null : "Cookie test 6";
    }

    @Test
    public void testStorageForeachLoop() throws Exception {
        int amount = 0;

        for (CookieVariable cookie : storage) {
            if (amount == 0) {
                assert cookie.getName().equals("TESTVAL") : "Cookie test";
                assert cookie.getValue().equals("asmalltestvalue") : "Cookie test";
            } else if (amount == 1) {
                assert cookie.getName().equals("SESSID") : "Cookie test";
                assert cookie.getValue().equals("somesessid") : "Cookie test";
            }
            amount++;
        }

        assert amount == 2 : "Cookie test";

        storage = new ReadOnlyCookieDataStorage(null);
        
        for (CookieVariable cookie : storage) {
        }
    }
}
