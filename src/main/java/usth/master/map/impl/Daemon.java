package usth.master.map.impl;

import java.io.IOException;
import java.rmi.RemoteException;

import usth.master.map.Worker;

public class Daemon implements Worker {
    public static final String root = "data";

    /**
     * Open file and socket to receive block of data.
     * */
    public int upload(String file) throws RemoteException {
        try {
            FileDeliver fileTransfer = new FileDeliver(root + "/" + file, true);
            Integer port = fileTransfer.openServer();
            fileTransfer.start();
            return port;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Open file and socket to send block of data.
     * */
    public int download(String file) throws RemoteException {
        try {
            FileDeliver fileTransfer = new FileDeliver(root + "/" + file, false);
            Integer port = fileTransfer.openServer();
            fileTransfer.start();
            return port;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void call() throws RemoteException {
    }

}
