package gov.faa.fens;

import java.io.*;

public class FensImporter
{
    public static void main(String[] args) throws IOException
    {
        DataRecordSet dataRecordSet = new DataRecordSet(){};
        try
        {
            if (args.length < 3)
            {
                System.out.println("\nUsage: java -jar FensImport.jar -tTechnology -nTestName FullPathnameToImportFile\n");
                System.out.println("e.g java -jar FensImport.jar -tLTE -nXena_TR15_P6_Jitter_Re-Run_T-Mobile \"./PostgresImport/Xena_TR15_P6_Jitter_Re-Run_T-Mobile Test_PW_Sierra_20210407_085934.csv\"\n");
                System.exit(1);
            }

            processArgs(args, dataRecordSet);

            dataRecordSet.RootFolderOrFile = dataRecordSet.InFilename.toLowerCase().endsWith(".zip") ?
                    UnzipFiles.UnzipAndCopyLocal(dataRecordSet.InFilename) : new File(dataRecordSet.InFilename);

            if (dataRecordSet.RootFolderOrFile == null)
            {
                System.out.println("Unable to establish root Folder from: " + dataRecordSet.InFilename);
                return;
            }

            String[] splitName = dataRecordSet.InFilename.split("\\.");

            dataRecordSet.OutFileTypeStr = splitName[splitName.length-1];

            // Move to the in file type class if input file is not always csv
            DataManager.ImportFileRows(dataRecordSet);

            // Determine the data being imported
            FileType.SetInFileType(dataRecordSet);

            // Default out data content same as in for now
            dataRecordSet.OutFileType = dataRecordSet.InFileType;

            // Prep for Export the file
            dataRecordSet.PrepareForExport();

            // Save to file for importing into db
            DataManager.ExportData(dataRecordSet);

            System.out.println("\nCompleted");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (dataRecordSet.OutWriter != null)
                dataRecordSet.OutWriter.close();
        }
    }

    private static void processArgs(String[] args, DataRecordSet dataRecordSet)
    {
        for (String arg : args)
        {
            if (arg.startsWith("-t"))
            {
                dataRecordSet.Technology = arg.substring(2);
            }
            else if (arg.startsWith("-n"))
            {
                dataRecordSet.Testname = arg.substring(2);
            }
            else
            {
                dataRecordSet.InFilename = arg;
            }
        }
    }

}