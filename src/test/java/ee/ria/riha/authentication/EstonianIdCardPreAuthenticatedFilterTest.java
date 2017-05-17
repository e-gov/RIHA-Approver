package ee.ria.riha.authentication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test_application.properties")
public class EstonianIdCardPreAuthenticatedFilterTest {

    @Value("${personalCertificateHeaderField}")
    private String sslHeaderField;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldRedirectAnonymousUsersToLoginPage() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"))
        ;
    }

    @Test
    public void shouldRedirectAdminUserWithValidCertificateToFrontPage() throws Exception {
        mvc.perform(
                get("/")
                        .header(sslHeaderField, "serialNumber=12345678901;personalName=John"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("ADMIN", "USER"))
                .andExpect(view().name("index"));
    }

    @Test
    public void shouldRedirectUserWithValidCertificateToFrontPage() throws Exception {
        mvc.perform(
                get("/")
                        .header(sslHeaderField, "serialNumber=10987654321;personalName=Doe"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(view().name("index"));
    }

    @Test
    public void shouldRedirectUsersWithInvalidCertificateToLoginPage() throws Exception {
        mvc.perform(
                get("/")
                        .header(sslHeaderField, "serialNumber=notANumber;personalName=John"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }
}
