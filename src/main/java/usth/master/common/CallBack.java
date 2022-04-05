package usth.master.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallBack extends Remote {
    /**
     * notify callback when task finished.
     * */
    public void completed() throws RemoteException;
}
