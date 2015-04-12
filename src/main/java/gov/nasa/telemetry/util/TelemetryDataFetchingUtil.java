/**
 * 
 */
package gov.nasa.telemetry.util;

import gov.nasa.telemetry.pojo.TelemetryData;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lightstreamer.ls_client.ConnectionInfo;
import com.lightstreamer.ls_client.ExtendedConnectionListener;
import com.lightstreamer.ls_client.ExtendedTableInfo;
import com.lightstreamer.ls_client.HandyTableListener;
import com.lightstreamer.ls_client.LSClient;
import com.lightstreamer.ls_client.PushConnException;
import com.lightstreamer.ls_client.PushServerException;
import com.lightstreamer.ls_client.SubscribedTableKey;
import com.lightstreamer.ls_client.UpdateInfo;

@Component
@Scope("prototype")
public class TelemetryDataFetchingUtil {
	private static final Log log = LogFactory.getLog(TelemetryDataFetchingUtil.class);
			
	//public static String[] items = new String[] {"TIME_000001"};
	//public static String[] items = new String[] {"TIME_000001", "USLAB000032","USLAB000035","USLAB000033","USLAB000036","USLAB000034","USLAB000037"};
	//public static String[] items = new String[] {"NODE3000011", "USLAB000058","USLAB000059","P4000001","P4000002","P4000004","P4000005"};
	public static String[] items = TelemetryData.allTelemetryItems;
	public static String[] schemas = new String[] {"Value"};
//	public static String groupName = "USLAB000032 USLAB000035 USLAB000033 USLAB000036 USLAB000034 USLAB000037";
	//public static String groupName = "P4000004";
//	public static String schemaName = "Value";
	//public static String result = null;
	public static HashMap<String, String> telemetryMap = new HashMap<String, String>();
	
	static{
		updateTelemetryDataCache();
	}
	
	public static String fetchTelemetryData(String telemetryId) throws Exception {
		log.debug(telemetryMap);
		return telemetryMap.get(telemetryId);
	}
	
	public static void updateTelemetryDataCache() {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					log.debug("Update Telemetry Data cache !");
					final LSClient myClient = new LSClient();
					try{
				        myClient.openConnection(
				            new ConnectionInfo() {
				                {
				                	this.pushServerUrl = "http://push.lightstreamer.com";
				                    this.adapter = "ISSLIVE";
				                    // this.maxBandwidth = new Double(1.0);
				                }
				            },
				            new ExtendedConnectionListener() {
				                private long bytes = 0;
				                public void onConnectionEstablished() {
				                    log.debug("Connection established");
				                }
				                
				                public void onSessionStarted(boolean isPolling) {
				                    //never called
				                }
				                
				                public void onSessionStarted(boolean isPolling, String controlLink) {
				                    String clAddendum = controlLink != null ? " to server " + controlLink : "";
				                    if (isPolling) {
				                        log.debug("Session started in smart polling"+clAddendum);
				                    } else {
				                        log.debug("Session started in streaming"+clAddendum);
				                    }
				                }
				                
				                public void onNewBytes(long newBytes) {
				                    this.bytes += newBytes;
				                }
				                
				                public void onDataError(PushServerException e) {
				                    log.debug("data error");
				                    e.printStackTrace();
				                }
				                
				                public void onActivityWarning(boolean warningOn) {
				                    if (warningOn) {
				                        log.debug("connection stalled");
				                    } else {
				                        log.debug("connection no longer stalled");
				                    }
				                }
				                
				                public void onEnd(int cause) {
				                    log.debug("connection forcibly closed with cause code " + cause);
				                }
				                
				                public void onClose() {
				                    log.debug("total bytes: " + bytes);
				                }
				                
				                public void onFailure(PushServerException e) {
				                    log.debug("server failure");
				                    e.printStackTrace();
				                }
				                
				                public void onFailure(PushConnException e) {
				                    log.debug("connection failure");
				                    e.printStackTrace();
				                }
				            }
				        );
				        
				        
				        HandyTableListener listener = new HandyTableListener() {
		                    private String notifyUpdate(UpdateInfo update) {
		                        return update.isSnapshot() ? "snapshot" : "update";
		                    }
		                    
		                    private String notifyValue(UpdateInfo update, String fldName) {
		                        String notify = " " + fldName + " = " + update.getNewValue(fldName);
		                        if (update.isValueChanged(fldName)) {
		                            notify += " (was " + update.getOldValue(fldName) + ")";
		                        }
		                        
		                        notify = update.getNewValue(fldName);
		                        log.debug("Notify: " + notify);
		                        
		                        return notify;
		                    }
		                    
		                    @Override
		                    public void onUpdate(int itemPos, String itemName, UpdateInfo update) {
		                    	log.debug("itemPos : " + itemPos + "itemName: " + itemName);
		                        
		                    	log.debug(
		                    			notifyUpdate(update) +
		                                " for " + itemName + ":" +
		    							notifyValue(update, "Value")
		                                );
		                    	
		                    	telemetryMap.put(itemName, notifyValue(update, "Value"));
		                    }
		                    
		                    public void onSnapshotEnd(int itemPos, String itemName) {
		                        log.debug("end of snapshot for " + itemName);
		                    }
		                    
		                    public void onRawUpdatesLost(int itemPos, String itemName, int lostUpdates) {
		                        log.debug(lostUpdates + " updates lost for " + itemName);
		                    }
		                    
		                    public void onUnsubscr(int itemPos, String itemName) {
		                    	log.debug("itemPos : " + itemPos + ", itemName: " + itemName);
		                    	
		                        log.debug("unsubscr " + itemName);
		                    }
		                    
		                    public void onUnsubscrAll() {
		                        log.debug("unsubscr table");
		                    }
		                };
				        
				        SubscribedTableKey tableRef = myClient.subscribeTable(
				                new ExtendedTableInfo(
				                    items,
				                    "MERGE",
				                    schemas,
				                    true) ,
				                listener,
				                false
				            );
				        
				        Thread.sleep(10000);
				        myClient.unsubscribeTable(tableRef);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
				        myClient.closeConnection();
					}
					
					try {
						Thread.sleep(20000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		thread.start();
    }
   
	public static void main(String[] args) throws Exception {
		updateTelemetryDataCache();
		
		Thread.sleep(20000);
		String result = TelemetryDataFetchingUtil.fetchTelemetryData("NODE3000011");
		log.debug("====>" + result);
	}
}
