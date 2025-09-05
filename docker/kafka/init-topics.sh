docker exec -i broker kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic fct.user.message.event --if-not-exists
docker exec -i broker kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic fct.user.event --if-not-exists
