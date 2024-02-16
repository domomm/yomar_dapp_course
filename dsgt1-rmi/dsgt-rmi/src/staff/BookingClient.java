package staff;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.Set;

import hotel.BookingDetail;
import hotel.BookingManager;
import hotel.BookingManagerInterface;

public class BookingClient extends AbstractScriptedSimpleTest {

	private BookingManager bm = null;

	public static void main(String[] args) throws Exception {
		Registry registry = LocateRegistry.getRegistry();
		BookingManagerInterface bm = (BookingManagerInterface) registry.lookup("BookingManager");
		Set<Integer> availableRooms = bm.getAvailableRooms(LocalDate.now());
		for (int roomNumber : availableRooms){
			System.out.println(roomNumber);
		}

		LocalDate date = LocalDate.of(2024, 3, 3);
		BookingDetail bookingDetail = new BookingDetail("Omar", 101, date);
		bm.addBooking(bookingDetail);

		System.out.println(bm.isRoomAvailable(101, date));
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
