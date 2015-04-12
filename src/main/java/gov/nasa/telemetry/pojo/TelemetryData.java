/**
 * 
 */
package gov.nasa.telemetry.pojo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TelemetryData {
	// All information needed.
	public static final String[] allTelemetryItems = new String[] {"NODE3000011", "USLAB000058","USLAB000059","P4000001","P4000002","P4000004","P4000005"};
	
	private String telemetryId;
	private String telemetryValue;

	public TelemetryData() {
		super();
	}

	public TelemetryData(String telemetryId, String telemetryValue) {
		super();
		this.telemetryId = telemetryId;
		this.telemetryValue = telemetryValue;
	}

	public String getTelemetryId() {
		return telemetryId;
	}

	public void setTelemetryId(String telemetryId) {
		this.telemetryId = telemetryId;
	}

	public String getTelemetryValue() {
		return telemetryValue;
	}

	public void setTelemetryValue(String telemetryValue) {
		this.telemetryValue = telemetryValue;
	}

}
