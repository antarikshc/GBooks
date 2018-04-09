package com.antarikshc.gbooks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<BookData> {

    private ArrayList<BookData> dataSet;
    private Context mContext;

    private static class ViewHolder{
        ImageView coverImage;
        TextView txtTitle;
        TextView txtAuthor;
        TextView txtDesc;
        TextView txtPrice;
    }

    public CustomAdapter(@NonNull Context context, ArrayList<BookData> dataSet) {
        super(context, R.layout.custom_list, dataSet);
        this.dataSet = dataSet;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        BookData dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_list, parent, false);
            viewHolder.coverImage = convertView.findViewById(R.id.coverImage);
            viewHolder.txtTitle = convertView.findViewById(R.id.txtTitle);
            viewHolder.txtAuthor = convertView.findViewById(R.id.txtAuthor);
            viewHolder.txtDesc = convertView.findViewById(R.id.txtDesc);
            viewHolder.txtPrice = convertView.findViewById(R.id.txtPrice);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtTitle.setText(dataModel.getTitle());
        viewHolder.txtAuthor.setText(dataModel.getAuthor());
        viewHolder.txtDesc.setText(dataModel.getDesc());
        Double price = dataModel.getPrice();
        viewHolder.txtPrice.setText(formatPrice(price));

        return convertView;
    }

    private String formatPrice(Double price) {
        Integer roundOff = (int) Math.round(price);
        return roundOff.toString();
    }
}
