package gov.faa.fens;

import java.util.Map;

public class XenaFileType extends FileType
{
    XenaFileType()
    {

        PatternJson = "{\"index\":{\"_id\":\"%s\"}}\n{\"timestamp\":\"%s\",\"time\":\"%s\",\"year\":\"%d\",\"month\":\"%d\",\"day\":\"%d\",\"hour\":\"%d\",\"minute\":\"%d\",\"second\":\"%d\"," +
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
        UrlPattern = "/xenapr/_bulk?pretty";
        HeaderCsv = "timestamp,technology,testname,srcport,sid,destport,tid,txl1bps,txbps,txfps,txbytes,txframes,rxl1bps,rxbps,rxfps,rxbytes,rxframes,rxfcserrors,rxseqerr,rxpcklossratio,packetlossps,prtt,rxmiserr,rxp1derr,rxber,rxbercurr,latencycurr,latencycurrmin,latencycurrmax,latencyavg,latencymin,latencymax,jittercurr,jittercurrmin,jittercurrmax,jitteravg,jittermin,jittermax";
//                   Timestamp,SrcPort,SID,DestPort,TID,TxL1Bps,TxBps,TxFps,TxBytes,TxFrames,RxL1Bps,RxBps,RxFps,RxBytes,RxFrames,RxFcsErrors,RxSeqErr,RxPckLossRatio,                  RxMisErr,RxPldErr,RxBer,RxBerCurr,LatencyCurr,LatencyCurrMin,LatencyCurrMax,LatencyAvg,LatencyMin,LatencyMax,JitterCurr,JitterCurrMin,JitterCurrMax,JitterAvg,JitterMin,JitterMax

        PatternCsv =
            "\"%s\",\"%s\",\"%s\"," +
            "\"%s\",%d,\"%s\",%d," +
            "%d,%d,%d,%d,%d," +
            "%d,%d,%d,%d,%d,%d,%d,%d," +
            "%d,%d," +
            "%d,%d,%f,%d," +
            "%f,%f,%f,%f,%f,%f," +
            "%f,%f,%f,%f,%f,%f";
    }

