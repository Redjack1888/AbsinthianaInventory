package com.example.android.absinthianainventory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
//import android.app.LoaderManager;
import android.content.ContentValues;
//import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import com.example.android.absinthianainventory.data.InventoryContract.ItemEntry;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    /**
     * Identifier for the item data loader
     */
    private static final int EXISTING_ITEM_LOADER = 0;

    /**
     * Identifier for the permission to read external storage
     */
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 0;

    private static final String STATE_URI = "STATE_URI";

    /**
     * Content URI for the existing item (null if it's a new item)
     */
    private Uri mCurrentItemUri;

    /**
     * EditText field to enter the item's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the item's description
     */
    private EditText mDescriptionEditText;

    /**
     * EditText field to enter the item's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the item's price
     */
    private EditText mQuantityEdit;

    /**
     * EditText field to enter the Supplier name
     */
    private EditText mSupplierNameEdit;

    /**
     * EditText field to enter the Supplier phone number
     */
    private EditText mSupplierPhoneEdit;

    /**
     * EditText field to enter the Supplier email
     */
    private EditText mSupplierEmailEdit;

    /**
     * EditText field to enter the item's category
     */
    private Spinner mCategorySpinner;

    /**
     * Category of the item. The possible valid values are in the AbsinthianaContract.java file:
     * {@link ItemEntry#CATEGORY_UNKNOWN}, {@link ItemEntry#CATEGORY_GLASSES}, {@link ItemEntry#CATEGORY_SPOONS},
     * {@link ItemEntry#CATEGORY_FOUNTAINS}, {@link ItemEntry#CATEGORY_PIPES}, {@link ItemEntry#CATEGORY_CARAFES},
     * {@link ItemEntry#CATEGORY_BROUILLEURS}, {@link ItemEntry#CATEGORY_ACCESSORIES_SETS},
     * {@link ItemEntry#CATEGORY_OTHER_ACCESSORIES}.
     */
    private int mCategory = ItemEntry.CATEGORY_UNKNOWN;

    /**
     * Boolean flag that keeps track of whether the item has been edited (true) or not (false)
     */
    private boolean mItemHasChanged = false;

    ImageButton decreaseQuantity;
    ImageButton increaseQuantity;

    private Button imageBtn;
    private ImageView imageView;
    private Uri pictureUri;

//    Bitmap bitmap;
//
//    String email;
//    String emailValidationPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//    String phoneNumber;
//    String phoneNumberValidationPattern = "^[0-9-]+$";

//    long mCurrentItemId;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mItemHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // If the intent DOES NOT contain a item content URI, then we know that we are
        // creating a new item.
        if (mCurrentItemUri == null) {
            // This is a new item, so change the app bar to say "Add a Item"
            setTitle(getString(R.string.editor_activity_title_new_item));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a item that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing item, so change app bar to say "Edit Item"
            setTitle(getString(R.string.editor_activity_title_edit_item));

            // Initialize a loader to read the item data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_item_description);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mCategorySpinner = (Spinner) findViewById(R.id.spinner_category);
        mQuantityEdit = (EditText) findViewById(R.id.quantity_edit);
        mSupplierNameEdit = (EditText) findViewById(R.id.supplier_name_edit);
        mSupplierPhoneEdit = (EditText) findViewById(R.id.supplier_phone_edit);
        mSupplierEmailEdit = (EditText) findViewById(R.id.supplier_email_edit);

        decreaseQuantity = (ImageButton) findViewById(R.id.decrease_quantity);
        increaseQuantity = (ImageButton) findViewById(R.id.increase_quantity);

        imageBtn = (Button) findViewById(R.id.select_image);
        imageView = (ImageView) findViewById(R.id.image_view_holder);

        imageView.setImageURI(pictureUri);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEdit.setOnTouchListener(mTouchListener);
        mSupplierNameEdit.setOnTouchListener(mTouchListener);
        mSupplierPhoneEdit.setOnTouchListener(mTouchListener);
        mSupplierEmailEdit.setOnTouchListener(mTouchListener);
        mCategorySpinner.setOnTouchListener(mTouchListener);

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractOneToQuantity();
                mItemHasChanged = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumOneToQuantity();
                mItemHasChanged = true;
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenImageSelector();
                mItemHasChanged = true;
            }
        });

        setupSpinner();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (pictureUri != null)
            outState.putString(STATE_URI, pictureUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(STATE_URI) &&
                !savedInstanceState.getString(STATE_URI).equals("")) {
            pictureUri = Uri.parse(savedInstanceState.getString(STATE_URI));

            ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                   imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    imageView.setImageURI(pictureUri);
                }
            });
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the category of the item.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCategorySpinner.setAdapter(categorySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.category_glasses))) {
                        mCategory = ItemEntry.CATEGORY_GLASSES;
                    } else if (selection.equals(getString(R.string.category_spoons))) {
                        mCategory = ItemEntry.CATEGORY_SPOONS;
                    } else if (selection.equals(getString(R.string.category_fountains))) {
                        mCategory = ItemEntry.CATEGORY_FOUNTAINS;
                    } else if (selection.equals(getString(R.string.category_pipes))) {
                        mCategory = ItemEntry.CATEGORY_PIPES;
                    } else if (selection.equals(getString(R.string.category_carafes))) {
                        mCategory = ItemEntry.CATEGORY_CARAFES;
                    } else if (selection.equals(getString(R.string.category_brouilleurs))) {
                        mCategory = ItemEntry.CATEGORY_BROUILLEURS;
                    } else if (selection.equals(getString(R.string.category_accessories_set))) {
                        mCategory = ItemEntry.CATEGORY_ACCESSORIES_SETS;
                    } else if (selection.equals(getString(R.string.category_other_accessories))) {
                        mCategory = ItemEntry.CATEGORY_OTHER_ACCESSORIES;
                    } else {
                        mCategory = ItemEntry.CATEGORY_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCategory = ItemEntry.CATEGORY_UNKNOWN;
            }
        });
    }

    /**
     * Method to decrease by one the quantity
     */
    private void subtractOneToQuantity() {
        String previousValueString = mQuantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            mQuantityEdit.setText(String.valueOf(previousValue - 1));
        }
    }

    /**
     * Method to increase by one the quantity
     */
    private void sumOneToQuantity() {
        String previousValueString = mQuantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        mQuantityEdit.setText(String.valueOf(previousValue + 1));
    }

    /**
     * Method to open Image Selector - Permissions Requirements
     */
    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    /**
     * Method to open Image Selector - Intent
     */
    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            // This part of the code was
            if (resultData != null) {
                pictureUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + pictureUri.toString());
