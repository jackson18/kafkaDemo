package kafka.producer.partiton;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
 
/**
 * 生产者负载均衡算法 partitioner
 * @author jackson
 *
 */
public class SimplePartitioner implements Partitioner {
	
    public SimplePartitioner (VerifiableProperties props) {
    }
 
    public int partition(Object key, int a_numPartitions) {
        int partition = 0;
        String stringKey = (String) key;
        int offset = stringKey.lastIndexOf('.');
        if (offset > 0) {
           partition = Integer.parseInt( stringKey.substring(offset+1)) % a_numPartitions;
        }
       return partition;
  }
 
}
