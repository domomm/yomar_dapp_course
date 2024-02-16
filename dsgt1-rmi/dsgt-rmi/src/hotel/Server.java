package hotel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) throws Exception {
        BookingManager bm = new BookingManager();
        BookingManagerInterface stub = (BookingManagerInterface) UnicastRemoteObject.exportObject(bm, 0);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("BookingManager", stub);
    }
}
