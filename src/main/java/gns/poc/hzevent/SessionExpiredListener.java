package gns.poc.hzevent;

import java.io.Serializable;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryExpiredListener;

public class SessionExpiredListener implements EntryExpiredListener<String, Session>, Serializable{

    private String appId;

    public SessionExpiredListener(String appId) {
        this.appId = appId;
    }

    @Override
    public void entryExpired(EntryEvent<String,Session> event) {
        Session session = event.getOldValue();
        String message = String.format("Expired! app: %s, sessionId: %s, duration: %s",
            appId, session.getId(), System.currentTimeMillis() - session.getTimestamp());
        System.out.println(message);
    }
    
}
