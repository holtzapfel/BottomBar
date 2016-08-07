package com.roughike.bottombar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

/*
 * BottomBar library for Android
 * Copyright (c) 2016 Iiro Krankka (http://github.com/roughike).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BottomBarBadge extends TextView {
    private int count;
    private boolean isVisible = false;
    private long animationDuration = 150;

    public BottomBarBadge(Context context) {
        super(context);
    }

    /**
     * Set the unread / new item / whatever count for this Badge.
     *
     * @param count the value this Badge should show.
     */
    public void setCount(int count) {
        this.count = count;
        setText(String.valueOf(count));
    }

    /**
     * Get the currently showing count for this Badge.
     *
     * @return current count for the Badge.
     */
    public int getCount() {
        return count;
    }

    /**
     * Set the scale animation duration in milliseconds.
     *
     * @param duration animation duration in milliseconds.
     */
    public void setAnimationDuration(long duration) {
        this.animationDuration = duration;
    }

    /**
     * Shows the badge with a neat little scale animation.
     */
    public void show() {
        isVisible = true;
        ViewCompat.animate(this)
                .setDuration(animationDuration)
                .scaleX(1)
                .scaleY(1)
                .start();
    }

    /**
     * Hides the badge with a neat little scale animation.
     */
    public void hide() {
        isVisible = false;
        ViewCompat.animate(this)
                .setDuration(animationDuration)
                .scaleX(0)
                .scaleY(0)
                .start();
    }

    /**
     * Is this badge currently visible?
     *
     * @return true is this badge is visible, otherwise false.
     */
    public boolean isVisible() {
        return isVisible;
    }

    void attachToTab(final BottomBarTab tabToAddTo, int backgroundColor) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setLayoutParams(params);
        setGravity(Gravity.CENTER);
        MiscUtils.setTextAppearance(this, R.style.BB_BottomBarBadge_Text);

        int three = MiscUtils.dpToPixel(getContext(), 3);
        ShapeDrawable backgroundCircle = BadgeCircle.make(three * 3, backgroundColor);
        setPadding(three, three, three, three);
        setBackgroundCompat(backgroundCircle);

        final FrameLayout container = new FrameLayout(getContext());
        container.setLayoutParams(params);

        ViewGroup parent = (ViewGroup) tabToAddTo.getParent();
        parent.removeView(tabToAddTo);

        container.setTag(tabToAddTo.getTag());
        tabToAddTo.setTag(null);
        container.addView(tabToAddTo);
        container.addView(this);

        parent.addView(container, tabToAddTo.getIndexInContainer());

        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                adjustPositionAndSize(tabToAddTo);
            }
        });
    }

    protected void adjustPosition(View tabToAddTo) {
        setX((float) (tabToAddTo.getX() + (tabToAddTo.getWidth() / 1.75)));
    }

    private void adjustPositionAndSize(View tabToAddTo) {
        adjustPosition(tabToAddTo);
        setTranslationY(10);

        int size = Math.max(getWidth(), getHeight());

        ViewGroup.LayoutParams params = getLayoutParams();

        if (params.width != size || params.height != size) {
            params.width = size;
            params.height = size;
            setLayoutParams(params);
        }
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(background);
        } else {
            setBackgroundDrawable(background);
        }
    }
}
