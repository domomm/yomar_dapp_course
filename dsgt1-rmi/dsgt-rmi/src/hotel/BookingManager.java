package hotel;

import staff.BookingClient;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class BookingManager implements BookingManagerInterface {

	private Room[] rooms;

	public BookingManager() {
		this.rooms = initializeRooms();
	}

	public Set<Integer> getAllRooms() {
		Set<Integer> allRooms = new HashSet<Integer>();
		Iterable<Room> roomIterator = Arrays.asList(rooms);
		for (Room room : roomIterator) {
			allRooms.add(room.getRoomNumber());
		}
		return allRooms;
	}

	public boolean isRoomAvailable(int roomNumber, LocalDate date) {
		//implement this method
		for (Room room : rooms) {
			if (room.getRoomNumber() == roomNumber) {
				for (BookingDetail booking : room.getBookings()){
					System.out.printf("Server: Checking if room %d is available%n" +
									"The date of the booking is %s while the asked date is %s%n",
							roomNumber,
							booking.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
							date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
					if (booking.getDate().isEqual(date))
						return false;
				}
			}
		}
		return true;
	}

	public void addBooking(BookingDetail bookingDetail) {
		//implement this method
		int roomNumber = bookingDetail.getRoomNumber();
		LocalDate date = bookingDetail.getDate();
		if (isRoomAvailable(roomNumber, date)) {
			for (Room room : rooms) {
				if (room.getRoomNumber() == roomNumber) {
					room.getBookings().add(bookingDetail);
					return;
				}
			}
		} else {
			throw new IllegalArgumentException("Room " + roomNumber + " is not available on " + date);
		}
		//checkBookingsOfRoom(roomNumber); // this is only for debugging purposes
	}

	private void checkBookingsOfRoom(int roomNumber){
		for (Room room: rooms){
			if (room.getRoomNumber() == roomNumber){
				System.out.printf("room number: %d%n", roomNumber);
				for (BookingDetail bd : room.getBookings()){
					System.out.printf("Guest is: %s, date is %s, room number is %d%n",
							bd.getGuest(),
							bd.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
							bd.getRoomNumber());
				}
			}
		}
	}

	public Set<Integer> getAvailableRooms(LocalDate date) {
		//implement this method
		System.out.println("There is a call to get available rooms for date " + date.toString());
		Set<Integer> retRooms = new TreeSet<>();
		for (Room room: rooms){
			if (isRoomAvailable(room.getRoomNumber(), date)){
				retRooms.add(room.getRoomNumber());
			}
		}
		return retRooms;
	}

	private static Room[] initializeRooms() {
		Room[] rooms = new Room[4];
		rooms[0] = new Room(101);
		rooms[1] = new Room(102);
		rooms[2] = new Room(201);
		rooms[3] = new Room(203);
		return rooms;
	}
}
