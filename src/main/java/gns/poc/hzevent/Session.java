package gns.poc.hzevent;

import java.io.Serializable;

public class Session implements Serializable{
    private String id;
    private Long timestamp;
    private Integer count = 0;
    private static int idInc = 0;

    public Session(String appId) {
        id = String.format("%s-%s",appId,++idInc);
        timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void update() {
        count = count + 1;
    }

    @Override
    public String toString() {
        return String.format("{id:%s, timestamp:%s}", id, timestamp);
    }
}
