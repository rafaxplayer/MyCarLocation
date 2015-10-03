package com.mycarlocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.mycarlocation.GlobalUttilities.setToolBar;

public class Hystory_Activity extends AppCompatActivity {
private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hystory);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolBar(this,toolbar);

    }


    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private String[] mDataset;

        public RecyclerAdapter(String[] myDataset) {
            mDataset = myDataset;
        }


        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);

            RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int i) {

        }


        @Override
        public int getItemCount() {
            return mDataset.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;
            public TextView mTextView2;
            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.textDirection);
                mTextView2 = (TextView) v.findViewById(R.id.textLocation);
            }
        }
    }

}
