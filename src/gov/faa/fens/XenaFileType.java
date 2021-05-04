package gov.faa.fens;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

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
        HeaderCsv = "timestamp,srcport,srcname,sid,destport,destname,tid,srctodest,txl1bps,txbps,txfps,txbytes,txframes,rxl1bps,rxbps,rxfps,rxbytes,rxframes,rxfcserrors,rxseqerr,rxpcklossratio,packetlossps,prtt,rxmiserr,rxp1derr,rxber,rxbercurr,latencycurr,latencycurrmin,latencycurrmax,latencyavg,latencymin,latencymax,jittercurr,jittercurrmin,jittercurrmax,jitteravg,jittermin,jittermax";
//                   Timestamp,SrcPort,        SID,DestPort,         TID,          TxL1Bps,TxBps,TxFps,TxBytes,TxFrames,RxL1Bps,RxBps,RxFps,RxBytes,RxFrames,RxFcsErrors,RxSeqErr,RxPckLossRatio,                  RxMisErr,RxPldErr,RxBer,RxBerCurr,LatencyCurr,LatencyCurrMin,LatencyCurrMax,LatencyAvg,LatencyMin,LatencyMax,JitterCurr,JitterCurrMin,JitterCurrMax,JitterAvg,JitterMin,JitterMax


        PatternCsv = "\"%s\"," +
            "\"%s\",\"%s\",%d,\"%s\",\"%s\",%d,\"%s\"," +
            "%d,%d,%d,%d,%d," +
            "%d,%d,%d,%d,%d,%d,%d,%d," +
            "%f,%d," +
            "%d,%d,%d,%d," +
            "%f,%f,%f,%f,%f,%f," +
            "%f,%f,%f,%f,%f,%f," ;
    }

    @Override
    public void FormatDataRows(DataRecordSet dataRecordSet)
    {
        // Convert InRows to OutRows

        Map<String, String> portMap = getPortMap(dataRecordSet);
        Map<String, String> portReMap = getPortReMap(dataRecordSet);
        String header = null;
        Boolean isTMobile = dataRecordSet.Filename.contains("T-Mobile");
        Boolean isVZW = dataRecordSet.Filename.contains("VZW");
//         Boolean isXena = dataRecordSet.OutFileType instanceof XenaFileType;
        Boolean isException = dataRecordSet.Filename.equals("exception");

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
            String srcname = "";
            srcname = portMap.get(srcport);

            int sid = 0;
            if (isTMobile)
                sid = Integer.parseInt(csvData[2]);

            String destport = "P-1-5-6";
            if (isTMobile)
                destport = csvData[3];

            String destname;
            destname = portMap.get(destport);

            int tid = 0;
            if (isTMobile)
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
//txnonp1dbytes,txnonp1dframes,rxnonp1dbytes,rxnonp1dframes
            Boolean hasnonpid = !srcport.startsWith("P-0-5");

            long txnonp1dbytes = hasnonpid ? Long.parseLong(csvData[++i]) : 0;
            long txnonp1dframes = hasnonpid ? Long.parseLong(csvData[++i]): 0;
            long rxnonp1dbytes = hasnonpid ? Long.parseLong(csvData[++i]) :0;
            long rxnonp1dframes = hasnonpid ? Long.parseLong(csvData[++i]) :0;

            int rxfcserrors = Integer.parseInt(csvData[++i]);
            int rxseqerr = Integer.parseInt(csvData[++i]);
            int rxpcklossratio = Integer.parseInt(csvData[++i]);

            int packetlossps = 0;
            if (isException)
                packetlossps = Math.abs(rxseqerr - dataRecordSet.OldNewseqerr[0]);
            dataRecordSet.OldNewseqerr[0] = rxseqerr;

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
        //    rxpauseframes = Integer.parseInt(csvData[++i]);

            int rxpfcframes = 0;
        //    rxpfcframes = Integer.parseInt(csvData[++i]);

            if (dataRecordSet.OutFileType != null)
            {
                String Record = String.format(dataRecordSet.OutFileType.PatternCsv, xenaTimestamp.Timestamp,
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
                String Record =
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
}
