package gov.faa.fens;

import com.sun.nio.zipfs.ZipFileSystemProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipFiles {

    public static File UnzipAndCopyLocal(String zipFilePath) throws Exception
    {
        File unzippedTempDir = null;
        try
        {
            // Unzip the file into temp
            unzippedTempDir = unzip(zipFilePath);

            // Extract Files
            List<File> filesReturn = getFilesWithExt(unzippedTempDir, "csv,log");

            // create local folder using name of zip file
            // Make xena dir
            File zipFile = new File(zipFilePath);
            String zipFileName = zipFile.getName();
            String workingPath = zipFilePath.split("\\.")[0] + "\\";
            File newDir = new File(workingPath + "xena");
            newDir.mkdirs();
            String xenaPath = newDir.getAbsolutePath();

            String xenaTextFileName = xenaPath + "\\xena.txt";
            String xenaPingFileName = xenaPath + "\\ping.txt";
            Process process;

            // change to working dir
         //   String workingPath = zipFilePath.replace(zipFileName, "");
            System.setProperty("user.dir", xenaPath);
            System.out.println("Working Folder: " + System.getProperty("user.dir"));

            for (File thefile : filesReturn)
            {
                String fullFilename = thefile.getAbsolutePath();
                if (fullFilename.endsWith(".csv") && thefile.getName().startsWith("BL2"))
                {
                    // Move to csv to the xena folder
                    copyFile(thefile, new File(xenaTextFileName));

                    // Run Perl for xena file
                    System.out.println("Creating xena csv files...");
                    String perlCommand = "perl ..\\..\\fens_parse_files_BL2_rm_exclude.pl \"" + xenaTextFileName + "\"";
                    process = Runtime.getRuntime().exec(perlCommand, null, newDir);
                    process.waitFor();
                }
                else if (fullFilename.endsWith(".log"))
                {
                    // Move ping log to xena folder
                    copyFile(thefile, new File(xenaPingFileName));

                    // Run Perl for ping file
                    String perlCommand = "perl ..\\..\\ping_test_dos.pl \"" + xenaPingFileName + "\"";
                    System.out.println("Creating ping csv file...");
                    process = Runtime.getRuntime().exec(perlCommand, null, newDir);
                    process.waitFor();
                }
            }
            return new File(xenaPath);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (unzippedTempDir != null)
                unzippedTempDir.delete();
        }

        return null;
    }

    public static void copyFile(File source, File dest) throws Exception
    {
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static List<File> getFilesWithExt(File rootFile, String fileFilter)
    {

        List<File> filesFound = new ArrayList();

        getFileRecursive(rootFile, fileFilter, filesFound);

        return filesFound;
    }

    public static File getFileRecursive(File rootFile, String fileFilter, List<File> filesFound)
    {
        if (rootFile == null)
            return null;
        File [] filesSearch = rootFile.listFiles();
        File fileOrPath = null;
        for (int i = 0;  i < filesSearch.length; i++)
        {
            fileOrPath = filesSearch[i];
            while(fileOrPath != null && fileOrPath.isDirectory())
            {
                fileOrPath = getFileRecursive(fileOrPath, fileFilter, filesFound);
            }
            if (fileOrPath == null)
                continue;

            String[] filesplit = fileOrPath.getName().split("\\.");
            if (filesplit.length > 1)
            {
                 if (fileFilter.contains(filesplit[filesplit.length-1]))
                 {
                     filesFound.add(fileOrPath);
                 }
            }
        }
        return null;
    }
/*    public static void main(String[] args) throws IOException {
        String fileZip = "C:\\Users\\Public\\FENS\\0-BL2 Results 0330 to 0401.zip";
        File destDir = new File("C:\\Users\\Public\\FENS\\testcase");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }*/

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        String zipName = zipEntry.getName();
        File destFile = new File(destinationDir, zipName);

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }


    private static File unzip(String zipFilePath) throws Exception
    {
//System.currentTimeMillis()
        Path tempDirWithPrefix = Files.createTempDirectory("FENS");
        String unzipDestinationTemp = tempDirWithPrefix.toAbsolutePath().toString();

        File dir = new File(unzipDestinationTemp);

        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = null;// = zis.getNextEntry();
            while((ze = zis.getNextEntry()) != null)
            {
                String fileName = ze.getName();
                File newFile = new File(unzipDestinationTemp + File.separator + fileName);
                if (ze.isDirectory())
                {
                    newFile.mkdirs();
                    continue;
                }
                    System.out.println("Unzipping file "+newFile.getAbsolutePath());
                //create directories for sub directories in zip

/*                String parentName = newFile.getParent();
                File parentFile = new File(parentName);
                parentFile.mkdirs();*/

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir;
    }

}