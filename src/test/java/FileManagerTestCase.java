import com.mulesoft.FileManager;
import com.mulesoft.Main;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by federico on 02/05/15.
 */
public class FileManagerTestCase {
    @Test
    public void getProxy()
    {
        List<File> files = FileManager.listf("/home/federico/testFolder/apps");
        int a = 1 ;
    }

    @Test
    public void listApps()
    {
        File[] apps = FileManager.listApps("/home/federico/testFolder/apps");
        List<File> files = FileManager.listf(apps[0].getAbsolutePath());
        int a = 1;
    }

    @Test
    public void getFileContent() throws IOException {
        String content = FileManager.getFileContent("/home/federico/testFolder/apps/bare-http-proxy/bare-http-proxy.xml");
        int a = 1;
    }

}
