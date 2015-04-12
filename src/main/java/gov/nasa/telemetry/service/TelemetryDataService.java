/**
 * 
 */
package gov.nasa.telemetry.service;

import java.util.List;

import gov.nasa.telemetry.pojo.TelemetryData;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 */
@WebService
@Path("/telemetry")
public interface TelemetryDataService {

	@GET
    @Path("{id}")
	//@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    String getTelemetryData(@PathParam("id") String telemetryId);
	
	@GET
    @Path("/all")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	List<TelemetryData> getAllTelemetryData();
}
