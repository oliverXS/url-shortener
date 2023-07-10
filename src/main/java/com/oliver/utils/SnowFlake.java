package com.oliver.utils;

public class SnowFlake {
    private static final long EPOCH = 1672531200000L;
    private static final int TIMESTAMP_BITS = 41;
    private static final int WORKER_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowFlake(long workerId) {
        if (workerId < 0 || workerId >= (1L << WORKER_ID_BITS)) {
            throw new IllegalArgumentException("Worker ID must be between 0 and " + ((1L << WORKER_ID_BITS) - 1));
        }
        this.workerId = workerId;
    }

    public synchronized long generateId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Invalid system clock: Clock moved backwards.");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & ((1L << SEQUENCE_BITS) - 1);
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        long timestamp = currentTimestamp - EPOCH;
        return (timestamp << (WORKER_ID_BITS + SEQUENCE_BITS)) | (workerId << SEQUENCE_BITS) | sequence;
    }

    private long waitNextMillis(long currentTimestamp) {
        long nextTimestamp = System.currentTimeMillis();
        while (nextTimestamp <= currentTimestamp) {
            nextTimestamp = System.currentTimeMillis();
        }
        return nextTimestamp;
    }
}
