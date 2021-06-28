package gov.faa.fens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WanQualityFileType extends FileType
{
/*    static String headerCsv = "technology,testname,WAN name,Type,Cell ID,Carrier,Band,LTE - RSRP,LTE - SINR,LTE - RSRQ,Latency,MNC,MCC,Time,Latitude,Longitude,Altitude\n";
    static String patternCsv = "%s,%s,%s,%s,%d,%s,%s,%d,%d,%d,%,%d,%d,%s,%f,%f,%f\n";

    static String headerCsv3G = "technology,testname,WAN name,Type,Cell ID,Carrier,Band,3G - RSSI,3G - EC/IO,LTE - RSRP,LTE - SINR,LTE - RSRQ,Latency,MNC,MCC,Time,Latitude,Longitude,Altitude\n";
    static String patternCsv3G = "%s,%s,%s,%s,%d,%s,%s,%d,%d,%d,%d,%d,%,%d,%d,%s,%f,%f,%f\n";*/

    static boolean is3G;

    WanQualityFileType()
    {
        HeaderCsv = "technology,testname,WAN name,Type,Cell ID,Carrier,Band,3G - RSSI,3G - EC/IO,LTE - RSRP,LTE - SINR,LTE - RSRQ,Latency,MNC,MCC,Time,Latitude,Longitude,Altitude\n";
        PatternCsv = "%s,%s,%s,%s,%d,%s,%s,%d,%d,%d,%d,%d,%,%d,%d,%s,%f,%f,%f\n";
    }

    @Override
    public void PrepareForExport(DataRecordSet dataRecordSet) throws Exception
    {
        String header = null;

        dataRecordSet.OutFileSuffix = "_db_import.csv";
        for (String _row : dataRecordSet.InRows)
        {
            // Handle a no data row
            if (_row.startsWith(","))
                continue;

            String row = _row;
            if (header == null)
            {
                is3G = row.contains("3G");
                header = HeaderCsv;
                dataRecordSet.OutRows.add(HeaderCsv);
                continue;
            }
            // replace missing values with -1 if they do not exist
            while (row.contains(",,"))
            {
                row = row.replace(",,", ",-1,");
            }
            if (row.endsWith(","))
                row = row + "-1";

            String buildRow = row;
            List<String> cols = Arrays.asList(row.split(","));

            // Fix missing seconds in timestamp
            String timestamp = cols.get(cols.size()-4);
            String[] timesplit = timestamp.split(" ");
            if (timesplit[1].split(":").length < 3)
            {
                timestamp = timestamp + ":00";
                cols.set(cols.size()-4, timestamp);
                buildRow = String.join(",",cols);
            }

            if (!is3G)
            {
                List<String> firstGroup = cols.subList(0,5);
                List<String> secondGroup = cols.subList(5,cols.size());
                buildRow = String.join(",", firstGroup) + ",-1,-1," + String.join(",", secondGroup);
            }
            String outrow = String.format("%s,%s,%s\n", dataRecordSet.Technology, dataRecordSet.Testname, buildRow);
            dataRecordSet.OutRows.add(outrow);
        }
    }
}
