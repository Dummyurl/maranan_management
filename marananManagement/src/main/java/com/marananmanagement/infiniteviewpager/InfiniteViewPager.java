package com.marananmanagement.infiniteviewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;


/**
 * A {@link ViewPager} that allows pseudo-infinite paging with a wrap-around
 * effect. Should be used with an {@link InfinitePagerAdapter}.
 */

/**
 * Auto Scroll View Pager
 * <ul>
 * <strong>Basic Setting and Usage</strong>
 * <li>{@link #startAutoScroll()} start auto scroll, or {@link #startAutoScroll(int)} start auto scroll delayed</li>
 * <li>{@link #stopAutoScroll()} stop auto scroll</li>
 * <li>{@link #setInterval(long)} set auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}</li>
 * </ul>
 * <ul>
 * <strong>Advanced Settings and Usage</strong>
 * <li>{@link #setDirection(int)} set auto scroll direction</li>
 * <li>{@link #setCycle(boolean)} set whether automatic cycle when auto scroll reaching the last or first item, default
 * is true</li>
 * <li>{@link #setSlideBorderMode(int)} set how to process when sliding at the last or first item</li>
 * <li>{@link #setStopScrollWhenTouch(boolean)} set whether stop auto scroll when touching, default is true</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-12-30
 */
public class InfiniteViewPager extends ViewPager {

	public final int DEFAULT_INTERVAL = 3000;

	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	/** do nothing when sliding at the last or first item **/
	public static final int SLIDE_BORDER_MODE_NONE = 0;
	/** cycle when sliding at the last or first item **/
	public static final int SLIDE_BORDER_MODE_CYCLE = 1;
	/** deliver event to parent when sliding at the last or first item **/
	public static final int SLIDE_BORDER_MODE_TO_PARENT = 2;

	/** auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL} **/
	private long interval = DEFAULT_INTERVAL;
	/** auto scroll direction, default is {@link #RIGHT} **/
	private int direction = RIGHT;
	/**
	 * whether automatic cycle when auto scroll reaching the last or first item,
	 * default is true
	 **/
	private boolean isCycle = true;
	/** whether stop auto scroll when touching, default is true **/
	private boolean stopScrollWhenTouch = true;
	/**
	 * how to process when sliding at the last or first item, default is
	 * {@link #SLIDE_BORDER_MODE_NONE}
	 **/
	private int slideBorderMode = SLIDE_BORDER_MODE_NONE;
	/** whether animating when auto scroll at the last or first item **/
	private boolean isBorderAnimation = true;
	/** scroll factor for auto scroll animation, default is 1.0 **/
	private double autoScrollFactor = 10.0;
	/** scroll factor for swipe scroll animation, default is 1.0 **/
	private double swipeScrollFactor = 10.0;

	private Handler handler;
	private boolean isAutoScroll = false;
	private boolean isStopByTouch = false;
	private float touchX = 0f, downX = 0f;
	private CustomDurationScroller scroller = null;

	public static final int SCROLL_WHAT = 0;

	public InfiniteViewPager(Context context) {
		super(context);
		init();
	}

