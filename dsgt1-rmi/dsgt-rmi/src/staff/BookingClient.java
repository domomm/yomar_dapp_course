package staff;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import hotel.BookingDetail;
import hotel.BookingManager;
import hotel.BookingManagerInterface;

public class BookingClient extends AbstractScriptedSimpleTest {

	private BookingManager bm = null;

	public static void main(String[] args) throws Exception {
		//Registry registry = LocateRegistry.getRegistry("localhost", 8083);
		Registry registry = LocateRegistry.getRegistry("13.75.158.120", 8083);
		BookingManagerInterface bm = (BookingManagerInterface) registry.lookup("BookingManager");
		LocalDate date = LocalDate.of(2024, 3, 3);
		Set<Integer> availableRooms = bm.getAvailableRooms(date);
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

	/***************
	 * CONSTRUCTOR *
	 ***************/
	public BookingClient() {
		try {
			//Look up the registered remote instance
			bm = new BookingManager();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	@Override
	public boolean isRoomAvailable(Integer roomNumber, LocalDate date) {
		//Implement this method
		return true;
	}

	@Override
	public void addBooking(BookingDetail bookingDetail) throws Exception {
		//Implement this method
	}

	@Override
	public Set<Integer> getAvailableRooms(LocalDate date) {
		//Implement this method
		return null;
	}

	@Override
	public Set<Integer> getAllRooms() {
		return bm.getAllRooms();
	}
}
