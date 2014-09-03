package fr.xgouchet.android.bttf.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fr.xgouchet.android.bttf.R;

/**
 *
 */
public class WatchfaceTutorialFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_tutorial, container, false);

        TextView text = (TextView) rootView.findViewById(R.id.text_title);
        text.setText(R.string.tutorial_title_watchface);

        text = (TextView) rootView.findViewById(R.id.text_content);
        text.setText(Html.fromHtml(getString(R.string.tutorial_text_watchface)));

        ImageView image = (ImageView) rootView.findViewById(R.id.image);
        image.setImageResource(R.drawable.tutorial_watchface);

        AnimationDrawable frameAnimation = (AnimationDrawable) image.getDrawable();
        frameAnimation.setCallback(image);
        frameAnimation.setVisible(true, true);


        return rootView;
    }
}
