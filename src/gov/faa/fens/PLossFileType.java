package gov.faa.fens;

public class PLossFileType extends FileType
{
    PLossFileType()
    {
        UrlPattern = "/ploss/_bulk?pretty";
        HeaderCsv = "timestamp,dayofweek,ip,srctodest,isLoss";
        PatternCsv = "\"%s\",%d,\"%s\",\"%s\",%d";
        PatternJson = "{\"index\":{\"_id\":\"%s\"}}\n{\"timestamp\":\"%s\",\"dayofweek\":\"%d\"," +
                "\"ip\":\"%s\",\"srctodest\":\"%s\",\"isLoss\":\"%d\"}\n";
    }
}