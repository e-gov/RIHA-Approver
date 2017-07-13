package ee.ria.riha.conf;

import ee.ria.riha.storage.client.StorageClient;
import ee.ria.riha.storage.domain.CommentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Valentin Suhnjov
 */
@Configuration
public class ApplicationConfiguration {

    @Bean
    public CommentRepository commentRepository(ApplicationProperties applicationProperties) {
        return new CommentRepository(getStorageClient(applicationProperties));
    }

    private StorageClient getStorageClient(ApplicationProperties applicationProperties) {
        RestTemplate restTemplate = new RestTemplate();
        return new StorageClient(restTemplate, applicationProperties.getStorageClient().getBaseUrl());
    }

}
