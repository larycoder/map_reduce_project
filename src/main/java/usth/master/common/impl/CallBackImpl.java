package usth.master.common.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import usth.master.common.CallBack;

public class CallBackImpl extends UnicastRemoteObject implements CallBack {
    private Integer nbNode;
    private Integer nbCplt; // count number of notify

    public CallBackImpl(int nbNode) throws RemoteException {
        this.nbNode = nbNode;
        this.nbCplt = 0;
    }

    public void set() {
        nbCplt = 0;
    }

    public synchronized void completed() throws RemoteException {
        nbCplt++;
        System.out.print("Receive completed notify ");
        System.out.print("[");
        System.out.print(nbCplt.toString() + "/" + nbNode.toString());
        System.out.println("]");
        notify();
    }

    public synchronized void waitForAll() {
        while (nbCplt < nbNode) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
