package hotel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("Server starting...");
        System.setProperty("java.rmi.server.hostname", "13.75.158.120");
        BookingManager bm = new BookingManager();
        BookingManagerInterface stub = (BookingManagerInterface) UnicastRemoteObject.exportObject(bm, 8083);
        int registryPort = 8083;
        System.out.println("Creating registry...");
        LocateRegistry.createRegistry(registryPort);
        Registry registry = LocateRegistry.getRegistry(registryPort);
        System.out.println("Binding stub...");
        registry.rebind("BookingManager", stub);
        System.out.println("Server ready to use. Registry can be found in port 8083.");
    }
}
