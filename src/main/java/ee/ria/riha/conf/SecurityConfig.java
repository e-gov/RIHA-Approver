package ee.ria.riha.conf;

import ee.ria.riha.authentication.EstonianIdCardPreAuthenticatedFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${personalCertificateHeaderField}")
    private String personalCertificateHeaderField;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(preAuthenticatedAuthenticationProvider());
    }

    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();

        UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> userDetailsByNameServiceWrapper = new UserDetailsByNameServiceWrapper<>();
        userDetailsByNameServiceWrapper.setUserDetailsService(
                username ->
                        new User(
                                username,
                                "",
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_USER"))));

        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(userDetailsByNameServiceWrapper);
        return preAuthenticatedAuthenticationProvider;
    }

    @Bean
    public EstonianIdCardPreAuthenticatedFilter estonianIdCardPreAuthenticatedFilter() throws Exception {
        EstonianIdCardPreAuthenticatedFilter filter = new EstonianIdCardPreAuthenticatedFilter();
        filter.setExceptionIfHeaderMissing(false);
        filter.setPrincipalRequestHeader(personalCertificateHeaderField);
        AuthenticationManager manager = authenticationManager();
        filter.setAuthenticationManager(manager);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().addHeaderWriter(
                new XFrameOptionsHeaderWriter(
                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                )
        );
        http.addFilterBefore(estonianIdCardPreAuthenticatedFilter(), BasicAuthenticationFilter.class);

        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login");
    }

}
