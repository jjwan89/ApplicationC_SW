package fenping.szlt.com.usbhotel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class CmdUtils {
    public static boolean installSliceApk(String path) {

            String cmd = "pm install -r "+path;
            Process process = null;
            DataOutputStream os = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = null;
            StringBuilder errorMsg = null;
            try {
                //静默安装需要root权限
                process = Runtime.getRuntime().exec("su");
                os = new DataOutputStream(process.getOutputStream());
                os.write(cmd.getBytes());
                os.writeBytes("\n");
                os.writeBytes("exit\n");
                os.flush();
                //执行命令
                process.waitFor();
                //获取返回结果
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    if (process != null) {
                        process.destroy();
                    }
                    if (successResult != null) {
                        successResult.close();
                    }
                    if (errorResult != null) {
                        errorResult.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return successMsg.toString().equals("Success");
        }
}
