    package com.example.qrscanner;

    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.drawable.ColorDrawable;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.PickVisualMediaRequest;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.cardview.widget.CardView;
    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.example.qrscanner.DB.DBHelper;
    import com.example.qrscanner.adapter.DepartmentAdapter;
    import com.example.qrscanner.adapter.GadgetsAdapter;
    import com.example.qrscanner.adapter.ItemAdapter;
    import com.example.qrscanner.models.Assigned_to_User_Model;
    //    import com.example.qrscanner.options.Data;
    import com.example.qrscanner.models.Department;
    import com.example.qrscanner.models.Gadgets;
    import com.example.qrscanner.utils.Utils;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.Date;
    import java.util.List;

    public class UpdateData extends AppCompatActivity {

        private EditText assignedTo, deviceModel, datePurchased;
        private TextView qrText, dateExpired, status, availability, titleTextView, chooser, chooserDepartment;
        private ImageView backBtn, settings, currentActivity, add_newGadget, currentIcon, currentIcon2, imageViewGadget;
        private CardView saveBtn, cancelBtn;
        private GadgetsAdapter gadgetsAdapter;
        private DepartmentAdapter departmentAdapter;
        private String gadgetCategoryName, departmentCategoryName;
        private ArrayList<String> serialNum_id, assignedTo_id, department_id, device_id, deviceModel_id, datePurchased_id, dateExpire_id, status_id, availability_id;
        private DBHelper dbHelper;
        private static final String pattern = "MM/dd/yy";


        private Department departmentPosition;

        private ItemAdapter itemAdapter;
        private Gadgets gadgetPosition;
        private ListView listView;
        private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

        private void updateAvailabilityStatus() {
            if (!assignedTo.getText().toString().isEmpty()) {
                availability.setText("In Use");
            } else {
                availability.setText("In Stock");
            }
        }

        // Constructor to accept ItemAdapter reference
        public UpdateData() {

        }


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                updateSaveButtonState();
                updateAvailabilityStatus();


            }

        };

        private void updateSaveButtonState() {
            if (!deviceModel.getText().toString().isEmpty() && !datePurchased.getText().toString().isEmpty()) {
                if (assignedTo.getText().toString().isEmpty()) {
                    saveBtn.setEnabled(true);
                } else {
                    saveBtn.setEnabled(true);
                }
            } else {
                saveBtn.setEnabled(false);
            }
        }


        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_update_data);
//            LayoutInflater inflater = LayoutInflater.from(this);;
//            ViewGroup parentViewGroup = findViewById(R.id.main);
//            View infoLayout = inflater.inflate(R.layout.info_layout, parentViewGroup, false);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });


            currentActivity = findViewById(R.id.currentActivity);
            currentActivity.setImageResource(R.drawable.ic_edit);

            titleTextView = findViewById(R.id.titleTextView);
            titleTextView.setText("Update Data");

            backBtn = findViewById(R.id.backBtn);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            settings = findViewById(R.id.settingsIcon);
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UpdateData.this, Settings.class);
                    startActivity(intent);
