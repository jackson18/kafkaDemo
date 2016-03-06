package kafka.consumer.group;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
 
/**
 * 消费者工作线程
 * @author jackson
 *
 */
public class ConsumerWork implements Runnable {
	
    @SuppressWarnings("rawtypes")
	private KafkaStream m_stream;
    private int m_threadNumber;
 
    @SuppressWarnings("rawtypes")
	public ConsumerWork(KafkaStream a_stream, int a_threadNumber) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
    }
 
    @SuppressWarnings("unchecked")
	public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext()){
            System.out.println("Thread " + m_threadNumber + ": " + new String(it.next().message()));
			
        }
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}