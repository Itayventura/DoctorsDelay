import org.apache.log4j.Logger;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Environment;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;


public class AlgorithmsImpl implements Algorithms {
    private static final Logger logger = Logger.getLogger(AlgorithmsImpl.class);
    private static final String CSV_SEPARATOR = ",";
    private static final String dataSetPath = "C:\\Users\\shiranpilas\\university\\4 year\\crowdsourcing\\dataSet.csv";
    private DataBase db;

    /**
     * @param db - mock when testing, real impl when server initializes
     */
    public AlgorithmsImpl(DataBase db) {
        logger.info("Algorithms is initialized with db=" + db.getClass().getName());
        this.db = db;
    }

    @Override
    public int getCurrentDelay(String doctorsName) {
        return -1; //TODO - implement
    }

    @Override
    public int getEstimatedDelay(String doctorsName, LocalDateTime meetingDateTime) {
        List<DataBase.DelayReport> listDelayReports = db.getReports(doctorsName,LocalDateTime.now().minusMonths(6),LocalDateTime.now());
        writeToCSV(listDelayReports);
        Instances dataSet = getDataSet();
        dataSet.setClassIndex(dataSet.numAttributes() - 1);

        //need to be added = >  filter and preperation data
        //need to be added = > lock csv file while use it

        Instances[] trainTestSplitResult = trainTestSplit(dataSet,80);

        RandomForest forest=new RandomForest();
        forest.setNumIterations(5000);//number of trees


        try{
            forest.buildClassifier(trainTestSplitResult[0]);
            Evaluation eval = new Evaluation(trainTestSplitResult[0]);
            eval.evaluateModel(forest, trainTestSplitResult[1]);

            System.out.println("** Decision Tress Evaluation with Datasets **");
            System.out.println(eval.toSummaryString());
            System.out.print(" the expression for the input data as per alogorithm is ");
            System.out.println(forest);
            System.out.println(eval.toMatrixString());
            System.out.println(eval.toClassDetailsString());
        }
        catch(Exception ex){
            logger.info("train and testing model were failed: " + ex.getMessage());
        }









        return -1; //TODO - implement
    }

    @Override
    public void addReport(String doctorsName, int reportedDelay) {
        //TODO - implement
    }



    private static void writeToCSV(List<DataBase.DelayReport> DelayReportList)
    {

        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\shiranpilas\\university\\4 year\\crowdsourcing\\dataSet_output.csv"), "UTF-8"));
            StringBuffer oneLine = new StringBuffer();
            oneLine.append("Year");
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Month");
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Day");
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Hour");
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Minutes");
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Delay");
            oneLine.append(CSV_SEPARATOR);
            bw.write(oneLine.toString()+ System.lineSeparator());
            int counter = 0;

            for (DataBase.DelayReport report : DelayReportList)
            {
                counter++;
                oneLine = new StringBuffer();
                oneLine.append(report.getReportTimestamp().getYear());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(report.getReportTimestamp().getMonth().getValue());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(report.getReportTimestamp().getDayOfWeek());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(report.getReportTimestamp().getHour());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(report.getReportTimestamp().getMinute());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(report.getReportedDelay());
                if(counter < DelayReportList.size()){
                    bw.write(oneLine.toString()+System.lineSeparator());
                }
                else{
                    bw.write(oneLine.toString());
                }

            }
            bw.flush();
            bw.close();
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }


    private void convertCSVToArff(String csvPath, String arffPath){

        try{

            // load CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(csvPath));
            Instances data = loader.getDataSet();

            // save ARFF
            ArffSaver arffFile = new ArffSaver();
            arffFile.setInstances(data);
            arffFile.setFile(new File(arffPath));
            arffFile.writeBatch();
            // .arff file will be created in the output location
        }
        catch(Exception ex){
            logger.info("convert CSV file to Arff failed: " + ex.getMessage());
        }
    }

    private Instances getDataSet()
    {
        Instances dataSet = null;
        try
        {
            DataSource source = new DataSource(dataSetPath);
            //source.reset();
            dataSet = source.getDataSet();

        }
        catch(Exception ex){
            logger.info("get data set was failed: " + ex.getMessage());

        }
        return dataSet;
    }
    private Instances[] trainTestSplit(Instances dataSet, int percentage)
    {
        Instances[] trainTestSplitResult = new Instances[2];
        int trainSize = (int) Math.round(dataSet.numInstances() * percentage / 100);
        int testSize = dataSet.numInstances() - trainSize;
        dataSet.randomize(new java.util.Random(0));
        Instances train = new Instances(dataSet, 0, trainSize);
        Instances test = new Instances(dataSet, trainSize, testSize);
        trainTestSplitResult[0] = train;
        trainTestSplitResult[1] = test;
        return trainTestSplitResult;

    }



}


