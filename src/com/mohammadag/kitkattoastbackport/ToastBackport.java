package com.mohammadag.kitkattoastbackport;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.content.res.XModuleResources;
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
		XModuleResources modRes = XModuleResources.createInstance(startupParam.modulePath, null);
		final Drawable backgroundDrawable = modRes.getDrawable(R.drawable.toast_frame_holo);
		final Typeface typeFace = Typeface.createFromAsset(modRes.getAssets(), "RobotoCondensed-Regular.ttf");

		XC_LayoutInflated hook = new XC_LayoutInflated() {
			@SuppressWarnings("deprecation")
			@Override
			public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
				TextView view = (TextView) liparam.view.findViewById(android.R.id.message);
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
		} catch (Resources.NotFoundException e) {

		} catch (Throwable t) {

		}
	}
}
