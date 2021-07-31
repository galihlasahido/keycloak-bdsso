package id.ads.keycloak.bsso;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordFormFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.keycloak.provider.ProviderConfigProperty.STRING_TYPE;

public class BDSSOFactory extends UsernamePasswordFormFactory {
    public static final String PROVIDER_ID = "bdsso-authenticator";
    public static final BDSSOAuth SINGLETON = new BDSSOAuth();
    protected static final List<ProviderConfigProperty> configMetadata;
    static final String ALLOWED_IP_ADDRESS_CONFIG = "allowed_ip_address";
    static final String KEY_CONFIG = "key_config";

    static { configMetadata = ProviderConfigurationBuilder.create()
                .property().name("ipbdsso").type(STRING_TYPE).label("ID Address")
                    .defaultValue("127.0.0.1").helpText("IP Address of SSO")
                    .add().build();
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[] { AuthenticationExecutionModel.Requirement.REQUIRED };
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getDisplayType() {
        return "SSO BD";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> list = new ArrayList();

        ProviderConfigProperty name = new ProviderConfigProperty();
        name.setType(STRING_TYPE);
        name.setName(ALLOWED_IP_ADDRESS_CONFIG);
        name.setLabel("Host of SSO server");
        name.setDefaultValue("http://localhost:9097");
        list.add(name);

        ProviderConfigProperty key = new ProviderConfigProperty();
        key.setType(STRING_TYPE);
        key.setName(KEY_CONFIG);
        key.setLabel("Host of SSO AES Key");
        key.setDefaultValue("xxxxxxxxxxxxxxxx");
        list.add(key);

        return list;
    }
}
