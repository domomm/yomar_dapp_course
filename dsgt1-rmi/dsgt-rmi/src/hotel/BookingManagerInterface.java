package hotel;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.Set;

public interface BookingManagerInterface extends Remote{
    public boolean isRoomAvailable(int roomNumber, LocalDate date) throws RemoteException;
    public void addBooking(BookingDetail bookingDetail) throws RemoteException;
    public Set<Integer> getAvailableRooms(LocalDate date) throws RemoteException;
    public Set<Integer> getAllRooms() throws RemoteException;
}
