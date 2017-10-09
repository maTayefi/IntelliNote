import com.uttesh.exude.exception.InvalidDataException;
import connectors.MatlabConnector;
import connectors.ReutersHandler;
import data_process.PrepareClustering;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import utilities.Utility;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static utilities.Utility.*;


/**
 * Created by maTayefi on 3/5/2017.
 */
public class IntelliNote{

    public static void main(String[] args) throws IOException, InvalidDataException, MatlabInvocationException, MatlabConnectionException, ExecutionException, InterruptedException {

        switch (Utility.current_stage) {
            case getting_data:
                getting_data();
                break;
            case process_data:
                process_data();
                break;
            case showing_result:
                showing_result();
                break;
            case getting_user_changes:
                getting_user_changes();
                break;
            case apply_changes:
                apply_changes();
                break;
            default:
                getting_data();
                break;
        }
    }

    private static void getting_data() throws IOException, InvalidDataException, MatlabInvocationException, MatlabConnectionException, ExecutionException, InterruptedException {
        //Reuters
        if (input == 0) {
            ReutersHandler.createModel();
        }
        //Evernote
        else {

        }
        current_stage = process_data;
        process_data();
    }

    private static void process_data() throws MatlabInvocationException, MatlabConnectionException, ExecutionException, InterruptedException {
        System.out.println("process_data");
        if (Utility.current_subStage == 0) {
            PrepareClustering.createVectorOfVectors();
            current_subStage = 1;
        }
        if (current_subStage == 1) {
            PrepareClustering.createSimilarityMatrix();
            current_subStage = 2;
        }
        if (current_subStage == 2) {
            System.out.println("runClustering");
            MatlabConnector.runClustering();
            current_subStage = 0;
        }
        current_stage = showing_result;
        showing_result();
    }

    private static void showing_result() {

        current_stage = getting_user_changes;
        getting_user_changes();
    }

    private static void getting_user_changes() {

        current_stage = apply_changes;
        apply_changes();
    }

    private static void apply_changes() {

        current_stage = getting_data;
    }


}
