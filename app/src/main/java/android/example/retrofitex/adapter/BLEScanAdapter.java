package android.example.retrofitex.adapter;

import android.bluetooth.BluetoothDevice;
import android.example.retrofitex.R;
import android.example.retrofitex.models.BLEDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BLEScanAdapter extends RecyclerView.Adapter<BLEScanAdapter.ViewHolder>{

    private List<BLEDevice> list;

    public BLEScanAdapter(List<BLEDevice> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public BLEScanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ble_scan_list, parent, false);
        return new BLEScanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BLEScanAdapter.ViewHolder holder, int position) {

        BLEDevice device = list.get(position);
        holder.BLEName.setText(device.getBeaconAddress());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView BLEName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            BLEName = itemView.findViewById(R.id.ble_name);
        }
    }
}
