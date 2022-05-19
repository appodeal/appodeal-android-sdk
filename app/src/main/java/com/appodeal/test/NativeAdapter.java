package com.appodeal.test;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NativeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Integer> publisherList;

    NativeAdapter(List<Integer> publisherList) {
        this.publisherList = publisherList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = new TextView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TextViewHolder) holder).setText(publisherList.get(position));
    }

    @Override
    public int getItemCount() {
        return publisherList != null ? publisherList.size() : 0;
    }

    int getLastValue() {
        if (publisherList != null && publisherList.size() > 0) {
            return publisherList.get(publisherList.size() - 1);
        }

        return 0;
    }

    void addPack(List<Integer> additionalStringTestList) {
        if (publisherList != null) {
            int startPosition = getItemCount();

            publisherList.addAll(additionalStringTestList);

            notifyItemRangeInserted(startPosition, additionalStringTestList.size());
        }
    }

    void clearContent() {
        if (publisherList != null) {
            publisherList.clear();

            notifyDataSetChanged();
        }
    }


    class TextViewHolder extends RecyclerView.ViewHolder {

        TextViewHolder(View itemView) {
            super(itemView);
        }

        public void setText(int value) {
            TextView textView = (TextView) itemView;
            textView.setText(String.valueOf(value));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100);
        }

    }

}