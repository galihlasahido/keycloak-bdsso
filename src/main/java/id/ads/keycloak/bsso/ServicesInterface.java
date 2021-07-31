package id.ads.keycloak.bsso;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authnoncore")
public interface ServicesInterface {
    @POST
    @Path("/loginnoncore")
    @Consumes({ MediaType.APPLICATION_JSON })
    Response loginssocore(User movie);

}
