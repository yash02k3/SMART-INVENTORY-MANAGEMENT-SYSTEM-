package com.cypheranalytica.inventory.service;

import com.cypheranalytica.inventory.exception.InsufficientStockException;
import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Item;
import com.cypheranalytica.inventory.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service to simulate multi-threaded concurrent stock updates.
 * Demonstrates Java Multithreading lifecycle (Thread & Runnable),
 * race condition avoidance, and synchronized critical sections
 * preventing overselling across simultaneous billing counters.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class ConcurrencySimulationService {

    private final InventoryService inventoryService;
    private final TransactionService transactionService;

    // Synchronization lock object for stock transaction safety
    private static final Object STOCK_LOCK = new Object();

    public ConcurrencySimulationService(InventoryService inventoryService, TransactionService transactionService) {
        this.inventoryService = inventoryService;
        this.transactionService = transactionService;
    }

    /**
     * CounterTask implements Runnable to simulate a checkout terminal or billing counter.
     */
    private class CounterWorker implements Runnable {
        private final String counterName;
        private final int itemId;
        private final int requestedQty;
        private final AtomicInteger successCounter;
        private final AtomicInteger failedCounter;
        private final List<String> logs;

        public CounterWorker(String counterName, int itemId, int requestedQty,
                             AtomicInteger successCounter, AtomicInteger failedCounter,
                             List<String> logs) {
            this.counterName = counterName;
            this.itemId = itemId;
            this.requestedQty = requestedQty;
            this.successCounter = successCounter;
            this.failedCounter = failedCounter;
            this.logs = logs;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            log(String.format(">> [%s | %s] Starting transaction request for %d unit(s)...", 
                    threadName, counterName, requestedQty));

            // Small randomized sleep to simulate network and cashier latency
            try {
                Thread.sleep((long) (Math.random() * 120 + 30));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // CRITICAL SECTION: Synchronized to prevent Race Conditions & Overselling
            synchronized (STOCK_LOCK) {
                try {
                    Item itemBefore = inventoryService.getItemById(itemId);
                    int currentQty = itemBefore.getQuantity();

                    log(String.format("   [%s | %s] Current stock available: %d. Attempting checkout...", 
                            threadName, counterName, currentQty));

                    // Execute atomic stock deduction
                    Transaction txn = transactionService.recordStockOut(
                            itemId, requestedQty, counterName, "Concurrent simulation order"
                    );

                    Item itemAfter = inventoryService.getItemById(itemId);
                    successCounter.incrementAndGet();
                    log(String.format("   ✓ [%s | %s] SUCCESS! TXN: %s | Deducted: %d | Remaining Stock: %d",
                            threadName, counterName, txn.getTxnCode(), requestedQty, itemAfter.getQuantity()));

                } catch (InsufficientStockException e) {
                    failedCounter.incrementAndGet();
                    log(String.format("   ✗ [%s | %s] REJECTED: %s (Available: %d)",
                            threadName, counterName, e.getMessage(), e.getAvailable()));
                } catch (InventoryException e) {
                    failedCounter.incrementAndGet();
                    log(String.format("   ✗ [%s | %s] ERROR: %s", threadName, counterName, e.getMessage()));
                }
            }

            log(String.format(">> [%s | %s] Completed thread execution.", threadName, counterName));
        }

        private void log(String message) {
            synchronized (logs) {
                logs.add(message);
                System.out.println(message);
            }
        }
    }

    /**
     * Executes the concurrency test with multiple worker threads.
     */
    public void runSimulation(int itemId, int numCounters, int unitsPerCounter) {
        System.out.println("\n====================================================================");
        System.out.println("          CONCURRENCY & MULTITHREADING SIMULATION TEST             ");
        System.out.println("   Simulating simultaneous stock checkout across multiple counters  ");
        System.out.println("====================================================================");

        try {
            Item targetItem = inventoryService.getItemById(itemId);
            int initialStock = targetItem.getQuantity();

            System.out.println("Target Product  : " + targetItem.getName() + " (SKU: " + targetItem.getSku() + ")");
            System.out.println("Initial Stock   : " + initialStock + " units");
            System.out.println("Active Counters : " + numCounters + " concurrent worker threads");
            System.out.println("Order Size      : " + unitsPerCounter + " unit(s) per counter");
            System.out.println("Total Demand    : " + (numCounters * unitsPerCounter) + " units");
            System.out.println("--------------------------------------------------------------------");
            System.out.println(">> Spawning threads and starting execution...\n");

            AtomicInteger successCounter = new AtomicInteger(0);
            AtomicInteger failedCounter = new AtomicInteger(0);
            List<String> executionLogs = new ArrayList<>();
            List<Thread> threadPool = new ArrayList<>();

            // 1. Thread Creation (Thread Lifecycle: NEW)
            for (int i = 1; i <= numCounters; i++) {
                String counterName = (i == 1) ? "Counter-1 (Billing)" :
                                     (i == 2) ? "Counter-2 (Express)" :
                                     (i == 3) ? "Counter-3 (Online App)" :
                                     (i == 4) ? "Counter-4 (B2B Bulk)" : ("Counter-" + i);

                CounterWorker worker = new CounterWorker(
                        counterName, itemId, unitsPerCounter, successCounter, failedCounter, executionLogs
                );

                Thread t = new Thread(worker, "WorkerThread-" + i);
                threadPool.add(t);
            }

            // 2. Thread Execution (Thread Lifecycle: RUNNABLE)
            long startTime = System.currentTimeMillis();
            for (Thread t : threadPool) {
                t.start();
            }

            // 3. Thread Synchronization / Join (Thread Lifecycle: WAITING -> TERMINATED)
            for (Thread t : threadPool) {
                t.join();
            }
            long duration = System.currentTimeMillis() - startTime;

            Item finalItem = inventoryService.getItemById(itemId);
            int finalStock = finalItem.getQuantity();

            System.out.println("\n--------------------------------------------------------------------");
            System.out.println("                   SIMULATION RESULTS & VERIFICATION                 ");
            System.out.println("--------------------------------------------------------------------");
            System.out.printf("  Execution Time      : %d ms\n", duration);
            System.out.printf("  Initial Stock       : %d units\n", initialStock);
            System.out.printf("  Successful Orders   : %d\n", successCounter.get());
            System.out.printf("  Rejected Orders     : %d (Prevented Overselling)\n", failedCounter.get());
            System.out.printf("  Final Stock Left    : %d units\n", finalStock);
            System.out.printf("  Oversold Check      : %s\n", (finalStock >= 0 ? "PASSED (Zero overselling)" : "FAILED (Stock < 0)"));
            System.out.println("====================================================================\n");

        } catch (InventoryException | InterruptedException e) {
            System.err.println("Simulation interrupted: " + e.getMessage());
        }
    }
}
