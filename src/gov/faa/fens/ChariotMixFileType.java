package gov.faa.fens;

import java.util.*;

public class ChariotMixFileType extends FileType
{

    ChariotMixFileType()
    {

        HeaderCsv = "\"technology\",\"testname\",\"timestamp\",\"mix\",\"Source IP\",\"Destination IP\",\"Throughput\",\"Avg Throughput\",\"Min Throughput\",\"Max Throughput\",\"Total Measured Time\",\"Total Bytes Sent\",\"Total Bytes Sent By Source\",\"Total Bytes Sent By Destination\",\"Total Bytes Received\",\"Total Bytes Received By Source\",\"Total Bytes Received By Destination\",\"Bytes Lost Percentage\",\"Total Bytes Lost\",\"Total Bytes Lost By Source\",\"Total Bytes Lost By Destination\",\"Datagrams Lost\",\"Datagrams Lost By Source\",\"Datagrams Lost By Destination\",\"Maximum Consecutive DG Lost\",\"Max Maximum Consecutive DG Lost\",\"Max Maximum Consecutive DG Lost By Source\",\"Max Maximum Consecutive DG Lost By Destination\",\"Total Datagrams Sent\",\"Total Datagrams Sent By Source\",\"Total Datagrams Sent By Destination\",\"Total Datagrams Received\",\"Total Datagrams Received By Source\",\"Total Datagrams Received By Destination\",\"Total Duplicate DG Sent\",\"Total Duplicate DG Sent By Source\",\"Total Duplicate DG Sent By Destination\",\"Total Duplicate DG Received\",\"Total Duplicate DG Received By Source\",\"Total Duplicate DG Received By Destination\",\"Total DG Out of Order\",\"Jitter\",\"Avg Jitter\",\"Min\",\"Max\",\"Delay Variation\",\"Max Delay Variation\",\"One-Way Delay\",\"Avg One-Way Delay\",\"Min One-Way Delay\",\"Max One-Way Delay\",\"Estimated Error\",\"Maximum Error\"\n";
        // 53 original cols
        PatternCsv = "technology,testname,%s\n";
    }

    @Override
    public void PrepareForExport(DataRecordSet dataRecordSet) throws Exception
    {
        String header = null;
        dataRecordSet.OutFileSuffix = "_chariot_mix_db_import.csv";

        for (String row : dataRecordSet.InRows)
        {
            String[] rowInSplit = row.split(",");
            String[] rowOutSplit = Arrays.copyOfRange(rowInSplit,0,53);;

            if (header == null)
            {
                header = HeaderCsv;
                dataRecordSet.OutRows.add(HeaderCsv);
                continue;
            }
            String outData = Arrays.toString(rowOutSplit);
            String outrow = String.format(PatternCsv, dataRecordSet.Technology, dataRecordSet.Testname, outData);
            dataRecordSet.OutRows.add(outrow);
        }
    }
}
