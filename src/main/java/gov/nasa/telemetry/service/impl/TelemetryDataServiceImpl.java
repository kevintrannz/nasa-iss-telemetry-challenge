package gov.nasa.telemetry.service.impl;


import java.util.ArrayList;
import java.util.List;

import gov.nasa.telemetry.pojo.TelemetryData;
import gov.nasa.telemetry.service.TelemetryDataService;
import gov.nasa.telemetry.util.TelemetryDataFetchingUtil;

import javax.jws.WebService;

import org.springframework.stereotype.Service;

@Service("telemetryDataServiceManager")
@WebService(serviceName = "TelemetryDataService", endpointInterface = "gov.nasa.telemetry.service.TelemetryDataService")
public class TelemetryDataServiceImpl implements TelemetryDataService {
	
	@Override
	public String getTelemetryData(String telemetryId) {
		String result = "";
		try {
			result = TelemetryDataFetchingUtil.fetchTelemetryData(telemetryId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public List<TelemetryData> getAllTelemetryData() {
		List<TelemetryData> listTelemetryData = new ArrayList<TelemetryData>();
		try {
			for (String telemetryId : TelemetryData.allTelemetryItems) {
				listTelemetryData.add(new TelemetryData(telemetryId, TelemetryDataFetchingUtil.fetchTelemetryData(telemetryId)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return listTelemetryData;
	}

}
