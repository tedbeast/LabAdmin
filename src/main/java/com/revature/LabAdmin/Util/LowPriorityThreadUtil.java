package com.revature.LabAdmin.Util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO This class will contain a single thread to be used for low-priority batch jobs, such as scheduled lab updates,
 * pkey invalidation, and updates to saved lab blobs.
 * Executing these processes in a single thread will help to avoid tricky race condition issues with
 * file manip / blob storage
 */
public class LowPriorityThreadUtil {
    public static ExecutorService executor = Executors.newFixedThreadPool(1);
}
