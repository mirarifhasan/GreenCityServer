package hsquad.greencityserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import hsquad.greencityserver.Common.Common;
import hsquad.greencityserver.Interface.ItemClickListener;
import hsquad.greencityserver.Model.Category;
import hsquad.greencityserver.Model.PlantService;
import hsquad.greencityserver.ViewHolder.PlantServiceViewHolder;

public class PlantServiceListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout rootLayout;

    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference plant_serviceList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryID = "";

    FirebaseRecyclerAdapter<PlantService, PlantServiceViewHolder> adapter;

    // Add new Plant or Service
    EditText mName, mDescription, mPrice, mDiscount;
    Button selectImageBtn, uploadBtn;

    PlantService newPlantService;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_service_list);

        //Firebase
        db = FirebaseDatabase.getInstance();
        plant_serviceList = db.getReference("Plant_Service");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init
        recyclerView = (RecyclerView)findViewById(R.id.recycler_plant_service);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPlantServiceDialog();
            }
        });

        if(getIntent() != null)
            categoryID = getIntent().getStringExtra("CategoryID");
        if(!categoryID.isEmpty())
            loadListPlant_Service(categoryID);
    }

    private void showAddPlantServiceDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlantServiceListActivity.this);
        alertDialog.setTitle("Add new plant/service");
        alertDialog.setMessage("Please fill full Information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_plant_service_layout, null);

        mName = add_menu_layout.findViewById(R.id.nameEditText);
        mDescription = add_menu_layout.findViewById(R.id.descriptionEditText);
        mPrice = add_menu_layout.findViewById(R.id.priceEditText);
        mDiscount = add_menu_layout.findViewById(R.id.discountEditText);

        selectImageBtn = add_menu_layout.findViewById(R.id.selectImageButton);
        uploadBtn = add_menu_layout.findViewById(R.id.uploadButton);

        //Event for Button
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterfce, int which) {
                dialogInterfce.dismiss();

                //Here, just create a new category
                if(newPlantService != null){
                    plant_serviceList.push().setValue(newPlantService);
                    Snackbar.make(rootLayout, "New category " + newPlantService.getName() + " added", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterfce, int which) {
                dialogInterfce.dismiss();
            }
        });
        alertDialog.show();
    }

    private void chooseImage() {
        /*
        Intent intent = new Intent();
        intent.setType("images/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        */

        //Different tutorial
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading..");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);

            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(PlantServiceListActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Set value for newCategory if image upload and we can gt download link
                                    newPlantService = new PlantService();

                                    newPlantService.setName(mName.getText().toString());
                                    newPlantService.setDescription(mDescription.getText().toString());
                                    newPlantService.setPrice(mPrice.getText().toString());
                                    newPlantService.setDiscount(mDiscount.getText().toString());
                                    newPlantService.setMenuID(categoryID);
                                    newPlantService.setImage(uri.toString());

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(PlantServiceListActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded " + progress + "%");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            saveUri = data.getData();
            selectImageBtn.setText("Image selected");
        }
    }

    private void loadListPlant_Service(String categoryID) {
        adapter = new FirebaseRecyclerAdapter<PlantService, PlantServiceViewHolder>(PlantService.class, R.layout.plant_item,
                PlantServiceViewHolder.class, plant_serviceList.orderByChild("menuID").equalTo(categoryID)) {
            @Override
            protected void populateViewHolder(PlantServiceViewHolder viewHolder, PlantService model, int position) {
                viewHolder.plant_serviceName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.plant_serviceImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdatePlantServiceDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){
            deleteItem(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteItem(String key) {
        plant_serviceList.child(key).removeValue();
        Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
    }

    private void showUpdatePlantServiceDialog(String key, final PlantService item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlantServiceListActivity.this);
        alertDialog.setTitle("Edit plant/service");
        alertDialog.setMessage("Please fill full Information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_plant_service_layout, null);

        mName = add_menu_layout.findViewById(R.id.nameEditText);
        mDescription = add_menu_layout.findViewById(R.id.descriptionEditText);
        mPrice = add_menu_layout.findViewById(R.id.priceEditText);
        mDiscount = add_menu_layout.findViewById(R.id.discountEditText);

        selectImageBtn = add_menu_layout.findViewById(R.id.selectImageButton);
        uploadBtn = add_menu_layout.findViewById(R.id.uploadButton);

        //Set default Name for editor view
        mName.setText(item.getName());
        mDescription.setText(item.getDescription());
        mPrice.setText(item.getPrice());
        mDiscount.setText(item.getDiscount());

        //Event for Button
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterfce, int which) {
                dialogInterfce.dismiss();

                //Update Information
                item.setName(mName.getText().toString());
                item.setDescription(mDescription.getText().toString());
                item.setPrice(mPrice.getText().toString());
                item.setDiscount(mDiscount.getText().toString());

                plant_serviceList.push().setValue(item);
                Snackbar.make(rootLayout, "Item " + item.getName() + " edited", Snackbar.LENGTH_LONG).show();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterfce, int which) {
                dialogInterfce.dismiss();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final PlantService item) {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading..");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("GreenCity_Category/" + imageName);

            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(PlantServiceListActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Set value for newCategory if image upload and we can gt download link
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(PlantServiceListActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded " + progress + "%");
                }
            });
        }
    }
}
