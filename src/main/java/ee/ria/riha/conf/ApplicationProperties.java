package ee.ria.riha.conf;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Valentin Suhnjov
 */
@Configuration
@ConfigurationProperties(prefix = "approver")
public class ApplicationProperties {

    private final StorageClientProperties storageClient = new StorageClientProperties();

    public StorageClientProperties getStorageClient() {
        return storageClient;
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

}