    @Override
    public void PrepareForExport(DataRecordSet dataRecordSet)
    {
        // Convert InRows to OutRows
        Map<String, String> portMap = getPortMap(dataRecordSet);
        Map<String, String> portReMap = getPortReMap(dataRecordSet);
        String header = null;
        Boolean isTMobile = dataRecordSet.InFilename.contains("T-Mobile");
        Boolean isVZW = dataRecordSet.InFilename.contains("VZW");
//         Boolean isXena = dataRecordSet.OutFileType instanceof XenaFileType;
        Boolean isException = dataRecordSet.InFilename.equals("exception");
        dataRecordSet.OutFileSuffix = "xena_db_import.csv";

        for (String row : dataRecordSet.InRows)
        {
            String[] csvData = row.split(",");
            if (header == null)
            {
                header = String.join(",", csvData);
                //dataRecordSet.OutRows.add(header);
                continue;
            }
            XenaTimestamp xenaTimestamp = buildXenaTimestamp(csvData[0]);

            String srcport = csvData[1];
            // Ignore log start/end record
            if (srcport.equals("[Log initialized]") || srcport.equals("[Log stopped]"))
                continue;

            // D3-VZW goes to Exception Index
         //   String srcname = "";
        //  srcname = portMap.get(srcport);

            int sid = Integer.parseInt(csvData[2]);
/*            if (isTMobile)
                sid = Integer.parseInt(csvData[2]);*/

            String destport = csvData[3];;
/*            if (isTMobile)
                destport = csvData[3];*/

        //    String destname;
         //   destname = portMap.get(destport);

            int tid = Integer.parseInt(csvData[4]);
/*            if (isTMobile)
                tid = Integer.parseInt(csvData[4]);*/

        //    String srctodest = srcname + " to " + destname;
         //   if (portReMap.containsKey(srctodest))
         //       srctodest = portReMap.get(srctodest);
          //  else
          //      System.out.println("No port remap for: " + srctodest);

            int i = 4;
            if (isVZW)
                i = 1;

            int txl1bps = makeFloorZeroInt(csvData[++i]);
            int txbps = makeFloorZeroInt(csvData[++i]);
            int txfps = makeFloorZeroInt(csvData[++i]);
            long txbytes = makeFloorZeroLong(csvData[++i]);
            long txframes = makeFloorZeroLong(csvData[++i]);
            int rxl1bps = makeFloorZeroInt(csvData[++i]);
            int rxbps = makeFloorZeroInt(csvData[++i]);
            int rxfps = makeFloorZeroInt(csvData[++i]);
            long rxbytes = makeFloorZeroLong(csvData[++i]);
            long rxframes = makeFloorZeroLong(csvData[++i]);

           // Boolean hasnonpid = !srcport.startsWith("P-0-5");

/*            long txnonp1dbytes = hasnonpid ? Long.parseLong(csvData[++i]) : 0;
            long txnonp1dframes = hasnonpid ? Long.parseLong(csvData[++i]): 0;
            long rxnonp1dbytes = hasnonpid ? Long.parseLong(csvData[++i]) :0;
            long rxnonp1dframes = hasnonpid ? Long.parseLong(csvData[++i]) :0;*/

            int rxfcserrors = makeFloorZeroInt(csvData[++i]);
            int rxseqerr = makeFloorZeroInt(csvData[++i]);
            int rxpcklossratio = makeFloorZeroInt(csvData[++i]);

            int packetlossps = 0;
            if (isException)
                packetlossps = Math.abs(rxseqerr - dataRecordSet.OldNewseqerr[0]);

            dataRecordSet.OldNewseqerr[0] = rxseqerr;

            int rxmiserr = makeFloorZeroInt(csvData[++i]);
            int rxp1derr = makeFloorZeroInt(csvData[++i]);

            String rxBerStr = csvData[++i];

            // rxber is always nano seconds so make zero
            Float rxber = 0.0f;

            int rxbercurr = makeFloorZeroInt(csvData[++i]);

            Float latencycurr = makeFloorZeroFloat(csvData[++i]);
            Float latencycurrmin = makeFloorZeroFloat(csvData[++i]);
            Float latencycurrmax = makeFloorZeroFloat(csvData[++i]);

            Float latencyavg = makeFloorZeroFloat(csvData[++i]);
            Float latencymin = makeFloorZeroFloat(csvData[++i]);
            Float latencymax = makeFloorZeroFloat(csvData[++i]);

            Float jittercurr = makeFloorZeroFloat(csvData[++i]);
            Float jittercurrmin = makeFloorZeroFloat(csvData[++i]);
            Float jittercurrmax = makeFloorZeroFloat(csvData[++i]);

            Float jitteravg = makeFloorZeroFloat(csvData[++i]);
            Float jittermin = makeFloorZeroFloat(csvData[++i]);
            Float jittermax = makeFloorZeroFloat(csvData[++i]);

        //    int rxpauseframes = 0;
        //    rxpauseframes = Integer.parseInt(csvData[++i]);

        //    int rxpfcframes = 0;
        //    rxpfcframes = Integer.parseInt(csvData[++i]);


            String csvOutRecord = String.format(PatternCsv,
                    xenaTimestamp.Timestamp,
                    dataRecordSet.Technology, dataRecordSet.Testname,
                    srcport, sid, destport, tid,
                    txl1bps, txbps, txfps, txbytes, txframes,
                    rxl1bps, rxbps, rxfps, rxbytes, rxframes,rxfcserrors, rxseqerr,rxpcklossratio,
                    packetlossps, 0,
                    rxmiserr, rxp1derr, rxber, rxbercurr,
                    latencycurr, latencycurrmin, latencycurrmax, latencyavg, latencymin, latencymax,
                    jittercurr, jittercurrmin, jittercurrmax, jitteravg, jittermin, jittermax);

            if (dataRecordSet.OutRows.size() == 0)
                dataRecordSet.OutRows.add(HeaderCsv +"\n");
            dataRecordSet.OutRows.add(csvOutRecord+"\n");

        }

/*        PatternCsv =
            "\"%s\"," +
            "\"%s\",\"%s\"," +
            "\"%s\",%d,\"%s\",%d," +
            "%d,%d,%d,%d,%d," +
            "%d,%d,%d,%d,%d,%d,%d,%d," +
            "%d,%d," +
            "%d,%d,%f,%d," +
            "%f,%f,%f,%f,%f,%f," +
            "%f,%f,%f,%f,%f,%f";*/

    }
    private static int makeFloorZeroInt(String data)
    {
        return data.contains("E-") ? 0 : Integer.parseInt(data);
    }

    private static long makeFloorZeroLong(String data)
    {
        return data.contains("E-") ? 0 : Long.parseLong(data);
    }

    private static float makeFloorZeroFloat(String data)
    {
        return data.contains("E-") ? 0 : Float.parseFloat(data);
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


    private static XenaTimestamp buildXenaTimestampNew(String timestampIn)
    {
        timestampIn = timestampIn.replace("\"","");
        XenaTimestamp xenaTimestamp = new XenaTimestamp();
 /*       String[] datetimeSplit = timestampIn.split(" ");
        xenaTimestamp.Date = datetimeSplit[0];
        xenaTimestamp.Time = datetimeSplit[1];

        xenaTimestamp.Timestamp =

     //   String[] dateSplits = datetimeSplit[0].split("-");
        String[] timeSplits = datetimeSplit[1].split(":");

        xenaTimestamp.Year = Integer.parseInt(dateSplits[0]);
        String month = (dateSplits[1].startsWith("0")) ? dateSplits[1].substring(1) : dateSplits[1];
        xenaTimestamp.Month = Integer.parseInt(month);
        String day = (dateSplits[2].startsWith("0")) ? dateSplits[2].substring(1) : dateSplits[2];
        xenaTimestamp.Day = Integer.parseInt(day);


        xenaTimestamp.Hour = Integer.parseInt(timeSplits[0]);
        xenaTimestamp.Minute = Integer.parseInt(timeSplits[1]);
        xenaTimestamp.Second = Integer.parseInt(timeSplits[2]);*/


        return xenaTimestamp;
    }
}
