package gov.faa.fens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

public class ChariotMixFileType extends FileType
{
    //   private static String TitleHeader = "timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount\n";

    ChariotMixFileType()
    {
        // 17 cols
        HeaderCsv = "technology,testname,timestamp,mix,Source IP,Destination IP,Throughput,Avg Throughput,Min Throughput,Max Throughput,Total Measured Time,Total Bytes Sent,Total Bytes Sent By Source,Total Bytes Sent By Destination,Total Bytes Received,Total Bytes Received By Source,Total Bytes Received By Destination,Bytes Lost Percentage,Total Bytes Lost,Total Bytes Lost By Source,Total Bytes Lost By Destination,Datagrams Lost,Datagrams Lost By Source,Datagrams Lost By Destination,Maximum Consecutive DG Lost,Max Maximum Consecutive DG Lost,Max Maximum Consecutive DG Lost By Source,Max Maximum Consecutive DG Lost By Destination,Total Datagrams Sent,Total Datagrams Sent By Source,Total Datagrams Sent By Destination,Total Datagrams Received,Total Datagrams Received By Source,Total Datagrams Received By Destination,Total Duplicate DG Sent,Total Duplicate DG Sent By Source,Total Duplicate DG Sent By Destination,Total Duplicate DG Received,Total Duplicate DG Received By Source,Total Duplicate DG Received By Destination,Total DG Out of Order,Jitter,Avg Jitter,Min Jitter,Max Jitter,Delay Variation,Max Delay Variation,One-Way Delay,Avg One-Way Delay,Min One-Way Delay,Max One-Way Delay,Estimated Error,Maximum Error,Mos,Avg Mos,Min Mos,Max Mos,R-value,Avg R-value,End-to-End Delay,Avg End-to-End Delay,Jitter Buffer Lost Datagrams,Total Jitter Buffer Lost Datagrams,DF,Avg DF,Min DF,Max DF,MLR,Avg MLR,Min MLR,Max MLR,RSSI for Source,RSSI for Destination,BSSID for Source,BSSID for Destination,SSID for Source,SSID for Destination,Wi-Fi Link Speed for Source,Wi-Fi Link Speed for Destination,Wi-Fi Frequency for Source,Wi-Fi Frequency for Destination,Wi-Fi Channel for Source,Wi-Fi Channel for Destination,Latitude for Source,Latitude for Destination,Longitude for Source,Longitude for Destination,Total HTTP Video Upshifts,Total HTTP Video Downshifts,Total HTTP Video Very Low Quality Segments,Total HTTP Video Low Quality Segments,Total HTTP Video Medium Quality Segments,Total HTTP Video High Quality Segments,Total HTTP Video Very High Quality Segments,Avg HTTP Video Prebuffering Duration,Min HTTP Video Prebuffering Duration,Max HTTP Video Prebuffering Duration,Total HTTP Video Stopped Count,Total HTTP Video Stopped Duration,Total HTTP Video Buffer Full Count,Total HTTP Video Buffer Full Duration,HTTP Video Rate,Avg HTTP Video Rate,Min HTTP Video Rate,Max HTTP Video Rate,CPU Utilization for Source,CPU Utilization for Destination\n";
        PatternCsv = "%s,%s,%s\n";
    }

    @Override
    public void PrepareForExport(DataRecordSet dataRecordSet) throws Exception
    {
        String header = null;
        dataRecordSet.OutFileSuffix = "_chariot_mix_db_import.csv";

        for (String row : dataRecordSet.InRows)
        {
            if (header == null)
            {
                header = HeaderCsv;
                dataRecordSet.OutRows.add(HeaderCsv);
                continue;
            }
            String outrow = String.format(PatternCsv, dataRecordSet.Technology, dataRecordSet.Testname, row);
            dataRecordSet.OutRows.add(outrow);
        }
    }
}
