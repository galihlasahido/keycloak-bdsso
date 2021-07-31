# keycloak-bdsso

This is a simple Keycloak Java Authenticator that checks if the user and send it to BD SSO. 

## build

Make sure that Keycloak SPI dependencies and your Keycloak server versions match. Keycloak SPI dependencies version is configured in `pom.xml` in the `keycloak.version` property.  

To build the project execute the following command:

```bash
mvn package
```

## deploy

And then, assuming `$KEYCLOAK_HOME` is pointing to you Keycloak installation, just copy it into deployments directory:
 
```bash
cp target/keycloak-bdsso.jar $KEYCLOAK_HOME/standalone/deployments/
```
# keycloak-bdsso
