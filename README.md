Android: TouchImageView
Created by: Mike Ortiz
Updated by: Hank Zhu
Date: 9/19/2012
----------------------
Add support to ViewPager flipping.

Example to work with ViewPager:
=======
	public class ImageViewPager extends ViewPager {
	
		public ImageViewPager(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
	
		public ImageViewPager(Context context) {
			super(context);
		}
	
		@Override
		protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
			if (v instanceof TouchImageView) {
				TouchImageView imageView = (TouchImageView) v;
				return imageView.canScrollHorizontally(dx);
			}
	
			return super.canScroll(v, checkV, dx, x, y);
		}
	}