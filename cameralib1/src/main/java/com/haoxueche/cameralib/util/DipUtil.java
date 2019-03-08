package com.haoxueche.cameralib.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by xiezhongming on 17/9/22.
 *
 */

public class DipUtil {

    /**
     * dip转pix
     *
     * @param dp
     * @return
     */

    public static float dp2px(float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

}
