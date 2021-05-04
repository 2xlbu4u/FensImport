package gov.faa.fens;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

public class DataManager
{
    private static int blockCounter;
    protected FileType _FileType;

    public DataManager(FileType fileType) throws Exception
    {
        //_FileType = fileType;
    }



    public static void FormatDataRow(String[] csvData, DataRecordSet dataRecordSet) throws Exception
    {
        String Record;
        dataRecordSet.SetOutRecordHeader();
        if (dataRecordSet.OutFileType instanceof PcapFileType)
        {

            Record = String.format(dataRecordSet.OutFileType.PatternCsv, csvData[0],csvData[1],csvData[2],csvData[3],csvData[4]);
            return;
        }
         //dataRecordSet.OutFileType.Record = String.format(dataRecordSet.OutFileType.PatternCsv,

        if (csvData.length < 6 || (csvData.length > 6 && csvData.length < 9))
        {
            System.out.println("Not enough data for valid record");
            return;
        }
        try
        {
            Map<String, String> portMap = null;
            Map<String, String> portReMap = null;

            String testGroup = "";
            if (dataRecordSet.Filename.contains("BL2a"))
            {
                portMap = MapManager.portMapA;
                portReMap = MapManager.portReMapA;
            }
            else if (dataRecordSet.Filename.contains("BL2b"))
            {
                portMap = MapManager.portMapB;
                portReMap = MapManager.portReMapB;
            }
            else
            {
                portMap = MapManager.portMapC;
                portReMap = MapManager.portReMapC;
            }


            if (dataRecordSet.OutFileType instanceof PingFileType || dataRecordSet.OutFileType instanceof PLossFileType)
            {
                String date = csvData[0];
                String yearStr = "";
                String monthStr = "";
                String dayStr = "";

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
                    monthStr = MapManager.monthMap.get(ymdSplit[1]);
                    dayStr = ymdSplit[0];
                }
                else
                {
                    String[] ymdSplit = date.split(" ");
                    yearStr = ymdSplit[2];
                    monthStr = MapManager.monthMap.get(ymdSplit[1]);
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
                c.set(year, month - 1, day);
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

                if (hourStr.equals("24"))
                    hourStr = "00";

                String ip = csvData[2];

                String srcToDest = null;
                // Default 6 columns
                String bytesStr = csvData[3];
                String rttStr = csvData[4];
                String ttlStr = csvData[5];

                if (csvData.length > 6)
                {
                    bytesStr = csvData[5];
                    rttStr = csvData[6];
                    ttlStr = csvData[7];
                }

                String[] splitName = dataRecordSet.Filename.split("_");
                String[] derivedNameParts = splitName[0].split("\\\\");
                String derivedName = derivedNameParts[derivedNameParts.length - 1];

                srcToDest = derivedName.split("\\.")[0];

                int bytes = bytesStr.equals("error") || bytesStr.equals("timeout") || bytesStr.equals("timedout") || bytesStr.equals("unreach") ? -1 : Integer.parseInt(bytesStr);

                int prtt = 0;//floatToInt(rttStr);

                int ttl = 0;//floatToInt(ttlStr);

                String timestamp = yearStr + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minuteStr + ":" + secondStr;  //"2020-02-01 01:02:03";

                if (dataRecordSet.OutFileType != null)
                {
                    // TODO: Move to poly method
                    if (dataRecordSet.OutFileType instanceof PLossFileType)
                    {
                        int isLoss =  prtt == -1 ? 1 : 0;
                        Record = String.format(dataRecordSet.OutFileType.PatternJson, UUID.randomUUID().toString(), timestamp, dayofweek, ip, srcToDest, isLoss);
                    }
                    else if (dataRecordSet.OutFileType instanceof PingFileType)
                    {
                        Record = String.format(dataRecordSet.OutFileType.PatternJson, UUID.randomUUID().toString(), timestamp, dayofweek, ip, srcToDest, bytes, prtt, ttl);
                    }
                }
                else
                {
  //                  dataRecordSet.OutFileType.Record = String.format(dataRecordSet.OutFileType.PatternJson, UUID.randomUUID().toString(), timestamp, time, dayofweek, year, month, day, hour, minute, second,
     //                       ip, srcToDest, bytes, prtt, ttl);
// Handle double load
//                    if (srcToDest.equals("ACY to OKC"))
//                    {
//                        srcToDest = portReMap.get(srcToDest);
//                        // Add Xena record containing prtt data
//                        dataRecordSet.OutFileTypeXena.Record = String.format(dataRecordSet.OutFileTypeXena.PatternCsv, UUID.randomUUID().toString(), timestamp, time, year, month, day, hour, minute, second,
//                                "ACY", "ACY", 0, "OKC", "OKC", 1, srcToDest,
//                                0, 0, 0, 0, 0,
//                                0, 0, 0, 0, 0,
//                                0, 0, 0, 0,
//                                0, 0, 0, prtt, 0, 0,
//                                0.0, 0,
//                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
//                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
//                                0, 0
//                        );
//                    }
                }
            }
            else if (dataRecordSet.OutFileType instanceof XenaFileType)
            {
                XenaTimestamp xenaTimestamp = null;//buildXenaTimestamp(csvData[0]);

                Boolean isTMobile = dataRecordSet.Filename.contains("T-Mobile");
                Boolean isVZW = dataRecordSet.Filename.contains("VZW");
                Boolean isXena = dataRecordSet.OutFileType instanceof XenaFileType;
                Boolean isException = dataRecordSet.Filename.equals("exception");

                String srcport = csvData[1];

                // D3-VZW goes to Exception Index
                String srcname = "";
                srcname = portMap.get(srcport);

                int sid = 0;
                if (isXena || isTMobile)
                    sid = Integer.parseInt(csvData[2]);

                String destport = "P-1-5-6";
                if (isXena || isTMobile)
                    destport = csvData[3];

                String destname;
                destname = portMap.get(destport);

                int tid = 0;
                if (isXena || isTMobile)
                    tid = Integer.parseInt(csvData[4]);

                String srctodest = srcname + " to " + destname;
                if (portReMap.containsKey(srctodest))
                    srctodest = portReMap.get(srctodest);
                else
                    System.out.println("No port remap for: " + srctodest);

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
                    packetlossps = Math.abs(rxseqerr - dataRecordSet.OldNewseqerr[0]);
                dataRecordSet.OldNewseqerr[0] = rxseqerr;

                int rxmiserr = Integer.parseInt(csvData[++i]);
                int rxp1derr = Integer.parseInt(csvData[++i]);

                String rxBerStr = csvData[++i];

                // rxber is always nano seconds so make zero
                Float rxber = 0.0f;

                int rxbercurr = 0;//floatToInt(csvData[++i]);


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

                if (dataRecordSet.OutFileType != null)
                {
                    Record = String.format(dataRecordSet.OutFileType.PatternCsv, xenaTimestamp.Timestamp,
                            srcport, srcname, sid, destport, destname, tid, srctodest,
                            txl1bps, txbps, txfps, txbytes, txframes,
                            rxl1bps, rxbps, rxfps, rxbytes, rxframes,
                            txnonp1dbytes, txnonp1dframes, rxnonp1dbytes, rxnonp1dframes,
                            rxfcserrors, rxseqerr, packetlossps, 0, rxmiserr, rxp1derr,
                            rxber, rxbercurr,
                            latencycurr, latencycurrmin, latencycurrmax, latencyavg, latencymin, latencymax,
                            jittercurr, jittercurrmin, jittercurrmax, jitteravg, jittermin, jittermax,
                            rxpauseframes, rxpfcframes);
                }
                else
                {
                    Record =
                            String.format(dataRecordSet.OutFileType.PatternJson, UUID.randomUUID().toString(), xenaTimestamp.Timestamp, xenaTimestamp.Time, xenaTimestamp.Year, xenaTimestamp.Month,
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
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }

    }
    public static void ImportFileRows(DataRecordSet dataRecordSet) throws Exception
    {
        ArrayList<String[]> retList = new ArrayList<>();
        BufferedReader fileReader = new BufferedReader(new FileReader(dataRecordSet.Filename));
        dataRecordSet.InRows = new ArrayList<>();
        String row;
        while ((row = fileReader.readLine()) != null)
        {
            dataRecordSet.InRows.add(row);
        }
    }

/*    public static void exportFile(DataRecordSet dataRecordSet) throws Exception
    {
        dataRecordSet.OutFileType.ExportData(dataRecordSet);
    }*/

    public static List<DataRecordSet> ProcessInputFiles(DataRecordSet _dataRecordSet) throws Exception
    {
        List<DataRecordSet> recordSetList = new ArrayList<>();

        BufferedReader fileReader = null;
        FileType outFType = _dataRecordSet.OutFileType;
        List<String> fileList = new ArrayList<>();
        try
        {
            File rootFolder = _dataRecordSet.RootFolderOrFile;
            File[] filesToInspect = rootFolder.isFile() ? new File[]{rootFolder} : rootFolder.listFiles();

            if (filesToInspect == null || filesToInspect.length == 0)
            {
                System.out.println("No csv files to process, exiting");
                System.exit(1);
            }

            for (File nextFileToInspect : filesToInspect)
            {
                String fullFilename = nextFileToInspect.getAbsolutePath();
                if (outFType instanceof PcapFileType && !fullFilename.toLowerCase().contains("wireshark"))
                    continue;
                else if (!(outFType instanceof PcapFileType) && !fullFilename.endsWith(".csv"))
                    continue;
                fileList.add(fullFilename);
            }

            if (fileList.size() == 0)
            {
                System.out.println("No files to process, exiting");
                System.exit(1);
            }

            for (String nextFile : fileList)
            {
                DataRecordSet dataRecordSet = new DataRecordSet(_dataRecordSet);
                dataRecordSet.Filename = nextFile;

                System.out.println("Reading data from: " + dataRecordSet.Filename);

                fileReader = new BufferedReader(new FileReader(nextFile));

                String row = null;
                String header = null;
                int rowCount = 0;
                try
                {
                    dataRecordSet.OldNewseqerr = new int[]{0};
                    startReader:
                    while ((row = fileReader.readLine()) != null)
                    {
                        rowCount++;
                        if (_dataRecordSet.OutFileType instanceof PcapFileType)
                        {
                            if (row.length() == 0 || row.startsWith("No"))
                                continue;

                            // Packet info
                            String[] packetInfo = row.split("\\s+");
                            String timestamp = packetInfo[2] + " " + packetInfo[3];
                            if (!packetInfo[4].equals(MapManager.pcapSourceIP))
                            {
                                while (!row.startsWith("No"))
                                {
                                    // Not interested in these packets
                                    row = fileReader.readLine();
                                    if (row == null)
                                        break startReader;
                                    rowCount++;
                                }
                                continue;
                            }
                            String srcname = MapManager.pcapIpMap.get(packetInfo[4]);
                            String destname = MapManager.pcapIpMap.get(packetInfo[5]);
                            String srctodest =  String.format("%s to %s", srcname, destname);
                            // Blank row
                            row = fileReader.readLine();
                            rowCount++;
                            // Start of data
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < 8; i++)
                            {
                                // Only last 82 so skip first 2 lines
                                row = fileReader.readLine();
                                rowCount++;
                                if (i < 2)
                                    continue;
                                String[] datasplit =  row.split("\\s+");
                                List<String> dataRow = Arrays.asList(datasplit);
                                int startId = 1;
                                if (i == 2)
                                    startId = 11;
                                int endId = 17;
                                    if (i == 7)
                                        endId = 13;
                                if (i > 2 )
                                    sb.append(" ");
                                List<String> dataBytes = dataRow.subList(startId, endId);
                                sb.append(String.join(" ", dataBytes));
                            }
                            String datBytesField = sb.toString();
                            String normalRow = String.format("%s,%s,%s,%s,%s", timestamp, srcname, destname, srctodest, datBytesField);
                            dataRecordSet.InRows.add(normalRow);
                        }
                        else
                        {
                            if (header == null)
                            {
                                header = row;
                                continue;
                            }
                            String[] rowData = row.split(",");
                            if (rowData.length == 0)
                            {
                                System.out.println("Empty data row, rejected rowcount: " + rowCount);
                                continue;
                            }
                            if (rowData.length < 5)
                            {
                                System.out.println("Bad data row, rejected: rowcount: " +  rowCount);
                                continue;
                            }
                            dataRecordSet.InRows.add(row);
                        }
                    }

                    recordSetList.add(dataRecordSet);
                }
                catch(Exception ex)
                {
                    System.out.println("Bad data row, rejected: rowcount: " +  rowCount + "\n" + ex);
                    ex.printStackTrace();
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println("Bad ProcessInputFiles: " + ex);
            ex.printStackTrace();
        }
        finally
        {
            if (fileReader != null)
            {
                fileReader.close();
            }
        }

        return recordSetList;
    }

    public static void ExportData(FileType fileType, BufferedWriter outWriter) throws Exception
    {
        if (fileType != null && outWriter != null)
        {
            fileType.Sb.append("").append("\n");
            if (--fileType.BlockCounter <= 0)
            {
                outWriter.append(fileType.Sb.toString());
                System.out.println("Loaded " + fileType.Sb.length() + " Bytes ");
                fileType.BlockCounter = FileType.BLOCK_SIZE;
                fileType.Sb = new StringBuffer();
            }
        }
    }

    public static Boolean TestConnection(String urlString) throws Exception
    {
        HttpURLConnection con = null;
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

    private static StringBuffer sendJsonData(FileType fileType) throws Exception
    {
        String urlString = fileType.Hostname + fileType.UrlPattern;
        int status = sendJsonRequest(urlString, fileType.Sb.toString());
        System.out.println("Loaded " + fileType.Sb.length() + " Bytes " + status);
        fileType.BlockCounter = FileType.BLOCK_SIZE;
        return new StringBuffer();
    }

    public static void OutJsonData(FileType fileType) throws Exception
    {
        String Record = null;
        if (fileType != null && Record != null)
        {
            fileType.Sb.append(Record);//.append("\n");
            if (--fileType.BlockCounter <= 0)
            {
                fileType.Sb = sendJsonData(fileType);
            }
        }
    }
    public static void OutputData(DataRecordSet dataRecordSet) throws Exception
    {
        try
        {
            for (String row : dataRecordSet.InRows)
            {
                String[] csvData = row.split(",");
                DataManager.FormatDataRow(csvData, dataRecordSet);
                if (dataRecordSet.OutWriter != null)
                {
                    ExportData(dataRecordSet.OutFileType, dataRecordSet.OutWriter);
                }
                else
                {
                    OutJsonData(dataRecordSet.OutFileType);
                }
            }

            if (dataRecordSet.OutFileType.Sb.length() > 0)
            {
                if (dataRecordSet.OutWriter != null)
                {
                    dataRecordSet.OutWriter.append(dataRecordSet.OutFileType.Sb.toString());
                    System.out.println("Loaded " + dataRecordSet.OutFileType.Sb.length() + " Bytes ");
                    dataRecordSet.OutFileType.BlockCounter = FileType.BLOCK_SIZE;
                    dataRecordSet.OutFileType.Sb = new StringBuffer();

                }
                else
                    dataRecordSet.OutFileType.Sb = sendJsonData(dataRecordSet.OutFileType);
            }
        }
        finally
        {

        }
    }

}
