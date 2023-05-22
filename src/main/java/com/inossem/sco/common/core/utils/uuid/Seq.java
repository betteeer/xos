package com.inossem.sco.common.core.utils.uuid;

import com.inossem.sco.common.core.utils.DateUtils;
import com.inossem.sco.common.core.utils.StringUtils;
import java.util.concurrent.atomic.AtomicInteger;

public class Seq {
    public static final String commSeqType = "COMMON";
    public static final String uploadSeqType = "UPLOAD";
    private static AtomicInteger commSeq = new AtomicInteger(1);
    private static AtomicInteger uploadSeq = new AtomicInteger(1);
    private static String machineCode = "A";

    public Seq() {
    }

    public static String getId() {
        return getId("COMMON");
    }

    public static String getId(String type) {
        AtomicInteger atomicInt = commSeq;
        if ("UPLOAD".equals(type)) {
            atomicInt = uploadSeq;
        }

        return getId(atomicInt, 3);
    }

    public static String getId(AtomicInteger atomicInt, int length) {
        String result = DateUtils.dateTimeNow();
        result = result + machineCode;
        result = result + getSeq(atomicInt, length);
        return result;
    }

    private static synchronized String getSeq(AtomicInteger atomicInt, int length) {
        int value = atomicInt.getAndIncrement();
        int maxSeq = (int)Math.pow(10.0, (double)length);
        if (atomicInt.get() >= maxSeq) {
            atomicInt.set(1);
        }

        return StringUtils.padl(value, length);
    }
}
