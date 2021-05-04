package gov.faa.fens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

public class HistoFileType extends FileType
{
    public static String TitleHeader = "timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount\n";

    HistoFileType()
    {
        HeaderCsv = "CP1,,CP5,,P1,,P2,,P4,,P5,,\n" +
                    "timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount\n";
        PatternCsv = "%f,%d,%f,%d,%f,%d,%f,%d,%f,%d,%f,%d\n";
    }

    @Override
    public void FormatDataRows(DataRecordSet dataRecordSet) throws Exception
    {
        BufferedWriter outWriter = null;
        Map<String, List<TimePacketRecord>> jitterPacketMap = new HashMap<>();
        Map<String, List<TimePacketRecord>> latencyPacketMap = new HashMap<>();
        Map<String, List<TimePacketRecord>> currentPacketMap = null;

    //    for (DataRecordSet dataRecordSet : recordSetList)
     //   {
            if (dataRecordSet.Filename.contains("Jitter"))
                currentPacketMap = jitterPacketMap;
            else if (dataRecordSet.Filename.contains("Latency"))
                currentPacketMap = latencyPacketMap;

            String[] segments = dataRecordSet.Filename.split(" ");
            String nameExt = segments[segments.length-1];
            String portName = nameExt.split("\\.")[0];
            if (!currentPacketMap.containsKey(portName))
                currentPacketMap.put(portName, new ArrayList<>());

            String[] timeRow = dataRecordSet.InRows.get(0).split(",");
            String[] countRow = dataRecordSet.InRows.get(1).split(",");

            for (int i = 2; i < timeRow.length; i++)
            {
                TimePacketRecord timePacketRecord = new TimePacketRecord(timeRow[i], countRow[i]);
                currentPacketMap.get(portName).add(timePacketRecord);
            }
     //   }
        String rootFolder = "";
        String jitterFilename = rootFolder + "\\jitter.export";
        ExportHistogramData(jitterFilename, jitterPacketMap);

        String latencyFilename = rootFolder + "\\latency.export";
        ExportHistogramData(latencyFilename, latencyPacketMap);
    }

    public static void ExportHistogramData(String filename, Map<String, List<TimePacketRecord>> packetMap) throws Exception
    {
        Files.deleteIfExists(new File(filename).toPath());
        BufferedWriter outWriter = new BufferedWriter(new FileWriter(filename, true));
        Set<String> _keys = packetMap.keySet();
        String[] keys = _keys.toArray(new String[_keys.size()]);
        Arrays.sort(keys);
        String topheader = String.join(",,", keys);
        outWriter.append(topheader).append(",\n");
        outWriter.append(HistoFileType.TitleHeader);

        StringBuffer sbDataRow = new StringBuffer();

        DecimalFormat df = new DecimalFormat("0.00");
        for (int i = 0; i < 253; i++)
        {
            List<String> rowValues = new ArrayList<>();
            for (String key : keys)
            {
                List<TimePacketRecord> timePacketRecordList = packetMap.get(key);
                TimePacketRecord timePacketRecord = timePacketRecordList.get(i);
                double dVal = Double.parseDouble(timePacketRecord.TimeBucket)/1000000.0;
                rowValues.add(df.format(dVal));
                rowValues.add(timePacketRecord.PacketCount);
            }
            String outRow = String.join(",",rowValues);
            outWriter.append(outRow).append("\n");
        }
        outWriter.close();
    }
}
