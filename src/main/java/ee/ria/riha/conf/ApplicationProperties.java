package ee.ria.riha.conf;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Valentin Suhnjov
 */
@Configuration
@ConfigurationProperties(prefix = "approver")
public class ApplicationProperties {

    private final StorageClientProperties storageClient = new StorageClientProperties();
    private final SecurityProperties security = new SecurityProperties();

    public StorageClientProperties getStorageClient() {
        return storageClient;
    }

    public SecurityProperties getSecurity() {
        return security;
    }

    public static class StorageClientProperties {

        @NotEmpty
        private String baseUrl;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class Cors {
        private final List<String> allowedOrigins = new ArrayList<>();

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins.addAll(allowedOrigins);
        }
    }

    public static class SecurityProperties {
        private final Cors cors = new Cors();

        public Cors getCors() {
            return cors;
        }
    }
}
