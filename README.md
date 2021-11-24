# hz-ttl
A PoC for Hazelcast TTL.

# Why
- To prove we can use hazelcast TTL as trigger (using listener)
- To prove trigger is local (trigger is executed by one node only, the one owning the primary of entry)
- To prove solution is HA (when a node goes down and primary migrates, the trigger will be executed by the node where primary migrated to)
