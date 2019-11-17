/**
 * ComposeGradient（混合渲染）应用：
 * 1)定义： ComposeShader(Shader shaderA,Shader shaderB, Xfermode mode)
 *         ComposeShader(Shader shaderA,Shader shaderB, PorterDuff.Mode mode)
 * 2)设置： mPaint.setShader(mComposeShader)
 * 3)渲染： mCanvas.draw(......,mPaint)
 * 4）按照各自Shader的规则进行组合，组合式遵守Xfermode规则，不论绘制（draw）的位置和大小。
 */
package org.xotty.multimedia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class Shader_ComposeGradient_Fragment extends Fragment {
    private int viewWidth = 500;
    private int viewHeight = 570;

    public Shader_ComposeGradient_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context contextThemeWrapper = new ContextThemeWrapper(
                getActivity(), R.style.AppTheme_NoActionBar);
        LayoutInflater localInflater = inflater
                .cloneInContext(contextThemeWrapper);

        //构建Fragment的根视图LinearLayout
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        param.topMargin = 50;
        root.setLayoutParams(param);

        //添加双色渲染图
        LinearLayout items0 = (LinearLayout) localInflater.inflate(R.layout.fragment_shader_item, container, false);
        FrameLayout leftFrame0 = items0.findViewById(R.id.frameLeft);
        FrameLayout rightFrame0 = items0.findViewById(R.id.frameRight);
        ComposeShaderView myComposeShaderView = new ComposeShaderView(getContext(), 0);
        LinearLayout.LayoutParams param0 = new LinearLayout.LayoutParams(viewWidth, viewHeight);
        leftFrame0.addView(myComposeShaderView, param0);
        myComposeShaderView = new ComposeShaderView(getContext(), 1);
        rightFrame0.addView(myComposeShaderView, param0);
        root.addView(items0);

        //添加多色渲染图
        LinearLayout items1 = (LinearLayout) localInflater.inflate(R.layout.fragment_shader_item, container, false);
        FrameLayout leftFrame1 = items1.findViewById(R.id.frameLeft);
        FrameLayout rightFrame1 = items1.findViewById(R.id.frameRight);
        myComposeShaderView = new ComposeShaderView(getContext(), 2);
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(viewWidth, viewHeight);
        leftFrame1.addView(myComposeShaderView, param1);
        myComposeShaderView = new ComposeShaderView(getContext(), 3);
        rightFrame1.addView(myComposeShaderView, param1);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param2.topMargin = 50;
        root.addView(items1, param2);

        return root;
    }

    public class ComposeShaderView extends View {
        String modeTag;
        int tileMode, rectWidth, rectHeight;
        private Paint mPaint, xPaint;
        Shader shaderA,shaderB;
        Shader mComposeShader = null;


        ComposeShaderView(Context context, int shaderMode) {
            super(context);
            init(shaderMode);
            rectWidth = viewWidth;
            rectHeight = viewHeight - 70;
        }

        private void init(int mode) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            Bitmap mBmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon);

            mPaint = new Paint();
            xPaint = new Paint();
            xPaint.setColor(Color.RED);
            xPaint.setStrokeWidth(5);
            xPaint.setStyle(Paint.Style.FILL);
            xPaint.setTextAlign(Paint.Align.CENTER);
            xPaint.setTextSize(40);
            tileMode = mode;
            //填充颜色数组
            int[] colors = {0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffff00, 0xff00ffff};
            //与颜色数组对应的位置数组（0～1）
            float[] pos = {0f, 0.2f, 0.4f, 0.6f, 1.0f};
            //颜色沿着（x0,y0）～ (x1,y1)的直线渐变，多余部分会按照TileMode方式填充
            switch (tileMode) {
                case 0:
                    modeTag = "1";
                    shaderA=new BitmapShader(mBmp, Shader.TileMode.REPEAT,  Shader.TileMode.REPEAT);
                    shaderB=new BitmapShader(mBmp, Shader.TileMode.MIRROR,  Shader.TileMode.MIRROR);

                    break;
                case 1:
                    modeTag = "2";
                    shaderA=new LinearGradient(0, rectHeight / 2, rectWidth / 2, rectHeight  / 2, colors, pos, Shader.TileMode.MIRROR);
                    shaderB=new LinearGradient(0, rectHeight / 2, rectWidth / 2, rectHeight  / 2, colors, pos, Shader.TileMode.MIRROR);
                    break;
                case 2:
                    modeTag = "3";
                    shaderA=new BitmapShader(mBmp, Shader.TileMode.REPEAT,  Shader.TileMode.REPEAT);
                    shaderB=new LinearGradient(0, rectHeight / 2, rectWidth/2, rectHeight / 2, colors, pos, Shader.TileMode.MIRROR);

                    break;
                case 3:
                    modeTag = "4";
                    shaderA= new LinearGradient(0, rectHeight / 2, rectWidth / 2, rectHeight  / 2, colors, pos, Shader.TileMode.MIRROR);
                    shaderB= new RadialGradient(rectWidth / 2, rectHeight / 2, 100, colors, pos, Shader.TileMode.REPEAT);

                    break;
            }

            mComposeShader = new ComposeShader(shaderA,shaderB,PorterDuff.Mode.ADD);
            //设置渲染
            mPaint.setShader( mComposeShader);
        }

//        @SuppressLint("DrawAllocation")
//        @Override
//        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//            super.onLayout(changed, left, top, right, bottom);
//            SweepGradient mGradient = null;
//            //填充颜色数组
//            int[] colors = {0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffff00, 0xff00ffff};
//            //与颜色数组对应的位置数组（0～1）
//            float[] pos = {0f, 0.2f, 0.4f, 0.6f, 1.0f};
//            //颜色沿着（x0,y0）～ (x1,y1)的直线渐变，多余部分会按照TileMode方式填充
//            switch (tileMode) {
//                case 0:
//
//                case 1:
//                    modeTag = "双色";
//                    mGradient = new SweepGradient(rectWidth / 2, rectHeight / 2, 0xffff0000, 0xff00ff00);
//                    break;
//                case 2:
//
//                case 3:
//                    modeTag = "多色";
//                    mGradient = new SweepGradient(rectWidth / 2, rectHeight / 2, colors, pos);
//                    break;
//            }
//
//            //设置渲染
//            mPaint.setShader(mGradient);
//
//        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //实施渲染
//            if (tileMode % 2 == 0)
                canvas.drawRect(0, 0, rectWidth, rectHeight, mPaint);
//            else
//                canvas.drawCircle(rectWidth / 2, rectHeight / 2, rectHeight / 2, mPaint);

            canvas.drawText(modeTag, getWidth() / 2, getHeight() - 30, xPaint);
        }
    }
}