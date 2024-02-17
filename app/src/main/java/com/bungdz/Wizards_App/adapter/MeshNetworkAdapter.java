package com.bungdz.Wizards_App.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bungdz.Wizards_App.MainActivity;
import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.models.Alarm;
import com.bungdz.Wizards_App.models.NodeInfo;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeshNetworkAdapter extends RecyclerView.Adapter<MeshNetworkAdapter.ViewHolder> {
    private List<NodeInfo> dataList;
    private Context mContext;

    private List<Pair<String, String>> keyValuePairList;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private ArrayList<String> listRole;

    private static final String CORRECT_PIN = "1234"; // Mã PIN đúng
    public MeshNetworkAdapter(List<NodeInfo> dataList, Context mContext, ArrayList<String> listRole) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.listRole = listRole;
        keyValuePairList = new ArrayList<>();
    }

    public List<Pair<String, String>> getKeyValuePairList() {
        return keyValuePairList;
    }

    public void addMesh(NodeInfo nodeInfo) {
        dataList.add(nodeInfo);
        notifyItemInserted(dataList.size() - 1);
    }

    public void deleteMesh(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mesh_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NodeInfo nodeInfo = dataList.get(position);
        keyValuePairList.add(new Pair<>(nodeInfo.getRole(), ""+position));
        holder.textViewRole.setText(nodeInfo.getRole());
        holder.textViewUUID.setText("UUID:   " + nodeInfo.getUuid());
        holder.textViewUnicast.setText("Unicast Addr:   " + nodeInfo.getUnicast());
        holder.textViewParent.setText("LPN:   " + nodeInfo.getParent());
        holder.textViewNodeKey.setText("Role:   " + nodeInfo.getRole());
        if(nodeInfo.getRole().equals("Gateway")){

        }else {
            holder.buttonUnprovision.setVisibility(View.VISIBLE);
            holder.buttonUnprovision.setOnClickListener(v -> showPasswordInputDialog(holder.getBindingAdapterPosition()));
        }
    }

    private void showPasswordInputDialog(final int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.dialog_password_input, null);
        final EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);

        builder.setView(dialogView)
                .setTitle("Nhập mật khẩu")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String enteredPassword = passwordEditText.getText().toString();
                    // Kiểm tra mật khẩu
                    if (enteredPassword.equals(CORRECT_PIN)) {

                        performAction(adapterPosition);
                    } else {
                        uiHandler.post(() -> {
                            Toast.makeText(mContext, "Mật khẩu sai", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void performAction(int adapterPosition) {
        Log.d("Button Clicked", "Button clicked for position: " + adapterPosition);
        NodeInfo nodeInfo1 = dataList.get(adapterPosition);
        String nodeKey = nodeInfo1.getRole();
        ThingsBoardHandle.deleteMesh(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[0].getDeviceID(), nodeKey, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println(listRole);
                Log.d("deleteMesh", "Xoa thanh cong " + adapterPosition);
                if (listRole.contains(nodeKey)) {
                    listRole.remove(nodeKey);
                    System.out.println(listRole);
                    System.out.println("Đã xoá phần tử: " + nodeKey);
                } else {
                    System.out.println("Không tìm thấy phần tử để xoá: " + nodeKey);
                }
                uiHandler.post(() -> {
                    dataList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                });
                int index = ThingsBoardInfo.getIndexThingsBoard(nodeKey);
                String allField = "fan,led,device1,device2,device3,temperature,humidity";
                ThingsBoardHandle.httpDeleteTelematryData(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[index].getDeviceID(), allField, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewRole;
        public TextView textViewUUID;
        public TextView textViewUnicast;
        public TextView textViewParent;

        public TextView textViewNodeKey;
        public Button buttonUnprovision;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRole = itemView.findViewById(R.id.textViewRole);
            textViewUUID = itemView.findViewById(R.id.textViewUUID);
            textViewUnicast = itemView.findViewById(R.id.textViewUnicast);
            textViewParent = itemView.findViewById(R.id.textViewParent);
            textViewNodeKey = itemView.findViewById(R.id.textViewNodeKey);
            buttonUnprovision = itemView.findViewById(R.id.buttonUnprovision);
        }
    }
}
