package gov.faa.fens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

public class WanQualityFileType extends FileType
{
    //   private static String TitleHeader = "timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount\n";

    WanQualityFileType()
    {
        // 17 cols
        HeaderCsv = "technology,testname,WAN name,Type,Cell ID,Carrier,Band,LTE - RSRP,LTE - SINR,LTE - RSRQ,Latency,MNC,MCC,Time,Latitude,Longitude,Altitude\n";
        PatternCsv = "%s,%s,%s,%s,%d,%s,%s,%d,%d,%d,%,%d,%d,%s,%f,%f,%f\n";
    }

    @Override
    public void PrepareForExport(DataRecordSet dataRecordSet) throws Exception
    {
        String header = null;
        dataRecordSet.OutFileSuffix = "_wan_quality_db_import.csv";

        for (String row : dataRecordSet.InRows)
        {
            //String[] csvData = row.split(",");
            if (header == null)
            {
                header = HeaderCsv;
                dataRecordSet.OutRows.add(HeaderCsv);
                continue;
            }
            String outrow = String.format("%s,%s,%s\n", dataRecordSet.Technology, dataRecordSet.Testname, row);
            dataRecordSet.OutRows.add(outrow);
        }
    }
}
