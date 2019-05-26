package it.moondroid.colormixer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Created by marco.granatiero on 22/09/2014.
 */
public class ColorFragment extends DialogFragment {

    private static final String ARGS_KEY_COLOR = "color";
    private ColorTextView mColorView;

    public static ColorFragment newInstance(Integer startColor) {
        ColorFragment f = new ColorFragment();
        putArguments(f, startColor);
        return f;
    }

    public ColorFragment() {
        // Required empty public constructor
    }

    private static void putArguments(ColorFragment fragment, Integer startColor){
        Bundle args = new Bundle();
        args.putInt(ARGS_KEY_COLOR, startColor);
        fragment.setArguments(args);
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_color, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle(getResources().getString(R.string.dialog_color_title));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_color_btn_positive), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Color", mColorView.getText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getActivity().getString(R.string.toast_color_copied), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_color_btn_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mColorView = (ColorTextView)view.findViewById(R.id.view_color);
        mColorView.setColor(getArguments().getInt(ARGS_KEY_COLOR, Color.BLACK));

        return alertDialogBuilder.create();
    }
}
