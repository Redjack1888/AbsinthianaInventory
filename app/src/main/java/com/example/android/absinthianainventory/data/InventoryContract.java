package com.example.android.absinthianainventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.absinthianainventory";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.absinthianainventory/inventory/ is a valid path for
     * looking at item data. content://com.example.android.absinthianainventory/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class that defines constant values for the items database table.
     * Each entry in the table represents a single item.
     */
    public static final class ItemEntry implements BaseColumns {

        /** The content URI to access the item data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /** Name of database table for items */
        public final static String TABLE_NAME = "inventory";

        /**
         * Unique ID number for the item (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_NAME ="name";

        /**
         * Description of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_DESCRIPTION = "description";

        /**
         * Category of the item.
         *
         * The possible values are {{@link #CATEGORY_UNKNOWN}, {@link #CATEGORY_GLASSES},
         * or {@link #CATEGORY_SPOONS}, {@link #CATEGORY_FOUNTAINS}, {@link #CATEGORY_PIPES},
         * {@link #CATEGORY_CARAFES}, {@link #CATEGORY_BROUILLEURS},
         * {@link #CATEGORY_ACCESSORIES_SETS}, {@link #CATEGORY_OTHER_ACCESSORIES}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_CATEGORY = "category";

        /**
         * Price of the item.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_PRICE = "price";

        /**
         * Quantity of the item.
         *
         * Type: INTEGER
         */
        public static final String COLUMN_ITEM_QUANTITY = "quantity";

        /**
         * Supplier Name of the item.
         *
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplierName";

        /**
         * Supplier Phone number
         *
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_PHONE = "supplierPhone";

        /**
         * Supplier e-mail address
         *
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_EMAIL = "supplierEmail";

        /**
         * Image location of the item.
         *
         * Type: TEXT
         */
        public static final String COLUMN_ITEM_IMAGE = "productPic";

        /**
         * Possible values for the category of the item.
         */
        public static final int CATEGORY_UNKNOWN = 0;
        public static final int CATEGORY_GLASSES = 1;
        public static final int CATEGORY_SPOONS = 2;
        public static final int CATEGORY_FOUNTAINS = 3;
        public static final int CATEGORY_PIPES = 4;
        public static final int CATEGORY_CARAFES = 5;
        public static final int CATEGORY_BROUILLEURS = 6;
        public static final int CATEGORY_ACCESSORIES_SETS = 7;
        public static final int CATEGORY_OTHER_ACCESSORIES = 8;

        /**
         * Returns whether or not the given category is {@link #CATEGORY_UNKNOWN}, {@link #CATEGORY_GLASSES},
         * or {@link #CATEGORY_SPOONS}, {@link #CATEGORY_FOUNTAINS}, {@link #CATEGORY_PIPES},
         * {@link #CATEGORY_CARAFES}, {@link #CATEGORY_BROUILLEURS}, {@link #CATEGORY_ACCESSORIES_SETS},
         * {@link #CATEGORY_OTHER_ACCESSORIES}.
         */
        public static boolean isValidCategory(int category) {
            if (category == CATEGORY_UNKNOWN || category == CATEGORY_GLASSES || category == CATEGORY_SPOONS || category == CATEGORY_FOUNTAINS || category == CATEGORY_PIPES || category == CATEGORY_CARAFES || category == CATEGORY_BROUILLEURS || category == CATEGORY_ACCESSORIES_SETS || category == CATEGORY_OTHER_ACCESSORIES) {
                return true;
            }
            return false;
        }
    }

}