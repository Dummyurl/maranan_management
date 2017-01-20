package com.marananmanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.marananmanagement.AlertEditPage;
import com.marananmanagement.LiveBroadcastImages;
import com.marananmanagement.R;
import com.marananmanagement.VideoAdvertisement;
import com.marananmanagement.httpholder.AsyncRequest;
import com.marananmanagement.interfaces.Constant;
import com.marananmanagement.util.Utilities;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


public class AlertEditAutoScrollAdapter extends PagerAdapter implements Constant {
    public static AlertEditAutoScrollAdapter context;
    private Context ctx;
    private String id;
    private ArrayList<String> flag;
    private ViewPager pager;
    private int count;

    public static AlertEditAutoScrollAdapter getInstance() {
        return context;
    }

    public AlertEditAutoScrollAdapter(Context videoNewActivity, ArrayList<String> flag, String id, ViewPager pager) {
        ctx = videoNewActivity;
        context = AlertEditAutoScrollAdapter.this;
        this.flag = flag;
        this.id = id;
        this.pager = pager;
    }

    @Override
    public int getCount() {
        if (flag != null && flag.size() > 0) {
            count = flag.size() + 1;
        } else {
            count = 1;
        }
        return count;
    }

    /**
     * Create the page for the given position. The adapter is responsible for
     * adding the view to the container given here, although it only must ensure
     * this is done by the time it returns from .
     *
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page. This does not need
     * to be a View, but can be some other container of the page.
     */
    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.alert_edit_pager_chooser_view, collection, false);
        ImageView view = (ImageView) v.findViewById(R.id.imageViewInfiniteView);
        ImageView cross_view_pager = (ImageView) v.findViewById(R.id.cross_view_pager);
        ImageView img_youtube = (ImageView) v.findViewById(R.id.img_youtube);
        ImageView img_camera = (ImageView) v.findViewById(R.id.img_camera);
        LinearLayout infinitePagerSelection = (LinearLayout) v.findViewById(R.id.infinitePagerSelection);
        RelativeLayout relative_up = (RelativeLayout) v.findViewById(R.id.relative_up);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            }
        });

        img_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(AlertEditPage.getInstance().isGetUploading()==false){
                    Intent liveImages = new Intent(ctx, VideoAdvertisement.class);
                    liveImages.putExtra("id", id);
                    liveImages.putExtra("name_class", "AlertEditPage");
                    ((Activity) ctx).startActivityForResult(liveImages, REQ_CODE_YOUTUBE);
                }else{
                    Utilities.showToast(ctx, "Uploading...Please wait");
                }

            }
        });

        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(AlertEditPage.getInstance().isGetUploading()==false){
                    Intent liveImages = new Intent(ctx, LiveBroadcastImages.class);
                    liveImages.putExtra("id", id);
                    liveImages.putExtra("name_class", "AlertEditPage");
                    ((Activity) ctx).startActivityForResult(liveImages, REQ_CODE_LIVE_IMAGES);
                }else{
                    Utilities.showToast(ctx, "Uploading...Please wait");
                }

            }
        });

        cross_view_pager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(AlertEditPage.getInstance().isGetUploading() == false){

                    if(pager.getCurrentItem() < AlertEditPage.getInstance().getImageCount()){

                        int newImageCount = (AlertEditPage.getInstance().getImageCount()-1);
                        AlertEditPage.getInstance().setImageCount(newImageCount);
                        /* Delete Image Alert Request */
                        new AsyncRequest().deleteAlertImage(id, flag.get(pager.getCurrentItem()));
                    }

                    flag.remove(pager.getCurrentItem());
                    AlertEditPage.getInstance().setViewpagerAdapter(flag);

                }else{
                    Utilities.showToast(ctx, "Uploading...Please wait");
                }
            }
        });


        if (position == getCount() - 1) {

            cross_view_pager.setVisibility(View.GONE);
            relative_up.setBackgroundColor(R.color.navy);
            view.setVisibility(View.GONE);
            infinitePagerSelection.setVisibility(View.VISIBLE);

        } else {

            if (flag != null && flag.size() > 0) {
                cross_view_pager.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                if (flag.get(position).startsWith("http://")
                        || flag.get(position).startsWith("https://")) {
                    Picasso.with(ctx).load(flag.get(position)).placeholder(R.color.black)
                            .error(R.color.black).into(view);
                } else {
                    File file = new File(flag.get(position));
                    Picasso.with(ctx).load(file).placeholder(R.color.black)
                            .error(R.color.black).into(view);
                }
                infinitePagerSelection.setVisibility(View.GONE);
            }

        }
        ((ViewPager) collection).addView(v, 0);
        return v;
    }

    /**
     * Remove a page for the given position. The adapter is responsible for
     * removing the view from its container, although it only must ensure this
     * is done by the time it returns from .
     *
     * @param position The page position to be removed.
     */
    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((RelativeLayout) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    /**
     * Called when the a change in the shown pages has been completed. At this
     * point you must ensure that all of the pages have actually been added or
     * removed from the container as appropriate.
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

    public ArrayList<String> getImageList() {
        return flag;
    }
}