//                imageView.setImageBitmap(getBitmapFromUri(pictureUri));
                imageView.setImageURI(pictureUri);
//                imageView.invalidate();
            }
        }
    }

//    /**
//     * Method to take the Bitmap from the picture Uri
//     */
//    public Bitmap getBitmapFromUri(Uri uri) {
//
//        if (uri == null || uri.toString().isEmpty())
//            return null;
//
//        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();
//
//        InputStream input = null;
//        try {
//            input = this.getContentResolver().openInputStream(pictureUri);
//
//            // Get the dimensions of the bitmap
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            bmOptions.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(input, null, bmOptions);
//            input.close();
//
//            int photoW = bmOptions.outWidth;
//            int photoH = bmOptions.outHeight;
//
//            // Determine how much to scale down the image
//            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//
//            // Decode the image file into a Bitmap sized to fill the View
//            bmOptions.inJustDecodeBounds = false;
//            bmOptions.inSampleSize = scaleFactor;
//            bmOptions.inPurgeable = true;
//
//            input = this.getContentResolver().openInputStream(pictureUri);
//            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
//            input.close();
//            return bitmap;
//
//        } catch (FileNotFoundException fne) {
//            Log.e(LOG_TAG, "Failed to load image.", fne);
//            return null;
//        } catch (Exception e) {
//            Log.e(LOG_TAG, "Failed to load image.", e);
//            return null;
//        } finally {
//            try {
//                input.close();
//            } catch (IOException ioe) {
//
//            }
//        }
//    }

    /**
     * Verify input from editor before to save item into database.     *
     */
    private boolean addItem() {
        boolean isAllOk = true;
        if (!checkTheValues(mNameEditText, "name")) {
            isAllOk = false;
        }
        if (!checkTheValues(mDescriptionEditText, "description")) {
            isAllOk = false;
        }
        if (!checkTheValues(mPriceEditText, "price")) {
            isAllOk = false;
        }
        if (!checkTheValues(mQuantityEdit, "quantity")) {
            isAllOk = false;
        }
        if (!checkTheValues(mSupplierNameEdit, "supplier name")) {
            isAllOk = false;
        }
        if (!checkTheValues(mSupplierPhoneEdit, "supplier phone")) {
            isAllOk = false;
        }
        if (!checkTheValues(mSupplierEmailEdit, "supplier email")) {
            isAllOk = false;
        }
        if (pictureUri == null && mCurrentItemUri == null) {
            isAllOk = false;
            imageBtn.setError("Missing image");
        }

        if (!isAllOk) {
            return false;
        }
        return true;
    }

    /**
     * Get user input from editor and save item into database.
     */
    private void saveItem() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEdit.getText().toString().trim();
        String supplierNameString = mSupplierNameEdit.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEdit.getText().toString().trim();
        String supplierEmailString = mSupplierEmailEdit.getText().toString().trim();
