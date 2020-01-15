package com.aserbao.androidcustomcamera.base.viewHolder;

import android.view.View;

/**
 * 功能:
 *
 * @author aserbao
 * @date : On 2019/2/19 4:53 PM
 * @email: this is empty email
 * @project:AserbaosAndroid
 * @package:com.aserbao.aserbaosandroid.base.interfaces
 */
public interface IBaseRecyclerItemClickListener {
   void itemClickBack(View view, int position, boolean isLongClick, int comeFrom);

}
