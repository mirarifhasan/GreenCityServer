package hsquad.greencityserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.util.Base64Utils;

import hsquad.greencityserver.Interface.ItemClickListener;
import hsquad.greencityserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress, txtOrderUserName;

    public Button editBtn, removeBtn, detailsBtn;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderUserName = (TextView)itemView.findViewById(R.id.order_userName);

        editBtn = (Button)itemView.findViewById(R.id.editBtn);
        removeBtn = (Button)itemView.findViewById(R.id.removeBtn);
        detailsBtn = (Button)itemView.findViewById(R.id.detailsBtn);

    }

}
