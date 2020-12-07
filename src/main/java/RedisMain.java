import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
public class RedisMain {
    public static void main(String[] args) {
        RedissonClient client = getRedisClient("",6379,"123456",15);
        String key = "";

        RBucket<String>  value = client.getBucket(key);
        System.out.println(value.get());
    }

    private static RedissonClient getRedisClient(String redisHost, int redisPort, String redisAuth, int redisDb){
        // 单实例模式
        Config config = new Config();
        config.useSingleServer()
                .setAddress(String.format("redis://%s:%s", redisHost, redisPort))
                .setPassword(redisAuth)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(4)
                .setSubscriptionConnectionPoolSize(10)
                .setSubscriptionConnectionMinimumIdleSize(4)
                // 同任何节点建立连接时的等待超时,时间单位是毫秒,默认：10000
                .setConnectTimeout(30000)
                // 等待节点回复命令的时间,该时间从命令发送成功时开始计时,默认:3000
                .setTimeout(10000)
                // 如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时,将抛出错误.
                // 如果尝试在此限制之内发送成功,则开始启用 timeout（命令等待超时） 计时,默认值：3
                .setRetryAttempts(5)
                // 在一条命令发送失败以后,等待重试发送的时间间隔,时间单位是毫秒,默认值：1500
                .setRetryInterval(3000)
                .setDatabase(redisDb);
        config.setThreads(4);
        config.setNettyThreads(4);
        return Redisson.create(config);
    }

}
