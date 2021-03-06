/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tech.freak.wizardpager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tech.freak.wizardpager.model.AddRemoveItemsPage;
import com.tech.freak.wizardpager.R;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ProductItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class AddRemoveItemsFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private ArrayList<ProductItem> mChoices;
    private String mKey;
    private Page mPage;

    public static AddRemoveItemsFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        AddRemoveItemsFragment fragment = new AddRemoveItemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AddRemoveItemsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        AddRemoveItemsPage AddRemoveItemsPage = (AddRemoveItemsPage) mPage;
        mChoices = AddRemoveItemsPage.getAllChoices();
//        mChoices = new ArrayList<String>();
//        mChoices.
//        for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
//            mChoices.add(fixedChoicePage.getOptionAt(i));
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom_fragment_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

//        final ListView listView = (ListView) rootView.findViewById(R.id.custom_fragment_listview);




        //instantiate custom adapter
        MyCustomAdapter adapter = new MyCustomAdapter(mChoices, getActivity());
        ListView lView = (ListView)rootView.findViewById(R.id.custom_fragment_listview);
        lView.setAdapter(adapter);
//        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Pre-select currently selected item.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
//                String selection = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
//                for (int i = 0; i < mChoices.size(); i++) {
//                    if (mChoices.get(i).equals(selection)) {
//                        listView.setItemChecked(i, true);
//                        break;
//                    }
//                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        mPage.getData().putString(Page.SIMPLE_DATA_KEY,
//                getListAdapter().getItem(position).toString());
//        mPage.notifyDataChanged();
//    }
}
