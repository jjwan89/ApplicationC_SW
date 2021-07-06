package fenping.szlt.com.usbhotel.recyc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.evilbinary.tv.widget.BorderView;
import org.evilbinary.tv.widget.TvGridLayoutManagerScrolling;

import java.util.ArrayList;
import java.util.List;

import fenping.szlt.com.usbhotel.Const;
import fenping.szlt.com.usbhotel.R;
import utils.SettingUtils;

/**
 * 作者:evilbinary on 2/20/16.
 * 邮箱:rootdebug@163.com
 */
public class DemoRecyclerViewActivity extends Activity {

    private BorderView border;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_view);
        border = new BorderView(this);
        border.setBackgroundResource(R.drawable.shape_app_focus);
        border.getEffect().setScale(1.0f);
        testRecyclerViewGridLayout();

    }



    private void testRecyclerViewGridLayout() {
        //test grid
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager gridlayoutManager = new TvGridLayoutManagerScrolling(this, 5);
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridlayoutManager);
        recyclerView.setFocusable(false);
        border.attachTo(recyclerView);
        createData(recyclerView);

    }
    private List<ResolveInfo> getAllApps(Context mContext) {
        List<ResolveInfo> allApp = new ArrayList<ResolveInfo>();
        PackageManager packageManager = mContext.getPackageManager();
        // 所有应用列表
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> tempAppList = packageManager.queryIntentActivities(mainIntent, 0);
        allApp.addAll(tempAppList);
        int size = tempAppList.size();
        String name = mContext.getPackageName();

        for (ResolveInfo resolveInfo : tempAppList) {
            if (name.equals(resolveInfo.activityInfo.packageName)) {
                allApp.remove(resolveInfo);
                break;
            }
        }

        return allApp;
    }

    private void createData(RecyclerView recyclerView) {
        //创建数据集
        String[] dataset = new String[100];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = "item" + i;
        }
        List<ResolveInfo> allApps = getAllApps(this);
        // 创建Adapter，并指定数据集
        MyAdapter adapter = new MyAdapter(this, allApps);
        // 设置Adapter
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
    }


}
