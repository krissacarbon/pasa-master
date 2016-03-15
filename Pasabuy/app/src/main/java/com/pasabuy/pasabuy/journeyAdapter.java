package com.pasabuy.pasabuy;

/**
 * Created by jesus on 3/14/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class journeyAdapter extends ArrayAdapter<journeyObject>{
    private static LayoutInflater mInflater ;


    private static class ViewHolder {

        private TextView countryView;
        private TextView cityView;
        private TextView returnView;

    }

    public journeyAdapter(Context context, int textViewResourceId, ArrayList<journeyObject> items) {
        super(context, textViewResourceId, items);
    }

    public journeyAdapter(Context context, journeyObject[] values){
        super(context,-1,values);

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.journey_list_item, parent, false);

            viewHolder = new ViewHolder();
//            viewHolder.productView = (TextView) convertView.findViewById(R.id.product_hi);
//            viewHolder.quantityView = (TextView) convertView.findViewById(R.id.quantity_hi);
//            viewHolder.priceView = (TextView) convertView.findViewById(R.id.price_hi);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        journeyObject item = getItem(position);

////        viewHolder.productView.setText(item.getName());
////        viewHolder.quantityView.setText(item.getQuantity()+"");
//        viewHolder.priceView.setText(item.getPrice()+"");

        return convertView;
    }
}
