package usth.master.worker;

import java.rmi.Remote;
import java.rmi.RemoteException;

import usth.master.app.MapReduce;
import usth.master.common.CallBack;

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
     * @param m map execution interface
     * @param blockin filename of input map
     * @param blockout filename of output map
     * @param cb callback to notify master after finishing task
     * */
    void call(MapReduce m, String blockin, String blockout, CallBack cb)
            throws RemoteException;
}
