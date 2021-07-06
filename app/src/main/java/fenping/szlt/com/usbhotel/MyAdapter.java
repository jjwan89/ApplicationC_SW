package fenping.szlt.com.usbhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import utils.Tools;

/**
 * 作者:evilbinary on 2/20/16.
 * 邮箱:rootdebug@163.com
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public void setmDataset(List<DownItem> mDataset) {
        this.mDataset = mDataset;
    }

    // 数据集
    private List<DownItem> mDataset=new ArrayList<DownItem>();
    private Context mContex;
    private int id;
    private View.OnFocusChangeListener mOnFocusChangeListener;
    private OnBindListener onBindListener;
    public interface  OnBindListener{
        public void onBind(View view, int i);
    };

    public MyAdapter(Context context, List<DownItem> data) {
        super();
        mContex = context;
        if(!Tools.isEmpty(data)){
            mDataset.clear();
            mDataset.addAll(data);
        }
    }
    public MyAdapter(Context context, List<DownItem> data, int id) {
        super();
        mContex = context;
        if(!Tools.isEmpty(data)){
            mDataset.clear();
            mDataset.addAll(data);
        }
        this.id=id;
    }

    public MyAdapter(Context context, List<DownItem> data, int id, View.OnFocusChangeListener onFocusChangeListener) {
        super();
        mContex = context;
        if(!Tools.isEmpty(data)){
            mDataset.clear();
            mDataset.addAll(data);
        }
        this.id=id;
        this.mOnFocusChangeListener=onFocusChangeListener;
    }

    public void setOnBindListener(OnBindListener onBindListener) {
        this.onBindListener = onBindListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int resId=0;
        if(this.id>0){
            resId=this.id;
        }
        View view = LayoutInflater.from(mContex).inflate(resId, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String url = mDataset.get(i).getUrl();
        String substring = url.substring(url.lastIndexOf("/") + 1);
        viewHolder.url.setText(substring);
        viewHolder.load.setText(mDataset.get(i).getLoad());

        //viewHolder.mTextView.setText(mDataset.get(i));
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
        if(onBindListener!=null){
            onBindListener.onBind(viewHolder.itemView,i);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView url;
        public TextView load;

        public ViewHolder(View itemView) {
            super(itemView);
            url = (TextView) itemView.findViewById(R.id.url);
            load = (TextView) itemView.findViewById(R.id.load);

        }
    }

    public void setData(List<DownItem> data){
        if(!Tools.isEmpty(data)){
            mDataset.clear();
            mDataset.addAll(data);
            notifyDataSetChanged();
        }
    }

}
