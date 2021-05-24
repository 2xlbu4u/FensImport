package gov.faa.fens;

import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class PingFileType extends FileType
{

    PingFileType()
    {
        UrlPattern = "/pingpr/_bulk?pretty";

        HeaderCsv = "technology,testname,timestamp,dayofweek,ip,bytes,rtt,ttl\n";
        PatternCsv = "\"%s\",\"%s\",\"%s\",%d,\"%s\",%d,%d,%d\n";

/*
        PatternJson = "{\"index\":{\"_id\":\"%s\"}}\n{\"timestamp\":\"%s\",\"time\":\"%s\",\"dayofweek\":\"%d\",\"year\":\"%d\",\"month\":\"%d\",\"day\":\"%d\",\"hour\":\"%d\",\"minute\":\"%d\",\"second\":\"%d\"," +
                "\"ip\":\"%s\",\"srctodest\":\"%s\",\"bytes\":\"%d\",\"rtt\":\"%d\",\"ttl\":\"%d\"}\n";
*/
    }

    @Override
    public void PrepareForExport(DataRecordSet dataRecordSet) throws Exception
    {
        String header = null;
        dataRecordSet.OutFileSuffix = "_db_import.csv";

        for (String row : dataRecordSet.InRows)
        {
            String[] csvData = row.split(",");
            if (header == null)
            {
                header = HeaderCsv;
                dataRecordSet.OutRows.add(header);
                continue;
            }
            String date = csvData[0];
            String yearStr = "";
            String monthStr = "";
            String dayStr = "";

            if (date.contains("/"))
            {
                String[] ymdSplit = date.split("/");
                yearStr = ymdSplit[2];
                if (yearStr.length() == 2)
                    yearStr = "20" + yearStr;
                monthStr = ymdSplit[0];
                dayStr = ymdSplit[1];
            }
            else if (date.contains("-"))
            {
                String[] ymdSplit = date.split("-");
                yearStr = ymdSplit[2];
                if (yearStr.length() == 2)
                    yearStr = "20" + yearStr;
                monthStr = MapManager.monthMap.get(ymdSplit[1]);
                dayStr = ymdSplit[0];
            }
            else
            {
                String[] ymdSplit = date.split(" ");
                yearStr = ymdSplit[2];
                monthStr = MapManager.monthMap.get(ymdSplit[1]);
                dayStr = ymdSplit[0];
            }

            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);

            if (monthStr.length() == 1)
                monthStr = "0" + monthStr;
            if (dayStr.length() == 1)
                dayStr = "0" + dayStr;
            String time = csvData[1];

            String[] hmsSplit = time.split(":");

            // String time = timed;

            Calendar c = Calendar.getInstance();
            c.set(year, month - 1, day);
            int dayofweek = c.get(Calendar.DAY_OF_WEEK);

            String hourStr = hmsSplit[0];
            String minuteStr = hmsSplit[1];
            String secondStr = hmsSplit[2];

            int hour = Integer.parseInt(hourStr);
            int minute = Integer.parseInt(minuteStr);
            int second = Integer.parseInt(secondStr);

            if (hourStr.length() == 1)
                hourStr = "0" + hourStr;
            if (minuteStr.length() == 1)
                minuteStr = "0" + minuteStr;
            if (secondStr.length() == 1)
                secondStr = "0" + secondStr;

            if (hourStr.equals("24"))
                hourStr = "00";

            String ip = csvData[2];

            String srcToDest = null;
            // Default 6 columns
            String bytesStr = csvData[3];
            String rttStr = csvData[4];
            String ttlStr = csvData[5];

            if (csvData.length > 6)
            {
                bytesStr = csvData[5];
                rttStr = csvData[6];
                ttlStr = csvData[7];
            }

          //  String[] splitName = dataRecordSet.InFilename.split("_");
        //    String[] derivedNameParts = splitName[0].split("\\\\");
         //   String derivedName = derivedNameParts[derivedNameParts.length - 1];

          //  srcToDest = derivedName.split("\\.")[0];

            int bytes = bytesStr.equals("error") || bytesStr.equals("timeout") || bytesStr.equals("timedout") || bytesStr.equals("unreach") ? -1 : Integer.parseInt(bytesStr);

            int prtt = rttStr.equals("error") || rttStr.equals("timeout") || rttStr.equals("timedout") || rttStr.equals("unreach") ? -1 : (int)Float.parseFloat(rttStr);

            int ttl = ttlStr.equals("error") || ttlStr.equals("timeout") || ttlStr.equals("timedout") || ttlStr.equals("unreach") ? -1 : (int)Float.parseFloat(ttlStr);

            String timestamp = yearStr + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minuteStr + ":" + secondStr;  //"2020-02-01 01:02:03";

            String outrow = String.format(dataRecordSet.OutFileType.PatternCsv, dataRecordSet.Technology, dataRecordSet.Testname, timestamp, dayofweek, ip, bytes, prtt, ttl);
            dataRecordSet.OutRows.add(outrow);
        }

///////////////////////////////////////////////////////////////////
//        for (String row : dataRecordSet.InRows)
//        {
//            if (header == null)
//            {
//                header = HeaderCsv;
//                dataRecordSet.OutRows.add(HeaderCsv);
//                continue;
//            }
//            // replace lat, lon, alt with 0 if they do not exist
//            row = row.replace(",,,", ",0.0,0.0,0.0");
//
//            String outrow = String.format("%s,%s,%s\n", dataRecordSet.Technology, dataRecordSet.Testname, row);
//            dataRecordSet.OutRows.add(outrow);
//        }
    }
}