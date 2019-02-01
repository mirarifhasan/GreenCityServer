package hsquad.greencityserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import hsquad.greencityserver.Common.Common;
import hsquad.greencityserver.Interface.ItemClickListener;
import hsquad.greencityserver.R;

public class PlantServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView plant_serviceName;
    public ImageView plant_serviceImage;

    private ItemClickListener itemClickListener;

    public PlantServiceViewHolder(View itemView){
        super(itemView);

        plant_serviceName = (TextView)itemView.findViewById(R.id.plant_service_nameTV);
        plant_serviceImage = (ImageView)itemView.findViewById(R.id.plant_service_imageIV);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view){
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}