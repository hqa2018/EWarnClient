package com.taide.ewarn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taide.ewarn.R;
import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.model.TIAStation;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xuexiang.xui.utils.ResUtils.getResources;


public class LocalDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private final List<LocalDeviceViewItem> localDeviceViewItemList = new ArrayList<>();
    private final Context mContext;
    private final ArrayList<TIAStation> mData;

    //第一步 定义接口
    public interface OnItemClickListener {
        void onClick(int position, TIAStation tiaStation);
    }
    private OnItemClickListener listener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public LocalDeviceAdapter(Context context ,ArrayList<TIAStation> data){
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_tia_card_view_list_item , parent ,false);
        RecyclerView.ViewHolder viewHolder = new CellViewHolder(item);
        item.setOnClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onClick(position,mData.get(position));
                }
            }
        });
        ((LocalDeviceAdapter.CellViewHolder) holder).bindView(mData.get(position));

    }

    public void updateItems(boolean animated) {

        int previousSize = localDeviceViewItemList.size();
        localDeviceViewItemList.clear();
        notifyItemRangeRemoved(0, previousSize);

        for(int i=0; i< DataMemoryManager.tiaListViewArray.size(); i++){
            TIAStation li = DataMemoryManager.tiaListViewArray.get(i);
            LocalDeviceViewItem lvi = new LocalDeviceViewItem(li.getNETCODE(),li.getSTACODE(),li.getSN());
            localDeviceViewItemList.add(lvi);
        }

        if (animated) {
            // Log.d("StationMonitor", "9");
            // Log.d("StationMonitor", "localDeviceItemList.size()="+localDeviceItemList.size());
            notifyItemRangeInserted(0, localDeviceViewItemList.size());
        } else {
            //Log.d("StationMonitor", "10");
            //Log.d("StationMonitor", "localDeviceItemList.size()="+localDeviceItemList.size());
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onClick(View view) {

    }

    public static class CellViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_user_name)
        TextView itemNameTv;
        @BindView(R.id.tv_title)
        TextView itemTitleTv;
        @BindView(R.id.tv_code)
        TextView itemCodeTv;
        @BindView(R.id.tv_lon)
        TextView itemLonTv;
        @BindView(R.id.tv_lat)
        TextView itemLatTv;
        @BindView(R.id.iv_image)
        RadiusImageView itemImage;


        public CellViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        //显示绑定控件内容
        public void bindView(TIAStation tiaStation){
            itemNameTv.setText("设备编号:"+tiaStation.getSN());
            if(tiaStation.getDEVTYPE().equals("TIA-ALERT")){
                itemTitleTv.setText("类型:预警物联网喇叭");
                itemImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_alert));
            }else{
                itemTitleTv.setText(tiaStation.getDEVTYPE()+"预警终端");
            }
            itemCodeTv.setText("台网/台站代码:"+tiaStation.getNETCODE()+"/"+tiaStation.getSTACODE());
            itemLonTv.setText("经度:E"+tiaStation.getLONGITUDE());
            itemLatTv.setText("纬度:N"+tiaStation.getLATITUDE());
            //itemSumTv.setText("设备类型："+tiaStation.getDEVTYPE());
        }
    }

    public static class LocalDeviceViewItem {
        public String SN="";
        public String macaddress="";
        public String netcode="";
        public String stacode="";


        public LocalDeviceViewItem(String netcode, String stacode, String SN) {
            this.SN = SN;
            this.netcode = netcode;
            this.stacode = stacode;
        }
    }
}
