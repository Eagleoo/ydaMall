package com.mall.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/10/12.
 *
 * 注意 不要在 xml 文件中设置 digits属性 不然会出现监听异常错误
 */

public class MoneyInputFilter implements InputFilter {
    Pattern mPattern;

    //输入的最大金额
    private   int MAX_VALUE = Integer.MAX_VALUE;
    //小数点后的位数
    private  int POINTER_LENGTH = 2;

    private  String POINTER = ".";

    private  String ZERO = "0";

    private int index=0;

    /**
     *  默认输入小数点后两位
     */
    public MoneyInputFilter() {
        mPattern = Pattern.compile("([0-9]|\\.)*");
    }

    /**
     *  设置可以输入小数点后几位
     *  当为0时，不可以在输入小数点
     */

    public MoneyInputFilter(int lenght) {
        mPattern = Pattern.compile("([0-9]|\\.)*");
        POINTER_LENGTH=lenght;
        if (lenght==0){
            mPattern = Pattern.compile("([0-9])*");
        }
    }

    public void setIndex(int index){
        this.index=index;
    }


    /**
     * @param source    新输入的字符串
     * @param start     新输入的字符串起始下标，一般为0
     * @param end       新输入的字符串终点下标，一般为source长度-1
     * @param dest      输入之前文本框内容
     * @param dstart    原内容起始坐标，一般为0
     * @param dend      原内容终点坐标，一般为dest长度-1
     * @return          输入内容
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();

        //验证删除等按键
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }

        Matcher matcher = mPattern.matcher(source);
        //已经输入小数点的情况下，只能输入数字
        if(destText.contains(POINTER)) {
            Log.e("已经输入","已经输入小数点的情况");
            if (!matcher.matches()) {
                return "";
            } else {
                if (POINTER.equals(source.toString())) {  //只能输入一个小数点
                    return "";
                }
            }

            //验证小数点精度，保证小数点后只能输入两位
            Log.e("destText",destText);
            int index = destText.indexOf(POINTER);
            int length= destText.length()-1-index;





            Log.e("精度1","index"+index+"length"+length);

            Log.e("精度2","dstart"+dstart+"dend"+dend);
//            index3length1  155.10
            // index3length2
            if (length >= POINTER_LENGTH&&this.index>index) {
                Log.e("精度3","dstart"+dstart+"dend"+dend);
                return dest.subSequence(dstart, dend);
            }
        } else {
            /**
             * 没有输入小数点的情况下，只能输入小数点和数字
             * 1. 首位不能输入小数点
             * 2. 如果首位输入0，则接下来只能输入小数点了
             */
            if (!matcher.matches()) {
                return "";
            } else {
                if ((POINTER.equals(source.toString())) && TextUtils.isEmpty(destText)) {  //首位不能输入小数点
                    return "";
                }else if (ZERO.equals(destText)&&index==0){//如果首位输入为0，光标在第一个

                }

                else if (!POINTER.equals(source.toString()) && ZERO.equals(destText)) { //如果首位输入0，接下来只能输入小数点
                    return "";
                }
            }
        }

        //验证输入金额的大小
        double sumText = Double.parseDouble(destText + sourceText);
        if (sumText > MAX_VALUE) {
            return dest.subSequence(dstart, dend);
        }

        String str=dest.subSequence(dstart, dend) + sourceText;
        Log.e("最后的的","LL"+str);
        return dest.subSequence(dstart, dend) + sourceText;
    }
}