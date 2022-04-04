package usth.master.worker.impl;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import usth.master.app.MapReduce;
import usth.master.common.CallBack;
import usth.master.common.FileDeliver;
import usth.master.worker.Daemon;

public class DaemonImpl extends UnicastRemoteObject implements Daemon {
    public static final String root = "data";

    public DaemonImpl() throws RemoteException {}

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

    public void call(MapReduce m, String blockin, String blockout, CallBack cb)
    throws RemoteException {
        // prepare
        String input = root + "/" + blockin;
        String output = root + "/" + blockout;

        File fileOut = new File(output);
        try {
            if (!fileOut.exists()) fileOut.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        m.executeMap(input, output);
    }
}
