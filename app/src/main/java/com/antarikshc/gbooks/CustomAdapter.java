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

    CustomAdapter(@NonNull Context context, ArrayList<BookData> dataSet) {
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

        viewHolder.coverImage.setImageBitmap(dataModel.getCoverImage());
        viewHolder.txtTitle.setText(dataModel.getTitle());
        viewHolder.txtAuthor.setText(dataModel.getAuthor());
        viewHolder.txtDesc.setText(dataModel.getDesc());

        Double price = dataModel.getPrice();
        if(price == null) {
            viewHolder.txtPrice.setTextSize(21f);
            viewHolder.txtPrice.setText(R.string.not_for_sale);
            dataModel.setBuyString("NOT FOR SALE");
        } else {
            String currency = dataModel.getCurrency();
            String formattedPrice = formatPrice(price);
            switch (currency) {
                case "INR":
                    viewHolder.txtPrice.setText(R.string.rupee);
                    dataModel.setBuyString("\u20B9" + formattedPrice);
                    break;
                case "USD":
                    viewHolder.txtPrice.setText("$");
                    dataModel.setBuyString("$" + formattedPrice);
                    break;
                case "EUR":
                    viewHolder.txtPrice.setText(R.string.euro);
                    dataModel.setBuyString("\u20ac" + formattedPrice);
                    break;
                default:
                    viewHolder.txtPrice.setText(currency);
            }
            viewHolder.txtPrice.setTextSize(26f);
            viewHolder.txtPrice.append(formattedPrice);
        }

        return convertView;
    }

    private String formatPrice(Double price) {
        Integer roundOff = (int) Math.round(price);
        return roundOff.toString();
    }
}