//                    Log.d("TAG", "Clicked Success");
                }
            });



            dbHelper = new DBHelper(this);

            qrText = findViewById(R.id.qrText);


            assignedTo = findViewById(R.id.assignedTo);
            chooserDepartment = findViewById(R.id.chooserDepartment);
            deviceModel = findViewById(R.id.deviceModel);
            datePurchased = findViewById(R.id.textDatePurchased);
            dateExpired = findViewById(R.id.textDateExpired);
            status = findViewById(R.id.status);
            availability = findViewById(R.id.availability);
            saveBtn = findViewById(R.id.saveBtn);

            imageViewGadget = findViewById(R.id.imageViewGadget);
            chooser = findViewById(R.id.chooser);

            ArrayList<Assigned_to_User_Model> deviceList = new ArrayList<>();
            serialNum_id = new ArrayList<>();
            assignedTo_id = new ArrayList<>();
            department_id = new ArrayList<>();
            device_id = new ArrayList<>();
            deviceModel_id = new ArrayList<>();
            datePurchased_id = new ArrayList<>();
            dateExpire_id = new ArrayList<>();
            status_id = new ArrayList<>();
            availability_id = new ArrayList<>();
            itemAdapter = new ItemAdapter(R.layout.info_layout, this, deviceList, serialNum_id, assignedTo_id, department_id, device_id, deviceModel_id, datePurchased_id, dateExpire_id, status_id, availability_id, null, null);;


            // Get Value from intent
            qrText.setText(getIntent().getStringExtra("serialNumber"));
            assignedTo.setText(getIntent().getStringExtra("name"));
            chooserDepartment.setText(getIntent().getStringExtra("department"));

            chooser.setText(getIntent().getStringExtra("device"));
            deviceModel.setText(getIntent().getStringExtra("deviceModel"));
            datePurchased.setText(getIntent().getStringExtra("datePurchased"));
            dateExpired.setText(getIntent().getStringExtra("dateExpired"));
            status.setText(getIntent().getStringExtra("status"));
            availability.setText(getIntent().getStringExtra("availability"));
            updateAvailabilityStatus();

            assignedTo.addTextChangedListener(textWatcher);
            deviceModel.addTextChangedListener(textWatcher);
            datePurchased.addTextChangedListener(textWatcher);
            dateExpired.addTextChangedListener(textWatcher);
            status.addTextChangedListener(textWatcher);

            // Add chooser here
            chooser.setOnClickListener(v -> {
                clickOptionDevice();
                chooser.setText(getIntent().getStringExtra("device"));
            });

            chooserDepartment.setOnClickListener(v -> {
                clickDepartmentCategory();
                Log.d("TAG", "onCreate: Call clickDepCatMethod");
                int itemCount = departmentAdapter.getCount();
                int itemCount2 = getDepartmentCategoryFromDatabase().size();

                Log.d("TAG", "department adapter item count " + itemCount);
                Log.d("TAG", "department db item count " + itemCount2);
                if (itemCount > 0) {
                    Log.d("TAG", "department item count " + itemCount);
                }
            });

            //Icon picker for ic_edit gadget items
            pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    // Handle the selected image URI
                    currentIcon.setImageURI(uri);
                    currentIcon2.setImageURI(uri);


                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

            cancelBtn = findViewById(R.id.cancelBtn);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


            datePurchased.setOnFocusChangeListener((view, hasFocus) -> {
                if (!hasFocus) {
                    // Parse the input date
                    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                    Date inputDate;

                    try {
                        inputDate = dateFormat.parse(datePurchased.getText().toString());
                    } catch (ParseException e) {
                        // Toast "Invalid date format. "
                        return;
                    }

                    // Calculate expiration date and set status
                    Utils.calculateExpirationAndStatus(inputDate, dateExpired, status);
                    itemAdapter.notifyDataSetChanged();
                }
            });


            //SAVE BUTTON
            saveBtn.setOnClickListener(v -> {
                Intent intent = new Intent();
                if (!deviceModel.getText().toString().isEmpty() && !datePurchased.getText().toString().isEmpty()) {
                    if (assignedTo.getText().toString().isEmpty() || !assignedTo.getText().toString().isEmpty()) {
                        Assigned_to_User_Model assigned = new Assigned_to_User_Model();

                        // Proceed with saving data
                        dbHelper.editDevice(getIntent().getStringExtra("serialNumber"),  assignedTo.getText().toString(), chooserDepartment.getText().toString(), gadgetCategoryName, deviceModel.getText().toString(), datePurchased.getText().toString(), dateExpired.getText().toString(), status.getText().toString(), availability.getText().toString());

                        Log.d("Saved?", "onCreate: " + deviceModel.getText().toString());
                        deviceList.add(assigned);

                        intent.putExtra("dataRefreshed", true);
                        setResult(RESULT_OK, intent);
                        finish();
//                        Toast toast = Toast.makeText(UpdateData.this, "Saved Success", Toast.LENGTH_SHORT);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();


                    }
                } else {
                    // Toast "Save Failed", "Please fill up all fields"
                }

            });
        }
        private void clickOptionDevice() {

            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateData.this, R.style.AlertDialogTheme);
            View customView = LayoutInflater.from(UpdateData.this).inflate(R.layout.layout_show_option, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));

            // Find the ListView in your custom layout
            listView = customView.findViewById(R.id.list_item_option);

            // Set the adapter for the ListView
            listView.setAdapter(new GadgetsAdapter(UpdateData.this, getGadgetsCategoryFromDatabase()));

            // Set the custom view to the AlertDialog.Builder
            builder.setView(customView);

            // Create and show the AlertDialog
            AlertDialog itemDialog = builder.create();
            if (itemDialog.getWindow() != null) {
                itemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            itemDialog.show();


            add_newGadget = customView.findViewById(R.id.addGadgetBtn);
            add_newGadget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddNewGadgetDialog();
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    gadgetPosition = (Gadgets) parent.getItemAtPosition(position);
                    if (gadgetPosition != null) {
                        gadgetCategoryName = gadgetPosition.getGadgetCategoryName();
                        itemDialog.dismiss();
                        chooser.setText(gadgetCategoryName);
                        int positionNew = position+1;
                        displayGadgetImageInt(UpdateData.this, imageViewGadget, positionNew);

                        Toast.makeText(UpdateData.this, "Selected " + gadgetCategoryName, Toast.LENGTH_SHORT).show();
                    } else {
                        //TODO fix this
                        gadgetCategoryName = "Unknown";
                        Toast.makeText(UpdateData.this, "Null pos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    gadgetPosition = (Gadgets) parent.getItemAtPosition(position);
                    showEditDialog(gadgetPosition);
                    gadgetCategoryName = gadgetPosition.getGadgetCategoryName();
                    Toast.makeText(UpdateData.this, gadgetCategoryName, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        private void showEditDialog(final Gadgets gadgets) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateData.this, R.style.AlertDialogTheme);
            View view = LayoutInflater.from(UpdateData.this).inflate(R.layout.layout_edit_spinner_item, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));
            builder.setView(view);

            // Set up the input
            final EditText input = view.findViewById(R.id.editText);
            gadgetCategoryName = gadgetPosition.getGadgetCategoryName();
            input.setText(gadgetCategoryName);
            final Button actionOK = view.findViewById(R.id.actionOK);
            final Button actionCancel = view.findViewById(R.id.actionCancel);
            final Button actionDelete = view.findViewById(R.id.actionDelete);
            final ImageView iconICPick = view.findViewById(R.id.addIconGadget);
            ImageView imageViewCurrent = view.findViewById(R.id.currentIconA);

            // Set the ImageView resource Based on what user pick
            currentIcon = iconICPick;
            currentIcon2 = imageViewCurrent;

            byte[] gImage = gadgetPosition.getImage();

            final AlertDialog deviceChooserDialog = builder.create();
            if (deviceChooserDialog.getWindow() != null) {
                deviceChooserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            deviceChooserDialog.show();

            iconICPick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
            });


            // Set up the buttons
            actionOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newGadgetCategoryName = input.getText().toString();
                    byte[] newGadgetImage = Utils.imageViewToByte(UpdateData.this, iconICPick);

//                Toast.makeText(UpdateData.this, "Gadget to ic_edit ID:" + gadgets.getId(), Toast.LENGTH_SHORT).show();

                    if (newGadgetCategoryName.isEmpty()) {
                        Toast.makeText(UpdateData.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        if (newGadgetImage == null) {
                            if (gImage != null) {
                                newGadgetImage = gImage;
                            }
                        } else {
                            newGadgetImage = Utils.imageViewToByte(UpdateData.this, iconICPick);
                        }
                        dbHelper.updateGadgetCategory(gadgets, newGadgetCategoryName, newGadgetImage);
                        updateGadgetList();
                        deviceChooserDialog.dismiss();

                    }
                }
            });

            actionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UpdateData.this, "Gadget to delete ID:" + gadgets.getId(), Toast.LENGTH_SHORT).show();
