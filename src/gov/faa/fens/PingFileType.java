package gov.faa.fens;

public class PingFileType extends FileType
{

    PingFileType()
    {
        UrlPattern = "/pingpr/_bulk?pretty";
        HeaderCsv = "timestamp,dayofweek,ip,srctodest,bytes,rtt,ttl";
        PatternCsv = "\"%s\",%d,\"%s\",\"%s\",%d,%d,%d";
        PatternJson = "{\"index\":{\"_id\":\"%s\"}}\n{\"timestamp\":\"%s\",\"time\":\"%s\",\"dayofweek\":\"%d\",\"year\":\"%d\",\"month\":\"%d\",\"day\":\"%d\",\"hour\":\"%d\",\"minute\":\"%d\",\"second\":\"%d\"," +
                "\"ip\":\"%s\",\"srctodest\":\"%s\",\"bytes\":\"%d\",\"rtt\":\"%d\",\"ttl\":\"%d\"}\n";
    }


}