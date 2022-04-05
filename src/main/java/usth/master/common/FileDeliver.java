package usth.master.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server side class that handle stream data from client to server
 * including upload/download file to/from server.
 *
 * */
public class FileDeliver extends Thread {
    private String fileName;
    private ServerSocket ss;
    private boolean opt; // true is upload, false is download

    /**
     * @param fileName work with file in server
     * @param opt action option: true is upload, false download
     * */
    public FileDeliver(String fileName, boolean opt) {
        this.fileName = fileName;
        this.opt = opt;
    }

    /**
     * open stream server.
     * */
    public Integer openServer() throws IOException {
        ss = new ServerSocket(0);
        return ss.getLocalPort();
    }

    public void run() {
        try {
            // accept only one connection
            Socket client = ss.accept();

            File file = new File(fileName);
            if (!file.exists()) file.createNewFile();

            InputStream is;
            OutputStream os;

            System.out.println("Deliver access file: " + fileName);
            if (opt == true) {
                is = client.getInputStream();
                os = new FileOutputStream(file);
            } else {
                is = new FileInputStream(file);
                os = client.getOutputStream();
            }

            byte[] data = new byte[1000];
            int len;
            while ((len = is.read(data)) > 0) {
                os.write(data, 0, len);
                os.flush();
            }

            is.close();
            os.close();
            client.close();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
