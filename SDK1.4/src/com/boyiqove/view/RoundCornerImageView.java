package com.boyiqove.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author WindowY
 *	圆角的imageview
 */
public class RoundCornerImageView extends ImageView {

	public RoundCornerImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Path path=new Path();
		int w = this.getWidth(); 
		int h = this.getHeight(); 
		path.addRoundRect(new RectF(0, 0, w, h), 5.0f, 5.0f, Path.Direction.CW); 
		canvas.clipPath(path);
		super.onDraw(canvas);
	}
}
