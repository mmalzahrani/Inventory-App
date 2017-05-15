/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.inventory.inventoryapp.data.InventoryContract.ProductEntry;


/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {

    // Get current quantity from DB
    private Uri mCurrentProductUri;


    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {


        // Get current position
        final int position = cursor.getPosition();

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);


        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        quantityTextView.setText(productQuantity);
        priceTextView.setText(productPrice);

        /*
         * Order button
         * make the button onClickListener
         * once click reduce the quantity by 1
         */
        Button button = (Button) view.findViewById(R.id.order_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Move to the position of the cursor
                cursor.moveToPosition(position);

                // Find the columns of product attributes that we're interested in
                // Find the columns of product attributes that we're interested in
                int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
                int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
                int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
                int supplierEmailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
                int pictureColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE);

                // Extract out the value from the Cursor for the given column index
                int id = cursor.getInt(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                int price = cursor.getInt(priceColumnIndex);
                int quantity = cursor.getInt(quantityColumnIndex);
                String supplier = cursor.getString(supplierColumnIndex);
                String supplierEmail = cursor.getString(supplierEmailColumnIndex);
                byte[] picture = cursor.getBlob(pictureColumnIndex);


                // Get current uri for the product
                mCurrentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);


                if (quantity != 0) {

                    quantity = quantity - 1;

                    // Create a ContentValues object where column names are the keys,
                    // and product attributes from the editor are the values.
                    ContentValues values = new ContentValues();

                    values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);

                    values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);

                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

                    values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);

                    values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmail);

                    values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, picture);

                    // Update the quantity index and notify the cursor for the change
                    context.getContentResolver().update(mCurrentProductUri, values, null, null);

                    // Set notification URI on the Cursor,
                    // so we know what content URI the Cursor was created for.
                    // If the data at this URI changes, then we know we need to update the Cursor.
                    cursor.setNotificationUri(context.getContentResolver(), mCurrentProductUri);

                } else {
                    Toast.makeText(context, context.getString(R.string.toast_product_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
