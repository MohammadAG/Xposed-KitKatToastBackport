package com.mohammadag.kitkattoastbackport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XResources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class ToastBackport implements IXposedHookZygoteInit {
	@SuppressLint("NewApi")
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		XC_LayoutInflated hook = new XC_LayoutInflated() {
			@SuppressWarnings("deprecation")
			@Override
			public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
				TextView view = (TextView) liparam.view.findViewById(android.R.id.message);
				Resources res = getResources(view.getContext());

				Typeface typeFace = Typeface.createFromAsset(res.getAssets(), "RobotoCondensed-Regular.ttf");
				Drawable backgroundDrawable = res.getDrawable(R.drawable.toast_frame_holo);

				view.setTypeface(typeFace);

				LinearLayout layout = (LinearLayout) liparam.view;
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
					layout.setBackground(backgroundDrawable);
				} else {
					layout.setBackgroundDrawable(backgroundDrawable);
				}
			}
		};

		XResources.hookSystemWideLayout("android", "layout", "transient_notification", hook);
		try {
			XResources.hookSystemWideLayout("android", "layout", "tw_transient_notification", hook);
		} catch (Throwable t) { }
	}

	/* Get resources for our own package */
	public static Resources getResources(Context context) {
		try {
			return context.getPackageManager().getResourcesForApplication("com.mohammadag.kitkattoastbackport");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}
