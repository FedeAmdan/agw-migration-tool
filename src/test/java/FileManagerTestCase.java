import com.mulesoft.FileManager;
import com.mulesoft.Main;
import com.mulesoft.ProxyType;

import org.junit.Test;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class FileManagerTestCase {
    //@Test
    //public void getProxy()
    //{
    //    List<File> files = FileManager.listf("/home/federico/testFolder/apps");
    //    int a = 1 ;
    //}
    //
    //@Test
    //public void listApps()
    //{
    //    File[] apps = FileManager.listApps("/home/federico/testFolder/apps");
    //    List<File> files = FileManager.listf(apps[0].getAbsolutePath());
    //    int a = 1;
    //}
    //
    //@Test
    //public void getFileContent() throws IOException {
    //    String content = FileManager.getFileContent("/home/federico/testFolder/apps/bare-http-proxy/bare-http-proxy.xml");
    //    int a = 1;
    //}
    //
    //@Test
    //public void replaceXMLFile() throws IOException
    //{
    //    FileManager.replaceXmlFile("/Users/federicoamdan/migrationToolTestFolder/apps/old-apikit-proxy", ProxyType.APIKIT_PROXY);
    //
    //}
    @Test
    public void getMD5() throws IOException, NoSuchAlgorithmException
    {
        List<File> files = FileManager.listf("/Users/federicoamdan/Projects/proxies-generator/output/");

        for (File f : files)
        {
            System.out.println("File: " + f.getName() + " Hash: " + FileManager.hashFile(f, "MD5"));
        }
    }
    //private String getMD5FromFile(String path) throws NoSuchAlgorithmException
    //{
    //    File file = Paths.get("/path/to/file").toFile();
    //    byte[] b =
    //
    //    byte[] hash = MessageDigest.getInstance("MD5").digest(b);
    //}



}
