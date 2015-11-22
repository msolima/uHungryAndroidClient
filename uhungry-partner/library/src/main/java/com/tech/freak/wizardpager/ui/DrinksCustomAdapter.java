package com.tech.freak.wizardpager.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.tech.freak.wizardpager.R;
import com.tech.freak.wizardpager.model.DrinkItem;
import com.tech.freak.wizardpager.model.DrinksCache;
import com.tech.freak.wizardpager.model.SelectedDrinkItem;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

public class DrinksCustomAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<DrinkItem> lProductList;
    private Context context;
    private DrinkItem currentDrinkItem;
    private View currentView;
    private MaterialDialog currentDialog;
    public DrinksCustomAdapter(ArrayList<DrinkItem> aInProductList, Context context) {
        lProductList = aInProductList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lProductList.size();
    }

    @Override
    public Object getItem(int pos) {
        return lProductList.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

//    public void setlMap(HashMap<String,Integer> aInMap)
//    {
//        lMap.putAll(aInMap);
//    }

//    @Override
//    public long getItemId(int pos) {
//        return list.get(pos).getId();
//        //just return 0 if your list items do not have an Id variable.
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_view, null);
        }
        currentView = view;
        final String lCurrentItem = lProductList.get(position).getItemName();
        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(lCurrentItem);

        TextView counter = (TextView)view.findViewById(R.id.counter);
        if(lProductList.get(position).getQuantity() > 0)
        {
            counter.setText(""+lProductList.get(position).getQuantity());
        }else{
            counter.setText("");
        }
        //TextView counter = (TextView)view.findViewById(R.id.counter);


        //Handle buttons and add onClickListeners
        ImageButton deleteBtn = (ImageButton)view.findViewById(R.id.remove_button);
        ImageButton addBtn = (ImageButton)view.findViewById(R.id.add_button);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                //list.remove(position); //or some other task
                currentDrinkItem = lProductList.get(position);

                int lQuantity = currentDrinkItem.getQuantity();
                if (lQuantity > 0) {
                    lQuantity--;
                    currentDrinkItem.setQuantity(lQuantity);
                }
                DrinksCache.getInstance().removeProduct(currentDrinkItem.getItemName());
                currentDrinkItem.reset();
                notifyDataSetChanged();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentDrinkItem = lProductList.get(position);
                showOptionsDialog();
//                showSizesCommentsView();
//                int lquantity = lProductList.get(position).getQuantity();
//                lquantity++;
//                lProductList.get(position).setQuantity(lquantity);
                notifyDataSetChanged();
            }
        });

        return view;
    }
    private EditText passwordInput;
    private View positiveAction;

    private void showSizesCommentsView() {
        int layout;
        switch (currentDrinkItem.getSizes())
        {
            case 1:
                layout = R.layout.drink_details_1_comments_only;
                break;
            case 2:
                layout = R.layout.drink_details_2;
                break;
            case 3:
                layout = R.layout.drink_details_3;
                break;
            case 4:
                layout = R.layout.drink_details_4;
                break;
            default:
                layout = R.layout.drink_details_1_comments_only;
                break;
        }
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.add_drink)
                .customView(layout, true)
                .positiveText(R.string.submit)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        EditText lComments = (EditText) dialog.getCustomView().findViewById(R.id.comments);
                        currentDrinkItem.setSelectedComments(lComments.getText().toString());
                        String selectedSize;
                        if(currentDrinkItem.isSizesAvailable())
                        {
                            selectedSize = currentDrinkItem.getSelectedSize();
                        }else{
                            selectedSize = null;
                        }
                        SelectedDrinkItem lSelectedDrinkItem = new SelectedDrinkItem(
                                currentDrinkItem.getItemName(),
                                currentDrinkItem.getSizesPrices().get(currentDrinkItem.getSelectedSize()),
                                selectedSize,
                                currentDrinkItem.getSelectedOptions(),
                                currentDrinkItem.getSelectedComments()
                        );
                        DrinksCache.getInstance().addProduct(lSelectedDrinkItem.getItemName(),lSelectedDrinkItem);
                        currentDrinkItem.reset();
                        int lQuantity = currentDrinkItem.getQuantity();
                        lQuantity++;
                        currentDrinkItem.setQuantity(lQuantity);
                        showToast("Drink Added");
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        currentDrinkItem.reset();
                    }
                }).build();
        dialog.setCanceledOnTouchOutside(false);
        SegmentedGroup segmented2 = (SegmentedGroup) dialog.getCustomView().findViewById(R.id.segmented2);
        currentDialog = dialog;
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        if(segmented2 != null) {
            segmented2.setOnCheckedChangeListener(
                    new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            currentDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            if (checkedId == R.id.button21) {
                                currentDrinkItem.setSelectedSize("S");
                            } else if (checkedId == R.id.button22) {
                                currentDrinkItem.setSelectedSize("M");
                            } else if (checkedId == R.id.button23) {
                                currentDrinkItem.setSelectedSize("L");
                            } else if (checkedId == R.id.button24) {
                                currentDrinkItem.setSelectedSize("XL");
                            } else {
                                currentDrinkItem.setSelectedSize(null);
                                currentDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            }
                        }
                    }
            );
        }else{
            currentDrinkItem.setSelectedSize("S");
            positiveAction.setEnabled(true); // disabled by default
        }

        //noinspection ConstantConditions
        passwordInput = (EditText) dialog.getCustomView().findViewById(R.id.comments);
//        passwordInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                positiveAction.setEnabled(s.toString().trim().length() > 0);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });


        int widgetColor = ThemeSingleton.get().widgetColor;

        MDTintHelper.setTint(passwordInput,
                widgetColor == 0 ? ContextCompat.getColor(context, R.color.material_teal_500) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }
    private Toast mToast;
    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showOptionsDialog() {
        ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Multi options Currently not supported in drinks
        if(currentDrinkItem.getOptionsType() == DrinkItem.NO_OPTIONS ||
                currentDrinkItem.getOptionsType() == DrinkItem.MULTI_CHOICES)
        {
            showSizesCommentsView();
        }else if(currentDrinkItem.getOptionsType() == DrinkItem.SIGNLE_CHOICE)
        {
            currentDrinkItem.getSelectedOptions().add(currentDrinkItem.getOptions().get(0));
            builder .setSingleChoiceItems(currentDrinkItem.getOptions().toArray(new CharSequence[currentDrinkItem.getOptions().size()]),0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String itemSelected = currentDrinkItem.getOptions().get(which);
                                    currentDrinkItem.getSelectedOptions().clear();
                                    currentDrinkItem.getSelectedOptions().add(itemSelected);
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItems results somewhere
                            // or return them to the component that opened the dialog
                            showSizesCommentsView();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            currentDrinkItem.getSelectedOptions().clear();
                            currentDrinkItem.reset();
                        }
                    });
            builder.create().setCanceledOnTouchOutside(false);
            builder.show();

        }

        // Set the dialog title
//        builder.setTitle(R.string.pick_toppings)
        /*builder
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(currentDrinkItem.getOptions().toArray(new CharSequence[currentDrinkItem.getOptions().size()]), null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                String itemChecked = currentDrinkItem.getOptions().get(which);
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    currentDrinkItem.getSelectedOptions().add(itemChecked);
                                } else if (currentDrinkItem.getSelectedOptions().contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    currentDrinkItem.getSelectedOptions().remove(itemChecked);
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                    }
                 })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.show();*/
    }
}