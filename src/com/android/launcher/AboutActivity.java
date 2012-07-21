package com.android.launcher;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class AboutActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);
		ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
		pager.setAdapter(new AwesomePagerAdapter());

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Start the chat app
		if (keyCode == 84) {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(
					"org.ninjas.noisyninjaclient",
					"org.ninjas.noisyninjaclient.MainActivity"));
			startActivity(intent);
		}

		return super.onKeyDown(keyCode, event);
	}

	private class AwesomePagerAdapter extends PagerAdapter {

		private int[] imageResources = new int[] { R.drawable.ninjaapp_about_1,
				R.drawable.ninjaapp_about_2, R.drawable.ninjaapp_about_3,
				R.drawable.ninjaapp_about_4 };

		@Override
		public int getCount() {
			return imageResources.length;
		}

		/**
		 * Create the page for the given position. The adapter is responsible
		 * for adding the view to the container given here, although it only
		 * must ensure this is done by the time it returns from
		 * {@link #finishUpdate()}.
		 * 
		 * @param container
		 *            The containing View in which the page will be shown.
		 * @param position
		 *            The page position to be instantiated.
		 * @return Returns an Object representing the new page. This does not
		 *         need to be a View, but can be some other container of the
		 *         page.
		 */
		@Override
		public Object instantiateItem(View collection, int position) {
			ImageView iv = new ImageView(AboutActivity.this);
			iv.setImageResource(imageResources[position]);
			iv.setScaleType(ScaleType.FIT_XY);
			((ViewPager) collection).addView(iv, 0);

			return iv;
		}

		/**
		 * Remove a page for the given position. The adapter is responsible for
		 * removing the view from its container, although it only must ensure
		 * this is done by the time it returns from {@link #finishUpdate()}.
		 * 
		 * @param container
		 *            The containing View from which the page will be removed.
		 * @param position
		 *            The page position to be removed.
		 * @param object
		 *            The same object that was returned by
		 *            {@link #instantiateItem(View, int)}.
		 */
		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((ImageView) view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageView) object);
		}

		/**
		 * Called when the a change in the shown pages has been completed. At
		 * this point you must ensure that all of the pages have actually been
		 * added or removed from the container as appropriate.
		 * 
		 * @param container
		 *            The containing View which is displaying this adapter's
		 *            page views.
		 */
		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

	}

}
