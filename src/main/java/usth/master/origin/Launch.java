package usth.master.origin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import usth.master.app.MapReduce;
import usth.master.common.CallBack;
import usth.master.common.ClientTransfer;
import usth.master.worker.Daemon;

/**
 * Communicate to server, execute map and synthesize result.
 * */
public class Launch {
    private List<Map<String, String>> hosts;
    private Map<String, String> hostFiles;
    private String rootDir;

    /**
     * @param hosts list of mapper hosts
     * @param hostFiles list of mapper hosts and corresponding mapper files
     * @param rootDir directory to store and merge to final result
     * */
    public Launch(
        List<Map<String, String>> hosts, Map<String, String> hostFiles,
        String rootDir, MapReduce m) {
        this.hosts = hosts;
        this.hostFiles = hostFiles;
        this.rootDir = rootDir;
    }

    /**
     * Pull data from server file and push to local file
     * @param host host information for get file as map: host, port, name
     * @param hostFileName file on host for pulling
     * @param fileName local file name to push data into
     * @return true for success and false for fail
     * */
    public boolean pullFile(
        Map<String, String> host, String hostFileName, String fileName) {
        String data = ClientTransfer.pull(host, hostFileName);
        String localFileName = rootDir + "/" + fileName;

        try {
            File rootDirProp = new File(rootDir);
            if (!rootDirProp.exists()) {
                rootDirProp.mkdirs();
            }

            File localFile = new File(localFileName);
            if (!localFile.exists()) {
                localFile.createNewFile();
            }

            OutputStream os = new FileOutputStream(localFile);
            os.write(data.getBytes());
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Call execute and collect result.
     * @param m mapreduce executor
     * @param cb callback for complete information
     * @return final result file
     * */
    public String run(MapReduce m, CallBack cb) {
        List<String> localFiles = new ArrayList<String>();

        // execute and collect result
        for (Map<String, String> host : hosts) {
            try {
                // Prepare name for processing
                String fileId = host.get("host") + ":" + host.get("port");
                String hostFileName = hostFiles.get(fileId);
                String hostFileResultName = UUID.randomUUID().toString();
                String fileName = rootDir + "/" + UUID.randomUUID().toString();
                String rmiUrl = "rmi://"
                                + host.get("host") + ":"
                                + host.get("port") + "/"
                                + host.get("name");

                // process and get result
                Daemon worker = (Daemon) Naming.lookup(rmiUrl);
                worker.call(m, hostFileName, hostFileResultName, cb);
                if (pullFile(host, hostFileResultName, fileName)) {
                    localFiles.add(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String finalResult = rootDir + "/" + UUID.randomUUID().toString();
        m.executeReduce(localFiles, finalResult);
        return finalResult;
    }
}
