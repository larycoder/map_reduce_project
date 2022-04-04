package usth.master.common.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import usth.master.common.CallBack;

public class CallBackImpl extends UnicastRemoteObject implements CallBack {
    int nbNode;

    public CallBackImpl(int nbNode) throws RemoteException {
        this.nbNode = nbNode;
    }

    public void completed() throws RemoteException {
        notify();
    }

    public synchronized void waitForAll() {
        for (int i = 0; i < nbNode; i++) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
