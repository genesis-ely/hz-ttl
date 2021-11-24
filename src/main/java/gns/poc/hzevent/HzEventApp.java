package gns.poc.hzevent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.EntryProcessor;

public class HzEventApp {

  private static final String SESSION_MAP_NAME = "sessions";
  private static String appId;
  private static final List<String> LOCAL_IDS = new ArrayList<>();

  public static void main(String[] args) {
     appId = args[0];
     Config config = new Config();
     TcpIpConfig tcpConfig = config.getNetworkConfig().getJoin().getTcpIpConfig();
     tcpConfig.setEnabled(true);
     tcpConfig.addMember("127.0.0.1:5701");
     tcpConfig.addMember("127.0.0.1:5702");
     tcpConfig.addMember("127.0.0.1:5703");
     MapConfig mapConfig = config.getMapConfig(SESSION_MAP_NAME);
     mapConfig.setTimeToLiveSeconds(10);
    //  mapConfig.setMaxIdleSeconds(5);
     mapConfig.addEntryListenerConfig(listenerConfig());
     final HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
     System.out.println("RUNNING " + appId);

     Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      instance.shutdown();
     }));

     addSession(instance);

     int interval = 0;
     while (true) {
       try {
        TimeUnit.SECONDS.sleep(1);
        // updateSession(instance);
        ++interval;
        if (interval == 10) {
          addSession(instance);
          interval = 0;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
     }
   }

  private static void addSession(HazelcastInstance instance) {
    Session session = new Session(appId);
    String id = session.getId();
    instance.getMap(SESSION_MAP_NAME).put(id, session);
    LOCAL_IDS.add(id);
  }

  private static void updateSession(HazelcastInstance instance) {
    for (String id: LOCAL_IDS) {
      final Session session = (Session) instance.getMap(SESSION_MAP_NAME).get(id);
      if (session != null) {
        session.update();
        instance.getMap(SESSION_MAP_NAME).executeOnKey(id, new EntryProcessor() {

          @Override
          public Object process(Entry entry) {
            Session _session = (Session) entry.getValue();
            _session.update();
            entry.setValue(_session);
            return session;
          }
          
        });
      }
    }
  }

  private static EntryListenerConfig listenerConfig() {
     EntryListenerConfig config = new EntryListenerConfig();
     config.setImplementation(new SessionExpiredListener(appId));
     config.setLocal(true);
     config.setIncludeValue(true);
     return config;
   }
}
