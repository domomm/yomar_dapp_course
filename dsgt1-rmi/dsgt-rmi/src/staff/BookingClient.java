package staff;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import hotel.BookingDetail;
import hotel.BookingManager;
import hotel.BookingManagerInterface;

public class BookingClient {
	private static final int NUM_THREADS = 1000; // Change this to the desired number of threads

	public static void main(String[] args) throws Exception {
		CountDownLatch latch = new CountDownLatch(NUM_THREADS);
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < NUM_THREADS; i++) {
			executor.submit(() -> {
				try {
					latch.countDown(); // Signal that this thread has started
					latch.await(); // Wait for all threads to start
					long threadStartTime = System.currentTimeMillis();
					performTask();
					long threadEndTime = System.currentTimeMillis();
					writeToFile(Thread.currentThread().getId(), threadEndTime - threadStartTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken: " + (endTime - startTime) + " milliseconds");
	}

	private static void performTask() throws Exception {
		//Registry registry = LocateRegistry.getRegistry("localhost", 8083);
		Registry registry = LocateRegistry.getRegistry("13.75.158.120", 8083);
		BookingManagerInterface bm = (BookingManagerInterface) registry.lookup("BookingManager");
		LocalDate date = LocalDate.of(2024, 3, 3);
		Set<Integer> availableRooms = bm.getAvailableRooms(date);
		System.out.println("Thread " + Thread.currentThread().getId() + ": Available rooms in date " + date.toString() + " are: " + availableRooms.toString());
	}

	private static synchronized void writeToFile(long threadId, long duration) {
		try (FileWriter writer = new FileWriter("output.csv", true)) {
			writer.append(threadId + "," + duration + "\n");
		} catch (IOException e) {
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
