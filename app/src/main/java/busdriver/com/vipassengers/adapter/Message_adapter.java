package busdriver.com.vipassengers.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import busdriver.com.vipassengers.R;
import busdriver.com.vipassengers.module.Message_item;

/**
 * Created by Sarps on 2/8/2017.
 */
public class Message_adapter extends BaseAdapter {
    Context context;
    ArrayList<Message_item> list;

    public Message_adapter(Context context, ArrayList<Message_item> list) {
        this.context = context;
        this.list = list;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView tv_msg, tv_date;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tv_msg.setText(list.get(position).getMsg());
        holder.tv_date.setText(list.get(position).getDate());

        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}