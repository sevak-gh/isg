package com.infotech.isg.service;

import com.infotech.isg.service.impl.AccessControlImpl;
import com.infotech.isg.domain.Client;
import com.infotech.isg.repository.ClientRepository;
import com.infotech.isg.validation.ErrorCodes;

import java.util.Map;
import java.util.HashMap;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
* test cases for access control.
*
* @author Sevak Gharibian
*/
public class AccessControlTest {

    private AccessControl accessControl;
    private ClientRepository clientRepository;

    @BeforeClass
    public void setUp() {

        // mock repository for client
        clientRepository = new ClientRepository() {
            private Map<String, Client> clients = new HashMap<String, Client>() {
                {
                    put("root", new Client() {{
                            setId(1);
                            setUsername("root");
                            // pass = SHA512("123456")
                            setPassword("ba3253876aed6bc22d4a6ff53d8406c6ad864195"
                                        + "ed144ab5c87621b6c233b548baeae6956df346"
                                        + "ec8c17f5ea10f35ee3cbc514797ed7ddd31454"
                                        + "64e2a0bab413");
                            addIp("1.1.1.1");
                            addIp("2.2.2.2");
                            setIsActive(true);
                        }
                    });
                    put("tejarat", new Client() {{
                            setId(2);
                            setUsername("tejarat");
                            // pass = SHA512("tejarat")
                            setPassword("6020527f4cbbd21282676133b9c66afdd0e1a55"
                                        + "8c15c61de1a58e8276d891e19a75a3b272e34"
                                        + "91a6e1b14c9b4e1e9aceb6e0ec65f4c91ba1a"
                                        + "223e31889423de5");
                            addIp("10.20.120.10");
                            setIsActive(false);
                        }
                    });
                    put("vanak", new Client() {{
                            setId(3);
                            setUsername("vanak");
                            // pass = SHA512("van123")
                            setPassword("1e4b158a909ba4627a98b83b14db16fbcf319be"
                                        + "10cf7fa287119fd269757d57a72c506284ed0"
                                        + "2c7c92738751778f472ec48200da5c82e1b56"
                                        + "8b52b774339142e");
                            setIsActive(true);
                        }
                    });
                }
            };

            public Client findByUsername(String username) {
                return clients.get(username);
            }
        };

        accessControl = new AccessControlImpl(clientRepository);
    }

    @DataProvider(name = "provideClients")
    public Object[][] provideClients() {
        return new Object[][] {
            {"", "", "", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"admin", "admin123", "2.5.6.7", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"root", "", "", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"Root", "123456", "1.1.1.1", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"root", "123456", "1.1.1.1", ErrorCodes.OK},
            {"tejarat", "111111", "1.1.1.1", ErrorCodes.INVALID_USERNAME_OR_PASSWORD},
            {"tejarat", "tejarat", "10.20.120.10", ErrorCodes.DISABLED_CLIENT_ACCOUNT},
            {"tejarat", "tejarat", "1.1.1.1", ErrorCodes.DISABLED_CLIENT_ACCOUNT},
            {"vanak", "van123", "1.1.1.1", ErrorCodes.INVALID_CLIENT_IP},
            {"vanak", "van123", "10.20.120.10", ErrorCodes.INVALID_CLIENT_IP},
            {"root", "123456", "10.20.120.50", ErrorCodes.INVALID_CLIENT_IP},
            {"root", "123456", "172.16.10.15", ErrorCodes.INVALID_CLIENT_IP},
            {"root", "123456", "2.2.2.2", ErrorCodes.OK}
        };
    }

    @Test(dataProvider = "provideClients")
    public void shouldAuthenticateReturnExpectedErrorNode(String username, String password, String ip, int errorCode) {
        // arrange
        // username,password,ip combinations and expected error codes provided through data provider

        // act
        int result = accessControl.authenticate(username, password, ip);

        // assert
        assertThat(result, is(errorCode));
    }
}
