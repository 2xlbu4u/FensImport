package gov.faa.fens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

public class HistoFileType extends FileType
{
    private static String TitleHeader = "timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount\n";

    HistoFileType()
    {
        HeaderCsv = "technology,testname,time,count\n";
        PatternCsv = "\"%s\",\"%s\",%d,%d\n";
    }

    @Override
    public void PrepareForExport(DataRecordSet dataRecordSet) throws Exception
    {
        String header = dataRecordSet.InRows.get(0);
        dataRecordSet.OutRows.add(HeaderCsv);
        if (header.contains("Jitter"))
        {
            dataRecordSet.OutFileSuffix = "_jitter_histo_db_import.csv";
        }
        else if (header.contains("Latency"))
        {
            dataRecordSet.OutFileSuffix = "_latency_histo_db_import.csv";
        }
        String[] timeRow = dataRecordSet.InRows.get(1).split(",");
        String[] countRow = dataRecordSet.InRows.get(2).split(",");

        // first 2 cols get skipped
        for (int i = 2; i < timeRow.length; i++)
        {
            String outrow = String.format(PatternCsv, dataRecordSet.Technology, dataRecordSet.Testname, Integer.parseInt(timeRow[i]), Integer.parseInt(countRow[i]));
            dataRecordSet.OutRows.add(outrow);

        }
    }
}
 //  TimePacketRecord timePacketRecord = new TimePacketRecord(timeRow[i], countRow[i]);
/*

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
*/
/*                String[] segments = dataRecordSet.InFilename.split(" ");
                String nameExt = segments[segments.length - 1];
                String portName = nameExt.split("\\.")[0];

                if (!currentPacketMap.containsKey(portName))
                    currentPacketMap.put(portName, new ArrayList<>());*/