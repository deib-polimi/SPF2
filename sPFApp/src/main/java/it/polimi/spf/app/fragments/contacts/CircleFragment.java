/* 
 * Copyright 2014 Jacopo Aliprandi, Dario Archetti
 * Copyright 2015 Stefano Cappa
 *
 * This file is part of SPF.
 *
 * SPF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * SPF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPF.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.polimi.spf.app.fragments.contacts;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsCompatButton;

import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.LoadersConfig;
import it.polimi.spf.app.R;
import it.polimi.spf.app.R.id;
import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.security.DefaultCircles;
import it.polimi.spf.framework.security.PersonRegistry;

public class CircleFragment extends Fragment implements
        OnClickListener,
        LoaderManager.LoaderCallbacks<Collection<String>> {

    private CircleArrayAdapter mAdapter;

    @Bind(R.id.contacts_circle_add_name)
    EditText mNewCircleName;
    @Bind(id.contacts_circle_list)
    ListView circleList;
    @Bind(R.id.contacts_circle_add_button)
    IconicsCompatButton addButton;


    public static CircleFragment newInstance() {
        return new CircleFragment();
    }

    public CircleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.contacts_circle_page, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        circleList.setEmptyView(getView().findViewById(R.id.contacts_circle_emptyview));

        mAdapter = new CircleArrayAdapter(getActivity());
        circleList.setAdapter(mAdapter);

        addButton.setOnClickListener(this);

        startLoader(LoadersConfig.LOAD_CIRCLE_LOADER, null, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class CircleArrayAdapter extends ArrayAdapter<String> {

        public CircleArrayAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(R.layout.personas_listelement, parent, false);
            ViewHolder holder = ViewHolder.from(view);

            String item = getItem(position);
            holder.name.setText(item);

            if (DefaultCircles.isDefault(item)) {
                holder.deletebutton.setVisibility(View.GONE);
            } else {
                holder.deletebutton.setVisibility(View.VISIBLE);
                holder.deletebutton.setOnClickListener(CircleFragment.this);
                holder.deletebutton.setTag(item);
            }
            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            return false; // Prevent click on item
        }

    }

    private static class ViewHolder {

        public static ViewHolder from(View view) {
            Object o = view.getTag();
            if (o != null & (o instanceof ViewHolder)) {
                return (ViewHolder) o;
            }

            ViewHolder holder = new ViewHolder();
            view.setTag(holder);

            holder.name = (TextView) view.findViewById(R.id.personas_entry_name);
            holder.deletebutton = (IconicsCompatButton) view.findViewById(R.id.personas_entry_delete);

            return holder;
        }

        public TextView name;
        public IconicsCompatButton deletebutton;

    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        switch (v.getId()) {
            case R.id.contacts_circle_add_button:
                String newCircleName = mNewCircleName.getText().toString();
                if (newCircleName.length() == 0) {
                    makeToast("Circle name must not be empty");
                    return;
                }

                if (mAdapter.getPosition(newCircleName) > -1) {
                    makeToast("A circle with this name already exists");
                    return;
                }

                mNewCircleName.setText("");

                args.putString(LoadersConfig.EXTRA_CIRCLE, newCircleName);
                startLoader(LoadersConfig.ADD_CIRCLE_LOADER, args, true);
                return;
            case R.id.personas_entry_delete:
                args.putString(LoadersConfig.EXTRA_CIRCLE, (String) v.getTag());
                startLoader(LoadersConfig.DELETE_CIRCLE_LOADER, args, true);
                return;
            default:
                Log.d("Circletag", "error loader id not found");
        }
    }

    private void makeToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Collection<String>> onCreateLoader(int id, final Bundle args) {
        final PersonRegistry registry = SPF.get().getSecurityMonitor().getPersonRegistry();
        switch (id) {
            case LoadersConfig.LOAD_CIRCLE_LOADER:
                return new AsyncTaskLoader<Collection<String>>(getActivity()) {

                    @Override
                    public Collection<String> loadInBackground() {
                        return registry.getGroups();
                    }
                };
            case LoadersConfig.ADD_CIRCLE_LOADER:
                return new AsyncTaskLoader<Collection<String>>(getActivity()) {

                    @Override
                    public Collection<String> loadInBackground() {
                        String group = args.getString(LoadersConfig.EXTRA_CIRCLE);
                        registry.addGroup(group);
                        return registry.getGroups();
                    }
                };
            case LoadersConfig.DELETE_CIRCLE_LOADER:
                return new AsyncTaskLoader<Collection<String>>(getActivity()) {

                    @Override
                    public Collection<String> loadInBackground() {
                        String group = args.getString(LoadersConfig.EXTRA_CIRCLE);
                        registry.removeGroup(group);
                        return registry.getGroups();
                    }
                };

            default:
                return null;
        }
    }

    private void startLoader(int id, Bundle args, boolean destroyPrevious) {
        if (destroyPrevious) {
            getActivity().getSupportLoaderManager().destroyLoader(id);
        }
        getActivity().getSupportLoaderManager().initLoader(id, args, this).forceLoad();
    }

    @Override
    public void onLoadFinished(Loader<Collection<String>> loader, Collection<String> data) {
        mAdapter.clear();
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<Collection<String>> loader) {
        // Do nothing
    }
}
