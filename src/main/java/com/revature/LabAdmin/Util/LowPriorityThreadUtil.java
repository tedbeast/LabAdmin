package com.revature.LabAdmin.Util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class will contain a single thread to be used for low-priority batch jobs, such as scheduled lab updates,
 * pkey invalidation, and updates to saved lab blobs
 */
public class LowPriorityThreadUtil {
    public static ExecutorService executor = Executors.newFixedThreadPool(1);
}