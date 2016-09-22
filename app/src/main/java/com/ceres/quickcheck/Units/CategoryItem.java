package com.ceres.quickcheck.Units;

import java.util.ArrayList;

/**
 * 掌管類別
 * Created by apple on 2016/3/16.
 */
public class CategoryItem {
    public long id;
    public String parentItem;
    public ArrayList<String> childItem;
    public String iconColor;
    public int iconStyle;
    /**0為支出，1為收入**/
    public int isIncome;
}
