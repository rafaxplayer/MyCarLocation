package com.mycarlocation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.mycarlocation.classes.Installation;
import com.mycarlocation.classes.Location;
import com.mycarlocation.classes.SwipeToDismissTouchListener;
import com.mycarlocation.classes.touchTutorial;

import java.util.ArrayList;
import java.util.List;

import static com.mycarlocation.classes.GlobalUttilities.setToolBar;

public class Hystory_Activity extends AppCompatActivity {

    private RecyclerView listhistory;
    private String ID;
    private Firebase myFirebaseRef;
    private RecyclerAdapter adapter;
    private SwipeToDismissTouchListener swipeToDismissTouchListener;
    private SharedPreferences prefs;
    private CoordinatorLayout historycoord;
    private LinearLayout empty_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historyactivity);
        setToolBar(this, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        historycoord = (CoordinatorLayout) findViewById(R.id.historycoordinated);

        listhistory = (RecyclerView) findViewById(R.id.listhistory);
        empty_view=(LinearLayout)findViewById(R.id.emptyview);
        listhistory.setHasFixedSize(true);
        listhistory.setLayoutManager(new LinearLayoutManager(this));
        listhistory.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerAdapter();
        listhistory.setAdapter(adapter);

        ID = new Installation().id(this);

        swipeToDismissTouchListener = new SwipeToDismissTouchListener(listhistory, new SwipeToDismissTouchListener.DismissCallbacks() {

            @Override
            public SwipeToDismissTouchListener.SwipeDirection canDismiss(int position) {

                return SwipeToDismissTouchListener.SwipeDirection.RIGHT;
            }

            @Override
            public void onDismiss(RecyclerView view, List<SwipeToDismissTouchListener.PendingDismissData> dismissData) {
                for (SwipeToDismissTouchListener.PendingDismissData data : dismissData) {
                    adapter.removeItem(data.position);

                }
            }
        });
        listhistory.addOnItemTouchListener(swipeToDismissTouchListener);
        if (prefs.getBoolean("FirstUse", true))
            showEditDialog();
    }

    private void checkAdapterIsEmpty () {
        if (adapter.getItemCount() == 0) {
            empty_view.setVisibility(View.VISIBLE);
        } else {
            empty_view.setVisibility(View.GONE);
        }
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        touchTutorial tuto = new touchTutorial();
        tuto.show(fm, "tuto_touch");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocations() {

        myFirebaseRef = new Firebase("https://mycarlocation.firebaseio.com/"+ID + "/locations");
        Query queryRef = myFirebaseRef.orderByChild("date");
        queryRef.addValueEventListener(new ValueEventListener() {
            
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Location> locations = new ArrayList<Location>();
                for (DataSnapshot location : dataSnapshot.getChildren()) {

                    Location loc=location.getValue(Location.class);
                    String key = location.getKey();
                    loc.setKeyfirebase(key);

                    locations.add(loc);

                }
                adapter.additems(locations);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocations();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty();
            }
        });
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private ArrayList<Location> mDataset;

        public RecyclerAdapter() {
            if (mDataset == null) {
                mDataset = new ArrayList<Location>();
            }

        }

        public void additems(ArrayList<Location> mDatos) {
            this.mDataset = mDatos;
            notifyDataSetChanged();

        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);

            RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int i) {
            if (mDataset != null) {
                viewHolder.mTextViewDirection.setText(((Location) mDataset.get(i)).getDirection());
                viewHolder.mTextViewDate.setText(((Location) mDataset.get(i)).getDate());
            }

        }

        public void removeItem(int pos) {
            notifyItemRemoved(pos);
            removeLocation(((Location) mDataset.get(pos)).getKeyfirebase(), pos);
        }

        private void removeLocation(final String id, final int pos) {
            myFirebaseRef = new Firebase("https://mycarlocation.firebaseio.com/" + ID + "/locations");
            myFirebaseRef.child(id).removeValue(new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    Snackbar.make(historycoord, "Ok Removed location", Snackbar.LENGTH_LONG)
                            .show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mTextViewDirection;
            public TextView mTextViewDate;


            public ViewHolder(View v) {
                super(v);
                mTextViewDirection = (TextView) v.findViewById(R.id.textDirection);
                mTextViewDate = (TextView) v.findViewById(R.id.date);

                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
        private static final int EMPTY_VIEW = 10;
    }

}
