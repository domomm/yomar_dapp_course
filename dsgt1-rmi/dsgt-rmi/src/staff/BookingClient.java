package staff;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.*;

import hotel.BookingDetail;
import hotel.BookingManagerInterface;

public class BookingClient {
	private static final int NUM_THREADS = 4000;
	private static BookingManagerInterface bm;
	private static final String OUTPUT_FILE = "output-swi/output-4000thr-2min-timeout.csv";
	private static final int DURATION = 120;
	private static final int TIMEOUT = 60;

	public static void main(String[] args) throws Exception {
		initializeBookingManager();
		initWriteToFile(OUTPUT_FILE);
		CountDownLatch latch = new CountDownLatch(NUM_THREADS);
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		LocalDateTime startLocalTime = LocalDateTime.now();
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < NUM_THREADS; i++) {
			executor.submit(() -> {
				try {
					latch.countDown(); // Signal that this thread has started
					latch.await(); // Wait for all threads to start
					//performTask(1000);
					performTaskDuration(DURATION, startTime, TIMEOUT); //timeout is optional
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

		long endTime = System.currentTimeMillis();
		LocalDateTime endLocalTime = LocalDateTime.now();
		writeToFile(OUTPUT_FILE, -1 + "," + startLocalTime + "\n");
		writeToFile(OUTPUT_FILE, -2 + "," + endLocalTime + "\n");
		System.out.println("Total time taken: " + (endTime - startTime) + " milliseconds");
		System.out.println("It starts at " + startLocalTime.toString() + " and ends at " + endLocalTime.toString());
	}

	private static void initializeBookingManager() throws Exception {
		//Registry registry = LocateRegistry.getRegistry("localhost", 8083);
		//Registry registry = LocateRegistry.getRegistry("13.75.158.120", 8083); //AUS
		Registry registry = LocateRegistry.getRegistry("20.250.163.126", 8083); //SWITZERLAND
		bm = (BookingManagerInterface) registry.lookup("BookingManager");
	}

	private static void performTask(int numIterations, long processStartTime) {
		LocalDate date = LocalDate.of(2024, 3, 3);
		for (int i = 0; i < numIterations; i++) {
			try {
				long threadStartTime = System.currentTimeMillis();
				Set<Integer> availableRooms = bm.getAvailableRooms(date);
				long threadEndTime = System.currentTimeMillis();
				long duration = threadEndTime - threadStartTime;
				System.out.println("Thread " + Thread.currentThread().getId() + ": Iteration " + (i + 1) + " - Available rooms in date " + date.toString() + " are: " + availableRooms.toString());
				writeToFile(OUTPUT_FILE, Thread.currentThread().getId(), duration, processStartTime, i);
			} catch (Exception e) {
				System.out.println("ERROR in iteration " + i +" of thread " + Thread.currentThread().getId());
				writeToFile(OUTPUT_FILE, Thread.currentThread().getId(), -1, processStartTime, i);
			}
		}
	}

	private static void performTaskDuration(long durationInSeconds, long processStartTime, int timeout) {
		LocalDate date = LocalDate.of(2024, 3, 3);
		long startTime = System.currentTimeMillis();
		long endTime = startTime + (durationInSeconds * 1000); // Convert seconds to milliseconds
		int iteration = 1;

		ExecutorService executor = Executors.newSingleThreadExecutor();

		while (System.currentTimeMillis() < endTime) {
			try {
				long taskStartTime = System.currentTimeMillis();
				Callable<Set<Integer>> task = () -> bm.getAvailableRooms(date);
				Future<Set<Integer>> future = executor.submit(task);

				Set<Integer> availableRooms;
				try {
					availableRooms = future.get(timeout, TimeUnit.SECONDS); // Timeout set to 5 seconds
				} catch (TimeoutException e) {
					future.cancel(true); // Cancel the task
					System.out.println("ERROR Thread " + Thread.currentThread().getId() + ": Timeout of " + timeout+ " seconds occurred while fetching available rooms");
					writeToFile(OUTPUT_FILE, Thread.currentThread().getId(), -2, processStartTime, iteration);
					iteration++;
					continue; // Go to next iteration
				}

				long taskEndTime = System.currentTimeMillis();
				long taskDuration = taskEndTime - taskStartTime;

				System.out.println("Thread " + Thread.currentThread().getId() + ": Iteration " + iteration + " - Available rooms in date " + date.toString() + " are: " + availableRooms.toString());
				writeToFile(OUTPUT_FILE, Thread.currentThread().getId(), taskDuration, processStartTime, iteration);

				iteration++;
			} catch (Exception e) {
				System.out.println("ERROR in iteration " + iteration + " of thread " + Thread.currentThread().getId());
				writeToFile(OUTPUT_FILE, Thread.currentThread().getId(), -1, processStartTime, iteration);
				iteration++;
			}
		}

		executor.shutdown();
	}

	private static void performTaskDuration(long durationInSeconds, long processStartTime) {
		LocalDate date = LocalDate.of(2024, 3, 3);
		long startTime = System.currentTimeMillis();
		long endTime = startTime + (durationInSeconds * 1000); // Convert seconds to milliseconds
		int iteration = 1;

		while (System.currentTimeMillis() < endTime) {
			try {
				long threadStartTime = System.currentTimeMillis();
				Set<Integer> availableRooms = bm.getAvailableRooms(date);
				long threadEndTime = System.currentTimeMillis();
				long threadDuration = threadEndTime - threadStartTime;

				System.out.println("Thread " + Thread.currentThread().getId() + ": Iteration " + iteration + " - Available rooms in date " + date.toString() + " are: " + availableRooms.toString());
				writeToFile(OUTPUT_FILE, Thread.currentThread().getId(), threadDuration, processStartTime, iteration);

				iteration++;
			} catch (Exception e) {
				System.out.println("ERROR in iteration " + iteration + " of thread " + Thread.currentThread().getId());
				writeToFile(OUTPUT_FILE, Thread.currentThread().getId(), -1, processStartTime, iteration);
				iteration++;
			}
		}
	}
	private static synchronized void writeToFile(String fileName, long threadId, long duration, long startTime, int iteration) {
		try (FileWriter writer = new FileWriter(fileName, true)) {
			long endTime = System.currentTimeMillis() - startTime;
			writer.append(threadId + "," + duration + "\n");
		} catch (IOException e) {
			System.out.println("ERROR writing file");
			e.printStackTrace();
		}
	}

	private static synchronized void writeToFile(String fileName, String content) {
		try (FileWriter writer = new FileWriter(fileName, true)) {
			writer.append(content);
		} catch (IOException e) {
			System.out.println("ERROR writing file");
			e.printStackTrace();
		}
	}

	private static void initWriteToFile(String fileName){
		try (FileWriter writer = new FileWriter(fileName, false)) {
			writer.append("threadId,duration\n");
		} catch (IOException e) {
			System.out.println("ERROR init writing file");
			e.printStackTrace();
		}
	}

	private static void testBmFunctionalities(Set<Integer> availableRooms, LocalDate date, BookingManagerInterface bm) throws RemoteException {
		for (int roomNumber : availableRooms){
			System.out.println(roomNumber);
		}
		try {
			BookingDetail bd = new BookingDetail("omar", 101, date);
			bm.addBooking(bd);
		} catch (Exception e) {
			// Handle any exception
			System.err.println("An error occurred: " + e.getMessage());
			// Optionally, you can also log the exception or take any other necessary actions
		}
		System.out.println(bm.isRoomAvailable(101, date));
		try {
			BookingDetail bd = new BookingDetail("omar", 101, date);
			bm.addBooking(bd);
		} catch (Exception e) {
			// Handle any exception
			System.err.println("An error occurred: " + e.getMessage());
			// Optionally, you can also log the exception or take any other necessary actions
		}
		System.out.println(bm.isRoomAvailable(101, date));
		Set<Integer> currentAvRoom = bm.getAvailableRooms(date);
		System.out.println("Currently available rooms");
		for (int roomNumber : currentAvRoom){
			System.out.printf("%d ", roomNumber);
		}
		System.out.printf("%n");
	}

}
