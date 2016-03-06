package kafka.consumer.group;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 组(Group)消费模型
 * @author jackson
 *
 */
public class GroupConsumerTest{
	
	private final ConsumerConnector consumer;
    private final String topic;
    private ExecutorService executor;
	
    
	public GroupConsumerTest(String a_zookeeper, String a_groupId, String a_topic){
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(a_zookeeper, a_groupId));
        this.topic = a_topic;
	}
	
	/**
     * 创建消费者配置对象
     * @param a_zookeeper
     * @param a_groupId
     * @return
     */
	private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "2000");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }
	
	/**
	 * 
	 * @param a_numThreads
	 */
    @SuppressWarnings("rawtypes")
	public void execute(int a_numThreads) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
 
        executor = Executors.newFixedThreadPool(a_numThreads);
 
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            executor.submit(new ConsumerWork(stream, threadNumber));
            threadNumber++;
        }
    }
	
	/**
	 * 关闭消费者及连接池连接
	 */
	public void shutdown() {
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
        try {
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
   }
 
	public static void main(String[] args) {
		if(args.length < 1){
			System.out.println("Please assign partition number.");
		}
		
        String zooKeeper = "192.168.10.109:12181";
        String groupId = "1";
        String topic = "test";
        int threads = Integer.parseInt(args[0]);
 
		GroupConsumerTest example = new GroupConsumerTest(zooKeeper, groupId, topic);
        example.execute(threads);
 
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ie) {
        	ie.printStackTrace();
        }
        example.shutdown();
    }
	
}