	public InfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		handler = new MyHandler(this);
		setViewPagerScroller();
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		// offset first element so that we can scroll to the left
		setCurrentItem(0);
	}

	@Override
	public void setCurrentItem(int item) {
		// offset the current item to ensure there is space to scroll
		setCurrentItem(item, false);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		if (getAdapter().getCount() == 0) {
			super.setCurrentItem(item, smoothScroll);
			return;
		}
		item = getOffsetAmount() + (item % getAdapter().getCount());
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public int getCurrentItem() {
		if (getAdapter().getCount() == 0) {
			return super.getCurrentItem();
		}
		int position = super.getCurrentItem();
		if (getAdapter() instanceof InfinitePagerAdapter) {
			InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
			// Return the actual item position in the data backing
			// InfinitePagerAdapter
			return (position % infAdapter.getRealCount());
		} else {
			return super.getCurrentItem();
		}
	}

	private int getOffsetAmount() {
		if (getAdapter().getCount() == 0) {
			return 0;
		}
		if (getAdapter() instanceof InfinitePagerAdapter) {
			InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
			// allow for 100 back cycles from the beginning
			// should be enough to create an illusion of infinity
			// warning: scrolling to very high values (1,000,000+) results in
			// strange drawing behaviour
			return infAdapter.getRealCount() * 10;
		} else {
			return 0;
		}
	}

	/**
	 * start auto scroll, first scroll delay time is {@link #getInterval()}
	 */
	public void startAutoScroll() {
		isAutoScroll = true;
		sendScrollMessage((long) (interval + scroller.getDuration()
				/ autoScrollFactor * swipeScrollFactor));
	}

	/**
	 * start auto scroll
	 * 
	 * @param delayTimeInMills
	 *            first scroll delay time
	 */
	public void startAutoScroll(int delayTimeInMills) {
		isAutoScroll = true;
		sendScrollMessage(delayTimeInMills);
	}

	/**
	 * stop auto scroll
	 */
	public void stopAutoScroll() {
		isAutoScroll = false;
		handler.removeMessages(SCROLL_WHAT);
	}

	/**
	 * set the factor by which the duration of sliding animation will change
	 * while swiping
	 */
	public void setSwipeScrollDurationFactor(double scrollFactor) {
		swipeScrollFactor = scrollFactor;
	}

	/**
	 * set the factor by which the duration of sliding animation will change
	 * while auto scrolling
	 */
	public void setAutoScrollDurationFactor(double scrollFactor) {
		autoScrollFactor = scrollFactor;
	}

	private void sendScrollMessage(long delayTimeInMills) {
		/** remove messages before, keeps one message is running at most **/
		handler.removeMessages(SCROLL_WHAT);
		handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
	}

	/**
	 * set ViewPager scroller to change animation duration when sliding
	 */
	private void setViewPagerScroller() {
		try {
			Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
			scrollerField.setAccessible(true);
			Field interpolatorField = ViewPager.class
					.getDeclaredField("sInterpolator");
			interpolatorField.setAccessible(true);

			scroller = new CustomDurationScroller(getContext(),
					(Interpolator) interpolatorField.get(null));
			scrollerField.set(this, scroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * scroll only once
	 */
	public void scrollOnce() {
		PagerAdapter adapter = getAdapter();
		int currentItem = getCurrentItem();
		int totalCount;
		if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
			return;
		}

		int nextItem = (direction == LEFT) ? --currentItem : ++currentItem;
		if (nextItem < 0) {
			if (isCycle) {
				setCurrentItem(totalCount - 1, isBorderAnimation);
			}
		} else if (nextItem == totalCount) {
			if (isCycle) {
				setCurrentItem(0, isBorderAnimation);
			}
		} else {
			setCurrentItem(nextItem, true);
		}
	}

	/**
	 * <ul>
	 * if stopScrollWhenTouch is true
	 * <li>if event is down, stop auto scroll.</li>
	 * <li>if event is up, start auto scroll again.</li>
	 * </ul>
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev);

		if (stopScrollWhenTouch) {
			if ((action == MotionEvent.ACTION_DOWN) && isAutoScroll) {
				isStopByTouch = true;
				stopAutoScroll();
			} else if (ev.getAction() == MotionEvent.ACTION_UP && isStopByTouch) {
				startAutoScroll();
			}
		}

		if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT
				|| slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
			touchX = ev.getX();
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				downX = touchX;
			}
			int currentItem = getCurrentItem();
			PagerAdapter adapter = getAdapter();
			int pageCount = adapter == null ? 0 : adapter.getCount();
			/**
			 * current index is first one and slide to right or current index is
			 * last one and slide to left.<br/>
			 * if slide border mode is to parent, then
			 * requestDisallowInterceptTouchEvent false.<br/>
			 * else scroll to last one when current item is first one, scroll to
			 * first one when current item is last one.
			 */
			if ((currentItem == 0 && downX <= touchX)
					|| (currentItem == pageCount - 1 && downX >= touchX)) {
				if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
					getParent().requestDisallowInterceptTouchEvent(false);
				} else {
					if (pageCount > 1) {
						setCurrentItem(pageCount - currentItem - 1,
								isBorderAnimation);
					}
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				return super.dispatchTouchEvent(ev);
			}
		}
		getParent().requestDisallowInterceptTouchEvent(true);

		return super.dispatchTouchEvent(ev);
	}

	public static class MyHandler extends Handler {

		private final WeakReference<InfiniteViewPager> autoScrollViewPager;

		public MyHandler(InfiniteViewPager autoScrollViewPager) {
			this.autoScrollViewPager = new WeakReference<InfiniteViewPager>(
					autoScrollViewPager);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SCROLL_WHAT:
				InfiniteViewPager pager = this.autoScrollViewPager.get();
				if (pager != null) {
					pager.scroller
							.setScrollDurationFactor(pager.autoScrollFactor);
					pager.scrollOnce();
					pager.scroller
							.setScrollDurationFactor(pager.swipeScrollFactor);
					pager.sendScrollMessage(pager.interval
							+ pager.scroller.getDuration());
				}
			default:
				break;
			}
		}
	}

	/**
	 * get auto scroll time in milliseconds, default is
	 * {@link #DEFAULT_INTERVAL}
	 * 
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * set auto scroll time in milliseconds, default is
	 * {@link #DEFAULT_INTERVAL}
	 * 
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * get auto scroll direction
	 * 
	 * @return {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
	 */
	public int getDirection() {
		return (direction == LEFT) ? LEFT : RIGHT;
	}

	/**
	 * set auto scroll direction
	 * 
	 * @param direction
	 *            {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * whether automatic cycle when auto scroll reaching the last or first item,
	 * default is true
	 * 
	 * @return the isCycle
	 */
	public boolean isCycle() {
		return isCycle;
	}

	/**
	 * set whether automatic cycle when auto scroll reaching the last or first
	 * item, default is true
	 * 
	 * @param isCycle
	 *            the isCycle to set
	 */
	public void setCycle(boolean isCycle) {
		this.isCycle = isCycle;
	}

	/**
	 * whether stop auto scroll when touching, default is true
	 * 
	 * @return the stopScrollWhenTouch
	 */
	public boolean isStopScrollWhenTouch() {
		return stopScrollWhenTouch;
	}

	/**
	 * set whether stop auto scroll when touching, default is true
	 * 
	 * @param stopScrollWhenTouch
	 */
	public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
		this.stopScrollWhenTouch = stopScrollWhenTouch;
	}

	/**
	 * get how to process when sliding at the last or first item
	 * 
	 * @return the slideBorderMode {@link #SLIDE_BORDER_MODE_NONE},
	 *         {@link #SLIDE_BORDER_MODE_TO_PARENT},
	 *         {@link #SLIDE_BORDER_MODE_CYCLE}, default is
	 *         {@link #SLIDE_BORDER_MODE_NONE}
	 */
	public int getSlideBorderMode() {
		return slideBorderMode;
	}

	/**
	 * set how to process when sliding at the last or first item
	 * 
	 * @param slideBorderMode
	 *            {@link #SLIDE_BORDER_MODE_NONE},
	 *            {@link #SLIDE_BORDER_MODE_TO_PARENT},
	 *            {@link #SLIDE_BORDER_MODE_CYCLE}, default is
	 *            {@link #SLIDE_BORDER_MODE_NONE}
	 */
	public void setSlideBorderMode(int slideBorderMode) {
		this.slideBorderMode = slideBorderMode;
	}

	/**
	 * whether animating when auto scroll at the last or first item, default is
	 * true
	 * 
	 * @return
	 */
	public boolean isBorderAnimation() {
		return isBorderAnimation;
	}

	/**
	 * set whether animating when auto scroll at the last or first item, default
	 * is true
	 * 
	 * @param isBorderAnimation
	 */
	public void setBorderAnimation(boolean isBorderAnimation) {
		this.isBorderAnimation = isBorderAnimation;
	}
}
