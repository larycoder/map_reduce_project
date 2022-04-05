package usth.master;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usth.master.app.MapReduce;
import usth.master.app.impl.WordCount;
import usth.master.common.CallBack;
import usth.master.common.impl.CallBackImpl;
import usth.master.origin.Launch;
import usth.master.origin.Split;
import usth.master.worker.Daemon;
import usth.master.worker.impl.DaemonImpl;

/**
 * Deploy and run map reduce.
 *
 */
public class Driver {
    private static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("Map reduce program...\n")
        .append("Usage: java Driver [OPT] [bind_name | host_file input_file]\n")
        .append("\n")
        .append("OPT:\n")
        .append(" -d run Daemon process\n")
        .append(" -e run word count app\n")
        .append("\n")
        .append("ARG:\n")
        .append(" bind_name: bind_port name and port to bind Daemon\n")
        .append(" host_file: file contains list of mapper hosts\n")
        .append("            with each line is a mapper and have pattern\n")
        .append("            [host:port:name]\n")
        .append(" input_file: input file for map reduce application\n")
        .append("\n");
        return builder.toString();
    }

    /**
     * @param name rmi binding name
     * @param port rmi binding port
     * */
    private void bootDaemon(String name, Integer port) {
        try {
            Daemon worker = new DaemonImpl();
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(name, worker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param hostFile file contains all workers with pattern: (host:port:name\n)
     * @param inputFile input file for map reduce process
     * @param app map reduce application
     * @return final result file
     * */
    private String bootApp(String hostFile, String inputFile, MapReduce app) {
        try {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(hostFile)));

            List<Map<String, String>> hosts = new ArrayList<Map<String, String>>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] hostInfo = line.split(":");
                Map<String, String> hostMap = new HashMap<String, String>();
                hostMap.put("host", hostInfo[0]);
                hostMap.put("port", hostInfo[1]);
                hostMap.put("name", hostInfo[2]);
                hosts.add(hostMap);
            }

            Split splitter = new Split(hosts, inputFile);
            Map<String, String> mapperFiles = splitter.run();

            CallBack cb = new CallBackImpl(hosts.size());
            Launch launcher = new Launch(hosts, mapperFiles, "data");
            return launcher.run(app, cb);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main( String[] args ) {
        if (args.length != 2 && args.length != 3) {
            System.out.println(usage());
        } else {
            Driver driver = new Driver();

            if (args[0].equals("-d")) {
                String[] inputs = args[1].split(":");
                driver.bootDaemon(inputs[0], Integer.parseInt(inputs[1]));
            } else if (args[0].equals("-e")) {
                MapReduce app = new WordCount();
                driver.bootApp(args[1], args[2], app);
            } else {
                usage();
            }
        }
    }
}
