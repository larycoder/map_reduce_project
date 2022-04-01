package usth.master.app;

import java.io.Serializable;
import java.util.Collection;

/**
 * support logic for map and reduce task.
 * */
public interface MapReduce extends Serializable {
    /**
     * @param blockin filename to get input string
     * @param blockout filename to get output string
     * */
    public void executeMap(String blockin, String blockout);

    /**
     * @param blocks list of result block
     * @param blocks filename of final result output
     * */
    public void executeReduce(Collection<String> blocks, String finalresults);
}
