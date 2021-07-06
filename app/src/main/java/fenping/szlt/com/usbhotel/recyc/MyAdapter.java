package fenping.szlt.com.usbhotel.recyc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import fenping.szlt.com.usbhotel.Const;
import fenping.szlt.com.usbhotel.R;
import utils.Tools;

/**
 * 作者:evilbinary on 2/20/16.
 * 邮箱:rootdebug@163.com
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    // 数据集
    private List<ResolveInfo>  mDataset;
    private Context mContex;
    private int id;
    private View.OnFocusChangeListener mOnFocusChangeListener;
    private OnBindListener onBindListener;
    private PackageManager packageManager;

    public interface  OnBindListener{
        public void onBind(View view, int i);
    };

    public MyAdapter(Context context, List<ResolveInfo> dataset) {
        super();
        mContex = context;
        mDataset = dataset;
        packageManager =mContex.getPackageManager();
    }
    public MyAdapter(Context context, List<ResolveInfo> dataset, int id) {
        super();
        mContex = context;
        mDataset = dataset;
        this.id=id;
        packageManager =mContex.getPackageManager();
    }

    public MyAdapter(Context context, List<ResolveInfo> dataset, int id, View.OnFocusChangeListener onFocusChangeListener) {
        super();
        mContex = context;
        mDataset = dataset;
        this.id=id;
        this.mOnFocusChangeListener=onFocusChangeListener;
        packageManager =mContex.getPackageManager();
    }

    public void setOnBindListener(OnBindListener onBindListener) {
        this.onBindListener = onBindListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int resId= R.layout.item;
        if(this.id>0){
            resId=this.id;
        }
        View view = LayoutInflater.from(mContex).inflate(resId, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {

        viewHolder.itemView.setTag(i);
        viewHolder.mTextView.setText(mDataset.get(i).loadLabel(packageManager));
        Glide.with(mContex).load(mDataset.get(i).loadIcon(packageManager)).into(viewHolder.app_icon);
        viewHolder.icon_bg.setBackgroundColor(mContex.getResources().getColor(Const.color[i% Const.color.length]));
        viewHolder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
        if(onBindListener!=null){
            onBindListener.onBind(viewHolder.itemView,i);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.openAPk(mContex, mDataset.get(i).activityInfo.packageName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public ImageView app_icon;
        public ImageView icon_bg;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.textView);
            app_icon = (ImageView) itemView.findViewById(R.id.app_icon);
            icon_bg = (ImageView) itemView.findViewById(R.id.icon_bg);

        }
    }

    public void setData(List<ResolveInfo> data){
        this.mDataset=data;
    }

}