//                gadgetsAdapter = new GadgetsAdapter(UpdateData.this, getGadgetsFromDatabase());
                    dbHelper.deleteGadgetCategory(gadgets);
//                gadgetsAdapter.notifyDataSetChanged();
                    updateGadgetList();
                    deviceChooserDialog.dismiss();
                    Toast.makeText(UpdateData.this, "Delete Btn Clicked", Toast.LENGTH_SHORT).show();
                }
            });

            actionCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceChooserDialog.dismiss();
                }
            });
        }


        private void showAddNewGadgetDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateData.this, R.style.AlertDialogTheme);
            View view = LayoutInflater.from(UpdateData.this).inflate(R.layout.layout_add_new_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));
            builder.setView(view);

            final ImageView imageView = view.findViewById(R.id.warning);
            imageView.setImageResource(R.drawable.add_icon);
            // Set up the input
            final EditText input = view.findViewById(R.id.editText);
            final Button actionOK = view.findViewById(R.id.actionOK);
            final Button actionCancel = view.findViewById(R.id.actionCancel);
            final ImageView iconICPick = view.findViewById(R.id.addIconGadget);
            ImageView imageViewCurrent = view.findViewById(R.id.currentIconA);

            // Set the ImageView resource Based on what user pick
            currentIcon = iconICPick;
            currentIcon2 = imageViewCurrent;

            final AlertDialog deviceChooserDialog = builder.create();
            if (deviceChooserDialog.getWindow() != null) {
                deviceChooserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            deviceChooserDialog.show();

            iconICPick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UpdateData.this, "Add image clicked", Toast.LENGTH_SHORT).show();
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
            });


            // Set up the buttons
            actionOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newGadgetCategoryName = input.getText().toString();
                    byte[] newGadgetImage = Utils.imageViewToByte(UpdateData.this, iconICPick);

                    if (newGadgetCategoryName.isEmpty()) {
                        Toast.makeText(UpdateData.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {

                        if (newGadgetImage == null) {
                            newGadgetImage = Utils.getDefaultImageByteArray(UpdateData.this, R.drawable.device_model);
                        }
                        dbHelper.addGadgetCategory(newGadgetCategoryName, newGadgetImage);
                        updateGadgetList();
                        deviceChooserDialog.dismiss();

                    }
                }
            });

            actionCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceChooserDialog.dismiss();
                }
            });
        }


        private void updateGadgetList() {
            List<Gadgets> gadgetsList = getGadgetsCategoryFromDatabase();
            gadgetsAdapter = new GadgetsAdapter(UpdateData.this, gadgetsList);
            listView.setAdapter(gadgetsAdapter);
            gadgetsAdapter.notifyDataSetChanged();
        }

        private List<Gadgets> getGadgetsCategoryFromDatabase() {
            List<Gadgets> gadgetsList = dbHelper.getAllGadgetsCategory();

            // Add default gadgets if database is empty
            if (gadgetsList.isEmpty()) {
                dbHelper.addGadgetCategory("Unknown", Utils.getDefaultImageByteArray(UpdateData.this, R.drawable.ic_unknown_device));
                dbHelper.addGadgetCategory("Laptop", Utils.getDefaultImageByteArray(UpdateData.this, R.drawable.laptop_icon));
                dbHelper.addGadgetCategory("Phone", Utils.getDefaultImageByteArray(UpdateData.this, R.drawable.mobile_phone_2635));
                dbHelper.addGadgetCategory("Tablet", Utils.getDefaultImageByteArray(UpdateData.this, R.drawable.ic_tablet));
                dbHelper.addGadgetCategory("Desktop", Utils.getDefaultImageByteArray(UpdateData.this, R.drawable.pc_computer_6840));

                gadgetsList = dbHelper.getAllGadgetsCategory();
            }

            return gadgetsList;
        }

        public void displayGadgetImageInt(Context context, ImageView imageView, int gadgetId) {
            byte[] imageBytes = dbHelper.getGadgetCategoryImageInt(gadgetId);
            if (imageBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageView.setImageBitmap(bitmap);
            } else {
                // Set a default image if no image is found
                imageView.setImageResource(R.drawable.device_model);
            }
        }
        // END GADGETS CHOOSER

        private void clickDepartmentCategory() {

            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateData.this, R.style.AlertDialogTheme);
            View customView = LayoutInflater.from(UpdateData.this).inflate(R.layout.layout_show_option, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));

            TextView titleText = customView.findViewById(R.id.titleText);
            titleText.setText("Select Department");

            // Find the ListView in your custom layout
            listView = customView.findViewById(R.id.list_item_option);

            // Set the adapter for the ListView
            List<Department> departmentList = getDepartmentCategoryFromDatabase();
            departmentAdapter = new DepartmentAdapter(UpdateData.this, departmentList);
            listView.setAdapter(departmentAdapter);

            // Set the custom view to the AlertDialog.Builder
            builder.setView(customView);

            // Create and show the AlertDialog
            AlertDialog itemDialogGadgetsCategory = builder.create();
            if (itemDialogGadgetsCategory.getWindow() != null) {
                itemDialogGadgetsCategory.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            itemDialogGadgetsCategory.show();


            add_newGadget = customView.findViewById(R.id.addGadgetBtn);
            add_newGadget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "onClick: clicked on add in department");
                    showAddNewDepartmentDialog();
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    departmentPosition = (Department) parent.getItemAtPosition(position);
                    if (departmentPosition != null) {
                        departmentCategoryName = departmentPosition.getDepartmentCategoryName();
                        itemDialogGadgetsCategory.dismiss();
                        chooserDepartment.setText(departmentCategoryName);

                        Toast.makeText(UpdateData.this, "Selected " + departmentCategoryName, Toast.LENGTH_SHORT).show();
                    } else {
                        departmentCategoryName = "Unknown";
                        Toast.makeText(UpdateData.this, "Null pos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    departmentPosition = (Department) parent.getItemAtPosition(position);
                    showEditDepartmentCategoryDialog(departmentPosition);
                    gadgetCategoryName = departmentPosition.getDepartmentCategoryName();
                    Toast.makeText(UpdateData.this, departmentCategoryName, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        private List<Department> getDepartmentCategoryFromDatabase() {
            List<Department> departmentList = dbHelper.getAllDepartmentCategory();

            // Add default departments if the list is empty
            if (departmentList.isEmpty()) {
                dbHelper.addDepartmentCategory("HR");
                dbHelper.addDepartmentCategory("Finance");
                dbHelper.addDepartmentCategory("IT");
                dbHelper.addDepartmentCategory("Operations");
                departmentList = dbHelper.getAllDepartmentCategory();
            }

            return departmentList;
        }


        private void updateDepartmentList() {
            List<Department> departmentList = getDepartmentCategoryFromDatabase();
            departmentAdapter = new DepartmentAdapter(UpdateData.this, departmentList);
            listView.setAdapter(departmentAdapter);
            departmentAdapter.notifyDataSetChanged();
        }


        private void showAddNewDepartmentDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateData.this, R.style.AlertDialogTheme);
            View view = LayoutInflater.from(UpdateData.this).inflate(R.layout.layout_add_new_dialog, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));
            builder.setView(view);

            final ImageView imageView = view.findViewById(R.id.warning);
            imageView.setImageResource(R.drawable.add_icon);
            // Set up the input
            final EditText input = view.findViewById(R.id.editText);
            final Button actionOK = view.findViewById(R.id.actionOK);
            final Button actionCancel = view.findViewById(R.id.actionCancel);
            final ImageView iconICPick = view.findViewById(R.id.addIconGadget);
            ImageView imageViewCurrent = view.findViewById(R.id.currentIconA);
            iconICPick.setVisibility(View.GONE);
            imageViewCurrent.setVisibility(View.GONE);

            final AlertDialog deviceChooserDialog = builder.create();
            if (deviceChooserDialog.getWindow() != null) {
                deviceChooserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            deviceChooserDialog.show();


            // Set up the buttons
            actionOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newDepartmentCategoryName = input.getText().toString();

                    if (newDepartmentCategoryName.isEmpty()) {
                        Toast.makeText(UpdateData.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {

                        dbHelper.addDepartmentCategory(newDepartmentCategoryName);
                        updateDepartmentList();
                        deviceChooserDialog.dismiss();

                    }
                }
            });

            actionCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceChooserDialog.dismiss();
                }
            });
        }


        private void showEditDepartmentCategoryDialog(final Department department) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateData.this, R.style.AlertDialogTheme);
            View view = LayoutInflater.from(UpdateData.this).inflate(R.layout.layout_edit_spinner_item, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));
            builder.setView(view);

            // Set up the input
            final EditText input = view.findViewById(R.id.editText);
            departmentCategoryName = departmentPosition.getDepartmentCategoryName();
            input.setText(departmentCategoryName);
            final Button actionOK = view.findViewById(R.id.actionOK);
            final Button actionCancel = view.findViewById(R.id.actionCancel);
            final Button actionDelete = view.findViewById(R.id.actionDelete);
            final ImageView iconICPick = view.findViewById(R.id.addIconGadget);
            ImageView imageViewCurrent = view.findViewById(R.id.currentIconA);
            iconICPick.setVisibility(View.GONE);
            imageViewCurrent.setVisibility(View.GONE);


            final AlertDialog departmentChooserDialog = builder.create();
            if (departmentChooserDialog.getWindow() != null) {
                departmentChooserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            departmentChooserDialog.show();

            iconICPick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
            });

            // Set up the buttons
            actionOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int departmentId = departmentPosition.getDepartmentCategoryId();
                    String newDepartmentCategoryName = input.getText().toString();

                    if (newDepartmentCategoryName.isEmpty()) {
                        Toast.makeText(UpdateData.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {

                        dbHelper.editDepartmentCategory(departmentId, newDepartmentCategoryName);
                        updateDepartmentList();
                        departmentChooserDialog.dismiss();

                    }
                }
            });

            actionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UpdateData.this, "Gadget to delete ID:" + department.getDepartmentCategoryId(), Toast.LENGTH_SHORT).show();
                    dbHelper.deleteDepartmentCategory(department);
                    updateDepartmentList();
                    departmentChooserDialog.dismiss();
                    Toast.makeText(UpdateData.this, "Delete Btn Clicked", Toast.LENGTH_SHORT).show();
                }
            });

            actionCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    departmentChooserDialog.dismiss();
                }
            });
        }

    }