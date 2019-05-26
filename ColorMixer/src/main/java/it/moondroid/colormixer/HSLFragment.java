package it.moondroid.colormixer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link it.moondroid.colormixer.HSLFragment.OnColorChangeListener} interface
 * to handle interaction events.
 *
 */
public class HSLFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    private static final String ARGS_KEY_MODAL = "is_modal";
    private static final String ARGS_KEY_COLOR = "color";

    private HSLColor mHSL = new HSLColor(0.0f, 100.0f, 50.0f); //Default color

    private HueSeekBar mHueSeekBar;
    private LightnessSeekBar mLightnessSeekBar;
    private SaturationSeekBar mSaturationSeekBar;

    private ColorTextView mPreviousColor;
    private ColorTextView mNextColor;

    private OnColorChangeListener mListener;

    public interface OnColorChangeListener {

        public void onColorChange(int color);
        public void onColorConfirmed(int color);
        public void onColorCancel();
    }

    public static HSLFragment newInstance(Integer startColor) {
        HSLFragment f = new HSLFragment();
        putArguments(f, startColor, true);
        return f;
    }

    public HSLFragment() {
        // Required empty public constructor
        putArguments(this, mHSL.getRGB(), false);
    }


    private static void putArguments(HSLFragment fragment, Integer startColor, Boolean isModal){
        Bundle args = new Bundle();
        args.putInt(ARGS_KEY_COLOR, startColor);
        args.putBoolean(ARGS_KEY_MODAL, isModal);
        fragment.setArguments(args);
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_hsl, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle(getResources().getString(R.string.dialog_hsl_title));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_hsl_btn_positive), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mListener.onColorConfirmed(mHSL.getRGB());
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_hsl_btn_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onColorCancel();
                dialog.dismiss();
            }
        });
        setupUI(view);
        return alertDialogBuilder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        boolean isModal = getArguments().getBoolean(ARGS_KEY_MODAL);

        if(isModal) // AVOID REQUEST FEATURE CRASH
        {
            return super.onCreateView(inflater, container, savedInstanceState);

        } else {
            View rootView = inflater.inflate(R.layout.fragment_hsl, container, false);
            setupUI(rootView);

            return rootView;
        }

    }


    private void setupUI(View view){

        mHSL.setRGB(getArguments().getInt(ARGS_KEY_COLOR, mHSL.getRGB()));

        mHueSeekBar = (HueSeekBar)view.findViewById(R.id.hue_seekbar);
        mHueSeekBar.initWithColor(mHSL.getRGB());
        mHueSeekBar.setOnSeekBarChangeListener(this);
        mLightnessSeekBar = (LightnessSeekBar)view.findViewById(R.id.lightness_seekbar);
        mLightnessSeekBar.initWithColor(mHSL.getRGB());
        mLightnessSeekBar.setOnSeekBarChangeListener(this);
        mSaturationSeekBar = (SaturationSeekBar)view.findViewById(R.id.saturation_seekbar);
        mSaturationSeekBar.initWithColor(mHSL.getRGB());
        mSaturationSeekBar.setOnSeekBarChangeListener(this);

        mPreviousColor = (ColorTextView)view.findViewById(R.id.previous_color);
        mPreviousColor.setColor(mHSL);
        mNextColor = (ColorTextView)view.findViewById(R.id.next_color);
        mNextColor.setColor(mHSL);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnColorChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnColorChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if(fromUser){
            if(seekBar instanceof HueSeekBar){
                mHSL.setHue(mHueSeekBar.getHue());
            }
            if(seekBar instanceof LightnessSeekBar){
                mHSL.setLuminance(mLightnessSeekBar.getLightness());
            }
            if(seekBar instanceof SaturationSeekBar){
                mHSL.setSaturation(mSaturationSeekBar.getSaturation());
            }

            updateSeekBars(seekBar);

            mNextColor.setColor(mHSL);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        updateSeekBars(seekBar);

        mListener.onColorChange(mHSL.getRGB());
    }


    private void updateSeekBars(SeekBar seekBar){

        mLightnessSeekBar.setColor(mHSL);
        mSaturationSeekBar.setColor(mHSL);

    }

}
