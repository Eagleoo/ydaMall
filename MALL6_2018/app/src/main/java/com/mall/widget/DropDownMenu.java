package com.mall.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.view.R;

import java.util.List;

public class DropDownMenu extends LinearLayout {

    //顶部菜单布局
    private LinearLayout tabMenuView;
    //底部容器，包含popupMenuViews，maskView
    private FrameLayout containerView;
    //弹出菜单父布局
    private FrameLayout popupMenuViews;
    //遮罩半透明View，点击可关闭DropDownMenu
    private View maskView;
    //tabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;

    //tab选中颜色
    private int textSelectedColor = 0xff49afef;
    //tab未选中颜色
    private int textUnselectedColor = 0xff000000;
    //遮罩颜色
    private int maskColor = 0x88888888;
    //背景色
    private int menuBackgroundColor = 0xffffffff;
    //tab字体大小
    private int menuTextSize = 15;
    //最大高度
    private int menuMaxHeight = 350;
    private boolean needSetSelectedColor = false;
    //tab选中图标
    private int menuSelectedIcon = R.drawable.drop_down_selected_icon;
    //tab未选中图标
    private int menuUnselectedIcon = R.drawable.drop_down_unselected_icon;

    Drawable unSelectedDrawable;
    Drawable selectedDrawable;

    public DropDownMenu(Context context) {
        super(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        //初始化tabMenuView并添加到tabMenuView
        tabMenuView = new LinearLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setBackgroundColor(menuBackgroundColor);
        tabMenuView.setLayoutParams(params);
        addView(tabMenuView, 0);

        //初始化containerView并将其添加到DropDownMenu
        containerView = new FrameLayout(context);
        containerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(containerView, 1);

        Resources res = getResources();
        unSelectedDrawable = res.getDrawable(menuUnselectedIcon);
        selectedDrawable = res.getDrawable(menuSelectedIcon);
        unSelectedDrawable.setBounds(1, 1, 20, 20);
        selectedDrawable.setBounds(1, 1, 20, 20);
    }

    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<View> viewDatas, @NonNull View contentView) {
        if (tabTexts.size() != viewDatas.size()) {
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal viewDatas.size()");
        }

        for (int i = 0; i < tabTexts.size(); i++) {
            addTab(tabTexts, i);
        }
        containerView.addView(contentView, 0);

        maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(maskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                maskView.setEnabled(false);
                ((TextView) tabMenuView.getChildAt(current_tab_position)).setTextColor(textUnselectedColor);
                closeMenu();
            }
        });
        containerView.addView(maskView, 1);
        maskView.setVisibility(GONE);

        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setVisibility(GONE);
        containerView.addView(popupMenuViews, 2);
        View view = null;
        for (int i = 0; i < viewDatas.size(); i++) {
            view = viewDatas.get(i);
            if (i == 0 || i == 1) {
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpTpPx(menuMaxHeight)));
            } else {
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            popupMenuViews.addView(view, i);
        }

    }

    public void clear() {
        containerView.removeAllViews();
        tabMenuView.removeAllViews();
    }

    private void addTab(@NonNull List<String> tabTexts, int i) {
        final TextView tab = new TextView(getContext());
        tab.setSingleLine();
        tab.setEllipsize(TextUtils.TruncateAt.END);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(menuTextSize);
        tab.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        tab.setTextColor(textUnselectedColor);
        tab.setCompoundDrawables(null, null, unSelectedDrawable, null);
        tab.setText(tabTexts.get(i));
        tab.setPadding(dpTpPx(5), dpTpPx(12), dpTpPx(20), dpTpPx(12));
        //添加点击事件
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(tab);
            }
        });
        tabMenuView.addView(tab);
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(int tabIndex, String text) {
        if (tabIndex != -1) {
            if (needSetSelectedColor) {
                ((TextView) tabMenuView.getChildAt(tabIndex)).setTextColor(textSelectedColor);
            } else {
                ((TextView) tabMenuView.getChildAt(tabIndex)).setTextColor(textUnselectedColor);
            }
            ((TextView) tabMenuView.getChildAt(tabIndex)).setText(text);
        }
    }

    public String getTabText(int tabIndex) {
        return ((TextView) tabMenuView.getChildAt(tabIndex)).getText() + "";
    }

    public View getTab(int tabIndex) {
        return tabMenuView.getChildAt(tabIndex);
    }

    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            tabMenuView.getChildAt(i).setClickable(clickable);
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setCompoundDrawables(null, null, unSelectedDrawable, null);
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setTextColor(textUnselectedColor);
            popupMenuViews.setVisibility(View.GONE);
            popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_out));
            maskView.setVisibility(GONE);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    maskView.setEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            maskView.setAnimation(animation);
            current_tab_position = -1;
        }

    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }

    /**
     * 切换菜单
     *
     * @param target
     */
    private void switchMenu(View target) {
        for (int i = 0; i < tabMenuView.getChildCount(); i++) {
            if (target == tabMenuView.getChildAt(i)) {
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews.setVisibility(View.VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        maskView.setVisibility(VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        popupMenuViews.getChildAt(i).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                    current_tab_position = i;
                    ((TextView) tabMenuView.getChildAt(i)).setTextColor(textSelectedColor);
                    ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawables(null, null, selectedDrawable, null);
                }
            } else {
                ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawables(null, null, unSelectedDrawable, null);
                ((TextView) tabMenuView.getChildAt(i)).setTextColor(textUnselectedColor);
                popupMenuViews.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    public int dpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }
}
