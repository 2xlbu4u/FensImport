package gov.faa.fens;

public class XenaFileType extends FileType
{


    XenaFileType()
    {

        PatternJson = "{\"index\":{\"_id\":\"%s\"}}\n{\"timestamp\":\"%s\",\"time\":\"%s\",\"year\":\"%d\",\"month\":\"%d\",\"day\":\"%d\",\"hour\":\"%d\",\"minute\":\"%d\",\"second\":\"%d\"," +
        "\"srcport\":\"%s\",\"srcname\":\"%s\",\"sid\":\"%d\",\"destport\":\"%s\",\"destname\":\"%s\",\"tid\":\"%d\",\"srctodest\":\"%s\"," +
        "\"txl1bps\":\"%d\",\"txbps\":\"%d\",\"txfps\":\"%d\",\"txbytes\":\"%d\",\"txframes\":\"%d\"," +
        "\"rxl1bps\":\"%d\",\"rxbps\":\"%d\",\"rxfps\":\"%d\",\"rxbytes\":\"%d\",\"rxframes\":\"%d\"," +
        "\"txnonp1dbytes\":\"%d\",\"txnonp1dframes\":\"%d\",\"rxnonp1dbytes\":\"%d\",\"rxnonp1dframes\":\"%d\"," +
        "\"rxfcserrors\":\"%d\",\"rxseqerr\":\"%d\",\"packetlossps\":\"%d\",\"prtt\":\"%d\",\"rxmiserr\":\"%d\",\"rxp1derr\":\"%d\"," +
        "\"rxber\":\"%f\",\"rxbercurr\":\"%d\"," +
        "\"latencycurr\":\"%f\",\"latencycurrmin\":\"%f\",\"latencycurrmax\":\"%f\",\"latencyavg\":\"%f\",\"latencymin\":\"%f\",\"latencymax\":\"%f\"," +
        "\"jittercurr\":\"%f\",\"jittercurrmin\":\"%f\",\"jittercurrmax\":\"%f\",\"jitteravg\":\"%f\",\"jittermin\":\"%f\",\"jittermax\":\"%f\"," +
        "\"rxpauseframes\":\"%d\",\"rxpfcframes\":\"%d\"" +
        "}\n";
        UrlPattern = "/xenapr/_bulk?pretty";
        HeaderCsv = "timestamp,srcport,srcname,sid,destport,destname,tid,srctodest,txl1bps,txbps,txfps,txbytes,txframes,rxl1bps,rxbps,rxfps,rxbytes,rxframes,txnonp1dbytes,txnonp1dframes,rxnonp1dbytes,rxnonp1dframes,rxfcserrors,rxseqerr,packetlossps,prtt,rxmiserr,rxp1derr,rxber,rxbercurr,latencycurr,latencycurrmin,latencycurrmax,latencyavg,latencymin,latencymax,jittercurr,jittercurrmin,jittercurrmax,jitteravg,jittermin,jittermax,rxpauseframes,rxpfcframes";
        PatternCsv = "\"%s\"," +
            "\"%s\",\"%s\",%d,\"%s\",\"%s\",%d,\"%s\"," +
            "%d,%d,%d,%d,%d," +
            "%d,%d,%d,%d,%d," +
            "%d,%d,%d,%d," +
            "%d,%d,%d,%d,%d,%d," +
            "%f,%d," +
            "%f,%f,%f,%f,%f,%f," +
            "%f,%f,%f,%f,%f,%f," +
            "%d,%d";
    }
}
