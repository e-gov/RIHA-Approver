package ee.ria.riha.conf;

import ee.ria.riha.authentication.EstonianIdCardPreAuthenticatedFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${personalCertificateHeaderField}")
    private String personalCertificateHeaderField;

    @Value("${ldap.auth.userSearchBase}")
    private String userSearchBase;

    @Value("${ldap.auth.groupSearchBase}")
    private String groupSearchBase;

    @Value("${ldap.auth.userSearchFilter}")
    private String userSearchFilter;


    @Value("${ldap.host}")
    private String ldapHost;

    @Value("${ldap.port}")
    private String ldapPort;

    @Value("${ldap.baseDn}")
    private String ldapBaseDn;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(preAuthenticatedAuthenticationProvider());
    }

    @Bean
    public UserDetailsService ldapUserDetailsService() {
        DefaultLdapAuthoritiesPopulator authoritiesPopulator = new DefaultLdapAuthoritiesPopulator(contextSource(), groupSearchBase);
        authoritiesPopulator.setGroupRoleAttribute("ou");
        authoritiesPopulator.setDefaultRole("ROLE_USER");

        return new LdapUserDetailsService(
                new FilterBasedLdapUserSearch(userSearchBase, userSearchFilter, contextSource()),
                authoritiesPopulator);
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        return new DefaultSpringSecurityContextSource(
                Collections.singletonList("ldap://" + ldapHost + ":" + ldapPort + "/"),
                ldapBaseDn);
    }

    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();

        UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> userDetailsByNameServiceWrapper = new UserDetailsByNameServiceWrapper<>();
        userDetailsByNameServiceWrapper.setUserDetailsService(ldapUserDetailsService());

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
