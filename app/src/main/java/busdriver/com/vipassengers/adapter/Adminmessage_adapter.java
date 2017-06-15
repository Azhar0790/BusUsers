package busdriver.com.vipassengers.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import busdriver.com.vipassengers.CustomerActivity;
import busdriver.com.vipassengers.R;
import busdriver.com.vipassengers.module.Message_item;

/**
 * Created by Sarps on 2/8/2017.
 */
public class Adminmessage_adapter extends BaseAdapter {
    Context context;
    ArrayList<Message_item> list;
    SharedPreferences pref;

    public Adminmessage_adapter(Context context, ArrayList<Message_item> list) {
        this.context = context;
        this.list = list;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView tv_msg, tv_date;
        Button btn_reply;
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
            holder.btn_reply = (Button) convertView.findViewById(R.id.btn_reply);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        pref = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        holder.tv_msg.setText(list.get(position).getMsg());
        holder.tv_date.setText(list.get(position).getDate());
        holder.btn_reply.setVisibility(View.VISIBLE);
        holder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, CustomerActivity.class);
                i.putExtra("admin_msg","admin_msg");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
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