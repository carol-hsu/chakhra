package com.example.statesaver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.statesaver.utils.DbHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private boolean hasInternet(){
        boolean is_connect = true;
        if(connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                != NetworkInfo.State.CONNECTED &&
                connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                        != NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            is_connect = false;
        }
        return is_connect;
    }

    private String[] dummy_run(String query){
        System.out.println("Get:" + query);
        String[] python_loop_urls = { "https://www.w3schools.com/python/python_for_loops.asp",
                "https://wiki.python.org/moin/ForLoop",
                "https://www.geeksforgeeks.org/loops-in-python/"};
        String[] java_class_urls = { "https://www.geeksforgeeks.org/classes-objects-java/",
                "https://www.w3schools.com/java/java_classes.asp",
                "https://www.programiz.com/java-programming/class-objects"};
        if(query.toLowerCase().contains("python"))
            return python_loop_urls;

        return java_class_urls;
    }

    private void searchButtonClicked(String searchString) {
        if (searchString.compareTo("") == 0) {
            return;
        }

        if (!hasInternet()){
            //do p2p
        }else{
            dummy_run(searchString) // url array for snapshots
        }
        //insertSearchRequestInDb(searchString);
    }

    private void insertSearchRequestInDb(String searchString) {
        DbHandler.getInstance(getContext()).insertOwnSearchRequestInDb(searchString);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        Button searchButton = rootView.findViewById(R.id.search_button);
        final TextView text = rootView.findViewById(R.id.search_textbox);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButtonClicked(text.getText().toString());
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
