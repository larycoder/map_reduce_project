package usth.master;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Run on slave node to execute Map on the node.
 *
 * 1. Split connect (TCP) to Daemon in order to upload a block of data which
 *    then be stored in file.
 * 2. Launch invoke (RMI) a method call() of Daemon in order to execute a Map
 *    on the node.
 * 3. Launch connect (TCP) to Daemon in order to download a block of data
 *    (result).
 * */
public interface Daemon extends Remote {
    /**
     * Upload data to server.
     * @param file upload file name.
     * @return socket port.
     * */
    int upload(String file) throws RemoteException;

    /**
     * Upload data to server.
     * @param file upload file name.
     * @return socket port.
     * */
    int download(String file) throws RemoteException;

    /**
     * Call map execution.
     * */
    void call() throws RemoteException;
}
