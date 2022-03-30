package usth.master;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallBack extends Remote {
    /**
     * notify callback when task finished.
     * */
    void completed() throws RemoteException;
}
