package gov.faa.fens;

public class HistoFileType extends FileType
{
    public static String TitleHeader = "timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount\n";
    HistoFileType()
    {
        HeaderCsv = "CP1,,CP5,,P1,,P2,,P4,,P5,,\n" +
                    "timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount,timebucket,packetcount\n";
        PatternCsv = "%f,%d,%f,%d,%f,%d,%f,%d,%f,%d,%f,%d\n";
    }
}
