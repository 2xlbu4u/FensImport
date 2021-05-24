package gov.faa.fens;

public class ChariotMixType extends FileType
{
    //   private static String TitleHeader = "timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount\n";

    ChariotMixType()
    {
        // 17 cols
        HeaderCsv = "technology,testname,";
        PatternCsv = "%s,%s,%s,%s,%d,%s,%s,%d,%d,%d,%,%d,%d,%s,%f,%f,%f\n";
    }

    @Override
    public void PrepareForExport(DataRecordSet dataRecordSet) throws Exception
    {
        String header = null;
        dataRecordSet.OutFileSuffix = "_db_import.csv";

        for (String row : dataRecordSet.InRows)
        {
            if (header == null)
            {
                header = HeaderCsv + row + "\n";
                dataRecordSet.OutRows.add(header);
                continue;
            }
            // replace lat, lon, alt with 0 if they do not exist
            row = row.replace("N/A", "0");
/*            while (row.contains(",,"))
            {
                row = row.replace(",,",",null,");
            }

            if (row.endsWith(","))
            {
                row = row + "null";
            }*/

            String outrow = String.format("%s,%s,%s\n", dataRecordSet.Technology, dataRecordSet.Testname, row);
            dataRecordSet.OutRows.add(outrow);
        }
    }
}
