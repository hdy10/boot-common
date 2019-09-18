package com.github.hdy.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 高效GUID产生算法(sequence),基于Snowflake实现64位自增ID算法。
 * 优化开源项目 https://gitee.com/yu120/sequence
 *
 * @author 贺大爷
 * @date 2019/6/25
 */
public class IdWorker {
    /**
     * 主机和进程的机器码
     */
    private static Sequence WORKER = new Sequence();

    /**
     * 毫秒格式化时间
     */
    public static final DateTimeFormatter MILLISECOND = DateTimeFormatter.ofPattern(Dates.FORMAT_NONE);

    /**
     * 19位
     */
    public static long getId() {
        return WORKER.nextId();
    }

    public static String getIdStr() {
        return String.valueOf(WORKER.nextId());
    }

    /**
     * 格式化的毫秒时间
     */
    public static String getMillisecond() {
        return LocalDateTime.now().format(MILLISECOND);
    }

    /**
     * 时间 ID = Time + ID
     * <p>例如：可用于商品订单 ID</p>
     */
    public static String getTimeId() {
        return getMillisecond() + getId();
    }

    /**
     * 有参构造器
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 序列号
     */
    public static void initSequence(long workerId, long datacenterId) {
        WORKER = new Sequence(workerId, datacenterId);
    }


    /**#####################UUID########################*/
    /**
     * 标准UUID(36位)
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 使用ThreadLocalRandom获取UUID获取更优的效果 去掉"-"
     */
    public static String get32UUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString().replace("-", "");
    }

    /**
     * 得到16位的UUID-(数字)
     *
     * @return
     */
    public static Long get16UUID() {
        int machineId = 1;// 最大支持1-9个集群机器部署

        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {// 有可能是负数
            hashCodeV = -hashCodeV;
        }
        String string = machineId + String.format("%015d", hashCodeV);
        return Long.valueOf(string);
    }

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    /**
     * 得到8位的UUID-(码)
     *
     * @return
     */
    public static Long get8UUID() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return Long.valueOf(shortBuffer.toString());

    }
}
