package gov.faa.fens;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ImportCSV
{
    private static String xenaHeaderCsv = "timestamp," +
            "srcport,srcname,sid,destport,destname,tid,srctodest," +
            "txl1bps,txbps,txfps,txbytes,txframes,"  +
            "rxl1bps,rxbps,rxfps,rxbytes,rxframes,"  +
            "txnonp1dbytes,txnonp1dframes,rxnonp1dbytes,rxnonp1dframes,"  +
            "rxfcserrors,rxseqerr,packetlossps,prtt,rxmiserr,rxp1derr,"  +
            "rxber,rxbercurr,"+
            "latencycurr,latencycurrmin,latencycurrmax,latencyavg,latencymin,latencymax," +
            "jittercurr,jittercurrmin,jittercurrmax,jitteravg,jittermin,jittermax" +
            "rxpauseframes,rxpfcframes";
    
    private static String pingHeaderCsv = "timestamp,srctodest,bytes,rtt,ttl";

    private static String xenaPatternCsv = "\"%s\"," +
            "\"%s\",\"%s\",%d,\"%s\",\"%s\",%d,\"%s\"," +
            "%d,%d,%d,%d,%d," +
            "%d,%d,%d,%d,%d," +
            "%d,%d,%d,%d," +
            "%d,%d,%d,%d,%d,%d," +
            "%f,,%d" +
            "%f,%f,%f,%f,%f,%f," +
            "%f,%f,%f,%f,%f,%f," +
            "%d,%d\n";

    private static String pingPatternJson = "{\"index\":{\"_id\":\"%s\"}}\n{\"timestamp\":\"%s\",\"time\":\"%s\",\"dayofweek\":\"%d\",\"year\":\"%d\",\"month\":\"%d\",\"day\":\"%d\",\"hour\":\"%d\",\"minute\":\"%d\",\"second\":\"%d\"," +
            "\"ip\":\"%s\",\"srctodest\":\"%s\",\"bytes\":\"%d\",\"rtt\":\"%d\",\"ttl\":\"%d\"}\n";

    private static String xenaPatternJson = "{\"index\":{\"_id\":\"%s\"}}\n{\"timestamp\":\"%s\",\"time\":\"%s\",\"year\":\"%d\",\"month\":\"%d\",\"day\":\"%d\",\"hour\":\"%d\",\"minute\":\"%d\",\"second\":\"%d\"," +
    "\"srcport\":\"%s\",\"srcname\":\"%s\",\"sid\":\"%d\",\"destport\":\"%s\",\"destname\":\"%s\",\"tid\":\"%d\",\"srctodest\":\"%s\"," +
    "\"txl1bps\":\"%d\",\"txbps\":\"%d\",\"txfps\":\"%d\",\"txbytes\":\"%d\",\"txframes\":\"%d\"," +
    "\"rxl1bps\":\"%d\",\"rxbps\":\"%d\",\"rxfps\":\"%d\",\"rxbytes\":\"%d\",\"rxframes\":\"%d\"," +
    "\"txnonp1dbytes\":\"%d\",\"txnonp1dframes\":\"%d\",\"rxnonp1dbytes\":\"%d\",\"rxnonp1dframes\":\"%d\"," +
    "\"rxfcserrors\":\"%d\",\"rxseqerr\":\"%d\",\"packetlossps\":\"%d\",\"prtt\":\"%d\",\"rxmiserr\":\"%d\",\"rxp1derr\":\"%d\"," +
    "\"rxber\":\"%f\",\"rxbercurr\":\"%d\"," +
    "\"latencycurr\":\"%f\",\"latencycurrmin\":\"%f\",\"latencycurrmax\":\"%f\",\"latencyavg\":\"%f\",\"latencymin\":\"%f\",\"latencymax\":\"%f\"," +
    "\"jittercurr\":\"%f\",\"jittercurrmin\":\"%f\",\"jittercurrmax\":\"%f\",\"jitteravg\":\"%f\",\"jittermin\":\"%f\",\"jittermax\":\"%f\"," +
    "\"rxpauseframes\":\"%d\",\"rxpfcframes\":\"%d\"" +
    "}\n";

    private static final Map<String, String> monthMap = new HashMap<>();

    private static final Map<String, String> portMapA = new HashMap<>();
    private static final Map<String, String> portMapB = new HashMap<>();
    private static final Map<String, String> portMapC = new HashMap<>();

    private static final Map<String, String> portReMapA = new HashMap<>();
    private static final Map<String, String> portReMapB = new HashMap<>();
    private static final Map<String, String> portReMapC = new HashMap<>();

    private static final String urlPatternXena = "/xenapr/_bulk?pretty";
    private static final String urlPatternPing = "/pingtime/_bulk?pretty";
    private static final String urlPatternException = "/xenapexp/_bulk?pretty";
    private static String urlStringPing;
    private static String urlStringXena;
    private static String urlStringException;

    private static final int BLOCK_SIZE = 3000;

    private static int blockCounterXena = BLOCK_SIZE;
    private static int blockCounterPing = BLOCK_SIZE;
    private static int blockCounterException = BLOCK_SIZE;
    private static int blockCounterCsvXena = BLOCK_SIZE;
    private static int blockCounterCsvPing = BLOCK_SIZE;

    private static Boolean isCsvOutOnly;

    static
    {
        // Maps

        // BL2a
        portMapA.put("P-0-3-0","Cisco 2800");
        portMapA.put("P-0-3-1","Digi D2");
        portMapA.put("P-0-3-2","Digi D3");
        portMapA.put("P-0-3-3","Pepwave P4");
        portMapA.put("P-0-3-4","Pepwave P5");
        portMapA.put("P-0-3-5","Pepwave B305");
        portMapA.put("P-1-5-0","Digi D1");
        portMapA.put("P-1-5-1","CradlePoint CP5");
        portMapA.put("P-1-5-2","Digi D1");
        portMapA.put("P-1-5-3","Pepwave P1");
        portMapA.put("P-1-5-4","Pepwave P2");
        portMapA.put("P-1-5-5","Pepwave P2");
        portMapA.put("P-1-5-6","CradlePoint CP6");

        // BL2b
        portMapB.put("P-0-3-0","Cisco 2800");
        portMapB.put("P-0-3-1","Digi D2");
        portMapB.put("P-0-3-2","Digi D3");
        portMapB.put("P-0-3-3","Pepwave P4");
        portMapB.put("P-0-3-4","Pepwave P5");
        portMapB.put("P-0-3-5","Pepwave B305");
        portMapB.put("P-1-5-0","Digi D1");
        portMapB.put("P-1-5-1","Pepwave P6");
        portMapB.put("P-1-5-2","CradlePoint CP1");
        portMapB.put("P-1-5-3","CradlePoint CP5");
        portMapB.put("P-1-5-4","Pepwave P1");
        portMapB.put("P-1-5-5","Pepwave P2");

        // BL2c
        portMapC.put("P-0-3-0","Cisco 2800");
        portMapC.put("P-0-3-1","Digi D2");
        portMapC.put("P-0-3-2","Digi D3");
        portMapC.put("P-0-3-3","Pepwave P4");
        portMapC.put("P-0-3-4","Pepwave P5");
        portMapC.put("P-0-3-5","Pepwave B305");
        portMapC.put("P-1-5-0","Digi D1");
        portMapC.put("P-1-5-1","Pepwave P6");
        portMapC.put("P-1-5-2","CradlePoint CP1");
        portMapC.put("P-1-5-3","CradlePoint CP5");
        portMapC.put("P-1-5-4","Pepwave P1");
        portMapC.put("P-1-5-5","Pepwave P2");

        // BL2a
        portReMapA.put("Cisco 2800 to CradlePoint CP5","BL2a-2 I2OKC AT&T");
        portReMapA.put("CradlePoint CP5 to Cisco 2800","BL2a-2 OKC2I AT&T");
        portReMapA.put("Cisco 2800 to Digi D3","BL2a-3 I2ACY Vz");
        portReMapA.put("Digi D3 to Cisco 2800","BL2a-3 ACY2I Vz");
        portReMapA.put("Pepwave B305 to Pepwave P4","BL2a-4 I2ACY T-Mobile");
        portReMapA.put("Pepwave P4 to Pepwave B305","BL2a-4 ACY2I T-Mobile");
        portReMapA.put("Pepwave B305 to Pepwave P5","BL2a-5 I2ACY T-Mobile unencrypted");
        portReMapA.put("Pepwave P5 to Pepwave B305","BL2a-5 ACY2I T-Mobile unencrypted");
        portReMapA.put("Digi D2 to Digi D1","BL2a-6 ACY2OKC AT&T");
        portReMapA.put("Digi D1 to Digi D2","BL2a-6 OKC2ACY AT&T");
        portReMapA.put("Digi D3 to CradlePoint CP6","BL2a-6e ACY2OKC Vz");
        portReMapA.put("CradlePoint CP6 to Digi D3","BL2a-6e OKC2ACY Vz");
        portReMapA.put("Pepwave P4 to Pepwave P2","BL2a-6e ACY2OKC T-Mobile");
        portReMapA.put("Pepwave P2 to Pepwave P4","BL2a-6e OKC2ACY T-Mobile");
        portReMapA.put("Pepwave P1 to Pepwave P2","BL2a-7 OKC2OKC AT&T to T-Mobile");
        portReMapA.put("Pepwave P2 to Pepwave P1","BL2a-7 OKC2OKC T-Mobile to AT&T");
        portReMapA.put("ACY to OKC","BL2a-8 Internet");

        // BL2b
        portReMapB.put("Cisco 2800 to Digi D1","BL2b-2 I2OKC AT&T");
        portReMapB.put("Digi D1 to Cisco 2800","BL2b-2 OKC2I AT&T");
        portReMapB.put("Cisco 2800 to Digi D3","BL2b-3 I2ACY Vz");
        portReMapB.put("Digi D3 to Cisco 2800","BL2b-3 ACY2I Vz");
        portReMapB.put("Pepwave B305 to Pepwave P6","BL2b-4 I2OKC T-Mobile");
        portReMapB.put("Pepwave P6 to Pepwave B305","BL2b-4 OKC2I T-Mobile");
        portReMapB.put("CradlePoint CP1 to CradlePoint CP5","BL2b-6 OKC2OKC Verizon to AT&T");
        portReMapB.put("CradlePoint CP5 to CradlePoint CP1","BL2b-6 OKC2OKC AT&T to Verizon");
        portReMapB.put("Pepwave P1 to Pepwave P2","BL2b-7 OKC2OKC AT&T to T-Mobile");
        portReMapB.put("Pepwave P2 to Pepwave P1","BL2b-7 OKC2OKC T-Mobile to AT&T");
        portReMapB.put("ACY to OKC","BL2b-8 Internet");
        portReMapB.put("Pepwave B305 to Pepwave P4","BL2b-9 I2ACY T-Mobile");
        portReMapB.put("Pepwave P4 to Pepwave B305","BL2b-9 ACY2I T-Mobile");
        portReMapB.put("Pepwave B305 to Pepwave P5","BL2b-10 I2ACY T-Mobile unencrypted");
        portReMapB.put("Pepwave P5 to Pepwave B305","BL2b-10 ACY2I T-Mobile unencrypted");

        // BL2c
/*      portReMapC.put("Pepwave B305 to Pepwave P4","BL2c-1a I2ACY T-Mobile");
        portReMapC.put("Pepwave P4 to Pepwave B305","BL2c-1a ACY2I T-Mobile");
        portReMapC.put("Pepwave B305 to Pepwave P5","BL2c-1b I2ACY T-Mobile unencrypted");
        portReMapC.put("Pepwave P5 to Pepwave B305","BL2c-1b ACY2I T-Mobile unencrypted");

        portReMapC.put("Pepwave B305 to Pepwave P4","BL2c-2a I2ACY T-Mobile");
        portReMapC.put("Pepwave P4 to Pepwave B305","BL2c-2a ACY2I T-Mobile");
        portReMapC.put("Pepwave B305 to Pepwave P5","BL2c-2b I2ACY T-Mobile unencrypted");
        portReMapC.put("Pepwave P5 to Pepwave B305","BL2c-2b ACY2I T-Mobile unencrypted");

        portReMapC.put("Pepwave B305 to Pepwave P4","BL2c-3a I2ACY T-Mobile");
        portReMapC.put("Pepwave P4 to Pepwave B305","BL2c-3a ACY2I T-Mobile");
        portReMapC.put("Pepwave B305 to Pepwave P5","BL2c-3b I2ACY T-Mobile");
        portReMapC.put("Pepwave P5 to Pepwave B305","BL2c-3b ACY2I T-Mobile");*/

        portReMapC.put("Pepwave P4 to Pepwave P5","BL2c-4 P42P5 ACY T-Mobile to T-Mobile");
        portReMapC.put("Pepwave P5 to Pepwave P4","BL2c-4 P52P4 ACY T-Mobile to T-Mobile");
        portReMapC.put("CradlePoint CP1 to CradlePoint CP5","BL2c-5 CP12CP5 OKC Verizon to Verizon");
        portReMapC.put("CradlePoint CP5 to CradlePoint CP1","BL2c-5 CP52CP1 OKC Verizon to Verizon");
        portReMapC.put("Pepwave P1 to Pepwave P2","BL2c-6 P12P2 OKC AT&T to AT&T");
        portReMapC.put("Pepwave P2 to Pepwave P1","BL2c-6 P22P1 OKC AT&T to AT&T");
        portReMapC.put("ACY to OKC","BL2c-8 Internet");

 /*       portReMapC.put("Cisco 2800 to Digi D1","BL2b-2 I2OKC AT&T");
        portReMapC.put("Digi D1 to Cisco 2800","BL2b-2 OKC2I AT&T");
        portReMapC.put("Cisco 2800 to Digi D3","BL2b-3 I2ACY Vz");
        portReMapC.put("Digi D3 to Cisco 2800","BL2b-3 ACY2I Vz");
        portReMapC.put("Pepwave B305 to Pepwave P6","BL2b-4 I2OKC T-Mobile");
        portReMapC.put("Pepwave P6 to Pepwave B305","BL2b-4 OKC2I T-Mobile");
        portReMapC.put("CradlePoint CP1 to CradlePoint CP5","BL2b-6 OKC2OKC Verizon to AT&T");
        portReMapC.put("CradlePoint CP5 to CradlePoint CP1","BL2b-6 OKC2OKC AT&T to Verizon");
        portReMapC.put("Pepwave P1 to Pepwave P2","BL2b-7 OKC2OKC AT&T to T-Mobile");
        portReMapC.put("Pepwave P2 to Pepwave P1","BL2b-7 OKC2OKC T-Mobile to AT&T");
        portReMapC.put("ACY to OKC","BL2b-8 Internet");
*/
            monthMap.put("Jan", "01");
            monthMap.put("Feb", "02");
            monthMap.put("Mar", "03");
            monthMap.put("Apr", "04");
            monthMap.put("May", "05");
            monthMap.put("Jun", "06");
            monthMap.put("Jul", "07");
            monthMap.put("Aug", "08");
            monthMap.put("Sep", "09");
            monthMap.put("Oct", "10");
            monthMap.put("Nov", "11");
            monthMap.put("Dec", "12");

    }
    
    public static void main(String[] args) throws IOException
    {
                  //  String zipFilePath = "C:\\Users\\Public\\FENSTest\\BL2 Results 0330 to 0401.zip";
        try
        {
            if (args.length < 2)
            {
                System.out.println("\nUsage: java -jar FensImport.jar http://hostname:port FullPathnameToZipFile");
                System.out.println("where FullPathnameToZipFile is the full pathname to the file to be imported");
                System.exit(1);
            }

            String readFilePath = args[1];

            isCsvOutOnly = args.length > 2 && args[2].toLowerCase().equals("-f");

            File rootFolder = readFilePath.toLowerCase().endsWith(".zip") ?  UnzipFiles.UnzipAndCopyLocal(readFilePath) : new File(readFilePath);
                    //new File("C:\\Users\\Public\\FENSTest\\BL2 Results 0423 to 0424\\xena"); //
            if (rootFolder == null)
            {
                System.out.println("Unable to establish root Folder from: " + readFilePath);
            }
            else
            {
                importCsvData(args[0], rootFolder);
            }
            System.out.println("\nCompleted");

        }
        catch (Exception ex)
        {
             ex.printStackTrace();
        }
    }

    private static void importCsvData(String hostname, File rootFolder ) throws Exception
    {
        BufferedReader csvReader= null;
        BufferedWriter outWriter = null;
        urlStringPing = hostname + urlPatternPing;
        urlStringXena = hostname + urlPatternXena;
        urlStringException = hostname + urlPatternException;

        StringBuffer sbXena = new StringBuffer();
        StringBuffer sbPing = new StringBuffer();
        StringBuffer sbException = new StringBuffer();
        StringBuffer sbCsvXena = new StringBuffer();
        StringBuffer sbCsvPing = new StringBuffer();

        List<String> fileList = new ArrayList<>();
        try
        {
            // Run through csv files and load/output
            File[] csvfiles = rootFolder.isFile() ? new File[]{rootFolder} : rootFolder.listFiles();

            if (csvfiles == null || csvfiles.length == 0)
            {
                System.out.println("No csv files to process, exiting");
                System.exit(1);
            }

            for (File csvfile : csvfiles)
            {
                String fullFilename = csvfile.getAbsolutePath();
                if (!fullFilename.endsWith(".csv"))
                    continue;
                fileList.add(fullFilename);
            }

            if (fileList.size() == 0)
            {
                System.out.println("No csv files to process, exiting");
                System.exit(1);
            }

            if (!isCsvOutOnly && !testConnection(hostname))
            {
                System.exit(1);
            }

            if (isCsvOutOnly)
            {
                String outFilename = rootFolder + "\\outData.export";
                Files.deleteIfExists(new File(outFilename).toPath());
                outWriter = new BufferedWriter(new FileWriter(outFilename, true));
                sbCsvXena.append(xenaHeaderCsv).append("\n");
            }

            int linecount = 0;
            for (String filename : fileList)
            {
                csvReader = new BufferedReader(new FileReader(filename));
                System.out.println("Loading " + filename);
                String row = null;
                String header = null;
                String fileType = null;
                try
                {
                    int[] oldNewseqerr = new int[]{0};
                    while ((row = csvReader.readLine()) != null)
                    {
                        if (header == null)
                        {
                            header = row;
                            if (header.startsWith("Date"))
                                fileType = "ping";
                            else if (filename.contains("VZW") || filename.contains("T-Mobile"))
                                fileType = "exception";
                            else
                                fileType = "xena";
                            continue;
                        }
                        String[] csvData = row.split(",");
                        if (csvData.length == 0)
                        {
                            System.out.println("Empty data row, rejected");
                            continue;
                        }
                        if (csvData.length < 5)
                        {
                            System.out.println("Bad data row, rejected: "+row);
                            continue;
                        }

                        DataRecordSet dataRecordSet = formatData(filename, fileType, csvData, oldNewseqerr);
                        //System.out.print("Line: "+ ++linecount + "\r");
                        if (dataRecordSet.csvRecordXena != null)
                        {
                            sbCsvXena.append(dataRecordSet.csvRecordXena).append("\n");
                            if (--blockCounterCsvXena <= 0)
                            {
                                sbCsvXena = saveCsvData("xena", sbCsvXena, outWriter);
                            }
                        }

                        if (dataRecordSet.jsonRecordXena != null)
                        {
                            sbXena.append(dataRecordSet.jsonRecordXena);
                            if (--blockCounterXena <= 0)
                            {
                                sbXena = sendJsonData("xena", sbXena.toString());
                            }
                        }

                        if (dataRecordSet.jsonRecordPing != null)
                        {
                            sbPing.append(dataRecordSet.jsonRecordPing);
                            if (--blockCounterPing <= 0)
                            {
                                sbPing = sendJsonData("ping", sbPing.toString());
                            }
                        }
                        if (dataRecordSet.jsonRecordException != null)
                        {
                            sbException.append(dataRecordSet.jsonRecordException);
                            if (--blockCounterException <= 0)
                            {
                                sbException = sendJsonData("exception", sbException.toString());
                            }
                        }

                    }

                    if (sbCsvXena.length() > 0)
                    {
                        sbCsvXena = saveCsvData("xena", sbCsvXena, outWriter);
                    }

                    if (sbXena.length() > 0)
                    {
                        sbXena = sendJsonData("xena", sbXena.toString());
                    }
                    if (sbPing.length() > 0)
                    {
                        sbPing = sendJsonData("ping", sbPing.toString());
                    }
                    if (sbException.length() > 0)
                    {
                        sbException = sendJsonData("exception", sbException.toString());
                    }

                }
                catch (Exception ex)
                {
                    System.out.println("row: " + row);
                    ex.printStackTrace();
                    throw ex;
                }
            }
        }
        finally
        {
            if (csvReader != null)
                csvReader.close();
            if (outWriter != null)
                outWriter.close();
        }

    }

    private static StringBuffer saveCsvData(String fileType, StringBuffer sbDataBuffer, BufferedWriter outWriter) throws Exception
    {
        if (fileType.equals("xena"))
        {
            outWriter.append(sbDataBuffer.toString());
            System.out.print("Exported " + sbDataBuffer.length() + " xena Bytes\r");
            blockCounterCsvXena = BLOCK_SIZE;
        }
        return new StringBuffer();
    }

    private static StringBuffer sendJsonData(String fileType, String sbData) throws Exception
    {
        if (fileType.equals("xena"))
        {
            int status = sendJsonRequest(urlStringXena, sbData);
            System.out.println("Loaded " + sbData.length() + " xena Bytes " + status);
            blockCounterXena = BLOCK_SIZE;
        }
        if (fileType.equals("ping"))
        {
            int status = sendJsonRequest(urlStringPing, sbData);
            System.out.println("Loaded " + sbData.length() + " ping Bytes " + status);
            blockCounterPing = BLOCK_SIZE;
        }
        if (fileType.equals("exception"))
        {
            int status = sendJsonRequest(urlStringException, sbData);
            System.out.println("Loaded " + sbData.length() + " exception Bytes " + status);
            blockCounterException = BLOCK_SIZE;
        }
        return new StringBuffer();
    }

    private static DataRecordSet formatData(String filename, String fileType, String[] csvData, int[] oldNewseqerr) throws Exception
    {
        DataRecordSet dataRecordSet =  new DataRecordSet();

        String jsonRecord;

        int status =0;
        try
        {

        Map<String,String> portMap = null;
        Map<String,String> portReMap = null ;

        String testGroup = "";
        if (filename.contains("BL2a")) { portMap = portMapA; portReMap = portReMapA; }
        else if (filename.contains("BL2b")) { portMap = portMapB; portReMap = portReMapB; }
        else  { portMap = portMapC; portReMap = portReMapC; }

        if (fileType.equals("ping"))
        {
                String date = csvData[0];
                String yearStr="";
                String monthStr="";
                String dayStr="";

                if (date.contains("/"))
                {
                    String[] ymdSplit = date.split("/");
                    yearStr = ymdSplit[2];
                    if (yearStr.length() == 2)
                        yearStr = "20" + yearStr;
                    monthStr = ymdSplit[0];
                    dayStr = ymdSplit[1];
                }
                else if (date.contains("-"))
                {
                    String[] ymdSplit = date.split("-");
                    yearStr = ymdSplit[2];
                    if (yearStr.length() == 2)
                        yearStr = "20" + yearStr;
                    monthStr = monthMap.get(ymdSplit[1]);
                    dayStr = ymdSplit[0];
                }
                else
                {
                    String[] ymdSplit = date.split(" ");
                    yearStr = ymdSplit[2];
                    monthStr = monthMap.get(ymdSplit[1]);
                    dayStr = ymdSplit[0];
                }

                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                int day = Integer.parseInt(dayStr);

                if (monthStr.length() == 1)
                    monthStr = "0" + monthStr;
                if (dayStr.length() == 1)
                    dayStr = "0" + dayStr;
                String time = csvData[1];

                String[] hmsSplit = time.split(":");

               // String time = timed;

                Calendar c = Calendar.getInstance();
                c.set(year,month-1,day);
                int dayofweek = c.get(Calendar.DAY_OF_WEEK);

                String hourStr = hmsSplit[0];
                String minuteStr = hmsSplit[1];
                String secondStr = hmsSplit[2];

                int hour = Integer.parseInt(hourStr);
                int minute = Integer.parseInt(minuteStr);
                int second = Integer.parseInt(secondStr);

                if (hourStr.length() == 1)
                    hourStr = "0" + hourStr;
                if (minuteStr.length() == 1)
                    minuteStr = "0" + minuteStr;
                if (secondStr.length() == 1)
                    secondStr = "0" + secondStr;

                String ip = csvData[2];

                String srcToDest = null;
                // Default 6 columns
                String bytesStr = csvData[3];
                String rttStr = csvData[4];
                String ttlStr = csvData[5];

                if (csvData.length == 6)
                {
                    if (filename.toLowerCase().contains("toddr"))
                    {
                        srcToDest = "Cable – FIOS VA to internet";
                    }
                    else if (filename.toLowerCase().contains("google"))
                    {
                        srcToDest = "Cable – Google fiber to Internet";
                    }                // Mark cable  ->    Spectrum cable FL
                    else if (filename.contains("Teresa"))
                    {
                        srcToDest = "cable - Comcast NJ to internet";
                    }
                    else if (filename.contains("FIOS"))
                    {
                        srcToDest = "cable - FIOS to internet";
                    }
                    else
                    {
                        srcToDest = "ACY to OKC";
                    }
                }
                else
                {
                    if (csvData.length < 9)
                    {
                        System.out.println("Not enough data for valid record");
                        return dataRecordSet;
                    }

                    bytesStr = csvData[5];
                    rttStr = csvData[6];
                    ttlStr = csvData[7];

                    if (filename.contains("FIOS 900"))
                    {
                        srcToDest = "Cable - FIOS 900 to internet";
                    }
                    else if (filename.contains("FIOS 150"))
                    {
                        srcToDest = "Cable - FIOS 150 to internet";
                    }
                    else if (filename.contains("FIOS 75"))
                    {
                        srcToDest = "Cable - FIOS 75 to internet";
                    }
                    else if (filename.contains("Mark cable"))
                    {
                        srcToDest = "Cable – Spectrum cable FL to internet";
                    }
                    else if (filename.contains("Cox"))
                    {
                        srcToDest = "Cable - Cox to internet";
                    }
                }

                int bytes = bytesStr.equals("timedout") || bytesStr.equals("unreach") ? -1 : Integer.parseInt(bytesStr);

                int prtt = floatToInt(rttStr);

                int ttl = floatToInt(ttlStr);

                String timestamp = yearStr + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minuteStr + ":" + secondStr;  //"2020-02-01 01:02:03";

                dataRecordSet.jsonRecordPing = String.format(pingPatternJson, UUID.randomUUID().toString(), timestamp, time, dayofweek, year, month, day, hour, minute, second,
                        ip, srcToDest, bytes, prtt, ttl);

                if (srcToDest.equals("ACY to OKC"))
                {
                    srcToDest = portReMap.get(srcToDest);
                    // Add Xena record containing prtt data
                    dataRecordSet.jsonRecordXena = String.format(xenaPatternJson, UUID.randomUUID().toString(), timestamp, time, year, month, day, hour, minute, second,
                            "ACY", "ACY", 0, "OKC", "OKC", 1, srcToDest,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, prtt, 0, 0,
                            0.0, 0,
                            0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                            0, 0
                    );
                }
            }
            else
            {
                XenaTimestamp xenaTimestamp = buildXenaTimestamp(csvData[0]);

                Boolean isTMobile = filename.contains("T-Mobile");
                Boolean isVZW = filename.contains("VZW");
                Boolean isXena = fileType.equals("xena");
                Boolean isException = fileType.equals("exception");

                String srcport = csvData[1];

                // D3-VZW goes to Exception Index
                String srcname="";
                srcname = portMap.get(srcport);

                int sid = 0;
                if (isXena|| isTMobile)
                    sid = Integer.parseInt(csvData[2]);

                String destport="P-1-5-6";
                if (isXena || isTMobile)
                    destport= csvData[3];

                String destname;
                destname = portMap.get(destport);

                int tid = 0;
                if (isXena || isTMobile)
                    tid = Integer.parseInt(csvData[4]);

                String srctodest = srcname + " to " + destname;
                if (portReMap.containsKey(srctodest))
                    srctodest = portReMap.get(srctodest);
                else
                    System.out.println("No port remap for: " +srctodest);

                int i = 4;
                if (isVZW)
                    i = 1;

                int txl1bps = Integer.parseInt(csvData[++i]);
                int txbps = Integer.parseInt(csvData[++i]);
                int txfps = Integer.parseInt(csvData[++i]);
                long txbytes = Long.parseLong(csvData[++i]);
                long txframes = Long.parseLong(csvData[++i]);
                int rxl1bps = Integer.parseInt(csvData[++i]);
                int rxbps = Integer.parseInt(csvData[++i]);
                int rxfps = Integer.parseInt(csvData[++i]);
                long rxbytes = Long.parseLong(csvData[++i]);
                long rxframes = Long.parseLong(csvData[++i]);

                long txnonp1dbytes = isXena ? Long.parseLong(csvData[++i]) : 0;
                long txnonp1dframes = isXena ? Long.parseLong(csvData[++i]) : 0;
                long rxnonp1dbytes = isXena ? Long.parseLong(csvData[++i]) : 0;
                long rxnonp1dframes = isXena ? Long.parseLong(csvData[++i]) : 0;

                int rxfcserrors = Integer.parseInt(csvData[++i]);
                int rxseqerr = Integer.parseInt(csvData[++i]);

                int packetlossps = 0;
                if (isXena || isException)
                    packetlossps = Math.abs(rxseqerr - oldNewseqerr[0]);
                oldNewseqerr[0] = rxseqerr;

                int rxmiserr = Integer.parseInt(csvData[++i]);
                int rxp1derr = Integer.parseInt(csvData[++i]);

                String rxBerStr = csvData[++i];

                // rxber is always nano seconds so make zero
                Float rxber = 0.0f;

                int rxbercurr = floatToInt(csvData[++i]);


                Float latencycurr = Float.parseFloat(csvData[++i]);
                Float latencycurrmin = Float.parseFloat(csvData[++i]);
                Float latencycurrmax = Float.parseFloat(csvData[++i]);

                Float latencyavg = Float.parseFloat(csvData[++i]);
                Float latencymin = Float.parseFloat(csvData[++i]);
                Float latencymax = Float.parseFloat(csvData[++i]);

                Float jittercurr = Float.parseFloat(csvData[++i]);
                Float jittercurrmin = Float.parseFloat(csvData[++i]);
                Float jittercurrmax = Float.parseFloat(csvData[++i]);

                Float jitteravg = Float.parseFloat(csvData[++i]);
                Float jittermin = Float.parseFloat(csvData[++i]);
                Float jittermax = Float.parseFloat(csvData[++i]);

                int rxpauseframes = 0;
                if (isXena)
                    rxpauseframes = Integer.parseInt(csvData[++i]);

                int rxpfcframes = 0;
                if (isXena)
                    rxpfcframes = Integer.parseInt(csvData[++i]);

                if (isCsvOutOnly)
                {
                    String csvRecordSet = String.format(xenaPatternCsv, xenaTimestamp.Timestamp,
                            srcport, srcname, sid, destport, destname, tid, srctodest,
                            txl1bps, txbps, txfps, txbytes, txframes,
                            rxl1bps, rxbps, rxfps, rxbytes, rxframes,
                            txnonp1dbytes, txnonp1dframes, rxnonp1dbytes, rxnonp1dframes,
                            rxfcserrors, rxseqerr, packetlossps, 0, rxmiserr, rxp1derr,
                            rxber, rxbercurr,
                            latencycurr, latencycurrmin, latencycurrmax, latencyavg, latencymin, latencymax,
                            jittercurr, jittercurrmin, jittercurrmax, jitteravg, jittermin, jittermax,
                            rxpauseframes, rxpfcframes);

                    dataRecordSet.csvRecordXena = csvRecordSet;
                }
                else
                {
                    String jsonRecordStr = String.format(xenaPatternJson, UUID.randomUUID().toString(), xenaTimestamp.Timestamp, xenaTimestamp.Time, xenaTimestamp.Year, xenaTimestamp.Month,
                            xenaTimestamp.Day, xenaTimestamp.Hour, xenaTimestamp.Minute, xenaTimestamp.Second,
                            srcport, srcname, sid, destport, destname, tid, srctodest,
                            txl1bps, txbps, txfps, txbytes, txframes,
                            rxl1bps, rxbps, rxfps, rxbytes, rxframes,
                            txnonp1dbytes, txnonp1dframes, rxnonp1dbytes, rxnonp1dframes,
                            rxfcserrors, rxseqerr, packetlossps, 0, rxmiserr, rxp1derr,
                            rxber, rxbercurr,
                            latencycurr, latencycurrmin, latencycurrmax, latencyavg, latencymin, latencymax,
                            jittercurr, jittercurrmin, jittercurrmax, jitteravg, jittermin, jittermax,
                            rxpauseframes, rxpfcframes);

                    if (isXena)
                        dataRecordSet.jsonRecordXena = jsonRecordStr;
                    else
                        dataRecordSet.jsonRecordException = jsonRecordStr;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
        return dataRecordSet;
    }

    private static int floatToInt(String floatstr)
    {
        String[] floatstrSplit = floatstr.split("\\.");
        int intVal = floatstr.equals("timedout") || floatstr.equals("unreach") ? -1 : Integer.parseInt(floatstrSplit[0]);
        if (floatstrSplit.length > 1 && Integer.parseInt(floatstrSplit[1]) >= 5)
            intVal++;
        return intVal;
    }


    private static XenaTimestamp buildXenaTimestamp(String timestampIn)
    {
        String[] dateTime = timestampIn.split("-");
        XenaTimestamp xenaTimestamp = new XenaTimestamp();

        xenaTimestamp.Year = Integer.parseInt(dateTime[0].substring(0, 4));
        xenaTimestamp.Month = Integer.parseInt(dateTime[0].substring(4, 6));
        xenaTimestamp.Day = Integer.parseInt(dateTime[0].substring(6, 8));

        xenaTimestamp.Hour = Integer.parseInt(dateTime[1].substring(0, 2));
        xenaTimestamp.Minute = Integer.parseInt(dateTime[1].substring(2, 4));
        xenaTimestamp.Second = Integer.parseInt(dateTime[1].substring(4, 6));

        xenaTimestamp.Time = dateTime[1].substring(0, 2) + ":" + dateTime[1].substring(2, 4) + ":" + dateTime[1].substring(4, 6);
        xenaTimestamp.Timestamp = dateTime[0].substring(0, 4) + "-" + dateTime[0].substring(4, 6) + "-" + dateTime[0].substring(6, 8) + " " + xenaTimestamp.Time;   //"2020-02-01 01:02:03";

        return xenaTimestamp;
    }


    private static Boolean testConnection(String urlString) throws Exception
    {
        HttpURLConnection con = null;
   //     String[] urlSplit = urlString.split("/");
   //     String[] hostSplit = urlSplit[2].split(":");
        try
        {
            System.out.println("\nEstablishing connection to " + urlString + "...");

            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.connect();
        }
        catch(ConnectException ce)
        {
            System.out.println("\nUnable to connect to " + urlString);
            System.out.println("\nPossible causes:\n");
            System.out.println("    - The provider is refusing the connection\n");
            System.out.println("    - If VPN is required, The VPN to the provider cannot be established\n");
            System.out.println("    - Your organization is actively blocking the attempt to connect");
            return false;
        }
        finally
        {
            if (con != null) con.disconnect();
        }
        return true;
    }

    private static int sendJsonRequest(String urlString, String jsonRecord) throws Exception
    {
        HttpURLConnection con = null;
        BufferedReader in = null;
        try
        {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            byte[] outJson = jsonRecord.getBytes(StandardCharsets.UTF_8);

            int length = outJson.length;
            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty("Content-Type", "application/x-ndjson; charset=UTF-8");
            con.connect();
            try (OutputStream os = con.getOutputStream())
            {
                os.write(outJson);
            }

            int status = con.getResponseCode();
            return status;
        }
        finally
        {
            if (in != null) in.close();
            if (con != null) con.disconnect();
        }
    }
}
class DataRecordSet
{
    public String jsonRecordXena;
    public String jsonRecordPing;
    public String jsonRecordException;
    public String csvRecordXena;
    public String csvRecordPing;
}

class XenaTimestamp
{
      public int Year;
      public int Month;
      public int Day;
      public int Hour;
      public int Minute;
      public int Second;
      public String Timestamp;
      public String Time;
}