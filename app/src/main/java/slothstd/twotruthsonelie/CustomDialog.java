package slothstd.twotruthsonelie;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Daniel on 21-May-16.
 */
public class CustomDialog {

    public Button positive, negative;
    public Dialog dialog;

    public void showDiaolg(Activity activity, String positiveButton, String negativeButton, String message) {

        dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialogText);
        text.setText(message);

        positive = (Button) dialog.findViewById(R.id.dialogPositive);
        positive.setText(positiveButton);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        negative = (Button) dialog.findViewById(R.id.dialogNegative);
        negative.setText(negativeButton);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();

    }


}
