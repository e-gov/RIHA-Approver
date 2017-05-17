package ee.ria.riha.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EstonianIdCardPreAuthenticatedFilter extends RequestHeaderAuthenticationFilter {

    private static final String SERIAL_NUMBER_GROUP = "serialNumber";
    private static final Pattern SSN_PARSING_PATTERN = Pattern.compile("^.*(?:serialNumber=(?<" + SERIAL_NUMBER_GROUP + ">\\d{11}))(\\D.*$|$)");

    private static String getSsnNumberFromCertificateString(String certificate) {

        if (certificate == null) {
            return null;
        }

        log.debug("found raw id card cert {} ", certificate);

        Matcher matcher = SSN_PARSING_PATTERN.matcher(certificate);
        if (matcher.matches()) {
            return matcher.group(SERIAL_NUMBER_GROUP);
        } else {
            log.debug("could not parse personal SSN from cert");
        }

        return null;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {

        String certificate = (String) super.getPreAuthenticatedPrincipal(httpServletRequest);

        if (certificate == null) {
            return null;
        }

        return getSsnNumberFromCertificateString(certificate);
    }
}