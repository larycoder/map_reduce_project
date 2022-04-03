package usth.master.origin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import usth.master.common.ClientTransfer;
import usth.master.worker.Daemon;

/**
 * Load file and distribute it to Map servers.
 * */
public class Split {
    private List<Map<String, String>> hosts;
    private String fileName;

    /**
     * @param hosts list of server hosts as map of keys: host, port, name
     * @param fileName input file to process
     * */
    public Split(List<Map<String, String>> hosts, String fileName) {
        this.hosts = hosts;
        this.fileName = fileName;
    }

    /**
     * @param host host used to push data into
     * @param file file used to store data on server
     * @param data data to push into servers
     * */
    private boolean push(Map<String, String> host, String file, String data) {
        return ClientTransfer.push(host, file, data);
    }

    /**
     * Load input file and convert to list of lines.
     * @param file file name
     * @return list of line on file
     * */
    private List<String> loadFile(String file) {
        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return lines;
    }

    /**
     * Split data on file to mapper servers.
     * @return list of host and corresponding pushed file
     * */
    public Map<String, String> run() {
        Map<String, String> info = new HashMap<String, String>();
        int hostNum = hosts.size();

        // prepare data as line
        List<String> lines = loadFile(fileName);
        int dataNum = lines.size();

        // add data to servers
        int dataPerHost = (dataNum + dataNum % hostNum) / hostNum;
        for (int h = 0; h < hostNum; h++) {
            // merge line to data
            int count = dataPerHost;
            String data = "";
            while (count-- == 0 || lines.isEmpty()) {
                data += lines.remove(0);
                data += "\n";
            }

            // push data to server
            String fileName = UUID.randomUUID().toString();
            if (push(hosts.get(h), fileName, data)) {
                Map<String, String> host = hosts.get(h);
                String hostStr = host.get("host") + ":"
                                 + host.get("port").toString();
                info.put(hostStr, fileName);
            }
        }
        return info;
    }
}
