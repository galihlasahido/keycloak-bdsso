package id.ads.keycloak.bsso;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.component.ComponentModel;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.messages.Messages;
import org.keycloak.storage.adapter.InMemoryUserAdapter;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Map;

import static org.keycloak.services.validation.Validation.FIELD_PASSWORD;

public class BDSSOAuth extends UsernamePasswordForm implements Authenticator {
    protected static ServicesLogger log = ServicesLogger.LOGGER;
    protected ComponentModel config;

    @Override
    public boolean validateUserAndPassword(AuthenticationFlowContext context, MultivaluedMap inputData) {
        String username = inputData.getFirst(AuthenticationManager.FORM_USERNAME).toString();
        String password = inputData.getFirst(CredentialRepresentation.PASSWORD).toString();
        if (username == null) {
            context.getEvent().error(Errors.USER_NOT_FOUND);
            Response challengeResponse = challenge(context, Messages.INVALID_USER);
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challengeResponse);
            return false;
        }

        // remove leading and trailing whitespace
        username = username.trim();
        context.getEvent().detail(Details.USERNAME, username);
        context.getAuthenticationSession().setAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME, username);
        UserModel user = new InMemoryUserAdapter(context.getSession(), context.getRealm(), username);
        user.setUsername(username);

        AuthenticatorConfigModel configModel = context.getAuthenticatorConfig();
        Map<String, String> config = configModel.getConfig();

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(config.get(BDSSOFactory.ALLOWED_IP_ADDRESS_CONFIG)));
        ServicesInterface proxy = target.proxy(ServicesInterface.class);

        Response ssoResponse = proxy.loginssocore(new User(user.getUsername(), EncryptDecryptAES.encrypt(password, config.get(BDSSOFactory.KEY_CONFIG))));
        String response = ssoResponse.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(response, Map.class);
            if (Integer.valueOf(map.get("status").toString()) == 200 &&
                    map.get("code").toString().equalsIgnoreCase("00")) {
                return true;
            } else {
                return badPasswordHandler(context, user, true,false);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        String rememberMe = inputData.getFirst("rememberMe").toString();
        boolean remember = rememberMe != null && rememberMe.equalsIgnoreCase("on");
        if (remember) {
            context.getAuthenticationSession().setAuthNote(Details.REMEMBER_ME, "true");
            context.getEvent().detail(Details.REMEMBER_ME, "true");
        } else {
            context.getAuthenticationSession().removeAuthNote(Details.REMEMBER_ME);
        }
        context.setUser(user);

        return true;
    }


    // Set up AuthenticationFlowContext error.
    private boolean badPasswordHandler(AuthenticationFlowContext context, UserModel user, boolean clearUser,boolean isEmptyPassword) {
        context.getEvent().user(user);
        context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
        Response challengeResponse = challenge(context, getDefaultChallengeMessage(context), FIELD_PASSWORD);
        if(isEmptyPassword) {
            context.forceChallenge(challengeResponse);
        }else{
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
        }

        if (clearUser) {
            context.clearUser();
        }
        return false;
    }

}