//        String productPic = pictureUri.toString();

        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank
        if (mCurrentItemUri == null && pictureUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(descriptionString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneString) && TextUtils.isEmpty(supplierEmailString) && mCategory == ItemEntry.CATEGORY_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new item.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and item attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ItemEntry.COLUMN_ITEM_DESCRIPTION, descriptionString);
        values.put(ItemEntry.COLUMN_ITEM_CATEGORY, mCategory);
        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(ItemEntry.COLUMN_ITEM_PRICE, price);
        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
        values.put(ItemEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(ItemEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        values.put(ItemEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);
        if (pictureUri != null) {
            values.put(ItemEntry.COLUMN_ITEM_IMAGE, pictureUri.toString());
        }

//        else {
//            // If the new content URI is null, then there could be an error with insertion.
//            pictureUri = Uri.parse("android.resource://com.example.android.absinthianainventory/" + R.drawable.blank);
//            values.put(ItemEntry.COLUMN_ITEM_IMAGE, pictureUri.toString());
//        }

        // Determine if this is a new or existing item by checking if mCurrentItemUri is null or not
        if (mCurrentItemUri == null) {
            // This is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.
            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Put an error icon in the editor fields before to save item to Database.
     * Some fields such as Category and Product Image are not verified because the code offer a basic logic alternative to both missing fields
     */
    private boolean checkTheValues(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError("Missing product " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        // If this is a new item, hide the "Order" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_order);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // save item in DB
                if (!addItem()) {
                    // saying to onOptionsItemSelected that user clicked button
                    return true;
                }
                // save item in DB
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_order:
                // dialog with phone and email
                orderConfirmation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the inventory table
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_DESCRIPTION,
                ItemEntry.COLUMN_ITEM_CATEGORY,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_SUPPLIER_NAME,
                ItemEntry.COLUMN_SUPPLIER_PHONE,
                ItemEntry.COLUMN_SUPPLIER_EMAIL,
                ItemEntry.COLUMN_ITEM_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_DESCRIPTION);
            int categoryColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_CATEGORY);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_PHONE);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_IMAGE);


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            int category = cursor.getInt(categoryColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier_name = cursor.getString(supplierNameColumnIndex);
            String supplier_phone = cursor.getString(supplierPhoneColumnIndex);
            String supplier_email = cursor.getString(supplierEmailColumnIndex);
            String product_image = Uri.parse(cursor.getString(imageColumnIndex)).toString();


            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mDescriptionEditText.setText(description);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEdit.setText(Integer.toString(quantity));
            mSupplierNameEdit.setText(supplier_name);
            mSupplierPhoneEdit.setText(supplier_phone);
            mSupplierEmailEdit.setText(supplier_email);
            imageView.setImageURI(Uri.parse(product_image));

            // Category is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (category) {
                case ItemEntry.CATEGORY_GLASSES:
                    mCategorySpinner.setSelection(1);
                    break;
                case ItemEntry.CATEGORY_SPOONS:
                    mCategorySpinner.setSelection(2);
                    break;
                case ItemEntry.CATEGORY_FOUNTAINS:
                    mCategorySpinner.setSelection(3);
                    break;
                case ItemEntry.CATEGORY_PIPES:
                    mCategorySpinner.setSelection(4);
                    break;
                case ItemEntry.CATEGORY_CARAFES:
                    mCategorySpinner.setSelection(5);
                    break;
                case ItemEntry.CATEGORY_BROUILLEURS:
                    mCategorySpinner.setSelection(6);
                    break;
                case ItemEntry.CATEGORY_ACCESSORIES_SETS:
                    mCategorySpinner.setSelection(7);
                    break;
                case ItemEntry.CATEGORY_OTHER_ACCESSORIES:
                    mCategorySpinner.setSelection(8);
                    break;
                default:
                    mCategorySpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mCategorySpinner.setSelection(0); // Select "Unknown" category
        mPriceEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEdit.setText("");
        mSupplierNameEdit.setText("");
        mSupplierPhoneEdit.setText("");
        mSupplierEmailEdit.setText("");
        imageView.setImageDrawable(null);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */
    private void orderConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhoneEdit.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int price = Integer.parseInt(mPriceEditText.getText().toString().trim());
                int quantityRequired = 100;
                int totalPrice = price*quantityRequired;
                String supplierProductPrice = String.valueOf(price);
                String totalPriceString = String.valueOf(totalPrice);
                // intent to email
                Intent email_intent = new Intent(android.content.Intent.ACTION_SENDTO);
                email_intent.setType("text/plain");
                email_intent.setData(Uri.parse("mailto:" + mSupplierEmailEdit.getText().toString().trim()));
                email_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order");
                String bodyMessage = "Please send us as soon as possible the following product:\n\n" +
                        mNameEditText.getText().toString().trim() +
                        "\nQuantity required: " + quantityRequired + " pieces\n" +
                        "Total Price: " + totalPriceString + "€" + " ( "+ quantityRequired + " x " + supplierProductPrice + "€)\n\n" +
                        "Sure in your answer.\nBest Regards.";
                email_intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(Intent.createChooser(email_intent, "Send email..."));
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the item in the database.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentItemUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}