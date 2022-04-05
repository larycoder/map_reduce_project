package usth.master.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.util.Map;

import usth.master.worker.Daemon;

/**
 * Client side handling for upload and download data from host
 * */
public class ClientTransfer {

    /**
     * @param host host used to push data into which is map: host, port, name
     * @param file file used to store data on server
     * @param data data to push into servers
     * */
    public static boolean push(
        Map<String, String> host, String file, String data) {
        try {
            String rmiUrl = "rmi://"
                            + host.get("host") + ":"
                            + host.get("port") + "/"
                            + host.get("name");
            Daemon server = (Daemon) Naming.lookup(rmiUrl);

            int port = server.upload(file);
            Socket svc = new Socket(host.get("host"), port);

            OutputStream os = svc.getOutputStream();
            InputStream is = new ByteArrayInputStream(data.getBytes());

            byte[] buffer = new byte[1000];
            int len;
            while((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }

            is.close();
            os.close();
            svc.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Pull data from host
     * @param host host information to pull data which is map: host, port, name
     * @param file host file holds data to pull
     * @return result data
     * */
    public static String pull(Map<String, String> host, String file) {
        try {
            String rmiUrl = "rmi://"
                            + host.get("host") + ":"
                            + host.get("port") + "/"
                            + host.get("name");
            Daemon server = (Daemon) Naming.lookup(rmiUrl);

            int port = server.download(file);
            Socket svc = new Socket(host.get("host"), port);

            InputStream is = svc.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String result = "";
            String line;
            while((line = br.readLine()) != null) {
                result += line + "\n";
            }

            br.close();
            is.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
